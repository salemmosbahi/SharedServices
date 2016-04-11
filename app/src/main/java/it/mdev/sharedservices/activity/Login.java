package it.mdev.sharedservices.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.mdev.sharedservices.Main;
import it.mdev.sharedservices.R;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.Encryption;
import it.mdev.sharedservices.util.ServerRequest;

/**
 * Created by salem on 4/4/16.
 */
public class Login extends Fragment {
    SharedPreferences pref;
    ServerRequest sr = new ServerRequest();
    Controllers conf = new Controllers();

    private EditText Email_etxt, Password_etxt;
    private TextInputLayout Email_input, Password_input;
    private Button Login_btn, SignUp_btn;

    public Login() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login, container, false);
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        Email_input = (TextInputLayout) v.findViewById(R.id.Email_input);
        Password_input = (TextInputLayout) v.findViewById(R.id.Password_input);
        Email_etxt = (EditText) v.findViewById(R.id.Email_etxt);
        Password_etxt = (EditText) v.findViewById(R.id.Password_etxt);
        Login_btn = (Button) v.findViewById(R.id.Login_btn);
        SignUp_btn = (Button) v.findViewById(R.id.SignUp_btn);

        Email_etxt.addTextChangedListener(new MyTextWatcher(Email_etxt));
        Password_etxt.addTextChangedListener(new MyTextWatcher(Password_etxt));

        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conf.NetworkIsAvailable(getActivity())) {
                    submitForm();
                } else {
                    Toast.makeText(getActivity(),R.string.networkunvalid,Toast.LENGTH_SHORT).show();
                }
            }
        });

        SignUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupForm();
            }
        });
        return  v;
    }

    private void signupForm() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_body, new SignUp());
        ft.addToBackStack(null);
        ft.commit();
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.sign_up));
    }

    private void submitForm() {
        if (!validateEmail()) { return; }
        if (!validatePassword()) { return; }

        Encryption algo = new Encryption();
        int x = algo.keyVirtual();
        String key = algo.key(x);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_key, x + ""));
        params.add(new BasicNameValuePair(conf.tag_email, algo.dec2enc(Email_etxt.getText().toString(), key)));
        params.add(new BasicNameValuePair(conf.tag_password, algo.dec2enc(Password_etxt.getText().toString(), key)));
        JSONObject json = sr.getJson(conf.url_login, params);
        if (json != null){
            try{
                String response = json.getString(conf.response);
                if(json.getBoolean(conf.res)) {
                    int keyVirtual = Integer.parseInt(json.getString(conf.tag_key));
                    String newKey = algo.key(keyVirtual);
                    String token = json.getString(conf.tag_token);
                    String username = algo.enc2dec(json.getString(conf.tag_fname), newKey) + " " + algo.enc2dec(json.getString(conf.tag_lname), newKey);
                    String country = algo.enc2dec(json.getString(conf.tag_country), newKey);
                    String city = algo.enc2dec(json.getString(conf.tag_city), newKey);
                    String picture = json.getString(conf.tag_picture);

                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(conf.tag_token, token);
                    edit.putString(conf.tag_username, username);
                    edit.putString(conf.tag_country, country);
                    edit.putString(conf.tag_city, city);
                    edit.putString(conf.tag_picture, picture);
                    edit.commit();

                    RelativeLayout rl = (RelativeLayout) getActivity().findViewById(R.id.nav_header_container);
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View vi = inflater.inflate(R.layout.toolnav_drawer, null);
                    TextView tv = (TextView) vi.findViewById(R.id.usernameTool_txt);
                    tv.setText(username);
                    ImageView im = (ImageView) vi.findViewById(R.id.pictureTool_iv);
                    byte[] imageAsBytes = Base64.decode(picture.getBytes(), Base64.DEFAULT);
                    im.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                    rl.addView(vi);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container_body, new Home());
                    ft.addToBackStack(null);
                    ft.commit();
                    ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
                }else{
                    Toast.makeText(getActivity(),response,Toast.LENGTH_SHORT).show();
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateEmail() {
        String email = Email_etxt.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            Email_input.setError(getString(R.string.email_err));
            requestFocus(Email_etxt);
            return false;
        } else {
            Email_input.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (Password_etxt.getText().toString().trim().isEmpty()) {
            Password_input.setError(getString(R.string.password_err));
            requestFocus(Password_etxt);
            return false;
        } else {
            Password_input.setErrorEnabled(false);
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
                case R.id.Email_etxt:
                    validateEmail();
                    break;
                case R.id.Password_etxt:
                    validatePassword();
                    break;
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }
}
