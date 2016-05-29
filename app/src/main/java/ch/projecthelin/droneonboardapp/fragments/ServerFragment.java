package ch.projecthelin.droneonboardapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import ch.projecthelin.droneonboardapp.DroneOnboardApp;
import ch.projecthelin.droneonboardapp.listeners.MessagingConnectionListener;
import ch.projecthelin.droneonboardapp.R;
import ch.projecthelin.droneonboardapp.services.DroneConnectionService;
import ch.projecthelin.droneonboardapp.services.MessagingConnectionService;

import javax.inject.Inject;
import java.util.Calendar;

public class ServerFragment extends Fragment implements MessagingConnectionListener {

    @Inject
    MessagingConnectionService messagingConnectionService;

    @Inject
    DroneConnectionService droneConnectionService;

    private TextView txtConnectionState;
    private TextView txtErrorLog;
    private TextView txtIP;
    private Button btnConnect;
    private Switch localConnectionSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DroneOnboardApp) getActivity().getApplication()).component().inject(this);
        messagingConnectionService.addConnectionListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_server, container, false);

        initializeViewComponents(view);
        initializeBtnListeners();

        txtErrorLog.append(messagingConnectionService.connectionState.name() + "\n");
        txtConnectionState.setText(messagingConnectionService.connectionState.name());
        txtIP.setText(messagingConnectionService.getRabbitMqServerAddress());

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        messagingConnectionService.removeConnectionListener(this);
    }


    private void initializeBtnListeners() {
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (messagingConnectionService.connectionState.equals(MessagingConnectionService.ConnectionState.CONNECTED)) {
                    messagingConnectionService.disconnect();
                } else {
                    messagingConnectionService.connect();
                }
            }
        });

        localConnectionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                messagingConnectionService.setIsLocalConnection(isChecked);
            }
        });

    }

    private void initializeViewComponents(View view) {
        txtConnectionState = (TextView) view.findViewById(R.id.txtConnectionState);
        txtIP = (TextView) view.findViewById(R.id.txtIP);
        txtErrorLog = (TextView) view.findViewById(R.id.txtErrorLog);
        btnConnect = (Button) view.findViewById(R.id.btnConnectToDrone);
        localConnectionSwitch = (Switch) view.findViewById(R.id.localConnectionSwitch);
    }

    @Override
    public void onConnectionStateChanged(final MessagingConnectionService.ConnectionState state) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                txtErrorLog.setText(getTime() + state.name() + "\n" + txtErrorLog.getText());
                txtConnectionState.setText(messagingConnectionService.connectionState.name());
                updateConnectButton();
            }
        });
    }

    private void updateConnectButton() {
        String buttonText;

        switch (messagingConnectionService.connectionState) {
            case CONNECTED:
                buttonText = "Disconnect";
                break;
            case RECONNECTING:
                buttonText = "Abort Reconnecting";
                break;
            default:
                buttonText = "Connect";
        }

        btnConnect.setText(buttonText);
    }

    private String getTime() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);

        return hour + ":" + minute + ":" + seconds + " ";
    }
}
