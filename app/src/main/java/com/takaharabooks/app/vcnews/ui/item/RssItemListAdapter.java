package com.takaharabooks.app.vcnews.ui.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.takaharabooks.app.vcnews.R;
import com.takaharabooks.app.vcnews.pref.DB_Data;
import com.takaharabooks.app.vcnews.pref.Preferences_Common;
import com.takaharabooks.app.vcnews.ui.common.ImageProcessor;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;

public class RssItemListAdapter extends ArrayAdapter<RssItem>
{
    Preferences_Common mPrefCom;
    protected List<RssItem> mItems;
    private final DB_Data m_dbData;

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

    // コンストラクタ
    public RssItemListAdapter(Context context, List<RssItem> objects, DB_Data dbData) {
        super(context, 0, objects);
        mPrefCom = new Preferences_Common(context);
        mItems = objects;
        m_dbData = dbData;
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

        RssItem item = null;

        String imageURL = "";
        if (null == view || (null != view && view.getTag() != this.getItem(position).getTag()))
        {
            item = this.getItem(position);
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

            view.setTag(item.getTag());

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // 画像セット
            if(!imageURL.isEmpty())
            {
                ImageView mImage = view.findViewById(R.id.item_sumb_img);
                //ListViewFunc.loadDLImage(mActivity, link, mImage, "item_sumb_img");
                Glide.with(getContext()).load(imageURL).into(mImage);
            }

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // タイトルセット（記事概要）
            TextView mTitle = view.findViewById(R.id.item_title);
            //mTitle.setTextColor(mTextColor);
            mTitle.setText(title);

            // お気に入り、日付、サイト名の列を追加
            //boolean bFavorite = m_dbData.IsFavoriteData(item.getLink().toString());
            //String strStar = "";
            //if(bFavorite) strStar = "<font color=#cccc00>★</font>";
            ImageView imageFav = view.findViewById(R.id.item_fav_img);
            if(m_dbData.IsFavoriteData(link))
            {
                imageFav.setImageResource(R.drawable.ic_icon_favorite_on);
            }
            else
            {
                imageFav.setImageResource(R.drawable.ic_icon_favorite_off);
            }
            RssItem finalItem = item;
            imageFav.setOnClickListener(v -> {
                ImageView img = (ImageView)v;
                if(finalItem.getFavoriteNo()==0) {
                    img.setImageResource(R.drawable.ic_icon_favorite_on);
                    m_dbData.InsertFavoriteDataInfo(finalItem);
                    finalItem.setFavoriteNo(1);
                }else{
                    img.setImageResource(R.drawable.ic_icon_favorite_off);
                    m_dbData.DeleteFavoriteData(link);
                    finalItem.setFavoriteNo(0);
                }
            });

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // 日付セット
            TextView mDate = view.findViewById(R.id.item_date);
//            CharSequence htmlDate = Html.fromHtml(String.format("%s年%s月%s日 %s",
//                    date.substring(0, 4), date.substring(5, 7), date.substring(8, 10), date.substring(11, 19))
//            );
            mDate.setText(date);

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // サイト名セット
            TextView mSite = view.findViewById(R.id.item_site);
            CharSequence htmlSite = Html.fromHtml(String.format("<font color=gray>%s</font>", strSiteName));
            mSite.setText(htmlSite);

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // 通貨イメージセット
            RelativeLayout layout = view.findViewById(R.id.item_container);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(32,32);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
            String[] strCurrencys = mPrefCom.getArraysString(R.array.setting_currency);
            //int[] nCurResID = mPrefCom.getArraysInt(R.array.setting_currency_resid);
            for(int nIndex=0; nIndex<strCurrencys.length; nIndex++)
            {
                String strCurrency = strCurrencys[nIndex];
                if (item.getCategory().toString().contains(strCurrency)) {
                    ImageView mCurImage = new ImageView(getContext());
                    switch(strCurrency)
                    {
                        case "BTC":
                            mCurImage.setImageResource(R.drawable.ic_icon_bitcoin);
                            break;
                        case "ETH":
                            mCurImage.setImageResource(R.drawable.ic_icon_ethereum);
                            break;
                    }
                    int nID = nIndex+1;
                    mCurImage.setId(nID);
                    layout.addView(mCurImage, params);
                    params = new RelativeLayout.LayoutParams(32,32);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.RIGHT_OF, nID);
                }
            }
        }
        return view;
    }

}
