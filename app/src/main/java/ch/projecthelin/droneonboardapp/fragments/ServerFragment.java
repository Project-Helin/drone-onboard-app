package ch.projecthelin.droneonboardapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import ch.projecthelin.droneonboardapp.DroneOnboardApp;
import ch.projecthelin.droneonboardapp.MessageListener;
import ch.projecthelin.droneonboardapp.R;
import ch.projecthelin.droneonboardapp.dto.dronestate.BatteryState;
import ch.projecthelin.droneonboardapp.dto.dronestate.DroneState;
import ch.projecthelin.droneonboardapp.dto.dronestate.GPSState;
import ch.projecthelin.droneonboardapp.services.DroneConnectionListener;
import ch.projecthelin.droneonboardapp.services.DroneConnectionService;
import ch.projecthelin.droneonboardapp.services.MessagingConnectionService;
import com.o3dr.android.client.apis.drone.DroneStateApi;
import com.o3dr.services.android.lib.drone.property.VehicleMode;

import javax.inject.Inject;

public class ServerFragment extends Fragment implements MessageListener {

    @Inject
    MessagingConnectionService messagingConnectionService;

    @Inject
    DroneConnectionService droneConnectionService;

    private TextView txtGps;
    private TextView txtPosition;
    private TextView txtBattery;
    private TextView txtAltitude;
    private TextView txtSpeed;
    private TextView txtFirmware;

    private Button btnConnect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DroneOnboardApp) getActivity().getApplication()).component().inject(this);
        messagingConnectionService.addListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_drone, container, false);

        initializeViewFields(view);
        initializeBtnListeners();

        return view;
    }

    @Override
    public void onDestroy () {
        messagingConnectionService.removeListener(this);
    }

    private void initializeBtnListeners() {
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                messagingConnectionService.connect();
            }
        });
    }

    private void initializeViewFields(View view) {
        txtGps = (TextView) view.findViewById(R.id.txtGPS);
        txtPosition = (TextView) view.findViewById(R.id.txtPosition);
        txtBattery = (TextView) view.findViewById(R.id.txtBattery);
        txtAltitude = (TextView) view.findViewById(R.id.txtAltitude);
        txtSpeed = (TextView) view.findViewById(R.id.txtSpeed);
        txtFirmware = (TextView) view.findViewById(R.id.txtFirmware);
        btnConnect = (Button) view.findViewById(R.id.btnConnectToDrone);
    }

    protected void alertUser(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMessageReceived(final String message) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                alertUser(message);
                droneConnectionService.takeOff();

            }
        });
    }
}
