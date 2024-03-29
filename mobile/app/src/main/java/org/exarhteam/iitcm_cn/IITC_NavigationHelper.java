package org.exarhteam.iitcm_cn;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

public class IITC_NavigationHelper extends ActionBarDrawerToggle implements OnItemClickListener {

    // Show/hide the up arrow on the very left
    // getActionBar().setDisplayHomeAsUpEnabled(enabled);

    // Show/hide the activity icon/logo
    // getActionBar().setDisplayShowHomeEnabled(enabled);

    // Show/hide the activity title
    // getActionBar().setDisplayShowTitleEnabled(enabled);

    // Makes the icon/title clickable
    // getActionBar().setHomeButtonEnabled(enabled);

    private final IITC_Mobile mIitc;
    private final ActionBar mActionBar;
    private final SharedPreferences mPrefs;
    private final NavigationAdapter mNavigationAdapter;
    private final DrawerLayout mDrawerLayout;
    private final ListView mDrawerLeft;
    private final View mDrawerRight;
    private final IITC_NotificationHelper mNotificationHelper;

    private boolean mDesktopMode = false;
    private Pane mPane = Pane.MAP;
    private String mHighlighter = null;

    public IITC_NavigationHelper(final IITC_Mobile iitc, final ActionBar bar, Toolbar toolbar) {
        super(iitc, (DrawerLayout) iitc.findViewById(R.id.drawer_layout),
                toolbar, R.string.drawer_open, R.string.drawer_close);

        mIitc = iitc;
        mActionBar = bar;
        mDrawerLeft = (ListView) iitc.findViewById(R.id.left_drawer);
        mDrawerRight = iitc.findViewById(R.id.right_drawer);
        mDrawerLayout = (DrawerLayout) iitc.findViewById(R.id.drawer_layout);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(iitc);

        mActionBar.setDisplayShowHomeEnabled(true); // show icon

        mNavigationAdapter = new NavigationAdapter();
        mDrawerLeft.setAdapter(mNavigationAdapter);
        mDrawerLeft.setOnItemClickListener(this);
        mDrawerLeft.setItemChecked(0, true);
        mDrawerLayout.setDrawerListener(this);
        mNotificationHelper = new IITC_NotificationHelper(mIitc);

        onPrefChanged(); // also calls updateActionBar()

        // workaround for not working home-button on v7 toolbar
        setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIitc.switchToPane(Pane.MAP);
            }
        });

        mNotificationHelper.showNotice(IITC_NotificationHelper.NOTICE_HOWTO);
    }

    private void updateViews() {
        final int position = mNavigationAdapter.getPosition(mPane);
        if (position >= 0 && position < mNavigationAdapter.getCount()) {
            mDrawerLeft.setItemChecked(position, true);
        } else {
            mDrawerLeft.setItemChecked(mDrawerLeft.getCheckedItemPosition(), false);
        }

        if (mDesktopMode) {
            mActionBar.setDisplayHomeAsUpEnabled(false); // Hide "up" indicator
            mActionBar.setHomeButtonEnabled(false); // Make icon unclickable
            mActionBar.setTitle(mIitc.getString(R.string.app_name));
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mDrawerLeft);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mDrawerRight);
            setDrawerIndicatorEnabled(false);
        } else {
            if (mIitc.isLoading()) {
                mActionBar.setDisplayHomeAsUpEnabled(false); // Hide "up" indicator
                mActionBar.setHomeButtonEnabled(false);// Make icon unclickable
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                setDrawerIndicatorEnabled(false);
            } else {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                if (mPane == Pane.MAP || mDrawerLayout.isDrawerOpen(mDrawerLeft)) {
                    mActionBar.setDisplayHomeAsUpEnabled(false); // Hide"up" indicator
                    mActionBar.setHomeButtonEnabled(false);// Make icon unclickable
                    setDrawerIndicatorEnabled(true);
                } else {
                    setDrawerIndicatorEnabled(false);
                    mActionBar.setHomeButtonEnabled(true);// Make icon clickable
                    mActionBar.setDisplayHomeAsUpEnabled(true); // Show "up" indicator
                }
            }

            if (mDrawerLayout.isDrawerOpen(mDrawerLeft) || mPane == Pane.MAP) {
                mActionBar.setTitle(mIitc.getString(R.string.app_name));
            } else {
                mActionBar.setTitle(mPane.label);
            }
        }

        final boolean mapVisible = mDesktopMode || mPane == Pane.MAP;
        if ("没有荧光笔".equals(mHighlighter) || isDrawerOpened() || mIitc.isLoading() || !mapVisible) {
            mActionBar.setSubtitle(null);
        } else {
            mActionBar.setSubtitle(mHighlighter);
        }
    }

    public void addPane(final String name, final String label, final String icon) {
        mNotificationHelper.showNotice(IITC_NotificationHelper.NOTICE_PANES);

        final Resources res = mIitc.getResources();
        final String packageName = res.getResourcePackageName(R.string.app_name);
        /*
         * since the package name is overridden in test builds
         * we can't use context.getPackageName() to get the package name
         * because the resources were processed before the package name was finally updated.
         * so we have to retrieve the package name of another resource with Resources.getResourcePackageName()
         * see http://www.piwai.info/renaming-android-manifest-package/
         */
        final int resId = mIitc.getResources().getIdentifier(icon, "drawable", packageName);
        mNavigationAdapter.add(new Pane(name, label, resId));
    }

    public void closeDrawers() {
        mDrawerLayout.closeDrawers();
    }

    public Pane getPane(final String id) {
        for (int i = 0; i < mNavigationAdapter.getCount(); i++) {
            final Pane pane = mNavigationAdapter.getItem(i);
            if (pane.name.equals(id))
                return pane;
        }
        throw new IllegalArgumentException("Unknown pane: " + id);
    }

    public void hideActionBar() {
        mActionBar.hide();
    }

    public boolean isDrawerOpened() {
        return mDrawerLayout.isDrawerOpen(mDrawerLeft) || mDrawerLayout.isDrawerOpen(mDrawerRight);
    }

    @Override
    public void onDrawerClosed(final View drawerView) {
        super.onDrawerClosed(drawerView);

        mIitc.getWebView().onWindowFocusChanged(true);
        // delay invalidating to prevent flickering in case another drawer is opened
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mIitc.invalidateOptionsMenu();
                updateViews();
            }
        }, 200);
    }

    @Override
    public void onDrawerOpened(final View drawerView) {
        super.onDrawerOpened(drawerView);
        mIitc.getWebView().onWindowFocusChanged(false);
        mIitc.invalidateOptionsMenu();
        updateViews();
        mDrawerLayout.closeDrawer(drawerView.equals(mDrawerLeft) ? mDrawerRight : mDrawerLeft);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        final Pane item = mNavigationAdapter.getItem(position);
        mIitc.switchToPane(item);

        if (item == Pane.INFO) {
            mNotificationHelper.showNotice(IITC_NotificationHelper.NOTICE_INFO);
        }

        mDrawerLayout.closeDrawer(mDrawerLeft);
    }

    public void onLoadingStateChanged() {
        updateViews();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.closeDrawer(mDrawerRight);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPostCreate(final Bundle savedInstanceState) {
        // Sync the toggle state after onRestoreInstanceState has occurred.
        syncState();
    }

    public void onPrefChanged() {
        mDesktopMode = mPrefs.getBoolean("pref_force_desktop", false);
        updateViews();
    }

    public void openRightDrawer() {
        if (mDrawerLayout.getDrawerLockMode(mDrawerRight) == DrawerLayout.LOCK_MODE_UNLOCKED) {
            mDrawerLayout.openDrawer(mDrawerRight);
        }
    }

    public void reset() {
        mPane = Pane.MAP;
        mNavigationAdapter.reset();
        updateViews();
    }

    public void setDebugMode(final boolean enabled) {
        mNavigationAdapter.reset();
    }

    public void setHighlighter(final String name) {
        mHighlighter = name;
        updateViews();
    }

    public void showActionBar() {
        mActionBar.show();
    }

    public void switchTo(final Pane pane) {
        mPane = pane;

        if (pane.equals(Pane.INFO)) mNotificationHelper.showNotice(IITC_NotificationHelper.NOTICE_SHARING);
        updateViews();
    }

    private class NavigationAdapter extends ArrayAdapter<Pane> {
        public NavigationAdapter() {
            super(mIitc, R.layout.list_item_selectable);

            reset();
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final TextView view = (TextView) super.getView(position, convertView, parent);
            final Pane item = getItem(position);
            view.setText(item.label);

            if (item.icon != 0) {
                view.setCompoundDrawablesWithIntrinsicBounds(item.icon, 0, 0, 0);
            }

            return view;
        }

        public void reset() {
            clear();

            add(Pane.INFO);
            add(Pane.ALL);
            add(Pane.FACTION);
            add(Pane.ALERTS);
        }
    }

    public static class Pane {
        public static final Pane ALL = new Pane("all", "全部", R.drawable.ic_action_view_as_list);
        public static final Pane FACTION = new Pane("faction", "阵营", R.drawable.ic_action_cc_bcc);
        public static final Pane ALERTS = new Pane("alerts", "警报", R.drawable.ic_action_warning);
        public static final Pane INFO = new Pane("info", "信息", R.drawable.ic_action_about);
        public static final Pane MAP = new Pane("map", "地图", R.drawable.ic_action_map);

        private final int icon;
        public String label;
        public String name;

        public Pane(final String name, final String label, final int icon) {
            this.name = name;
            this.label = label;
            this.icon = icon;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == null) return false;
            if (o.getClass() != getClass()) return false;

            final Pane pane = (Pane) o;
            return name.equals(pane.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
}
