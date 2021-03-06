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
     * 2ch???RSS?????????????????????????????????<br>
     * ?????????????????????????????????????????????????????????????????????
     *
     * @param	strSearchText		???????????????
     * @param	strCategory			??????????????????
     * @return	none
     ***********************************************************************************************/
    public void LoadRssItem(String strSearchText, String strCategory)
    {
        //if(null!=mItems) mItems.clear();
        // Web??????????????????
        if(null==mItems)
        {
            mItems = new MutableLiveData<>();
            List<RssItem> items = new ArrayList<>();
            mItems.setValue(items);

            task = new RssItemParserTask(this);

            // ????????????
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
        // ???????????????????????????????????????
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
            // ???????????????
            //mFragment.InitListView(mCategoryItems);
        }

    }

    public void LoadFragment(List<RssItem> list)
    {
        // mItems?????????
        AddRssItem(list);

        int nMax = mFragmentList.size();
        for(int nIndex=0; nIndex<nMax; nIndex++)
        {
            HomeFragment frag = mFragmentList.get(nIndex);
            if(frag.isVisible())
            {
                // ???????????????
                //frag.AddRssItem(list);
                //mFragment.InitCategory();
                frag.InitListView(list);
            }
        }
        mbLoading = false;
        //if(mbLoadEnd!=null) mbLoadEnd.setValue(true);
    }
}