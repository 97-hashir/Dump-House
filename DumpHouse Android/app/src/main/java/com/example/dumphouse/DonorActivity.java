package com.example.dumphouse;

import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DonorActivity extends FragmentActivity implements OnMapReadyCallback {
    private Bitmap bmp;
    private ProgressDialog simpleWaitDialog;
    private GoogleMap mMap;
    private MarkerOptions options = new MarkerOptions();

    private ArrayList<Donation> donations = new ArrayList<>();

    boolean flag= false;
    Button okbtn;
    TextView title;
    TextView desc;
    FirebaseDatabase database;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Button donate =  findViewById(R.id.button);

        database = FirebaseDatabase.getInstance();

        fab = findViewById(R.id.fab);

        // Read from the database
        DatabaseReference myRef = database.getReference("donations");

        // Donations arraylist fill with database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            double [] latlongs = new double[2];

            int i = 0;

            @Override
            public void onDataChange(DataSnapshot data ) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                //database
                for (DataSnapshot dataSnapshot: data.getChildren()){
                    // make object for arraylist
                    Donation donation = new Donation();

                // donations
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

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
                // Add in arraylist
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DonorActivity.this, Help.class);
                startActivity(intent);

            //new 
            }
        });



        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_window, null);

                Button okbtn =  popupView.findViewById(R.id.okbtn);
                final TextView title =  popupView.findViewById(R.id.title);
                final TextView desc =  popupView.findViewById(R.id.desc);
                final TextView phone =  popupView.findViewById(R.id.phone);


                // create the popup window
                int width = 600;
                int height = 600;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // draw shadow
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popupWindow.setElevation(20);
                }
                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                okbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Write a message to the database
                        DatabaseReference myRef = database.getReference("donations");
                        String key = myRef.push().getKey();


                        Donation post = new Donation(new LatLng(mMap.getCameraPosition().target.latitude,mMap.getCameraPosition().target.longitude), title.getText().toString(), desc.getText().toString(), phone.getText().toString());




                        Map<String, Object> postValues = post.toMap();

                        Map<String, Object> childUpdates = new HashMap<>();

                        childUpdates.put(key, postValues);

                        myRef.updateChildren(childUpdates);

                        donations.add(post);

                        drawMarkers();

                        popupWindow.dismiss();

                    }


                });

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });

                //pickImageFromGallery();
            }
            private void pickImageFromGallery() {
                Intent GalleryIntent = new Intent();
                GalleryIntent.setType("image/*");
                GalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(GalleryIntent,
                        "select image"), 1); //qrmuqwvd
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Draws markers on the map
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
        //       PicassoMarker marker = new PicassoMarker(mymarker);
        //        Picasso.with(DonorActivity.this).load("https://res.cloudinary.com/affiliatenetwork/image/upload/v1583020756/bmp_qynush.bmp").into(marker);
        //       Picasso.with(DonorActivity.this).load("https://res.cloudinary.com/affiliatenetwork/image/upload/v1583020970/w3c_home_w7sqxf.bmp").into(marker);

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

        // Maps variables for database
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
