package com.takaharabooks.app.vcnews.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences_Data
{
    //**********************定数定義*************************
    //***** プリファレンス定数
    public final static String PREF_AD_CHANGE			= "ad_change";
    public final static String PREF_NIGHT_LIGHT_MODE    = "mode_night_light";
    //**********************定数定義*************************

    // アプリのセッティング情報
    SharedPreferences m_csData;
    SharedPreferences.Editor m_csDataEditor;

    // コンストラクタ
    public Preferences_Data(Context csContext)
    {
        m_csData = PreferenceManager.getDefaultSharedPreferences(csContext);
        m_csDataEditor = m_csData.edit();
    }

    public void ClearAll(){
        m_csDataEditor.clear().commit();
    }

    /*************************************
     * 取得モードの情報	書き込み・読み込み
     **************************************/
    public void PutAdChange(int nKind){
        m_csDataEditor.putInt(PREF_AD_CHANGE, nKind);
        m_csDataEditor.commit();
    }
    public int GetAdChange(){
        return m_csData.getInt(PREF_AD_CHANGE, 0);
    }
    public void ClearAdChange(){
        m_csDataEditor.remove(PREF_AD_CHANGE).commit();
    }

    /*************************************
     * 取得モードの情報	書き込み・読み込み
     **************************************/
    public void PutModeNightLight(int nKind){
        m_csDataEditor.putInt(PREF_NIGHT_LIGHT_MODE, nKind);
        m_csDataEditor.commit();
    }
    public int GetModeNightLight(){
        return m_csData.getInt(PREF_NIGHT_LIGHT_MODE, 0);
    }
    public void ClearModeNightLight(){
        m_csDataEditor.remove(PREF_NIGHT_LIGHT_MODE).commit();
    }

}
