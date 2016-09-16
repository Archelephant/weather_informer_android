package info.lourie.barometer_regex;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    public String html = null;
    //empty array for weather readings
    //public String[] Weather = new String[]{"", "", "", "", "", "", "", ""};
    public String date = null;
    public String time = null;
    public String temp = null;
    public String weact = null;
    public String wind_dir = null;
    public String wind_speed = null;
    public String temp_forecast = null;
    public String humidity = null;
    public String pressure = null;
    public String time_forecast = null;
    public String response = null;

    private class GetWeather extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params){
            doRequest();
            return null;
        }


        public void doRequest(){
            String link = "https://yandex.ru/pogoda/moscow/";
            URL url = null;
            try {
                url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedReader read = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String webpage = "", data = "";
                while ((data = read.readLine()) != null) {
                    webpage += data + "\n";
                }
                html = webpage;

            }catch(MalformedURLException m){
                m.printStackTrace();
                displayException(m.getMessage());
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
                displayException(e.getMessage());
            }catch (IOException e){
                e.printStackTrace();
                displayException(e.getMessage());
            }catch (Exception ex){
                ex.printStackTrace();
                response = ex.getMessage();
                displayException(response);
            }
        }

        @Override
        protected void onPostExecute(String result){
            time = rfind(html, "<span class=\"current-weather__local-time\">(.+?)</span>");
            TextView tv_time = (TextView) findViewById(R.id.textTime);
            tv_time.setText(getString(R.string.Time) + time);

            date = rfind(html, "<dt class=\"day__date\" id=\"d_15\">сегодня,(.+?)</dt>");
            TextView tv_date = (TextView) findViewById(R.id.textDate);
            tv_date.setText(getString(R.string.Date) + date);

            temp = rfind(html, "<i class=\"icon icon_size_46 icon_thumb_bkn-d current-weather__condition-icon\" aria-hidden=\"true\" data-width=\"46\"></i>(.+?)<span");
            TextView tv_temp = (TextView) findViewById(R.id.textTemp);
            tv_temp.setText(getString(R.string.Temp) + temp);

            weact = rfind(html, "<span class=\"current-weather__comment\">(.+?)</span>");
            TextView tv_weact = (TextView) findViewById(R.id.textWeact);
            tv_weact.setText(getString(R.string.Weact) + weact);

            wind_dir = rfind(html, "<abbr class=\" icon-abbr\" title=\"(.+?)\">");
            wind_speed = rfind(html, "<span class=\"wind-speed\">(.+?)</span>");
            TextView tv_wind = (TextView) findViewById(R.id.textWind);
            tv_wind.setText(getString(R.string.Wind) + wind_dir + ", " + wind_speed);

            temp_forecast = rfind(html, "<span class=\"day-part__temp day-part__temp_constant\">(.+?)</span");
            TextView tv_temp_forecast = (TextView) findViewById(R.id.textForetemp);
            tv_temp_forecast.setText(getString(R.string.Foretemp)+temp_forecast);

            humidity = rfind(html, "<div class=\"current-weather__condition-item\">Влажность: (.+?)</div>");
            TextView tv_humidity = (TextView) findViewById(R.id.textHumidity);
            tv_humidity.setText(getString(R.string.Humidity) + humidity);

            pressure = rfind(html, "<div class=\"current-weather__condition-item\">Давление: (.+?)</div>");
            TextView tv_pressure = (TextView) findViewById(R.id.textPressure);
            tv_pressure.setText(getString(R.string.Pressure) + pressure);

            time_forecast = rfind(html, "<div class=\"current-weather__info-row current-weather__info-row_type_time\">(.+?)</div>");
            TextView tv_foretime = (TextView) findViewById(R.id.textForetime);
            //tv_foretime.setText(getString(R.string.Forecast_time) + time_forecast);
            tv_foretime.setText(html);
            tv_foretime.setTextIsSelectable(true);

        }
    }


    public void displayException(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String rfind(String input, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()){
            return matcher.group(1);
        } else return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //A few lines to initialize the TextViews
        TextView tv_time = (TextView) findViewById(R.id.textTime);
        tv_time.setText(getString(R.string.Time));

        TextView tv_date = (TextView) findViewById(R.id.textDate);
        tv_date.setText(getString(R.string.Date));

        TextView tv_temp = (TextView) findViewById(R.id.textTemp);
        tv_temp.setText(getString(R.string.Temp));

        TextView tv_weact = (TextView) findViewById(R.id.textWeact);
        tv_weact.setText(getString(R.string.Weact));

        TextView tv_wind = (TextView) findViewById(R.id.textWind);
        tv_weact.setText(getString(R.string.Wind));

        TextView tv_temp_forecast = (TextView) findViewById(R.id.textForetemp);
        tv_temp_forecast.setText(getString(R.string.Foretemp));

        TextView tv_humidity = (TextView) findViewById(R.id.textHumidity);
        tv_humidity.setText(getString(R.string.Humidity));

        TextView tv_pressure = (TextView) findViewById(R.id.textPressure);
        tv_pressure.setText(getString(R.string.Pressure));

        TextView tv_foretime = (TextView) findViewById(R.id.textForetime);
        tv_foretime.setText(getString(R.string.Forecast_time));

        new GetWeather().execute();

        Button button_refresh = (Button) findViewById(R.id.button_refresh);
        button_refresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                new GetWeather().execute();
            }
        });


    }
}
