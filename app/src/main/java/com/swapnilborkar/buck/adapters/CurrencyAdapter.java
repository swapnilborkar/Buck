package com.swapnilborkar.buck.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.swapnilborkar.buck.Constants;
import com.swapnilborkar.buck.R;

/**
 * Created by SWAPNIL on 19-09-2016.
 */

public class CurrencyAdapter extends BaseAdapter {

    private Context mContext;

    public CurrencyAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return Constants.CURRENCY_CODE_SIZE;
    }

    @Override
    public Object getItem(int i) {
        return Constants.CURRENCY_CODES[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.currency_item, null);
            viewHolder.textView = (TextView) view.findViewById(R.id.currencyText);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView.setText(Constants.CURRENCY_NAMES[i] + "(" + Constants.CURRENCY_CODES[i] + ")");
        return view;
    }

    private static class ViewHolder {
        TextView textView;
    }

}
