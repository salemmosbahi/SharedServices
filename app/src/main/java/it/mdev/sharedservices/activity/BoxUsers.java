package it.mdev.sharedservices.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import it.mdev.sharedservices.database.BoxNotifyAdapterList;
import it.mdev.sharedservices.database.BoxNotifyDB;
import it.mdev.sharedservices.database.UserCopyAdapterList;
import it.mdev.sharedservices.database.UserCopyDB;
import it.mdev.sharedservices.util.Calculator;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.ServerRequest;

/**
 * Created by salem on 23/04/16.
 */
public class BoxUsers extends Fragment {
    SharedPreferences pref;
    ServerRequest sr = new ServerRequest();
    Controllers conf = new Controllers();

    private ListView Box_lv;

    private ArrayList<UserCopyDB> BoxUsersDBList;
    private JSONArray dataJsonArray = null;
    private String idService, service;

    public BoxUsers() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.box, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.boxUser));
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        idService = getArguments().getString(conf.tag_idService);
        service = getArguments().getString(conf.tag_service);

        Box_lv = (ListView) v.findViewById(R.id.Box_lv);

        if(conf.NetworkIsAvailable(getActivity())){
            loadFunc();
        }else{
            Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    private void loadFunc() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_id, idService));
        JSONObject json = sr.getJson(conf.url_boxUsersList, params);
        BoxUsersDBList = new ArrayList<>();
        if (json != null) {
            try {
                if (json.getBoolean(conf.res)) {
                    dataJsonArray = json.getJSONArray(conf.data);
                    if (dataJsonArray.length() != 0) {
                        for (int i = 0; i < dataJsonArray.length(); i++) {
                            JSONObject c = dataJsonArray.getJSONObject(i);
                            String token = c.getString(conf.tag_token);
                            String username = c.getString(conf.tag_username);
                            String date = c.getString(conf.tag_date);
                            int[] tab = new Calculator().getAge(date);
                            String age = tab[0] + "years, " + tab[1] + "month, " + tab[2] + "day";
                            UserCopyDB uc = new UserCopyDB(token, username, age, service, idService);
                            BoxUsersDBList.add(uc);
                        }
                    }
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
            UserCopyAdapterList adapter = new UserCopyAdapterList(getActivity(), BoxUsersDBList, BoxUsers.this);
            Box_lv.setAdapter(adapter);
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.container_body, new BoxNotify());
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
