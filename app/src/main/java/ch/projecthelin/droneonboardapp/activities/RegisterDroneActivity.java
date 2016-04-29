package ch.projecthelin.droneonboardapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

//import ch.helin.messages.MySimpleMessage;
import ch.projecthelin.droneonboardapp.DroneOnboardApp;
import ch.projecthelin.droneonboardapp.R;
import ch.projecthelin.droneonboardapp.services.MessagingConnectionService;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterDroneActivity extends AppCompatActivity {

    private EditText droneNameTextField;
    private EditText organisationToken;
    private EditText payloadTextField;
    private EditText ipTextField;
    private EditText portTextField;
    private Button registerButton;

    @Inject
    MessagingConnectionService messagingConnectionService;


    private static final String IP_ADDRESS = "192.168.1.115";
    private static final String PORT = "9000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DroneOnboardApp) getApplication()).component().inject(this);
        setContentView(R.layout.activity_register);
        setUIComponents();
    }

    private void setUIComponents() {
        this.droneNameTextField = (EditText) findViewById(R.id.name);
        this.organisationToken = (EditText) findViewById(R.id.code);
        this.payloadTextField = (EditText) findViewById(R.id.payload);
        this.ipTextField = (EditText) findViewById(R.id.ip);
        this.portTextField = (EditText) findViewById(R.id.port);
        this.registerButton = (Button) findViewById(R.id.register_button);

        //Set port and IP to default
        this.ipTextField.setText(IP_ADDRESS);
        this.portTextField.setText(PORT);
    }

    public void onRegisterButtonClick(View view) {

        String ip = this.ipTextField.getText().toString();
        String port = this.portTextField.getText().toString();
        final String serverAddress = ip + ":5672";
        String url = "http://" + ip + ":" + port + "/api/drones";

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject drone = null;
        try {
            JSONObject droneValues = new JSONObject()
                    .put("name", droneNameTextField.getText())
                    .put("payload", String.valueOf(payloadTextField.getText()))
                    .put("organisationToken", organisationToken.getText());
            drone = new JSONObject().put("drone", droneValues);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, drone,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ddd", "received response");
                        messagingConnectionService.connect("", serverAddress);

//                        Intent intent = new Intent(this, MainActivity.class);
//                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }
        };

        queue.add(postRequest);


    }
}
