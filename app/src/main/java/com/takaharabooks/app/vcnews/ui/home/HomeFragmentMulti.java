package com.takaharabooks.app.vcnews.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.takaharabooks.app.vcnews.R;
import com.takaharabooks.app.vcnews.pref.Preferences_Common;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

public class HomeFragmentMulti extends Fragment
{
    FragmentPagerItemAdapter mFragAdapter;

    private HomeViewModel mHomeViewModel = null;
    private View mFragmentHome;
    private AdView mAdView = null;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        mHomeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        mFragmentHome = inflater.inflate(R.layout.fragment_home_multi, container, false);

        // 広告表示（リスト読み込み終わってから表示します）
        mAdView = mFragmentHome.findViewById(R.id.adView);
        InitAd();
        loadAd();

        return mFragmentHome;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        Preferences_Common mPrefs = new Preferences_Common(getContext());
        //FragmentPagerItems pages = new FragmentPagerItems(getContext());
        FragmentPagerItems.Creator PICreator = FragmentPagerItems.with(getContext());
        final ArrayList<Preferences_Common.SiteInfo> arSiteInfo = mPrefs.getSiteInfo();

        // 「すべて」を追加
        Preferences_Common.SiteInfo siteInfo = new Preferences_Common.SiteInfo();
        siteInfo.strSiteName = "すべて";
        arSiteInfo.add(0, siteInfo);

        int nSize = arSiteInfo.size();
        for(int nIndex=0; nIndex<nSize; nIndex++)
        {
            String strTagName = arSiteInfo.get(nIndex).strSiteName.toString();
            if(strTagName.isEmpty()) strTagName = arSiteInfo.get(nIndex).strCategory.toString();
            PICreator.add(strTagName, HomeFragment.class);
            //pages.add(FragmentPagerItem.of(strTagName, HomeFragment.class));
        }
        mFragAdapter = new FragmentPagerItemAdapter( getChildFragmentManager(), PICreator.create())
        {
            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position)
            {
                Object item = super.instantiateItem(container, position);
                if (item instanceof Fragment)
                {
                    HomeFragment frag = (HomeFragment) item;
                    frag.setSearchInfo(arSiteInfo.get(position).strSiteName, arSiteInfo.get(position).strCategory);
                }
                return item;
            }
        };

        ViewPager viewPager = mFragmentHome.findViewById(R.id.viewpager);
        viewPager.setAdapter(mFragAdapter);

        SmartTabLayout viewPagerTab = mFragmentHome.findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

        // RSS読み込み後の処理
        //final Observer<Boolean> loadAd = bLoadEnd ->
        //{
        //    if(bLoadEnd)
        //    {
        //
        //    }
        //};
        //mHomeViewModel.getLoadEnd().observe(getViewLifecycleOwner(), loadAd);

    }

    /*************************************
     * 広告の初期設定
     **************************************/
    public void InitAd()
    {
        if(mAdView == null) return;

        MobileAds.initialize(getContext(), initializationStatus -> {

        });
        //MobileAds.openDebugMenu(getContext(),"ca-app-pub-2980262928639137/9972463179");
//        mAdView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                // Code to be executed when an ad finishes loading.
//                Log.v("AdLoad", "onAdLoaded");
//            }
//
//            @Override
//            public void onAdFailedToLoad(LoadAdError adError) {
//                // Code to be executed when an ad request fails.
//                Log.v("AdLoad", "onAdFailedToLoad:" + adError.toString());
//            }
//
//            @Override
//            public void onAdOpened() {
//                // Code to be executed when an ad opens an overlay that
//                // covers the screen.
//                Log.v("AdLoad", "onAdOpened");
//            }
//
//            @Override
//            public void onAdClicked() {
//                // Code to be executed when the user clicks on an ad.
//                Log.v("AdLoad", "onAdClicked");
//            }
//
//            @Override
//            public void onAdClosed() {
//                // Code to be executed when the user is about to return
//                // to the app after tapping on an ad.
//                Log.v("AdLoad", "onAdClosed");
//            }
//        });
    }
    public void loadAd()
    {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

}
