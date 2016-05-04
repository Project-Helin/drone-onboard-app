package ch.projecthelin.droneonboardapp.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import javax.inject.Inject;


public class LocationService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private Location currentLocation;
    private LocationRequest mLocationRequest;
    private LocationListener locationListener;
    private static final int UPDATE_DISTANCE_IN_METERS = 5;

    private static final int MIN_UPDATE_INTERVAL_IN_SECONDS = 10;

    @Inject
    public LocationService () {}

    public void startLocationListening(Context context, LocationListener locationListener) {
        this.context = context;
        this.locationListener = locationListener;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mGoogleApiClient.connect();
        }

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(MIN_UPDATE_INTERVAL_IN_SECONDS)
                .setSmallestDisplacement(UPDATE_DISTANCE_IN_METERS);

    }

    public void stopLocationListening() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Context applicationContext = context.getApplicationContext();
            Toast.makeText(applicationContext, "Not Enough Permissions", Toast.LENGTH_LONG).show();
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location updatedLocation) {
        currentLocation = updatedLocation;
        locationListener.onLocationChanged(updatedLocation);
    }



}
