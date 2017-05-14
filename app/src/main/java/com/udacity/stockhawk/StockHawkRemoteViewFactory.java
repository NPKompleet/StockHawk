package com.udacity.stockhawk;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.StockProvider;


/**
 * Created by PHENOMENON on 5/12/2017.
 */

public class StockHawkRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Cursor mCursor;

    public StockHawkRemoteViewFactory(Context context, Intent intent){
        mContext= context;
    }

    public void initCursor(){
        if (mCursor != null) {
            mCursor.close();
        }

        final long identityToken = Binder.clearCallingIdentity();
        /**This is done because the widget runs as a separate thread
         when compared to the current app and hence the app's data won't be accessible to it
         because I'm using a content provided **/

        //fetch data from database ordered by the symbol column
        mCursor = mContext.getContentResolver().query(Contract.Quote.URI,
                new String[]{Contract.Quote.COLUMN_SYMBOL, Contract.Quote.COLUMN_PRICE,
                        Contract.Quote.COLUMN_ABSOLUTE_CHANGE}, null, null, Contract.Quote.COLUMN_SYMBOL);
        Binder.restoreCallingIdentity(identityToken);

    }

    @Override
    public void onCreate() {
        initCursor();
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
    }

    @Override
    public void onDataSetChanged() {
        initCursor();
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {

        return mCursor==null ? 0 : mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (position == AdapterView.INVALID_POSITION ||
                mCursor == null || !mCursor.moveToPosition(position)) {
            return null;
        }

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

        rv.setTextViewText(R.id.widget_stock_symbol, mCursor.getString(0));
        rv.setTextViewText(R.id.widget_stock_quote, mCursor.getString(1));

        if (mCursor.getFloat(2) > 0.00f){
            rv.setInt(R.id.widget_stock_ind, "setImageResource", R.drawable.ic_arrow_drop_up_black_24dp);
        } else {
            rv.setInt(R.id.widget_stock_ind, "setImageResource", R.drawable.ic_arrow_drop_down_black_24dp);
        }

        Intent fillInIntent= new Intent();
        fillInIntent.putExtra(mContext.getString(R.string.symbol_intent_key),  mCursor.getString(0));
        rv.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

        return rv;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
