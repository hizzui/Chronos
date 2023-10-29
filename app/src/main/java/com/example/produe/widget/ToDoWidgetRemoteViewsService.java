package com.example.produe.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

//  WidgetService is the {@link RemoteViewsService} that will return the RemoteViewsFactory
public class ToDoWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        // Return remote views factory object
        return new ToDoWidgetRemoteViewsFactory(this);
    }
}
