package com.persol.ispss;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class AccountFragment extends Fragment {

    private ISPSSManager ispssManager;


    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        ispssManager = new ISPSSManager(getActivity());

        SettingItem[] settingItems = new SettingItem[8];
        SettingItem userId = new SettingItem(1,"User ID",ispssManager.getContributorID(),R.drawable.id);
        SettingItem phone = new SettingItem(2,"Phone",ispssManager.getUserPhone(),R.drawable.phone_green);
        SettingItem email = new SettingItem(1,"Email",ispssManager.getUserEmail(),R.drawable.email_green);
        SettingItem residence = new SettingItem(1,"Residence",ispssManager.getUserResidence(),R.drawable.home_green);
        SettingItem history = new SettingItem(1,"History","History of your transactions",R.drawable.history_green);
        SettingItem favourites = new SettingItem(1,"Favourites","People you do transactions with regularly",R.drawable.favourite);
        SettingItem batchPayments = new SettingItem(6,"Batch Payments","Pay many people at go",R.drawable.payments_green);
        SettingItem logOut = new SettingItem(1,"Log out","Log out. You will have to sign in next time",R.drawable.exit);
        settingItems[0] = userId;
        settingItems[1] = phone;
        settingItems[2] = email;
        settingItems[3] = residence;
        settingItems[4] = history;
        settingItems[5] = favourites;
        settingItems[6] = batchPayments;
        settingItems[7] = logOut;

        SettingsArrayAdapter settingsArrayAdapter =  new SettingsArrayAdapter(getActivity(),R.layout.settings_item_layout,settingItems);
        ListView settingsListView = view.findViewById(R.id.settingsListView);
        settingsListView.setAdapter(settingsArrayAdapter);
        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent;
                switch (i){
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        break;
                    case 4:
                        intent = new Intent(getActivity(),HistoryActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        break;
                    case 6:
                        intent = new Intent(getActivity(),BatchPayActivity.class);
                        startActivity(intent);
                        break;
                    case 7:
                        ispssManager.logout();
                        break;
                }
            }
        });

        return view;
    }
}