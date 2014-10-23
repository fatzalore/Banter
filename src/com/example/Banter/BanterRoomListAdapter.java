package com.example.Banter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by Erlend on 17.10.2014.
 */
public class BanterRoomListAdapter extends ArrayAdapter<BanterPost> {

    private static String OPERATOR_PLUS = "plus";
    private static String OPERATOR_MINUS = "minus";

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
        } else {
            viewHolderItem.image.setVisibility(View.GONE);
        }

        viewHolderItem.name.setText(values.get(position).getName());
        viewHolderItem.text.setText(values.get(position).getText());
        viewHolderItem.likes.setText(Integer.toString(values.get(position).getLikes()));
        viewHolderItem.time.setText(values.get(position).getTime());

        viewHolderItem.incLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!values.get(position).isLiked()) {
                    values.get(position).incrementLikes();
                    notifyDataSetChanged();
                    values.get(position).setLiked(true);

                    /* update db */
                    new incrementOrDecrementLikes(values.get(position).getId(), OPERATOR_PLUS).execute();

                } else {
                    Toast.makeText(getContext(), "You have already rated this post", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewHolderItem.decLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!values.get(position).isLiked()) {
                    values.get(position).decrementLikes();
                    notifyDataSetChanged();
                    values.get(position).setLiked(true);

                    /* update db */
                    new incrementOrDecrementLikes(values.get(position).getId(), OPERATOR_MINUS).execute();

                } else {
                    Toast.makeText(getContext(), "You have already rated this post", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }

    /* Class handles the incrementing or decrementing of likes in database */
    class incrementOrDecrementLikes extends AsyncTask<String,String,String> {

        JSONParser jsonParser;
        int post_id;
        String operator;

        public incrementOrDecrementLikes(int post_id, String operator) {
            super();
            jsonParser = new JSONParser();
            this.post_id = post_id;
            this.operator = operator;
        }

        @Override
        protected String doInBackground(String... args) {

            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(BanterSQLContract.TAG_POST_ID, Integer.toString(post_id)));
            params.add(new BasicNameValuePair(BanterSQLContract.TAG_OPERATOR, operator));

            JSONObject json = jsonParser.makeHttpRequest(BanterSQLContract.URL_LIKE_POST, "POST", params);

            try {
                int success = json.getInt(BanterSQLContract.TAG_SUCCESS);
                if (success == 1) {

                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}