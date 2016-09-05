package com.swapnilborkar.buck.database;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

import com.swapnilborkar.buck.Constants;
import com.swapnilborkar.buck.utils.LogUtils;
import com.swapnilborkar.buck.value_objects.Currency;

import java.util.ArrayList;

/**
 * Created by SWAPNIL on 03-09-2016.
 */
public class CurrencyTableHelper {

    public static final String LOG_TAG = CurrencyTableHelper.class.getName();
    private CurrencyDatabaseAdapter mAdapter;

    public CurrencyTableHelper(CurrencyDatabaseAdapter adapter) {
        this.mAdapter = adapter;
    }

    public long insertCurrency(Currency currency) {
        ArrayList<Currency> currencies = getCurrencyHistory(currency.getBase(), currency.getName(), currency.getDate());
        if (currencies.size() == 0) {
            LogUtils.log(LOG_TAG, "No records found in database!");
            ContentValues initialValues = new ContentValues();
            initialValues.put(Constants.KEY_BASE, currency.getBase());
            initialValues.put(Constants.KEY_NAME, currency.getName());
            initialValues.put(Constants.KEY_DATE, currency.getDate());
            initialValues.put(Constants.KEY_RATE, currency.getRate());
            long id = mAdapter.getWritableDatabase().insert(Constants.CURRENCY_TABLE, null, initialValues);
            mAdapter.getWritableDatabase().close();
            return id;
        } else {
            LogUtils.log(LOG_TAG, "Record found in database!");
        }

        return currencies.get(0).getId();
    }

    public ArrayList<Currency> getCurrencyHistory(String base, String name, String date) {

        ArrayList<Currency> currencies = new ArrayList<>();

        Cursor cursor = mAdapter.getWritableDatabase().query(Constants.CURRENCY_TABLE,
                new String[]{Constants.KEY_ID, Constants.KEY_BASE, Constants.KEY_DATE, Constants.KEY_RATE, Constants.KEY_NAME},
                Constants.KEY_BASE + " = '" + base
                        + "' AND " + Constants.KEY_NAME + " = " + "'" + name
                        + "' AND " + Constants.KEY_DATE + " = '" + date + "'",
                null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                currencies.add(parseCurrency(cursor));
            }
            while (cursor.moveToNext()) {
                currencies.add(parseCurrency(cursor));
            }
        }

        return currencies;

    }

    public ArrayList<Currency> getCurrencyHistory(String base, String name) {

        ArrayList<Currency> currencies = new ArrayList<>();

        Cursor cursor = mAdapter.getWritableDatabase().query(Constants.CURRENCY_TABLE,
                new String[]{Constants.KEY_ID, Constants.KEY_BASE, Constants.KEY_DATE, Constants.KEY_RATE, Constants.KEY_NAME},
                Constants.KEY_BASE + " = '" + base
                        + "' AND " + Constants.KEY_NAME + " = " + "'" + name
                        + "'",
                null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                currencies.add(parseCurrency(cursor));
            }
            while (cursor.moveToNext()) {
                currencies.add(parseCurrency(cursor));
            }
        }

        return currencies;

    }

    private Currency parseCurrency(Cursor cursor) {

        Currency currency = new Currency();
        currency.setId(cursor.getLong(cursor.getColumnIndex(Constants.KEY_ID)));
        currency.setBase(cursor.getString(cursor.getColumnIndex(Constants.KEY_BASE)));
        currency.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_NAME)));
        currency.setRate(cursor.getInt(cursor.getColumnIndex(Constants.KEY_RATE)));
        currency.setDate(cursor.getString(cursor.getColumnIndex(Constants.KEY_DATE)));
        return currency;
    }

    public void clearCurrencyTable() {
        mAdapter.getWritableDatabase().delete(Constants.CURRENCY_TABLE, null, null);
    }

    public Currency getCurrency(long id) throws SQLException {

        Cursor cursor = mAdapter.getWritableDatabase().query(Constants.CURRENCY_TABLE, new String[]
                        {Constants.KEY_ID, Constants.KEY_BASE, Constants.KEY_NAME, Constants.KEY_RATE, Constants.KEY_DATE},
                Constants.KEY_ID + " = " + id, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return parseCurrency(cursor);
            }
        }
        return null;
    }


}

