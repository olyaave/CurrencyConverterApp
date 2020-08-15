package com.example.urrencyonverter.ClientRest;

import com.loopj.android.http.*;

public class CurrencyRestClient {

    private static final String BASE_URL = "https://free.currconv.com/api/v7/convert?q=USD_PHP&compact=ultra&apiKey=40d2bfbedfb51dbc97e2";

    private static final AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String currencyKey, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Accept", "application/json");
        client.get(getAbsoluteUrl(currencyKey), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String currencyKey) {
        return "https://free.currconv.com/api/v7/convert?q=" + currencyKey + "&compact=ultra&apiKey=40d2bfbedfb51dbc97e2";
    }

}