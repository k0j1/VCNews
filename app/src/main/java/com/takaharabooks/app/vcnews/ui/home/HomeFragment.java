package com.takaharabooks.app.vcnews.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel = null;
    private View mFragmentHome;
    private RssItem mSearchItem = null;
    public void setSearchInfo(CharSequence strName, CharSequence strCategory)
    {
        mSearchItem = new RssItem();
        mSearchItem.setSiteName(strName);
        mSearchItem.setCategory(strCategory);
    }

    protected RssItemListAdapter mAdapter;
    private DB_Data m_dbData = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
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

        return mFragmentHome;
    }

    /***********************************************************************************************
     * リストの初期設定<br>
     * 2chから取得したデータを表示
     *
     * @return	none
     ***********************************************************************************************/
    public void InitListView(List<RssItem> items)
    {
        // mSearchItemがセットされている場合は表示リストを絞り込み
        List<RssItem> dispItems;
        String strSiteName = mSearchItem.getSiteName().toString();
        String strCategory = mSearchItem.getCategory().toString();
        if(mSearchItem != null && !strSiteName.equals("すべて"))
        {
            dispItems = new ArrayList<>();
            // サイト名が空でなければ該当サイトのみのリスト作成
            if(!strSiteName.isEmpty())
            {
                for (int nIndex = 0; nIndex < items.size(); nIndex++) {
                    RssItem item = items.get(nIndex);
                    String strItemSiteName = item.getSiteName().toString();
                    if (strSiteName.equals(strItemSiteName)) {
                        dispItems.add(item);
                    }
                }
            }

            // 通貨名が空でなければ該当通貨のカテゴリのみのリスト作成
            if(!strCategory.isEmpty())
            {
                for (int nIndex = 0; nIndex < items.size(); nIndex++) {
                    RssItem item = items.get(nIndex);
                    List<CharSequence> strItemCategorys = item.getCategorys();
                    for(CharSequence strItemCategory : strItemCategorys)
                    {
                        if(strCategory.equals(strItemCategory.toString()))
                        {
                            dispItems.add(item);
                            break;
                        }
                    }
                }
            }
        }
        else
        {
            dispItems = items;
        }
        // プログレスバー削除
        ProgressBar progressBar = mFragmentHome.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        // リスト表示
        ListView RssListView = mFragmentHome.findViewById(R.id.layout_rss_listview);
        mAdapter = new RssItemListAdapter(this.getContext(), dispItems, m_dbData);
        // アダプタをリストビューにセットする
        final ListViewFunc.DateComp dateComp = new ListViewFunc.DateComp();
        mAdapter.sort(dateComp);
        ListViewFunc.InitListView((MainActivity)getActivity(), m_dbData, dispItems, RssListView, mAdapter);
    }

    /***********************************************************************************************
     * RSSデータの初期設定<br>
     * 2chから取得したデータの変更を受け取りリストビューセット
     *
     * @return	none
     ***********************************************************************************************/
    public void InitRssData()
    {
        // Create the observer which updates the UI.
        final Observer<List<RssItem>> itemsObserver = items ->
        {
            if(items != null)
            {
                if(items.size() > 0)
                {
                    InitListView(items);
                }
            }
        };
        homeViewModel.getItems(this).observe(getViewLifecycleOwner(), itemsObserver);
    }

}