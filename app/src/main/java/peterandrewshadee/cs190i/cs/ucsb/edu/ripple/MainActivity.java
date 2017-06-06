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
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity implements StationState.ListeningStationUpdateListener {

    private static final int REQUEST_CODE = 1337;
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String accessToken = intent.getStringExtra(ACCESS_TOKEN);

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

        // TODO: update the player
    }

    @Override
    public void OnListeningSongUpdate(StationState stationState) {

        // TODO: update the player
    }

    @Override
    public void OnListeningStationDie() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment musicBarFragment = getSupportFragmentManager().findFragmentById(R.id.main_musicbar);
        fragmentTransaction.hide(musicBarFragment);
        fragmentTransaction.commit();
    }
}
