package com.example.Banter;

import android.app.*;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jacobmeidell on 17.10.14.
 */
public class BanterMenuFragment extends Fragment {

    private View banterMenuFragment;
    private ListView banterRoomList;
    private BanterMenuListAdapter banterMenuListAdapter;
    private BanterActivity banterActivity;
    private JSONArray roomObject = null;
    private JSONArray searchArray = null;
    private ListView searchRoomDialogList;
    private EditText searchRoomDialogSearchField;
    private ImageButton searchRoomDialogButton;
    private ArrayList<BanterRoom> searchResultArray = new ArrayList<BanterRoom>();
    private BanterMenuListAdapter searchDialogAdapter;
    private EditText passwordDialogInput;
    Dialog dialog;
    private AlertDialog passwordDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        setHasOptionsMenu(true);
        banterActivity = (BanterActivity)getActivity();
        banterActivity.setContentView(R.layout.banter_menu_layout);
        banterMenuFragment = inflater.inflate(R.layout.banter_menu_layout,container,false);
        banterRoomList = (ListView) banterMenuFragment.findViewById(R.id.banter_room_list);
        // testing
        banterMenuListAdapter = new BanterMenuListAdapter(getActivity().getBaseContext(),banterActivity.banterDataModel.getBanterRooms());
        banterRoomList.setAdapter(banterMenuListAdapter);
        // testing

        if(banterActivity.isNetworkAvailable) {
            if(banterActivity.banterDataModel.banterRooms.size() > 0){
                loadRooms();
            }
        }

        /* Listen for item clicks */
        banterRoomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BanterActivity banterActivity = (BanterActivity) getActivity();
                banterActivity.banterDataModel.currentRoom = banterActivity.banterDataModel.banterRooms.get(position);
                banterActivity.transactToRoomFragment();
            }
        });

        return banterMenuFragment;
    }

    /* Set the custom action bar to the menu fragment */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        menu.clear();
        inflater.inflate(R.menu.banter_menu_actionbar,menu);
    }
    /* Interface to make activity transacto to BanterRoomFragment */
    public interface transactToRoomFragment {
        public void transactToRoomFragment();
    }
    public View getBanterMenuFragment(){
        return banterMenuFragment;
    }
    public BanterMenuListAdapter getBanterMenuListAdapter(){
        return banterMenuListAdapter;
    }

    public void createRooms(){
        new CreateRooms().execute();
    }
    public void loadRooms(){
        new LoadRooms().execute();
    }


    /* Class handles the loading of the rooms that a user has access to */
    class CreateRooms extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            banterActivity.progressDialog = new ProgressDialog(getActivity());
            banterActivity.progressDialog.setMessage("Creating Room...");
            banterActivity.progressDialog.setIndeterminate(false);
            banterActivity.progressDialog.setCancelable(false);
            banterActivity.progressDialog.show();
        }
        @Override
        protected String doInBackground(String... args) {
            String name = banterActivity.dialogRoomName.getText().toString();
            String password = banterActivity.dialogPassword.getText().toString();
            String adminPassword = banterActivity.dialogAdminPassword.getText().toString();
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date now = new Date();
            String dateCreated = sdfDate.format(now);

            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(BanterSQLContract.TAG_NAME,name));
            params.add(new BasicNameValuePair(BanterSQLContract.TAG_PASSWORD,password));
            params.add(new BasicNameValuePair(BanterSQLContract.TAG_ADMINPASSWORD,adminPassword));
            params.add(new BasicNameValuePair(BanterSQLContract.TAG_DATE_CREATED, dateCreated));
            JSONObject jsonObject = banterActivity.jsonParser.makeHttpRequest(BanterSQLContract.URL_CREATE_NEW_ROOM,"POST",params);

            try{
                int success = jsonObject.getInt(BanterSQLContract.TAG_SUCCESS);
                if(success == 1){
                    BanterRoom banterRoom = new BanterRoom(name);
                    banterRoom.setId(jsonObject.getInt(BanterSQLContract.TAG_ID));
                    banterActivity.banterDataModel.addBanterRoom(banterRoom);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String file_url) {
            banterActivity.progressDialog.dismiss();
        }

    }


    /* Class handles the loading of the rooms that a user has access to */
    class LoadRooms extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            banterActivity.progressDialog = new ProgressDialog(getActivity());
            banterActivity.progressDialog.setMessage("Loading rooms...");
            banterActivity.progressDialog.setIndeterminate(false);
            banterActivity.progressDialog.setCancelable(false);
            banterActivity.progressDialog.show();
        }
        @Override
        protected String doInBackground(String... args) {
            ArrayList<BanterRoom> temp = banterActivity.banterDataModel.banterRooms;
            int[] roomIDs = new int[temp.size()];
            for(int i = 0; i < temp.size(); i++){
                roomIDs[i] = temp.get(i).getId();
                Log.e("@@@@@@@@@@@","ACCESS TO ID " + roomIDs[i]);
            }
            for(int i = 0; i < roomIDs.length; i++){
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(BanterSQLContract.TAG_ID,Integer.toString(roomIDs[i])));
                JSONObject json = banterActivity.jsonParser.makeHttpRequest(BanterSQLContract.URL_GET_ROOM,"GET",params);
                try{
                    int success = json.getInt(BanterSQLContract.TAG_SUCCESS);
                    if (success == 1) {
                        roomObject = json.getJSONArray(BanterSQLContract.TAG_ROOM);
                        BanterRoom banterRoom = new BanterRoom(roomObject.getJSONObject(0).getString(BanterSQLContract.TAG_NAME));
                        banterRoom.setName(roomObject.getJSONObject(0).getString(BanterSQLContract.TAG_NAME));
                        banterRoom.setId(roomObject.getJSONObject(0).getInt(BanterSQLContract.TAG_ID));
                        Log.e("@@@@@@@@@@", "GETTING ID " + banterRoom.getId());
                        banterRoom.setAdminpassword(roomObject.getJSONObject(0).getString(BanterSQLContract.TAG_ADMINPASSWORD));
                        banterRoom.setDateCreated(roomObject.getJSONObject(0).getString(BanterSQLContract.TAG_DATE_CREATED));
                        banterRoom.setLastUpdated(roomObject.getJSONObject(0).getString(BanterSQLContract.TAG_LAST_UPDATED));
                        banterRoom.setPostAmount(roomObject.getJSONObject(0).getInt(BanterSQLContract.TAG_POSTS));
                        banterRoom.setPassword(roomObject.getJSONObject(0).getString(BanterSQLContract.TAG_PASSWORD));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String file_url) {
            banterActivity.progressDialog.dismiss();
            getBanterMenuListAdapter().notifyDataSetChanged();
        }

    }

    class SearchRooms extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String...args) {
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(BanterSQLContract.TAG_NAME,searchRoomDialogSearchField.getText().toString()));
            Log.e("@@@@@@@@@", "SEARCH FOR " + searchRoomDialogSearchField.getText().toString());
            JSONObject json = banterActivity.jsonParser.makeHttpRequest(BanterSQLContract.URL_GET_ROOM,"GET",params);
            try{
                int success = json.getInt(BanterSQLContract.TAG_SUCCESS);
                if(success == 1){
                    searchArray = json.getJSONArray(BanterSQLContract.TAG_ROOM);
                    BanterRoom banterRoom = new BanterRoom(searchArray.getJSONObject(0).getString(BanterSQLContract.TAG_NAME));
                    banterRoom.setId(searchArray.getJSONObject(0).getInt(BanterSQLContract.TAG_ID));
                    Log.e("@@@@@@@@@", "OBJECT RETURN " + banterRoom.getId() + " NAME " + banterRoom.getName());
                    banterRoom.setAdminpassword(searchArray.getJSONObject(0).getString(BanterSQLContract.TAG_ADMINPASSWORD));
                    banterRoom.setDateCreated(searchArray.getJSONObject(0).getString(BanterSQLContract.TAG_DATE_CREATED));
                    banterRoom.setLastUpdated(searchArray.getJSONObject(0).getString(BanterSQLContract.TAG_LAST_UPDATED));
                    banterRoom.setPostAmount(searchArray.getJSONObject(0).getInt(BanterSQLContract.TAG_POSTS));
                    banterRoom.setPassword(searchArray.getJSONObject(0).getString(BanterSQLContract.TAG_PASSWORD));
                    searchResultArray.add(banterRoom);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            searchDialogAdapter.notifyDataSetChanged();
                        }
                    });
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public void createSearchDialog(){
        dialog = new Dialog(banterActivity);
        dialog.setContentView(R.layout.banter_search_room_dialog);
        dialog.setTitle("Search rooms...");
        searchRoomDialogList = (ListView) dialog.findViewById(R.id.menu_search_list);
        searchDialogAdapter = new BanterMenuListAdapter(getActivity(),searchResultArray);
        searchRoomDialogList.setAdapter(searchDialogAdapter);
        searchRoomDialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!banterActivity.banterDataModel.isBanterRoomInList(searchResultArray.get(position))) {
                    /* prompt a enter password dialog */
                    hasUserAccsess(position);
                }
            }
        });
        searchRoomDialogSearchField = (EditText) dialog.findViewById(R.id.menu_search_field);
        searchRoomDialogButton = (ImageButton) dialog.findViewById(R.id.menu_search_search);
        searchRoomDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchRoomDialogSearchField.getText().toString().length() > 0){
                    new SearchRooms().execute();
                }
            }
        });
        dialog.show();

    }

    public void hasUserAccsess(final int position){
        passwordDialogInput = new EditText((getActivity()));
        passwordDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Enter password")
                .setView(passwordDialogInput)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (passwordDialogInput.getText().toString().equals(searchResultArray.get(position).getPassword())) {
                            banterActivity.banterDataModel.banterRooms.add(searchResultArray.get(position));
                            getBanterMenuListAdapter().notifyDataSetChanged();
                            passwordDialog.dismiss();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(),"Sorry, wrong password", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Abort", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        passwordDialog.dismiss();
                    }
                }).show();
    }
}
