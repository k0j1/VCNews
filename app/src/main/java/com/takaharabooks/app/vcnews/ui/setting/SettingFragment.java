package com.takaharabooks.app.vcnews.ui.setting;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.takaharabooks.app.vcnews.MainActivity;
import com.takaharabooks.app.vcnews.R;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingFragment extends PreferenceFragmentCompat
{
    private String mThemaStr = "";
    private SettingViewModel settingViewModel;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
//        settingViewModel =
//                new ViewModelProvider(this).get(SettingViewModel.class);
        setHasOptionsMenu(true);
        setPreferencesFromResource(R.xml.preferences, rootKey);
        ListPreference listPref = (ListPreference) findPreference(getResources().getString(R.string.settings_thema_key));
        listPref.setSummaryProvider(new Preference.SummaryProvider<ListPreference>()
        {
            @Override
            public CharSequence provideSummary(ListPreference preference)
            {
                String strThema = preference.getValue();
                if(mThemaStr.isEmpty())
                {
                    mThemaStr = strThema;
                }
                else if(mThemaStr != strThema)
                {
                    mThemaStr = strThema;
                    MainActivity ac = (MainActivity) getActivity();
                    ac.changeDarkLightMode();
                }
                return mThemaStr;
            }
        });
    }

//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        settingViewModel =
//                new ViewModelProvider(this).get(SettingViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_setting, container, false);
////        final TextView textView = root.findViewById(R.id.text_setting);
//        settingViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
//        return root;
//    }
}