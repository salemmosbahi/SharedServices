package it.mdev.sharedservices.database;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import it.mdev.sharedservices.R;
import it.mdev.sharedservices.activity.BoxUsers;
import it.mdev.sharedservices.activity.CarProfile;
import it.mdev.sharedservices.activity.DownloadProfile;
import it.mdev.sharedservices.activity.EventProfile;
import it.mdev.sharedservices.activity.PaperProfile;
import it.mdev.sharedservices.util.Controllers;

/**
 * Created by salem on 23/04/16.
 */
public class BoxNotifyAdapterList extends BaseAdapter {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    LayoutInflater inflater;
    Context contxt;
    List<BoxNotifyDB> data;
    Fragment fragment;

    public BoxNotifyAdapterList(Context contxt, List<BoxNotifyDB> data, Fragment fragment) {
        this.contxt = contxt;
        this.data = data;
        this.fragment = fragment;
    }

    @Override
    public int getCount() { return data.size(); }

    @Override
    public Object getItem(int position) { return data.get(position); }

    @Override
    public long getItemId(int position) { return data.indexOf(getItem(position)); }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        pref = contxt.getSharedPreferences(conf.app, Context.MODE_PRIVATE);
        BoxNotifyHolder holder = new BoxNotifyHolder();
        if (v == null) {
            inflater = (LayoutInflater) contxt.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.box_notify_list, null);
            holder.Name_txt = (TextView) v.findViewById(R.id.Name_txt);
            holder.Username_txt = (TextView) v.findViewById(R.id.Username_txt);
            holder.Service_txt = (TextView) v.findViewById(R.id.Service_txt);
            holder.Row_relative = (RelativeLayout) v.findViewById(R.id.BoxNotify_rl);
            v.setTag(holder);
        } else {
            holder = (BoxNotifyHolder) v.getTag();
        }
        holder.Name_txt.setText(data.get(position).getName());
        if (data.get(position).isMain()) {
            holder.Username_txt.setText("Me :)");
        } else {
            holder.Username_txt.setText(data.get(position).getUsernameMain());
        }
        holder.Service_txt.setText(data.get(position).getService());

        holder.Row_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View x) {
                //Toast.makeText(contxt, "xx " + data.get(position).isMain() + " "+ R.string.download + " " + data.get(position).getService(), Toast.LENGTH_SHORT).show();
                if (data.get(position).isMain()) {
                    Fragment fr = new BoxUsers();
                    FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    Bundle args = new Bundle();
                    args.putString(conf.tag_idService, data.get(position).getIdService());
                    args.putString(conf.tag_service, data.get(position).getService());
                    fr.setArguments(args);
                    ft.replace(R.id.container_body, fr);
                    ft.commit();
                } else {
                    if (data.get(position).getService().equals(contxt.getString(R.string.car))) {
                        Fragment fr = new CarProfile();
                        goFragment(fr, position);
                    } else if (data.get(position).getService().equals(contxt.getString(R.string.download))) {
                        Fragment fr = new DownloadProfile();
                        goFragment(fr, position);
                    } else if (data.get(position).getService().equals(contxt.getString(R.string.event))) {
                        Fragment fr = new EventProfile();
                        goFragment(fr, position);
                    } else if (data.get(position).getService().equals(contxt.getString(R.string.paper))) {
                        Fragment fr = new PaperProfile();
                        goFragment(fr, position);
                    }
                }
            }
        });
        return v;
    }

    private void goFragment(Fragment fr, int position) {
        FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        Bundle args = new Bundle();
        args.putString(conf.tag_id, data.get(position).getIdService());
        args.putString(conf.tag_activity, "BoxNotify");
        fr.setArguments(args);
        ft.replace(R.id.container_body, fr);
        ft.commit();
    }

    class BoxNotifyHolder {
        TextView Name_txt, Username_txt, Service_txt;
        RelativeLayout Row_relative;
    }
}
