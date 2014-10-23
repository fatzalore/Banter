package com.example.Banter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jacobmeidell on 17.10.14.
 */
public class BanterMenuListAdapter extends ArrayAdapter<BanterRoom> {

    private Context context;
    private ArrayList<BanterRoom> values;

    static class ViewHolderItem {
        TextView groupName;
        TextView lastUpdated;
        TextView posts;
    }

    public BanterMenuListAdapter(Context context, ArrayList<BanterRoom> values){
        super(context,R.layout.banter_menu_item,values);
        this.context = context;
        this.values = values;
    }
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent){

        ViewHolderItem viewHolderItem;
        BanterRoom banterRoom = values.get(position);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.banter_menu_item,parent,false);
            viewHolderItem = new ViewHolderItem();
            viewHolderItem.groupName = (TextView) convertView.findViewById(R.id.groupname);
            viewHolderItem.lastUpdated = (TextView) convertView.findViewById(R.id.lastupdated);
            viewHolderItem.posts = (TextView) convertView.findViewById(R.id.posts);
            convertView.setTag(viewHolderItem);
        } else {
            viewHolderItem = (ViewHolderItem) convertView.getTag();
        }
        viewHolderItem.groupName.setText(banterRoom.getName());
        if (banterRoom.isUpdateChecked()) {
            viewHolderItem.lastUpdated.setTextColor(Color.BLACK);
            viewHolderItem.posts.setTextColor(Color.BLACK);
            viewHolderItem.lastUpdated.setText(banterRoom.getLastUpdated());
        } else {
            viewHolderItem.lastUpdated.setTextColor(Color.parseColor("#137a0c"));
            viewHolderItem.posts.setTextColor(Color.parseColor("#137a0c"));
            viewHolderItem.lastUpdated.setText(banterRoom.getLastUpdated());
            //viewHolderItem.lastUpdated.setText(banterRoom.getLastUpdated() + " - " + banterRoom.getTimeSinceLastUpdate());
        }
        viewHolderItem.posts.setText(Integer.toString(banterRoom.getPostAmount()));

        return convertView;
    }

}
