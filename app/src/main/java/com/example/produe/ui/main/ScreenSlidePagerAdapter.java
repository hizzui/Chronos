package com.example.produe.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.produe.GraphFragment;
import com.example.produe.TimerFragment;
import com.example.produe.ToDoFragment;

public class ScreenSlidePagerAdapter extends FragmentStateAdapter {

   // Number of pages to slide between
    private static final int NUM_PAGES = 3;

    public ScreenSlidePagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Choose the fragment to open depending on the position of the view pager
        switch (position) {
            case 0:
                return new ToDoFragment();
            case 1:
                return new TimerFragment();
            default:
                return new GraphFragment();
        }
    }

    // Return number of pages to slide between
    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
