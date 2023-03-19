package com.example.myapplication;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewActivity extends AppCompatActivity {

    String des;
    double temp;
    int resID;
    private SharedPreferences mypref;

    static RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_view);

        String tomorrow_date = getIntent().getStringExtra("Tomorrow_date");
        TextView textView = findViewById(R.id.textView5);

        if(getIntent().getStringExtra("Tomorrow_des")!=null){
            des = getIntent().getStringExtra("Tomorrow_des");
            TextView description = findViewById(R.id.des);
            description.setText(des);

            temp = getIntent().getDoubleExtra("Tomorrow_temp",0);
            TextView temperature = findViewById(R.id.temp);
            temperature.setText(String.format("%,.2f", temp) + "C");

            resID = getIntent().getIntExtra("Tomorrow_resId",0);
            ImageView mWeatherImage = findViewById(R.id.weatherSymbolIV);
            mWeatherImage.setImageResource(resID);

        }

//        if(this.mypref.contains("Tomorrow_Des")){
//            TextView description = findViewById(R.id.des);
//            des = MainActivity.mypref.getString("Tomorrow_Des", "unknown");
//            description.setText(des);
//        }
//        if(this.mypref.contains("Tomorrow_Temp")){
//            TextView temperature = findViewById(R.id.temp);
//            temp = parseDouble(MainActivity.mypref.getString("Tomorrow_Temp", "0"));
//            temperature.setText(String.format("%,.2f", temp) + "C");
//        }
//        if(this.mypref.contains("Tomorrow_Res")){
//            resID = MainActivity.mypref.getInt("Tomorrow_Res", 0);
//            ImageView mWeatherImage = findViewById(R.id.weatherSymbolIV);
//            mWeatherImage.setImageResource(resID);
//        }

        textView.setText(tomorrow_date);
        navClick();
        handleClick();
        queue  = Volley.newRequestQueue(this);
    }
    public void navClick(){
        Button butt = findViewById(R.id.button);
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextScreen = new Intent(NewActivity.this, MainActivity.class);
                startActivity(nextScreen);
            }
        });

    }



    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("WEATHER_DES_Tomorrow",des);
        outState.putDouble("Temp_Tomorrow",temp);
        outState.putInt("Icon_Tomorrow",resID);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstance){
        super.onRestoreInstanceState(savedInstance);
        if(savedInstance != null){
            des = savedInstance.getString("WEATHER_DES_Tomorrow");
            if(des==null){
                des="Click the button to refresh";
            }
            temp = savedInstance.getDouble("Temp_Tomorrow");
            resID = savedInstance.getInt("Icon_Tomorrow");
        }
        Log.d("DEBUGGED", des);
        TextView description = findViewById(R.id.des);
        description.setText(des);
        TextView temperature = findViewById(R.id.temp);
        temperature.setText(temp + "C");
        ImageView mWeatherImage = findViewById(R.id.weatherSymbolIV);
        mWeatherImage.setImageResource(resID);
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("On Pause", "On_pause");
        MainActivity.mypref.edit().putString("Tomorrow_Des",des).apply();
        MainActivity.mypref.edit().putString("Tomorrow_Temp", Double.toString(temp)).apply();
        MainActivity.mypref.edit().putInt("Tomorrow_Res",resID).apply();
//        this.mypref.edit().putString("Tomorrow_Des",des).apply();
//        this.mypref.edit().putString("Tomorrow_Temp", Double.toString(temp)).apply();
//        this.mypref.edit().putInt("Tomorrow_Res",resID).apply();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("On Resume", "On_resume");
        if(MainActivity.mypref.contains("Tomorrow_Des")){
            TextView description = findViewById(R.id.des);
            des = MainActivity.mypref.getString("Tomorrow_Des", "unknown");
            description.setText(des);
        }
        if(MainActivity.mypref.contains("Tomorrow_Temp")){
            TextView temperature = findViewById(R.id.temp);
            temp = parseDouble(MainActivity.mypref.getString("Tomorrow_Temp", "0"));
            temperature.setText(String.format("%,.2f", temp) + "C");
        }
        if(MainActivity.mypref.contains("Tomorrow_Res")){
            resID = MainActivity.mypref.getInt("Tomorrow_Res", 0);
            ImageView mWeatherImage = findViewById(R.id.weatherSymbolIV);
            mWeatherImage.setImageResource(resID);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void handleClick(){
        Button butt = findViewById(R.id.getForecast);
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast();
            }
        });
    }

    public void getForecast(){
//        String url = "https://dataservice.accuweather.com/currentconditions/v1/134771?apikey=2E8WczxDe3jPwNoin1qthzrZaVCYDbP7";
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=Tampere,fi&appid=f8a5467bfbf5c0747dcf5b885d67852f";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    parseJSON(response);
                }, error -> {
        }
        );
        queue.add(stringRequest);
    }

    private void parseJSON(String res){
        try {
//            JSONArray weatherArray = new JSONArray(res);
//            des = weatherArray.getJSONObject(0).getString("WeatherText");
//            TextView description = findViewById(R.id.des);
//            description.setText(des);
//            temp = weatherArray.getJSONObject(0).getJSONObject("Temperature").getJSONObject("Metric").getDouble("Value");
//            TextView temperature = findViewById(R.id.temp);
//            temperature.setText(temp + "C");
            JSONObject weatherObj = new JSONObject(res);
            JSONObject weather = weatherObj.getJSONArray("list").getJSONObject(4);
            des = weather.getJSONArray("weather").getJSONObject(0).getString("description");
            TextView description = findViewById(R.id.des);
            description.setText(des);
            temp = weather.getJSONObject("main").getDouble("temp") - 273.15;
            TextView temperature = findViewById(R.id.temp);
            temperature.setText(String.format("%,.2f", temp) + "C");
            int cond = weather.getJSONArray("weather").getJSONObject(0).getInt("id");
            ImageView mWeatherImage = findViewById(R.id.weatherSymbolIV);
            resID = getResources().getIdentifier(this.updateWeatherIcon(cond), "drawable", getPackageName());
            mWeatherImage.setImageResource(resID);

        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    private String updateWeatherIcon(double condition) {

        if (condition >= 0 && condition < 300) {
            return "tstorm1";
        } else if (condition >= 300 && condition < 500) {
            return "light_rain";
        } else if (condition >= 500 && condition < 600) {
            return "shower3";
        } else if (condition >= 600 && condition <= 700) {
            return "snow4";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "tstorm3";
        }

        return "dunno";
    }
}