package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by peterwerner on 6/1/17.
 */

public class StationsListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_stationslist, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.stationlist_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setItemAnimator(null);


        // TODO: remove this test code + replace with DB listener

        // TEST CODE
        // TEST CODE
        // TEST CODE

        List<Broadcast> bcastlist = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Broadcast b = new Broadcast(i + "");
            if (i%3 == 0) {
                b.setArtist("Rostam");
                b.setSongName("Gravity Don't Pull Me");
                b.setUserName("Peter Werner");
            } else if (i%3 == 1) {
                b.setArtist("Kendrick Lamar");
                b.setSongName("BLOOD.");
                b.setUserName("Shadee Barzin");
            } else {
                b.setArtist("Tourist");
                b.setSongName("Placid Acid");
                b.setUserName("Andrew Ferguson");
            }
            bcastlist.add(b);
        }

        mRecyclerAdapter.Update(bcastlist);

        // TEST CODE
        // TEST CODE
        // TEST CODE


        return view;
    }

}
