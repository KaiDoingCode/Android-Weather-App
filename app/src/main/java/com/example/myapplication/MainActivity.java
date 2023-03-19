package com.example.myapplication;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    String des;
    double temp;
    int resID;
    static RequestQueue queue;
    static SharedPreferences mypref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Calendar c = Calendar.getInstance();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        String formattedTomorrowDate = df2.format(tomorrow.getTime());
        TextView textView = findViewById(R.id.textView8);
        textView.setText(formattedDate);
        navClick(formattedTomorrowDate);
        handleClick();
        queue  = Volley.newRequestQueue(this);

        mypref = PreferenceManager.getDefaultSharedPreferences(this);


    }

    @Override
    protected void onPause(){
        super.onPause();
        mypref.edit().putString("Des",des).apply();
        mypref.edit().putString("Temp", Double.toString(temp)).apply();
        mypref.edit().putInt("Res",resID).apply();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(mypref.contains("Des")){
            TextView description = findViewById(R.id.des);
            des = mypref.getString("Des", "unknown");
            description.setText(des);
        }
        if(mypref.contains("Temp")){
            TextView temperature = findViewById(R.id.temp);
            temp = parseDouble(mypref.getString("Temp", "0"));
            temperature.setText(String.format("%,.2f", temp) + "C");
        }
        if(mypref.contains("Res")){
            resID = mypref.getInt("Res", 0);
            ImageView mWeatherImage = findViewById(R.id.weatherSymbolIV);
            mWeatherImage.setImageResource(resID);
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("WEATHER_DES",des);
        outState.putDouble("Temp",temp);
        outState.putInt("Icon",resID);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstance){
        super.onRestoreInstanceState(savedInstance);
        if(savedInstance != null){
            des = savedInstance.getString("WEATHER_DES");
            if(des==null){
                des="Click the button to refresh";
            }
            temp = savedInstance.getDouble("Temp");
            resID = savedInstance.getInt("Icon");
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
    protected void onDestroy(){
        super.onDestroy();
        Log.d("On_destroyed", "destroyed");

    }

    public void navClick(String tomorrow_date){
        Button butt = findViewById(R.id.button);
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextScreen = new Intent(MainActivity.this, NewActivity.class);
                nextScreen.putExtra("Tomorrow_date", tomorrow_date);

                startActivity(nextScreen);

            }
        });

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
        String url = "https://api.openweathermap.org/data/2.5/weather?q=Tampere,fi&APPID=f8a5467bfbf5c0747dcf5b885d67852f";
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
            JSONObject weather = new JSONObject(res);
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

    public void openWebPage(View view){
        String url = "https://www.google.com";

        Uri uri = Uri.parse(url);

        Intent openWeb = new Intent(Intent.ACTION_VIEW, uri);

        try{
            startActivity(openWeb);
        }catch (ActivityNotFoundException e){
            Log.d("Error",e.toString());
        }
    }


    public void setAlarm(View view){
        Intent openAlarm = new Intent(AlarmClock.ACTION_SET_TIMER)
                .putExtra(AlarmClock.EXTRA_MESSAGE,"Timeout!")
                .putExtra(AlarmClock.EXTRA_LENGTH, 20)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        try{
            startActivity(openAlarm);
        }catch (ActivityNotFoundException e){
            Log.d("Error",e.toString());
        }
    }

    public  void openMap(View view){
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 60.192059, 24.945831);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        try{
            startActivity(intent);
        }catch (ActivityNotFoundException e){
            Log.d("Error",e.toString());
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