package com.swapnilborkar.buck.helpers;

import com.swapnilborkar.buck.Constants;
import com.swapnilborkar.buck.value_objects.Currency;

import org.json.JSONObject;

/**
 * Created by SWAPNIL on 09-08-2016.
 */
public class CurrencyParserHelper {

    public static Currency parseCurrency(JSONObject object, String currencyName) {
        Currency currency = new Currency();
        currency.setBase(object.optString(Constants.BASE));
        currency.setDate(object.optString(Constants.DATE));
        JSONObject rateObject = object.optJSONObject(Constants.RATES);
        if (rateObject != null) {
            currency.setRate(rateObject.optDouble(currencyName));
        }

        currency.setName(currencyName);
        return currency;
    }

}