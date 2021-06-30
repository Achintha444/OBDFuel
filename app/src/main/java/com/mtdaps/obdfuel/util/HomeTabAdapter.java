package com.mtdaps.obdfuel.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mtdaps.obdfuel.home.DashboardFragment;
import com.mtdaps.obdfuel.home.SendFragment;

import org.jetbrains.annotations.NotNull;

public class HomeTabAdapter extends FragmentStateAdapter{

    public HomeTabAdapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DashboardFragment();
            case 1:
                return new SendFragment();

        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
