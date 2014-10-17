package com.example.Banter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by jacobmeidell on 17.10.14.
 */
public class BanterMenuListAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] values;

    public BanterMenuListAdapter(Context context, String[] values){
        super(context,R.layout.bantermenuitem);
        this.context = context;
        this.values = values;
    }
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.bantermenuitem,parent,false);
        TextView gropuname = (TextView) row.findViewById(R.id.groupname);
        TextView lastupdated = (TextView) row.findViewById(R.id.lastupdated);
        TextView posts = (TextView) row.findViewById(R.id.posts);

        /* For test */
        gropuname.setText(values[0]);
        lastupdated.setText(values[1]);
        posts.setText(values[2]);
        Log.v("@@@@@@@@@@@@@@@",gropuname.getText().toString());
        Log.v("@@@@@@@@@@@@@@@",lastupdated.getText().toString());
        Log.v("@@@@@@@@@@@@@@@",posts.getText().toString());

        return row;
    }
}
