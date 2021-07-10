package com.takaharabooks.app.vcnews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.takaharabooks.app.vcnews.pref.DB_Data;
import com.takaharabooks.app.vcnews.ui.bookmark.BookmarkViewModel;
import com.takaharabooks.app.vcnews.ui.history.HistoryViewModel;
import com.takaharabooks.app.vcnews.ui.home.HomeViewModel;
import com.takaharabooks.app.vcnews.ui.item.RssItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity
{
    static public final int INTENT_SETTING = 1001;
    private AppBarConfiguration mAppBarConfiguration;
    FloatingActionButton mFloatBtn;

    private HomeViewModel mHomeViewModel;
    private BookmarkViewModel mBookmarkViewModel;
    private HistoryViewModel mHistoryViewModel;
    private SharedPreferences mPrefs;
    private DB_Data m_dbData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // テーマ適用はonCreateより前に行う
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        changeDarkLightMode();

        // onCreate
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DB初期化
        m_dbData = new DB_Data(this);
        m_dbData.InitDB();

        // ツールバー
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // フローティングボタン
        mFloatBtn = findViewById(R.id.fab);
        mFloatBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                NavController navController = navHostFragment.getNavController();
                int nID = navController.getCurrentDestination().getId();
                switch(nID)
                {
                    case R.id.nav_home:
                        mHomeViewModel.ReloadItems().observe(MainActivity.this, item ->
                        {
                            // Update the UI.
                        });
                        break;
                    case R.id.nav_bookmark:
                        mBookmarkViewModel.ReloadItems(m_dbData).observe(MainActivity.this, item ->
                        {
                            // Update the UI.
                        });
                        break;
                    default:
                        break;
                }
//                Snackbar.make(view, "リスト更新予定！！", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                // 各フラグメント更新
            }
        });

        // ナビゲーション
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
//        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
//        {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item)
//            {
//                switch(item.getItemId())
//                {
//                    case R.id.nav_email:
//                        // メーラー起動
//                        Intent intent = new Intent(Intent.ACTION_SENDTO);
//                        intent.setData(Uri.parse("mailto:"));
//                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@yamatocreation.info"});
//                        startActivity(intent);
//                        return true;
//                }
//                return false;
//            }
//        });
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_bookmark, R.id.nav_history, R.id.nav_setting)
                .setDrawerLayout(drawer)
                .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener()
        {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments)
            {
                int nID = destination.getId();
                switch(nID)
                {
                    case R.id.nav_home:
                    case R.id.nav_bookmark:
                        //navController.navigate(R.id.nav_setting);
                        mFloatBtn.setVisibility(View.VISIBLE);
                        break;
                    default:
                        mFloatBtn.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // データ
        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mBookmarkViewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);
        mHistoryViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
    }

//    @Override
//    public void onReloadList()
//    {
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // メニューの動作
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        switch (item.getItemId())
        {
            case R.id.action_settings:// 設定
                mFloatBtn.setVisibility(View.INVISIBLE);
                navController.navigate(R.id.nav_setting);
                //changeDarkLightMode();
                return true;
            case R.id.action_about_page:
                mFloatBtn.setVisibility(View.INVISIBLE);
                navController.navigate(R.id.nav_about_page);
                return true;
            case R.id.action_email:
                // メーラー起動
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@yamatocreation.info"});
                startActivity(intent);
                return true;
        }

        return false;
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void changeDarkLightMode()
    {
//        Preferences_Data prefData = new Preferences_Data(this);
//        int nMode = prefData.GetModeNightLight();
        String strThema = mPrefs.getString(
                getResources().getString(R.string.settings_thema_key),
                getResources().getString(R.string.settings_thema_default));
        switch (strThema)
        {
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                //prefData.PutModeNightLight(1);
                break;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                //prefData.PutModeNightLight(0);
                break;
        }
    }

    /*******************************************
     * ２ｃｈ（ＷＥＢ）を起動
     *******************************************/
    public void Intent2chViewer(RssItem sItem)
    {
        String strLink = sItem.getLink().toString();

        Intent intent = new Intent();
        intent.setClassName(
                getPackageName(),
                getPackageName() + ".ui.web.WebViewer");
        intent.putExtra("FAVRITE_NO", sItem.getFavoriteNo());
        intent.putExtra("SITE_NAME", sItem.getSiteName().toString());
        intent.putExtra("TITLE", sItem.getTitle().toString());
        intent.putExtra("DATE", sItem.getDate().toString());
        intent.putExtra("LINK", strLink);
        intent.putExtra("CATEGORY", sItem.getCategory().toString());
        if(null != strLink){
            intent.putExtra("URL", strLink);
        }
        startActivityForResult(intent, INTENT_SETTING);
    }
}