package com.lzh.yuanstrom.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.ui.PageFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chris.black on 6/11/15.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {
    private Map<Integer, PageFragment> mPageReferenceMap = new HashMap<>();

    private String[] titles;

    public int titleNo;

    public TabPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        titles = context.getResources().getStringArray(R.array.tab_array);
        titleNo = titles.length;

    }

    @Override
    public int getCount() {
        return titleNo;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        PageFragment myFragment = PageFragment.create(position + 1);
        mPageReferenceMap.put(position, myFragment);
        return myFragment;
    }

    public void destroy() {
        mPageReferenceMap.clear();
    }

    public PageFragment getFragment(int key) {
        //Log.d(TAG, "GET: " + key);
        if(key == -1){
            key = 0;
        }
        return mPageReferenceMap.get(key);
    }
}
