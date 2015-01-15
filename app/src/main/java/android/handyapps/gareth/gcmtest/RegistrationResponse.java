package android.handyapps.gareth.gcmtest;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

/**
 * Created by Gareth on 2015-01-15.
 */
public class RegistrationResponse {

    private String regID;

    public RegistrationResponse(String rID) {

        regID = rID;
    }

    public JSONArray getRegistrationResponse() {
        JSONArray jsonArray = null;

        try {
            String url = "http://gcm.garethprice.co.za/insertGCMRegID.php?regID=" + regID;

            HttpEntity httpEntity;

            // Creating a new DefaultHttpClient to establish connection
            DefaultHttpClient httpClient = new DefaultHttpClient();

            // retrieves information from the url
            HttpGet httpGet = new HttpGet(url);

            // the http response
            HttpResponse httpResponse = httpClient.execute(httpGet);

            // Obtains the message entity of this response
            httpEntity = httpResponse.getEntity();

            if (httpEntity != null) {

                try {
                    String entityResponse = EntityUtils.toString(httpEntity);
                    entityResponse = "[" + entityResponse + "]";
                    Log.v("entityResponse", entityResponse);
                    jsonArray = new JSONArray(entityResponse);
                } catch (IOException e) {
                    Log.v("--IOException--", e.getMessage());
                } catch (JSONException e) {
                    Log.v("--JSONException--", e.getMessage());
                }
            }
        } catch (ClientProtocolException e) {
            Log.v("--ClientProtocolException--", e.getMessage());
        } catch (IOException e) {
            Log.v("--IOException--", e.getMessage());
        }

        return jsonArray;
    }
}

