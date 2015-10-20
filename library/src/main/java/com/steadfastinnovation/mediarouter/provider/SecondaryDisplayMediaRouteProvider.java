package com.steadfastinnovation.mediarouter.provider;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.os.Build;
import android.support.v7.media.MediaRouteDescriptor;
import android.support.v7.media.MediaRouteProvider;
import android.support.v7.media.MediaRouteProviderDescriptor;
import android.support.v7.media.MediaRouter;
import android.util.SparseArray;
import android.view.Display;

public class SecondaryDisplayMediaRouteProvider extends MediaRouteProvider implements DisplayManager.DisplayListener {

    public static final String CATEGORY_SECONDARY_DISPLAY_ROUTE =
            "com.steadfastinnovation.mediarouter.provider.CATEGORY_SECONDARY_DISPLAY_ROUTE";

    private DisplayManager mDisplayManager;
    private SparseArray<MediaRouteDescriptor> mDescriptors;
    private IntentFilter mSecondaryDisplayIntentFilter;
    private String mDisplayDiscription;

    public SecondaryDisplayMediaRouteProvider(Context context, DisplayManager displayManager) {
        super(context);
        mDisplayManager = displayManager;

        mDescriptors = new SparseArray<>();
        mSecondaryDisplayIntentFilter = new IntentFilter();
        mSecondaryDisplayIntentFilter.addCategory(CATEGORY_SECONDARY_DISPLAY_ROUTE);
        mDisplayDiscription = context.getString(R.string.sfi_secondary_display_route_description);
    }

    protected void startListening() {
        // Repopulate descriptors when registering the listener
        mDescriptors.clear();
        Display[] displays = mDisplayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
        upsertDisplay(displays);
        publishRoutes();

        mDisplayManager.registerDisplayListener(this, null);
    }

    protected void stopListening() {
        mDisplayManager.unregisterDisplayListener(this);
    }

    private void publishRoutes() {
        MediaRouteProviderDescriptor.Builder descriptorProviderBuilder = new MediaRouteProviderDescriptor.Builder();
        for (int i = 0, size = mDescriptors.size(); i < size; i++) {
            descriptorProviderBuilder.addRoute(mDescriptors.valueAt(i));
        }
        setDescriptor(descriptorProviderBuilder.build());
    }

    private void upsertDisplay(Display... displays) {
        for (Display d : displays) {
            if (d != null && isPublicPresentation(d)) {
                try {
                    MediaRouteDescriptor descriptor = new MediaRouteDescriptor.Builder("" + d.getDisplayId(), d.getName())
                            .setDescription(mDisplayDiscription)
                            .setPresentationDisplayId(d.getDisplayId())
                            .addControlFilter(mSecondaryDisplayIntentFilter)
                            .setEnabled(true)
                            .setPlaybackType(MediaRouter.RouteInfo.PLAYBACK_TYPE_LOCAL)
                            .setPlaybackStream(AudioManager.STREAM_MUSIC)
                            .setVolumeHandling(MediaRouter.RouteInfo.PLAYBACK_VOLUME_FIXED)
                            .setVolumeMax(10)
                            .setVolume(10)
                            .build();
                    mDescriptors.put(d.getDisplayId(), descriptor);
                } catch (NullPointerException npe) {
                    // Continue, the display info changed under the display
                }
            }
        }
    }

    public boolean isPublicPresentation(Display d) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT
                || (d.getFlags() & (Display.FLAG_PRIVATE | Display.FLAG_PRESENTATION)) == Display.FLAG_PRESENTATION;
    }

    @Override
    public void onDisplayAdded(int displayId) {
        upsertDisplay(mDisplayManager.getDisplay(displayId));
        publishRoutes();
    }

    @Override
    public void onDisplayRemoved(int displayId) {
        mDescriptors.delete(displayId);
        publishRoutes();
    }

    @Override
    public void onDisplayChanged(int displayId) {
        if (mDescriptors.get(displayId) != null) {
            // Prevent main screen from getting into the descriptor list
            upsertDisplay(mDisplayManager.getDisplay(displayId));
        }
        publishRoutes();
    }
}
