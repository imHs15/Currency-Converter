package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    Spinner currencies1;
    Spinner currencies2;
    String currency1;
    String currency2;
    double value1;
    double value2;
    EditText val1;
    EditText val2;
    GifImageView loading;
    TextView textView;
    TextView title1;
    TextView title2;
    ArrayList<String> currencies;
    ArrayList<String> finalCurrencies;
    ArrayAdapter<String> adapter;
    Button reset1;
    Button reset2;
    ImageView convertButton;

    public void reset1(View view){
        val1.setText("0.0");
    }
    public void reset2(View view){
        val2.setText("0.0");
    }

    public class DownloadCountry extends AsyncTask<String, Void, String> {
        @Override public void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
            currencies1.setVisibility(View.INVISIBLE);
            currencies2.setVisibility(View.INVISIBLE);
            reset1.setVisibility(View.INVISIBLE);
            reset2.setVisibility(View.INVISIBLE);
            val1.setVisibility(View.INVISIBLE);
            val2.setVisibility(View.INVISIBLE);
            convertButton.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
            title1.setVisibility(View.INVISIBLE);
            title2.setVisibility(View.INVISIBLE);
        }

        public <String> ArrayList<String> removeDuplicates(ArrayList<String> list)
        {
            ArrayList<String> newList = new ArrayList<String>();
            for (String element : list) {
                if (!newList.contains(element)) {

                    newList.add(element);
                }
            }
            return newList;
        }

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                Log.i("Site", result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Err", "Site Not Found!");
                return null;
            }
        }
        @Override
        protected void onPostExecute(String s) {
            loading.setVisibility(View.INVISIBLE);
            currencies1.setVisibility(View.VISIBLE);
            currencies2.setVisibility(View.VISIBLE);
            reset1.setVisibility(View.VISIBLE);
            reset2.setVisibility(View.VISIBLE);
            val1.setVisibility(View.VISIBLE);
            val2.setVisibility(View.VISIBLE);
            convertButton.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            title1.setVisibility(View.VISIBLE);
            title2.setVisibility(View.VISIBLE);
            super.onPostExecute(s);
            try {
                JSONObject obj = new JSONObject(s);
                String results = obj.getString("results");
                JSONObject res = new JSONObject(results);
                Iterator<?> keys = res.keys();
                while(keys.hasNext()){
                    String id = (String)keys.next();
                    if (res.get(id) instanceof JSONObject) {
                        JSONObject xx = new JSONObject(res.get(id).toString());
                        String currencyId = xx.getString("currencyId");
                        currencies.add(currencyId);
                    }
                }
                Collections.sort(currencies);
                finalCurrencies = removeDuplicates(currencies);
                finalCurrencies.add(0, "Select Currency ID");
                Log.i("Currencies", finalCurrencies.toString());
                adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, finalCurrencies);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                currencies1.setAdapter(adapter);
                currencies2.setAdapter(adapter);
                currencies1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(i == 0){
                            currency1 = "";
                        }else{
                            currency1 = currencies1.getItemAtPosition(i).toString();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                currencies2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(i == 0){
                            currency2 = "";
                        }else{
                            currency2 = currencies2.getItemAtPosition(i).toString();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                Log.i("Currencies Selected" , currency1+"_"+currency2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class DownloadFactor extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                Log.i("Site", result);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Err", "Site Not Found!");
                return null;
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject obj = new JSONObject(s);
                String conversion = obj.getString(currency1 + "_" + currency2);
                Log.i("Factor", conversion);
                double factor = Double.parseDouble(conversion);
                if(value1==0) {
                    value1 = value2 /factor;
                    String convert1 = String.format("%.2f", value1);
                    val1.setText(convert1);
                    val1.setTextColor(Color.parseColor("#008000"));
                    val2.setTextColor(Color.parseColor("#000000"));
                    textView.setText(convert1+" "+currency1+" is "+val2.getText().toString()+" "+currency2);
                }
                else{
                    value2 = value1*factor;
                    String convert1 = String.format("%.2f", value2);
                    val2.setText(convert1);
                    val2.setTextColor(Color.parseColor("#008000"));
                    val1.setTextColor(Color.parseColor("#000000"));
                    textView.setText(val1.getText().toString()+" "+currency1+" is "+convert1+" "+currency2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        convertButton = findViewById(R.id.imageView);
        currencies1 = findViewById(R.id.currency1);
        currencies2 = findViewById(R.id.currency2);
        reset1 = findViewById(R.id.reset1);
        reset2 = findViewById(R.id.reset2);
        currencies = new ArrayList<String>();
        finalCurrencies = new ArrayList<String>();
        val1 = findViewById(R.id.editTextCurrency1);
        val2 = findViewById(R.id.editTextCurrency2);
        val1.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        val2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        textView = findViewById(R.id.textView);
        title1 = findViewById(R.id.title1);
        title2 = findViewById(R.id.title2);
        DownloadCountry task  = new DownloadCountry();
        loading = findViewById(R.id.loading);
        task.execute("https://free.currconv.com/api/v7/countries?apiKey=4df2d4f84e3ecf45424d");
    }

    public void convert(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        textView.setText("");
        if(val1.getText().toString().equals("")){
            value1=0.0;
            if(val2.getText().toString().equals("")){
                value2=0.0;
            }
        }
        else if(val2.getText().toString().equals("")){
            value2=0.0;
            if(val1.getText().toString().equals("")){
                value1=0.0;
            }
        }else {
            value1 = Double.parseDouble(val1.getText().toString());
            value2 = Double.parseDouble(val2.getText().toString());
        }

        if(value1==value2&&value1==0){
            Toast.makeText(MainActivity.this, "No Value Entered!", Toast.LENGTH_SHORT).show();
        }
        else if(value1!=0 && value2!=0){
            Toast.makeText(MainActivity.this, "Incorrect values entered!", Toast.LENGTH_SHORT).show();
        }
        else {
            DownloadFactor task  = new DownloadFactor();
            if(currency1.equals("") || currency2.equals("")){
                Toast.makeText(MainActivity.this, "Select a currency", Toast.LENGTH_SHORT).show();
            }else {
                task.execute("https://free.currconv.com/api/v7/convert?q=" + currency1 + "_" + currency2 + "&compact=ultra&apiKey=ab4b8963b114642a14aa");
            }
        }
    }
}