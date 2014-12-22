package com.l_0k.germes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by knyazev_o on 21.12.2014.
 */
public class StatusesHistoryAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<StatusHistory> statusHistoryArrayList;

    StatusesHistoryAdapter(Context context, ArrayList<StatusHistory> statusHistoryArrayList) {
        this.context = context;
        this.statusHistoryArrayList = statusHistoryArrayList;
        layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return statusHistoryArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return statusHistoryArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public StatusHistory getStatusHistory(int position) {
        return ((StatusHistory) getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
// используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.layout_status_history, parent, false);
        }

        StatusHistory statusHistory = getStatusHistory(position);

        // заполняем View в пункте списка данными
        ((TextView) view.findViewById(R.id.textViewStatusAndDateHistory)).setText(Task.StatusIDToString(context,
                Integer.parseInt(statusHistory.getStatus())) + " " + statusHistory.getStatusTimeStamp());
        ((TextView) view.findViewById(R.id.textViewAddressStatusHistory)).setText(statusHistory.getAddress());

        return view;
    }
}
