package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by peterwerner on 6/5/17.
 */

public class BroadcastParentFragment extends Fragment implements StationState.BroadcastStationUpdateListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_broadcastparent, container, false);
        if (StationState.broadcastStation != null) {
            OnBroadcastStationStart();
        } else {
            OnBroadcastStationDie();
        }
        StationState.SubscribeToBroadcastStationUpdates(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        StationState.UnsubscribeFromBroadcastStationUpdates(this);
    }


    @Override
    public void OnBroadcastStationStart() {
        SetFragment(new BroadcastFragment());
    }

    @Override
    public void OnBroadcastSongChange(StationState stationState) {}

    @Override
    public void OnBroadcastSongUpdate(StationState stationState) {}

    @Override
    public void OnBroadcastStationDie() {
        SetFragment(new BroadcastIntroFragment());
    }

    private void SetFragment (Fragment fragment) {
        try {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.broadcast_container, fragment)
                    .commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Toast.makeText(getContext(), "Error: failed to update fragment", Toast.LENGTH_SHORT).show();
        }
    }

}
