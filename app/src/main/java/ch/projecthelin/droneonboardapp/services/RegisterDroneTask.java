package ch.projecthelin.droneonboardapp.services;

import android.os.AsyncTask;
import ch.helin.messages.converter.JsonBasedMessageConverter;
import ch.helin.messages.dto.message.DroneDto;
import ch.helin.messages.dto.message.DroneDtoMessage;
import ch.helin.messages.dto.message.Message;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterDroneTask extends AsyncTask<String, Void, DroneDto> {

    private Exception exception;

    @Inject
    JsonBasedMessageConverter messageConverter;

    @Override
    protected DroneDto doInBackground(String... urls) {
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
            Message message = messageConverter.parseStringToMessage(result.toString());
            DroneDtoMessage droneDtoMessage = (DroneDtoMessage) message;

            return droneDtoMessage.getDroneDto();
        } catch(Exception e){
            exception = e;
        }
        return null;
    }

}
