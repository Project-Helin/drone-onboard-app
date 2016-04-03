package ch.projecthelin.droneonboardapp.services;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by styp on 02.04.16.
 */
public class RegisterDroneService extends AsyncTask<String, Void, String> {

    private Exception exception;

    @Override
    protected String doInBackground(String... urls) {
        try{
            StringBuilder result = new StringBuilder();
            URL url = new URL(urls[0]);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            return result.toString();
        } catch(Exception e){
            exception = e;
        }
        return null;
    }

}
