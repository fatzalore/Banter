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

    View banterMenuFragment;
    ListView banterRoomList;
    BanterMenuListAdapter banterMenuListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        setHasOptionsMenu(true);
        getActivity().setContentView(R.layout.banter_menu_layout);
        banterMenuFragment = inflater.inflate(R.layout.banter_menu_layout,container,false);
        banterRoomList = (ListView) banterMenuFragment.findViewById(R.id.banter_room_list);
        // testing
        ArrayList<BanterRoom> testdata = new ArrayList<BanterRoom>();
        testdata.add(new BanterRoom("NTNUI A-LAG"));
        banterMenuListAdapter = new BanterMenuListAdapter(getActivity().getBaseContext(),testdata);
        banterRoomList.setAdapter(banterMenuListAdapter);
        // testing

        /* Listen for item clicks */
        banterRoomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BanterActivity banterActivity = (BanterActivity) getActivity();
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
}
