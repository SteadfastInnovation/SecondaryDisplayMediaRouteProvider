package com.steadfastinnovation.mediarouter;

import android.app.Presentation;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.steadfastinnovation.mediarouter.provider.SecondaryDisplayMediaRouteProvider;

public class MainActivity extends AppCompatActivity {
    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private MediaRouterCallback mMediaRouterCallback;

    private Presentation mCurrentPresentation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mMediaRouter = MediaRouter.getInstance(getApplicationContext());
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(SecondaryDisplayMediaRouteProvider.CATEGORY_SECONDARY_DISPLAY_ROUTE)
                .addControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)
                .build();
        mMediaRouterCallback = new MediaRouterCallback();

        RouteInfo route = mMediaRouter.getSelectedRoute();
        startPresentation(route);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    @Override
    protected void onStop() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);

        return true;
    }

    private class MediaRouterCallback extends MediaRouter.Callback {
        @Override
        public void onRouteSelected(MediaRouter router, RouteInfo route) {
            super.onRouteSelected(router, route);
            startPresentation(route);
        }

        @Override
        public void onRouteUnselected(MediaRouter router, RouteInfo route) {
            super.onRouteUnselected(router, route);
            stopPresentation();
        }

        @Override
        public void onRouteChanged(MediaRouter router, RouteInfo route) {
            super.onRouteChanged(router, route);
            startPresentation(route);
        }

        @Override
        public void onRoutePresentationDisplayChanged(MediaRouter router, RouteInfo route) {
            super.onRoutePresentationDisplayChanged(router, route);
            startPresentation(route);
        }
    }

    private void startPresentation(RouteInfo route) {
        // Only start the presentation if the route is selected, not the default route,
        // has presentation display, and supports one of the categories we care about
        if (route != null && route.isSelected() && !route.isDefault() && route.getPresentationDisplay() != null
                && route.matchesSelector(mMediaRouteSelector)) {
            stopPresentation();
            mCurrentPresentation = new ColorPresentation(this, route.getPresentationDisplay());
            mCurrentPresentation.show();
        }
    }

    private void stopPresentation() {
        if (mCurrentPresentation != null) {
            mCurrentPresentation.dismiss();
            mCurrentPresentation = null;
        }
    }

}
