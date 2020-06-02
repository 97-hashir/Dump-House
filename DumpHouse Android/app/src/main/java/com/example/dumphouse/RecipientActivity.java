package com.example.dumphouse;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.cloudinary.android.MediaManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RecipientActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<Donation> donations = new ArrayList<>();

    Button contact;
    String phone;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_recipient);

        contact = findViewById(R.id.button);
        database = FirebaseDatabase.getInstance();

        // Read from the database
        DatabaseReference myRef = database.getReference("donations");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            double[] latlongs = new double[2];
            int i = 0;

            @Override
            public void onDataChange(DataSnapshot data) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot dataSnapshot : data.getChildren()) {
                    Donation donation = new Donation();

                    // donations
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        // desc
                        if (postSnapshot.getKey().equals("description")) {
                            donation.desc = postSnapshot.getValue(String.class);
                        }
                        // point
                        if (postSnapshot.getChildrenCount() > 1) {
                            for (DataSnapshot post : postSnapshot.getChildren()) {
                                latlongs[i] = post.getValue(double.class);
                                i++;
                            }
                            i = 0;
                            donation.latLng = new LatLng(latlongs[0], latlongs[1]);
                        }

                        // title
                        if (postSnapshot.getKey().equals("title")) {
                            donation.title = postSnapshot.getValue(String.class);
                        }

                        // phone
                        if (postSnapshot.getKey().equals("phone")) {
                            donation.phone = postSnapshot.getValue(String.class);
                        }

                    }
                    donations.add(donation);
                }
                drawMarkers();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("Error", "Failed to read value.", error.toException());
            }

        });


        contact.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));

               // startActivity(intent);

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void drawMarkers(){


        mMap.clear();
        for (Donation donation : donations) {
            options.position(donation.latLng);
            options.title(donation.title);
            options.snippet(donation.desc +" #"+ donation.phone);
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            Marker marker = mMap.addMarker(options);

        }
        try{
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(donations.get(donations.size()-1).latLng, 17));
        }
        catch(Exception e){
            Log.e("Error","Error signing up");
        }
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
        float zoomLevel = 17.0f; //This goes up to 21

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                contact.setEnabled(true);
                String id =   marker.getSnippet();

                String[] parts = id.split("#");
                phone = parts[1];
                return false;
            }
        });

    }


    public class Donation{
        LatLng latLng;
        String title;
        String desc;
        String phone;

        public Donation(){

        }
        public Donation(LatLng latLng, String title,String desc, String phone ){
            this.latLng = latLng;
            this.title = title;
            this.desc = desc;
            this.phone = phone;
        }

        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();

            HashMap<String, Object> point = new HashMap<>();
            point.put("v",latLng.latitude);
            point.put("v1",latLng.longitude);
            result.put("description", desc);
            result.put("title", title);
            result.put("point",point);
            result.put("phone",phone);
            return result;
        }
    }
}
