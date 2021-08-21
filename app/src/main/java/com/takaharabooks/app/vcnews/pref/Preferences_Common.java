package com.takaharabooks.app.vcnews.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.takaharabooks.app.vcnews.R;

import java.util.ArrayList;

public class Preferences_Common
{
    //**********************************************************
    // 定義
    //**********************************************************
    static public class SiteInfo
    {
        public int nID;
        public CharSequence strSiteName;
        public CharSequence strSiteUrl;
        public CharSequence strCategory;
        public SiteInfo() { Init(); }
        void Init(){ nID = 0; strSiteName = ""; strSiteUrl = ""; strCategory=""; }
    }

    Context mContext;
    SharedPreferences m_csData;
    public Preferences_Common(Context csContext)
    {
        mContext = csContext;
        m_csData = PreferenceManager.getDefaultSharedPreferences(csContext);
    }

    // リソースのサイト情報配列を取得
    public ArrayList<SiteInfo> getSiteInfo()
    {
        ArrayList<SiteInfo> arSiteInfo = new ArrayList<>();
        int[] nTitles = getArraysInt(R.array.rss_site_name);
        String[] strName = getArraysString(R.array.rss_site_name);
        String[] strUrl = getArraysString(R.array.rss_url);
        String[] strCurrency = getArraysString(R.array.setting_currency);

        boolean bDefaultValue = true;
        // 各サイト名追加
        for(int nIndex=0; nIndex<strName.length; nIndex++)
        {
            String strKey = String.format("setting.rss_site2.%04d", nIndex+1);
            if(nIndex >= 2) bDefaultValue = false;
            if(m_csData.getBoolean(strKey, bDefaultValue))
            {
                SiteInfo Info = new SiteInfo();
                Info.nID = nTitles[nIndex];
                Info.strSiteName = strName[nIndex];
                Info.strSiteUrl = strUrl[nIndex];
                arSiteInfo.add(Info);
            }
        }
        // 各通貨名追加
        bDefaultValue = true;
        for(int nIndex=0; nIndex<strCurrency.length; nIndex++)
        {
            String strKey = String.format("setting.currency.%04d", nIndex+1);
            if(m_csData.getBoolean(strKey, bDefaultValue))
            {
                SiteInfo Info = new SiteInfo();
                Info.nID = nTitles[nIndex];
                Info.strCategory = strCurrency[nIndex];
                arSiteInfo.add(Info);
            }
        }

        return arSiteInfo;
    }
    public int[] getArraysInt(int nID) { return mContext.getResources().getIntArray(nID); }
    public String[] getArraysString(int nID) { return mContext.getResources().getStringArray(nID); }
}