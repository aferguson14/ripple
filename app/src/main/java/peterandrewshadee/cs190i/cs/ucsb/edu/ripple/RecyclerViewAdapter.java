package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by peterwerner on 6/8/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter {

    ViewGroup mParent;
    List<Broadcast> broadcastList = new ArrayList<>();

    public RecyclerViewAdapter() {
        DatabaseReference dbr = FirebaseHelper.GetInstance().getBroadcastRef();
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    broadcastList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        try {
                            Broadcast bc = ds.getValue(Broadcast.class);
                            Log.d("getBroadcasts", bc.toString());
                            broadcastList.add(bc);
                            notifyDataSetChanged();
                            Log.d("stationslist", "broadcast update");
                        } catch (DatabaseException e) {

                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("getBroadcasts", "db error: " + databaseError.getMessage());
            }
        });
    }

    public void Update(@NonNull List<Broadcast> _broadcastList) {
        broadcastList.clear();
        broadcastList.addAll(_broadcastList);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mParent = parent;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_stationlist, parent, false);
        RecyclerView.ViewHolder holder = new RecyclerViewHolder(view, this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Broadcast b = broadcastList.get(position);

        RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder)holder;
        recyclerViewHolder.broadcast = b;
        recyclerViewHolder.position = position;
        recyclerViewHolder.UpdateFocus(true);

        ((TextView)holder.itemView.findViewById(R.id.stationlist_item_text_song)).setText(b.getSongName());
        ((TextView)holder.itemView.findViewById(R.id.stationlist_item_text_artist)).setText(b.getArtist());
        String caption = "";
        if (b.getUserName() != null)
            caption += b.getUserName();
        else
            caption += b.getId();
        if (b.getListeners() != null) {
            caption += " and " + b.getListeners().size() + " listeners";
        }
        else {
            caption += " and 0 listeners";
        }
        ((TextView)holder.itemView.findViewById(R.id.stationlist_item_text_caption)).setText(caption);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isBroadcasting) {
                    StationState.UpdateListeningStation(new StationState(b));
                    if (MainActivity.currentBroadcastId != null)
                        FirebaseHelper.GetInstance().deleteListener(MainActivity.myUserId, MainActivity.currentBroadcastId);
                    FirebaseHelper.GetInstance().addListener(MainActivity.myUserId, b.getId());
                    MainActivity.currentBroadcastId = b.getId();
                }
                else{
                    StationState.TryClearBroadcastStationWithConfirmationDialog(mParent.getContext());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return broadcastList.size();
    }
}
