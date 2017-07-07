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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by peterwerner on 6/1/17.
 */

public class StationsListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerAdapter;

    TextView stationsAvailableText;
    List broadcastList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_stationslist, container, false);


        Log.d("StationsListFragment", "onCreateView");
        mRecyclerView = (RecyclerView) view.findViewById(R.id.stationlist_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setItemAnimator(null);

        stationsAvailableText = (TextView) view.findViewById(R.id.stationlist_none_available_text);
        broadcastList = new ArrayList<>();

        //db listener simply for Text display if stations available
        DatabaseReference dbr = FirebaseHelper.GetInstance().getBroadcastRef();
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Log.d("StationsListFragment", "if statement");
                    broadcastList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        try {
                            Broadcast bc = ds.getValue(Broadcast.class);
                            if (bc != null) {
                                broadcastList.add(bc);
                            }
                        } catch (DatabaseException e) {

                        }
                    }
                    if(broadcastList.isEmpty()){
                        stationsAvailableText.setVisibility(View.VISIBLE);
                    }
                    else{
                        stationsAvailableText.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("getBroadcasts", "db error: " + databaseError.getMessage());
            }
        });

        return view;
    }


}
