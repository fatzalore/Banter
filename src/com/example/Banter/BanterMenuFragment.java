package com.example.Banter;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by jacobmeidell on 17.10.14.
 */
public class BanterMenuFragment extends Fragment {

    private View banterMenuFragment;
    private ListView banterRoomList;
    private BanterMenuListAdapter banterMenuListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        setHasOptionsMenu(true);
        BanterActivity banterActivity = (BanterActivity)getActivity();
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



}
