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

public class BanterActivity extends Activity implements BanterMenuFragment.transactToRoomFragment, Application.ActivityLifecycleCallbacks {

    private int resumed;
    private int paused;
    private int started;
    private int stopped;


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
        getApplication().registerActivityLifecycleCallbacks(this);

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
                    Toast.makeText(this,"Sorry, no internet connection available",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.search:
                toggleSearchDialog();
                break;
            case R.id.options:
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(this, findViewById(R.id.options));
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.banter_popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.options_leave_room:
                                transact(banterMenuFragment);
                                break;
                        }
                        return true;
                    }
                });
                popup.show(); //showing popup menu
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
    protected void onResume(){
        super.onResume();
        checkNetworkConnection();
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

    private void toggleSearchDialog(){
        if(banterMenuFragment.getSearchDialog() == null || !banterMenuFragment.getSearchDialog().isShowing()){
            banterMenuFragment.createSearchDialog();
        } else {
            banterMenuFragment.getSearchDialog().dismiss();
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

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
        if(banterRoomFragment.timer != null) {
            banterRoomFragment.timer.cancel();
            Log.e("@@@@@@@@@@", "CHANGING TIME INTERVAL FOR POST POLLING TO 5 sek");
            banterRoomFragment.beginPostPolling(5000);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
        if(banterRoomFragment.timer != null) {
            banterRoomFragment.timer.cancel();
            banterRoomFragment.beginPostPolling(30000);
        }
        Log.e("@@@@@@@@@@@","CHANGING TIME INTERVAL FOR POST POLLING TO 30 sek");
        android.util.Log.e("test", "application is in foreground: " + (resumed > paused));
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        android.util.Log.e("test", "application is visible: " + (started > stopped));
    }

    // And these two public static functions
    public boolean isApplicationVisible() {
        if(started > stopped){
            return true;
        }
        return false;
    }
}


