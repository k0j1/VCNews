package com.takaharabooks.app.vcnews.pref;

import android.app.Activity;
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
        public SiteInfo() { Init(); }
        void Init(){ nID = 0;strSiteName = ""; strSiteUrl = ""; }
    }

    SharedPreferences m_csData;
    public Preferences_Common(Context csContext)
    {
        m_csData = PreferenceManager.getDefaultSharedPreferences(csContext);
    }

    // リソースのサイト情報配列を取得
    public ArrayList<SiteInfo> getSiteInfo(Activity ac)
    {
        ArrayList<SiteInfo> arSiteInfo = new ArrayList<>();
        int[] nTitles = getArraysInt(ac, R.array.rss_site_name);
        String[] strName = getArraysString(ac, R.array.rss_site_name);
        String[] strUrl = getArraysString(ac, R.array.rss_url);

        boolean bDefaultValue = true;
        for(int nIndex=0; nIndex<strName.length; nIndex++)
        {
            String strKey = "setting.rss_site2." + String.format("%04d", nIndex+1);
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

        return arSiteInfo;
    }
    public int[] getArraysInt(Activity ac, int nID)
    {
        return ac.getResources().getIntArray(nID);
    }
    public String[] getArraysString(Activity ac, int nID)
    {
        return ac.getResources().getStringArray(nID);
    }
}