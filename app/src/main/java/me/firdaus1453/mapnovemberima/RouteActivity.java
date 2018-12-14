package me.firdaus1453.mapnovemberima;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.firdaus1453.mapnovemberima.helper.DirectionMapsV2;
import me.firdaus1453.mapnovemberima.helper.GPStrack;
import me.firdaus1453.mapnovemberima.helper.HeroHelper;
import me.firdaus1453.mapnovemberima.model.Distance;
import me.firdaus1453.mapnovemberima.model.Duration;
import me.firdaus1453.mapnovemberima.model.LegsItem;
import me.firdaus1453.mapnovemberima.model.OverviewPolyline;
import me.firdaus1453.mapnovemberima.model.ResponseRoute;
import me.firdaus1453.mapnovemberima.model.RoutesItem;
import me.firdaus1453.mapnovemberima.network.ApiInterface;
import me.firdaus1453.mapnovemberima.network.RetrofitConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final int REQUEST_LOCATION = 1;
    public static final int REQAWAL = 2;
    public static final int REQAKHIR = 3;
    private GoogleMap mMap;
    DirectionMapsV2 direction;
//    private GoogleApiClient googleApiClient;
//    private GPStrack gps;
//    private double lat;
//    private double lon;
//    private String nama_lokasi;
//    private LatLng lokasinya;
//    private Intent intent;
//    private double latawal;
//    private double lonawal;
//    private LatLng lokasiawal;
//    private double latakhir;
//    private double lonakhir;
//    private List<RoutesItem> routes;
//    private List<LegsItem> legs;
//    private Distance distance;
//    private Duration duration;
//    private String datapoly;


    @BindView(R.id.edtawal)
    EditText edtawal;
    @BindView(R.id.edtakhir)
    EditText edtakhir;
    @BindView(R.id.textjarak)
    TextView textjarak;
    @BindView(R.id.textwaktu)
    TextView textwaktu;
    @BindView(R.id.textharga)
    TextView textharga;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.btnlokasiku)
    Button btnlokasiku;
    @BindView(R.id.btnpanorama)
    Button btnpanorama;
    @BindView(R.id.linearbottom)
    LinearLayout linearbottom;
    @BindView(R.id.spinmode)
    Spinner spinmode;
    @BindView(R.id.relativemap)
    RelativeLayout relativemap;
    @BindView(R.id.frame1)
    FrameLayout frame1;
    private GoogleApiClient googleApiClient;
    private GPStrack gps;
    private double lat;
    private double lon;
    private String nama_lokasi;
    private LatLng lokasisaya;
    private Intent intent;
    private LatLng posisiku;
    private String name_location;
    private OverviewPolyline overPolyline;
    private Distance distances;
    private Duration durations;
    private List<RoutesItem> route;
    private List<LegsItem> legs;
    private double latawal;
    private double lonawal;
    private double latakhir;
    private double lonakhir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        cekStatusGPS();
    }

    private void cekStatusGPS() {
        // Membuat object untuk kita dapat mengakses location service atau GPS
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Melakukan pengecekan apakah GPS sudah berhasil aktif atau tidak
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS sudah di enabale", Toast.LENGTH_SHORT).show();
        }
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS disable", Toast.LENGTH_SHORT).show();
            enableLocation();
        }
    }

    private void enableLocation() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        }
                    }).build();

            googleApiClient.connect();
            final LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            final PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                    final Status status = locationSettingsResult.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(RouteActivity.this, REQUEST_LOCATION);
                                finish();
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;

                    }
                }
            });
        }
    }

    @OnClick({R.id.btnlokasiku, R.id.btnpanorama, R.id.edtawal, R.id.edtakhir})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnlokasiku:
                akseslokasiku();
                break;
            case R.id.btnpanorama:
                aksesPanorama();
                break;
            case R.id.edtawal:
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(RouteActivity.this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                //
                startActivityForResult(intent, REQAWAL);
                break;
            case R.id.edtakhir:
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(RouteActivity.this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                //
                startActivityForResult(intent, REQAKHIR);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Place place = PlaceAutocomplete.getPlace(this, data);
        if (requestCode == REQAWAL && resultCode != 0) {
            //get koordinat lat long
            latawal = place.getLatLng().latitude;
            lonawal = place.getLatLng().longitude;
            nama_lokasi = place.getName().toString();
            edtawal.setText(nama_lokasi);
            mMap.clear();
            addMarker(latawal, lonawal);
        } else if (requestCode == REQAKHIR && resultCode != 0) {
            //get koordinat lat long
            latakhir = place.getLatLng().latitude;
            lonakhir = place.getLatLng().longitude;
            nama_lokasi = place.getName().toString();
            edtakhir.setText(nama_lokasi);
            addMarker(latakhir, lonawal);
            aksesrute();
        }
    }

    private void aksesrute() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Proses pencarian rute", "Loading");
        ApiInterface apiInterface = RetrofitConfig.getInstanceRetrofit();
        String api = getText(R.string.google_maps_key).toString();
        Call<ResponseRoute> call = apiInterface.request_route(edtawal.getText().toString(),
                edtakhir.getText().toString(), api);

        //call back atau respon dari json
        call.enqueue(new Callback<ResponseRoute>() {
            @Override
            public void onResponse(Call<ResponseRoute> call, Response<ResponseRoute> response) {

                HeroHelper.pre("response route " + response.message());
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    if (status.equals("OK")) {
                        route = response.body().getRoutes();
                        legs = route.get(0).getLegs();

                        distances = legs.get(0).getDistance();
                        durations = legs.get(0).getDuration();

                        textjarak.setText(distances.getText());
                        textwaktu.setText(durations.getText());

                        int value = distances.getValue();
                        //untuk harga
                        double harga = Math.ceil(value / 1000);
                        double total = harga * 1000;
                        textharga.setText("Rp." + HeroHelper.
                                toRupiahFormat2(String.valueOf(total)));
                        //direction
                        direction = new DirectionMapsV2(RouteActivity.this);
                        overPolyline = route.get(0).getOverviewPolyline();
                        String point = overPolyline.getPoints();
                        direction.gambarRoute(mMap, point);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseRoute> call, Throwable t) {
                HeroHelper.pre(" error route" + t.getMessage());
            }
        });
    }

    //buat method add marker
    private void addMarker(Double lat, Double lon) {
        posisiku = new LatLng(lat, lon);
        name_location = convertName(lat, lon);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posisiku, 17));
        mMap.addMarker(new MarkerOptions().position(posisiku).title(name_location));


    }

    private String convertName(Double lat, Double lon) {
        name_location = null;
        Geocoder geocoder = new Geocoder(RouteActivity.this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(lat, lon, 1);
            name_location = list.get(0).getAddressLine(0) + "" + list.get(0).getCountryName();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return name_location;

    }

    private void aksesPanorama() {
        relativemap.setVisibility(View.GONE);
        frame1.setVisibility(View.VISIBLE);
        SupportStreetViewPanoramaFragment panoramaFragment = (SupportStreetViewPanoramaFragment)
                getSupportFragmentManager().findFragmentById(R.id.panorama);
        panoramaFragment.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
                streetViewPanorama.setPosition(lokasisaya);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                        }, 100
                );
            }
            return;
        } else {
            mMap = googleMap;
            akseslokasiku();
        }
    }

    private void akseslokasiku() {
        gps = new GPStrack(this);
        if (gps.canGetLocation() && mMap != null) {
            lat = gps.getLatitude();
            lon = gps.getLongitude();
            nama_lokasi = convertlocation(lat, lon);
            lokasisaya = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(lokasisaya).title(nama_lokasi)).setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasisaya, 15));
            mMap.getUiSettings().isCompassEnabled();
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().isMyLocationButtonEnabled();
            edtawal.setText(nama_lokasi);
        }
    }

    private String convertlocation(double lat, double lon) {
        nama_lokasi = null;

        Geocoder geocoder = new Geocoder(RouteActivity.this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(lat, lon, 1);
            if (list != null && list.size() > 0) {
                nama_lokasi = list.get(0).getAddressLine(0) + " " + list.get(0).getCountryName();
            } else {
                Toast.makeText(this, "Data kosong", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nama_lokasi;
    }
}
