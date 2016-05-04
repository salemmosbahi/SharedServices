package it.mdev.sharedservices.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
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
import java.util.Locale;

import it.mdev.sharedservices.Main;
import it.mdev.sharedservices.R;
import it.mdev.sharedservices.database.CarAdapterList;
import it.mdev.sharedservices.database.CarDB;
import it.mdev.sharedservices.database.PaperAdapterList;
import it.mdev.sharedservices.database.PaperDB;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.ServerRequest;

/**
 * Created by salem on 4/4/16.
 */
public class Paper extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private SwipeRefreshLayout PaperRefresh_swipe;
    ArrayList<PaperDB> PaperDBList;
    private ListView lv;
    JSONArray dataJsonArray = null;

    public Paper() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.paper, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.paper));
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        PaperRefresh_swipe = (SwipeRefreshLayout) v.findViewById(R.id.PaperRefresh_swipe);
        lv = (ListView) v.findViewById(R.id.PaperList);
        PaperRefresh_swipe.setOnRefreshListener(this);
        PaperRefresh_swipe.post(new Runnable() {
            public void run() {
                getPaper();
            }
        });

        return  v;
    }

    private void getPaper() {
        PaperRefresh_swipe.setRefreshing(true);
        if(conf.NetworkIsAvailable(getActivity())){
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(conf.tag_country, pref.getString(conf.tag_country, "")));
            PaperDBList = new ArrayList<>();
            JSONObject json = sr.getJson(conf.url_getPaper, params);
            if (json != null) {
                try{
                    if(json.getBoolean(conf.res)) {
                        dataJsonArray = json.getJSONArray(conf.data);
                        for (int i = 0; i < dataJsonArray.length(); i++) {
                            JSONObject c = dataJsonArray.getJSONObject(i);
                            String id = c.getString(conf.tag_id);
                            String name = c.getString(conf.tag_name);
                            String place = c.getString(conf.tag_place);
                            PaperDB cx = new PaperDB(id, name, place);
                            PaperDBList.add(cx);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                PaperAdapterList adapter = new PaperAdapterList(getActivity(), PaperDBList, Paper.this);
                lv.setAdapter(adapter);
            }
            PaperRefresh_swipe.setRefreshing(false);
        }else{
            PaperRefresh_swipe.setRefreshing(false);
            Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() { getPaper(); }

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
