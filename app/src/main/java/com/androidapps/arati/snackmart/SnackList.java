package com.androidapps.arati.snackmart;

/**
 * Created by aogale on 2/15/18.
 */

import android.content.res.AssetManager;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * This class holds snack list in an array list
 * It loads the list from assets which has the list in json format
 *  It provides methods to add new snack items, reset selection of
 *  items, get filetered list based on type of snack.
 *  If new items are added, it saves the modified list in internal
 *  storage and loads from it instead of assets.
 */

public class SnackList
{

    private static final String JSON_SNACK_ITEM_ROOT_KEY    = "snacklist";
    private static final String JSON_SNACK_ITEM_NAME_KEY    = "name";
    private static final String JSON_SNACK_ITEM_TYPE_KEY    = "type";
    private static final String SNACK_ITEMS_JSON_ASSET_NAME = "SnackItemsListAsset.json";
    private static final String SNACK_ITEMS_FILE_NAME       = "SnackItemsList.json";

    public static final String TAG = "SnackList";

    private static final String V_STR = "veggie";
    private static final String NV_STR = "non-veggie";

    enum SnackType
    {
        VEGGIE(V_STR), NONVEGGIE(NV_STR);

        String typeValue;

        SnackType(String value)
        {
            typeValue = value;
        }
        String getTypeValue()
        {
            return typeValue;
        }

        static SnackType getSnackTypeFromString(String type)
        {
            switch(type.toLowerCase()) {
                case V_STR:
                    return SnackType.VEGGIE;
                case NV_STR:
                default:
                    return SnackType.NONVEGGIE;
            }
        }
    }

    class SnackItem
    {
        String  name;
        SnackType type;
        boolean  selected;

        SnackItem(String name, String type)
        {
            this.name = name;
            this.type = SnackType.getSnackTypeFromString(type);
            selected = false;
        }
    }

    ArrayList<SnackItem> snackItems;

    SnackList()
    {
        snackItems = new ArrayList<SnackItem>();
    }


    /*
     * Load snack items from json from internal storage or assets
     */
    boolean loadSnackList()
    {
        Log.d(TAG, "Loading snack list");
        String jsonString = null;


        //first try loading file from internal storage
        File file = new File(SnackMartApplication.theApp.getFilesDir(), SNACK_ITEMS_FILE_NAME);

        jsonString = FileUtils.readTextFile(file);

        if(jsonString == null)    //now try from assets
        {
            try
            {
                AssetManager assetManager = SnackMartApplication.theApp.getAssets();
                jsonString = FileUtils.readTextFile(assetManager.open(SNACK_ITEMS_JSON_ASSET_NAME));
                Log.i(TAG, "Loading snack list from assets: " + jsonString);
            }
            catch (java.io.IOException e)
            {
                Log.e(TAG, "Error when reading data from assets" + e.getLocalizedMessage());
            }
        }
        else {
            Log.i(TAG, "Loading snack list from internal storage: " + jsonString);
        }

        if(jsonString == null)
        {
            Log.e(TAG, "Could not load snack items list");
            return false;
        }


        try //fill array list by parsing json
        {
            JSONObject snackListJsonObj = new JSONObject(jsonString);
            JSONArray snackItemsJsonArray = snackListJsonObj.getJSONArray(JSON_SNACK_ITEM_ROOT_KEY);

            for (int i = 0; i < snackItemsJsonArray.length(); i++)
            {
                JSONObject snackItemJson = snackItemsJsonArray.optJSONObject(i);
                snackItems.add(new SnackItem(snackItemJson.getString(JSON_SNACK_ITEM_NAME_KEY),
                        snackItemJson.getString(JSON_SNACK_ITEM_TYPE_KEY)));
            }
        }
        catch(org.json.JSONException e)
        {
            Log.e(TAG, "Bad json in assets" + e.getLocalizedMessage());
        }

        if(snackItems.size() == 0)
        {
            Log.e(TAG, "Could not parse json to create snack items list");
            return false;
        }
        return true;
    }

    //save snack items list to internal storage
    private boolean saveSnackList(JSONObject json)
    {
        Log.d(TAG, "Saving snack list to file");
        File file = new File(SnackMartApplication.theApp.getFilesDir(), SNACK_ITEMS_FILE_NAME);
        return FileUtils.writeTextFile(file, json.toString());
    }

    //serialize array list
    private JSONObject jsonize()
    {
        Log.d(TAG,"Serializing snack list array to json");
        JSONObject json = new JSONObject();
        JSONArray itemsArray = new JSONArray();

        try
        {
            for (SnackItem snackItem: snackItems)
            {
                JSONObject jsonItem = new JSONObject();
                jsonItem.put(JSON_SNACK_ITEM_NAME_KEY, snackItem.name);
                jsonItem.put(JSON_SNACK_ITEM_TYPE_KEY, snackItem.type.getTypeValue());
                itemsArray.put(jsonItem);
            }

            json.put(JSON_SNACK_ITEM_ROOT_KEY, itemsArray);
            return json;
        }

        catch(org.json.JSONException e)
        {
            Log.e(TAG, "Could not serialize array list to json. Error: " + e.getLocalizedMessage());
        }
        return null;
    }

    //return items for a given type
    ArrayList<SnackItem> getFilteredSnackList(boolean veggieSelected, boolean nonveggieSeleted)
    {

        Log.d(TAG, "Getting filtered list for given types");
        ArrayList<SnackItem> filteredList = new ArrayList<SnackItem>();

        if(!veggieSelected && !nonveggieSeleted)
        {
            return filteredList;
        }
        else if(veggieSelected && nonveggieSeleted)
        {
            return snackItems;
        }
        else
        {
            for (SnackItem snackItem: snackItems)
            {
                if((snackItem.type == SnackType.VEGGIE && veggieSelected) ||
                        (snackItem.type == SnackType.NONVEGGIE && nonveggieSeleted))
                {
                    filteredList.add(snackItem);
                    Log.d(TAG, "Added to filtered list: " + snackItem.name);
                }
            }
            return filteredList;
        }
    }

    //check if snack item exists in the list
    private boolean snackItemExists(String name)
    {
        Log.d(TAG, "Checking if snack item exists");
        for (SnackItem snackItem: snackItems)
        {
            if(snackItem.name.equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    //add new snack item to the list
    boolean addSnackItem(String name, String type)
    {
        Log.d(TAG, "Checking if snack item exists");
        //TODO: add more checks here based on specs
        if(name == null || name.isEmpty() || snackItemExists(name)) return false;

        snackItems.add(new SnackItem(name, type));
        JSONObject json = jsonize();
        Log.i(TAG, "New json = " + json.toString());

        saveSnackList(json);
        Log.i(TAG, "Added new item and saved to internal storage");
        return true;
    }

    //unselect veggie
    void unselectVeggie()
    {
        Log.d(TAG, "Unselecting Veggie");
        for (SnackItem snackItem : snackItems)
        {
            if(snackItem.type == SnackType.VEGGIE)
            {
                snackItem.selected = false;
            }
        }
    }

    //unselect veggie
    void unselectNonveggie()
    {
        Log.d(TAG, "Unselecting Nonveggie");
        for (SnackItem snackItem : snackItems)
        {
            if(snackItem.type == SnackType.NONVEGGIE)
            {
                snackItem.selected = false;
            }
        }
    }

    void resetSelection()
    {
        Log.d(TAG, "Resettings selection of all snack items");
        unselectVeggie();
        unselectNonveggie();
    }
}