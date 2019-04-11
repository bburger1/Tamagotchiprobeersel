package com.example.tamagotchiprobeersel;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

/**
 * @author Björn Burger
 * the tamagotchi part of the app
 * where hunger and energy go down
 * and you can collect food and crystals to gain
 * energy and lose hunger
 *
 * @author Nirvana van Hees
 * the on pause part
 * which saves the data so it can still be there when the app
 * is closed and opened again
 *
 * @version 1.0.0
 *
 */

public class MainActivity extends AppCompatActivity {

    private TextView TheHunger;
    private TextView FoodA;
    private TextView CrystalA;
    private TextView TheEnergy;

    private Button GetHungryButton;
    private Button EatButton;
    private Button FoodButton;
    private Button CrystalButton;
    private Button UseCrystalButton;
    private Button LowEnergyButton;
    private ImageView EnergyBar;
    private ImageView FoodSatisBar;
    private ImageView AppBackground;

    Handler handler = new Handler();

    private static final int LOCATION_REQUEST = 101;


    int Food;
    int Hunger;
    int Crystal;
    int Energy;
    final int Delay = 5000; //Function update repeat in milliseconds in the handler

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        Context context = getApplicationContext();

        // Get shared preferences and get stored data or default
        SharedPreferences preferences = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        Food = preferences.getInt("food", 0);
        Hunger = preferences.getInt("hunger", 100);
        Crystal = preferences.getInt("crystal", 0);
        Energy = preferences.getInt("energy", 100);
        //AGE HAS TO BE ADDED HERE


        //Check Permissions before opening the map, this makes sure the app does not
        // crash due to permissions being asked asynchronously
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DoCycleEnergy();
        DoCycleHunger();
        handler.postDelayed((Runnable) mToasRunnable, Delay); // To start the cycle of LifeNeeds

        AppBackground = (ImageView) findViewById(R.id.Background);
        AppBackground.setImageDrawable(getResources().getDrawable(R.drawable.backgroundout3));

        // Button to gather food
        FoodButton = (Button) findViewById(R.id.buttonFood);
        FoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GainFood();
            }
        });

        // Button to Feed and let the tamagotchi eat
        EatButton = (Button) findViewById(R.id.buttonEat);
        EatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Eat();
            }
        });

        LowEnergyButton = (Button) findViewById(R.id.ButtonLowEnergy);
        LowEnergyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoCycleEnergy();
            }
        });

        GetHungryButton = (Button) findViewById(R.id.ButtonGetHungry); // Button to do a cycle for energy
        GetHungryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoCycleHunger();
            }
        });

        CrystalButton = (Button) findViewById(R.id.buttonCrystal);
        CrystalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GainCrystal();
            }
        });

        UseCrystalButton = (Button) findViewById(R.id.ButtonUseCrystal);
        UseCrystalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UseCrystal();
            }
        });

        FoodA = (TextView) findViewById(R.id.Afood);
        CrystalA = (TextView) findViewById(R.id.ACrystal);

        updateFoodUI();
        updateCrystalUI();
        updateEnergyUI();
        updateHungerUI();
    }

    //saving the data of hunger, energy, crystals and food
    //so it can loaded when the app is opened again
    @Override
    protected void onPause() {
        super.onPause();

        // Get shared preferences and get stored data or default
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("food", Food);
        editor.putInt("hunger", Hunger);
        editor.putInt("crystal", Crystal);
        editor.putInt("energy", Energy);
        //AGE NEEDS TO BE ADDED HERE

        editor.commit();
    }

    private Runnable mToasRunnable = new Runnable() {
        @Override
        public void run() {
            DoCycleHunger();
            DoCycleEnergy();
            handler.postDelayed(this, Delay); // to repeat the cycle x every amount of seconds
        }
    };

    // Cycle with the function for adding hunger etc.
    public void DoCycleHunger() {
        Hunger--;
        updateHungerUI();
    }

    public void updateHungerUI() {
        TheHunger = (TextView) findViewById(R.id.THunger);
        TheHunger.setText("Satisfaction:" + " " + String.valueOf(Hunger));

        if (Hunger >= 99) {
            Hunger = 99;
        }

        FoodSatisBar = (ImageView) findViewById(R.id.VFoodSatisBar);
        if (Hunger > 75) {
            FoodSatisBar.setImageDrawable(getResources().getDrawable(R.drawable.lifebar100));
        }
        else if ((Hunger > 50)&&(Hunger<=75)){
            FoodSatisBar.setImageDrawable(getResources().getDrawable(R.drawable.lifebar75));
        }
        else if  ((Hunger > 25)&&(Hunger<= 50)) {
            FoodSatisBar.setImageDrawable(getResources().getDrawable(R.drawable.lifebar50));
        }
        else {
            FoodSatisBar.setImageDrawable(getResources().getDrawable(R.drawable.lifebar25));
        }
    }
    public void DoCycleEnergy() {
        Energy--;
        updateEnergyUI();
    }

    public void updateEnergyUI() {
        TheEnergy = (TextView) findViewById(R.id.TEnergy);
        TheEnergy.setText("Energy:" + " " + String.valueOf(Energy));

        EnergyBar = (ImageView) findViewById(R.id.VEnergyBar);
        if (Energy > 75) {
            EnergyBar.setImageDrawable(getResources().getDrawable(R.drawable.lifebar100));
        }
        else if ((Energy > 50)&&(Energy<=75)){
            EnergyBar.setImageDrawable(getResources().getDrawable(R.drawable.lifebar75));
        }
        else if  ((Energy > 25)&&(Energy<= 50)) {
            EnergyBar.setImageDrawable(getResources().getDrawable(R.drawable.lifebar50));
        }
        else {
            EnergyBar.setImageDrawable(getResources().getDrawable(R.drawable.lifebar25));
        }
    }

    // Function to let the tamagotchi eat.
    public void Eat() {
        if ((Food > 0) && (Hunger <= 90)) {
            Hunger = Hunger + 10;
            Food--;
            Toast.makeText(MainActivity.this, " Hmm that was delicious!", Toast.LENGTH_SHORT).show();
        } else if (Hunger > 90) {
            Toast.makeText(MainActivity.this, " I am not Hungry :)", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, " You have no food :(", Toast.LENGTH_SHORT).show();
        }

        updateFoodUI();
    }

    public void updateFoodUI() {
        TheHunger.setText("Hunger:" + " " + String.valueOf(Hunger));
        FoodA.setText(String.valueOf(Food));
    }

    public void UseCrystal(){
        if ((Crystal > 0) && (Energy <= 90)) {
            Energy = Energy + 10;
            Crystal--;
            Toast.makeText(MainActivity.this, " Wow, I Feel energized!", Toast.LENGTH_SHORT).show();
        } else if (Hunger < 6) {
            Toast.makeText(MainActivity.this, " I have plenty of energy:)", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, " You have no crystals :(", Toast.LENGTH_SHORT).show();
        }

        updateCrystalUI();
    }

    public void updateCrystalUI() {
        TheEnergy.setText("Energy:" + " " + String.valueOf(Energy));
        CrystalA.setText(String.valueOf(Crystal) );
    }


    public void GainFood() {
        Food++;
        Toast.makeText(MainActivity.this, " You found food!", Toast.LENGTH_SHORT).show();

        updateFoodUI();
    }

    public void GainCrystal(){
        Crystal++;
        Toast.makeText(MainActivity.this, " You found a crystal!", Toast.LENGTH_SHORT).show();

        updateCrystalUI();
    }

    public void openMaps(View view) {
        Intent i = new Intent(this, maps.class);
        startActivity(i);
    }
}


