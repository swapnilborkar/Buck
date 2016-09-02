package com.swapnilborkar.buck.helpers;

import com.swapnilborkar.buck.Constants;
import com.swapnilborkar.buck.value_objects.Currency;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SWAPNIL on 09-08-2016.
 */
public class CurrencyParserHelper {

    public static Currency parseCurrency(JSONObject obj, String currencyName) {
        Currency currency = new Currency();
        currency.setBase(obj.optString(Constants.BASE));

        return null;
    }

}