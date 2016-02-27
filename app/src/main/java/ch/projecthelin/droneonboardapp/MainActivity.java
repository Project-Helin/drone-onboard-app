package ch.projecthelin.droneonboardapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.o3dr.android.client.ControlTower;
import com.o3dr.android.client.interfaces.TowerListener;

public class MainActivity extends AppCompatActivity implements TowerListener {

    private ControlTower controlTower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.controlTower = new ControlTower(getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        this.controlTower.connect(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        this.controlTower.disconnect();
    }


    @Override
    public void onTowerConnected() {

    }

    @Override
    public void onTowerDisconnected() {

    }
}
