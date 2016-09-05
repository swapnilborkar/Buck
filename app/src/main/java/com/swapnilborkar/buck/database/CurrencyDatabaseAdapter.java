package com.swapnilborkar.buck.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.swapnilborkar.buck.Constants;
import com.swapnilborkar.buck.utils.LogUtils;

/**
 * Created by SWAPNIL on 03-09-2016.
 */
public class CurrencyDatabaseAdapter extends SQLiteOpenHelper {

    public static final String LOG_TAG = CurrencyDatabaseAdapter.class.getName();
    public static final int DATABASE_VERSION = 1;
    public static final String CURRENCY_TABLE_CREATE = "CREATE TABLE "
            + Constants.CURRENCY_TABLE + "("
            + Constants.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Constants.KEY_BASE + " TEXT NOT NULL, "
            + Constants.KEY_NAME + " TEXT NOT NULL, "
            + Constants.KEY_RATE + " REAL, "
            + Constants.KEY_DATE + " DATE);";

    public CurrencyDatabaseAdapter(Context context) {
        super(context, Constants.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        try {
            database.execSQL(CURRENCY_TABLE_CREATE);
            LogUtils.log(LOG_TAG, "Currency table created!");
        } catch (SQLException e) {
            e.printStackTrace();
            LogUtils.log(LOG_TAG, "Currency table creation error!");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        clearCurrentTable(database);
        onCreate(database);
    }

    private void clearCurrentTable(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS" + Constants.CURRENCY_TABLE);
    }
}
