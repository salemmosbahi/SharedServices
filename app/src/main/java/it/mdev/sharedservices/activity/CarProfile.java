package it.mdev.sharedservices.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.mdev.sharedservices.Main;
import it.mdev.sharedservices.R;
import it.mdev.sharedservices.database.UserCopyAdapterList;
import it.mdev.sharedservices.database.UserCopyDB;
import it.mdev.sharedservices.util.Calculator;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.ServerRequest;
import it.mdev.sharedservices.util.SocketIO;

/**
 * Created by salem on 24/04/16.
 */
public class CarProfile extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    Calculator cal = new Calculator();
    ServerRequest sr = new ServerRequest();
    Socket socket = SocketIO.getInstance();

    private ArrayList<UserCopyDB> UserCopyList;
    private JSONArray usersCopy = null, dataJsonArray = null;
    private TextView Model_txt, Route_txt, Place_txt, Status_txt, Date_txt, DateComplete_txt, GoingComingHighway_txt, Description_txt, UserMain_txt;
    private Button Demand_btn, Complete_btn;
    private ListView UserCopy_lv;
    private String tokenApp, tokenMain, usernameApp, usernameMain, idService, model, depart, destination, latitude, longitude, goingComing, highway, description, date, time, dateComplete, country, status;
    private String activity;
    private int place;
    private MapView mMapView;
    private GoogleMap googleMap;
    private CameraPosition cameraPosition;

    public CarProfile() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.car_profile, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.car_profile));
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        idService = getArguments().getString(conf.tag_id);
        activity = getArguments().getString(conf.tag_activity);
        if (!activity.equals("CarProfile")) {
            SharedPreferences.Editor edit = pref.edit();
            edit.putString(conf.tag_activity, activity);
            edit.commit();
        }
        tokenApp = pref.getString(conf.tag_token, "");
        usernameApp = pref.getString(conf.tag_username, "");

        socket.connect();

        Model_txt = (TextView) v.findViewById(R.id.Model_txt);
        Route_txt = (TextView) v.findViewById(R.id.Route_txt);
        GoingComingHighway_txt = (TextView) v.findViewById(R.id.GoingComingHighway_txt);
        Description_txt = (TextView) v.findViewById(R.id.Description_txt);
        Place_txt = (TextView) v.findViewById(R.id.Place_txt);
        Status_txt = (TextView) v.findViewById(R.id.Status_txt);
        Date_txt = (TextView) v.findViewById(R.id.Date_txt);
        DateComplete_txt = (TextView) v.findViewById(R.id.DateComplete_txt);
        UserMain_txt = (TextView) v.findViewById(R.id.UserMain_txt);
        Demand_btn = (Button) v.findViewById(R.id.Demand_btn);
        Complete_btn = (Button) v.findViewById(R.id.Complete_btn);
        UserCopy_lv = (ListView) v.findViewById(R.id.UserCopy_lv);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        if (conf.NetworkIsAvailable(getActivity())) {
            checkDemandFunc();
            getProfile();
        } else {
            Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
        }

        if (latitude.equals("")) {
            googleMap.clear();
            mMapView.setVisibility(View.GONE);
        } else {
            double x = Double.valueOf(latitude);
            double y = Double.valueOf(longitude);
            MarkerOptions mark = new MarkerOptions().position(new LatLng(x, y)).title("Hello Shared Services Maps");
            mark.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            googleMap.addMarker(mark);
            cameraPosition = new CameraPosition.Builder().target(new LatLng(x, y)).zoom(10).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        Model_txt.setText(model);
        Route_txt.setText(depart + "->" + destination);
        GoingComingHighway_txt.setText(goingComing + ", " + highway);
        Description_txt.setText(description);
        Place_txt.setText(place + " places.");
        Status_txt.setText(status);
        Date_txt.setText("Created in " + date + " " + time);
        DateComplete_txt.setText("Completed in " + dateComplete);
        UserMain_txt.setText("Main: " + usernameMain);

        if (tokenApp.equals(tokenMain)) {
            Complete_btn.setVisibility(View.VISIBLE);
        } else {
            Complete_btn.setVisibility(View.GONE);
        }
        if (status.equals("complete")) {
            Demand_btn.setVisibility(View.GONE);
            Complete_btn.setVisibility(View.GONE);
            DateComplete_txt.setVisibility(View.VISIBLE);
        }

        Demand_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addDemandFunc();
            }
        });

        Complete_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                completeCarFunc();
            }
        });

        return v;
    }

    private void completeCarFunc() {
        JSONObject jx = new JSONObject();
        try {
            jx.put(conf.tag_type, "nottoken");
            socket.emit(conf.io_count, jx);
        } catch (JSONException e) { }

        List<NameValuePair> paramx = new ArrayList<NameValuePair>();
        paramx.add(new BasicNameValuePair(conf.tag_id, idService));
        paramx.add(new BasicNameValuePair(conf.tag_token, tokenApp));
        JSONObject jsonx = sr.getJson(conf.url_completeCar, paramx);
        if (jsonx != null) {
            try {
                Toast.makeText(getActivity(), jsonx.getString(conf.response), Toast.LENGTH_LONG).show();
                if (jsonx.getBoolean(conf.res)) {
                    dateComplete = jsonx.getString(conf.tag_dateComplete);
                    Complete_btn.setVisibility(View.GONE);
                    DateComplete_txt.setText("Completed in " + dateComplete);
                    Status_txt.setText("complete");
                    DateComplete_txt.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addDemandFunc() {
        JSONObject jx = new JSONObject();
        try {
            jx.put(conf.tag_type, "token");
            jx.put(conf.tag_tokenMain, tokenMain);
            jx.put(conf.tag_token, tokenApp);
            socket.emit(conf.io_count, jx);
        } catch (JSONException e) { }

        List<NameValuePair> paramx = new ArrayList<NameValuePair>();
        paramx.add(new BasicNameValuePair(conf.tag_service, getString(R.string.car)));
        paramx.add(new BasicNameValuePair(conf.tag_id, idService));
        paramx.add(new BasicNameValuePair(conf.tag_name, model + ": " + depart + "->" + destination));
        paramx.add(new BasicNameValuePair(conf.tag_token, tokenApp));
        paramx.add(new BasicNameValuePair(conf.tag_username, usernameApp));
        paramx.add(new BasicNameValuePair(conf.tag_date, pref.getString(conf.tag_dateN, "")));
        paramx.add(new BasicNameValuePair(conf.tag_usernameMain, usernameMain));
        JSONObject jsonx = sr.getJson(conf.url_addDemand, paramx);
        if (jsonx != null) {
            try {
                Toast.makeText(getActivity(), jsonx.getString(conf.response), Toast.LENGTH_LONG).show();
                if (jsonx.getBoolean(conf.res)) {
                    Demand_btn.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkDemandFunc() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_id, idService));
        params.add(new BasicNameValuePair(conf.tag_token, tokenApp));
        JSONObject json = sr.getJson(conf.url_checkDemand, params);
        if (json != null) {
            try {
                if (json.getBoolean(conf.res)) {
                    Demand_btn.setVisibility(View.GONE);
                } else {
                    Demand_btn.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
    }

    private void getProfile() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_id, idService));
        UserCopyList = new ArrayList<>();
        JSONObject json = sr.getJson(conf.url_getCarProfile, params);
        if (json != null) {
            try {
                if(json.getBoolean(conf.res)) {
                    dataJsonArray = json.getJSONArray(conf.data);
                    JSONObject c = dataJsonArray.getJSONObject(0);
                    model = c.getString(conf.tag_model);
                    depart = c.getString(conf.tag_depart);
                    destination = c.getString(conf.tag_destination);
                    latitude = c.getString(conf.tag_latitude);
                    longitude = c.getString(conf.tag_longitude);
                    date = c.getString(conf.tag_date);
                    time = c.getString(conf.tag_time);
                    dateComplete = c.getString(conf.tag_dateComplete);
                    goingComing = c.getString(conf.tag_goingComing);
                    highway = c.getString(conf.tag_highway);
                    place = c.getInt(conf.tag_place);
                    description = c.getString(conf.tag_description);
                    status = c.getString(conf.tag_status);
                    tokenMain = c.getString(conf.tag_token);
                    usernameMain = c.getString(conf.tag_username);
                    country = c.getString(conf.tag_country);
                    usersCopy = c.getJSONArray(conf.tag_usersCopy);
                    if (usersCopy.length() != 0) {
                        for (int i = 0; i < usersCopy.length(); i++) {
                            JSONObject x = usersCopy.getJSONObject(i);
                            String token = x.getString(conf.tag_token);
                            String username = x.getString(conf.tag_username);
                            String age = x.getString(conf.tag_age);
                            UserCopyDB uc = new UserCopyDB(token, username, age, "Car", idService);
                            UserCopyList.add(uc);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            UserCopyAdapterList adapter = new UserCopyAdapterList(getActivity(), UserCopyList, CarProfile.this);
            UserCopy_lv.setAdapter(adapter);
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
    }

    private void goFragment(String str) {
        if (str.equals("BoxNotify")) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            ft.replace(R.id.container_body, new BoxNotify());
            ft.commit();
        } else if (str.equals("Car")) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            ft.replace(R.id.container_body, new Car());
            ft.commit();
        } else if (str.equals("CarSearch")) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            ft.replace(R.id.container_body, new CarSearch());
            ft.commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (activity.equals("BoxNotify")) {
            goFragment("BoxNotify");
        } else if (activity.equals("Car")) {
            goFragment("Car");
        } else if (activity.equals("CarSearch")) {
            goFragment("CarSearch");
        } else if (activity.equals("CarProfile")) {
            goFragment(pref.getString(conf.tag_activity, ""));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
