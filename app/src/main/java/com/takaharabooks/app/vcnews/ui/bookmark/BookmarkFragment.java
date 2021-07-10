package com.takaharabooks.app.vcnews.ui.bookmark;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class BookmarkFragment extends Fragment {

    private BookmarkViewModel bookmarkViewModel;
    private View mRoot;
    private AdView mAdView;

    //protected List<RssItem> mItems = null;
    protected RssItemListAdapter mAdapter;
    private DB_Data m_dbData;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        bookmarkViewModel = new ViewModelProvider(requireActivity()).get(BookmarkViewModel.class);

        mRoot = inflater.inflate(R.layout.fragment_bookmark, container, false);

        m_dbData = new DB_Data(this.getContext());
        m_dbData.InitDB();

        // 初回RSSデータ読み込み
        InitRssData();
        //InitListView(mItems)
        InitAd();

        return mRoot;
    }

    /***********************************************************************************************
     * リストの初期設定<br>
     * 2chから取得したデータを表示
     *
     * @param	items
     * @return	none
     ***********************************************************************************************/
    public void InitListView(List<RssItem> items)
    {
        ListView RssList = (ListView)mRoot.findViewById(R.id.layout_rss_listview);
        mAdapter = new RssItemListAdapter(this.getContext(), getActivity(), items, m_dbData);
        // アダプタをリストビューにセットする
        ListViewFunc.InitListView((MainActivity)getActivity(), m_dbData, items, RssList, mAdapter);
    }

    //
    /***********************************************************************************************
     * RSSデータ読み込み<br>
     * ブックマークしたデータをDBから読み込んで表示
     *
     * @return	none
     ***********************************************************************************************/
    public void InitRssData()
    {
        bookmarkViewModel.clearItems();
        bookmarkViewModel.getItems(m_dbData, this).observe(getViewLifecycleOwner(), items ->
        {
            // Update the UI.
            if(items != null && items.size() > 0)
            {
                //InitListView(items);
            }
        });
    }

    /*************************************
     * 広告の初期設定
     **************************************/
    public void InitAd()
    {
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener()
        {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus)
            {

            }
        });
        mAdView = mRoot.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

}