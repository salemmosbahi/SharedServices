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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.mdev.sharedservices.Main;
import it.mdev.sharedservices.R;
import it.mdev.sharedservices.util.Calculator;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.ServerRequest;

/**
 * Created by salem on 28/04/16.
 */
public class CarAdd extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private EditText ModelCA_etxt, DescriptionCA_etxt, PlaceCA_etxt;
    private TextInputLayout ModelCA_input, DescriptionCA_input, PlaceCA_input;
    private Spinner CityDepartCA_sp, CityDestinationCA_sp;
    private TextView DateCA_txt, TimeCA_txt;
    private SwitchCompat GoingComingCA_swt, HighwayCA_swt;
    private Button AddCA_btn, EmptyCA_btn;
    private FloatingActionButton PositionCA_btn;

    private ArrayList<String> CitysList;
    private ArrayAdapter<String> cityAdapter;
    private JSONArray citys = null;
    private int year, month, day, hour, minute;

    public CarAdd() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.car_add, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.car_add));
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        ModelCA_input = (TextInputLayout) v.findViewById(R.id.ModelCA_input);
        ModelCA_etxt = (EditText) v.findViewById(R.id.ModelCA_etxt);
        DescriptionCA_input = (TextInputLayout) v.findViewById(R.id.DescriptionCA_input);
        DescriptionCA_etxt = (EditText) v.findViewById(R.id.DescriptionCA_etxt);
        CityDepartCA_sp = (Spinner) v.findViewById(R.id.CityDepartCA_sp);
        PositionCA_btn = (FloatingActionButton) v.findViewById(R.id.PositionCA_btn);
        CityDestinationCA_sp = (Spinner) v.findViewById(R.id.CityDestinationCA_sp);
        DateCA_txt = (TextView) v.findViewById(R.id.DateCA_txt);
        TimeCA_txt = (TextView) v.findViewById(R.id.TimeCA_txt);
        GoingComingCA_swt = (SwitchCompat) v.findViewById(R.id.GoingComingCA_swt);
        HighwayCA_swt = (SwitchCompat) v.findViewById(R.id.HighwayCA_swt);
        PlaceCA_input = (TextInputLayout) v.findViewById(R.id.PlaceCA_input);
        PlaceCA_etxt = (EditText) v.findViewById(R.id.PlaceCA_etxt);

        AddCA_btn = (Button) v.findViewById(R.id.AddCA_btn);
        EmptyCA_btn = (Button) v.findViewById(R.id.EmptyCA_btn);

        Calendar d = new Calculator().getCurrentTime();
        year = d.get(Calendar.YEAR);
        month  = d.get(Calendar.MONTH);
        day  = d.get(Calendar.DAY_OF_MONTH);
        hour = d.get(Calendar.HOUR_OF_DAY);
        minute = d.get(Calendar.MINUTE);

        DateCA_txt.setText(new StringBuilder().append(year).append("/").append(month + 1).append("/").append(day));
        DateCA_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), R.style.MyMaterialDesignTheme, dateSetListener, year, month, day).show();
            }
        });

        TimeCA_txt.setText(new StringBuilder().append(hour).append(":").append(minute));
        TimeCA_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(getActivity(), R.style.MyMaterialDesignTheme, timeSetListener, hour, minute, true).show();
            }
        });

        ModelCA_etxt.addTextChangedListener(new MyTextWatcher(ModelCA_etxt));
        PlaceCA_etxt.addTextChangedListener(new MyTextWatcher(PlaceCA_etxt));

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
        CityDepartCA_sp.setAdapter(cityAdapter);
        CityDepartCA_sp.setSelection(cityAdapter.getPosition(pref.getString(conf.tag_city, "")));
        CityDestinationCA_sp.setAdapter(cityAdapter);

        PositionCA_btn.setOnClickListener(new View.OnClickListener() {
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

        EmptyCA_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                emptyForm();
            }
        });

        AddCA_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (conf.NetworkIsAvailable(getActivity())) {
                    addForm();
                } else {
                    Toast.makeText(getActivity(),R.string.networkunvalid,Toast.LENGTH_SHORT).show();
                }
            }
        });

        GoingComingCA_swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    GoingComingCA_swt.setText(R.string.going_coming);
                } else {
                    GoingComingCA_swt.setText(R.string.going);
                }
            }
        });

        HighwayCA_swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    HighwayCA_swt.setText(R.string.highway);
                } else {
                    HighwayCA_swt.setText(R.string.highway_not);
                }
            }
        });

        return v;
    }

    private void emptyForm() {
        ModelCA_etxt.setText("");
        DescriptionCA_etxt.setText("");
        PlaceCA_etxt.setText("");
        CityDepartCA_sp.setSelection(cityAdapter.getPosition(pref.getString(conf.tag_city, "")));
        CityDestinationCA_sp.setSelection(cityAdapter.getPosition(pref.getString(conf.tag_city, "")) + 1);
    }

    private void addForm() {
        if (!validateModel()) { return; }
        if (!validatePlace()) { return; }
        if (!validateCityDepart()) { return; }
        if (!validateCityDestination()) { return; }

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_model, ModelCA_etxt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_description, DescriptionCA_etxt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_country, pref.getString(conf.tag_country, "")));
        params.add(new BasicNameValuePair(conf.tag_depart, CityDepartCA_sp.getSelectedItem().toString()));
        params.add(new BasicNameValuePair(conf.tag_destination, CityDestinationCA_sp.getSelectedItem().toString()));
        if (pref.getString(conf.tag_latitude, "") != "") {
            params.add(new BasicNameValuePair(conf.tag_latitude, pref.getString(conf.tag_latitude, "")));
            params.add(new BasicNameValuePair(conf.tag_longitude, pref.getString(conf.tag_longitude, "")));
        } else {
            params.add(new BasicNameValuePair(conf.tag_latitude, ""));
            params.add(new BasicNameValuePair(conf.tag_longitude, ""));
        }
        params.add(new BasicNameValuePair(conf.tag_date, DateCA_txt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_time, TimeCA_txt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_goingComing, GoingComingCA_swt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_highway, HighwayCA_swt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_place, PlaceCA_etxt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
        params.add(new BasicNameValuePair(conf.tag_username, pref.getString(conf.tag_username, "")));
        JSONObject json = sr.getJson(conf.url_addCar, params);
        if (json != null) {
            try {
                String response = json.getString(conf.response);
                if(json.getBoolean(conf.res)) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.container_body, new CarSearch());
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

    private boolean validateCityDepart() {
        if(CityDepartCA_sp.getSelectedItem() != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean validateCityDestination() {
        if(CityDestinationCA_sp.getSelectedItem() != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean validatePlace() {
        if(PlaceCA_etxt.getText().toString().trim().isEmpty()) {
            PlaceCA_input.setError(getString(R.string.place_err));
            requestFocus(PlaceCA_etxt);
            return false;
        } else {
            if ((Integer.parseInt(PlaceCA_etxt.getText().toString()) < 1) || (Integer.parseInt(PlaceCA_etxt.getText().toString()) > 8)) {
                PlaceCA_input.setError(getString(R.string.place_err));
                requestFocus(PlaceCA_etxt);
                return false;
            } else {
                PlaceCA_input.setErrorEnabled(false);
            }
        }
        return true;
    }

    private boolean validateModel() {
        if(ModelCA_etxt.getText().toString().trim().isEmpty()) {
            ModelCA_input.setError(getString(R.string.model_err));
            requestFocus(ModelCA_etxt);
            return false;
        } else {
            ModelCA_input.setErrorEnabled(false);
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
                case R.id.ModelCA_etxt:
                    validateModel();
                    break;
                case R.id.PlaceCA_etxt:
                    validatePlace();
                    break;
            }
        }
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            DateCA_txt.setText(new StringBuilder().append(year).append("/").append(month + 1).append("/").append(day));
            new TimePickerDialog(getActivity(), R.style.MyMaterialDesignTheme, timeSetListener, hour, minute, true).show();
        }
    };

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int min) {
            hour = hourOfDay;
            minute = min;
            TimeCA_txt.setText(new StringBuilder().append(hour).append(":").append(minute));
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
        ft.replace(R.id.container_body, new Car());
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
