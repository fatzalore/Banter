package com.example.Banter;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by jacobmeidell on 17.10.14.
 */
public class BanterMenuFragment extends Fragment {

    View banterMenuFragment;
    ListView banterRoomList;
    BanterMenuListAdapter banterMenuListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        setHasOptionsMenu(true);
        getActivity().setContentView(R.layout.bantermenulayout);
        banterMenuFragment = inflater.inflate(R.layout.bantermenulayout,container,false);
        banterRoomList = (ListView) banterMenuFragment.findViewById(R.id.banterroomlist);
        // testing
        String[] test = {"GROUPNAME","16 October 2014, 14:25:55","842"};
        banterMenuListAdapter = new BanterMenuListAdapter(getActivity().getBaseContext(),test);
        banterRoomList.setAdapter(banterMenuListAdapter);
        // testing


        return banterMenuFragment;
    }

    /* Set the custom action bar to the menu fragment */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        menu.clear();
        inflater.inflate(R.menu.bantermenuactionbar,menu);
    }
}
