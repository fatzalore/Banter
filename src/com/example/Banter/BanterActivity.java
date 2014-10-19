package com.example.Banter;

import android.app.*;
import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BanterActivity extends Activity implements BanterMenuFragment.transactToRoomFragment {

    int currentFragment = -1;
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
    JSONArray rooms = null;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        banterDataModel = new BanterDataModel();
        fragmentManager = getFragmentManager();
        banterMenuFragment = new BanterMenuFragment();
        banterRoomFragment = new BanterRoomFragment();
        new LoadRooms().execute();
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
                createNewRoomDialog();
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
                    banterMenuFragment.addBanterRoomToList(banterRoom);
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
    class LoadRooms extends AsyncTask<String,String, String> {

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
                    Log.e("@@@@@@@@@@@@@", " " +rooms.length());
                    for (int i = 0; i < rooms.length(); i++) {
                        JSONObject c = rooms.getJSONObject(i);
                        String name = c.getString(TAG_NAME);
                        banterDataModel.addBanterRoom(new BanterRoom(name));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String file_url) {
            progressDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for(BanterRoom banterRoom : banterDataModel.getBanterRooms()){
                        banterMenuFragment.addBanterRoomToList(banterRoom);
                    }
                }
            });
        }

    }
}


