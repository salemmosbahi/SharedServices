package it.mdev.sharedservices.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.mdev.sharedservices.Main;
import it.mdev.sharedservices.R;
import it.mdev.sharedservices.util.Calculator;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.ServerRequest;

/**
 * Created by salem on 01/05/16.
 */
public class EventAdd extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private EditText Name_etxt, Description_etxt;
    private TextInputLayout Name_input, Description_input;
    private Spinner City_sp;
    private TextView Date_txt, Time_txt;
    private Button Add_btn, Empty_btn;
    private FloatingActionButton Position_btn;

    private ArrayList<String> CitysList;
    private ArrayAdapter<String> cityAdapter;
    private JSONArray citys = null;
    private int year, month, day, hour, minute;

    public EventAdd() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_add, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.event_add));
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        Name_input = (TextInputLayout) v.findViewById(R.id.Name_input);
        Name_etxt = (EditText) v.findViewById(R.id.Name_etxt);
        Description_input = (TextInputLayout) v.findViewById(R.id.Description_input);
        Description_etxt = (EditText) v.findViewById(R.id.Description_etxt);
        City_sp = (Spinner) v.findViewById(R.id.City_sp);
        Position_btn = (FloatingActionButton) v.findViewById(R.id.Position_btn);
        Date_txt = (TextView) v.findViewById(R.id.Date_txt);
        Time_txt = (TextView) v.findViewById(R.id.Time_txt);

        Add_btn = (Button) v.findViewById(R.id.Add_btn);
        Empty_btn = (Button) v.findViewById(R.id.Empty_btn);

        Calendar d = new Calculator().getCurrentTime();
        year = d.get(Calendar.YEAR);
        month  = d.get(Calendar.MONTH);
        day  = d.get(Calendar.DAY_OF_MONTH);
        hour = d.get(Calendar.HOUR_OF_DAY);
        minute = d.get(Calendar.MINUTE);

        Date_txt.setText(new StringBuilder().append(year).append("/").append(month + 1).append("/").append(day));
        Date_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), R.style.MyMaterialDesignTheme, dateSetListener, year, month, day).show();
            }
        });

        Time_txt.setText(new StringBuilder().append(hour).append(":").append(minute));
        Time_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(getActivity(), R.style.MyMaterialDesignTheme, timeSetListener, hour, minute, true).show();
            }
        });

        Name_etxt.addTextChangedListener(new MyTextWatcher(Name_etxt));

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
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
        cityAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,CitysList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        City_sp.setAdapter(cityAdapter);
        City_sp.setSelection(cityAdapter.getPosition(pref.getString(conf.tag_city, "")));

        Position_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor edit = pref.edit();
                edit.putString(conf.tag_latitude, "");
                edit.putString(conf.tag_longitude, "");
                edit.commit();

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.container_body, new PositionMAP());
                ft.commit();
            }
        });

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
        Description_etxt.setText("");
        City_sp.setSelection(cityAdapter.getPosition(pref.getString(conf.tag_city, "")));
    }

    private void addForm() {
        if (!validateName()) { return; }
        if (!validateDescription()) { return; }
        if (!validateCity()) { return; }

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_name, Name_etxt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_description, Description_etxt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_country, pref.getString(conf.tag_country, "")));
        params.add(new BasicNameValuePair(conf.tag_city, City_sp.getSelectedItem().toString()));
        if (pref.getString(conf.tag_latitude, "") != "") {
            params.add(new BasicNameValuePair(conf.tag_latitude, pref.getString(conf.tag_latitude, "")));
            params.add(new BasicNameValuePair(conf.tag_longitude, pref.getString(conf.tag_longitude, "")));
        } else {
            params.add(new BasicNameValuePair(conf.tag_latitude, ""));
            params.add(new BasicNameValuePair(conf.tag_longitude, ""));
        }
        params.add(new BasicNameValuePair(conf.tag_date, Date_txt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_time, Time_txt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
        params.add(new BasicNameValuePair(conf.tag_username, pref.getString(conf.tag_username, "")));
        JSONObject json = sr.getJson(conf.url_addEvent, params);
        if (json != null) {
            try {
                String response = json.getString(conf.response);
                if(json.getBoolean(conf.res)) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.container_body, new EventSearch());
                    ft.commit();
                }
                Toast.makeText(getActivity(),response,Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateCity() {
        if(City_sp.getSelectedItem() != null) {
            return true;
        } else {
            return false;
        }
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

    private boolean validateDescription() {
        if(Description_etxt.getText().toString().trim().isEmpty()) {
            Description_input.setError(getString(R.string.description_err));
            requestFocus(Description_etxt);
            return false;
        } else {
            Description_input.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {
        private View view;
        private MyTextWatcher(View view) { this.view = view; }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.Name_etxt:
                    validateName();
                    break;
                case R.id.Description_etxt:
                    validateDescription();
                    break;
            }
        }
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            Date_txt.setText(new StringBuilder().append(year).append("/").append(month + 1).append("/").append(day));
            new TimePickerDialog(getActivity(), R.style.MyMaterialDesignTheme, timeSetListener, hour, minute, true).show();
        }
    };

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int min) {
            hour = hourOfDay;
            minute = min;
            Time_txt.setText(new StringBuilder().append(hour).append(":").append(minute));
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.container_body, new Event());
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
