package com.androidapps.arati.snackmart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import android.content.DialogInterface;
import android.app.AlertDialog;

import java.util.ArrayList;
/*
 * Main activity
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();

        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(this);

        CheckBox veggieCheckBox = (CheckBox) findViewById(R.id.veggie_checkbox);
        CheckBox nonveggieCheckBox = (CheckBox) findViewById(R.id.nonveggie_checkbox);
        veggieCheckBox.setChecked(true);
        nonveggieCheckBox.setChecked(true);

        veggieCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //if user unchecked snack type, unselect all items of that type
                if(!isChecked)
                {
                    SnackMartApplication.theApp.snackList.unselectVeggie();
                }
                populateListView();
            }
        });

        nonveggieCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //if user unchecked snack type, unselect all items of that type
                if(!isChecked)
                {
                    SnackMartApplication.theApp.snackList.unselectNonveggie();
                }
                populateListView();
            }
        });

        populateListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.add_action:
                Log.d(TAG, "User wants to add snack item");
                showNewItemDialog();

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.submit_button:
                Log.d(TAG, "User clicked Submit");

                ListView listView = (ListView) findViewById(R.id.snack_list);
                final SnackListAdapter listViewAdapter = (SnackListAdapter)listView.getAdapter();

                String orderSummary = listViewAdapter.getSelectedItemsSummary();
                if(orderSummary.isEmpty())
                {
                    showErrorDialog(getResources().getString(R.string.submit_order_error));
                    break;
                }

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this );
                //order_review_summary
                View alertDialogView = getLayoutInflater().inflate(R.layout.order_review_layout, null);

                final TextView summaryView = (TextView) alertDialogView.findViewById(R.id.order_review_summary);
                summaryView.setText(orderSummary);

                alertBuilder.setView(alertDialogView);
                alertBuilder.setPositiveButton(R.string.confirm_dialog_button, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                SnackMartApplication.theApp.snackList.resetSelection();
                                populateListView();
                            }
                        }
                );

                AlertDialog alert = alertBuilder.create();
                alert.show();

                break;
        }
    }


    private void populateListView()
    {
        Log.d(TAG, "Populating list view again");
        CheckBox veggieCheckBox = (CheckBox) findViewById(R.id.veggie_checkbox);
        CheckBox nonveggieCheckBox = (CheckBox) findViewById(R.id.nonveggie_checkbox);

        ListView listView = (ListView) findViewById(R.id.snack_list);

        ArrayList<SnackList.SnackItem> filteredSnackList =
                SnackMartApplication.theApp.snackList.getFilteredSnackList(veggieCheckBox.isChecked(), nonveggieCheckBox.isChecked());

        SnackListAdapter adapter = new SnackListAdapter(this, filteredSnackList);

        /*for testing */
        //adapter.selectAll();
        /*for testing */

        listView.setAdapter(adapter);

    }

    //show dialog for adding a new snack
    private void showNewItemDialog()
    {
        AlertDialog.Builder addAlertBuilder = new AlertDialog.Builder(MainActivity.this);
        View alertDialogView = getLayoutInflater().inflate(R.layout.add_snack_dialog_layout, null);

        final EditText newSnackName = (EditText) alertDialogView.findViewById(R.id.new_snack_edit_text);
        final Switch typeSwitch = (Switch) alertDialogView.findViewById(R.id.new_snack_type_switch);

        addAlertBuilder.setView(alertDialogView);
        addAlertBuilder.setPositiveButton(R.string.new_snack_dialog_save_button,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface addDialog, int which)
                    {
                        String type = typeSwitch.isChecked()? typeSwitch.getTextOn().toString() : typeSwitch.getTextOff().toString();
                        if(!SnackMartApplication.theApp.snackList.addSnackItem(newSnackName.getText().toString(), type))
                        {
                            showErrorDialog(getResources().getString(R.string.new_snack_error));
                        }
                        else
                        {
                            addDialog.dismiss();
                            populateListView();
                        }

                    }
                });

        addAlertBuilder.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog addSnackAlert = addAlertBuilder.create();
        addSnackAlert.show();
    }

    private void showErrorDialog(String message) {
        //error adding snack. Show error dialog
        AlertDialog.Builder errorAlertBuilder = new AlertDialog.Builder(MainActivity.this);
        View alertDialogView = getLayoutInflater().inflate(R.layout.error_dialog_layout, null);
        TextView errorTextView = (TextView) alertDialogView.findViewById(R.id.error_text);
        errorTextView.setText(message);
        errorAlertBuilder.setView(alertDialogView);

        errorAlertBuilder.setPositiveButton(R.string.confirm_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
        errorAlertBuilder.create().show();
    }
}
