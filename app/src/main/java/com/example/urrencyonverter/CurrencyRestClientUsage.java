package com.example.urrencyonverter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import cz.msebera.android.httpclient.Header;
import org.json.*;
import com.loopj.android.http.*;

class CurrencyRestClientUsage {

    public void getResponseCurrency(String fromToName, Context context, OnJSONResponseCallback callback)  {

        CurrencyRestClient.get(fromToName, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Double rate = -1.0;
                try {
                    rate = (Double) response.get(fromToName);
                } catch(ClassCastException e){
                    Integer value = 0;
                    try {
                        value = (Integer) response.get(fromToName);
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                    rate = Double.valueOf(value);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("JSONObject: ", "" + response);
                callback.onJSONResponse(statusCode, rate);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                sendError(statusCode, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                sendError(statusCode, throwable);
            }

            private void sendError(int statusCode, Throwable throwable){
                callback.onJSONResponse(statusCode, 0.0);
                Log.d("Failed_onFailure: ", "" + statusCode);
                Log.d("Error : ", "" + throwable);
            }
        });
    }
}