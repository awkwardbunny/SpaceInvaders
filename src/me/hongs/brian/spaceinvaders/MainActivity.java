package me.hongs.brian.spaceinvaders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MainActivity extends Activity {
	
	RelativeLayout rl_root;
	ImageView img_play;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        
        rl_root = (RelativeLayout)findViewById(R.id.rl_root);
        img_play = (ImageView)findViewById(R.id.img_play);
        
        rl_root.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {
	            	case MotionEvent.ACTION_DOWN:
	    				Intent i = new Intent(MainActivity.this, GameActivity.class);
	    				startActivity(i);
	    				finish();
	            		return true;
				}
				return false;
			}
        });
        
        blink();
    }
	
	private void blink(){
	    final Handler handler = new Handler();
	    new Thread(new Runnable() {
	        @Override
	        public void run() {
		        int timeToBlink = 800;
		        
		        try{
		        	Thread.sleep(timeToBlink);
		        }catch (Exception e) {}
		        
		        handler.post(new Runnable() {
		            @Override
		            public void run() {
		            	if(img_play.getVisibility() == View.VISIBLE){
		            		img_play.setVisibility(View.INVISIBLE);
		            	}else{
		                	img_play.setVisibility(View.VISIBLE);
		                }
		            	blink();
		            }
		        });
	        }
	    }).start();
	}



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
