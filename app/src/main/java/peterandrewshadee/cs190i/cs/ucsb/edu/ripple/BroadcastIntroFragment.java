package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.CurrentlyPlaying;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.media.MediaMetadata.METADATA_KEY_ALBUM;

/**
 * Created by peterwerner on 6/1/17.
 */

public class BroadcastIntroFragment extends Fragment {

    private static final String spotifyPackageName = "com.spotify.music";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_broadcastintro, container, false);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OpenSpotifyApp()) {
                    FirebaseHelper.GetInstance().addBroadcast(MainActivity.myUserId);

                    MainActivity.isBroadcasting = true;
                } else {
                    Toast.makeText(v.getContext(), "You must have the Spotify app installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private boolean OpenSpotifyApp () {
        Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(spotifyPackageName);
        if (launchIntent != null) {
            startActivity(launchIntent);
            return true;
        }
        return false;
    }

}
