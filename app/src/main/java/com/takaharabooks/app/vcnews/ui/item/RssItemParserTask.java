package com.takaharabooks.app.vcnews.ui.item;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Xml;

import com.takaharabooks.app.vcnews.ui.home.HomeViewModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RssItemParserTask extends AsyncTask<ArrayList<String>, Integer, RssItem>
{
    //private MainActivity mActivity;
    //private HomeFragment mFragment;
    private HomeViewModel mViewModel;
    private List<RssItem> mList;
    private ProgressDialog mProgressDialog;

//    private String mSearchText = "";
//    private String mCategory;
//    private boolean m_bCategory = false;

    // コンストラクタ
    public RssItemParserTask(HomeViewModel ViewModel)
    {
        mViewModel = ViewModel;
        //mCategory = "";
        mList = new ArrayList<>();
    }

//    public void SetSearchText(String strSearchText)
//    {
//        mSearchText = strSearchText;
//    }
//    public void SetCategory(String strCategory)
//    {
//        mCategory = strCategory;
//        m_bCategory = true;
//    }

    // タスクを実行した直後にコールされる
    @Override
    protected void onPreExecute()
    {
        // ライトオン、画面固定に設定
        //mActivity.InitSetting();
        // プログレスバーを表示する
        //startProgresDlg(0);
    }

    // プログレスバー更新処理： UIスレッドで実行される
    @Override
    protected void onProgressUpdate(Integer... progress)
    {
        //if(null!=progress && null!=progress[0]) setProgressValue(progress[0]);
    }

    // バックグラウンドにおける処理を担う。タスク実行時に渡された値を引数とする
    @SafeVarargs
    @Override
    protected final RssItem doInBackground(ArrayList<String>... params) {
        RssItem result = null;
        try {
            // HTTP経由でアクセスし、InputStreamを取得する
            ArrayList<String> strUrlArray = params[0];
            ArrayList<String> strNameArray = params[1];
            //setProgresDlgMax(strNameArray.size());
            for(int nIndex=0; nIndex<strUrlArray.size(); nIndex++)
            {
                //int nID = nIndex+1;
                //SharedPreferences prefs		= PreferenceManager.getDefaultSharedPreferences(mActivity);
                //boolean bEnable   		= prefs.getBoolean(SettingActivity.KEY_RSS_ + nID, true);
                // 設定画面で有効の場合のみ取得
                //if(bEnable)
                {
                    URL url = new URL(strUrlArray.get(nIndex));
                    InputStream is = url.openConnection().getInputStream();
                    parseXml(is, strNameArray.get(nIndex));
                }
                publishProgress(nIndex+1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ここで返した値は、onPostExecuteメソッドの引数として渡される
        return result;
    }

    // メインスレッド上で実行される
    @Override
    protected void onPostExecute(RssItem result)
    {
        // 例外処理
        //if(mActivity.isFinishing()) return;
        //if(mActivity.isDestroyed()) return;

        // フラグメント更新
        if(mViewModel != null)
        {
            mViewModel.LoadFragment(mList);
        }
    }

    /***********************************************************************************************
     * XMLをパースする
     ***********************************************************************************************/
    public void parseXml(InputStream is, String strName) throws IOException, XmlPullParserException
    {
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(is, null);
            int eventType = parser.getEventType();
            RssItem currentItem = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag;
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tag = parser.getName();
                        if (tag.equals("entry") || tag.equals("item"))
                        {
                            currentItem = new RssItem();
                        }
                        else if (currentItem != null)
                        {
                            String strLink = "";
                            switch (tag)
                            {
                                // 記事タイトル
                                case "title":
                                    currentItem.setTitle(parser.nextText());
                                    break;
                                // 日付
                                case "issued":
                                case "date":
                                case "pubDate":
                                    //日付が「Wed, 4 Jul 2001 12:08:56 -0700」のような場合の指定
                                    Date d = null;
                                    SimpleDateFormat df;
                                    String strDate = parser.nextText();
                                    //if(d == null)
                                    {
                                        df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", java.util.Locale.US);
                                        try {
                                            d = df.parse(strDate);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if(d == null)
                                    {
                                        //日付が「Wed, 4 Jul 2001 12:08:56 -0700」のような場合の指定
                                        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US);
                                        try {
                                            d = df.parse(strDate);
                                        }catch(ParseException e){
                                            e.printStackTrace();
                                        }
                                    }
                                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss", java.util.Locale.JAPAN);
                                    //日付が「2001-07-04 12:08:56」のような場合の指定
                                    //SimpleDateFormat df2 = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss", java.util.Locale.US);
                                    //Date d = df2.parse(parser.nextText());
                                    currentItem.setDate(df2.format(d));
                                    break;
                                // 記事リンク先
                                case "link":
                                    for(int i=0; i<parser.getAttributeCount(); i++)
                                    {
                                        if(parser.getAttributeName(i).equals("href")){
                                            strLink = parser.getAttributeValue(i);
                                        }
                                    }
                                    if(strLink.equals(""))
                                    {
                                        strLink = parser.nextText();
                                        //strLink = parser.getAttributeValue(null, "href");
                                    }
                                    currentItem.setLink(strLink);
                                    break;
                                // カテゴリ
                                case "subject":
                                case "category":
                                    currentItem.addCategory(parser.nextText());
                                    break;
                                // 画像のURL
                                case "enclosure":
                                // case "img":
                                    for(int i=0; i<parser.getAttributeCount(); i++)
                                    {
                                        if(parser.getAttributeName(i).equals("url")/* || parser.getAttributeName(i).equals("src")*/){
                                            strLink = parser.getAttributeValue(i);
                                            break;
                                        }
                                    }
                                    if(strLink.equals(""))
                                    {
                                        strLink = parser.nextText();
                                        //strLink = parser.getAttributeValue(null, "href");
                                    }
                                    currentItem.setImageURL(strLink);
                                    break;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tag = parser.getName();
                        if (tag.equals("entry") || tag.equals("item"))
                        {
                            //boolean bSearch = false;
                            //boolean bCategory = false;
//                            if(IsSearchText(currentItem.getCategory().toString(), currentItem.getTitle().toString()) ){
//                                bSearch = true;
//                            }
//                            if(IsCategory(currentItem.getCategory().toString()))
//                            {
//                                bCategory = true;
//                            }
                            // 条件一致していれば追加
                            //if(bSearch || bCategory)
                            {
//                                RssItem sFavItem = mActivity.m_dbData.GetFavoriteData(currentItem.getLink().toString());
//                                if(null!=sFavItem) currentItem.setFavoriteNo(sFavItem.getFavoriteNo());
                                currentItem.setSiteName(strName);
                                mList.add(currentItem);
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return mList;
    }

//    private boolean IsSearchText(String strTitle, String strCategory)
//    {
//        boolean bRet = false;
//        if(!mSearchText.equals("") && strTitle.contains(mSearchText)) bRet = true;
//        if(!mSearchText.equals("") && strCategory.contains(mSearchText)) bRet = true;
//        return bRet;
//    }
//
//    private boolean IsCategory(String strCategory)
//    {
//        boolean bRet = false;
//        if(m_bCategory)
//        {
//            if(mCategory.equals("")) bRet = true;
//            if(!mCategory.equals("") && mCategory.equals(strCategory)) bRet = true;
//        }
//        return bRet;
//    }

    //////////////////////////////////////////////////////
    // 読み込み中ダイアログ開始
    //////////////////////////////////////////////////////
//    public void startProgresDlg(int nNum)
//    {
//        if(null!=mProgressDialog) mProgressDialog.dismiss();
//        mProgressDialog = new ProgressDialog(mActivity);
//        mProgressDialog.setTitle("Now Loading...");
//        mProgressDialog.setMax(nNum);
//        mProgressDialog.setMessage("データ取得中です\nしばらくお待ちください");
//        //mProgressDialog.setIndeterminate(false);
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        //mProgressDialog.setCancelable(false);
//        mProgressDialog.show();
//    }
//    public void setProgresDlgMax(int nNum)
//    {
//        mProgressDialog.setMax(nNum);
//    }

    //////////////////////////////////////////////////////
    // 読み込み中ダイアログ削除
    //////////////////////////////////////////////////////
//    protected void dismissProgresDlg()
//    {
//        if(mProgressDialog != null && mProgressDialog.isShowing()){
//            mProgressDialog.dismiss();
//            mProgressDialog = null;
//        }
//    }

    //////////////////////////////////////////////////////
    // プログレスバーの更新
    //////////////////////////////////////////////////////
//    private void setProgressValue(Integer progress)
//    {
//        if(null!=mProgressDialog){
//            mProgressDialog.setProgress(progress);
//        }
//    }
}
