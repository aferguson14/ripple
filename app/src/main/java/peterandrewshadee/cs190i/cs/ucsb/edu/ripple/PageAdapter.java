package peterandrewshadee.cs190i.cs.ucsb.edu.ripple;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by peterwerner on 6/1/17.
 */

// https://guides.codepath.com/android/google-play-style-tabs-using-tablayout
public class PageAdapter extends FragmentPagerAdapter {

    public PageAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new BroadcastParentFragment();
        }
        if (position == 1) {
            return new StationsListFragment();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Broadcast";
        }
        if (position == 1) {
            return "Listen";
        }
        return null;
    }
}