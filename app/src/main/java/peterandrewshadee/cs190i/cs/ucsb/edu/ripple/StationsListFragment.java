package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by peterwerner on 6/1/17.
 */

public class StationsListFragment extends Fragment {

    static List<Broadcast> mBroadcastList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_stationslist, container, false);

        mBroadcastList = new ArrayList<>();
        FirebaseHelper.GetInstance().getBroadcasts();


        test(view);
        return view;
    }



    // TODO: need to update mBroadCastList & listen to DB
    void test (View view) {
//        for(final Broadcast broadcast : mBroadcastList){
//            Log.d("stationslist", "station size: " + mBroadcastList.size());
//            ((Button) view.findViewById(R.id.test_button_togglebroadcasteractive)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("stationslist", "clicked");
//                    if (StationState.listeningStation == null) {
//                        // Make it look like we're playing a song
//                        StationState.UpdateListeningStation(new StationState(
//                                broadcast.getId(),
//                                broadcast.getUserName(),
//                                broadcast.getSongId(),
//                                broadcast.getSongName(),
//                                broadcast.getArtist(),
//                                broadcast.getIs_playing(),
//                                broadcast.getDuration_ms(),
//                                broadcast.getProgress_ms(),
//                                new ArrayList<String>() //TODO: update listeners
//                        ));
//                    } else {
//                        // Close the song
//                        StationState.UpdateListeningStation(null);
//                    }
//                }
//            });
//        }
        ((Button) view.findViewById(R.id.test_button_togglebroadcasteractive)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StationState.listeningStation == null) {
                    // Make it look like we're playing a song
                    StationState.UpdateListeningStation(new StationState(
                            null,
                            "Peter Werner",
                            null,
                            "Love on a Real Train",
                            "Tangerine Dream",
                            true,
                            30,
                            0,
                            new ArrayList<String>()
                    ));
                } else {
                    // Close the song
                    StationState.UpdateListeningStation(null);
                }
            }
        });
        ((Button) view.findViewById(R.id.test_button_togglebroadcasterpaused)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseHelper.GetInstance().getBroadcasts(); //FOR TESTING
                Log.d("stationslist", "station size: " + mBroadcastList.size());
                if (StationState.listeningStation != null) {
                    StationState.listeningStation.isPlaying = !StationState.listeningStation.isPlaying;
                    StationState.NotifyListeningStationDataChanged();
                }
            }
        });
    }

}
