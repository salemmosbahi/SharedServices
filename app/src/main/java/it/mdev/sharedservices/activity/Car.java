package it.mdev.sharedservices.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.mdev.sharedservices.Main;
import it.mdev.sharedservices.R;
import it.mdev.sharedservices.database.CarAdapterList;
import it.mdev.sharedservices.database.CarDB;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.ServerRequest;

/**
 * Created by salem on 4/4/16.
 */
public class Car extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private SwipeRefreshLayout CarRefresh_swipe;
    private ListView lvCar;
    private FloatingActionButton AddCar_btn, SearchCar_btn;
    ArrayList<CarDB> CarDBList;
    JSONArray dataJsonArray = null;

    public Car() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.car, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.car));
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        CarRefresh_swipe = (SwipeRefreshLayout) v.findViewById(R.id.CarRefresh_swipe);
        lvCar = (ListView) v.findViewById(R.id.CarList);
        CarRefresh_swipe.setOnRefreshListener(this);
        CarRefresh_swipe.post(new Runnable() {
            public void run() {
                getCarEnd();
            }
        });

        AddCar_btn = (FloatingActionButton) v.findViewById(R.id.AddCar_btn);
        AddCar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.container_body, new CarAdd());
                ft.commit();
            }
        });

        SearchCar_btn = (FloatingActionButton) v.findViewById(R.id.SearchCar_btn);
        SearchCar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.container_body, new CarSearch());
                ft.commit();
            }
        });

        return  v;
    }

    private void getCarEnd() {
        CarRefresh_swipe.setRefreshing(true);
        if(conf.NetworkIsAvailable(getActivity())){
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(conf.tag_country, pref.getString(conf.tag_country, "")));
            params.add(new BasicNameValuePair(conf.tag_depart, pref.getString(conf.tag_city, "")));
            CarDBList = new ArrayList<>();
            JSONObject json = sr.getJson(conf.url_getCarEnd, params);
            if (json != null) {
                try{
                    if(json.getBoolean(conf.res)) {
                        dataJsonArray = json.getJSONArray(conf.data);
                        for (int i = 0; i < dataJsonArray.length(); i++) {
                            JSONObject c = dataJsonArray.getJSONObject(i);
                            String id = c.getString(conf.tag_id);
                            String model = c.getString(conf.tag_model);
                            String depart = c.getString(conf.tag_depart);
                            String destination = c.getString(conf.tag_destination);
                            String date = c.getString(conf.tag_date);
                            CarDB cx = new CarDB(id, model, depart, destination, date);
                            CarDBList.add(cx);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                CarAdapterList adapter = new CarAdapterList(getActivity(), CarDBList, Car.this);
                lvCar.setAdapter(adapter);
            }
            CarRefresh_swipe.setRefreshing(false);
        }else{
            CarRefresh_swipe.setRefreshing(false);
            Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() { getCarEnd(); }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.container_body, new Home());
        ft.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
