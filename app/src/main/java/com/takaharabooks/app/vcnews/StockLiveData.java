package com.takaharabooks.app.vcnews;

import com.takaharabooks.app.vcnews.ui.item.RssItem;

import androidx.lifecycle.LiveData;

public class StockLiveData extends LiveData<RssItem>
{
//    private static StockLiveData sInstance;
//    private StockManager stockManager;
//
//    private SimplePriceListener listener = new SimplePriceListener() {
//        @Override
//        public void onLoadItem(RssItem price) {
//            setValue(price);
//        }
//    };
//
//    @MainThread
//    public static StockLiveData get(String symbol) {
//        if (sInstance == null) {
//            sInstance = new StockLiveData(symbol);
//        }
//        return sInstance;
//    }
//
//    private StockLiveData(String symbol)
//    {
//        // ここで実際の読み込み処理を行う
//        stockManager = new StockManager(symbol);
//    }
//
//    @Override
//    protected void onActive() {
//        stockManager.requestPriceUpdates(listener);
//    }
//
//    @Override
//    protected void onInactive() {
//        stockManager.removeUpdates(listener);
//    }
}