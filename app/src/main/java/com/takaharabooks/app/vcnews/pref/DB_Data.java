package com.takaharabooks.app.vcnews.pref;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.takaharabooks.app.vcnews.ui.item.RssItem;

import java.util.ArrayList;
import java.util.List;

public class DB_Data extends DB_Base
{
    //**********************定数定義**********************
    //***** 数値
    // Version
    public static final int DB_VER = 1;
    //***** 文字列
    // DB名
    public static final String	DB_NAME = "vcnews.db";
    // テーブル名
    public static final String	DB_FAVORITE_TBL = "tb_favorite_data";
    public static final String	DB_HISTORY_TBL = "tb_history_data";
    // View名
    //public static final String	DB_VIEW = "vw_data";
    // 列名
    public static final String	DB_COL_FAVORITE_ID			= "favorite_id";
    public static final String	DB_COL_FAVORITE_SITE_NAME	= "favorite_site_name";
    public static final String	DB_COL_FAVORITE_TITLE		= "favorite_title";
    public static final String	DB_COL_FAVORITE_DESCRIPTION	= "favorite_description";
    public static final String	DB_COL_FAVORITE_CONTENT 	= "favorite_content";
    public static final String	DB_COL_FAVORITE_DATE		= "favorite_date";
    public static final String	DB_COL_FAVORITE_LINK 		= "favorite_link";
    public static final String	DB_COL_FAVORITE_CREATOR 	= "favorite_creator";
    public static final String	DB_COL_FAVORITE_CATEGORY	= "favorite_category";
    public static final String	DB_COL_FAVORITE_TAG        	= "favorite_tag";
    public static final String	DB_COL_FAVORITE_IMAGE_LINK 	= "favorite_image_link";
    public static final String	DB_COL_FAVORITE_SITE_LINK 	= "favorite_site_link";

    public static final String	DB_COL_HISTORY_ID			= "history_id";
    public static final String	DB_COL_HISTORY_SITE_NAME	= "history_site_name";
    public static final String	DB_COL_HISTORY_TITLE		= "history_title";
    public static final String	DB_COL_HISTORY_DESCRIPTION	= "history_description";
    public static final String	DB_COL_HISTORY_CONTENT 	    = "history_content";
    public static final String	DB_COL_HISTORY_DATE		    = "history_date";
    public static final String	DB_COL_HISTORY_LINK 		= "history_link";
    public static final String	DB_COL_HISTORY_CREATOR 	    = "history_creator";
    public static final String	DB_COL_HISTORY_CATEGORY	    = "history_category";
    public static final String	DB_COL_HISTORY_TAG        	= "history_tag";
    public static final String	DB_COL_HISTORY_IMAGE_LINK 	= "history_image_link";
    public static final String	DB_COL_HISTORY_SITE_LINK 	= "history_site_link";
    // クエリ
    public static final String	DB_QUERY_SELECT_FAV_DATA =
            "select * from " + DB_FAVORITE_TBL;
    public static final String	DB_QUERY_SELECT_FAV_ONE_DATA =
            "select * from " + DB_FAVORITE_TBL + " where " + DB_COL_FAVORITE_ID + " = %d";
    public static final String	DB_QUERY_SELECT_FAV_ONE_DATA_BY_LINK =
            "select * from " + DB_FAVORITE_TBL + " where " + DB_COL_FAVORITE_LINK + " = \"%s\"";
    public static final String	DB_QUERY_SELECT_HIS_DATA =
            "select * from " + DB_HISTORY_TBL;
    public static final String	DB_QUERY_SELECT_HIS_ONE_DATA =
            "select * from " + DB_HISTORY_TBL + " where " + DB_COL_HISTORY_ID + " = %d";
    public static final String	DB_QUERY_SELECT_HIS_ONE_DATA_BY_LINK =
            "select * from " + DB_HISTORY_TBL + " where " + DB_COL_HISTORY_LINK + " = \"%s\"";
    //**********************定数定義**********************

    private Context m_csContext;
    //private SQLiteDatabase	m_csDB;

    // コンストラクタ
    public DB_Data(Context context)
    {
        super(context, DB_NAME, DB_VER);
        m_csContext = context;
    }

    //データベースオブジェクトを取得する（データベースにアクセスするとDBがない場合は作成される。）
    public void InitDB(){
        InitDBBase(DB_MODE_WRITE);
    }

    // @Override onCreate
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        CreateTBLForFavoriteData(db);
        CreateTBLForHistoryData(db);
    }

    // @Override onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Log.v("MyDBOpenHelper", "onUpgrade_Start");
        //Log.v("MyDBOpenHelper", "onUpgrade_End");
    }

    /*****************************************
     *  お気に入りテーブルを作成
     ****************************************/
    public void CreateTBLForFavoriteData(SQLiteDatabase db)
    {
        try{
            String sql = String.format(
                    "create table %s (" +
                            " %s integer primary key, %s text, %s text, " +
                            " %s text, %s text, %s text, " +
                            " %s text, %s text, %s text, " +
                            " %s text, %s text, %s text " +
                            ")",
                    DB_FAVORITE_TBL,
                    DB_COL_FAVORITE_ID,  	        DB_COL_FAVORITE_SITE_NAME,      DB_COL_FAVORITE_TITLE,
                    DB_COL_FAVORITE_DESCRIPTION,    DB_COL_FAVORITE_CONTENT,        DB_COL_FAVORITE_DATE,
                    DB_COL_FAVORITE_LINK,	        DB_COL_FAVORITE_CREATOR,        DB_COL_FAVORITE_CATEGORY,
                    DB_COL_FAVORITE_TAG,            DB_COL_FAVORITE_IMAGE_LINK,     DB_COL_FAVORITE_SITE_LINK
            );
            db.execSQL(sql);
            Log.v("CreateTBLForFolder", "SUCCESS");
        }catch(SQLException e){
            Log.v("CreateTBLForFolder", e.getMessage());
            return;
        }
    }
    public void CreateTBLForHistoryData(SQLiteDatabase db)
    {
        try{
            String sql = String.format(
                    "create table %s (" +
                            " %s integer primary key, %s text, %s text, " +
                            " %s text, %s text, %s text, " +
                            " %s text, %s text, %s text, " +
                            " %s text, %s text, %s text " +
                            ")",
                    DB_HISTORY_TBL,
                    DB_COL_HISTORY_ID,  	        DB_COL_HISTORY_SITE_NAME,      DB_COL_HISTORY_TITLE,
                    DB_COL_HISTORY_DESCRIPTION,     DB_COL_HISTORY_CONTENT,        DB_COL_HISTORY_DATE,
                    DB_COL_HISTORY_LINK,	        DB_COL_HISTORY_CREATOR,        DB_COL_HISTORY_CATEGORY,
                    DB_COL_HISTORY_TAG,             DB_COL_HISTORY_IMAGE_LINK,     DB_COL_HISTORY_SITE_LINK
            );
            db.execSQL(sql);
            Log.v("CreateTBLForFolder", "SUCCESS");
        }catch(SQLException e){
            Log.v("CreateTBLForFolder", e.getMessage());
            return;
        }
    }

    /*****************************************
     *  お気に入り情報を取得（From ID）
     ****************************************/
    public RssItem GetFavoriteData(int nFavoriteID)
    {
        Cursor csCur;
        // ＤＢからデータの取得
        try{
            String strQuery = String.format( DB_QUERY_SELECT_FAV_ONE_DATA, nFavoriteID );
            Log.v("DB_Data", strQuery);
            csCur = readDB(strQuery);
        }catch(Exception e){
            // 例外時は何もしない
            e.printStackTrace();
            return null;
        }

        csCur.moveToFirst();
        RssItem sItem = new RssItem();
        sItem.setFavoriteNo(csCur.getInt(0));
        sItem.setSiteName(csCur.getString(1));
        sItem.setTitle(csCur.getString(2));
        sItem.setDate(csCur.getString(5));
        sItem.setLink(csCur.getString(6));
        sItem.setCategory(csCur.getString(8));
        sItem.setImageURL(csCur.getString(10));
        csCur.moveToNext();
        csCur.close();

        return sItem;
    }

    /*****************************************
     *  お気に入り情報を取得（From Link）
     ****************************************/
    public RssItem GetFavoriteData(String strLink)
    {
        Cursor csCur;
        // ＤＢからデータの取得
        try{
            String strQuery = String.format( DB_QUERY_SELECT_FAV_ONE_DATA_BY_LINK, strLink );
            Log.v("DB_Data", strQuery);
            csCur = readDB(strQuery);
        }catch(Exception e){
            // 例外時は何もしない
            e.printStackTrace();
            return null;
        }

        csCur.moveToFirst();
        RssItem sItem = new RssItem();
        sItem.setFavoriteNo(csCur.getInt(0));
        sItem.setSiteName(csCur.getString(1));
        sItem.setTitle(csCur.getString(2));
        sItem.setDate(csCur.getString(5));
        sItem.setLink(csCur.getString(6));
        sItem.setCategory(csCur.getString(8));
        sItem.setImageURL(csCur.getString(10));
        csCur.moveToNext();
        csCur.close();

        return sItem;
    }

    /*****************************************
     *  お気に入り情報リストを全件取得
     ****************************************/
    public List<RssItem> GetFavoriteAllData()
    {
        List<RssItem> sItems = new ArrayList<RssItem>();

        Cursor csCur;
        // ＤＢからデータの取得
        try{
            String strQuery = DB_QUERY_SELECT_FAV_DATA;
            Log.v("DB_Data", strQuery);
            csCur = readDB(strQuery);
        }catch(Exception e){
            // 例外時は何もしない
            e.printStackTrace();
            return sItems;
        }

        int nDataNum = csCur.getCount();

        csCur.moveToLast();
        for(int nIndex=0; nIndex<nDataNum; nIndex++)
        {
            RssItem sItem = new RssItem();
            sItem.setFavoriteNo(csCur.getInt(0));
            sItem.setSiteName(csCur.getString(1));
            sItem.setTitle(csCur.getString(2));
            sItem.setDate(csCur.getString(5));
            sItem.setLink(csCur.getString(6));
            sItem.setCategory(csCur.getString(8));
            sItem.setImageURL(csCur.getString(10));
            sItems.add(sItem);
            csCur.moveToPrevious();
        }
        csCur.close();

        return sItems;
    }
    public List<RssItem> GetHistoryAllData()
    {
        List<RssItem> sItems = new ArrayList<RssItem>();

        Cursor csCur;
        // ＤＢからデータの取得
        try{
            String strQuery = DB_QUERY_SELECT_HIS_DATA;
            Log.v("DB_Data", strQuery);
            csCur = readDB(strQuery);
        }catch(Exception e){
            // 例外時は何もしない
            e.printStackTrace();
            return sItems;
        }

        int nDataNum = csCur.getCount();

        csCur.moveToLast();
        for(int nIndex=0; nIndex<nDataNum; nIndex++)
        {
            RssItem sItem = new RssItem();
            sItem.setFavoriteNo(csCur.getInt(0));
            sItem.setSiteName(csCur.getString(1));
            sItem.setTitle(csCur.getString(2));
            sItem.setDate(csCur.getString(5));
            sItem.setLink(csCur.getString(6));
            sItem.setCategory(csCur.getString(8));
            sItem.setImageURL(csCur.getString(10));
            sItems.add(sItem);
            csCur.moveToPrevious();
        }
        csCur.close();

        return sItems;
    }

    /*****************************************
     *  お気に入り情報の有無チェック（From ID）
     ****************************************/
    public boolean IsFavoriteData(int nFavoriteID)
    {
        boolean bRet = false;

        Cursor csCur;
        // ＤＢからデータの取得
        try{
            String strQuery = String.format( DB_QUERY_SELECT_FAV_ONE_DATA, nFavoriteID );
            Log.v("DB_Data", strQuery);
            csCur = readDB(strQuery);
        }catch(Exception e){
            // 例外時は何もしない
            return bRet;
        }

        int nDataNum = csCur.getCount();
        csCur.close();

        if(0<nDataNum) bRet = true;

        return bRet;
    }
    public boolean IsHistoryData(int nHistoryID)
    {
        boolean bRet = false;

        Cursor csCur;
        // ＤＢからデータの取得
        try{
            String strQuery = String.format( DB_QUERY_SELECT_HIS_ONE_DATA, nHistoryID );
            Log.v("DB_Data", strQuery);
            csCur = readDB(strQuery);
        }catch(Exception e){
            // 例外時は何もしない
            return bRet;
        }

        int nDataNum = csCur.getCount();
        csCur.close();

        if(0<nDataNum) bRet = true;

        return bRet;
    }

    /*****************************************
     *  お気に入り情報の有無チェック（From Link）
     ****************************************/
    public boolean IsFavoriteData(String strLink)
    {
        boolean bRet = false;

        Cursor csCur;
        // ＤＢからデータの取得
        try{
            String strQuery = String.format( DB_QUERY_SELECT_FAV_ONE_DATA_BY_LINK, strLink );
            Log.v("DB_Data", strQuery);
            csCur = readDB(strQuery);
        }catch(Exception e){
            // 例外時は何もしない
            return bRet;
        }

        int nDataNum = csCur.getCount();
        csCur.close();

        if(0<nDataNum) bRet = true;

        return bRet;
    }
    public boolean IsHistoryData(String strLink)
    {
        boolean bRet = false;

        Cursor csCur;
        // ＤＢからデータの取得
        try{
            String strQuery = String.format( DB_QUERY_SELECT_HIS_ONE_DATA_BY_LINK, strLink );
            Log.v("DB_Data", strQuery);
            csCur = readDB(strQuery);
        }catch(Exception e){
            // 例外時は何もしない
            return bRet;
        }

        int nDataNum = csCur.getCount();
        csCur.close();

        if(0<nDataNum) bRet = true;

        return bRet;
    }

    /*****************************************
     *  データを挿入
     ****************************************/
    private void InsertData(String strDBName, RssItem sItem, int nID)
    {
        try{
            String sql = String.format(
                    "insert into %s values (" +
                            "%d, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s' " +
                    ")",
                    strDBName,
                    nID, sItem.getSiteName().toString(), sItem.getTitle().toString(),
                    "", "", sItem.getDate().toString(),
                    sItem.getLink().toString(), "", sItem.getCategory().toString(),
                    "", sItem.getImageURL().toString(), ""
            );
            m_csDB.execSQL(sql);
            Log.v("### InsertData", "SUCCESS");
        }catch(SQLException e){
            Log.v("### InsertData", e.getMessage());
            return;
        }
    }
    public void InsertFavoriteDataInfo(RssItem sItem)
    {
        if(!IsFavoriteData(sItem.getLink().toString()))
        {
            int nID = GetMaxFavoriteID() + 1;
            InsertData(DB_FAVORITE_TBL, sItem, nID);
        }
    }
    public void InsertHistoryDataInfo(RssItem sItem)
    {
        if(IsHistoryData(sItem.getLink().toString()))
        {
            DeleteHistoryData(sItem.getLink().toString());
        }
        int nID = GetMaxHistoryID() + 1;
        InsertData(DB_HISTORY_TBL, sItem, nID);
    }


    /*****************************************
     *  ＤＢからラベル別ファイル削除
     ****************************************/
    public void DeleteFavoriteData(String strLink)
    {
        String strWhere = String.format("%s = \"%s\"", DB_COL_FAVORITE_LINK, strLink);
        DeleteDB(m_csContext, DB_FAVORITE_TBL, strWhere);
    }
    public void DeleteHistoryData(String strLink)
    {
        String strWhere = String.format("%s = \"%s\"", DB_COL_HISTORY_LINK, strLink);
        DeleteDB(m_csContext, DB_HISTORY_TBL, strWhere);
    }
    public void DeleteAllHistoryData()
    {
        DeleteDB(m_csContext, DB_HISTORY_TBL, "");
    }

    /*****************************************
     *  ラベルの最大のインデックスＩＤを取得
     ****************************************/
    public int GetMaxFavoriteID(){
        return GetMax(DB_COL_FAVORITE_ID, DB_FAVORITE_TBL);
    }
    public int GetMaxHistoryID(){
        return GetMax(DB_COL_HISTORY_ID, DB_HISTORY_TBL);
    }

    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    // ＤＢ上に入れるデフォルトの値

    //private int[] m_stnDefxxx = {
    //};

}