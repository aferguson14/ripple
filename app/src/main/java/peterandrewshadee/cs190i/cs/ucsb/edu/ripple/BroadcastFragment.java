package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by peterwerner on 6/5/17.
 */

public class BroadcastFragment extends Fragment implements StationState.BroadcastStationUpdateListener {

    TextView textSong, textArtist, textCaption;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_broadcast, container, false);

        textSong = (TextView) view.findViewById(R.id.broadcaster_song_title);
        textArtist = (TextView) view.findViewById(R.id.broadcaster_song_artist);
        textCaption = (TextView) view.findViewById(R.id.broadcaster_text_caption);

        view.findViewById(R.id.broadcaster_button_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StationState.TryClearBroadcastStationWithConfirmationDialog(getContext());
            }
        });

        if (StationState.broadcastStation != null) {
            OnBroadcastSongUpdate(StationState.broadcastStation);
        }

        return view;
    }

    @Override
    public void OnBroadcastStationStart() {}

    @Override
    public void OnBroadcastSongChange(StationState stationState) {}

    @Override
    public void OnBroadcastSongUpdate(StationState stationState) {
        textSong.setText(stationState.songTitle);
        textArtist.setText(stationState.songArtist);
        textCaption.setText("Broadcasting to " + stationState.listenerIds.size() + " listeners");
    }

    @Override
    public void OnBroadcastStationDie() {}
}
