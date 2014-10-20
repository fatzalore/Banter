package com.example.Banter;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BanterActivity extends Activity implements BanterMenuFragment.transactToRoomFragment {

    int currentFragment = -1;
    boolean isNetworkAvailable;

    BanterMenuFragment banterMenuFragment;
    BanterRoomFragment banterRoomFragment;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    BanterDataModel banterDataModel;

    EditText dialogRoomName;
    EditText dialogPassword;
    EditText dialogRepeatPassword;
    EditText dialogAdminPassword;
    EditText dialogRepeatAdminPassword;
    ImageButton dialogYes;
    ImageButton dialogNo;


    ProgressDialog progressDialog;
    JSONParser jsonParser = new JSONParser();
    static String URL_GET_ALL_ROOMS = "http://vie.nu/banter/getAllRooms.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ROOMS= "rooms";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_ADMINPASSWORD = "admin_password";
    private static final String TAG_DATE_CREATED = "date_created";
    private static final String TAG_LAST_UPDATED = "last_updated";
    private static final String TAG_POSTS = "posts";

    JSONArray rooms = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        banterDataModel = new BanterDataModel();
        fragmentManager = getFragmentManager();
        loadBanterDataModel();
        Log.e("@@@@@@@@@@@@@@@","" + banterDataModel.banterRooms.size());
        banterMenuFragment = new BanterMenuFragment();
        banterRoomFragment = new BanterRoomFragment();
        isNetworkAvailable = isNetworkAvailable();

        if(isNetworkAvailable) {
            new LoadRooms().execute();
        }
        transact(banterMenuFragment);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.add:
                if(isNetworkAvailable){
                    createNewRoomDialog();
                } else {
                    Toast.makeText(this,"Sorry, you are not connected to the internet",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.search:
                toggleSearchField();
                break;
            case R.id.options:
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }
    /* Transaction between fragments */
    public void transact(Fragment to) {
        currentFragment++;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content, to);
        fragmentTransaction.addToBackStack("MenuFragment");
        fragmentTransaction.commit();
    }

    public void transactToRoomFragment() {
        transact(banterRoomFragment);
    }

    private void saveBanterDataModel(){
        try {
            FileOutputStream fileOutputStream = openFileOutput("banter", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(banterDataModel);
            objectOutputStream.close();
            Log.e("SAVING",""+banterDataModel.banterRooms.size());

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void loadBanterDataModel(){
        try {
            FileInputStream fileInputStream = new FileInputStream(getFilesDir().getAbsolutePath() + "/banter");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            banterDataModel = (BanterDataModel)objectInputStream.readObject();
            objectInputStream.close();
            Log.e("LOADING",""+banterDataModel.banterRooms.size());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        saveBanterDataModel();
    }

    @Override
    public void onBackPressed() {
        currentFragment--;
        if(currentFragment < 0){
            finish();
        }
        super.onBackPressed();
    }
    /* Creates the pop up dialog to input information about new Banter Room */
    private void createNewRoomDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.banter_new_room_dialog);
        dialog.setTitle("Create new banter room");
        dialogRoomName = (EditText) dialog.findViewById(R.id.new_room_dialog_room_name);
        dialogPassword = (EditText) dialog.findViewById(R.id.new_room_dialog_password);
        dialogRepeatPassword = (EditText) dialog.findViewById(R.id.new_room_dialog_repeat_password);
        dialogAdminPassword = (EditText) dialog.findViewById(R.id.new_room_dialog_admin_password);
        dialogRepeatAdminPassword = (EditText) dialog.findViewById(R.id.new_room_dialog_repeat_admin_password);
        dialogYes = (ImageButton) dialog.findViewById(R.id.new_room_dialog_yes);
        dialogNo = (ImageButton) dialog.findViewById(R.id.new_room_dialog_no);
        dialogYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO IMPLEMENT SAVING
                /* FOR TESTING ONLY */
                try {
                    BanterRoom banterRoom = new BanterRoom(dialogRoomName.getText().toString());
                    banterDataModel.addBanterRoom(banterRoom);
                    dialog.dismiss();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        dialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void toggleSearchField(){
        EditText searchField = (EditText) banterMenuFragment.getBanterMenuFragment().findViewById(R.id.menu_search_field);
        if(searchField.getVisibility() == View.VISIBLE){
            searchField.setVisibility(View.GONE);
        } else {
            searchField.setVisibility(View.VISIBLE);
        }

    }

    /* Class handles the loading of the rooms that a user has access to */
    class LoadRooms extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(BanterActivity.this);
            progressDialog.setMessage("Loading rooms...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... args) {
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = jsonParser.makeHttpRequest(URL_GET_ALL_ROOMS,"GET",params);
            try{
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    rooms = json.getJSONArray(TAG_ROOMS);
                    for (int i = 0; i < rooms.length(); i++) {
                        JSONObject c = rooms.getJSONObject(i);
                        BanterRoom banterRoom = new BanterRoom(c.getString(TAG_NAME));
                        banterRoom.setId(c.getInt(TAG_ID));
                        banterRoom.setAdminpassword(c.getString(TAG_ADMINPASSWORD));
                        banterRoom.setDateCreated(c.getString(TAG_DATE_CREATED));
                        banterRoom.setLastUpdated(c.getString(TAG_LAST_UPDATED));
                        banterRoom.setPostAmount(c.getInt(TAG_POSTS));
                        banterRoom.setPassword(c.getString(TAG_PASSWORD));
                        banterDataModel.addBanterRoom(banterRoom);
                        Log.e("@@@@@@@@@@@@@@@","" + banterDataModel.banterRooms.size());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String file_url) {
            progressDialog.dismiss();
            banterMenuFragment.getBanterMenuListAdapter().notifyDataSetChanged();
        }

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}


