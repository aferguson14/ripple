package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.util.Log;

import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerState;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.CurrentlyPlaying;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Ferg on 6/5/17.
 */

public class SpotifyApiController {
    private final SpotifyService mSpotifyService;

    private String mCurrentSongUri;


    public SpotifyApiController(String accessToken){
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

    public void addTrackToPlayList(String userId, String playlistId, Map map1, Map map2, Callback callback){
        Map<String, Object> options = new HashMap<>();
//        options.put(SpotifyService.OFFSET, offset);
//        options.put(SpotifyService.LIMIT, limit);
        mSpotifyService.addTracksToPlaylist(userId, playlistId, map1, map2);
    }

    public void addTrackToSavedTracks(String trackId, Callback callback){
        mSpotifyService.addToMySavedTracks(trackId, callback);
    }

    public void containsTrackMySavedTracks(String trackId, Callback callback){
        mSpotifyService.containsMySavedTracks(trackId, callback);
    }

    public void removeTrackFromSavedTracks(String trackId, Callback callback){
        mSpotifyService.removeFromMySavedTracks(trackId, callback);
    }

    public String getCurrentSongUri(){
        return mCurrentSongUri;
    }
}
