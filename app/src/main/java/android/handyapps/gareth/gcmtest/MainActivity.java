package android.handyapps.gareth.gcmtest;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;


public class MainActivity extends ActionBarActivity {

    private GoogleCloudMessaging googleCloudMessaging;
    EditText msg;
    String GCMRegID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If GCMRegID stored in shared prefs is not set
        if (checkSharedPrefs().equals("")) {
            // Register device for GCM
            new GCMRegistration().execute();
        }
        else{
            Log.i("GCM ID",checkSharedPrefs());
        }
    }

    // Return GCM RegID
    private String checkSharedPrefs() {

        SharedPreferences savedValue = this.getSharedPreferences("GCMID", 0);
        return savedValue.getString("ID", "");
    }

    // Save GCM RegID to shared prefs
    private void setSharedPreferences() {

        SharedPreferences savedValue = this.getSharedPreferences("GCMID", 0);
        SharedPreferences.Editor editor = savedValue.edit();
        editor.putString("ID", GCMRegID);
        editor.apply();
    }

    // Obtain message from edit text and send the message
    public void SendNotification(View view) {

        msg = (EditText)findViewById(R.id.notification);
        new SendNotification().execute();

    }

    private class SendNotification extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String response;

            try {
                String url = "http://gcm.garethprice.co.za/pushNotification.php?msg=" + URLEncoder.encode(msg.getText().toString(), "UTF-8");

                HttpEntity httpEntity;

                // Creating a new DefaultHttpClient to establish connection
                DefaultHttpClient httpClient = new DefaultHttpClient();

                // retrieves information from the url
                HttpGet httpGet = new HttpGet(url);

                // the http response
                HttpResponse httpResponse = httpClient.execute(httpGet);

                // Obtains the message entity of the response
                httpEntity = httpResponse.getEntity();

                if (httpEntity != null) {

                    try {
                        response = EntityUtils.toString(httpEntity);
                        response = "[" + response + "]";
                        Log.v("entityResponse", response);

                    } catch (IOException e) {
                        Log.v("--IOException--", e.getMessage());
                    }
                }
            } catch (ClientProtocolException e) {
                Log.v("--ClientProtocolException--", e.getMessage());
            } catch (IOException e) {
                Log.v("--IOException--", e.getMessage());
            }

            return null;
        }
    }

    private class GCMRegistration extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if(googleCloudMessaging == null){
                  googleCloudMessaging = GoogleCloudMessaging.getInstance(getApplicationContext());
                }
                // Registering the device for GCM
                GCMRegID = googleCloudMessaging.register(getString(R.string.project_number));

            }
            catch (IOException e) {
                Log.v("--GCM Registration Error--", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i("--GCM REGISTRATION ID--", GCMRegID);
            // Saving the GCM RegID
            setSharedPreferences();
            // Inserting the GCM RegID into the DB
            new InsertRegistrationID().execute(new RegistrationResponse(GCMRegID));

        }
    }

    private class InsertRegistrationID extends AsyncTask<RegistrationResponse, Void, JSONArray> {


        @Override
        protected JSONArray doInBackground(RegistrationResponse... params) {
            return params[0].getRegistrationResponse();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            try{
                String response;

                for(int i = 0;i < jsonArray.length();i++){
                    JSONObject json = jsonArray.getJSONObject(i);
                    response = json.getString("response");

                    if(response.equals("true")){
                        Toast.makeText(MainActivity.this,"Registration Complete",Toast.LENGTH_LONG).show();
                    }
                }
            }
            catch (JSONException e) {
                Log.v("--JSONException--", e.getMessage());
            }
        }
    }
}

