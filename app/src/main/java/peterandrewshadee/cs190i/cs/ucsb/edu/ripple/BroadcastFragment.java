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

public class BroadcastFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_broadcast, container, false);

        TextView songTitleView = (TextView) view.findViewById(R.id.broadcaster_song_title);
        TextView songArtistView = (TextView) view.findViewById(R.id.broadcaster_song_artist);

        songTitleView.setText(StationState.broadcastStation.songTitle);
        songArtistView.setText(StationState.broadcastStation.songArtist);
        // Toast.makeText(getContext(), StationState.broadcastStation.songTitle, Toast.LENGTH_SHORT).show();
        return view;
    }
}
