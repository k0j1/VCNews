package com.takaharabooks.app.vcnews.ui.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.takaharabooks.app.vcnews.MainActivity;
import com.takaharabooks.app.vcnews.R;
import com.takaharabooks.app.vcnews.pref.DB_Data;
import com.takaharabooks.app.vcnews.ui.item.RssItem;
import com.takaharabooks.app.vcnews.ui.item.RssItemListAdapter;

import java.util.Comparator;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

public class ListViewFunc
{
    /*******************************************
     * 並び替え処理
     *******************************************/
    static public class DateComp implements Comparator<RssItem>
    {
        static final int DESC = -1;
        @Override
        public int compare(RssItem RItem1, RssItem RItem2)
        {
            String s1 = RItem1.getDate().toString();
            String s2 = RItem2.getDate().toString();

            if(s1.length()==0) return 1;
            if(s2.length()==0) return -1;

            return (s1.compareTo(s2) * DESC);
        }
    }

    /***********************************************************************************************
     * リストの初期設定<br>
     * 2chから取得したデータを表示
     *
     * @param	mActivity
     * @param	m_dbData
     * @param	items
     * @param	RssList
     * @param	Adapter
     * @return	none
     ***********************************************************************************************/
    static public void InitListView(final MainActivity mActivity, final DB_Data m_dbData, final List<RssItem> items, ListView RssList, RssItemListAdapter Adapter)
    {
        // 例外処理
        if(RssList == null) return;
        if(Adapter == null) return;

        final SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);

        RssList.removeAllViewsInLayout();
        RssList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3)
            {
                // 設定取得（起動ブラウザ）
                boolean bStandardBrowser = mPrefs.getBoolean(
                        mActivity.getApplicationContext().getResources().getString(R.string.settings_browser_key),
                        mActivity.getApplicationContext().getResources().getBoolean(R.bool.settings_browser_default));

                // クリックしたアイテム取得
                RssItem item = items.get(position);

                // 履歴に追加
                m_dbData.InsertHistoryDataInfo(item);

                // 設定に応じて標準ブラウザで表示するか独自ブラウザで表示するかを切り分け
                if(bStandardBrowser)
                {
                    // ブラウザ起動
                    Uri uri = Uri.parse(item.getLink().toString());
                    Intent i = new Intent(Intent.ACTION_VIEW,uri);
                    mActivity.startActivity(i);
                }
                else
                {
                    // WebViewer起動
                    mActivity.Intent2chViewer(item);
                }
            }
        });

        // アダプタをリストビューにセットする
        //mAdapter = new RssItemListAdapter(this.getContext(), getActivity(), items, m_dbData);
        RssList.setAdapter(Adapter);
    }

    // ダウンロードイメージのサイズ設定
    public static void loadDLImage(Context context, String strUrl, ImageView image, String strImageIDName)
    {
        RequestManager rm = Glide.with(context);
        rm.load(strUrl).listener(createLoggerListener(strImageIDName + image.getId()))
        //.override() サイズ指定も可能
        .into(image);
    }

    static private RequestListener<Drawable> createLoggerListener(final String name)
    {
        return new RequestListener<Drawable>()
        {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource)
            {
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target target, DataSource dataSource, boolean isFirstResource)
            {
                if (resource instanceof BitmapDrawable)
                {
                    Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                    Log.d("GlideApp",
                            String.format("Ready %s bitmap %,d bytes, size: %d x %d",
                                    name,
                                    bitmap.getByteCount(),
                                    bitmap.getWidth(),
                                    bitmap.getHeight()));
                }
                return false;
            }
        };
    }
}
