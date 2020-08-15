package com.example.urrencyonverter;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;
import com.example.urrencyonverter.DatabaseRoom.AppDatabase;
import com.example.urrencyonverter.DatabaseRoom.ThreadDeleting;
import com.example.urrencyonverter.DatabaseRoom.ThreadInsert;

import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        TextView.OnEditorActionListener, NoticeDialogFragment.NoticeDialogListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            setContentView(R.layout.activity_main);
        else
            setContentView(R.layout.activity_land);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-1")
                .fallbackToDestructiveMigration().build();

        listOfCurrencyName = getResources().getStringArray(R.array.currencies);
        listOfCurrencyValue = new ArrayList<>();
        if (savedInstanceState != null) {
            listOfCurrencyValue = (ArrayList<currencyPair>) savedInstanceState.getSerializable("listOfValue");
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

    public  void clearDatabase(){
        ThreadDeleting thread = new ThreadDeleting(db);
        thread.start();
    }

    public  void updateDatabase(){
        if(listOfCurrencyValue.size() % 5 == 0 && listOfCurrencyValue.size() > 0){
            // update last 15 elements
            ThreadInsert thread = new ThreadInsert(db);
            thread.addList(listOfCurrencyValue);
            thread.start();
//            Log.i("Database:", "У меня закончились силы..........");
        }
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
        try {
            rate = Double.parseDouble(str.replaceAll(",", ".")) / rate;
        } catch (NullPointerException e){
            Log.e("SetReverseRateValue: ", "При делении rate = null!");
        }
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
        if(findValueInList(from_to) != null) return;
        listOfCurrencyValue.add(new currencyPair(from_to, rate));
        updateDatabase();
    }

    private Double findValueInList(String from_to){
        for (com.example.urrencyonverter.currencyPair currencyPair : listOfCurrencyValue) {
            if(currencyPair.fromToName.equals(from_to))
                return currencyPair.value;
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

        outState.putSerializable("listOfValue", listOfCurrencyValue);
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

    AppDatabase db;

    private String[] listOfCurrencyName;
    private ArrayList<currencyPair> listOfCurrencyValue;

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