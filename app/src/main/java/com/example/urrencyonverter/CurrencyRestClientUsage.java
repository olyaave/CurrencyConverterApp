package com.example.urrencyonverter;

import android.util.Log;
import cz.msebera.android.httpclient.Header;
import org.json.*;
import com.loopj.android.http.*;
import org.json.simple.parser.JSONParser;

class CurrencyRestClientUsage {


    public void getResponseCurrency(String from, String to, OnJSONResponseCallback callback)  {
        CurrencyRestClient.get(new String(from + "_" + to), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                JSONObject jsonObject = response;
                Double rate = -1.0;
//                try {
//                    rate = (Double) jsonObject.get(new String(from + "_" + to));
//                } catch(ClassCastException e){
//                    Integer value = 0;
//                    try {
//                        value = (Integer) jsonObject.get(new String(from + "_" + to));
//                    } catch (JSONException jsonException) {
//                        jsonException.printStackTrace();
//                    }
//                    rate = Double.valueOf(value);
//                }
//                catch (JSONException e) {
//                    e.printStackTrace();
//                }

                Log.d("JSONObject: ", "" + response);
//                Log.d("Rate: ", "" + rate);
                callback.onJSONResponse(true, rate);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("Failed_onFailure: ", "" + statusCode);
                Log.d("Error : ", "" + throwable);
                callback.onJSONResponse(false, -1.0);
            }
            //
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Failed_onFailure: ", "" + statusCode);
                Log.d("Error : ", "" + throwable);
                callback.onJSONResponse(false, -1.0);
            }

        });
    }

}