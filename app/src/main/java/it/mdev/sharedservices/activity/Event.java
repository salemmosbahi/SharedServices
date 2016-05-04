package it.mdev.sharedservices.activity;

import android.content.Context;
import android.content.SharedPreferences;
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
import it.mdev.sharedservices.database.EventAdapterList;
import it.mdev.sharedservices.database.EventDB;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.ServerRequest;

/**
 * Created by salem on 4/4/16.
 */
public class Event extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private SwipeRefreshLayout EventRefresh_swipe;
    private ListView lv;
    private FloatingActionButton Add_btn, Search_btn;
    ArrayList<EventDB> EventDBList;
    JSONArray dataJsonArray = null;

    public Event() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.event));
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        EventRefresh_swipe = (SwipeRefreshLayout) v.findViewById(R.id.EventRefresh_swipe);
        lv = (ListView) v.findViewById(R.id.EventList);
        EventRefresh_swipe.setOnRefreshListener(this);
        EventRefresh_swipe.post(new Runnable() {
            public void run() {
                getEventEnd();
            }
        });

        Add_btn = (FloatingActionButton) v.findViewById(R.id.Add_btn);
        Add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.container_body, new EventAdd());
                ft.commit();
            }
        });

        Search_btn = (FloatingActionButton) v.findViewById(R.id.Search_btn);
        Search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.container_body, new EventSearch());
                ft.commit();
            }
        });

        return  v;
    }

    private void getEventEnd() {
        EventRefresh_swipe.setRefreshing(true);
        if(conf.NetworkIsAvailable(getActivity())){
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(conf.tag_country, pref.getString(conf.tag_country, "")));
            params.add(new BasicNameValuePair(conf.tag_city, pref.getString(conf.tag_city, "")));
            EventDBList = new ArrayList<>();
            JSONObject json = sr.getJson(conf.url_getEventEnd, params);
            if (json != null) {
                try{
                    if(json.getBoolean(conf.res)) {
                        dataJsonArray = json.getJSONArray(conf.data);
                        for (int i = 0; i < dataJsonArray.length(); i++) {
                            JSONObject c = dataJsonArray.getJSONObject(i);
                            String id = c.getString(conf.tag_id);
                            String name = c.getString(conf.tag_name);
                            String city = c.getString(conf.tag_city);
                            String date = c.getString(conf.tag_date);
                            EventDB cx = new EventDB(id, name, city, date);
                            EventDBList.add(cx);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                EventAdapterList adapter = new EventAdapterList(getActivity(), EventDBList, Event.this);
                lv.setAdapter(adapter);
            }
            EventRefresh_swipe.setRefreshing(false);
        }else{
            EventRefresh_swipe.setRefreshing(false);
            Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() { getEventEnd(); }

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
}
