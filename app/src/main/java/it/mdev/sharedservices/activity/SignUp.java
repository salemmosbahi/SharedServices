package it.mdev.sharedservices.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.mdev.sharedservices.Main;
import it.mdev.sharedservices.R;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.Encryption;
import it.mdev.sharedservices.util.ServerRequest;

/**
 * Created by salem on 4/4/16.
 */
public class SignUp extends Fragment {
    SharedPreferences pref;
    ServerRequest sr = new ServerRequest();
    Controllers conf = new Controllers();

    private EditText Fname_etxt, Lname_etxt, Email_etxt, Password_etxt, Phone_etxt;
    private TextView DateN_txt;
    private TextInputLayout Fname_input, Lname_input, Email_input, Password_input, Phone_input;
    private ImageView Picture_iv;
    private Spinner Gender_sp, Country_sp, City_sp;
    private SwitchCompat Driver_swt;
    private Button Login_btn, SignUp_btn;

    private Boolean driver;
    private int year, month, day;
    private static final int SELECT_PICTURE = 1;
    private ArrayList<String> CountrysList, CitysList;
    private ArrayAdapter<String> cityAdapter, countryAdapter;
    private JSONArray countrys = null, citys = null;

    public SignUp() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.signup, container, false);
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        Picture_iv = (ImageView) v.findViewById(R.id.Picture_iv);
        Fname_input = (TextInputLayout) v.findViewById(R.id.Fname_input);
        Fname_etxt = (EditText) v.findViewById(R.id.Fname_etxt);
        Lname_input = (TextInputLayout) v.findViewById(R.id.Lname_input);
        Lname_etxt = (EditText) v.findViewById(R.id.Lname_etxt);
        Gender_sp = (Spinner) v.findViewById(R.id.Gender_sp);
        DateN_txt = (TextView) v.findViewById(R.id.DateN_txt);
        Country_sp = (Spinner) v.findViewById(R.id.Country_sp);
        City_sp = (Spinner) v.findViewById(R.id.City_sp);
        Email_input = (TextInputLayout) v.findViewById(R.id.Email_input);
        Email_etxt = (EditText) v.findViewById(R.id.Email_etxt);
        Password_input = (TextInputLayout) v.findViewById(R.id.Password_input);
        Password_etxt = (EditText) v.findViewById(R.id.Password_etxt);
        Phone_input = (TextInputLayout) v.findViewById(R.id.Phone_input);
        Phone_etxt = (EditText) v.findViewById(R.id.Phone_etxt);
        Driver_swt = (SwitchCompat) v.findViewById(R.id.Driver_swt);
        driver = false;
        Login_btn = (Button) v.findViewById(R.id.Login_btn);
        SignUp_btn = (Button) v.findViewById(R.id.SignUp_btn);

        Fname_etxt.addTextChangedListener(new MyTextWatcher(Fname_etxt));
        Lname_etxt.addTextChangedListener(new MyTextWatcher(Lname_etxt));
        Email_etxt.addTextChangedListener(new MyTextWatcher(Email_etxt));
        Password_etxt.addTextChangedListener(new MyTextWatcher(Password_etxt));

        Picture_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_PICTURE);
            }
        });

        CountrysList = new ArrayList<String>();
        CitysList = new ArrayList<String>();
        List<NameValuePair> countryParams = new ArrayList<NameValuePair>();
        JSONObject json = sr.getJson(conf.url_getAllCountry, countryParams);
        if(json != null){
            try{
                if(json.getBoolean(conf.res)){
                    countrys = json.getJSONArray("data");
                    for (int i=0; i<countrys.length(); i++) {
                        JSONObject c = countrys.getJSONObject(i);
                        CountrysList.add(c.getString(conf.tag_name));
                    }
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }

        countryAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, CountrysList);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Country_sp.setAdapter(countryAdapter);

        cityAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,CitysList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        City_sp.setAdapter(cityAdapter);

        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DateN_txt.setText(new StringBuilder().append(1990).append("/").append(month + 1).append("/").append(day));
        DateN_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), R.style.MyMaterialDesignTheme, dateSetListener, year, month, day).show();
            }
        });

        Gender_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Country_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CitysList = new ArrayList<String>();
                List<NameValuePair> cityParams = new ArrayList<NameValuePair>();
                cityParams.add(new BasicNameValuePair(conf.tag_name, Country_sp.getSelectedItem().toString()));
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
                cityAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, CitysList);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                City_sp.setAdapter(cityAdapter);
            }

            public void onNothingSelected(AdapterView<?> parent) { }
        });

        Driver_swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    driver = true;
                } else {
                    driver = false;
                }
            }
        });


        SignUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(conf.NetworkIsAvailable(getActivity())){
                    submitForm();
                }else{
                    Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
                }
            }
        });
        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginForm();
            }
        });
        return v;
    }

    private void LoginForm() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_body, new Login());
        ft.addToBackStack(null);
        ft.commit();
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.login));
    }

    private void submitForm() {
        if (!validateFname()) { return; }
        if (!validateLname()) { return; }
        if (!validateEmail()) { return; }
        if (!validatePassword()) { return; }
        if (!validatePhone()) { return; }

        Encryption algo = new Encryption();
        int x = algo.keyVirtual();
        String key = algo.key(x);
        //algo.dec2enc(String.valueOf(driver), key)
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_key, x + ""));
        params.add(new BasicNameValuePair(conf.tag_picture, getStringPicture()));
        params.add(new BasicNameValuePair(conf.tag_fname, algo.dec2enc(Fname_etxt.getText().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_lname, algo.dec2enc(Lname_etxt.getText().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_gender, algo.dec2enc(Gender_sp.getSelectedItem().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_dateN, algo.dec2enc(DateN_txt.getText().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_country, algo.dec2enc(Country_sp.getSelectedItem().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_city, algo.dec2enc(City_sp.getSelectedItem().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_email, algo.dec2enc(Email_etxt.getText().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_password, algo.dec2enc(Password_etxt.getText().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_phone, algo.dec2enc(Phone_etxt.getText().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_driver, algo.dec2enc(String.valueOf(driver), key)));
        JSONObject json = sr.getJson(conf.url_signup, params);
        if(json != null){
            try{
                String jsonstr = json.getString(conf.response);
                Toast.makeText(getActivity(),jsonstr,Toast.LENGTH_LONG).show();
                if(json.getBoolean(conf.res)){
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_body, new Login());
                    //ft.addToBackStack(null);
                    ft.commit();
                    ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.login));
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateFname() {
        if(Fname_etxt.getText().toString().trim().isEmpty()) {
            Fname_input.setError(getString(R.string.fname_err));
            requestFocus(Fname_etxt);
            return false;
        } else {
            Fname_input.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLname() {
        if(Lname_etxt.getText().toString().trim().isEmpty()) {
            Lname_input.setError(getString(R.string.lname_err));
            requestFocus(Lname_etxt);
            return false;
        } else {
            Lname_input.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmail() {
        String email = Email_etxt.getText().toString().trim();
        if(email.isEmpty() || !isValidEmail(email)){
            Email_input.setError(getString(R.string.email_err));
            requestFocus(Email_etxt);
            return false;
        }else{
            Email_input.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if(Password_etxt.getText().toString().trim().isEmpty()) {
            Password_input.setError(getString(R.string.password_err));
            requestFocus(Password_etxt);
            return false;
        } else {
            Password_input.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePhone() {
        if(Phone_etxt.getText().toString().trim().isEmpty()) {
            Phone_input.setError(getString(R.string.phone_err));
            requestFocus(Phone_etxt);
            return false;
        } else {
            Phone_input.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
                case R.id.Fname_etxt:
                    validateFname();
                    break;
                case R.id.Lname_etxt:
                    validateLname();
                    break;
                case R.id.Email_etxt:
                    validateEmail();
                    break;
                case R.id.Password_etxt:
                    validatePassword();
                    break;
                case R.id.Phone_etxt:
                    validatePassword();
                    break;
            }
        }
    }

    private String getStringPicture() {
        Picture_iv.buildDrawingCache();
        Bitmap bitmap = Picture_iv.getDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                Picture_iv.setImageURI(selectedImageUri);
            }
        }
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            DateN_txt.setText(new StringBuilder().append(year).append("/").append(month + 1).append("/").append(day));
        }
    };

    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }

}
