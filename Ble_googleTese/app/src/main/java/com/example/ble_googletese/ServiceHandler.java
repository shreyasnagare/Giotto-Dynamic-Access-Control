package com.example.ble_googletese;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class ServiceHandler {

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;
    static int getSet;
    String access_token = "";

    SharedPreferences pref;

    public ServiceHandler() {

    }

    public String maketheServiceCall(String url, int method, StringEntity soap, int x) throws IOException {
        getSet = x;
        return this.makeSoapServiceCall(url, method, null, soap);
    }


    /*
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */
    public String makeServiceCall(String url, int method) throws IOException {
        Context context = MainActivity.gettheContext();
        pref = context.getSharedPreferences("MyPref", 0);
        access_token = pref.getString("access_token", null);
        return this.makeServiceCall(url, method, null);
    }

    /*
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */
    public String makeServiceCall(String url, int method,
                                  List<NameValuePair> params) throws IOException {
        try {
            // http client
//            DefaultHttpClient httpClient = new DefaultHttpClient();
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https",
                    SSLSocketFactory.getSocketFactory(), 443));

            schemeRegistry.register( new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

            HttpParams paramss = new BasicHttpParams();

            SingleClientConnManager mgr = new SingleClientConnManager(paramss, schemeRegistry);

            HttpClient httpClient = new DefaultHttpClient(mgr, paramss);
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                httpPost.addHeader("Authorization", "Bearer "+access_token);
                httpPost.addHeader("content-type", "application/json");
                httpPost.addHeader("charset", "utf-8");
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);
                httpGet.addHeader("Authorization", "Bearer "+access_token);
                httpGet.addHeader("content-type", "application/json");
                httpGet.addHeader("charset", "utf-8");

                httpResponse = httpClient.execute(httpGet);

            }
            assert httpResponse != null;
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException | ClientProtocolException e) {
            e.printStackTrace();
        }

        return response;

    }

    public String makeSoapServiceCall(String url, int method,
                                      List<NameValuePair> params, StringEntity soapMessage) throws IOException {
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Content-type", "text/xml; charset=utf-8");

                if(getSet == 1)
                    httpPost.setHeader("SOAPAction", "\"urn:Belkin:service:basicevent:1#SetBinaryState\"");

                else
                    httpPost.setHeader("SOAPAction", "\"urn:Belkin:service:basicevent:1#GetBinaryState\"");

                httpPost.setEntity(soapMessage);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }
                httpResponse = httpClient.execute(httpPost);


            } else if (method == GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);

                httpResponse = httpClient.execute(httpGet);

            }
            assert httpResponse != null;
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException | ClientProtocolException e) {
            Log.d("exception", "here");
            e.printStackTrace();
        }

        return response;

    }
}
