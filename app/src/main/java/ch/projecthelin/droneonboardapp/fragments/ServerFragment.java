package ch.projecthelin.droneonboardapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import ch.projecthelin.droneonboardapp.DroneOnboardApp;
import ch.projecthelin.droneonboardapp.MessagingListener;
import ch.projecthelin.droneonboardapp.R;
import ch.projecthelin.droneonboardapp.services.DroneConnectionService;
import ch.projecthelin.droneonboardapp.services.MessagingConnectionService;

import javax.inject.Inject;
import java.util.Calendar;

public class ServerFragment extends Fragment implements MessagingListener {

    @Inject
    MessagingConnectionService messagingConnectionService;

    @Inject
    DroneConnectionService droneConnectionService;

    private TextView txtConnectionState;
    private TextView txtErrorLog;
    private TextView txtIP;
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

        View view = inflater.inflate(R.layout.fragment_server, container, false);

        initializeViewFields(view);
        initializeBtnListeners();

        txtErrorLog.append(messagingConnectionService.connectionState.name() + "\n");
        txtConnectionState.setText(messagingConnectionService.connectionState.name());
        txtIP.setText(MessagingConnectionService.RMQ_REMOTE_SERVER_ADDR);

        return view;
    }

    @Override
    public void onDestroy() {
        messagingConnectionService.removeListener(this);
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
    }

    private void initializeViewFields(View view) {
        txtConnectionState = (TextView) view.findViewById(R.id.txtConnectionState);
        txtIP = (TextView) view.findViewById(R.id.txtIP);
        txtErrorLog = (TextView) view.findViewById(R.id.txtErrorLog);
        btnConnect = (Button) view.findViewById(R.id.btnConnectToDrone);
    }

    @Override
    public void onMessageReceived(final String message) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                txtErrorLog.setText(getTime() + message + "\n" + txtErrorLog.getText());
                droneConnectionService.takeOff();
            }
        });
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
