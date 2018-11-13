package br.edu.uepb.nutes.haniot.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.edu.uepb.nutes.haniot.fragment.FragmentDashMain;

public class FragmentPageAdapter extends FragmentPagerAdapter {
    public FragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new FragmentDashMain();
    }

    @Override
    public int getCount() {
        return 1;
    }
}
