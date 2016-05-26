package ch.projecthelin.droneonboardapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import ch.helin.messages.dto.MissionDto;
import ch.projecthelin.droneonboardapp.DroneOnboardApp;
import ch.projecthelin.droneonboardapp.R;
import ch.projecthelin.droneonboardapp.services.DroneConnectionService;
import ch.projecthelin.droneonboardapp.services.MessagingConnectionService;
import com.o3dr.android.client.apis.drone.ExperimentalApi;

import javax.inject.Inject;

public class MissionActivity extends AppCompatActivity {

    @Inject
    MessagingConnectionService messagingConnectionService;

    @Inject
    DroneConnectionService droneConnectionService;

    private TextView orderProductAmountText;
    private TextView orderProductNameText;
    private boolean isServoOpen;
    private Button btnServo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DroneOnboardApp) getApplication()).component().inject(this);
        setContentView(R.layout.activity_mission);

        initializeViewComponents();

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        MissionDto currentMission = messagingConnectionService.getCurrentMission();

        orderProductNameText.setText(currentMission.getOrderProduct().getProduct().getName());
        orderProductAmountText.setText(currentMission.getOrderProduct().getAmount().toString());

    }
    private void initializeViewComponents() {
        this.orderProductNameText = (TextView) findViewById(R.id.orderProductName);
        this.orderProductAmountText = (TextView) findViewById(R.id.orderProductAmount);
        this.btnServo = (Button) findViewById(R.id.btnServo);
    }

    public void loadingFinished(View view) {
        Intent result = new Intent(this, MainActivity.class);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    public void toggleServo(View view) {
        int pwm;
        String buttonText;

        if (isServoOpen) {
            pwm = droneConnectionService.getServoClosedPWM();
            isServoOpen = false;
            buttonText = "Open Servo!";
        } else {
            pwm = droneConnectionService.getServoOpenPWM();
            isServoOpen = true;
            buttonText = "Close Servo!";
        }
        ExperimentalApi.setServo(droneConnectionService.getDrone(), droneConnectionService.getServoChannel(), pwm);
        btnServo.setText(buttonText);
    }

    public void cancel(View view) {
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
