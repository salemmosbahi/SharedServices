package it.mdev.sharedservices.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
 * Created by salem on 28/04/16.
 */
public class CarAdd extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private EditText Model_etxt, Description_etxt, Place_etxt;
    private TextInputLayout Model_input, Description_input, Place_input;
    private Spinner CityDepart_sp, CityDestination_sp;
    private TextView Date_txt, Time_txt;
    private SwitchCompat GoingComing_swt, Highway_swt;
    private Button Add_btn, Empty_btn;

    private ArrayList<String> CitysList;
    private ArrayAdapter<String> cityAdapter;
    private JSONArray citys = null;

    public CarAdd() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.car_add, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.car_add));
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        Model_input = (TextInputLayout) v.findViewById(R.id.Model_input);
        Model_etxt = (EditText) v.findViewById(R.id.Model_etxt);
        Description_input = (TextInputLayout) v.findViewById(R.id.Description_input);
        Description_etxt = (EditText) v.findViewById(R.id.Description_etxt);
        CityDepart_sp = (Spinner) v.findViewById(R.id.CityDepart_sp);
        CityDestination_sp = (Spinner) v.findViewById(R.id.CityDestination_sp);
        Date_txt = (TextView) v.findViewById(R.id.Date_txt);
        Time_txt = (TextView) v.findViewById(R.id.Time_txt);
        GoingComing_swt = (SwitchCompat) v.findViewById(R.id.GoingComing_swt);
        Highway_swt = (SwitchCompat) v.findViewById(R.id.Highway_swt);
        Place_input = (TextInputLayout) v.findViewById(R.id.Place_input);
        Place_etxt = (EditText) v.findViewById(R.id.Place_etxt);

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
        } else {
            Add_btn.setEnabled(false);
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
        cityAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,CitysList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CityDepart_sp.setAdapter(cityAdapter);
        CityDepart_sp.setSelection(cityAdapter.getPosition(pref.getString(conf.tag_city, "")));
        CityDestination_sp.setAdapter(cityAdapter);
        CityDestination_sp.setSelection(cityAdapter.getPosition(pref.getString(conf.tag_city, "")) + 1);

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

        GoingComing_swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    GoingComing_swt.setText(R.string.going_coming);
                } else {
                    GoingComing_swt.setText(R.string.going);
                }
            }
        });

        Highway_swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Highway_swt.setText(R.string.highway);
                } else {
                    Highway_swt.setText(R.string.highway_not);
                }
            }
        });

        return v;
    }

    private void emptyForm() {
        Model_etxt.setText("");
        Description_etxt.setText("");
        Place_etxt.setText("");
        CityDepart_sp.setSelection(cityAdapter.getPosition(pref.getString(conf.tag_city, "")));
        CityDestination_sp.setSelection(cityAdapter.getPosition(pref.getString(conf.tag_city, "")) + 1);
    }

    private void addForm() {
        if (!validateModel()) { return; }
        if (!validatePlace()) { return; }

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_model, Model_etxt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_description, Description_etxt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_country, pref.getString(conf.tag_country, "")));
        params.add(new BasicNameValuePair(conf.tag_depart, CityDepart_sp.getSelectedItem().toString()));
        params.add(new BasicNameValuePair(conf.tag_destination, CityDestination_sp.getSelectedItem().toString()));
        params.add(new BasicNameValuePair(conf.tag_date, Date_txt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_time, Time_txt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_goingComing, GoingComing_swt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_highway, Highway_swt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_place, Place_etxt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
        params.add(new BasicNameValuePair(conf.tag_username, pref.getString(conf.tag_username, "")));
        JSONObject json = sr.getJson(conf.url_addCar, params);
        if (json != null) {
            try{
                String response = json.getString(conf.response);
                if(json.getBoolean(conf.res)) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.container_body, new CarSearch());
                    ft.commit();
                }
                Toast.makeText(getActivity(),response,Toast.LENGTH_SHORT).show();
            }catch(JSONException e){
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
    }

    private boolean validatePlace() {
        if(Place_etxt.getText().toString().trim().isEmpty()) {
            Place_input.setError(getString(R.string.place_err));
            requestFocus(Place_etxt);
            return false;
        } else {
            if ((Integer.parseInt(Place_etxt.getText().toString()) <= 1) && (Integer.parseInt(Place_etxt.getText().toString()) >= 8)) {
                Place_input.setError(getString(R.string.place_err));
                requestFocus(Place_etxt);
                return false;
            } else {
                Place_input.setErrorEnabled(false);
            }
        }
        return true;
    }

    private boolean validateModel() {
        if(Model_etxt.getText().toString().trim().isEmpty()) {
            Model_input.setError(getString(R.string.model_err));
            requestFocus(Model_etxt);
            return false;
        } else {
            Model_input.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.container_body, new Download());
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
