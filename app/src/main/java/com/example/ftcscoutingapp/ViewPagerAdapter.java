package com.example.ftcscoutingapp;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private OpmodeInputForm opmodeFragment = new OpmodeInputForm();
    private AutonomousInputForm autonomousFragment = new AutonomousInputForm();
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return opmodeFragment;
            case 1: return autonomousFragment;
            default: return opmodeFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public OpmodeInputForm getOpmodeFragment() {
        return opmodeFragment;
    }

    public AutonomousInputForm getAutonomousFragment() {
        return autonomousFragment;
    }
}
