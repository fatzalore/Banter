package com.example.Banter;

import android.app.*;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private Dialog searchDialog;
    private Dialog passwordDialog;
    private ImageButton passwordDialogYes;
    private ImageButton passwordDialogNo;
    private Dialog leaveRoomDialog;
    private ImageButton leaveRoomDialogYes;
    private ImageButton leaveRoomDialogNo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        setHasOptionsMenu(true);
        banterActivity = (BanterActivity)getActivity();
        banterActivity.setContentView(R.layout.banter_menu_layout);
        banterMenuFragment = inflater.inflate(R.layout.banter_menu_layout,container,false);
        banterRoomList = (ListView) banterMenuFragment.findViewById(R.id.banter_room_list);

        banterActivity.setTitle("Your rooms");
        banterActivity.getActionBar().setDisplayHomeAsUpEnabled(false);

        banterMenuListAdapter = new BanterMenuListAdapter(getActivity().getBaseContext(),banterActivity.banterDataModel.getBanterRooms());
        banterRoomList.setAdapter(banterMenuListAdapter);

        if(banterActivity.isNetworkAvailable) {
            if(banterActivity.banterDataModel.banterRooms.size() > 0){
                loadRooms();
            }
        }
        setBanterRoomListOnClickListener();
        setBanterRoomListOnLongClickListener();



        return banterMenuFragment;
    }
    private void setBanterRoomListOnClickListener(){
         /* Listen for item clicks */
        banterRoomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BanterActivity banterActivity = (BanterActivity) getActivity();
                banterActivity.banterDataModel.currentRoom = banterActivity.banterDataModel.banterRooms.get(position);
                banterActivity.banterDataModel.banterRooms.get(position).setUpdateChecked(true);
                banterActivity.transactToRoomFragment();
            }
        });
    }
    private void setBanterRoomListOnLongClickListener(){
        banterRoomList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                leaveRoomDialog = new Dialog(banterActivity);
                leaveRoomDialog.setContentView(R.layout.banter_menu_leave_room_dialog);
                leaveRoomDialog.setTitle("Leave room?");
                leaveRoomDialogYes = (ImageButton)leaveRoomDialog.findViewById(R.id.leave_room_yes);
                leaveRoomDialogNo = (ImageButton)leaveRoomDialog.findViewById(R.id.leave_room_no);
                leaveRoomDialogYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(banterActivity,"Left room " + banterActivity.banterDataModel.banterRooms.get(pos).getName(),Toast.LENGTH_SHORT);
                        banterActivity.banterDataModel.banterRooms.remove(pos);
                        banterMenuListAdapter.notifyDataSetChanged();
                        leaveRoomDialog.dismiss();
                    }
                });
                leaveRoomDialogNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leaveRoomDialog.dismiss();
                    }
                });
                leaveRoomDialog.show();
                return true;
            }
        });
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
        protected String doInBackground(String... args) {

            for(int i = 0; i <  banterActivity.banterDataModel.banterRooms.size(); i++){
                BanterRoom banterRoom = banterActivity.banterDataModel.banterRooms.get(i);
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(BanterSQLContract.TAG_ID,Integer.toString(banterRoom.getId())));
                JSONObject json = banterActivity.jsonParser.makeHttpRequest(BanterSQLContract.URL_GET_ROOM,"GET",params);
                try{
                    int success = json.getInt(BanterSQLContract.TAG_SUCCESS);
                    if (success == 1) {
                        roomObject = json.getJSONArray(BanterSQLContract.TAG_ROOM);
                        banterRoom.setName(roomObject.getJSONObject(0).getString(BanterSQLContract.TAG_NAME));
                        banterRoom.setId(roomObject.getJSONObject(0).getInt(BanterSQLContract.TAG_ID));
                        banterRoom.setAdminpassword(roomObject.getJSONObject(0).getString(BanterSQLContract.TAG_ADMINPASSWORD));
                        banterRoom.setDateCreated(roomObject.getJSONObject(0).getString(BanterSQLContract.TAG_DATE_CREATED));
                        if (!roomObject.getJSONObject(0).getString(BanterSQLContract.TAG_LAST_UPDATED).equals(banterRoom.getLastUpdated())) {
                            banterRoom.setUpdateChecked(false);
                            banterRoom.setLastUpdated(roomObject.getJSONObject(0).getString(BanterSQLContract.TAG_LAST_UPDATED));
                            banterRoom.setTimeSinceLastUpdated();
                        }
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
            getBanterMenuListAdapter().notifyDataSetChanged();
        }

    }


    class SearchRooms extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            banterActivity.progressDialog = new ProgressDialog(getActivity());
            banterActivity.progressDialog.setMessage("Searching for room...");
            banterActivity.progressDialog.setIndeterminate(false);
            banterActivity.progressDialog.setCancelable(true);
            banterActivity.progressDialog.show();

        }

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
        @Override
        protected void onPostExecute(String file_url) {
            banterActivity.progressDialog.dismiss();
        }

    }

    public void createSearchDialog(){
        searchDialog = new Dialog(banterActivity);
        searchDialog.setContentView(R.layout.banter_search_room_dialog);
        searchDialog.setTitle("Search rooms...");
        searchRoomDialogList = (ListView) searchDialog.findViewById(R.id.menu_search_list);
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
        searchRoomDialogSearchField = (EditText) searchDialog.findViewById(R.id.menu_search_field);
        searchRoomDialogButton = (ImageButton) searchDialog.findViewById(R.id.menu_search_search);
        searchRoomDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchRoomDialogSearchField.getText().toString().length() > 0){
                    if(searchResultArray.size() > 0){
                        searchResultArray.clear();
                        searchDialogAdapter.notifyDataSetChanged();
                    }
                    new SearchRooms().execute();
                }
            }
        });
        searchDialog.show();

    }

    public void hasUserAccsess(final int position) {
        passwordDialog = new Dialog(banterActivity);
        passwordDialog.setContentView(R.layout.banter_search_input_password);
        passwordDialog.setTitle("Input password");
        passwordDialogYes = (ImageButton) passwordDialog.findViewById(R.id.password_dialog_input_yes);
        passwordDialogNo = (ImageButton) passwordDialog.findViewById(R.id.password_dialog_input_no);
        passwordDialogInput = (EditText) passwordDialog.findViewById(R.id.password_dialog_input);


        passwordDialogYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordDialogInput.getText().toString().equals(searchResultArray.get(position).getPassword())) {
                    banterActivity.banterDataModel.banterRooms.add(searchResultArray.get(position));
                    getBanterMenuListAdapter().notifyDataSetChanged();
                    passwordDialog.dismiss();
                    searchDialog.dismiss();
                } else {
                    Toast.makeText(banterActivity,"Sorry, wrong password",Toast.LENGTH_SHORT).show();
                }
            }
        });

        passwordDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordDialog.dismiss();
            }
        });
        passwordDialog.show();
    }

    public Dialog getSearchDialog(){
        return searchDialog;
    }
    public void setSearchDialog(Dialog dialog){
        searchDialog = dialog;
    }

}
