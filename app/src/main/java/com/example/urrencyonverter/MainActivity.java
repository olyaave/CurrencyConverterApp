package com.example.urrencyonverter;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TextView.OnEditorActionListener, NoticeDialogFragment.NoticeDialogListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            setContentView(R.layout.activity_main);
        else
            setContentView(R.layout.activity_land);


        listOfCurrencyName = getResources().getStringArray(R.array.currencies);
        listOfCurrencyValue = new HashMap<String, Double>();
        if (savedInstanceState != null) {
            listOfCurrencyValue = (Map<String, Double>) savedInstanceState.getSerializable("listOfValue");
            isShowingDialog = savedInstanceState.getBoolean("isShow");
            if(isShowingDialog) {
                showNoticeDialog(savedInstanceState.getString("dialogError"));

            }
        }

        progressBar = findViewById(R.id.progressBar);
        frameLayout = findViewById(R.id.frame_layout);

        fromText = findViewById(R.id.fromText);
        toText = findViewById(R.id.toText);
        if (savedInstanceState != null) {
            fromText.setText(savedInstanceState.getString("fromText"));
            toText.setText(savedInstanceState.getString("toText"));
        }
        else fromText.setText("1");

        fromText.setOnEditorActionListener(this);
        toText.setOnEditorActionListener(this);

        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(this, R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        int fromCurrencyName = 1;

        if (savedInstanceState != null){
            spinnerFrom.setSelection(savedInstanceState.getInt("fromSpinnerSelect"));
            spinnerTo.setSelection(savedInstanceState.getInt("toSpinnerSelect"));
            fromCurrency = savedInstanceState.getString("fromCurrency");
            toCurrency = savedInstanceState.getString("toCurrency");
        }
        else {
            fromCurrency = listOfCurrencyName[fromCurrencyName];
            toCurrency = listOfCurrencyName[0];
        }
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
        if(savedInstanceState == null) spinnerFrom.setSelection(fromCurrencyName);

        spinnerFrom.setOnItemSelectedListener(this);
        spinnerTo.setOnItemSelectedListener(this);
    }

    private void setRateValue(){

        if(fromText.getText().toString().equals(""))
            fromText.setText("1");

        progressBarOn();

        Double rate;
        String fromToName = fromCurrency + "_" + toCurrency;
        if((rate = findValueInList(fromToName)) == null) {
            getResponse(fromToName);
        } else
            getValue(rate, toText, false);

    }

    private void getResponse(String fromToName){
        CurrencyRestClientUsage clientUsage = new CurrencyRestClientUsage();
        clientUsage.getResponseCurrency(fromToName, getApplicationContext(), (statusCode, rate) ->
        {
            if(statusCode == 200){
                getValue(rate, toText, false);
                addValueInList(fromToName, rate);
            }
            else{
                progressBarOff();
                if(statusCode == 500 || statusCode == 0)
                    showNoticeDialog(getResources().getString(R.string.error_500));
                else if(statusCode == 503)
                    showNoticeDialog(getResources().getString(R.string.error_503));
                else if(statusCode == 404)
                    showNoticeDialog(getResources().getString(R.string.error_404));
            }
        });
    }

    private void setReversRateValue(){
        progressBarOn();

        Double rate = findValueInList(fromCurrency + "_" + toCurrency);
        String str = "" + toText.getText();

        rate =   Double.parseDouble(str.replaceAll(",", ".")) / rate;

        Log.i("Rate Value:", String.valueOf(rate));

        getValue(rate, fromText, true);
    }

    public void getValue(Double rate, TextView textTo, boolean isReverse) {
        isShowingDialog = false;
        Double currValue = isReverse ? 1.0 : Double.parseDouble(fromText.getText().toString()
                                                                        .replaceAll(",", "."));
        Double rateValue = rate * currValue;
        DecimalFormat df = new DecimalFormat("###.####");
        df.setRoundingMode(RoundingMode.CEILING);
        fromValue = fromText.getText().toString();
        toValue = df.format(rateValue);
        textTo.setText(df.format(rateValue));

        progressBarOff();
    }

    private void addValueInList(String from_to, Double rate){
            listOfCurrencyValue.put(from_to, rate);
    }

    private Double findValueInList(String from_to){
        for (Map.Entry<String, Double> currencyPair : listOfCurrencyValue.entrySet()) {
            if(currencyPair.getKey().equals(from_to))
                return currencyPair.getValue();
        }
        return null;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
        if(v == fromText){
            setRateValue();
        }
        else setReversRateValue();
        fromValue = fromText.getText().toString();
        toValue = toText.getText().toString();
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent,
                               View itemSelected, int selectedItemPosition, long selectedId) {
        if (parent == spinnerFrom)
            fromCurrency = listOfCurrencyName[selectedItemPosition];
        else  toCurrency = listOfCurrencyName[selectedItemPosition];
        setRateValue();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("listOfValue", (Serializable) listOfCurrencyValue);
        outState.putString("fromCurrency", fromCurrency);
        outState.putString("toCurrency", toCurrency);
        outState.putString("fromValue", fromValue);
        outState.putString("toValue", toValue);
        outState.putInt("fromSpinnerSelect", spinnerFrom.getSelectedItemPosition());
        outState.putInt("toSpinnerSelect", spinnerTo.getSelectedItemPosition());
        outState.putBoolean("isShow", isShowingDialog);
        if(isShowingDialog) {
            outState.putString("dialogError", dialog.getTag());
        }
        super.onSaveInstanceState(outState);
    }

    private void progressBarOff(){
        frameLayout.setClickable(false);
        frameLayout.setFocusable(false);
        progressBar.setVisibility(100);

    }
    private void progressBarOn(){
        progressBar.setVisibility(0);
        frameLayout.setClickable(true);
        frameLayout.setFocusable(true);
    }

    public void showNoticeDialog(String errorString) {

        dialog = new NoticeDialogFragment();
        isShowingDialog = true;
        dialog.show(getSupportFragmentManager(), errorString);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        setRateValue();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        finish();
    }

    private String[] listOfCurrencyName;
    private Map<String, Double> listOfCurrencyValue;

    private String fromCurrency;
    private String toCurrency;

    private ProgressBar progressBar;
    private FrameLayout frameLayout;

    DialogFragment dialog;
    boolean isShowingDialog;

    Spinner spinnerFrom;
    Spinner spinnerTo;

    private TextView fromText;
    private TextView toText;

    private String fromValue;
    private String toValue;
}