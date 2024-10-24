package com.myApp.timer;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class MainActivity extends AppCompatActivity {
    InterstitialAd mInterstitialAd;
    private TextView timerText;
    private TextView resetText;
    private Handler handler = new Handler();
    private long startTime = 0;
    private boolean running = false;

    // Runnable to update the timer every 10 milliseconds

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadAds();

        // Make the app fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        timerText = findViewById(R.id.timerText);
        resetText = findViewById(R.id.resetText);
        View rootLayout = findViewById(R.id.rootLayout);

        // Touch the screen to start/pause the timer
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (running) {
                        pauseTimer();
                    } else {
                        startTimer();
                    }
                }
                return true;
            }
        });

        // Reset button (TextView in top-left corner)
        resetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }

    private void startTimer() {
        startTime = System.currentTimeMillis() - startTime;
        handler.postDelayed(updateRunnable, 0);
        running = true;
    }

    private void pauseTimer() {
        handler.removeCallbacks(updateRunnable);
        startTime = System.currentTimeMillis() - startTime;
        running = false;
    }

    private void resetTimer() {
        loadAds();
        showAds();
        handler.removeCallbacks(updateRunnable);
        startTime = 0;
        timerText.setText("00:00");
        running = false;
    }

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis() - startTime;

            int minutes = (int) (currentTime / 60000);
            int seconds = (int) (currentTime / 1000) % 60;
            int millis = (int) (currentTime % 1000) / 10;

            if (minutes > 0) {
                // Format MM:SS:MsMs
                timerText.setText(String.format("%02d:%02d:%02d", minutes, seconds, millis));
            } else {
                // Format SS:MsMs
                timerText.setText(String.format("%02d:%02d", seconds, millis));
            }

            handler.postDelayed(this, 10);  // Update every 10 milliseconds
        }
    };


    public void loadAds(){
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });

    }

    public void showAds(){
        if (mInterstitialAd != null) {
            mInterstitialAd.show(MainActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    public void checkToast(){
        Toast.makeText(getApplicationContext(), "check", Toast.LENGTH_SHORT).show();

    }

}
