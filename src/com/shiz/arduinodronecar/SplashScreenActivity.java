package com.shiz.arduinodronecar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreenActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        /****** Create Thread that will sleep for 5 seconds *************/         
        Thread background = new Thread() {
            public void run() { 
                  
                try { 
                    // Thread will sleep for 5 seconds 
                    sleep(SPLASH_TIME_OUT);
                      
                    // After 5 seconds redirect to another intent 
                    Intent i=new Intent(getBaseContext(),MainActivity.class);
                    startActivity(i);
                      //Remove activity 
                    finish();
                      
                } catch (Exception e) {
                  
                } 
            } 
        }; 
          
        // start thread 
        background.start();
    }

}