package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by peterwerner on 6/8/17.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder implements StationState.ListeningStationUpdateListener {

    public Broadcast broadcast = null;
    public Integer position = null;
    private boolean isFocused = false, wasFocused = false;
    private RecyclerViewAdapter parent;

    public RecyclerViewHolder(View itemView, RecyclerViewAdapter parent) {
        super(itemView);
        this.parent = parent;
        StationState.SubscribeToListeningStationUpdates(this);
    }

    void UpdateFocus () {
        UpdateFocus(false);
    }
    void UpdateFocus (boolean forceUpdate) {
        isFocused = broadcast != null
                && StationState.listeningStation != null
                && StationState.listeningStation.userId != null
                && StationState.listeningStation.userId == broadcast.getId();

        if (forceUpdate || isFocused != wasFocused && position != null) {
            int colorAccent = isFocused ? ContextCompat.getColor(itemView.getContext(), R.color.colorAccent) : Color.parseColor("#f9f9f9");
            int colorText = isFocused ? Color.WHITE : ContextCompat.getColor(itemView.getContext(), R.color.colorPrimaryDark);

            itemView.findViewById(R.id.stationlist_item_container).setBackgroundColor(colorAccent);
//            ((TextView)itemView.findViewById(R.id.stationlist_item_text_dot)).setTextColor(colorText);
            ((TextView)itemView.findViewById(R.id.stationlist_item_text_song)).setTextColor(colorText);
//            ((TextView)itemView.findViewById(R.id.stationlist_item_text_artist)).setTextColor(colorText);
            ((TextView)itemView.findViewById(R.id.stationlist_item_text_caption)).setTextColor(colorText);

            if (isFocused != wasFocused && position != null) {
                try {
                    parent.notifyItemChanged(position);
                } catch (IllegalStateException e) {}
            }
        }
        wasFocused = isFocused;
    }

    @Override
    public void OnListeningStationStart() {
        UpdateFocus();
    }

    @Override
    public void OnListeningSongChange(StationState stationState) {
        UpdateFocus();
    }

    @Override
    public void OnListeningSongUpdate(StationState stationState) {}

    @Override
    public void OnListeningStationDie() {
        UpdateFocus();
    }
}
