package ch.projecthelin.droneonboardapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

import ch.helin.messages.converter.JsonBasedMessageConverter;
import ch.helin.messages.dto.message.stateMessage.BatteryStateMessage;
import ch.helin.messages.dto.message.stateMessage.DroneStateMessage;
import ch.helin.messages.dto.message.stateMessage.GpsStateMessage;
import ch.helin.messages.dto.state.BatteryState;

import ch.helin.messages.dto.state.DroneState;

import ch.helin.messages.dto.state.GpsState;
import ch.projecthelin.droneonboardapp.DroneOnboardApp;
import ch.projecthelin.droneonboardapp.R;

import ch.projecthelin.droneonboardapp.fragments.DroneFragment;
import ch.projecthelin.droneonboardapp.fragments.OverviewFragment;
import ch.projecthelin.droneonboardapp.fragments.ServerFragment;
import ch.projecthelin.droneonboardapp.services.DroneConnectionListener;
import ch.projecthelin.droneonboardapp.services.DroneConnectionService;
import ch.projecthelin.droneonboardapp.services.MessagingConnectionService;

public class MainActivity extends AppCompatActivity implements DroneConnectionListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Inject
    MessagingConnectionService messagingConnectionService;

    @Inject
    DroneConnectionService droneConnectionService;

    private JsonBasedMessageConverter jsonBasedMessageConverter = new JsonBasedMessageConverter();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DroneOnboardApp) getApplication()).component().inject(this);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        droneConnectionService.addConnectionListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void goToMissionScreen(View view) {
        Intent intent = new Intent(this, MissionActivity.class);
        startActivity(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDroneStateChange(DroneState state) {
        DroneStateMessage droneStateMessage = new DroneStateMessage();
        droneStateMessage.setDroneState(state);

    }

    @Override
    public void onGpsStateChange(GpsState state) {
        GpsStateMessage gpsStateMessage = new GpsStateMessage();
        gpsStateMessage.setGpsState(state);
        Log.d(getClass().getCanonicalName(), "Send Message: " + gpsStateMessage.toString());

        messagingConnectionService.sendMessage(jsonBasedMessageConverter.parseMessageToString(gpsStateMessage));
    }

    @Override
    public void onBatteryStateChange(BatteryState state) {
        BatteryStateMessage batteryStateMessage = new BatteryStateMessage();
        batteryStateMessage.setBatteryStage(state);

        Log.d(getClass().getCanonicalName(), "Send Message: " + batteryStateMessage.toString());

        messagingConnectionService.sendMessage(jsonBasedMessageConverter.parseMessageToString(batteryStateMessage));
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: {
                    return new OverviewFragment();
                }
                case 1: {
                    return new ServerFragment();
                }
                case 2: {
                    return new DroneFragment();
                }
                default: {
                    return new OverviewFragment();
                }
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Overview";
                case 1:
                    return "Server";
                case 2:
                    return "Drone";
            }
            return null;
        }
    }
}
