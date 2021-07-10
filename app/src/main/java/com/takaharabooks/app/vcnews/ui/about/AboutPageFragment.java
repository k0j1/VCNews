package com.takaharabooks.app.vcnews.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.takaharabooks.app.vcnews.R;
import com.takaharabooks.app.vcnews.ui.bookmark.BookmarkViewModel;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutPageFragment extends Fragment {

    private BookmarkViewModel bookmarkViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        Element elem = new Element();
        elem.setTitle(getString(R.string.about_app_version));
        Element elemYamato = new Element();
        elemYamato.setTitle("- YAMATO CREATION -");
        Element elemK0j1 = new Element();
        elemK0j1.setTitle("- k0j1 -");
        Element elemSeparator = new Element();
        elemSeparator.setTitle("　");
        //elemSeparator.setGravity(Gravity.CENTER);
        return new AboutPage(getContext())
                .isRTL(false)
                .setDescription("　")
                //.enableDarkMode(false)
                //.setCustomFont(String) // or Typeface
                .setImage(R.drawable.ic_about_page_logo)
                .addItem(elem)
                .addItem(getTermsOfUse())
                .addItem(getGooglePlayElement())
                //*** YAMATO CREATION
                //.addItem(elemSeparator)
                .addItem(elemYamato)
                .addEmail("contact@yamatocreation.info", "E-mail")
                .addWebsite("https://www.yamatocreation.info/", "WebSite")
                .addFacebook("yuki.yamato.9085", "Facebook")
                //.addTwitter("yamatocreation", "Twitter")
                //*** k0j1
                //.addItem(elemSeparator)
                .addItem(elemK0j1)
                .addEmail("k0j123t@gmail.com", "Email")
                .addWebsite("https://www.takahara-books.com/Android/", "WebSite")
                .addGitHub("k0j1", "Github")
                .addTwitter("k0j1_t", "Twitter")
                .addItem(getCopyRightsElement())
                .create();
    }

    // プライバシーポリシーおよび利用規約
    Element getTermsOfUse()
    {
        Element elem = new Element();
        final String strApps = getString(R.string.dev_terms_of_use);
        elem.setTitle(strApps);
        elem.setIconDrawable(R.drawable.ic_icon_article);
        elem.setAutoApplyIconTint(true);
        //elem.setIconTint(R.color.about_twitter_color);
        Uri uri = Uri.parse("https://www.yamatocreation.info/coinnews");
        Intent goHP = new Intent(Intent.ACTION_VIEW, uri);
        elem.setIntent(goHP);
        return elem;
    }

    // GooglePlayストア
    Element getGooglePlayElement()
    {
        Element elem = new Element();
        final String strApps = getString(R.string.dev_more_apps);
        elem.setTitle(strApps);
        elem.setIconDrawable(R.drawable.ic_icon_google_play);
        elem.setAutoApplyIconTint(true);
        elem.setIconTint(R.color.about_play_store_color);
        Uri uri = Uri.parse("https://play.google.com/store/apps/dev?id=6971176554699886046");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        elem.setIntent(goToMarket);
        return elem;
    }


    // コピーライト
    Element getCopyRightsElement()
    {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.dev_copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
        copyRightsElement.setAutoApplyIconTint(true);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }
}