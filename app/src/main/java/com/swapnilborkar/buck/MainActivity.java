package com.swapnilborkar.buck;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.swapnilborkar.buck.database.CurrencyDatabaseAdapter;
import com.swapnilborkar.buck.database.CurrencyTableHelper;
import com.swapnilborkar.buck.receivers.CurrencyReceiver;
import com.swapnilborkar.buck.services.CurrencyService;
import com.swapnilborkar.buck.utils.LogUtils;
import com.swapnilborkar.buck.value_objects.Currency;

public class MainActivity extends AppCompatActivity implements CurrencyReceiver.Receiver {

    public static final String LOG_TAG = MainActivity.class.getName();

    //CURRENCY CONVERSION BASE AND TARGET FROM CONSTANT ARRAYS
    private String mBaseCurrency = Constants.CURRENCY_CODES[15];
    private String mTargetCurrency = Constants.CURRENCY_CODES[30];
    private CurrencyTableHelper currencyTableHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDb();
        retrieveCurrencyExchangeRate();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//}


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

    private void retrieveCurrencyExchangeRate() {

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
        startService(intent);

    }

}
