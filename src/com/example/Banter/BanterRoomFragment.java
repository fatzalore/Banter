package com.example.Banter;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.*;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.widget.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Timer;

/**
 * Created by jacobmeidell on 17.10.14.
 */
public class BanterRoomFragment extends Fragment {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 1777;
    public static final int REQ_CODE_PICK_IMAGE = 1;

    public static final String BANTER_CAMERA_IMAGE_FOLDER_NAME = "Banter";

    public static final int MAX_POST_STORED_LOCALLY = 20;

    View banterRoomFragment;

    ListView banterRoomList;
    ImageButton cameraButton;
    ImageButton attachImageButton;
    ImageButton smileyButton;
    ImageButton submitPostButton;
    ImageButton newPostTextSubmitButton;
    EditText newPostText;
    ImageView newPostImage;

    String supportedImageExtension = ".jpeg";

    int interval = 5000;

    Timer timer;
    JSONArray posts = null;
    JSONParser jsonParser = new JSONParser();

    Dialog submitDialog;
    ImageButton submitDialogYes;
    ImageButton submitDialogNo;
    EditText submitDialogInput;

    Dialog removeImageDialog;
    ImageButton removeImageDialogYes;
    ImageButton removeImageDialogNo;

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

        banterActivity.setTitle(banterActivity.banterDataModel.currentRoom.getName());
        //banterActivity.getActionBar().setDisplayHomeAsUpEnabled(true);
        //Commented because i could not be able to set onclicklistener anywhere..

        /* get the views from layout */
        cameraButton = (ImageButton) banterRoomFragment.findViewById(R.id.room_post_camera_button);
        attachImageButton = (ImageButton) banterRoomFragment.findViewById(R.id.room_post_image_button);
        smileyButton = (ImageButton) banterRoomFragment.findViewById(R.id.room_post_smiley_button);
        //submitPostButton = (ImageButton) banterRoomFragment.findViewById(R.id.room_post_submit_button);
        newPostText = (EditText) banterRoomFragment.findViewById(R.id.room_post_text);
        newPostImage = (ImageView) banterRoomFragment.findViewById(R.id.room_post_camera_temp);
        newPostTextSubmitButton = (ImageButton) banterRoomFragment.findViewById(R.id.room_textfield_submit_button);
        
        /* bottom buttons listeners */
        addCameraListener();
        addAttachImageListener();
        //addSmileyListener();
        addSubmitPostListener();

        addOnHoldCameraTempImageListener();

        banterRoomListAdapter = new BanterRoomListAdapter(getActivity().getBaseContext(), banterActivity.banterDataModel.currentRoom.getPosts());
        banterRoomList.setAdapter(banterRoomListAdapter);

        setBanterRoomListOnClickListener();

        beginPostPolling(interval);


        /* get some likes too */
        new getLikes().execute();

        return banterRoomFragment;
    }

    private void setBanterRoomListOnClickListener(){

        /* Listen for item clicks */
        banterRoomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                banterActivity.banterDataModel.currentRoom.getPosts().get(i).setUpdateChecked(true);
                banterRoomListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addAttachImageListener() {
        attachImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQ_CODE_PICK_IMAGE);
            }
        });
    }

    private void addCameraListener() {
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File file = new File(Environment.getExternalStorageDirectory() + "/" + BANTER_CAMERA_IMAGE_FOLDER_NAME + File.separator + "image" + banterActivity.banterDataModel.imageCounter + supportedImageExtension);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    private void addSubmitPostListener() {
        View.OnClickListener submitListener =  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* set data into currentPost */
                currentPost.setText(newPostText.getText().toString());
                currentPost.setTimeAndDateToNow();
                currentPost.setLikes(0);

                if (currentPost.getImage() != null || currentPost.getText().length() > 0) {

                    submitDialog = new Dialog(banterActivity);
                    submitDialog.setContentView(R.layout.banter_room_submit_dialog);
                    submitDialog.setTitle("Banter by...?");
                    submitDialogYes = (ImageButton) submitDialog.findViewById(R.id.room_submit_dialog_yes);
                    submitDialogNo = (ImageButton) submitDialog.findViewById(R.id.room_submit_dialog_no);
                    submitDialogInput = (EditText) submitDialog.findViewById(R.id.room_submit_dialog_input);
                    submitDialogYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (submitDialogInput.getText().toString().length() < 1) {
                                currentPost.setName("Anonymous");
                            } else {
                                currentPost.setName(submitDialogInput.getText().toString());
                            }
                            if (banterActivity.isNetworkAvailable) {
                                new CreatePost().execute();
                                newPostImage.setVisibility(View.GONE);
                                newPostText.setText("");
                                submitDialog.dismiss();
                            } else {
                                Toast.makeText(getActivity(), "Sorry, no internet connection available", Toast.LENGTH_SHORT).show();
                                submitDialog.dismiss();
                            }
                        }

                    });

                    submitDialogNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            submitDialog.dismiss();
                        }
                    });
                    submitDialog.show();
                }
            }
        };
        //submitPostButton.setOnClickListener(submitListener);
        newPostTextSubmitButton.setOnClickListener(submitListener);
    }

    /* If a user holds down on a temporary picture, give option to remove */
    private void addOnHoldCameraTempImageListener() {
        newPostImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                removeImageDialog = new Dialog(getActivity());
                removeImageDialog.setContentView(R.layout.banter_room_remove_image_dialog);
                removeImageDialog.setTitle("Remove image?");
                removeImageDialogYes = (ImageButton) removeImageDialog.findViewById(R.id.remove_image_dialog_yes);
                removeImageDialogNo = (ImageButton) removeImageDialog.findViewById(R.id.remove_image_dialog_no);
                removeImageDialogYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentPost.setImage(null);
                        newPostImage.setVisibility(View.GONE);
                        removeImageDialog.dismiss();
                    }
                });
                removeImageDialogNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeImageDialog.dismiss();
                    }
                });
                removeImageDialog.show();
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
        }
        //Check that request code matches ours:
        else if (requestCode == CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == banterActivity.RESULT_OK) {
                String imagePath = Environment.getExternalStorageDirectory()+ "/" + BANTER_CAMERA_IMAGE_FOLDER_NAME + File.separator + "image" + banterActivity.banterDataModel.imageCounter + supportedImageExtension;
                //Get our saved file into a bitmap object:
                File file = new File(imagePath);

                /* Scale to a thumbnail */
                Bitmap thumbnail = BanterImageFactory.decodeSampledBitmapFromFile(file.getAbsolutePath(), 300, 300);

                /* store in currentPost */
                currentPost.setImage(thumbnail);
                currentPost.setImagePath(imagePath);

                /* show in ui */
                newPostImage.setImageBitmap(thumbnail);
                newPostImage.setVisibility(View.VISIBLE);
            }
        }

        else if (requestCode == REQ_CODE_PICK_IMAGE) {
            if(resultCode == getActivity().RESULT_OK){
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = banterActivity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                /* get a thumbnail */
                //Bitmap original = BanterImageFactory.decodeSampledBitmapFromFile(filePath, 1000, 750);
                Bitmap thumbnail = BanterImageFactory.decodeSampledBitmapFromFile(filePath, 300, 300);

                /* store thumbnail in currentPost */
                currentPost.setImage(thumbnail);

                /* show in ui */
                newPostImage.setImageBitmap(thumbnail);
                newPostImage.setVisibility(View.VISIBLE);
            }
        }
    }

    /* Set the custom action bar to the menu fragment */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.banter_room_actionbar, menu);
    }
    
    public BanterRoomListAdapter getBanterRoomListAdapter(){
        return banterRoomListAdapter;
    }


    /* Class handles the loading of the posts of a room that a user has access to */
    class PostPolling extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... args) {
            BanterRoom current = banterActivity.getBanterDataModel().currentRoom;

            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(BanterSQLContract.TAG_ROOM_ID, Integer.toString(current.getId())));

            /* what is id of last post? if exists */
            if (current.getPosts().size() > 0) {
                params.add(new BasicNameValuePair(BanterSQLContract.TAG_POST_ID,Integer.toString(current.getPosts().get(0).getId())));
            } else {
                params.add(new BasicNameValuePair(BanterSQLContract.TAG_POST_ID,Integer.toString(0)));
            }

            JSONObject json = jsonParser.makeHttpRequest(BanterSQLContract.URL_GET_POSTS,"GET",params);
            try{
                int success = json.getInt(BanterSQLContract.TAG_SUCCESS);
                if (success == 1) {
                    posts = json.getJSONArray(BanterSQLContract.TAG_POSTS);
                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject c = posts.getJSONObject(i);

                        BanterPost banterPost = new BanterPost();
                        banterPost.setId(c.getInt(BanterSQLContract.TAG_ID));
                        banterPost.setName(c.getString(BanterSQLContract.TAG_NAME));
                        banterPost.setLikes(c.getInt(BanterSQLContract.TAG_LIKES));
                        banterPost.setText(c.getString(BanterSQLContract.TAG_TEXT));
                        banterPost.setTime(c.getString(BanterSQLContract.TAG_TIME));
                        banterPost.setUpdateChecked(false);

                        if (c.getInt(BanterSQLContract.TAG_IMAGE) == 1) {
                            banterPost.setImage(BanterImageFactory.getBitmapFromURL("http://vie.nu/banter/banterImages/image" + banterPost.getId() + supportedImageExtension));
                        }

                        banterActivity.getBanterDataModel().currentRoom.getPosts().add(0, banterPost);

                        /* more then max posts in memory? */
                        if (banterActivity.getBanterDataModel().currentRoom.getPosts().size() > MAX_POST_STORED_LOCALLY) {
                            /* First in First Out */
                            banterActivity.getBanterDataModel().currentRoom.getPosts().remove(MAX_POST_STORED_LOCALLY);
                        }

                        if (!banterRoomFragment.isShown()) {
                            createNotification(banterPost.getName(), banterPost.getText());
                        }

                        if(banterRoomFragment.isShown()){
                            if (timer != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getBanterRoomListAdapter().notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if(banterRoomFragment.isShown()){
                if (timer != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getBanterRoomListAdapter().notifyDataSetChanged();
                        }
                    });
                }
            }
        }
    }

    /* Class handles the submition of new post */
    class CreatePost extends AsyncTask<String,String,String> {

        int post_id = -1;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            banterActivity.progressDialog = new ProgressDialog(banterActivity);
            banterActivity.progressDialog.setMessage("Submitting post...");
            banterActivity.progressDialog.setIndeterminate(false);
            banterActivity.progressDialog.setCancelable(false);
            banterActivity.progressDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            /* submit the post */
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(BanterSQLContract.TAG_NAME, currentPost.getName()));
            params.add(new BasicNameValuePair(BanterSQLContract.TAG_TEXT, currentPost.getText()));
            params.add(new BasicNameValuePair(BanterSQLContract.TAG_TIME, currentPost.getTime()));
            params.add(new BasicNameValuePair(BanterSQLContract.TAG_ROOM_ID, Integer.toString(banterActivity.banterDataModel.currentRoom.getId())));
            if (currentPost.getImage() != null) {
                params.add(new BasicNameValuePair(BanterSQLContract.TAG_IMAGE, "1"));
            } else {
                params.add(new BasicNameValuePair(BanterSQLContract.TAG_IMAGE, "0"));
            }
            JSONObject jsonObject = jsonParser.makeHttpRequest(BanterSQLContract.URL_CREATE_NEW_POST,"POST",params);

            try{
                int success = jsonObject.getInt(BanterSQLContract.TAG_SUCCESS);
                if(success == 1){
                    post_id = jsonObject.getInt(BanterSQLContract.TAG_POST_ID);
                    if (currentPost.getImage() != null) {
                        BanterImageFactory.sendImageToServer(currentPost.getImage(), "image" + post_id + ".jpeg");
                    }
                } else {
                    Toast.makeText(getActivity().getBaseContext(), "Sorry, something went wrong..", Toast.LENGTH_SHORT);
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String file_url) {
            banterActivity.progressDialog.dismiss();
            currentPost = new BanterPost();
            currentPost.setImage(null);
        }
    }

    public void beginPostPolling(int interval){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new PostPolling().execute();
            }
        }, 0, interval);
    }

    @Override
    public void onPause() {
        super.onPause();
        for(BanterPost banterPost : banterActivity.banterDataModel.currentRoom.getPosts()){
            banterPost.setUpdateChecked(true);
        }
        /* cancel post polling when user is about to leave fragment */
        //timer.cancel();
        /* timer set to null so background task knows if we have left the fragment */
        //timer = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (timer == null) {
            beginPostPolling(interval);
        }
    }

    /* Class handles the loading of the posts of a room that a user has access to */
    class getLikes extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... args) {
            BanterRoom current = banterActivity.getBanterDataModel().currentRoom;

            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(BanterSQLContract.TAG_ROOM_ID, Integer.toString(current.getId())));

            /* what is id of last post? if exists */
            if (current.getPosts().size() > 0) {
                params.add(new BasicNameValuePair(BanterSQLContract.TAG_POST_ID, Integer.toString(current.getPosts().get(current.getPosts().size() - 1).getId())));
            } else {
                params.add(new BasicNameValuePair(BanterSQLContract.TAG_POST_ID, Integer.toString(0)));
            }

            JSONObject json = jsonParser.makeHttpRequest(BanterSQLContract.URL_GET_LIKES, "GET", params);
            try {
                int success = json.getInt(BanterSQLContract.TAG_SUCCESS);
                if (success == 1) {
                    posts = json.getJSONArray(BanterSQLContract.TAG_POSTS);
                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject c = posts.getJSONObject(i);
                        BanterPost banterPost = current.getPost(c.getInt(BanterSQLContract.TAG_ID));
                        banterPost.setLikes(c.getInt(BanterSQLContract.TAG_LIKES));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (banterRoomFragment.isShown()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getBanterRoomListAdapter().notifyDataSetChanged();
                    }
                });
            }
        }
    }

    public void createNotification(String user, String message) {
        Notification.Builder builder = new Notification.Builder(banterActivity)
                .setSmallIcon(R.drawable.banter_logo2)
                .setContentTitle(user)
                .setContentText(message)
                .setVibrate(new long[]{1000,1000,1000})
                .setLights(Color.GREEN,3000,3000);

        Intent resultIntent = new Intent(banterActivity, BanterActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(banterActivity, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) banterActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    public void animateNewPosts(View view) {
        final RelativeLayout relativeLayout = (RelativeLayout)view;
        ObjectAnimator anim = ObjectAnimator.ofObject(relativeLayout,"anim",new ArgbEvaluator(),Color.parseColor("#137a0c"),Color.parseColor("#f2f2f2"));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                relativeLayout.setBackgroundColor(Integer.parseInt(animation.getAnimatedValue().toString()));
            }
        });
        anim.setDuration(5000);
        anim.setRepeatCount(1);
        anim.start();
    }

    /* Class handles the loading of an image */
    class getImage extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            Bitmap bitmap = BanterImageFactory.getBitmapFromURL("http://vie.nu/bilder/Argentina.jpg");
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (banterRoomFragment.isShown()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newPostImage.setImageBitmap(currentPost.getImage());
                        newPostImage.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }
}
