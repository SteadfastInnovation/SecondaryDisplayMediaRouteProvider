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
import android.support.v7.media.MediaRouter.RouteInfo;
import android.util.SparseArray;
import android.view.Display;

import java.util.List;

public class SecondaryDisplayMediaRouteProvider extends MediaRouteProvider implements DisplayManager.DisplayListener {

    public static final String CATEGORY_SECONDARY_DISPLAY_ROUTE =
            "com.steadfastinnovation.mediarouter.provider.CATEGORY_SECONDARY_DISPLAY_ROUTE";

    private DisplayManager mDisplayManager;
    private SparseArray<MediaRouteDescriptor> mDescriptors;
    private IntentFilter mSecondaryDisplayIntentFilter;
    private String mDisplayDescription;

    public SecondaryDisplayMediaRouteProvider(Context context, DisplayManager displayManager) {
        super(context);
        mDisplayManager = displayManager;

        mDescriptors = new SparseArray<>();
        mSecondaryDisplayIntentFilter = new IntentFilter();
        mSecondaryDisplayIntentFilter.addCategory(CATEGORY_SECONDARY_DISPLAY_ROUTE);
        mDisplayDescription = context.getString(R.string.sfi_secondary_display_route_description);
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
        List<RouteInfo> routes = MediaRouter.getInstance(getContext()).getRoutes();
        for (Display d : displays) {
            try {
                // Only add a route if the display is a public presentation display and there are
                // no routes that already provide the display
                if (d != null && isPublicPresentation(d) && !isDisplayProvidedByRoute(d, routes)) {
                    MediaRouteDescriptor descriptor = new MediaRouteDescriptor.Builder("" + d.getDisplayId(), d.getName())
                            .setDescription(mDisplayDescription)
                            .setPresentationDisplayId(d.getDisplayId())
                            .addControlFilter(mSecondaryDisplayIntentFilter)
                            .setEnabled(true)
                            .setPlaybackType(RouteInfo.PLAYBACK_TYPE_LOCAL)
                            .setPlaybackStream(AudioManager.STREAM_MUSIC)
                            .setVolumeHandling(RouteInfo.PLAYBACK_VOLUME_FIXED)
                            .setVolumeMax(10)
                            .setVolume(10)
                            .setDeviceType(RouteInfo.DEVICE_TYPE_TV)
                            .build();
                    mDescriptors.put(d.getDisplayId(), descriptor);
                }
            } catch (NullPointerException npe) {
                // Continue, the display info changed under the display
            }
        }
    }

    public boolean isPublicPresentation(Display d) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT
                || (d.getFlags() & (Display.FLAG_PRIVATE | Display.FLAG_PRESENTATION)) == Display.FLAG_PRESENTATION;
    }

    private boolean isDisplayProvidedByRoute(Display d, List<RouteInfo> routes) {
        for (RouteInfo route : routes) {
            if (route.isDefault()) {
                // Skip default route
                continue;
            }
            Display routeDisplay = route.getPresentationDisplay();
            if (routeDisplay != null && routeDisplay.getDisplayId() == d.getDisplayId()) {
                // This route is providing the display
                return true;
            }
        }
        return false;
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
