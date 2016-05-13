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
import it.mdev.sharedservices.database.DownloadAdapterList;
import it.mdev.sharedservices.database.DownloadDB;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.ServerRequest;

/**
 * Created by salem on 4/4/16.
 */
public class Download extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private SwipeRefreshLayout Refresh_swipe;
    private ListView lv;
    private FloatingActionButton Add_btn, Search_btn;
    ArrayList<DownloadDB> DownloadDBList;
    JSONArray dataJsonArray = null;

    public Download() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.download, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.download));
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        Refresh_swipe = (SwipeRefreshLayout) v.findViewById(R.id.Refresh_swipe);
        lv = (ListView) v.findViewById(R.id.DownloadList);
        Refresh_swipe.setOnRefreshListener(this);
        Refresh_swipe.post(new Runnable() {
                               public void run() {
                                   getDownloadEnd();
                               }
                           });

        Add_btn = (FloatingActionButton) v.findViewById(R.id.Add_btn);
        Add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.container_body, new DownloadAdd());
                ft.commit();
            }
        });

        Search_btn = (FloatingActionButton) v.findViewById(R.id.Search_btn);
        Search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.container_body, new DownloadSearch());
                ft.commit();
            }
        });

        return  v;
    }

    private void getDownloadEnd() {
        Refresh_swipe.setRefreshing(true);
        if(conf.NetworkIsAvailable(getActivity())){
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(conf.tag_country, pref.getString(conf.tag_country, "")));
            params.add(new BasicNameValuePair(conf.tag_city, pref.getString(conf.tag_city, "")));
            DownloadDBList = new ArrayList<>();
            JSONObject json = sr.getJson(conf.url_getDownloadEnd, params);
            if (json != null) {
                try{
                    if(json.getBoolean(conf.res)) {
                        dataJsonArray = json.getJSONArray(conf.data);
                        for (int i = 0; i < dataJsonArray.length(); i++) {
                            JSONObject c = dataJsonArray.getJSONObject(i);
                            String id = c.getString(conf.tag_id);
                            String picture = c.getString(conf.tag_picture);
                            String name = c.getString(conf.tag_name);
                            int size = c.getInt(conf.tag_size);
                            String date = c.getString(conf.tag_date);
                            String status = c.getString(conf.tag_status);
                            DownloadDB rec = new DownloadDB(id, picture, name, date, status, size);
                            DownloadDBList.add(rec);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                DownloadAdapterList adapter = new DownloadAdapterList(getActivity(), DownloadDBList, Download.this);
                lv.setAdapter(adapter);
            }
            Refresh_swipe.setRefreshing(false);
        }else{
            Refresh_swipe.setRefreshing(false);
            Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() { getDownloadEnd(); }

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
