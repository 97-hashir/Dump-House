package com.example.dumphouse.ui.home_recipient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.dumphouse.BuildConfig;
import com.example.dumphouse.R;
import com.google.android.gms.maps.OnMapReadyCallback;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeRecipientFragment extends Fragment  implements OnMapReadyCallback {

    private HomeRecipientViewModel homeViewModel;
    private GoogleMap mMap;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<Donation> donations = new ArrayList<>();
    View root;
    Button contact;
    String phone;
    FirebaseDatabase database;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =  ViewModelProviders.of(this).get(HomeRecipientViewModel.class);
         root = inflater.inflate(R.layout.fragment_home, container, false);




        contact = root.findViewById(R.id.button);
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
                     Donation donation = new  Donation();

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
                if ( ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                startActivity(intent);

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)    getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        return root;
    }

    private void drawMarkers(){


        mMap.clear();
        for (Donation donation : donations) {
            options.position(donation.latLng);
            options.title(donation.title);
            options.snippet(donation.desc +" #"+ donation.phone);
            Log.v("MyTagGoesHere",donation.title);
            int id =  this.getResources().getIdentifier(donation.title.toLowerCase() , "drawable",  BuildConfig.APPLICATION_ID);

            options.icon(BitmapDescriptorFactory.fromResource(id));
//            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.computer));
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
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg0)
            {
                NavigationView nav = (NavigationView) root.findViewById(R.id.nav_view);
                nav.setVisibility(View.GONE);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                contact.setEnabled(true);
                String id =   marker.getSnippet();
//                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.book2));
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
