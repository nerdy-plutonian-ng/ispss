package com.persol.ispss;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.persol.ispss.Constants.Beneficiaries;

public class SettingsArrayAdapter extends ArrayAdapter<SettingItem> {

    private SettingItem[] settingItems;
    private Context context;
    private ISPSSManager ispssManager;

    public SettingsArrayAdapter(@NonNull Context context, int resource, @NonNull SettingItem[] settingItems) {
        super(context, resource, settingItems);
        this.settingItems = settingItems;
        this.context = context;
        this.ispssManager = new ISPSSManager(this.context);
    }

    @Override
    public int getCount() {
        return settingItems.length;
    }

    @Nullable
    @Override
    public SettingItem getItem(int position) {
        return settingItems[position];
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.settings_item_layout, parent, false);
        }
        final View view = convertView;
        TextView titleTV = view.findViewById(R.id.titleTV);
        TextView valueTV  = view.findViewById(R.id.valueTV);
        ImageView iconIV = view.findViewById(R.id.icon);
        iconIV.setImageResource(settingItems[position].getIcon());
        titleTV.setText(settingItems[position].getTitle());
        valueTV.setText(settingItems[position].getValue());
        return view;
    }
}
