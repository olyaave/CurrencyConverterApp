package com.example.urrencyonverter;

import android.util.Log;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

public class RestClientUsage extends JsonHttpResponseHandler {

    RestClientUsage(String from, String to){
        this.from = from;
        this.to = to;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

        JSONParser parser = new JSONParser();

        Object obj = null;
//                try {
        JSONObject jsonObject = response;

        try {
            rate = (Double) jsonObject.get(new String(from + "_" + to));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Rate: ", "" + rate);

        Log.d("JSONObject: ", "" + response);

    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
        Log.i("INFO: ", "Called onSuccess(int statusCode, Header[] headers, JSONArray timeline)");
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        Log.d("Failed: ", "" + statusCode);
        Log.d("Error : ", "" + throwable);
    }
    //
    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        Log.d("Failed: ", "" + statusCode);
        Log.d("Error : ", "" + throwable);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        Log.d("Failed: ", "" + statusCode);
        Log.d("Error : ", "" + throwable);
    }

    private String from;
    private String to;
    private Double rate;
}
