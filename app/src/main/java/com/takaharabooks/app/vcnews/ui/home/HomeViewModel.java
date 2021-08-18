package com.takaharabooks.app.vcnews.ui.home;

import android.content.SharedPreferences;

import com.takaharabooks.app.vcnews.MainActivity;
import com.takaharabooks.app.vcnews.R;
import com.takaharabooks.app.vcnews.ui.item.RssItem;
import com.takaharabooks.app.vcnews.ui.item.RssItemParserTask;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;

public class HomeViewModel extends ViewModel {
    private MainActivity mActivity;
    List<HomeFragment> mFragmentList = null;
    private SharedPreferences mPrefs = null;
    boolean mbLoading = false;

    String[] strUrl;
    String[] strName;
    RssItemParserTask task;

    private MutableLiveData<Boolean> mbLoadEnd;
    public MutableLiveData<Boolean> getLoadEnd()
    {
        mbLoadEnd = new MutableLiveData();
        mbLoadEnd.setValue(false);
        return mbLoadEnd;
    }
    private MutableLiveData<List<RssItem>> mItems;
    private MutableLiveData<List<RssItem>> mCategoryItems;
    public MutableLiveData<List<RssItem>> getItems(MainActivity ac, HomeFragment fragment)
    {
        if(ac != null && mPrefs == null)
        {
            mActivity = ac;
            mPrefs = PreferenceManager.getDefaultSharedPreferences(ac);
            strUrl = mActivity.getResources().getStringArray(R.array.rss_url);
            strName = mActivity.getResources().getStringArray(R.array.rss_site_name);
        }
        if(fragment != null)
        {
            if(mFragmentList == null)
            {
                mFragmentList = new ArrayList();
            }
            mFragmentList.add(fragment);
        }
        if(mPrefs != null && mFragmentList != null && mItems == null && !mbLoading)
        {
            mbLoading = true;
            LoadRssItem(null, "");
        }
        return mItems;
    }
    public MutableLiveData<List<RssItem>> ReloadItems()
    {
        if(mItems != null)
        {
            mItems.getValue().clear();
            mItems.setValue(null);
            mItems = null;
        }
        return getItems(null,null);
    }


    public HomeViewModel()
    {
//        mText = new MutableLiveData<>();
//        mText.setValue("This is home fragment");
    }

    public void AddRssItem(List<RssItem> items)
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
            mItems = new MutableLiveData();
            List<RssItem> items = new ArrayList();
            mItems.setValue(items);

            task = new RssItemParserTask(mActivity, this);

            // 全てから
            ArrayList<String> strUrlArray = new ArrayList();
            ArrayList<String> strNameArray = new ArrayList();
            boolean bDefaultValue = true;
            for(int nIndex=0; nIndex<strName.length; nIndex++)
            {
                String strKey = "setting.rss_site2." + String.format("%04d", nIndex+1);
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
            mCategoryItems = new MutableLiveData();
            for(int nIndex=0; nIndex<mItems.getValue().size(); nIndex++)
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
        int nMax = mFragmentList.size();
        for(int nIndex=0; nIndex<nMax; nIndex++)
        {
            HomeFragment frag = mFragmentList.get(nIndex);
            if(frag.isVisible())
            {
                // データ表示
                frag.AddRssItem(list);
                //mFragment.InitCategory();
                frag.InitListView(list);
            }
        }
        mbLoading = false;
        if(mbLoadEnd!=null) mbLoadEnd.setValue(true);
    }
}