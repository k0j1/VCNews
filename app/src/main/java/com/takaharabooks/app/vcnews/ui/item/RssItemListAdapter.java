package com.takaharabooks.app.vcnews.ui.item;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.takaharabooks.app.vcnews.MainActivity;
import com.takaharabooks.app.vcnews.R;
import com.takaharabooks.app.vcnews.pref.DB_Data;
import com.takaharabooks.app.vcnews.ui.common.ImageProcessor;
import com.takaharabooks.app.vcnews.ui.common.ListViewFunc;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import androidx.collection.LruCache;
import androidx.preference.PreferenceManager;

public class RssItemListAdapter extends ArrayAdapter<RssItem>
{
    private Context mContext;
    private MainActivity mActivity;
    protected List<RssItem> mItems;
    private LayoutInflater mInflater;
    private TextView mTitle;
    private TextView mDate;
    private TextView mSite;
    private ImageView mImage;
    private DB_Data m_dbData;
    private SharedPreferences mPrefs;

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
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        //final int cacheSize = maxMemory / 8;

        final int cacheSize = 1024 * 1024 * 10; // 5MB～100MB
        if(mMemoryCache == null)
        {
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
            if(null == mProcessor){
                long lCacheCapacity = ConvertMBtoBSize(100);
                mProcessor = new ImageProcessor(mContext, mMemoryCache);
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
        mContext = context;
        mActivity = (MainActivity) activity;
        mItems = objects;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        m_dbData = dbData;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        View view = convertView;

        RssItem item = this.getItem(position);
        String strTag = item.getSiteName().toString() + ":" + item.getLink().toString();

        String imageURL = "";
        if (null == view || (null != view && view.getTag() != strTag))
        {
            imageURL = item.getImageURL().toString();
            imageURL.trim();
            if(imageURL.isEmpty() || imageURL.length() <= 0)
            {
                view = mInflater.inflate(R.layout.item_rss_row, null);
            }
            else
            {
                Log.e("### RSS Image URL", imageURL);
                view = mInflater.inflate(R.layout.item_rss_row2, null);
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
                mImage = (ImageView) view.findViewById(R.id.item_sumb_img);
                //ListViewFunc.loadDLImage(mActivity, link, mImage, "item_sumb_img");
                Glide.with(mActivity).load(imageURL).into(mImage);
//                final Bitmap bitmap = mProcessor.getBitmapFromMemCache(link);
//                if(null != bitmap)
//                {
//                    mImage.setImageBitmap(bitmap);
//                }
//                else
//                {
//                    mImage.setImageBitmap(null);
//                    mImage.setTag(strTag);
//                    //画像取得スレッド起動
//                    FetchImageTask task = new FetchImageTask(mImage);
//                    task.execute(imageURL);
//                }
            }

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // タイトルセット（記事概要）
            mTitle = (TextView) view.findViewById(R.id.item_title);
            mTitle.setText(title);
//            mTitle.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    boolean bStandardBrowser = mPrefs.getBoolean(
//                            mActivity.getApplicationContext().getResources().getString(R.string.settings_browser_key),
//                            mActivity.getApplicationContext().getResources().getBoolean(R.bool.settings_browser_default));
//
//                    m_dbData.InsertHistoryDataInfo(item);
//
//                    if(bStandardBrowser)
//                    {
//                        // ブラウザ起動
//                        Uri uri = Uri.parse(item.getLink().toString());
//                        Intent i = new Intent(Intent.ACTION_VIEW,uri);
//                        mActivity.startActivity(i);
//                    }
//                    else
//                    {
//                        // WebViewer起動
//                        mActivity.Intent2chViewer(item);
//                    }
//                }
//            });

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
            mDate = (TextView) view.findViewById(R.id.item_date);
//            CharSequence htmlDate = Html.fromHtml(String.format("%s年%s月%s日 %s",
//                    date.substring(0, 4), date.substring(5, 7), date.substring(8, 10), date.substring(11, 19))
//            );
            mDate.setText(date);

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // サイト名セット
            mSite = (TextView)view.findViewById(R.id.item_site);
            CharSequence htmlSite = Html.fromHtml(String.format("<font color=gray>%s</font>", strSiteName));
            mSite.setText(htmlSite);
        }
        return view;
    }

    // Image取得用スレッドクラス
    class ImageGetTask extends AsyncTask<String,Void,Bitmap> {
        private ImageView image;

        public ImageGetTask(ImageView _image) {
            image = _image;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap image;
            try {
                URL imageUrl = new URL(params[0]);
                InputStream imageIs;
                imageIs = imageUrl.openStream();
                image = BitmapFactory.decodeStream(imageIs);
                return image;
            } catch (MalformedURLException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            // 取得した画像をImageViewに設定します。
            image.setImageBitmap(result);
        }
    }

    class FetchImageTask extends AsyncTask<String, Void, Bitmap>
    {
        private ImageView imageView;
        private String tag;

        public FetchImageTask(ImageView imageView) {
            this.imageView = imageView;
            this.tag = imageView.getTag().toString();
        }

        @Override
        protected Bitmap doInBackground(String... params)
        {
            synchronized (mContext)
            {
                try {
                    Bitmap image = mProcessor.getBitmapFromMemCache(params[0]);
                    if (image == null)
                    {
                        //URL imageUrl = new URL(params[0]);
                        //image = Drawable.c((InputStream) imageUrl.getContent(), "");
                        //ImageCache.set(urls[0], image);
//                        InputStream imageIs;
//                        imageIs = imageUrl.openStream();
//                        image = BitmapFactory.decodeStream(imageIs);
//                        image = Bitmap.createScaledBitmap(image,128, 128, true);
                        ListViewFunc.loadDLImage(mActivity, params[0], imageView, tag);
                        //addBitmapToMemoryCache(params[0], image);
                        image = imageView.getDrawingCache();
                        mProcessor.addBitmapToCache(params[0], image);
                    }
                    return image;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (this.tag.equals(this.imageView.getTag())) {
                if (result == null) return;
                imageView.setVisibility(View.VISIBLE);
                this.imageView.setImageBitmap(result);
            }
            else
            {
                imageView.setVisibility(View.GONE);
                imageView.setImageBitmap(null);
            }
        }
    }

}
