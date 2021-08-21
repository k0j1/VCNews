package com.takaharabooks.app.vcnews.ui.home;

import android.content.SharedPreferences;

import com.takaharabooks.app.vcnews.R;
import com.takaharabooks.app.vcnews.ui.item.RssItem;
import com.takaharabooks.app.vcnews.ui.item.RssItemParserTask;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;

public class HomeViewModel extends ViewModel {
    //private MainActivity mActivity;
    List<HomeFragment> mFragmentList = null;
    private SharedPreferences mPrefs = null;
    boolean mbLoading = false;

    String[] strUrl = null;
    String[] strName = null;
    RssItemParserTask task;

//    private MutableLiveData<Boolean> mbLoadEnd;
//    public MutableLiveData<Boolean> getLoadEnd()
//    {
//        mbLoadEnd = new MutableLiveData();
//        mbLoadEnd.setValue(false);
//        return mbLoadEnd;
//    }
    private MutableLiveData<List<RssItem>> mItems = null;
    private MutableLiveData<List<RssItem>> mCategoryItems;
    public MutableLiveData<List<RssItem>> getItems(HomeFragment fragment)
    {
        if(mFragmentList == null)
        {
            mFragmentList = new ArrayList<>();
        }
        if(fragment != null)
        {
            mFragmentList.add(fragment);
        }

        if(!mbLoading && mItems == null)
        {
            mbLoading = true;

            if(fragment != null)
            {
                if (null == mPrefs) mPrefs = PreferenceManager.getDefaultSharedPreferences(fragment.getContext());
                if (null == strUrl) strUrl = fragment.getResources().getStringArray(R.array.rss_url);
                if (null == strName) strName = fragment.getResources().getStringArray(R.array.rss_site_name);
            }
            LoadRssItem(null, "");
        }
        return mItems;
    }
    public MutableLiveData<List<RssItem>> ReloadItems()
    {
        if(mbLoading) return null;

        if(mItems != null)
        {
            mItems.getValue().clear();
            mItems.setValue(null);
            mItems = null;
        }
        return getItems(null);
    }


    public HomeViewModel()
    {
//        mText = new MutableLiveData<>();
//        mText.setValue("This is home fragment");
    }

    private void AddRssItem(List<RssItem> items)
    {
        if(null!=mItems)
        {
            mItems.getValue().addAll(items);
        }
    }

    /***********************************************************************************************
     * 2chのRSSをＷＥＢから取得します<br>
     * 検索文字列かカテゴリのどちらか片方を指定します
     *
     * @param	strSearchText		検索文字列
     * @param	strCategory			検索カテゴリ
     * @return	none
     ***********************************************************************************************/
    public void LoadRssItem(String strSearchText, String strCategory)
    {
        //if(null!=mItems) mItems.clear();
        // Webから読み込み
        if(null==mItems)
        {
            mItems = new MutableLiveData<>();
            List<RssItem> items = new ArrayList<>();
            mItems.setValue(items);

            task = new RssItemParserTask(this);

            // 全てから
            ArrayList<String> strUrlArray = new ArrayList<>();
            ArrayList<String> strNameArray = new ArrayList<>();
            boolean bDefaultValue = true;
            for(int nIndex=0; nIndex<strName.length; nIndex++)
            {
                String strKey = String.format("setting.rss_site2.%04d", nIndex+1);
                if(nIndex >= 2) bDefaultValue = false;
                if(mPrefs.getBoolean(strKey, bDefaultValue))
                {
                    //true;
                    strUrlArray.add(strUrl[nIndex]);
                    strNameArray.add(strName[nIndex]);
                }
            }
            task.execute(strUrlArray, strNameArray);
        }
        // 取得済みのデータから絞込み
        else
        {
            mCategoryItems = new MutableLiveData<>();
            int nSize = mItems.getValue().size();
            for(int nIndex=0; nIndex<nSize; nIndex++)
            {
                RssItem item = mItems.getValue().get(nIndex);
                if(null!=strSearchText)
                {
                    //if(IsSearchText(strSearchText, item))
                    {
                        mCategoryItems.getValue().add(item);
                    }
                }
                else if(null!=strCategory)
                {
                    //if(IsCategory(strCategory, item))
                    {
                        mCategoryItems.getValue().add(item);
                    }
                }
            }
            // データ表示
            //mFragment.InitListView(mCategoryItems);
        }

    }

    public void LoadFragment(List<RssItem> list)
    {
        // mItemsに追加
        AddRssItem(list);

        int nMax = mFragmentList.size();
        for(int nIndex=0; nIndex<nMax; nIndex++)
        {
            HomeFragment frag = mFragmentList.get(nIndex);
            if(frag.isVisible())
            {
                // データ表示
                //frag.AddRssItem(list);
                //mFragment.InitCategory();
                frag.InitListView(list);
            }
        }
        mbLoading = false;
        //if(mbLoadEnd!=null) mbLoadEnd.setValue(true);
    }
}