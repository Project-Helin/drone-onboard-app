package ch.projecthelin.droneonboardapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.o3dr.services.android.lib.drone.connection.ConnectionParameter;
import com.o3dr.services.android.lib.drone.connection.ConnectionType;

import ch.projecthelin.droneonboardapp.R;
import ch.projecthelin.droneonboardapp.services.DroneConnectionService;

public class DroneFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // here comment
    private Spinner connectionSelector;

    public static final String TCP_SERVER_IP = "192.168.56.1";
    public static final int BAUD_RATE_FOR_USB = 115200;
    public static final int TCP_SERVER_PORT = 5760;

    private DroneConnectionService droneConnectionService;

    private ConnectionParameter connectionParams;

    public DroneFragment() {
        // Required empty public constructor
        droneConnectionService = DroneConnectionService.getInstance(this.getContext());


    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DroneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DroneFragment newInstance(String param1, String param2) {
        DroneFragment fragment = new DroneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    protected void setupConnectionModeSpinner(Spinner spinner) {
        String[] connectionModes = {"USB", "UDP", "TCP"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, connectionModes);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onConnectionSelected(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
    public void btnConnect(View view){
        droneConnectionService.connect();
    }

    public void onConnectionSelected(View view) {
        int connectionType = (int) this.connectionSelector.getSelectedItemPosition();

        Bundle extraParams = new Bundle();

        if (connectionType == ConnectionType.TYPE_USB) {
            extraParams.putInt(ConnectionType.EXTRA_USB_BAUD_RATE, BAUD_RATE_FOR_USB);
        } else if (connectionType == ConnectionType.TYPE_TCP) {
            extraParams.putString(ConnectionType.EXTRA_TCP_SERVER_IP, TCP_SERVER_IP);
            extraParams.putInt(ConnectionType.EXTRA_TCP_SERVER_PORT, TCP_SERVER_PORT);
        }

        connectionParams = new ConnectionParameter(connectionType, extraParams, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drone, container, false);

        connectionSelector = (Spinner) view.findViewById(R.id.spnConnectionMode);
        setupConnectionModeSpinner(connectionSelector);

        return view;
    }

    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}