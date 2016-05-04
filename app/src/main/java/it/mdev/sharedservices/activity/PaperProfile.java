package it.mdev.sharedservices.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
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
import it.mdev.sharedservices.database.PaperIndexAdapterList;
import it.mdev.sharedservices.database.PaperIndexDB;
import it.mdev.sharedservices.database.UserCopyAdapterList;
import it.mdev.sharedservices.database.UserCopyDB;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.ServerRequest;

/**
 * Created by salem on 24/04/16.
 */
public class PaperProfile extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private ArrayList<PaperIndexDB> IndexList;
    private JSONArray index = null, dataJsonArray = null;
    private TextView Name_txt, Place_txt;
    private ListView Index_lv;
    private String idService, name, place;

    public PaperProfile() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.paper_profile, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.paper_profile));
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        idService = getArguments().getString(conf.tag_id);

        Name_txt = (TextView) v.findViewById(R.id.Name_txt);
        Place_txt = (TextView) v.findViewById(R.id.Place_txt);
        Index_lv = (ListView) v.findViewById(R.id.Index_lv);

        if(conf.NetworkIsAvailable(getActivity())){
            getProfile();
        }else{
            Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
        }

        Name_txt.setText(name);
        Place_txt.setText(place);

        return v;
    }

    private void getProfile() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_id, idService));
        IndexList = new ArrayList<>();
        JSONObject json = sr.getJson(conf.url_getPaperProfile, params);
        if (json != null) {
            try {
                if(json.getBoolean(conf.res)) {
                    dataJsonArray = json.getJSONArray(conf.data);
                    JSONObject c = dataJsonArray.getJSONObject(0);
                    name = c.getString(conf.tag_name);
                    place = c.getString(conf.tag_place);
                    index = c.getJSONArray(conf.tag_index);
                    if (index.length() != 0) {
                        for (int i = 0; i < index.length(); i++) {
                            JSONObject x = index.getJSONObject(i);
                            String nameIndex = x.getString(conf.tag_name);
                            PaperIndexDB uc = new PaperIndexDB(nameIndex);
                            IndexList.add(uc);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PaperIndexAdapterList adapter = new PaperIndexAdapterList(getActivity(), IndexList, PaperProfile.this);
            Index_lv.setAdapter(adapter);
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.container_body, new Paper());
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
