package com.steadfastinnovation.mediarouter.provider;

import android.hardware.display.DisplayManager;

import androidx.mediarouter.media.MediaRouteProvider;
import androidx.mediarouter.media.MediaRouteProviderService;

public class SecondaryDisplayMediaRouteProviderService extends MediaRouteProviderService {

    private SecondaryDisplayMediaRouteProvider mMediaRouteProvider;

    private synchronized SecondaryDisplayMediaRouteProvider getSecondaryDisplayMediaRouteProvider() {
        if (mMediaRouteProvider == null) {
            mMediaRouteProvider = new SecondaryDisplayMediaRouteProvider(this,
                    (DisplayManager) getSystemService(DISPLAY_SERVICE));
        }
        return mMediaRouteProvider;
    }

    @Override
    public MediaRouteProvider onCreateMediaRouteProvider() {
        return getSecondaryDisplayMediaRouteProvider();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        getSecondaryDisplayMediaRouteProvider().startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getSecondaryDisplayMediaRouteProvider().stopListening();
    }
}
