package ch.projecthelin.droneonboardapp;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.o3dr.android.client.ControlTower;
import com.o3dr.android.client.Drone;
import com.o3dr.android.client.interfaces.DroneListener;
import com.o3dr.android.client.interfaces.TowerListener;
import com.o3dr.services.android.lib.drone.attribute.AttributeEvent;
import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
import com.o3dr.services.android.lib.drone.connection.ConnectionResult;
import com.o3dr.services.android.lib.drone.connection.ConnectionType;
import com.o3dr.services.android.lib.drone.property.Type;

public class MainActivity extends AppCompatActivity implements DroneListener, TowerListener {

    private ControlTower controlTower;
    private Drone drone;
    private int droneType = Type.TYPE_UNKNOWN;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.controlTower = new ControlTower(getApplicationContext());
        this.drone = new Drone(getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        this.controlTower.connect(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.drone.isConnected()) {
            this.drone.disconnect();
            updateConnectedButton(false);
        }
        this.controlTower.unregisterDrone(this.drone);
        this.controlTower.disconnect();
    }

    public void onBtnConnectTap(View view) {
        if(this.drone.isConnected()) {
            this.drone.disconnect();
        } else {
            Bundle extraParams = new Bundle();
            //extraParams.putInt(ConnectionType.EXTRA_USB_BAUD_RATE, 115200); //only for USB

            extraParams.putString(ConnectionType.EXTRA_TCP_SERVER_IP, "192.168.57.1");
            extraParams.putInt(ConnectionType.EXTRA_TCP_SERVER_PORT, 5762);// Set default port to 14550
            ConnectionParameter connectionParams = new ConnectionParameter(ConnectionType.TYPE_TCP, extraParams, null);
            this.drone.connect(connectionParams);
            Log.wtf("Helin","connected");
        }
    }

    protected void alertUser(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    protected void updateConnectedButton(Boolean isConnected) {
        Button connectButton = (Button)findViewById(R.id.btnConnect);
        if (isConnected) {
            connectButton.setText("Disconnect");
        } else {
            connectButton.setText("Connect");
        }
    }

    @Override
    public void onTowerConnected() {
        this.controlTower.registerDrone(this.drone, this.handler);
        this.drone.registerDroneListener(this);
    }

    @Override
    public void onTowerDisconnected() {

    }

    @Override
    public void onDroneConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onDroneEvent(String event, Bundle extras) {
        switch (event) {
            case AttributeEvent.STATE_CONNECTED:
                alertUser("Drone Connected");
                updateConnectedButton(this.drone.isConnected());
                break;

            case AttributeEvent.STATE_DISCONNECTED:
                alertUser("Drone Disconnected");
                updateConnectedButton(this.drone.isConnected());
                break;

            default:
                break;
        }
    }

    @Override
    public void onDroneServiceInterrupted(String errorMsg) {

    }
}
