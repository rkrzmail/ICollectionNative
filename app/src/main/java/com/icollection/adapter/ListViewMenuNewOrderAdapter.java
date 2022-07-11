package com.icollection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.icollection.R;
import com.icollection.modelservice.MenuItems;

import java.util.ArrayList;

/**
 * Created by Mounzer on 8/22/2017.
 */

public class ListViewMenuNewOrderAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MenuItems> menuItems;
    private LayoutInflater mInflater;

    public ListViewMenuNewOrderAdapter(Context mContext, ArrayList<MenuItems> menuItems){
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_menu_listview_first_design, null);
            holder = new ViewHolder();

            holder.menuText = (TextView) convertView.findViewById(R.id.title);
            holder.buttonMenu = (RelativeLayout) convertView.findViewById(R.id.button_menu);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.menuText.setText(menuItems.get(position).getTitle());

        return convertView;
    }

    public static class ViewHolder {
        TextView menuText;
        RelativeLayout buttonMenu;
    }
}
