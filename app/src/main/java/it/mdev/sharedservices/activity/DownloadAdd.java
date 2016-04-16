package it.mdev.sharedservices.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.ServerRequest;

/**
 * Created by salem on 15/04/16.
 */
public class DownloadAdd extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private EditText Name_etxt, Size_etxt;
    private TextInputLayout Name_input, Size_input;
    private Spinner City_sp;
    private Button Add_btn, Empty_btn;

    private ArrayList<String> CitysList;
    private ArrayAdapter<String> cityAdapter;
    private JSONArray citys = null;

    public DownloadAdd() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.download_add, container, false);
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        Name_input = (TextInputLayout) v.findViewById(R.id.Name_input);
        Name_etxt = (EditText) v.findViewById(R.id.Name_etxt);
        Size_input = (TextInputLayout) v.findViewById(R.id.Size_input);
        Size_etxt = (EditText) v.findViewById(R.id.Size_etxt);
        City_sp = (Spinner) v.findViewById(R.id.City_sp);
        Add_btn = (Button) v.findViewById(R.id.Add_btn);
        Empty_btn = (Button) v.findViewById(R.id.Empty_btn);

        CitysList = new ArrayList<String>();
        List<NameValuePair> cityParams = new ArrayList<NameValuePair>();
        cityParams.add(new BasicNameValuePair(conf.tag_name, pref.getString(conf.tag_country, "")));
        JSONObject jsonx = sr.getJson(conf.url_getAllCity, cityParams);
        if (jsonx != null) {
            try {
                if (jsonx.getBoolean(conf.res)) {
                    citys = jsonx.getJSONArray("data");
                    for (int i = 0; i < citys.length(); i++) {
                        JSONObject t = citys.getJSONObject(i);
                        CitysList.add(t.getString(conf.tag_name));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cityAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,CitysList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        City_sp.setAdapter(cityAdapter);
        City_sp.setSelection(cityAdapter.getPosition(pref.getString(conf.tag_city, "")));

        Empty_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                emptyForm();
            }
        });

        Add_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (conf.NetworkIsAvailable(getActivity())) {
                    addForm();
                } else {
                    Toast.makeText(getActivity(),R.string.networkunvalid,Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    private void emptyForm() {
        Name_etxt.setText("");
        Size_etxt.setText("");
        City_sp.setSelection(cityAdapter.getPosition(pref.getString(conf.tag_city, "")));
    }

    private void addForm() {
        if (!validateName()) { return; }
        if (!validateSize()) { return; }

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_name, Name_etxt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_size, Size_etxt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
        params.add(new BasicNameValuePair(conf.tag_username, pref.getString(conf.tag_username, "")));
        params.add(new BasicNameValuePair(conf.tag_city, City_sp.getSelectedItem().toString()));
        params.add(new BasicNameValuePair(conf.tag_country, pref.getString(conf.tag_country, "")));
        JSONObject json = sr.getJson(conf.url_addDownload, params);
        if (json != null){
            try{
                String response = json.getString(conf.response);
                if(json.getBoolean(conf.res)) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_body, new DownloadSearch());
                    ft.addToBackStack(null);
                    ft.commit();
                    ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.download_search));
                }
                Toast.makeText(getActivity(),response,Toast.LENGTH_SHORT).show();
            }catch(JSONException e){
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateSize() {
        if(Size_etxt.getText().toString().trim().isEmpty()) {
            Size_input.setError(getString(R.string.size_err));
            requestFocus(Size_etxt);
            return false;
        } else {
            if (Integer.parseInt(Size_etxt.getText().toString()) <= 2) {
                Size_input.setError(getString(R.string.size_err));
                requestFocus(Size_etxt);
                return false;
            } else {
                Size_input.setErrorEnabled(false);
            }
        }
        return true;
    }

    private boolean validateName() {
        if(Name_etxt.getText().toString().trim().isEmpty()) {
            Name_input.setError(getString(R.string.name_err));
            requestFocus(Name_etxt);
            return false;
        } else {
            Name_input.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
