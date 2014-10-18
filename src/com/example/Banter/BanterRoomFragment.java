package com.example.Banter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by jacobmeidell on 17.10.14.
 */
public class BanterRoomFragment extends Fragment {

    View banterRoomFragment;
    ListView banterRoomList;
    BanterRoomListAdapter banterRoomListAdapter;

    //BanterRoom currentRoom; // not implemented yet

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        setHasOptionsMenu(true);
        getActivity().setContentView(R.layout.banter_room_layout);
        banterRoomFragment = inflater.inflate(R.layout.banter_room_layout,container,false);
        banterRoomList = (ListView) banterRoomFragment.findViewById(R.id.room_chat_list);

        /* TEST DATA! */
        ArrayList<BanterPost> testData = new ArrayList<BanterPost>();
        BanterPost testPost1 = new BanterPost();
        testPost1.setName("Ali baba");
        testPost1.setLikes(9);
        testPost1.setText("Lorem ipsum .... blablablabla Banter i massevis");
        testPost1.setTimeAndDateToNow();
        BanterPost testPost2 = new BanterPost();
        testPost2.setName("Venn av ali baba");
        testPost2.setLikes(-11);
        testPost2.setTimeAndDateToNow();
        testPost2.setText("Lorem ipsum .... blablablabla Banter i massevis Lorem ipsum .... blablablabla Banter i massevis Lorem ipsum .... blablablabla Banter i massevis Lorem ipsum .... blablablabla Banter i massevis Lorem ipsum .... blablablabla Banter i massevis Lorem ipsum .... blablablabla Banter i massevis");
        testData.add(testPost1);
        testData.add(testPost2);
        /* TEST DATA end*/

        banterRoomListAdapter = new BanterRoomListAdapter(getActivity().getBaseContext(), testData);
        banterRoomList.setAdapter(banterRoomListAdapter);
        /* TEST DATA end */

        return banterRoomFragment;
    }

    /* Set the custom action bar to the menu fragment */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        menu.clear();
        inflater.inflate(R.menu.banter_room_actionbar,menu);
    }
}
