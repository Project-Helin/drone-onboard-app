package ch.projecthelin.droneonboardapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import ch.helin.messages.dto.message.DroneDto;
import ch.projecthelin.droneonboardapp.DroneOnboardApp;
import ch.projecthelin.droneonboardapp.R;
import ch.projecthelin.droneonboardapp.services.MessagingConnectionService;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class RegisterDroneActivity extends AppCompatActivity {

    public static final String DRONE_NAME_KEY = "drone_name_key";
    public static final String DRONE_TOKEN_KEY = "drone_token_key";
    private static final String IP_ADDRESS = "152.96.238.18";
    private static final String PORT = "9000";

    @Inject
    MessagingConnectionService messagingConnectionService;

    private EditText droneNameTextField;
    private EditText organisationToken;
    private EditText payloadTextField;
    private EditText ipTextField;
    private EditText portTextField;
    private Button registerButton;
    private String droneName;
    private String droneToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DroneOnboardApp) getApplication()).component().inject(this);
        setContentView(R.layout.activity_register);
        initializeViewComponents();

        boolean alreadyRegistered = loadDroneNameAndTokenFromSharedPreferences();

        if (alreadyRegistered) {
            goToMainActivity();
        }
    }

    private boolean loadDroneNameAndTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        droneName = sharedPreferences.getString(RegisterDroneActivity.DRONE_NAME_KEY, "");
        droneToken = sharedPreferences.getString(RegisterDroneActivity.DRONE_TOKEN_KEY, "");

        return droneName != null;
    }

    private void initializeViewComponents() {
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
        String url = "http://" + ip + ":" + port + "/api/drones";

        JSONObject requestData = createRegisterRequestDataFromInputValues();

        sendRegisterRequest(url, requestData);

    }

    private JSONObject createRegisterRequestDataFromInputValues() {
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
        return drone;
    }

    private void sendRegisterRequest(final String url, final JSONObject drone) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, drone,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        saveDroneNameAndTokenToSharedPreferences(response);

                        goToMainActivity();
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
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }
        };

        queue.add(postRequest);
    }

    private void saveDroneNameAndTokenToSharedPreferences(JSONObject response) {
        Gson gson = new GsonBuilder().create();

        DroneDto droneDto = gson.fromJson(response.toString(), DroneDto.class);

        saveToPreferences(RegisterDroneActivity.DRONE_NAME_KEY, droneDto.getName());
        saveToPreferences(RegisterDroneActivity.DRONE_TOKEN_KEY, droneDto.getToken().toString());
    }

    private void goToMainActivity() {
        Intent intent = new Intent(RegisterDroneActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void saveToPreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

}
