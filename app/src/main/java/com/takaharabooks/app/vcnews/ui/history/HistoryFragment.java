package com.takaharabooks.app.vcnews.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.takaharabooks.app.vcnews.MainActivity;
import com.takaharabooks.app.vcnews.R;
import com.takaharabooks.app.vcnews.pref.DB_Data;
import com.takaharabooks.app.vcnews.ui.common.ListViewFunc;
import com.takaharabooks.app.vcnews.ui.item.RssItem;
import com.takaharabooks.app.vcnews.ui.item.RssItemListAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    private View mRoot;
    private AdView mAdView;

    protected List<RssItem> mItems = null;
    protected RssItemListAdapter mAdapter;
    private DB_Data m_dbData;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_history, menu);
    }

    // メニューの動作
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        switch (item.getItemId())
        {
            case R.id.btn_delete_history:// 設定
                new SweetAlertDialog(HistoryFragment.this.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("確認")
                        .setContentText("すべての履歴を削除しますか？")
                        .setConfirmText("はい")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                        {
                            @Override
                            public void onClick(SweetAlertDialog sDialog)
                            {
                                m_dbData.DeleteAllHistoryData();
                                InitRssData();
                                InitListView(mItems);
                                // メッセージ表示
                                sDialog
                                        .setTitleText("削除完了")
                                        .setContentText("履歴からすべて削除しました")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(null)
                                        .showCancelButton(false)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .setCancelButton("いいえ", new SweetAlertDialog.OnSweetClickListener()
                        {
                            @Override
                            public void onClick(SweetAlertDialog sDialog)
                            {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
                return true;
        }

        return false;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        historyViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);
        mRoot = inflater.inflate(R.layout.fragment_history, container, false);

        m_dbData = new DB_Data(this.getContext());
        m_dbData.InitDB();

        // 初回RSSデータ読み込み
        InitRssData();
        InitListView(mItems);
        InitAd();

        return mRoot;
    }

    /***********************************************************************************************
     * リストの初期設定<br>
     * 2chから取得したデータを表示
     *
     * @param	items
     * @return	none
     ***********************************************************************************************/
    public void InitListView(List<RssItem> items)
    {
        ListView RssList = (ListView)mRoot.findViewById(R.id.layout_rss_listview);
        mAdapter = new RssItemListAdapter(this.getContext(), getActivity(), items, m_dbData);
        // アダプタをリストビューにセットする
        ListViewFunc.InitListView((MainActivity) getActivity(), m_dbData, items, RssList, mAdapter);

        RssList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                new SweetAlertDialog(HistoryFragment.this.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("確認")
                        .setContentText("この記事を履歴から削除しますか？")
                        .setConfirmText("はい")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                        {
                            @Override
                            public void onClick(SweetAlertDialog sDialog)
                            {
                                m_dbData.DeleteHistoryData(mItems.get(position).getLink().toString());
                                InitRssData();
                                InitListView(mItems);
                                // メッセージ表示
                                sDialog
                                        .setTitleText("削除完了")
                                        .setContentText("履歴から削除しました")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(null)
                                        .showCancelButton(false)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .setCancelButton("いいえ", new SweetAlertDialog.OnSweetClickListener()
                        {
                            @Override
                            public void onClick(SweetAlertDialog sDialog)
                            {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    public void InitRssData()
    {
        mItems = new ArrayList<RssItem>();
        AddRssItem(m_dbData.GetHistoryAllData());
    }

    public void AddRssItem(List<RssItem> items)
    {
        if(null!=mItems)
        {
            mItems.addAll(items);
        }
    }

    /*************************************
     * 広告の初期設定
     **************************************/
    public void InitAd()
    {
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener()
        {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus)
            {

            }
        });
        mAdView = mRoot.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}