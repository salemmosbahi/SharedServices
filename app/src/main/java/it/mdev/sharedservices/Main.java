package it.mdev.sharedservices;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.mdev.sharedservices.activity.BoxNotify;
import it.mdev.sharedservices.activity.Car;
import it.mdev.sharedservices.activity.Download;
import it.mdev.sharedservices.activity.Event;
import it.mdev.sharedservices.activity.Home;
import it.mdev.sharedservices.activity.Login;
import it.mdev.sharedservices.activity.Paper;
import it.mdev.sharedservices.activity.Profile;
import it.mdev.sharedservices.activity.About;
import it.mdev.sharedservices.design.FragmentDrawer;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.ServerRequest;
import it.mdev.sharedservices.util.SocketIO;

public class Main extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener{
    private SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();
    Socket socket = SocketIO.getInstance();

    public Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        pref = getSharedPreferences(conf.app, Context.MODE_PRIVATE);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        displayView(0);
        doIncrease(0);
        if(!pref.getString(conf.tag_token, "").equals("")){
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.nav_header_container);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View vi = inflater.inflate(R.layout.toolnav_drawer, null);
            TextView tv = (TextView) vi.findViewById(R.id.usernameTool_txt);
            tv.setText(pref.getString(conf.tag_username, ""));
            ImageView im = (ImageView) vi.findViewById(R.id.pictureTool_iv);
            byte[] imageAsBytes = Base64.decode(pref.getString(conf.tag_picture, "").getBytes(), Base64.DEFAULT);
            im.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            rl.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    displayView(5);
                }
            });
            rl.addView(vi);
            if (conf.NetworkIsAvailable(this)) {
                getCount(pref.getString(conf.tag_token, ""));
            } else {
                Toast.makeText(this, R.string.networkunvalid, Toast.LENGTH_SHORT).show();
            }
            socket.connect();
            socket.on(conf.io_count, handleIncomingCount); //listen in count notify
        }
    }

    private Emitter.Listener handleIncomingCount = new Emitter.Listener(){
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    final String tokenMain, token, type;
                    try {
                        type = data.getString(conf.tag_type);
                        tokenMain = data.getString(conf.tag_tokenMain);
                        token = data.getString(conf.tag_token);
                        if (type.equals("token")) {
                            if (tokenMain.equals(pref.getString(conf.tag_token, ""))) {
                                if (conf.NetworkIsAvailable(getApplication())) {
                                    getCount(pref.getString(conf.tag_token, ""));
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
                                }
                            } else if (token.equals(pref.getString(conf.tag_token, ""))) {
                                if (conf.NetworkIsAvailable(getApplication())) {
                                    getCount(pref.getString(conf.tag_token, ""));
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (type.equals("nottoken")) {
                            if (conf.NetworkIsAvailable(getApplication())) {
                                getCount(pref.getString(conf.tag_token, ""));
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
                            }
                        } else if (type.equals("logout")) {
                            if (tokenMain.equals(pref.getString(conf.tag_token, "")) || token.equals(pref.getString(conf.tag_token, ""))) {
                                doIncrease(0);
                            }
                        }
                    } catch (JSONException e) { }
                }
            });
        }
    };

    private void getCount(String token) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_token, token));
        JSONObject json = sr.getJson(conf.url_count, params);
        if (json != null) {
            try{
                if (json.getBoolean(conf.res)) {
                    int count = json.getInt(conf.tag_count);
                    doIncrease(count);
                } else {
                    doIncrease(0);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, R.string.serverunvalid, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_notify);
        menuItem.setIcon(counterDrawable(count, R.drawable.notify));
        return true;
    }

    private Drawable counterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.notify, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterPanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            view.setBackgroundResource(backgroundImageId);
            TextView textView = (TextView) view.findViewById(R.id.count_txt);
            textView.setText("" + count);
        }

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    private void doIncrease(int countDB) {
        count = countDB;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_notify){
            displayView(7);
            return true;
        }
        if (id == R.id.action_about) {
            displayView(6);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    public void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new Home();
                title = getString(R.string.app_name);
                break;
            case 1:
                fragment = new Car();
                title = getString(R.string.car);
                break;
            case 2:
                fragment = new Download();
                title = getString(R.string.download);
                break;
            case 3:
                fragment = new Event();
                title = getString(R.string.event);
                break;
            case 4:
                fragment = new Paper();
                title = getString(R.string.paper);
                break;
            case 5:
                fragment = new Profile();
                title = getString(R.string.profile);
                break;
            case 6:
                fragment = new About();
                title = getString(R.string.about);
                break;
            case 7:
                fragment = new BoxNotify();
                title = getString(R.string.box);
                break;
            default:
                break;
        }

        if (fragment != null) {
            if( pref.getString(conf.tag_token, "").equals("")) {
                if (title.equals(getString(R.string.app_name)) || title.equals(getString(R.string.about))) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.commit();
                } else {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.container_body, new Login());
                    fragmentTransaction.commit();
                }
            } else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();
            }
        }
    }
}
