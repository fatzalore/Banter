package com.example.Banter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Erlend on 17.10.2014.
 */
public class BanterRoomListAdapter extends ArrayAdapter<BanterPost> {

    private Context context;
    private ArrayList<BanterPost> values;

    static class ViewHolderItem{
        ImageView image;
        TextView name;
        TextView text;
        TextView time;
        TextView likes;
        ImageButton incLike;
        ImageButton decLike;
    }

    public BanterRoomListAdapter(Context context, ArrayList<BanterPost> values){
        super(context, R.layout.banter_room_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolderItem viewHolderItem;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.banter_room_item, parent, false);
            viewHolderItem = new ViewHolderItem();
            viewHolderItem.name = (TextView) convertView.findViewById(R.id.room_chat_list_name);
            viewHolderItem.time = (TextView) convertView.findViewById(R.id.room_chat_list_time);
            viewHolderItem.text = (TextView) convertView.findViewById(R.id.room_chat_list_text);
            viewHolderItem.likes = (TextView) convertView.findViewById(R.id.room_chat_list_likes);
            viewHolderItem.image = (ImageView) convertView.findViewById(R.id.room_chat_list_image);
            viewHolderItem.incLike = (ImageButton) convertView.findViewById(R.id.room_chat_list_like_inc);
            viewHolderItem.decLike = (ImageButton) convertView.findViewById(R.id.room_chat_list_like_dec);
            convertView.setTag(viewHolderItem);
        } else {
            viewHolderItem = (ViewHolderItem) convertView.getTag();
        }
        /* does post have image? */
        if (values.get(position).getImage() != null) {
            viewHolderItem.image.setImageBitmap(values.get(position).getImage());
            viewHolderItem.image.setVisibility(View.VISIBLE);
        }

        viewHolderItem.name.setText(values.get(position).getName());
        viewHolderItem.text.setText(values.get(position).getText());
        viewHolderItem.likes.setText(Integer.toString(values.get(position).getLikes()));
        viewHolderItem.time.setText(values.get(position).getTime());

        viewHolderItem.incLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                values.get(position).incrementLikes();
                notifyDataSetChanged();
                // TODO set image to a green arrow, same with on down
            }
        });

        viewHolderItem.decLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                values.get(position).decrementLikes();
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}