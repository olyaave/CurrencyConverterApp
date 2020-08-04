package com.example.urrencyonverter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.hamza.slidingsquaresloaderview.SlidingSquareLoaderView;
import cz.msebera.android.httpclient.Header;
import org.json.*;
import com.loopj.android.http.*;

class CurrencyRestClientUsage {

    public void getResponseCurrency(String from, String to, Context context, SlidingSquareLoaderView progressBar, OnJSONResponseCallback callback)  {
        Log.i("Currency: ", from + " " + to);

        CurrencyRestClient.get(new String(from + "_" + to), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                JSONObject jsonObject = response;
                Double rate = -1.0;
                try {
                    rate = (Double) jsonObject.get(new String(from + "_" + to));
                } catch(ClassCastException e){
                    Integer value = 0;
                    try {
                        value = (Integer) jsonObject.get(new String(from + "_" + to));
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                    rate = Double.valueOf(value);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("JSONObject: ", "" + response);
                Log.d("Rate: ", "" + rate);
                callback.onJSONResponse(statusCode, rate);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progressBar.setVisibility(Integer.valueOf(100));
                progressBar.stop();
                if (statusCode == 404) {
                    Toast.makeText(context, context.getResources().getString(R.string.error_404),
                            Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(context, context.getResources().getString(R.string.error_500),
                            Toast.LENGTH_LONG).show();
                } else if (statusCode == 503) {
                    Toast.makeText(context, context.getResources().getString(R.string.error_503),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.error_other),
                            Toast.LENGTH_LONG).show();
                }

                Log.d("Failed_onFailure: ", "" + statusCode);
                Log.d("Error : ", "" + throwable);
            }
        });
    }
}