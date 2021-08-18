package com.takaharabooks.app.vcnews.ui.item;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.takaharabooks.app.vcnews.R;
import com.takaharabooks.app.vcnews.pref.DB_Data;
import com.takaharabooks.app.vcnews.ui.common.ImageProcessor;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;

public class RssItemListAdapter extends ArrayAdapter<RssItem>
{
    //private MainActivity mActivity;
    protected List<RssItem> mItems;
    private final DB_Data m_dbData;

    /*******************************************
     * バイトサイズをＭＢサイズに変換
     *
     * @param	lSize	バイトサイズ
     * @return	MBサイズ
     *******************************************/
//    static public double ConvertMBSize(long lSize){
//        return (double)lSize/1024/1024;
//    }

    /*******************************************
     * MBサイズをBサイズに変換
     *
     * @param	lSize	MBサイズ
     * @return	Bサイズ
     *******************************************/
    static public long ConvertMBtoBSize(long lSize){
        return lSize*1024*1024;
    }

    protected LruCache<String, Bitmap> mMemoryCache;
    protected ImageProcessor mProcessor;
    public void InitCache()
    {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        //final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        //final int cacheSize = maxMemory / 8;

        final int cacheSize = 1024 * 1024 * 10; // 5MB～100MB
        if(mMemoryCache == null)
        {
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(@NonNull String key, @NonNull Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
            if(null == mProcessor){
                //long lCacheCapacity = ConvertMBtoBSize(100);
                mProcessor = new ImageProcessor(getContext(), mMemoryCache);
            }
        }
    }
//    public void addBitmapToMemoryCache(String key, Bitmap bitmap)
//    {
//        // 例外処理
//        if(key == null) return;
//        if(bitmap == null) return;
//        if (getBitmapFromMemCache(key) == null) {
//            mMemoryCache.put(key, bitmap);
//        }
//    }
//    public Bitmap getBitmapFromMemCache(String key)
//    {
//        if(key == null) return null;
//        return mMemoryCache.get(key);
//    }

    // コンストラクタ
    public RssItemListAdapter(Context context, Activity activity, List<RssItem> objects, DB_Data dbData) {
        super(context, 0, objects);
        //mActivity = (MainActivity) activity;
        mItems = objects;
        m_dbData = dbData;
        //mContext.setTheme(R.style.Theme_VCNews);
        //mTextColor = mContext.getColor(R.color.text_color);
        InitCache();
    }

    /***********************************************************************************************
     * アダプタにRSS情報アイテムを追加<br>
     *
     * @param	item		RSS情報のアイテム
     * @return	none
     ***********************************************************************************************/
    public void addItem(RssItem item)
    {
        mItems.add(item);
    }

    @Override
    public int getCount()
    {
        return mItems.size();
    }

    // 1行ごとのビューを生成する
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //LayoutInflater Inflater = mActivity.getLayoutInflater();
        LayoutInflater Inflater = LayoutInflater.from(getContext());
        View view = convertView;

        RssItem item = this.getItem(position);
        String strTag = item.getSiteName().toString() + ":" + item.getLink().toString();

        String imageURL = "";
        if (null == view || (null != view && view.getTag() != strTag))
        {

            imageURL = item.getImageURL().toString();
            imageURL.trim();
            if(imageURL.isEmpty())
            {
                view = Inflater.inflate(R.layout.item_rss_row, parent, false);
            }
            else
            {
                //imageURL.length();
                Log.e("### RSS Image URL", imageURL);
                view = Inflater.inflate(R.layout.item_rss_row2, parent, false);
            }
        }

        // 現在参照しているリストの位置からItemを取得する
        if (item != null)
        {
            // Itemから必要なデータを取り出し、それぞれTextViewにセットする
            String link = item.getLink().toString();
            String title = item.getTitle().toString();
            String strSiteName = item.getSiteName().toString();
            String date = item.getDate().toString();
            //CharSequence html = Html.fromHtml(String.format("%s", link, title));

            view.setTag(strTag);

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // 画像セット
            if(!imageURL.isEmpty())
            {
                ImageView mImage = (ImageView) view.findViewById(R.id.item_sumb_img);
                //ListViewFunc.loadDLImage(mActivity, link, mImage, "item_sumb_img");
                Glide.with(getContext()).load(imageURL).into(mImage);
            }

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // タイトルセット（記事概要）
            // この時点ではテーマ無
//            int nColor = getContext().getColor(R.color.text_color);
//            int nColor2 = mContext.getColor(R.color.text_color);
//            int nColor3 = mActivity.getColor(R.color.text_color);
            TextView mTitle = (TextView) view.findViewById(R.id.item_title);
            //mTitle.setTextColor(mTextColor);
            mTitle.setText(title);

            // お気に入り、日付、サイト名の列を追加
            //boolean bFavorite = m_dbData.IsFavoriteData(item.getLink().toString());
            //String strStar = "";
            //if(bFavorite) strStar = "<font color=#cccc00>★</font>";
            ImageView imageFav = (ImageView)view.findViewById(R.id.item_fav_img);
            if(m_dbData.IsFavoriteData(link))
            {
                imageFav.setImageResource(R.drawable.ic_icon_favorite_on);
            }
            else
            {
                imageFav.setImageResource(R.drawable.ic_icon_favorite_off);
            }
            imageFav.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ImageView img = (ImageView)v;
                    if(item.getFavoriteNo()==0) {
                        img.setImageResource(R.drawable.ic_icon_favorite_on);
                        m_dbData.InsertFavoriteDataInfo(item);
                        item.setFavoriteNo(1);
                    }else{
                        img.setImageResource(R.drawable.ic_icon_favorite_off);
                        m_dbData.DeleteFavoriteData(link);
                        item.setFavoriteNo(0);
                    }
                }
            });

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // 日付セット
            TextView mDate = (TextView) view.findViewById(R.id.item_date);
//            CharSequence htmlDate = Html.fromHtml(String.format("%s年%s月%s日 %s",
//                    date.substring(0, 4), date.substring(5, 7), date.substring(8, 10), date.substring(11, 19))
//            );
            mDate.setText(date);

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // サイト名セット
            TextView mSite = (TextView) view.findViewById(R.id.item_site);
            CharSequence htmlSite = Html.fromHtml(String.format("<font color=gray>%s</font>", strSiteName));
            mSite.setText(htmlSite);
        }
        return view;
    }

}
