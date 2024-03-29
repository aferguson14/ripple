package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by peterwerner on 6/5/17.
 */

public class BroadcastFragment extends Fragment implements StationState.BroadcastStationUpdateListener {

    TextView textSongAndArtist, textCaption;
    ImageView albumArt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_broadcast, container, false);

        albumArt = (ImageView) view.findViewById(R.id.broadcaster_album_art);
        textSongAndArtist = (TextView) view.findViewById(R.id.broadcaster_song_title_and_artist);
        textSongAndArtist.setSelected(true);
//        textArtist = (TextView) view.findViewById(R.id.broadcaster_song_artist);
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

        StationState.SubscribeToBroadcastStationUpdates(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        StationState.UnsubscribeFromBroadcastStationUpdates(this);
    }

    @Override
    public void OnBroadcastStationStart() {
    }

    @Override
    public void OnBroadcastSongChange(StationState stationState) {
        Picasso.with(getContext()).load(stationState.albumArtUrl).noPlaceholder().into(albumArt);
    }

    @Override
    public void OnBroadcastSongUpdate(StationState stationState) {
        textSongAndArtist.setText(stationState.songTitle + "  •  " + stationState.songArtist);
//        textArtist.setText(stationState.songArtist);
        textCaption.setText(stationState.listenerIds.size() + " listeners");
    }

    @Override
    public void OnBroadcastStationDie() {}

}
