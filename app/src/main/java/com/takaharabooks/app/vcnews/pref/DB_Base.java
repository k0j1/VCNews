package com.takaharabooks.app.vcnews.pref;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB_Base extends SQLiteOpenHelper
{
    static public final int DB_MODE_READ = 0;
    static public final int DB_MODE_WRITE = 1;

    protected Context m_csContext;

    protected SQLiteDatabase m_csDB = null;
    // 設定情報
    //public Preferences_Setting m_csSetting;

    public DB_Base(Context context, String strDBName, int nVer)
    {
        super(context, strDBName, null, nVer);
        // ☆子で定義
    }

    // 破棄されるときはＤＢを閉じる
    protected void finalize(){
        CloseDB();
    }

    //データベースオブジェクトを取得する（データベースにアクセスするとDBがない場合は作成される。）
    public boolean InitDBBase(int nMode){
        if( null == m_csDB ){
            try{
                switch(nMode){
                    case DB_MODE_READ:		m_csDB = getReadableDatabase();		break;
                    case DB_MODE_WRITE:		m_csDB = getWritableDatabase();		break;
                }
            }catch(SQLiteException e){
                //Toast.makeText(m_csContext, "Please Try Again...\n【DB Error】", Toast.LENGTH_SHORT);
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    // DBをclose
    public void CloseDB(){
        try{
            if( null != m_csDB && m_csDB.isOpen() ){
                m_csDB.close();
                m_csDB = null;
            }
        }catch(Exception e){}
    }

    // @Override onCreate
    public void onCreate(SQLiteDatabase db)
    {
        // ☆子で定義
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // ☆子で定義
    }

    public SQLiteDatabase GetDB(){
        return m_csDB;
    }

    //////////////////////////////////////////////////////
    // DBがロックされていないかを判定
    public boolean IsDBLocked(SQLiteDatabase db){
        if(null != db) return db.isDbLockedByOtherThreads();
        else return true;
    }

    //////////////////////////////////////////////////////
    // DBが有効かを判定
    public boolean IsDBValid(SQLiteDatabase db){
        boolean bRet = true;
        try{
            if(db == null) throw new Exception();
            if(IsDBLocked(db)) throw new Exception();				// ロックされていた場合はなにもしない
            //if(!db.isOpen() ) db = m_csDB;						// オープンされていない場合はメンバ変数のDB取得
            if(db == null || !db.isOpen() ) throw new Exception();	// それでもオープンされていない場合は終了
        }catch(Exception e){
            // DB無効
            //Toast.makeText(mContext, mContext.getString(R.string.MSG_ERR_MISS_DATA_READING), Toast.LENGTH_LONG);
            Log.v("DB", "DB is invalid.");
            bRet = false;
        }
        return bRet;
    }


    // DB書き込み(ContentValuesを使用する場合）
    public void writeDB(Context con, ContentValues values, String strDBTableName, String StrWhere){
        int colNum = m_csDB.update(strDBTableName, values, StrWhere, null);
        if(colNum == 0){
            m_csDB.insert(strDBTableName, "", values);
        }
        //SDAccess.BackupDB(con, DB_Data.DB_NAME);
    }

    // DBにInsert(ContentValuesを使用する場合）
    public void InsertDB(Context con, ContentValues values, String strDBTableName){
        m_csDB.insert(strDBTableName, "", values);
        //SDAccess.BackupDB(con, DB_Data.DB_NAME);
    }

    // DB読み込み（SQL文を直接発行,　戻り値　Cursor）
    public Cursor readDB(String strSQL) throws Exception{
        Cursor csCursor = m_csDB.rawQuery(strSQL, null);
        if(csCursor.getCount() == 0){
            throw new Exception();
        }
        return csCursor;
    }

    // DB読み込み（SQL文を直接発行,　一行戻ってくる場合　戻り値　int）
    public int readDBInt(String strSQL, String strColumn) throws Exception{
        SQLiteCursor csCursor = (SQLiteCursor)m_csDB.rawQuery(strSQL, null);
        if(csCursor.getCount() == 0){
            throw new Exception();
        }
        int nColumn = csCursor.getColumnIndex(strColumn);
        csCursor.moveToFirst();
        int nValue = csCursor.getInt(nColumn);
        csCursor.close();
        return nValue;
    }

    // DB読み込み（SQL文を直接発行,　一行戻ってくる場合　戻り値　String）
    public String readDBString(String strSQL, String strColumn) throws Exception{
        SQLiteCursor csCursor = (SQLiteCursor)m_csDB.rawQuery(strSQL, null);
        if(csCursor.getCount() == 0){
            throw new Exception();
        }
        int nColumn = csCursor.getColumnIndex(strColumn);
        csCursor.moveToFirst();
        String strValue = csCursor.getString(nColumn);
        csCursor.close();
        return strValue;
    }

    // DB読み込み
    public int readDB(String strDBTableName, String[] strCulumn) throws Exception{
        Cursor c = m_csDB.query(strDBTableName, strCulumn, "id=0", null, null, null, null, null);
        if(c.getCount()==0) throw new Exception();
        c.moveToFirst();
        int nValue = c.getInt(1);
        c.close();
        return nValue;
    }

    // DB読み込み（SQL文を直接発行,　戻り値　Cursor）
    public void DeleteDB(Context con, String strDBTableName, String StrWhere){
        m_csDB.delete(strDBTableName, StrWhere, null);
        //SDAccess.BackupDB(con, DB_Data.DB_NAME);
    }



    /*****************************************
     *  ＤＢから最大値を取得
     ****************************************/
    public int GetMax(String strGetItem, String strTableName){
        Cursor csCur;
        // ＤＢからデータの取得
        try{
            String strQuery = String.format( "select max(%s) from %s ",
                    strGetItem, strTableName
            );
            Log.v("DB_Data", strQuery);
            csCur = readDB(strQuery);
        }catch(Exception e){
            // 例外時は何もしない
            return 0;
        }
        csCur.moveToFirst();
        int nMaxNum = csCur.getInt(0);
        csCur.moveToNext();
        csCur.close();

        return nMaxNum;

    }

}
