package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.util.Log;

import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerState;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.CurrentlyPlaying;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Ferg on 6/5/17.
 */

public class CurrentlyPlayingController {
    private final SpotifyService mSpotifyService;

    private String mCurrentSongUri;


    public CurrentlyPlayingController(String accessToken){
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(accessToken);
        mSpotifyService = api.getService();
    }

    public void getMeInfo(Callback callback){
        mSpotifyService.getMe(callback);
    }

    public void fetchCurrentlyPlaying(Callback callback){
        mSpotifyService.getCurrentTrack(callback);
    }

    private void updatedatabase(){
        //FB code
    }

    public String getCurrentSongUri(){
        return mCurrentSongUri;
    }
}
