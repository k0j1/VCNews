package com.takaharabooks.app.vcnews.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.takaharabooks.app.vcnews.MainActivity;
import com.takaharabooks.app.vcnews.R;
import com.takaharabooks.app.vcnews.pref.DB_Data;
import com.takaharabooks.app.vcnews.ui.common.ListViewFunc;
import com.takaharabooks.app.vcnews.ui.item.RssItem;
import com.takaharabooks.app.vcnews.ui.item.RssItemListAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel = null;
    private View mFragmentHome;
    private RssItem mSearchItem = null;
    public void setSearchSiteName(CharSequence strName){ mSearchItem = new RssItem(); mSearchItem.setSiteName(strName); }

    protected RssItemListAdapter mAdapter;
//    protected SpinnerAdapter mSpinnerAdapter;
//    protected ConbinateAdView m_csAd = null;
    private DB_Data m_dbData = null;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
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
     * @param   items
     * @return	none
     ***********************************************************************************************/
    public void InitListView(List<RssItem> items)
    {
        // mSearchItemがセットされている場合は表示リストを絞り込み
        List<RssItem> dispItems;
        if(mSearchItem != null && mSearchItem.getSiteName()!="すべて")
        {
            dispItems = new ArrayList();
            for(int nIndex=0;nIndex<items.size();nIndex++)
            {
                RssItem item = items.get(nIndex);
                String strSearch = mSearchItem.getSiteName().toString();
                String strSiteName = item.getSiteName().toString();
                if(strSearch.equals(strSiteName))
                {
                    dispItems.add(item);
                }
            }
        }
        else
        {
            dispItems = items;
        }
        // プログレスバー削除
        ProgressBar progressBar = (ProgressBar) mFragmentHome.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        // リスト表示
        //int nColor = getContext().getColor(R.color.text_color);
        //int nColor2 = getActivity().getColor(R.color.text_color);
        //int nColor3 = requireContext().getColor(R.color.text_color);
        //int nColor4 = requireActivity().getColor(R.color.text_color);
        ListView RssList = (ListView)mFragmentHome.findViewById(R.id.layout_rss_listview);
        mAdapter = new RssItemListAdapter(this.getContext(), this.getActivity(), dispItems, m_dbData);
        // アダプタをリストビューにセットする
        final ListViewFunc.DateComp dateComp = new ListViewFunc.DateComp();
        mAdapter.sort(dateComp);
        ListViewFunc.InitListView((MainActivity)getActivity(), m_dbData, dispItems, RssList, mAdapter);
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
//                        homeViewModel.ReloadItems().observe((MainActivity) getActivity(), item ->
//                        {
//                            // Update the UI.
//                        });
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