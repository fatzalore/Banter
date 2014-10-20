package com.example.Banter;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jacobmeidell on 17.10.14.
 */
public class BanterRoomFragment extends Fragment {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    View banterRoomFragment;

    ListView banterRoomList;
    ImageButton cameraButton;
    ImageButton attachImageButton;
    ImageButton smileyButton;
    ImageButton submitPostButton;
    EditText newPostText;
    ImageView newPostImage;

    Timer timer;
    JSONArray posts = null;
    JSONParser jsonParser = new JSONParser();


    BanterRoomListAdapter banterRoomListAdapter;

    BanterPost currentPost = new BanterPost(); // This is the post that is currently written by user. When user press submit, the info (image etc.) will be retrieved from this obj.

    BanterActivity banterActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        setHasOptionsMenu(true);
        banterActivity = (BanterActivity) getActivity();
        getActivity().setContentView(R.layout.banter_room_layout);
        banterRoomFragment = inflater.inflate(R.layout.banter_room_layout,container,false);
        banterRoomList = (ListView) banterRoomFragment.findViewById(R.id.room_chat_list);

        /* get the views from layout */
        cameraButton = (ImageButton) banterRoomFragment.findViewById(R.id.room_post_camera_button);
        attachImageButton = (ImageButton) banterRoomFragment.findViewById(R.id.room_post_image_button);
        smileyButton = (ImageButton) banterRoomFragment.findViewById(R.id.room_post_smiley_button);
        submitPostButton = (ImageButton) banterRoomFragment.findViewById(R.id.room_post_submit_button);
        newPostText = (EditText) banterRoomFragment.findViewById(R.id.room_post_text);
        newPostImage = (ImageView) banterRoomFragment.findViewById(R.id.room_post_camera_temp);
        
        /* bottom buttons listeners */
        addCameraListener();
        //addAttachImageListener();
        //addSmileyListener();
        addSubmitPostListener();

        addOnHoldCameraTempImageListener();

        /* get data */
        // addTestData();
        /* TEST DATA end */

        banterRoomListAdapter = new BanterRoomListAdapter(getActivity().getBaseContext(), banterActivity.banterDataModel.currentRoom.getPosts());
        banterRoomList.setAdapter(banterRoomListAdapter);


        return banterRoomFragment;
    }

    private void addTestData() {
         /* TEST DATA! */
        banterActivity.banterDataModel.currentRoom = new BanterRoom("test room");
        BanterPost testPost1 = new BanterPost();
        testPost1.setName("Ali baba");
        testPost1.setLikes(9);
        testPost1.setText("Lorem ipsum .... blablablabla Banter i massevis");
        testPost1.setTimeAndDateToNow();
        BanterPost testPost2 = new BanterPost();
        testPost2.setName("Venn av ali baba");
        testPost2.setLikes(-11);
        testPost2.setTimeAndDateToNow();
        testPost2.setText("Lorem ipsum .... blablablabla Banter i massevis Lorem ipsum .... blablablabla Banter i massevis Lorem ipsum .... blablablabla Banter i massevis Lorem ipsum .... blablablabla Banter i massevis Lorem ipsum .... blablablabla Banter i massevis Lorem ipsum .... blablablabla Banter i massevis");
        banterActivity.banterDataModel.currentRoom.addPost(testPost1);
        banterActivity.banterDataModel.currentRoom.addPost(testPost2);
        /* TEST DATA end*/
    }

    private void addCameraListener() {
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE); // 100 = CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
            }
        });
    }

    private void addSubmitPostListener() {
        submitPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* set data into currentPost */
                currentPost.setText(newPostText.getText().toString());
                currentPost.setTimeAndDateToNow();
                currentPost.setLikes(0);

                if (currentPost.getImage() != null || currentPost.getText().length() > 0) {
                    /* ok, we are ready to post */
                    final EditText userTextView = new EditText(getActivity());
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Written by?")
                            .setView(userTextView)
                            .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    /* save post! */
                                    if (userTextView.getText().length() < 1)
                                        currentPost.setName("Anonymous");
                                    else
                                        currentPost.setName(userTextView.getText().toString());

                                    banterActivity.banterDataModel.currentRoom.addPost(currentPost);
                                    currentPost = new BanterPost();

                                    /* Remove old data */
                                    newPostImage.setVisibility(View.GONE);
                                    newPostText.setText("");

                                    /* TODO: update database */

                                    /* refresh ui */
                                    banterRoomListAdapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                /* user still have text and image available.. but nothing is done */
                                }
                            })
                            .show();

                } else {
                    /* no image or text? not a valid post.. */

                }
            }
        });
    }

    /* If a user holds down on a temporary picture, give option to remove */
    private void addOnHoldCameraTempImageListener() {
        newPostImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Remove image?")
                        .setPositiveButton("Keep", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // nothing happens
                            }
                        })
                        .setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                /* remove picture from ui and data object */
                                newPostImage.setVisibility(View.GONE);
                                currentPost.setImage(null);
                            }
                        })
                        .show();

                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // Image captured
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

                /* store in currentPost */
                currentPost.setImage(thumbnail);

                /* show in ui */
                newPostImage.setImageBitmap(thumbnail);
                newPostImage.setVisibility(View.VISIBLE);

            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    /* Set the custom action bar to the menu fragment */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.banter_room_actionbar,menu);
    }
    public BanterRoomListAdapter getBanterRoomListAdapter(){
        return banterRoomListAdapter;
    }


    /* Class handles the loading of the posts of a room that a user has access to */
    class PostPolling extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... args) {
            BanterRoom current = banterActivity.getBanterDataModel().currentRoom;
            BanterPost last = current.getPosts().get(current.getPosts().size()-1);
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(BanterSQLContract.TAG_ROOM_ID,Integer.toString(current.getId())));
            params.add(new BasicNameValuePair(BanterSQLContract.TAG_POST_ID,Integer.toString(last.getId())));
            JSONObject json = jsonParser.makeHttpRequest(BanterSQLContract.URL_GET_POSTS,"GET",params);
            try{
                int success = json.getInt(BanterSQLContract.TAG_SUCCESS);
                if (success == 1) {
                    posts = json.getJSONArray(BanterSQLContract.TAG_POSTS);
                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject c = posts.getJSONObject(i);

                        BanterPost banterPost = new BanterPost();
                        banterPost.setId(c.getInt(BanterSQLContract.TAG_POST_ID));
                        banterPost.setName(c.getString(BanterSQLContract.TAG_NAME));
                        banterPost.setLikes(c.getInt(BanterSQLContract.TAG_LIKES));
                        banterPost.setText(c.getString(BanterSQLContract.TAG_TEXT));
                        banterPost.setTime(c.getString(BanterSQLContract.TAG_TIME));

                        banterActivity.getBanterDataModel().currentRoom.addPost(banterPost);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String file_url) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getBanterRoomListAdapter().notifyDataSetChanged();
                }
            });
        }
    }


}
