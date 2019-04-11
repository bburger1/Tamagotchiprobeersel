package com.example.tamagotchiprobeersel;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;


/**
 * @author Nirvana van Hees
 *
 * @version 1.0.0
 *
 * making sure the app still does things when it is closed
 * so hunger and energy will still go down every 10 minutes
 * when the app is closed
 */

public class TimedService extends Service {
    // constant
    public static final long NOTIFY_INTERVAL = 10 * 60 * 1000; // 10 seconds

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                int Food;
                int Hunger;
                int Crystal;
                int Energy;

                @Override
                public void run() {
                    load();

                    Hunger--;
                    Energy--;

                    save();
                }

                private void load() {
                    // Get shared preferences and get stored data or default
                    SharedPreferences preferences = getApplication().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

                    Food = preferences.getInt("food", 0);
                    Hunger = preferences.getInt("hunger", 100);
                    Crystal = preferences.getInt("crystal", 0);
                    Energy = preferences.getInt("energy", 100);
                }

                private void save() {
                    // Get shared preferences and get stored data or default
                    SharedPreferences preferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putInt("food", Food);
                    editor.putInt("hunger", Hunger);
                    editor.putInt("crystal", Crystal);
                    editor.putInt("energy", Energy);

                    editor.commit();
                }

            });
        }
    }
}