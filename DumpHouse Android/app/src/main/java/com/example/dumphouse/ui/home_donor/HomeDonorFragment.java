package com.example.dumphouse.ui.home_donor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.dumphouse.BuildConfig;
import com.example.dumphouse.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeDonorFragment extends Fragment  implements OnMapReadyCallback{

    private HomeDonorViewModel slideshowViewModel;
    private Bitmap bmp;
    private ProgressDialog simpleWaitDialog;
    private GoogleMap mMap;
    private MarkerOptions options = new MarkerOptions();
    View root;
    private ArrayList<Donation> donations = new ArrayList<>();

    boolean flag= false;
    Button okbtn;
    TextView title;
    TextView desc;
    FirebaseDatabase database;



    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(HomeDonorViewModel.class);
          root = inflater.inflate(R.layout.fragment_home_donor, container, false);





        Button donate =  root.findViewById(R.id.button);

        database = FirebaseDatabase.getInstance();



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
                     Donation donation = new  Donation();

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





        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                View popupView = inflater.inflate(R.layout.popup_window, null);

                Button okbtn =  popupView.findViewById(R.id.okbtn);
                final TextView title =  popupView.findViewById(R.id.title);
                final TextView desc =  popupView.findViewById(R.id.desc);
                final TextView phone =  popupView.findViewById(R.id.phone);
                Button uploadImageButton =  popupView.findViewById(R.id.button4);

                // create the popup window
                int width = 600;
                int height = 800;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // draw shadow
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popupWindow.setElevation(20);
                }
                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                uploadImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        pickImageFromGallery();


                    }
                });

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


            }

        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)    getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        return root;
    }
    //pickImageFromGallery();
    private void pickImageFromGallery() {

        Intent GalleryIntent = new Intent();
        GalleryIntent.setType("image/*");
        GalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(GalleryIntent,
                "select image"), 1); //qrmuqwvd
    }

    // Draws markers on the map
    private void drawMarkers(){


        mMap.clear();

        for ( Donation donation : donations) {
            options.position(donation.latLng);
            options.title(donation.title);
            options.snippet(donation.desc +" #"+ donation.phone);
            try {
                int id = this.getResources().getIdentifier(donation.title.toLowerCase(), "drawable", BuildConfig.APPLICATION_ID);

                options.icon(BitmapDescriptorFactory.fromResource(id));
            }
            catch(Exception e){
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }
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
        //        Picasso.with(DonorActivity2.this).load("https://res.cloudinary.com/affiliatenetwork/image/upload/v1583020756/bmp_qynush.bmp").into(marker);
        //       Picasso.with(DonorActivity2.this).load("https://res.cloudinary.com/affiliatenetwork/image/upload/v1583020970/w3c_home_w7sqxf.bmp").into(marker);

    }

    // For drawing pictures, currently not using
    public class PicassoMarker implements Target {
        Marker mMarker;

        PicassoMarker(Marker marker) {
            mMarker = marker;
        }

        @Override
        public int hashCode() {
            return mMarker.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof  PicassoMarker) {
                Marker marker = (( PicassoMarker) o).mMarker;
                return mMarker.equals(marker);
            } else {
                return false;
            }
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
            mMarker.setVisible(true);

        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
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
