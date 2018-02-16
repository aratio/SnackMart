package com.androidapps.arati.snackmart;

/**
 * Created by aogale on 2/15/18.
 */

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.util.Log;
import java.util.ArrayList;



/**
 * This class is an adapter for list view in main activity
 * It calls SnackList class to get filtered list for selected type
 */

public class SnackListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<SnackList.SnackItem> mItemList;
    private LayoutInflater mInflater;


    public SnackListAdapter(Context context, ArrayList<SnackList.SnackItem> snackItems) {
        mContext = context;
        mItemList = snackItems; //save filtered list
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return mItemList.size();

    }

    @Override
    public SnackList.SnackItem getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listRowView = mInflater.inflate(R.layout.snack_list_row_layout, parent, false);

        final SnackList.SnackItem currentSnackItem = mItemList.get(position);

        CheckBox snackSelectedCheckBox =
                (CheckBox) listRowView.findViewById(R.id.snack_list_item_checkbox);

        final int veggieColor = ContextCompat.getColor(mContext, R.color.veggieColor);
        final int nonVeggieColor = ContextCompat.getColor(mContext, R.color.nonVeggieColor);
        int viewColor;

        switch (currentSnackItem.type) {
            case VEGGIE:
                viewColor = veggieColor;
                break;

            case NONVEGGIE:
            default:
                viewColor = nonVeggieColor;

        }

        snackSelectedCheckBox.setTextColor(viewColor);
        CompoundButtonCompat.setButtonTintList(snackSelectedCheckBox, ColorStateList.valueOf(viewColor));

        snackSelectedCheckBox.setText(currentSnackItem.name);
        snackSelectedCheckBox.setChecked(currentSnackItem.selected);

        snackSelectedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentSnackItem.selected = buttonView.isChecked();
            }
        });


        Log.d("SnackListAdapter", "name = " + currentSnackItem.name + "type=" + currentSnackItem.type);

        return listRowView;

    }

    String getSelectedItemsSummary() {

        StringBuilder selectedListStringBuilder = new StringBuilder();

        for (SnackList.SnackItem item : mItemList) {
            if (item.selected) {
                selectedListStringBuilder.append("\u25cf\t\t").append(item.name).append("\n");
            }

        }
        return selectedListStringBuilder.toString();

    }

    /* for testing */
    void selectAll() {
        for (SnackList.SnackItem item : mItemList) {
            item.selected = true;
        }
    }
    /* for testing */
}
