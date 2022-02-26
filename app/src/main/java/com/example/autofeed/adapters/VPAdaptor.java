package com.example.autofeed.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class VPAdaptor extends FragmentStateAdapter {

    ArrayList<Fragment> arrayList = new ArrayList<>();

    public VPAdaptor(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public VPAdaptor(@NonNull Fragment fragment) {
        super(fragment);
    }

    public VPAdaptor(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return arrayList.get(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void addFragment(@NonNull Fragment fragment) {
        arrayList.add(fragment);
    }
}