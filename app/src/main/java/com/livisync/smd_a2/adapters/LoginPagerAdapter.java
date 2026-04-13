package com.livisync.smd_a2.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.livisync.smd_a2.fragments.LoginFragment;
import com.livisync.smd_a2.fragments.SignUpFragment;

public class LoginPagerAdapter extends FragmentStateAdapter {
    public LoginPagerAdapter(FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new LoginFragment();
        } else {
            return new SignUpFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
