package it.mdev.sharedservices.database;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import it.mdev.sharedservices.R;
import it.mdev.sharedservices.activity.BoxUsers;
import it.mdev.sharedservices.activity.Download;
import it.mdev.sharedservices.activity.DownloadProfile;
import it.mdev.sharedservices.activity.DownloadSearch;
import it.mdev.sharedservices.util.Controllers;

/**
 * Created by salem on 15/04/16.
 */
public class DownloadAdapterList extends BaseAdapter {
    Controllers conf = new Controllers();
    LayoutInflater inflater;
    Context contxt;
    List<DownloadDB> data;
    Fragment fragment;

    public DownloadAdapterList(Context contxt, List<DownloadDB> data, Fragment fragment) {
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
        DownloadHolder holder = new DownloadHolder();
        if (v == null) {
            inflater = (LayoutInflater) contxt.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.download_list, null);
            holder.PictureDAL_iv = (ImageView) v.findViewById(R.id.PictureDAL_iv);
            holder.NameDAL_txt = (TextView) v.findViewById(R.id.NameDAL_txt);
            holder.StatusDAL_txt = (TextView) v.findViewById(R.id.StatusDAL_txt);
            holder.SizeDAL_txt = (TextView) v.findViewById(R.id.SizeDAL_txt);
            holder.DateDAL_txt = (TextView) v.findViewById(R.id.DateDAL_txt);
            holder.RowDAL_relative = (RelativeLayout) v.findViewById(R.id.RowDAL_rl);
            v.setTag(holder);
        } else {
            holder = (DownloadHolder) v.getTag();
        }

        if (data.get(position).getPicture().equals("")) {
            holder.PictureDAL_iv.setImageResource(R.drawable.download);
        } else {
            byte[] imageAsBytes = Base64.decode(data.get(position).getPicture().getBytes(), Base64.DEFAULT);
            holder.PictureDAL_iv.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }
        holder.NameDAL_txt.setText(data.get(position).getName());
        if (data.get(position).getStatus().equals("complete")) {
            holder.StatusDAL_txt.setText(R.string.download_complete);
        } else if (data.get(position).getStatus().equals("pending")) {
            holder.StatusDAL_txt.setText(R.string.download_pending);
        }
        holder.SizeDAL_txt.setText(data.get(position).getSize() + " Go");
        holder.DateDAL_txt.setText(data.get(position).getDate());

        holder.RowDAL_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View x) {
                Fragment fr = new DownloadProfile();
                FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                Bundle args = new Bundle();
                args.putString(conf.tag_id, data.get(position).getId());
                if (Download.class.isInstance(fragment)) {
                    args.putString(conf.tag_activity, "Download");
                } else if (DownloadSearch.class.isInstance(fragment)) {
                    args.putString(conf.tag_activity, "DownloadSearch");
                }
                fr.setArguments(args);
                ft.replace(R.id.container_body, fr);
                ft.commit();
            }
        });
        return v;
    }

    class DownloadHolder {
        ImageView PictureDAL_iv;
        TextView NameDAL_txt, StatusDAL_txt, SizeDAL_txt, DateDAL_txt;
        RelativeLayout RowDAL_relative;
    }
}
