package com.udacity.stockhawk;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by PHENOMENON on 5/14/2017.
 */

public class StockWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockHawkRemoteViewFactory(this, intent);
    }
}
