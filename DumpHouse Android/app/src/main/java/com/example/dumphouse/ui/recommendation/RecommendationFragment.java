package com.example.dumphouse.ui.recommendation;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dumphouse.BuildConfig;
import com.example.dumphouse.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecommendationFragment extends Fragment {

    private RecommendationViewModel recommendationViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private ArrayList<Donation> donations = new ArrayList<>();

    FirebaseDatabase database;
    TextView title;
    TextView description;
    TextView distance;
    TextView time;
    ImageView profile;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recommendationViewModel =
                ViewModelProviders.of(this).get(RecommendationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_recommendation, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        title =  root.findViewById(R.id.title);
        description =  root.findViewById(R.id.description);
        distance =  root.findViewById(R.id.distance);
        time =  root.findViewById(R.id.time);
        profile = root.findViewById(R.id.profile);

        recommendationViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        database = FirebaseDatabase.getInstance();

        final RequestQueue queue = Volley.newRequestQueue(getContext());
        final String url ="https://dumphouse.herokuapp.com/";

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



                fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(final Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {

                                    String data = "";

                                    // Request a string response from the provided URL.
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {

                                                    textView.setText("The API has calculated the nearest location!");

                                                    JSONObject jObj = null;
                                                    String index = "";
                                                    String distanceVal = "";
                                                    String timeVal = "";
                                                    try {
                                                        jObj = new JSONObject(response);
                                                        index  = jObj.getString("index");
                                                        distanceVal  = jObj.getString("distance");
                                                        timeVal  = jObj.getString("duration");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    Donation donation = donations.get(Integer.parseInt(index));

                                                    title.setText(donation.title);
                                                    description.setText(donation.desc + "\n" + donation.phone );
                                                    distance.setText("Total distance from your location: " +distanceVal+"km");
                                                    time.setText("Total duration from your location: " +timeVal);

                                                    int id =  getResources().getIdentifier(donation.title.toLowerCase() , "drawable",  BuildConfig.APPLICATION_ID);

                                        profile.setImageResource(id);


                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            textView.setText("That didn't work!"+ donationLocations());
                                        }
                                    })

                                    {
                                        @Override
                                        protected Map<String,String> getParams(){
                                            String origin = location.getLatitude()+","+location.getLongitude();

                                            Map<String,String> params = new HashMap<String, String>();
                                            params.put("origins",origin);
                                            params.put("destinations", donationLocations());
//                                    params.put("comment", Uri.encode(comment));


                                            return params;
                                        }

                                        @Override
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            Map<String,String> params = new HashMap<String, String>();
                                            params.put("Content-Type","application/x-www-form-urlencoded");
                                            return params;
                                        }
                                    };
                                    // Add the request to the RequestQueue.
                                    queue.add(stringRequest);

                                }
                            }
                        });

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("Error", "Failed to read value.", error.toException());
            }

        });



        return root;
    }

    private String donationLocations(){
        String data = "";
        int i = 0;
        for ( Donation donation : donations ) {
            if(i+1 <donations.size())
                data += ""+donation.latLng.latitude+","+ donation.latLng.longitude+"]";
            else
                data += ""+donation.latLng.latitude+","+ donation.latLng.longitude+"";
            i++;
        }
        data +="";
        return data;
    }

class Donation{
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