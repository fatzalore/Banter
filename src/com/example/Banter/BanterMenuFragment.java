package com.example.Banter;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jacobmeidell on 17.10.14.
 */
public class BanterMenuFragment extends Fragment {

    private View banterMenuFragment;
    private ListView banterRoomList;
    private BanterMenuListAdapter banterMenuListAdapter;
    private BanterActivity banterActivity;
    private JSONArray rooms = null;



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
                    //new LoadRooms().execute();
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
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = banterActivity.jsonParser.makeHttpRequest(BanterSQLContract.URL_GET_ALL_ROOMS,"GET",params);
            try{
                int success = json.getInt(BanterSQLContract.TAG_SUCCESS);
                if (success == 1) {
                    rooms = json.getJSONArray(BanterSQLContract.TAG_ROOMS);
                    for (int i = 0; i < rooms.length(); i++) {
                        JSONObject c = rooms.getJSONObject(i);

                        BanterRoom b = new BanterRoom(c.getString(BanterSQLContract.TAG_NAME));
                        b.setId(c.getInt(BanterSQLContract.TAG_ID));

                        for(BanterRoom banterRoom : banterActivity.banterDataModel.banterRooms){
                            if(banterRoom.equals(b)){
                                banterRoom.setName(c.getString(BanterSQLContract.TAG_NAME));
                                banterRoom.setId(c.getInt(BanterSQLContract.TAG_ID));
                                banterRoom.setAdminpassword(c.getString(BanterSQLContract.TAG_ADMINPASSWORD));
                                banterRoom.setDateCreated(c.getString(BanterSQLContract.TAG_DATE_CREATED));
                                banterRoom.setLastUpdated(c.getString(BanterSQLContract.TAG_LAST_UPDATED));
                                banterRoom.setPostAmount(c.getInt(BanterSQLContract.TAG_POSTS));
                                banterRoom.setPassword(c.getString(BanterSQLContract.TAG_PASSWORD));
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
            banterActivity.progressDialog.dismiss();
            getBanterMenuListAdapter().notifyDataSetChanged();
        }

    }

}
