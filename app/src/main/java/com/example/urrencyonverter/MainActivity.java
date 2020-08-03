package com.example.urrencyonverter;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fromText = findViewById(R.id.fromText);
        fromText.setText("1");
        toText = findViewById(R.id.toText);
        toText.setText("1");

        Spinner spinnerFrom = findViewById(R.id.spinnerFrom);
        Spinner spinnerTo = findViewById(R.id.spinnerTo);

        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(this, R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
        fromCurrency = spinnerFrom.getResources().getStringArray(R.array.currencies)[0];
        toCurrency = spinnerTo.getResources().getStringArray(R.array.currencies)[0];

        fromText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                setRateValue(fromText, toText);
                return false;
            }
        });
        toText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                setRateValue(toText, fromText);
                return false;
            }
        });

        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                String[] choose = getResources().getStringArray(R.array.currencies);
                fromCurrency = choose[selectedItemPosition];
                setRateValue(fromText, toText);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                String[] choose = getResources().getStringArray(R.array.currencies);
                toCurrency = choose[selectedItemPosition];
                setRateValue(fromText, toText);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



    }

    private void setRateValue(TextView fromTextPrint, TextView toTextPrint){

        if(fromTextPrint.getText().toString().equals("")) {
            fromTextPrint.setText("1");
        }
        CurrencyRestClientUsage clientUsage = new CurrencyRestClientUsage();
        clientUsage.getResponseCurrency(fromCurrency, toCurrency, new OnJSONResponseCallback() {

            @Override
                    public void onJSONResponse(boolean success, Double rate) {
                        if(success){

                            Double fromValue = Double.parseDouble(fromTextPrint.getText().toString());

                            Double rateValue = rate * fromValue;

                            DecimalFormat df = new DecimalFormat("###.##");
                            df.setRoundingMode(RoundingMode.CEILING);

                            Log.i("Line:", df.format(rateValue));
                            toTextPrint.setText(df.format(rateValue));

                        }
                        else throw new NullPointerException();
                    }
                });
    }
    String fromCurrency;
    String toCurrency;
    public Double rate;

    TextView fromText;
    TextView toText;
}
