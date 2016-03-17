package ch.projecthelin.droneonboardapp.activities;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import ch.projecthelin.droneonboardapp.DroneOnboardApp;
import ch.projecthelin.droneonboardapp.R;

public class RegisterDroneActivity extends AppCompatActivity {

    private EditText droneNameTextField;
    private EditText codeTextField;
    private EditText payloadTextField;
    private Button registerButton;

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
        this.registerButton = (Button) findViewById(R.id.register_button);
    }

    public void onRegisterButtonClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
