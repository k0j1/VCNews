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

public class HomeViewModel extends ViewModel
{
    MainActivity mActivity;
    HomeFragment mFragment;
    private SharedPreferences mPrefs;

    private MutableLiveData<List<RssItem>> mItems;
    private MutableLiveData<List<RssItem>> mCategoryItems;
    public MutableLiveData<List<RssItem>> getItems(MainActivity ac, HomeFragment fragment)
    {
        if(ac != null)
        {
            mActivity = ac;
            mPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        }
        if(fragment != null)
        {
            mFragment = fragment;
        }
        if(mItems == null)
        {
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
            mItems = new MutableLiveData<List<RssItem>>();
            List<RssItem> items = new ArrayList<RssItem>();
            mItems.setValue(items);

            //MainActivity activity = (MainActivity)this.getActivity();
            RssItemParserTask task = new RssItemParserTask(mActivity, mFragment);
//            if(null!=strSearchText) task.SetSearchText(strSearchText);
//            if(null!=strCategory) task.SetCategory(strCategory);
            // 全てから
            ArrayList<String> strUrlArray = new ArrayList<String>();
            ArrayList<String> strNameArray = new ArrayList<String>();
            String strUrl[] = mActivity.getResources().getStringArray(R.array.rss_url);
            String strName[] = mActivity.getResources().getStringArray(R.array.rss_site_name);
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
            mCategoryItems = new MutableLiveData<List<RssItem>>();
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
}