package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.content.ComponentName;
import android.content.Context;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.CurrentlyPlaying;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.media.MediaMetadata.METADATA_KEY_ALBUM;

/**
 * Created by shadeebarzin on 6/2/17.
 */

class FirebaseHelper {

    private static FirebaseHelper firebase;
    private DatabaseReference users;
    private DatabaseReference broadcasts;
    private DatabaseReference listeners;

    static final List<Broadcast> broadcastList = new ArrayList<>();


    // call this method on successful login
    static void Initialize() {
        if (firebase == null) firebase = new FirebaseHelper();
        firebase.getBroadcasts();
    }

    // use this method when want to add or read from db in any Activity/Fragment/etc.
    static FirebaseHelper GetInstance() {
        return firebase;
    }


    private FirebaseHelper() {
        FirebaseDatabase fb = FirebaseDatabase.getInstance();
        users = fb.getReference("users");
        broadcasts = fb.getReference("broadcasts");
    }

    // user userId should be spotify client userId
    // client userId -> { name: spotify user's name, photo: profile photo url }
    void addUser(User user) {
//        users.child(user.getUserId()).child("name").setValue(user.getName());
//        users.child(user.getUserId()).child("photo").setValue(user.getPhotoUrl());
        users.child(user.getUserId()).setValue(user);
    }

    void addUser(String userId, String name, String photo_url) {
        users.child(userId).child("name").setValue(name);
        users.child(userId).child("photo_url").setValue(photo_url);
    }

    void deleteUser(User user) { users.child(user.getUserId()).removeValue(); }

    void deleteUser(String userId) { users.child(userId).removeValue(); }

    void getUsers(final List<User> userList) {
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        User user = ds.getValue(User.class);
                        Log.d("getUsers", user.toString());
                        userList.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("getUsers", "db error: " + databaseError.getMessage());
            }
        });
    }


    // broadcaster's spotify client userId -> broadcast info (incl list of listeners)
//    void addBroadcast(User broadcaster) {
//        Broadcast bc = new Broadcast(broadcaster.getUserId());
//        broadcasts.child(broadcaster.getUserId()).setValue(bc);
//    }

//    void addBroadcast(String broadcasterId) {
//        Broadcast bc = new Broadcast(broadcasterId);
//        broadcasts.child(broadcasterId).setValue(bc);
//    }

    void addBroadcast(final String broadcasterId) {
        MainActivity.spotifyApiController.fetchCurrentlyPlaying(new Callback<CurrentlyPlaying>(){
            //CurrentlyPlaying attributes: timestamp, progress_ms, item (current track), is_playing
            @Override
            public void success(CurrentlyPlaying currentlyPlaying, Response response) {
                if(currentlyPlaying != null) {
                    StationState.UpdateBroadcastStation(new StationState(
                            MainActivity.myUserId,
                            MainActivity.myUserName,
                            currentlyPlaying.item.id,
                            currentlyPlaying.item.name,
                            currentlyPlaying.item.artists.get(0).name, //TODO: add all artists
                            currentlyPlaying.is_playing,
                            (long)(currentlyPlaying.item.duration_ms),
                            (long)(currentlyPlaying.progress_ms),
                            new ArrayList<String>()
                    ));
                    Broadcast bc = new Broadcast(broadcasterId);
                    bc.setUserName(MainActivity.myUserName);
                    bc.setSongId(currentlyPlaying.item.id);
                    bc.setSongName(currentlyPlaying.item.name);
                    bc.setArtist(currentlyPlaying.item.artists.get(0).name);
                    bc.setIs_playing(currentlyPlaying.is_playing);
                    bc.setDuration_ms(currentlyPlaying.item.duration_ms);
                    bc.setProgress_ms((long)(currentlyPlaying.progress_ms));
                    broadcasts.child(broadcasterId).setValue(bc);
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
    }

    void updateBroadcast(String broadcasterId, Broadcast broadcast) {
        DatabaseReference dbr = broadcasts.child(broadcasterId);
        dbr.child("artist").setValue(broadcast.getArtist());
        dbr.child("duration_ms").setValue(broadcast.getDuration_ms());
        dbr.child("is_playing").setValue(broadcast.getIs_playing());
        dbr.child("progress_ms").setValue(broadcast.getProgress_ms());
        dbr.child("songName").setValue(broadcast.getSongName());
    }

    void updateBroadcast(User broadcaster, Broadcast broadcast) {
        DatabaseReference dbr = broadcasts.child(broadcaster.getUserId());
        dbr.child("artist").setValue(broadcast.getArtist());
        dbr.child("duration_ms").setValue(broadcast.getDuration_ms());
        dbr.child("is_playing").setValue(broadcast.getIs_playing());
        dbr.child("progress_ms").setValue(broadcast.getProgress_ms());
        dbr.child("songName").setValue(broadcast.getSongName());
    }

    void updateBroadcastMetadata(String broadcasterId, Broadcast broadcast) {
        DatabaseReference dbr = broadcasts.child(broadcasterId);
        dbr.child("artist").setValue(broadcast.getArtist());
        dbr.child("duration_ms").setValue(broadcast.getDuration_ms());
        dbr.child("is_playing").setValue(broadcast.getIs_playing());
        dbr.child("songName").setValue(broadcast.getSongName());
    }

    void updateBroadcastPlayState(String broadcasterId, Broadcast broadcast){
        DatabaseReference dbr = broadcasts.child(broadcasterId);
        dbr.child("progress_ms").setValue(broadcast.getProgress_ms());
        dbr.child("is_playing").setValue(broadcast.getIs_playing());
    }

    void deleteBroadcast(User broadcaster) {
        broadcasts.child(broadcaster.getUserId()).removeValue();
    }

    void deleteBroadcast(String broadcasterId) {
        broadcasts.child(broadcasterId).removeValue();
    }

    void getBroadcasts() {

        broadcasts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Broadcast bc = ds.getValue(Broadcast.class);
                        Log.d("getBroadcasts", bc.toString());
                        broadcastList.add(bc);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("getBroadcasts", "db error: " + databaseError.getMessage());
            }
        });
    }


    // broadcaster's spotify client userId -> add listener id to list of listeners
    void addListener(User listener, User broadcaster) {
        broadcasts.child(broadcaster.getUserId()).child("listeners").child(listener.getUserId()).setValue("true");
    }

    void addListener(String listenerId, String broadcasterId) {
        broadcasts.child(broadcasterId).child("listeners").child(listenerId).setValue("true");
    }

    void deleteListener(User listener, User broadcaster) {
        broadcasts.child(broadcaster.getUserId()).child("listeners").child(listener.getUserId()).removeValue();
    }

    void deleteListener(String listenerId, String broadcasterId) {
        broadcasts.child(broadcasterId).child("listeners").child(listenerId).removeValue();
    }


}
