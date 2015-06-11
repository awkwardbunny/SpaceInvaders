package me.hongs.brian.spaceinvaders;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

public class GameActivity extends SimpleBaseGameActivity  implements SensorEventListener{

	private static int CAMERA_WIDTH = 1920;
	private static int CAMERA_HEIGHT = 1080;
	final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
	private ITextureRegion mBkgnd, mInv1, mInv2, mInv3, mHope, mShot;
	private Sprite hope;
	private ArrayList<Sprite> invaders = new ArrayList<Sprite>();
	
	private ITextureRegion mWin, mLoss, mScore;
	
	ArrayList<Sprite> shots = new ArrayList<Sprite>();
	
	ArrayList<Sprite> rem = new ArrayList<Sprite>();
	ArrayList<Sprite> rem2 = new ArrayList<Sprite>();
	
	Scene scene;
	
	boolean fin = false;
	boolean won = false;
	
	SensorManager senMan;
	Sensor acc;
	private long lastUpdate = 0;
	private long lastFire = 0;
	boolean fire = false;
	private Handler handler;
	private int interval = 8;
	
	public int score = 0;
	
	public int alien_state = 0;
	public int alien_delay = 55;
	public int alien_counter = 0;
	public int step = 50;
	
	Font mFont;
	Text t_score;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

        senMan = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acc = senMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lastUpdate = System.currentTimeMillis();
        
        handler = new Handler();
        new Thread(runner).start();
	}
	
	Runnable runner = new Runnable() {
	    @Override 
	    public void run() {
	    	if(!fin){
	    		move();
	    		handler.postDelayed(runner, interval);
	    	}else{
	    		if(won){
	    			win();
	    		}else{
	    			loss();
	    		}
	    	}
	    }
	  };
	
	protected void onPause(){
		super.onPause();
		senMan.unregisterListener(this);
	}
	
	void win(){
		Sprite win = new Sprite(800, 520, mWin, getVertexBufferObjectManager());
		scene.attachChild(win);
	}
	
	void loss(){
		Sprite loss = new Sprite(800, 520, mLoss, getVertexBufferObjectManager());
		scene.attachChild(loss);
	}

	protected void onResume(){
		super.onResume();
		senMan.registerListener(this, acc, SensorManager.SENSOR_DELAY_FASTEST);
		lastUpdate = System.currentTimeMillis();
	}
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		EngineOptions enop =  new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		enop.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
	    enop.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
	    
	    return enop;
	}

	@Override
	protected void onCreateResources() {
		try {
		    ITexture bkgnd = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/bkgnd.png");
		        }
		    });
		    ITexture invader1 = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/inv1.png");
		        }
		    });
		    ITexture invader2 = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/inv2.png");
		        }
		    });
		    ITexture invader3 = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/inv3.png");
		        }
		    });
		    ITexture hope = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/hope_com.png");
		        }
		    });
		    ITexture shot = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/shot.png");
		        }
		    });
		    ITexture win = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/win.png");
		        }
		    });
		    ITexture loss = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/lost_r.png");
		        }
		    });
		    ITexture score = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {
		        @Override
		        public InputStream open() throws IOException {
		            return getAssets().open("gfx/score1.png");
		        }
		    });
		    
		    bkgnd.load();
		    invader1.load();
		    invader2.load();
		    invader3.load();
		    hope.load();
		    shot.load();
		    win.load();
		    loss.load();
		    score.load();

		    mBkgnd = TextureRegionFactory.extractFromTexture(bkgnd);
		    mInv1 = TextureRegionFactory.extractFromTexture(invader1);
		    mInv2 = TextureRegionFactory.extractFromTexture(invader2);
		    mInv3 = TextureRegionFactory.extractFromTexture(invader3);
		    mHope = TextureRegionFactory.extractFromTexture(hope);
		    mShot = TextureRegionFactory.extractFromTexture(shot);
		    mScore = TextureRegionFactory.extractFromTexture(score);

		    mWin = TextureRegionFactory.extractFromTexture(win);
		    mLoss = TextureRegionFactory.extractFromTexture(loss);
		    Log.d("In", "onCreateResource");
		    
	        this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 40);
	        this.mFont.load();
		} catch (IOException e) {
		    Debug.e(e);
		}
	}

	@Override
	protected Scene onCreateScene() {
		scene = new Scene();
		Sprite backgroundSprite = new Sprite(0, 0, this.mBkgnd, getVertexBufferObjectManager());
		scene.attachChild(backgroundSprite);
		
		hope = new Sprite(930, 1000, mHope, getVertexBufferObjectManager());
		scene.attachChild(hope);

        senMan.registerListener(this, acc, SensorManager.SENSOR_DELAY_FASTEST);
        
        /* Invaders */
        int rows1 = 2;
        int rows2 = 1;
        int rows3 = 2;

        int cols = 12;
        
        for(int y = 0; y < rows1; y++){
        	for(int x = 0; x < cols; x++){ //136 1776
        		float xc = (x*(1726-186)/(cols-1))+186;
        		float yc = (y*(630-120)/(rows1+rows2+rows3-1))+70;
	        	Sprite inv = new Sprite(xc-50, yc, this.mInv1, getVertexBufferObjectManager());
	        	this.invaders.add(inv);
	        	scene.attachChild(inv);
        	}
        }
        
        for(int y = rows1; y < rows2+rows1; y++){
        	for(int x = 0; x < cols; x++){ //136 1776
        		float xc = (x*(1726-186)/(cols-1))+186;
        		float yc = (y*(630-120)/(rows1+rows2+rows3-1))+70;
	        	Sprite inv = new Sprite(xc-50, yc, this.mInv2, getVertexBufferObjectManager());
	        	this.invaders.add(inv);
	        	scene.attachChild(inv);
        	}
        }
        
        for(int y = rows2+rows1; y < rows3+rows2+rows1; y++){
        	for(int x = 0; x < cols; x++){ //136 1776
        		float xc = (x*(1726-186)/(cols-1))+186;
        		float yc = (y*(630-150)/(rows1+rows2+rows3-1))+70;
	        	Sprite inv = new Sprite(xc-50, yc, this.mInv3, getVertexBufferObjectManager());
	        	this.invaders.add(inv);
	        	scene.attachChild(inv);
        	}
        }
        
        Sprite sc = new Sprite(1750-140, 1000, this.mScore, getVertexBufferObjectManager());
        scene.attachChild(sc);
        
        t_score = new Text(1910-100, 1000, mFont, "0000", getVertexBufferObjectManager());
        scene.attachChild(t_score);
        t_score.setColor(Color.CYAN);
        
		return scene;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Sensor sen = event.sensor;
		
		if(sen.getType() == Sensor.TYPE_ACCELEROMETER){
//			float ax = event.values[0];
			float ay = event.values[1];
//			float az = event.values[2];
			
			long curTime = System.currentTimeMillis();
			
			if(hope != null){
				long d = (curTime - lastUpdate);
				lastUpdate = curTime;
				
				float x = hope.getX();
				
				if(Math.abs(ay) > 0.6)
					x += (0.02)*ay*d*d;
				
				if(x < 80){
					x = 80;
				}
				
				if(x > 1720){
					x = 1720;
				}
				
				hope.setX(x);
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
			if(!fin){
				long now = System.currentTimeMillis();
				if(now - lastFire > 360 && hope != null){
					fire();
					lastFire = now;
					return true;
				}
			}else{
				senMan.unregisterListener(this);
				Intent i = new Intent(GameActivity.this, MainActivity.class);
				startActivity(i);
				scene.dispose();
				finish();
			}
		}
		return false;
	}

	private void fire() {
//		Log.d("FIRE", "FIRE");
		fire = true;
	}
	
	private void move(){

		alien_counter++;

		Iterator<Sprite> it = invaders.iterator();
		while(it.hasNext()){
			Sprite inv = it.next();
			
			if(alien_counter == alien_delay){

				float x = inv.getX();
				float y = inv.getY();
				
				switch (alien_state){
					case 0:
					case 1: x += step; break;
					case 2: y += step; break;
					case 3:
					case 4:
					case 5:
					case 6: x -= step; break;
					case 7: y += step; break;
					case 8:
					case 9: x += step; break;
				}
				
				Log.d("MOVED", "MOVED?");
				inv.setPosition(x, y);
				
				if(y > 900)
					fin = true;
			}
		}
		
		if(alien_counter == alien_delay){
			alien_counter = 0;
			alien_state++;
			if(alien_state == 10)
				alien_state = 0;
		}
		
		if(fire){
			float x = hope.getX();
			float y = hope.getY();
			Sprite shot = new Sprite(x+56, y, mShot, getVertexBufferObjectManager());
			shots.add(shot);
			scene.attachChild(shot);
			fire = false;
		}
    	
		Iterator<Sprite> si = shots.iterator();
    	while(si.hasNext()){
    		boolean lost = false;
    		Sprite s = si.next();
    		
    		boolean hit = false;
    		it = invaders.iterator();
    		forloop:
    		while(it.hasNext()){
    			Sprite inv = it.next();
    			
    			if(s.collidesWith(inv)){
	    			hit = true;
	    			rem2.add(inv);
	    			it.remove();
//	    			scene.detachChild(inv);
	    			break forloop;
    			}
    		}
    		
    		float y = s.getY();
    		if(y < 0){
    			rem.add(s);
    			si.remove();
//    			scene.detachChild(s);
//    			s.dispose();
    		}else if(hit){
    			rem.add(s);
    			si.remove();
//    			scene.detachChild(s);
    			score += 10;
//    			Log.d("Score:", ""+score);
    			t_score.setText(""+score);
    		}else{
    			s.setY(y-10);
    		}
    		
    		if(invaders.size() == 0){
    			fin = true;
    			won = true;
    		}
    	}
    	
//    	for(Sprite s : rem){
////			shots.remove(s);
//    		s.detachSelf();
//    		s.dispose();
//    	}
//    	rem.clear();
//    	for(Sprite s : rem2){
////			invaders.remove(s);
//    		s.detachSelf();
//    		s.dispose();
//    	}
//    	rem2.clear();
    	
    	getEngine().runOnUpdateThread(new Runnable(){

			@Override
			public void run() {
				for(Sprite s : rem){
					s.detachSelf();
					s.dispose();
				}
				rem.clear();
				for(Sprite s : rem2){
					s.detachSelf();
					s.dispose();
				}
				rem2.clear();
			}
    		
    	});
	}
}
