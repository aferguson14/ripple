package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

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
    boolean isPlaying =false;
    double songDurationSeconds=0, songProgressSeconds=0; // Total length and current position of song
    Set<String> listenerIds = new HashSet<>();

    public StationState(String userId, String songId) {
        this.userId = userId;
        this.songId = songId;

        // TODO need to fetch the song title / username etc? web API? store in DB?
    }

    public StationState(String userId, String userName, String songId, String songTitle, String songArtist, boolean isPaused, double songDurationSeconds, double songProgressSeconds, Set<String> listenerIds) {
        this.userId = userId;
        this.userName = userName;
        this.songId = songId;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.isPlaying = isPaused;
        this.songDurationSeconds = songDurationSeconds;
        this.songProgressSeconds = songProgressSeconds;
        this.listenerIds = listenerIds;
    }

    public StationState(StationState _stationState) {
        this.userId = _stationState.userId;
        this.userName = _stationState.userName;
        this.songId = _stationState.songId;
        this.songTitle = _stationState.songTitle;
        this.songArtist = _stationState.songArtist;
        this.isPlaying = _stationState.isPlaying;
        this.songDurationSeconds = _stationState.songDurationSeconds;
        this.songProgressSeconds = _stationState.songProgressSeconds;
        this.listenerIds = _stationState.listenerIds;
    }

    boolean IsDifferentSong(StationState _stationState) {
        return this.userId != _stationState.userId || this.songId != _stationState.songId;
    }


    /*
     * STATIC STUFF
     */

    // Updates to the station the user is currently listening to
    public interface CurrentStationUpdateListener {
        void OnStationStart ();
        void OnSongChange (StationState stationState, boolean userWantsToPlay);
        void OnSongUpdate (StationState stationState, boolean userWantsToPlay);
        void OnStationDie ();
    }

    static StationState currentStation = null;
    static boolean userWantsToPlay = true; // True if the user wants to play (still might not play if the broadcaster pauses)
    private static List<CurrentStationUpdateListener> currentListeners = new ArrayList<>();

    public static void SubscribeToCurrentStationUpdates (CurrentStationUpdateListener listener) {
        currentListeners.add(listener);
    }

    public static void UnsubscribeFromCurrentStationUpdates (CurrentStationUpdateListener listener) {
        currentListeners.remove(listener);
    }

    public static void NotifyCurrentStationDataChanged() {
        UpdateCurrentStation(currentStation);
    }
    public static void UpdateCurrentStation (StationState _stationState) {
        StationState prevStation = currentStation;
        if (_stationState != null) {
            currentStation = new StationState(_stationState);
        } else {
            currentStation = null;
        }

        if (_stationState == null) {
            for (CurrentStationUpdateListener listener : currentListeners) {
                listener.OnStationDie();
            }
            userWantsToPlay = true;
        }
        else if (prevStation == null) {
            for (CurrentStationUpdateListener listener : currentListeners) {
                listener.OnStationStart();
            }
            for (CurrentStationUpdateListener listener : currentListeners) {
                listener.OnSongChange(currentStation, userWantsToPlay);
            }
        }
        else {
            if (prevStation.IsDifferentSong(currentStation)) {
                for (CurrentStationUpdateListener listener : currentListeners) {
                    listener.OnSongChange(currentStation, userWantsToPlay);
                }
            } else {
                for (CurrentStationUpdateListener listener : currentListeners) {
                    listener.OnSongUpdate(currentStation, userWantsToPlay);
                }
            }
        }
    }
}
