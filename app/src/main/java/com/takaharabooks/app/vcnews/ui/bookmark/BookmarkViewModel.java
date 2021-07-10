package com.takaharabooks.app.vcnews.ui.bookmark;

import com.takaharabooks.app.vcnews.pref.DB_Data;
import com.takaharabooks.app.vcnews.ui.item.RssItem;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BookmarkViewModel extends ViewModel
{
    private BookmarkFragment mFragment;
    private DB_Data m_dbData;
    private MutableLiveData<List<RssItem>> mItems;
    public LiveData<List<RssItem>> getItems(DB_Data dbData, BookmarkFragment fragment)
    {
        if(dbData != null)
        {
            m_dbData = dbData;
        }
        if(fragment != null)
        {
            mFragment = fragment;
        }
        if(mItems == null && m_dbData != null)
        {
            mItems = new MutableLiveData<List<RssItem>>();
            List<RssItem> items = new ArrayList<RssItem>();
            mItems.setValue(items);
            AddRssItem(m_dbData.GetFavoriteAllData());
        }
        if(mItems != null && mFragment != null)
        {
            mFragment.InitListView(mItems.getValue());
        }
        return mItems;
    }
    public LiveData<List<RssItem>> ReloadItems(DB_Data dbData)
    {
        if(mItems != null)
        {
            mItems.getValue().clear();
            mItems.setValue(null);
            mItems = null;
        }
        return getItems(dbData, null);
    }
    public boolean clearItems()
    {
        boolean bRet = false;
        if(mItems != null)
        {
            mItems.getValue().clear();
            mItems.setValue(null);
            mItems = null;
            bRet = true;
        }
        return bRet;
    }

    public BookmarkViewModel()
    {
//        mText = new MutableLiveData<>();
//        mText.setValue("This is Bookmark fragment");
    }

    public void AddRssItem(List<RssItem> items)
    {
        if(null!=mItems && null != mItems.getValue())
        {
            mItems.getValue().addAll(items);
        }
    }
}