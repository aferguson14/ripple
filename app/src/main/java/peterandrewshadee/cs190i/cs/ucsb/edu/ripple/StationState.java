package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by peterwerner on 6/3/17.
 */

public class StationState {
    String userId, userName=""; // Spotify user broadcasting
    String songId, songTitle="", songArtist=""; // Song currently playing
    boolean isPlaying = false;
    long songDurationMs, songProgressMs; // Total length and current position of song
    List<String> listenerIds = new ArrayList<>();

//    public StationState(String userId, String songId) {
//        this.userId = userId;
//        this.songId = songId;
//
//        // TODO need to fetch the song title / username etc? web API? store in DB?
//    }

    public StationState(String userId, String userName, String songId, String songTitle, String songArtist, Boolean isPlaying, Long songDurationMs, Long songProgressMs, List<String> listenerIds) {
        this.userId = userId;
        this.userName = userName;
        this.songId = songId;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.isPlaying = isPlaying;
        this.songDurationMs = songDurationMs;
        this.songProgressMs = songProgressMs;
        this.listenerIds = listenerIds;
    }

    public StationState(StationState _stationState) {
        this.userId = _stationState.userId;
        this.userName = _stationState.userName;
        this.songId = _stationState.songId;
        this.songTitle = _stationState.songTitle;
        this.songArtist = _stationState.songArtist;
        this.isPlaying = _stationState.isPlaying;
        this.songDurationMs = _stationState.songDurationMs;
        this.songProgressMs = _stationState.songProgressMs;
        this.listenerIds = _stationState.listenerIds;
    }

    public StationState(Broadcast broadcast) {
        this.userId = broadcast.getId();
        this.userName = broadcast.getUserName();
        this.songId = broadcast.getSongId();
        this.songTitle = broadcast.getSongName();
        this.songArtist = broadcast.getArtist();
        this.isPlaying = broadcast.getIs_playing() != null && broadcast.getIs_playing().booleanValue();
        this.songDurationMs = broadcast.getDuration_ms() != null ? broadcast.getDuration_ms() : 0;
        this.songProgressMs = broadcast.getProgress_ms() != null ? broadcast.getProgress_ms() : 0;
        if (broadcast.getListeners() != null) {
            this.listenerIds = broadcast.getListeners();
        }
    }

    boolean IsDifferentSong(StationState _stationState) {
        return this.userId != _stationState.userId || this.songId != _stationState.songId;
    }

    float GetNormalizedPosition () {
        if (songDurationMs <= 0) {
            return 0;
        } else {
            return songProgressMs / songDurationMs;
        }
    }


    /*
     * STATIC STUFF
     */

    // Updates to the station the user is currently listening to
    public interface ListeningStationUpdateListener {
        void OnListeningStationStart();
        void OnListeningSongChange(StationState stationState);
        void OnListeningSongUpdate(StationState stationState);
        void OnListeningStationDie();
    }

    // Updates to the station the user is broadcasting
    public interface BroadcastStationUpdateListener {
        void OnBroadcastStationStart();
        void OnBroadcastSongChange(StationState stationState);
        void OnBroadcastSongUpdate(StationState stationState);
        void OnBroadcastStationDie();
    }

    static StationState listeningStation = null; // Station the user is listening to (ie: someone else's broadcast)
    static StationState broadcastStation = null; // Station the user is broadcasting
    static boolean userWantsToPlay = true; // True if the user wants to play the listeningStation's song (still won't play if the broadcaster pauses)
    private static List<ListeningStationUpdateListener> listeningStationListeners = new ArrayList<>();
    private static List<BroadcastStationUpdateListener> broadcastStationListeners = new ArrayList<>();

    // Listening station

    public static void SubscribeToListeningStationUpdates(ListeningStationUpdateListener listener) {
        listeningStationListeners.add(listener);
    }

    public static void UnsubscribeFromListeningStationUpdates(ListeningStationUpdateListener listener) {
        listeningStationListeners.remove(listener);
    }

    public static void NotifyListeningStationDataChanged() {
        UpdateListeningStation(listeningStation);
    }

    public static void UpdateListeningStation(StationState newStation) {
        StationState prevStation = null;
        if (listeningStation != null) {
            prevStation = new StationState(listeningStation);
        }

        if (newStation != null) {
            listeningStation = new StationState(newStation);
            if (prevStation == null || listeningStation.userId != prevStation.userId) {
                ListenToDB();
            }
        } else {
            listeningStation = null;
            CancelListenToDB();
        }

        if (newStation == null) {
            for (ListeningStationUpdateListener listener : listeningStationListeners) {
                listener.OnListeningStationDie();
            }
            Log.d("StationState", "Die");
            userWantsToPlay = true;
        }
        else if (prevStation == null) {
            for (ListeningStationUpdateListener listener : listeningStationListeners) {
                listener.OnListeningStationStart();
            }
            for (ListeningStationUpdateListener listener : listeningStationListeners) {
                listener.OnListeningSongChange(listeningStation);
            }
            Log.d("StationState", "Start + Change");
        }
        else {
            if (prevStation.IsDifferentSong(listeningStation)) {
                for (ListeningStationUpdateListener listener : listeningStationListeners) {
                    listener.OnListeningSongChange(listeningStation);
                }
                Log.d("StationState", "Change");
            } else {
                for (ListeningStationUpdateListener listener : listeningStationListeners) {
                    listener.OnListeningSongUpdate(listeningStation);
                }
                Log.d("StationState", "Update");
            }
        }
    }

    // Broadcasting station

    public static void SubscribeToBroadcastStationUpdates(BroadcastStationUpdateListener listener) {
        broadcastStationListeners.add(listener);
    }

    public static void UnsubscribeFromBroadcastStationUpdates(BroadcastStationUpdateListener listener) {
        broadcastStationListeners.remove(listener);
    }

    public static void NotifyBroadcastStationDataChanged() {
        UpdateBroadcastStation(broadcastStation);
    }

    public static void UpdateBroadcastStation (StationState newStation) {
        StationState prevStation = null;
        if (broadcastStation != null) {
            prevStation = new StationState(broadcastStation);
        }

        if (newStation != null) {
            broadcastStation = new StationState(newStation);
        } else {
            broadcastStation = null;
        }

        if (newStation == null) {
            for (BroadcastStationUpdateListener listener : broadcastStationListeners) {
                listener.OnBroadcastStationDie();
            }
        }
        else if (prevStation == null) {
            for (BroadcastStationUpdateListener listener : broadcastStationListeners) {
                listener.OnBroadcastStationStart();
            }
            for (BroadcastStationUpdateListener listener : broadcastStationListeners) {
                listener.OnBroadcastSongChange(broadcastStation);
            }
        }
        else {
            if (prevStation.IsDifferentSong(broadcastStation)) {
                for (BroadcastStationUpdateListener listener : broadcastStationListeners) {
                    listener.OnBroadcastSongChange(broadcastStation);
                }
            } else {
                for (BroadcastStationUpdateListener listener : broadcastStationListeners) {
                    listener.OnBroadcastSongUpdate(broadcastStation);
                }
            }
        }

    }

    public static void TryClearBroadcastStationWithConfirmationDialog (Context context) {
        if (broadcastStation != null) {
            new AlertDialog.Builder(context)
                .setTitle("Stop Broadcasting")
                .setMessage("Are you sure you want to stop broadcasting?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateBroadcastStation(null);
                        if (MainActivity.myUserId != null) FirebaseHelper.GetInstance().deleteBroadcast(MainActivity.myUserId);
                        MainActivity.isBroadcasting = false;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
        }
    }



    private static ValueEventListener dbListener = null;

    private static void ListenToDB () {
        Log.d("listeners", "listening to db: " + listeningStation.userId);
        CancelListenToDB();
        DatabaseReference dbr = FirebaseHelper.GetInstance().getBroadcastRef().child(listeningStation.userId);
        dbListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                if (ds != null) {
                        try {
                            Broadcast bc = ds.getValue(Broadcast.class);
                            Log.d("listeners", "DB UPDATE: " + bc.getSongName() + " " + bc.getProgress_ms() + "ms");
                            StationState.UpdateListeningStation(new StationState(bc));
                        } catch (DatabaseException e) {
                            Log.d("listeners", "DB UPDATE: get rekt you have an error");
                        }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbr.addValueEventListener(dbListener);
    }

    private static void CancelListenToDB() {
        DatabaseReference dbr = FirebaseHelper.GetInstance().getBroadcastRef().child(listeningStation.userId);
        if (dbListener != null) {
            dbr.removeEventListener(dbListener);
        }
    }


}
