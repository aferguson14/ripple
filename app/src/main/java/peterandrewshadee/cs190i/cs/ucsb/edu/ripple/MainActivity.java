package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.PlayConfig;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.models.CurrentlyPlaying;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.media.MediaMetadata.METADATA_KEY_ALBUM;
import static android.media.MediaMetadata.METADATA_KEY_ARTIST;
import static android.media.MediaMetadata.METADATA_KEY_DURATION;
import static android.media.MediaMetadata.METADATA_KEY_TITLE;
import static android.media.session.PlaybackState.STATE_PAUSED;

public class MainActivity extends AppCompatActivity implements StationState.ListeningStationUpdateListener, PlayerNotificationCallback, ConnectionStateCallback{

    private static final int REQUEST_CODE = 1337;
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String CLIENT_ID = "5aa6208c6cd54357ad6b55bd67197d51";
    public static SpotifyApiController spotifyApiController;
    public static String myUserId;
    public static String myUserName;

    private Player mPlayer;

    public static Boolean isBroadcasting;
    public static String currentBroadcastId;

    public MediaController.Callback mSessionCallback;
    public MediaController spotifyMediaController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String accessToken = intent.getStringExtra(ACCESS_TOKEN);

        //WebAPI
        spotifyApiController = new SpotifyApiController(accessToken);
        MainActivity.spotifyApiController.getMeInfo(new Callback<UserPrivate>(){
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
        FirebaseHelper.GetInstance().getBroadcasts();


        /*
            MEDIA CONTROLLER
         */
        isBroadcasting = false;
        MediaSessionManager mm = (MediaSessionManager) this.getSystemService(
                Context.MEDIA_SESSION_SERVICE);
        mSessionCallback = new MediaController.Callback() {
            @Override
            public void onPlaybackStateChanged(PlaybackState state) {
                Log.d("NotificationListener", "playback changed");
                if (state != null) {
                    if(isBroadcasting) {
                        Broadcast bc = new Broadcast(myUserId);
                        bc.setProgress_ms(state.getPosition());
                        if(state.getState()==PlaybackState.STATE_PAUSED)
                            bc.setIs_playing(false);
                        else if(state.getState()==PlaybackState.STATE_PLAYING)
                            bc.setIs_playing(true);
                        FirebaseHelper.GetInstance().updateBroadcastPlayState(myUserId, bc);
                        Log.d("NotificationListener", "STATEPOS: " + state.getPosition());
                    }
                }
            }

            @Override
            public void onMetadataChanged(MediaMetadata metadata) {
                Log.d("NotificationListener", "metadata changed");
                if (metadata != null) {
                    if(isBroadcasting) {
                        final Broadcast bc = new Broadcast(myUserId);
                        bc.setArtist(metadata.getString(METADATA_KEY_ARTIST));
                        bc.setDuration_ms(metadata.getLong(METADATA_KEY_DURATION));
                        bc.setSongName(metadata.getString(METADATA_KEY_TITLE));
                        MainActivity.spotifyApiController.fetchCurrentlyPlaying(new Callback<CurrentlyPlaying>(){
                            //CurrentlyPlaying attributes: timestamp, progress_ms, item (current track), is_playing
                            @Override
                            public void success(CurrentlyPlaying currentlyPlaying, Response response) {
                                if(currentlyPlaying != null) {
                                    bc.setSongId(currentlyPlaying.item.id);
                                    Log.d("NotificationListener", "songid: " + currentlyPlaying.item.id);
                                    FirebaseHelper.GetInstance().updateBroadcastMetadata(myUserId, bc);
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

                        for(String key : metadata.keySet()){
                            Log.d("NotificationListener", "key: " + key);

                        }
                    }
                }
            }

            @Override
            public void onExtrasChanged(Bundle extras){
                Log.d("NotificationListener", "" + extras.size());
            }

            @Override
            public void onSessionDestroyed() {
                Log.d("NotificationListener", "SESSION DESTROYED");
            }
        };

        mm.addOnActiveSessionsChangedListener(new MediaSessionManager.OnActiveSessionsChangedListener() {
                                                  @Override
                                                  public void onActiveSessionsChanged(@Nullable List<MediaController> controllers) {
                                                      for (int i = 0; i < controllers.size(); i++) {
                                                          Log.d("NotificationListener", "controller: " + i + " " + controllers.get(i).getPackageName());
                                                          if (controllers.get(i).getPackageName().equals("com.spotify.music")) {
                                                              spotifyMediaController = controllers.get(i);
                                                              spotifyMediaController.registerCallback(mSessionCallback);
                                                          }
                                                      }
                                                  }
                                              },
                new ComponentName(getApplicationContext().getPackageName(), NotificationListener.class.getName()));
        /*
            MEDIA CONTROLLER
         */


        final ViewPager viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), MainActivity.this));
        viewPager.setCurrentItem(1); // Default to second page (Listen)
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);

        StationState.SubscribeToListeningStationUpdates(this);

    }

    @Override
    public void onPostResume() {
        super.onPostResume();
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
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment musicBarFragment = getSupportFragmentManager().findFragmentById(R.id.main_musicbar);
            fragmentTransaction.show(musicBarFragment);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Toast.makeText(this, "Error: failed to update fragment", Toast.LENGTH_SHORT).show();
        }

        if(isBroadcasting) {
//            FirebaseHelper.GetInstance().deleteBroadcast(myUserId);
//            isBroadcasting = false;
        }
    }

    @Override
    public void OnListeningSongChange(StationState stationState) {
        //TODO: test
        Log.d("listeners", "onListeningSongCHANGE");
        if(stationState.isPlaying && stationState.userWantsToPlay) {
            int songProgressMilli = (int) (stationState.songProgressMs + ((FirebaseHelper.GetInstance().timeAtUpdate == 0 ) ? 0 : System.currentTimeMillis() - FirebaseHelper.GetInstance().timeAtUpdate));
            mPlayer.play(PlayConfig.createFor("spotify:track:" + stationState.songId).withInitialPosition(songProgressMilli));
            Log.d("listeners", "songProgress " + songProgressMilli);
        }
        else{
            Log.d("listeners", "isPlaying: " + stationState.isPlaying + ", userWantsToPlay: " + stationState.userWantsToPlay);
            mPlayer.pause();
        }
    }

    @Override
    public void OnListeningSongUpdate(StationState stationState) {
        //TODO: test
        Log.d("listeners", "onListeningSongUPDATE");
        if(stationState.isPlaying && stationState.userWantsToPlay) {
            int songProgressMilli = (int) (stationState.songProgressMs + ((FirebaseHelper.GetInstance().timeAtUpdate == 0 ) ? 0 : System.currentTimeMillis() - FirebaseHelper.GetInstance().timeAtUpdate));
            mPlayer.play(PlayConfig.createFor("spotify:track:" + stationState.songId).withInitialPosition(songProgressMilli));
            Log.d("listeners", "songProgress " + songProgressMilli);

        }
        else{
            Log.d("listeners", "isPlaying: " + stationState.isPlaying + ", userWantsToPlay: " + stationState.userWantsToPlay);
            mPlayer.pause();
        }
//        if(stationState.isPlaying && stationState.userWantsToPlay) {
//            mPlayer.play("spotify:track:" + stationState.songId);
//
//            int songProgressSeconds = (int) (stationState.songProgressMs + ((FirebaseHelper.GetInstance().timeAtUpdate == 0 ) ? 0 : System.currentTimeMillis() - FirebaseHelper.GetInstance().timeAtUpdate));
//
//            Log.d("listeners", "songProgress " + songProgressSeconds);
//            mPlayer.seekToPosition(songProgressSeconds);
//        }
//        else{
//            mPlayer.pause();
//        }
    }

    @Override
    public void OnListeningStationDie() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment musicBarFragment = getSupportFragmentManager().findFragmentById(R.id.main_musicbar);
            fragmentTransaction.hide(musicBarFragment);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Toast.makeText(this, "Error: failed to update fragment", Toast.LENGTH_SHORT).show();
        }
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
