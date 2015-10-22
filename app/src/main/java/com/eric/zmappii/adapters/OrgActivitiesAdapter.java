package com.eric.zmappii.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eric.zmappii.R;
import com.eric.zmappii.bean.OrgActivity;

import java.util.List;

/**
 * Created by eric on 15/7/30.
 */
public class OrgActivitiesAdapter extends BaseAdapter {

    private List<OrgActivity> activities;
    private Context context;
    private LayoutInflater inflater;

    private static final String TAG = "ZMAPP";

    public OrgActivitiesAdapter(Context context, List<OrgActivity> orgActivities) {
        super();
        this.activities = orgActivities;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return activities.size();
    }

    @Override
    public Object getItem(int position) {

        return activities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, position+"");
        ViewHolder holder;
        if (null == convertView) {
            convertView = inflater.inflate(
                    R.layout.item_org_activities, null);

            holder = new ViewHolder();
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_activity_title);
            holder.tvTimePub = (TextView) convertView.findViewById(R.id.tv_activity_publish_time);
            holder.imgActivity = (ImageView) convertView.findViewById(R.id.img_activity);
            holder.tvPeriod = (TextView) convertView.findViewById(R.id.tv_activity_time);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvTitle.setText(activities.get(position).getTitle());
        holder.tvTimePub.setText(activities.get(position).getTimePub());
        holder.imgActivity.setImageDrawable(context.getResources().getDrawable(R.drawable.img_fishing));
//        getImg();
        holder.tvPeriod.setText(activities.get(position).getTimeStart() + " ~ " + activities.get(position).getTimeEnd());
        holder.tvContent.setText(activities.get(position).getContent());

        return convertView;
    }

    private void getImg() {

    }

    class ViewHolder {
        public TextView tvTitle;
        public TextView tvTimePub;
        public ImageView imgActivity;
        public TextView tvPeriod;
        public TextView tvContent;
    }
}
