package it.mdev.sharedservices.database;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import it.mdev.sharedservices.R;
import it.mdev.sharedservices.activity.BoxUsers;
import it.mdev.sharedservices.activity.CarProfile;
import it.mdev.sharedservices.activity.DownloadProfile;
import it.mdev.sharedservices.activity.EventProfile;
import it.mdev.sharedservices.activity.PaperProfile;
import it.mdev.sharedservices.activity.Profile;
import it.mdev.sharedservices.util.Controllers;

/**
 * Created by salem on 23/04/16.
 */
public class UserCopyAdapterList extends BaseAdapter {
    Controllers conf = new Controllers();
    LayoutInflater inflater;
    Context contxt;
    List<UserCopyDB> data;
    Fragment fragment;

    public UserCopyAdapterList(Context contxt, List<UserCopyDB> data, Fragment fragment) {
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
        UserCopyHolder holder = new UserCopyHolder();
        if (v == null) {
            inflater = (LayoutInflater) contxt.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.user_copy_list, null);
            holder.UserameUCAL_txt = (TextView) v.findViewById(R.id.UserameUCAL_txt);
            holder.AgeUCAL_txt = (TextView) v.findViewById(R.id.AgeUCAL_txt);
            holder.RowUCAL_relative = (RelativeLayout) v.findViewById(R.id.RowUCAL_rl);
            v.setTag(holder);
        } else {
            holder = (UserCopyHolder) v.getTag();
        }
        holder.UserameUCAL_txt.setText(data.get(position).getUsername());
        holder.AgeUCAL_txt.setText(data.get(position).getAge());
        holder.RowUCAL_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View x) {
                Fragment fr = new Profile();
                FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                Bundle args = new Bundle();
                if (BoxUsers.class.isInstance(fragment)) {
                    args.putString(conf.tag_activity, "BoxUsers");
                } else if (CarProfile.class.isInstance(fragment)) {
                    args.putString(conf.tag_activity, "CarProfile");
                } else if (DownloadProfile.class.isInstance(fragment)) {
                    args.putString(conf.tag_activity, "DownloadProfile");
                } else if (EventProfile.class.isInstance(fragment)) {
                    args.putString(conf.tag_activity, "EventProfile");
                } else if (PaperProfile.class.isInstance(fragment)) {
                    args.putString(conf.tag_activity, "PaperProfile");
                }
                args.putString(conf.tag_id, data.get(position).getToken());
                args.putString(conf.tag_service, data.get(position).getService());
                args.putString(conf.tag_idService, data.get(position).getIdService());
                fr.setArguments(args);
                ft.replace(R.id.container_body, fr);
                ft.commit();
            }
        });

        return v;
    }

    class UserCopyHolder {
        TextView UserameUCAL_txt, AgeUCAL_txt;
        RelativeLayout RowUCAL_relative;
    }
}
