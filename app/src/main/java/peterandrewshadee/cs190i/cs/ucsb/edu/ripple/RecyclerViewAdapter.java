package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peterwerner on 6/8/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter {

    ViewGroup mParent;
    final List<Broadcast> broadcastList = new ArrayList<>();

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
        String caption = b.getUserName() + " and " + b.getListeners().size() + " listeners";
        ((TextView)holder.itemView.findViewById(R.id.stationlist_item_text_caption)).setText(caption);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StationState.UpdateListeningStation(new StationState(b));
            }
        });
    }

    @Override
    public int getItemCount() {
        return broadcastList.size();
    }
}
