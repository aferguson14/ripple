package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.app.ActionBar;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.HashSet;

import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements StationState.ListeningStationUpdateListener, PlayerNotificationCallback, ConnectionStateCallback {

    private static final int REQUEST_CODE = 1337;
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String CLIENT_ID = "5aa6208c6cd54357ad6b55bd67197d51";
    public static CurrentlyPlayingController currentlyPlayingController;
    public static String myUserId;
    public static String myUserName;
    private Player mPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String accessToken = intent.getStringExtra(ACCESS_TOKEN);

        //WebAPI
        currentlyPlayingController = new CurrentlyPlayingController(accessToken);
        MainActivity.currentlyPlayingController.getMeInfo(new Callback<UserPrivate>(){
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                if(userPrivate != null) {
                    myUserId = userPrivate.id;
                    myUserName = userPrivate.display_name;
                }
                else
                    Log.d("restapi", "FAILURE");
//                            Toast.makeText(MainActivity.this, track.uri, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Track failure", error.toString());
            }
        });

        //Player
        Config playerConfig = new Config(this, accessToken, CLIENT_ID);
        mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized(Player player) {
                mPlayer.addConnectionStateCallback(MainActivity.this);
                mPlayer.addPlayerNotificationCallback(MainActivity.this);

                // init db on successful login
                FirebaseHelper.Initialize();
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });

        //Firebase
        FirebaseHelper.Initialize();

        final ViewPager viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), MainActivity.this));
        viewPager.setCurrentItem(1); // Default to second page (Listen)
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);

        StationState.SubscribeToListeningStationUpdates(this);

        OnListeningStationDie(); // Music bar starts hidden
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Spotify.destroyPlayer(this);
        StationState.UnsubscribeFromListeningStationUpdates(this);
    }


    /*
     * Currently playing song events
     */

    @Override
    public void OnListeningStationStart() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment musicBarFragment = getSupportFragmentManager().findFragmentById(R.id.main_musicbar);
        fragmentTransaction.show(musicBarFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void OnListeningSongChange(StationState stationState) {
        //TODO: test
        mPlayer.play("spotify:track:" + stationState.songId);
    }

    @Override
    public void OnListeningSongUpdate(StationState stationState) {
        //TODO: test
        mPlayer.play("spotify:track:" + stationState.songId);
        int songProgressSeconds = (int)Math.round(stationState.songProgressMs*.001);
        mPlayer.seekToPosition(songProgressSeconds);
    }

    @Override
    public void OnListeningStationDie() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment musicBarFragment = getSupportFragmentManager().findFragmentById(R.id.main_musicbar);
        fragmentTransaction.hide(musicBarFragment);
        fragmentTransaction.commit();
    }


    /*
     * Spotify Playback
     */

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
//        Log.d("MainActivity", "Playback event received: " + playerState.name());
//        switch (playerState) {
//            // Handle event type as necessary
//            default:
//                break;
//        }
        Log.d("MainActivity", "Playback event received: " + eventType.name());
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
//        Log.d("MainActivity", "Playback error received: " + error.name());
//        switch (error) {
//            // Handle error type as necessary
//            default:
//                break;
//        }
        Log.d("MainActivity", "Playback error received: " + errorType.name());
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
        //setContentView(R.layout.activity_main);
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
        //setContentView(R.layout.login_page);
    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

}
