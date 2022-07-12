package com.handheld.uhfrdemo;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.handheld.uhfr.R;

/**
 * Created by lbx on 2017/3/13.
 */
public class EPCadapter extends BaseAdapter {

    private List<EpcDataModel> list;
    private Context context;

    public EPCadapter(Context context, List<EpcDataModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override

    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_epc, null);
                holder.tVRssi = (TextView) convertView.findViewById(R.id.textView_rssi);
                holder.tvEpc = (TextView) convertView.findViewById(R.id.textView_epc);
                holder.tvId = (TextView) convertView.findViewById(R.id.textView_id);
                holder.tvCount = (TextView) convertView.findViewById(R.id.textView_count);
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView6);

                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

            if (list != null && !list.isEmpty()) {
                int id = position + 1;
                holder.tvId.setText(String.valueOf(id));
                holder.tvEpc.setText(list.get(position).getepc());
                holder.tVRssi.setText(list.get(position).getrssi());
                holder.tvCount.setText(String.valueOf(list.get(position).getCount()));

                if (list.get(position).isSent())
                    holder.imageView.setVisibility(View.VISIBLE);
                else
                    holder.imageView.setVisibility(View.INVISIBLE);

            }
        } catch (Exception ignored) {

        }

        return convertView;
    }

    private static class ViewHolder {
        TextView tVRssi;
        ImageView imageView;
        TextView tvEpc;
        TextView tvId;
        TextView tvCount;
    }
}
