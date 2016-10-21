package com.swapnilborkar.buck;

import android.content.Intent;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.swapnilborkar.buck.adapters.CurrencyAdapter;
import com.swapnilborkar.buck.database.CurrencyDatabaseAdapter;
import com.swapnilborkar.buck.database.CurrencyTableHelper;
import com.swapnilborkar.buck.receivers.CurrencyReceiver;
import com.swapnilborkar.buck.services.CurrencyService;
import com.swapnilborkar.buck.utils.AlarmUtils;
import com.swapnilborkar.buck.utils.LogUtils;
import com.swapnilborkar.buck.utils.NotificationUtils;
import com.swapnilborkar.buck.utils.SharedPreferencesUtils;
import com.swapnilborkar.buck.value_objects.Currency;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements CurrencyReceiver.Receiver {

    public static final String LOG_TAG = MainActivity.class.getName();

    //CURRENCY CONVERSION BASE AND TARGET FROM CONSTANT ARRAYS
    private String mBaseCurrency = Constants.CURRENCY_CODES[15];
    private String mTargetCurrency = Constants.CURRENCY_CODES[30];
    private CurrencyTableHelper currencyTableHelper;

    private int mServiceRepetition = AlarmUtils.REPEAT.REPEAT_EVERY_MINUTE.ordinal();

    private CoordinatorLayout mLogLayout;
    private Boolean mIsLogVisible = true;
    private ListView mBaseCurrencyList;
    private ListView mTargetCurrencyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resetDownloads();
        initCurrencies();
        initDb();
        initToolbar();
        initSpinner();
        initCurrencyList();
        showLogs();

        mLogLayout = (CoordinatorLayout) findViewById(R.id.logLayout);

    }

    private void showLogs() {

        final TextView logText = (TextView) findViewById(R.id.logText);
        LogUtils.setLogListener(new LogUtils.LogListener() {
            @Override
            public void onLogged(final StringBuffer log) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logText.setText(log.toString());
                        logText.invalidate();
                    }
                });
            }
        });

    }

    private void initCurrencies() {
        mBaseCurrency = SharedPreferencesUtils.getCurrency(this, true);
        mTargetCurrency = SharedPreferencesUtils.getCurrency(this, false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mServiceRepetition = SharedPreferencesUtils.getServiceRepetition(this);
        retrieveCurrencyExchangeRate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.setLogListener(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_logs:
                LogUtils.clearLogs();
                return true;

            case R.id.action_show_logs:
                mIsLogVisible = !mIsLogVisible;
                item.setIcon(mIsLogVisible ? R.drawable.ic_keyboard_hide : R.drawable.ic_keyboard);
                mLogLayout.setVisibility(mIsLogVisible ? View.VISIBLE : View.GONE);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onReceiveResult(int resultCode, final Bundle resultData) {

        switch (resultCode) {
            case Constants.STATUS_RUNNING:
                LogUtils.log(LOG_TAG, "Currency service is running!");
                break;

            case Constants.STATUS_FINISHED:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Currency currencyParcel = resultData.getParcelable(Constants.RESULT);

                        if (currencyParcel != null) {
                            String message =
                                    "Currency: " + currencyParcel.getBase() +
                                            " - " + currencyParcel.getName() +
                                            ": " + currencyParcel.getRate();
                            LogUtils.log(LOG_TAG, message);
                            long id = currencyTableHelper.insertCurrency(currencyParcel);
                            Currency currency = new Currency();
                            try {
                                currency = currencyTableHelper.getCurrency(id);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                LogUtils.log(LOG_TAG, "Currency retrieval failed!");
                            }

                            if (currency != null) {
                                String dbmessage = "Currency (DB): " + currency.getBase() + " - "
                                        + currency.getName() + " : " + currency.getRate();
                                LogUtils.log(LOG_TAG, dbmessage);

                                NotificationUtils.showNotificationMessage(getApplicationContext(),
                                        "Currency Exchange Rate", message);
                            }

                            if (NotificationUtils.isAppInBackground(MainActivity.this)) {
                                int numDownloads = SharedPreferencesUtils.getNumDownloads(getApplicationContext());
                                SharedPreferencesUtils.updateNumDownloads(getApplicationContext(), ++numDownloads);
                                if (numDownloads == Constants.MAX_DOWNLOADS) {
                                    LogUtils.log(LOG_TAG, "Max downloads for background processing has been reached!");
                                    mServiceRepetition = AlarmUtils.REPEAT.REPEAT_EVERYDAY.ordinal();
                                    retrieveCurrencyExchangeRate();
                                }
                            }
                        }
                    }
                });
                break;

            case Constants.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                LogUtils.log(LOG_TAG, error);
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
    }

    private void initDb() {
        CurrencyDatabaseAdapter currencyDatabaseAdapter = new CurrencyDatabaseAdapter(this);
        currencyTableHelper = new CurrencyTableHelper(currencyDatabaseAdapter);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void initSpinner() {
        final Spinner spinner = (Spinner) findViewById(R.id.timeFrequency);
        spinner.setSaveEnabled(true);
        spinner.setSelection(SharedPreferencesUtils.getServiceRepetition(this), false);
        spinner.post(new Runnable() {
            @Override
            public void run() {
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        ((TextView) view).setTextColor(getResources().getColor(R.color.materialWhite));
                        SharedPreferencesUtils.updateServiceRepetition(MainActivity.this, i);
                        mServiceRepetition = i;
                        if (i > AlarmUtils.REPEAT.values().length) {
                            AlarmUtils.stopService();
                        } else {
                            retrieveCurrencyExchangeRate();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {


                    }
                });
            }
        });
    }

    private void initCurrencyList() {
        mBaseCurrencyList = (ListView) findViewById(R.id.baseCurrencyList);
        mTargetCurrencyList = (ListView) findViewById(R.id.targetCurrencyList);

        CurrencyAdapter baseCurrencyAdapter = new CurrencyAdapter(this);
        CurrencyAdapter targetCurrencyAdapter = new CurrencyAdapter(this);

        mBaseCurrencyList.setAdapter(baseCurrencyAdapter);
        mTargetCurrencyList.setAdapter(targetCurrencyAdapter);

        int baseCurrencyIndex = retrieveIndexOf(mBaseCurrency);
        int targetCurrencyIndex = retrieveIndexOf(mTargetCurrency);

        mBaseCurrencyList.setItemChecked(baseCurrencyIndex, true);
        mTargetCurrencyList.setItemChecked(targetCurrencyIndex, true);

        mBaseCurrencyList.setSelection(baseCurrencyIndex);
        mTargetCurrencyList.setSelection(targetCurrencyIndex);

        addCurrencySelectionListener();

    }

    private void addCurrencySelectionListener() {
        mBaseCurrencyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBaseCurrency = Constants.CURRENCY_CODES[i];
                LogUtils.log(LOG_TAG, "Base currency has changed to " + mBaseCurrency);
                SharedPreferencesUtils.updateCurrency(MainActivity.this, mBaseCurrency, true);
                retrieveCurrencyExchangeRate();
            }
        });

        mTargetCurrencyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mTargetCurrency = Constants.CURRENCY_CODES[i];
                LogUtils.log(LOG_TAG, "Target currency has changed to " + mTargetCurrency);
                SharedPreferencesUtils.updateCurrency(MainActivity.this, mTargetCurrency, false);
                retrieveCurrencyExchangeRate();
            }
        });
    }


    private int retrieveIndexOf(String currency) {
        return Arrays.asList(Constants.CURRENCY_CODES).indexOf(currency);
    }

    private void retrieveCurrencyExchangeRate() {

        if (mServiceRepetition < AlarmUtils.REPEAT.values().length) {
            CurrencyReceiver receiver = new CurrencyReceiver(new Handler());
            receiver.setReceiver(this);
            Intent intent = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(), CurrencyService.class);
            intent.setExtrasClassLoader(CurrencyService.class.getClassLoader());

            Bundle bundle = new Bundle();
            String url = Constants.CURRENCY_URL + mBaseCurrency;
            bundle.putString(Constants.URL, url);
            bundle.putParcelable(Constants.RECEIVER, receiver);
            bundle.putInt(Constants.REQUEST_ID, Constants.REQUEST_ID_NUM);
            bundle.putString(Constants.CURRENCY_NAME, mTargetCurrency);
            bundle.putString(Constants.CURRENCY_BASE, mBaseCurrency);
            intent.putExtra(Constants.BUNDLE, bundle);

            //startService(intent);
            AlarmUtils.startService(this, intent,
                    AlarmUtils.REPEAT.values()[mServiceRepetition]);
        }

    }

    private void resetDownloads() {
        SharedPreferencesUtils.updateNumDownloads(this, 0);
    }

}
