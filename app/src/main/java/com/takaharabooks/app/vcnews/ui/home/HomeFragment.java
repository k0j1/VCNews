package com.takaharabooks.app.vcnews.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.takaharabooks.app.vcnews.MainActivity;
import com.takaharabooks.app.vcnews.R;
import com.takaharabooks.app.vcnews.pref.DB_Data;
import com.takaharabooks.app.vcnews.ui.common.ListViewFunc;
import com.takaharabooks.app.vcnews.ui.item.RssItem;
import com.takaharabooks.app.vcnews.ui.item.RssItemListAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel = null;
    private View mFragmentHome;
    private AdView mAdView = null;

    protected RssItemListAdapter mAdapter;
//    protected SpinnerAdapter mSpinnerAdapter;
//    protected ConbinateAdView m_csAd = null;
    AdLoader mAdLoader;
    private DB_Data m_dbData = null;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        if(m_dbData == null)
        {
            m_dbData = new DB_Data(this.getContext());
            m_dbData.InitDB();
        }

        if(homeViewModel == null)
        {
            homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
            mFragmentHome = inflater.inflate(R.layout.fragment_home, container, false);

            // 初回RSSデータ読み込み
            InitAd();
            InitRssData();
        }
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });


        return mFragmentHome;
    }

//    OnReloadListListener listener;
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        try {
//            listener = (OnReloadListListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() + " must implement OnReloadListListener");
//        }
//    }
//    public interface OnReloadListListener
//    {
//        public void onInitList();
//        public void onReloadList();
//    }

    /***********************************************************************************************
     * リストの初期設定<br>
     * 2chから取得したデータを表示
     *
     * @param	items
     * @return	none
     ***********************************************************************************************/
    public void InitListView(List<RssItem> items)
    {
        // プログレスバー削除
        ProgressBar progressBar = (ProgressBar) mFragmentHome.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        // リスト表示
        ListView RssList = (ListView)mFragmentHome.findViewById(R.id.layout_rss_listview);
        mAdapter = new RssItemListAdapter(this.getContext(), getActivity(), items, m_dbData);
        // アダプタをリストビューにセットする
        final ListViewFunc.DateComp dateComp = new ListViewFunc.DateComp();
        mAdapter.sort(dateComp);
        ListViewFunc.InitListView((MainActivity)getActivity(), m_dbData, items, RssList, mAdapter);
    }

    public void InitRssData()
    {
        // Create the observer which updates the UI.
        final Observer<List<RssItem>> itemsOvserver = new Observer<List<RssItem>>()
        {
            @Override
            public void onChanged(@Nullable final List<RssItem> items)
            {
                if(items != null)
                {
                    if(items.size() > 0)
                    {
                        InitListView(items);
                    }
                    else
                    {
                        homeViewModel.ReloadItems().observe((MainActivity) getActivity(), item ->
                        {
                            // Update the UI.
                        });
                    }
                }
            }
        };
        homeViewModel.getItems((MainActivity) getActivity(), this).observe(getViewLifecycleOwner(), itemsOvserver);
//        if(null == mItems)
//        {
//            LoadRssItem(null, "");
//        }
//        else
//        {
//            InitListView(mItems);
//        }
    }

    /*************************************
     * 広告の初期設定
     **************************************/
    public void InitAd()
    {
        if(mAdView != null) return;
        mAdView = mFragmentHome.findViewById(R.id.adView);

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener()
        {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus)
            {
            }
        });
        //MobileAds.openDebugMenu(getContext(),"ca-app-pub-2980262928639137/9972463179");
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.v("AdLoad", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                Log.v("AdLoad", "onAdFailedToLoad:" + adError.toString());
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.v("AdLoad", "onAdOpened");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.v("AdLoad", "onAdClicked");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.v("AdLoad", "onAdClosed");
            }
        });
        loadAd();
    }
    public void loadAd()
    {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

//    public void InitAd()
//    {
//        mAdLoader = new AdLoader.Builder(getContext(), "ca-app-pub-3940256099942544/2247696110")
//                .forNativeAd(new NativeAd.OnNativeAdLoadedListener()
//                {
//                    @Override
//                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd)
//                    {
//                    }
//                })
//                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
//                    @Override
//                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd)
//                    {
//                        UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
//                                .inflate(R.layout.ad_unified, null);
//                        // This method sets the text, images and the native ad, etc into the ad
//                        // view.
//                        populateUnifiedNativeAdView(unifiedNativeAd, adView);
//                        if (mAdLoader.isLoading()) {
//                            RssItem rssAd = new RssItem();
//                            rssAd.setAd(true);
//                            rssAd.setTitle(nativeAd.getHeadline());
//                            rssAd.setDate("スポンサー");
//                            rssAd.setLink(nativeAd.get);
//                        } else {
//                            // The AdLoader has finished loading ads.
//                        }
//
//                    }
//                .withAdListener(new AdListener()
//                {
//                    // AdListener callbacks like OnAdFailedToLoad, OnAdOpened, OnAdClicked and
//                    // so on, can be overridden here.
//                })
//                .withNativeAdOptions(new NativeAdOptions.Builder()
//                        // Methods in the NativeAdOptions.Builder class can be
//                        // used here to specify individual options settings.
//                        .build())
//                .build();
//        mAdLoader.loadAd(new AdRequest.Builder().build());
//    }

    public void ReloadRssData()
    {
        homeViewModel.ReloadItems();
    }

    public void AddRssItem(List<RssItem> items)
    {
        homeViewModel.AddRssItem(items);
    }

}