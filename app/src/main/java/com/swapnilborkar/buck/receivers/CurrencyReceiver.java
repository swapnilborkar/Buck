package com.swapnilborkar.buck.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by SWAPNIL on 12-08-2016.
 */
public class CurrencyReceiver extends ResultReceiver {

    private Receiver mReceiver;

    public CurrencyReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver mReceiver) {
        this.mReceiver = mReceiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }
}
