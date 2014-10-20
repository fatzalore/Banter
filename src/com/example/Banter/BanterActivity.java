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
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class BanterActivity extends Activity implements BanterMenuFragment.transactToRoomFragment {

    int currentFragment = -1;
    boolean isNetworkAvailable = false;

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

    Timer timer;

    ProgressDialog progressDialog;
    JSONParser jsonParser = new JSONParser();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        banterDataModel = new BanterDataModel();
        fragmentManager = getFragmentManager();
        loadBanterDataModel();
        checkNetworkConnection();
        banterMenuFragment = new BanterMenuFragment();
        banterRoomFragment = new BanterRoomFragment();

        transact(banterMenuFragment);

    }
    public BanterDataModel getBanterDataModel(){
        return banterDataModel;
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
            FileOutputStream fileOutputStream = getBaseContext().openFileOutput("banter", Context.MODE_PRIVATE);
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
            //deleteFile(getFilesDir().getAbsolutePath() + "banter");
            FileInputStream fileInputStream = new FileInputStream(getBaseContext().getFilesDir().getAbsolutePath() + "/banter");
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
        timer.cancel();
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
    private boolean validateRoomInput(){
        if(dialogRoomName.getText().toString().length() > 0 &&
           dialogPassword.getText().toString().length() > 0 &&
           dialogRepeatPassword.getText().toString().length() > 0 &&
           dialogAdminPassword.getText().toString().length() > 0 &&
           dialogRepeatAdminPassword.getText().toString().length() > 0 &&
           dialogAdminPassword.getText().toString().equals(dialogRepeatAdminPassword.getText().toString()) &&
           dialogPassword.getText().toString().equals(dialogRepeatPassword.getText().toString())){
           return true;
        }
        return false;
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
                try {
                    if(validateRoomInput()) {
                        banterMenuFragment.createRooms();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getBaseContext(),"Sorry, some fields are missing and/or passwords don't match",Toast.LENGTH_LONG).show();
                    }
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void checkNetworkConnection(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isNetworkAvailable = isNetworkAvailable();
            }
        }, 0, 5000);
    }
}


