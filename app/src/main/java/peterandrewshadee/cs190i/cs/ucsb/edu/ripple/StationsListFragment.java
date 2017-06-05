package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.HashSet;

/**
 * Created by peterwerner on 6/1/17.
 */

public class StationsListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_stationslist, container, false);
        TEST(view);
        return view;
    }

    // TODO: remove this test code

    void TEST (View view) {
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
                            new HashSet<String>()
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
                if (StationState.listeningStation != null) {
                    StationState.listeningStation.isPlaying = !StationState.listeningStation.isPlaying;
                    StationState.NotifyListeningStationDataChanged();
                }
            }
        });
    }

}
