package me.firdaus1453.mapnovemberima;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlacePickerActivity extends AppCompatActivity {

    @BindView(R.id.btnPlace)
    Button btnPlace;
    @BindView(R.id.tvPlacePicker)
    TextView tvPlacePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnPlace)
    public void onViewClicked() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(PlacePickerActivity.this),1);
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode != 0){
            Place place = PlacePicker.getPlace(PlacePickerActivity.this,data);
            String informasi = String.format("Place : %s \n alamat : %s \n latlong : %s \n phone : %s", place.getName(),place.getAddress(),place.getLatLng().latitude+","+place.getLatLng().longitude,place.getPhoneNumber());
            tvPlacePicker.setText(informasi);
        }
    }
}
