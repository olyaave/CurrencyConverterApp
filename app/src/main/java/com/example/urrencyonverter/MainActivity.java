package com.example.urrencyonverter;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.hamza.slidingsquaresloaderview.SlidingSquareLoaderView;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TextView.OnEditorActionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromText = findViewById(R.id.fromText);
        toText = findViewById(R.id.toText);
        fromText.setText("1");
        toText.setText("1");

        fromText.setOnEditorActionListener(this);
        toText.setOnEditorActionListener(this);

        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(this, R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);

        listOfCurrency = getResources().getStringArray(R.array.currencies);
        fromCurrency = listOfCurrency[1];
        toCurrency = listOfCurrency[0];

        spinnerFrom.setSelection(1);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        spinnerFrom.setOnItemSelectedListener(this);
        spinnerTo.setOnItemSelectedListener(this);

    }

    private void setRateValue(){

        if(fromText.getText().toString().equals(""))
            fromText.setText("1");

        progressBar = (SlidingSquareLoaderView) findViewById(R.id.progressBar);
        progressBar.start();


        CurrencyRestClientUsage clientUsage = new CurrencyRestClientUsage();
        clientUsage.getResponseCurrency(fromCurrency, toCurrency, getApplicationContext(), progressBar, new OnJSONResponseCallback()
        {
            @Override
            public void onJSONResponse(int resultCode, Double rate) {
                toText.setText(getValue(rate));
                progressBar.setVisibility(100);
                progressBar.stop();
            }

            public String getValue(Double rate) {
                Double rateValue = rate * Double.parseDouble(fromText.getText().toString());
                DecimalFormat df = new DecimalFormat("###.####");
                df.setRoundingMode(RoundingMode.CEILING);

                Log.i("Rate Value:", df.format(rateValue));
                return df.format(rateValue);
            }
        });
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        setRateValue();
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent,
                               View itemSelected, int selectedItemPosition, long selectedId) {
        if (parent == spinnerFrom)
            fromCurrency = listOfCurrency[selectedItemPosition];
        else
            toCurrency = listOfCurrency[selectedItemPosition];
        setRateValue();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private String[] listOfCurrency;

    private String fromCurrency;
    private String toCurrency;

    private SlidingSquareLoaderView progressBar;

    Spinner spinnerFrom;
    Spinner spinnerTo;

    private TextView fromText;
    private TextView toText;
}
