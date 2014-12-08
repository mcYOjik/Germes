package com.l_0k.germes;

/**
 * Created by knyazev_o on 28.10.2014.
 */

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TaskAdapter extends BaseAdapter  {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Task> taskArrayList;

    TaskAdapter(Context context, ArrayList<Task> tasks) {
        this.context = context;
        taskArrayList = tasks;
        layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return taskArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return taskArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.layout_task, parent, false);
        }

        Task task = getTask(position);

        // заполняем View в пункте списка данными
        ((TextView) view.findViewById(R.id.textViewID)).setText(task.getTask1cID());
        ((TextView) view.findViewById(R.id.textViewDates)).setText(task.getCreateDate() + " " + task.getUpToDate());
        ((TextView) view.findViewById(R.id.textPhoneAndAddress)).setText(task.getCustomerPhone() + ", " + task.getCustomerAddress());
        ((TextView) view.findViewById(R.id.textViewGoods)).setText(task.getGoods());

        switch (task.getStatus()) {
            case Task.STATUS_DELIVERING:
                ((ImageView) view.findViewById(R.id.imageViewTaskStatus)).setImageResource(R.drawable.car_blue70);
                ((ImageView) view.findViewById(R.id.imageViewTaskStatus)).setBackgroundResource(R.drawable.task_button_green);
                break;
            case Task.STATUS_DRIVER_REFUSED:
                ((ImageView) view.findViewById(R.id.imageViewTaskStatus)).setImageResource(R.drawable.car_blue70);
                ((ImageView) view.findViewById(R.id.imageViewTaskStatus)).setBackgroundResource(R.drawable.task_button_red);
                break;
            case Task.STATUS_DELIVERED:
                ((ImageView) view.findViewById(R.id.imageViewTaskStatus)).setImageResource(R.drawable.customer_blue70);
                ((ImageView) view.findViewById(R.id.imageViewTaskStatus)).setBackgroundResource(R.drawable.task_button_green);
                break;
            case Task.STATUS_CUSTOMER_REFUSED:
                ((ImageView) view.findViewById(R.id.imageViewTaskStatus)).setImageResource(R.drawable.customer_blue70);
                ((ImageView) view.findViewById(R.id.imageViewTaskStatus)).setBackgroundResource(R.drawable.task_button_red);
                break;
            default:
                ((ImageView) view.findViewById(R.id.imageViewTaskStatus)).setImageResource(R.drawable.car_blue70);
                ((ImageView) view.findViewById(R.id.imageViewTaskStatus)).setBackgroundResource(R.drawable.task_button_green);
        }
        ((ImageView) view.findViewById(R.id.imageViewTaskStatus)).setTag(task.get_id());

        return view;
    }

    private Task getTask(int position) {
        return ((Task) getItem(position));
    }

}
