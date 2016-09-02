package com.swapnilborkar.buck.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.swapnilborkar.buck.Constants;
import com.swapnilborkar.buck.helpers.CurrencyParserHelper;
import com.swapnilborkar.buck.utils.LogUtils;
import com.swapnilborkar.buck.utils.WebServiceUtils;
import com.swapnilborkar.buck.value_objects.Currency;

import org.json.JSONObject;

/**
 * Created by SWAPNIL on 23-08-2016.
 */
public class CurrencyService extends IntentService {

    public static final String LOG_TAG = CurrencyService.class.getName();

    public CurrencyService(String name) {
        super(name);
    }

    public CurrencyService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtils.log(LOG_TAG, "Currency Service has started!");
        Bundle intentBundle = intent.getBundleExtra(Constants.BUNDLE);
        final ResultReceiver receiver = intentBundle.getParcelable(Constants.RECEIVER);
        Parcel parcel = Parcel.obtain();
        assert receiver != null;
        receiver.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        ResultReceiver receiverForSending = ResultReceiver.CREATOR.createFromParcel(parcel);
        parcel.recycle();

        String url = intentBundle.getString(Constants.URL);
        String currencyName = intentBundle.getString(Constants.CURRENCY_NAME);

        Bundle bundle = new Bundle();
        if (url != null && !TextUtils.isEmpty(url)) {
            receiverForSending.send(Constants.STATUS_RUNNING, Bundle.EMPTY);
        }

        if (WebServiceUtils.hasInternetConnection(getApplicationContext())) {
            JSONObject jsonObject = WebServiceUtils.requestJsonObject(url);
            if (jsonObject != null) {
                Currency currency = CurrencyParserHelper.parseCurrency(jsonObject, currencyName);
                bundle.putParcelable(Constants.RESULT, currency);
                receiverForSending.send(Constants.STATUS_FINISHED, bundle);
            }
        }
    }
}
