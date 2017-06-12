package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shadeebarzin on 6/5/17.
 */


public class Broadcast {
//TODO : change progress to be a long
    private String id;
    private String userName;
    private String songId;
    private String songName;
    private String artist;
    private Boolean is_playing;
    private Long duration_ms;
    private Long progress_ms;
//    private List<String> listeners;
    private Map<String, String> listeners;

    public Broadcast() {} //was private


    public Broadcast(String id) {
        this.id = id;
//        this.listeners = new ArrayList<>();
        this.listeners = new HashMap<>();
    }

    //    public Broadcast(String id, List<String> listeners) {
    public Broadcast(String id, Map<String, String> listeners) {
        this.id = id;
        this.listeners = listeners;
    }

    @Override
    public String toString() {
        return
                id;
//                "num listeners: " + Integer.toString(listeners.size());

    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSongId() {
        return songId;
    }
    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }
    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Boolean getIs_playing() {
        return is_playing;
    }
    public void setIs_playing(Boolean is_playing) {
        this.is_playing = is_playing;
    }

    public Long getDuration_ms() {
        return duration_ms;
    }
    public void setDuration_ms(Long duration_ms) {
        this.duration_ms = duration_ms;
    }

    public Long getProgress_ms() {
        return progress_ms;
    }
    public void setProgress_ms(Long progress_ms) {
        this.progress_ms = progress_ms;
    }

//    public List<String> getListeners() { return listeners; }
//    public void setListeners(List<String> listeners) { this.listeners = listeners; }
    public ArrayList<String> getListeners() {
        if (listeners == null) return new ArrayList<>();
        return new ArrayList<>(listeners.keySet());
    }
    public void setListeners(Map<String, String> listeners) { this.listeners = listeners; }
}
