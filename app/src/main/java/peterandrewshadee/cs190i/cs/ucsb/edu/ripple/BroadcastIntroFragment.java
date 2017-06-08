package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashSet;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.CurrentlyPlaying;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
