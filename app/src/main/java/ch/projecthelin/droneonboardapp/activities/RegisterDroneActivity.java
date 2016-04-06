package ch.projecthelin.droneonboardapp.activities;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.o3dr.services.android.lib.util.Utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

//import ch.helin.messages.MySimpleMessage;
import ch.projecthelin.droneonboardapp.DroneOnboardApp;
import ch.projecthelin.droneonboardapp.R;
import ch.projecthelin.droneonboardapp.services.MessagingConnectionService;
import ch.projecthelin.droneonboardapp.services.RegisterDroneService;

public class RegisterDroneActivity extends AppCompatActivity {

    private EditText droneNameTextField;
    private EditText codeTextField;
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
        this.codeTextField = (EditText) findViewById(R.id.code);
        this.payloadTextField = (EditText) findViewById(R.id.payload);
        this.ipTextField = (EditText) findViewById(R.id.ip);
        this.portTextField = (EditText) findViewById(R.id.port);
        this.registerButton = (Button) findViewById(R.id.register_button);

        //Set port and IP to default
        this.ipTextField.setText(IP_ADDRESS);
        this.portTextField.setText(PORT);
    }

    public void onRegisterButtonClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);

        String ip = this.ipTextField.getText().toString();
        String port = this.portTextField.getText().toString();
        String hostAddress = null;
        String serverAddress = ip + ":5672";

        try {
            String s = new RegisterDroneService().execute("http://" + ip + ":" + port + "/registerDrone").get();
            Log.d(getClass().getCanonicalName(), s);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            Toast.makeText(this, "UnknownHostException: Error finding IP!", Toast.LENGTH_LONG);
            return;
        }
        hostAddress = hostAddress + ":5672";

        messagingConnectionService.connect(hostAddress, serverAddress);

        startActivity(intent);
    }
}
