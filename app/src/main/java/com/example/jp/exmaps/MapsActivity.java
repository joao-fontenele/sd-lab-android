package com.example.jp.exmaps;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import com.example.jp.exmaps.model.CountryModel;
import com.example.jp.exmaps.rest.ApiCountries;
import com.example.jp.exmaps.rest.Country;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean dbReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActiveAndroid.initialize(this);
        this.dbReady = false;
        this.populateTable();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActiveAndroid.dispose();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void markRandomCountry() {
        if (dbReady) {
            CountryModel country = CountryModel.getRandom();

            Log.d("jpdebug", "country chosen: " + country.toString());
            // setup the country's data marker
            LatLng position = new LatLng(country.getLatitude(), country.getLongitude());
            mMap.addMarker(new MarkerOptions().position(position).title(country.getName()));

            // move the camera to the marker
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        } else {
            Log.d("jpdebug", "db not ready yet");
        }
    }

    public void logCountriesFromDb() {
        List<CountryModel> countries = CountryModel.listCountries();
        for (CountryModel country : countries) {
            Log.i("jpdebugDbCountry", country.toString());
        }
    }

    public boolean checkDatabase() {
        long rows = new Select().from(CountryModel.class).count();
        Log.d("jpdebug", rows + " found on the database");
        logCountriesFromDb();
        return rows > 0;
    }

    public void populateTable() {
        Log.d("jpdebug", "entering populateTable");

        ApiCountries.CountryInterface s = ApiCountries.getCountryClient();
        assert s != null;
        s.getCountries(new Callback<List<Country>>() {
            @Override
            public void success(List<Country> countries, Response response) {
                Log.d("jpdebugRest", "retrofit SUCCESS at MapsActivity.populateTable");
//                SQLiteUtils.execSql("DROP TABLE IF EXISTS Country;");
                SQLiteUtils.execSql("DELETE FROM Country;");

                ActiveAndroid.beginTransaction();
                try {
                    for (Country country : countries) {
                        Log.i("jpdebugRest country", country.toString());
                        List<Double> latLng = country.getLatlng();
                        if (latLng.size() > 1) {
                            CountryModel model = new CountryModel(country.getName(), latLng.get(0), latLng.get(1));
                            model.save();
                        }
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                    dbReady = true;
                    markRandomCountry();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("jpdebugRest", "retrofit ERROR at MapsActivity.populateTable");
            }
        });
    }
}
