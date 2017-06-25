package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by peterwerner on 6/1/17.
 */

public class MusicBarFragment extends Fragment implements StationState.ListeningStationUpdateListener {

    private ToggleButton playPauseButton, addRemoveButton;
    private TextView textSong, textArtist, textCaption;
    private View progressBar;
    private TimerTask timerTask = null; // Task to periodically update the progress bar
    private Long timeLastUpdated = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_musicbar, container, false);

        playPauseButton = (ToggleButton) view.findViewById(R.id.musicbar_toggle_playpause);
        addRemoveButton = (ToggleButton) view.findViewById(R.id.musicbar_toggle_addremove);
        textSong = (TextView) view.findViewById(R.id.musicbar_text_song);
        textArtist = (TextView) view.findViewById(R.id.musicbar_text_artist);
        textCaption = (TextView) view.findViewById(R.id.musicbar_text_caption);
        progressBar = view.findViewById(R.id.musicbar_progressbar);

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StationState.userWantsToPlay = playPauseButton.isChecked();
                StationState.NotifyListeningStationDataChanged();
            }
        });

        addRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: make this button add / remove from spotify library
                MainActivity.spotifyApiController.addTrackToSavedTracks(StationState.listeningStation.songId, new Callback() {
                    @Override
                    public void success(Object o, Response response) {
                        Toast.makeText(getContext(), "Saved to your Library", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getContext(), "Failure", Toast.LENGTH_SHORT).show();
                    }
                });
//                Toast.makeText(getContext(), "Should add / remove from spotify library", Toast.LENGTH_SHORT).show();

            }
        });

        // Update the progress bar every 50ms
        final Timer timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run()
            {
                long currTime = System.currentTimeMillis();
                if (timeLastUpdated != null) {
                    long deltaTime = currTime - timeLastUpdated;
                    if (StationState.listeningStation != null && StationState.listeningStation.isPlaying) {
                        //TODO: modify so that progress bar works
//                        StationState.listeningStation.songProgressMs += 1000 * deltaTime;
                    }
                    UpdateProgressBarFromOtherThread();
                }
                timeLastUpdated = currTime;
            }
        };
        timer.schedule(timerTask, 0, 50);

        StationState.SubscribeToListeningStationUpdates(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        timerTask.cancel();

        StationState.UnsubscribeFromListeningStationUpdates(this);
    }

    /*
     * Currently playing song events
     */

    private void UpdateStationState(StationState stationState, boolean userWantsToPlay) {
        textSong.setText(SanitizeString(stationState.songTitle));
        textArtist.setText(SanitizeString(stationState.songArtist));
        String caption = stationState.userName + " and " + stationState.listenerIds.size() + " listeners";
        if (stationState.userName == null)
            caption = MainActivity.myUserId + " and " + stationState.listenerIds.size() + " listeners";
        textCaption.setText(SanitizeString(caption));
//        textCaption.setText(SanitizeString(stationState.userName + " and " + stationState.listenerIds.size() + " listeners"));

        playPauseButton.setEnabled(stationState.isPlaying);
        playPauseButton.setChecked(StationState.userWantsToPlay);

        playPauseButton.setAlpha(playPauseButton.isEnabled() ? 1 : 0.5f);
        addRemoveButton.setAlpha(addRemoveButton.isEnabled() ? 1 : 0.5f);
    }

    private void UpdateProgressBarFromOtherThread () {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UpdateProgressBar();
            }
        });
    }
    private void UpdateProgressBar () {
        LinearLayout.LayoutParams progressBarParams = (LinearLayout.LayoutParams) progressBar.getLayoutParams();
        progressBarParams.weight = StationState.listeningStation != null
                ? StationState.listeningStation.GetNormalizedPosition()
                : 0;
        progressBar.setLayoutParams(progressBarParams);
    }

    private String SanitizeString (String s) {
        return s != null && s.length() > 0 ? s : "-";
    }

    @Override
    public void OnListeningStationStart() {}

    @Override
    public void OnListeningSongChange(StationState stationState) {
        UpdateStationState (stationState, StationState.userWantsToPlay);
    }

    @Override
    public void OnListeningSongUpdate(StationState stationState) {
        UpdateStationState (stationState, StationState.userWantsToPlay);
    }

    @Override
    public void OnListeningStationDie() {}
}
