package com.example.mathrpg;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class StagePagerAdapter extends FragmentStatePagerAdapter {

    private int numPages;

    public StagePagerAdapter(FragmentManager fm, int numPages) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numPages = numPages;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0: return new StageFragment1();
            case 1: return new StageFragment2();
            case 2: return new StageFragment3();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return numPages;
    }
}
