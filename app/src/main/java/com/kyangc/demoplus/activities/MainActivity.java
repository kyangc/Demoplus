package com.kyangc.demoplus.activities;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.activities.base.BaseActivity;
import com.kyangc.demoplus.app.DemoApp;
import com.kyangc.demoplus.fragments.HttpClientFragment;
import com.kyangc.demoplus.fragments.MainFragment;
import com.kyangc.demoplus.settings.SettingManager;
import com.kyangc.demoplus.utils.S;
import com.kyangc.demoplus.utils.T;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.Bind;

public class MainActivity extends BaseActivity {

    protected static final int FRAGMENT_TYPE_HOME = 0;

    protected static final int FRAGMENT_TYPE_HTTP = 1;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.navigationView)
    NavigationView navigationView;

    @Bind(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @Bind(R.id.mainContainer)
    CoordinatorLayout mainContainer;

    @Bind(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @Bind(R.id.flFragmentContainer)
    FrameLayout flFragmentContainer;

    ActionBarDrawerToggle drawerToggle;

    MainActivity context;

    MainFragment mainFragment;

    HttpClientFragment httpClientFragment;

    Fragment currentFragment;

    FragmentManager fragmentManager;

    FragmentTransaction fragmentTransaction;

    int currentFragmentType = 0;

    long firstTimePressBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initViews();
        setFragment(currentFragmentType);
    }

    @Override
    public void initData() {
        super.initData();

        //Context
        context = this;

        //Fragment
        fragmentManager = getFragmentManager();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            long delta = System.currentTimeMillis() - firstTimePressBack;
            if (delta > DemoApp.QUIT_INTERVAL) {
                T.show(this, "Press BACK again to quit", (int) (DemoApp.QUIT_INTERVAL * 0.5));
                firstTimePressBack = System.currentTimeMillis();
            } else {
                this.finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayFab(SettingManager.getIsFabShown());
    }

    @Override
    public void initViews() {
        super.initViews();

        //Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open,
                R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);

        //Drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Intent i;
                        switch (menuItem.getItemId()) {
                            case R.id.home:
                                setFragment(FRAGMENT_TYPE_HOME);
                                break;
                            case R.id.http_client:
                                setFragment(FRAGMENT_TYPE_HTTP);
                                break;
                            case R.id.sniffer_client:
                                SnifferActivity.start(context);
                                break;
                            case R.id.swipe_demo:
                                i = new Intent(context, SwipeListItemActivity.class);
                                context.startActivity(i);
                                break;
                            case R.id.fast_gallery:
                                GalleryActivity.start(MainActivity.this);
                                break;
                            case R.id.about:
                                i = new Intent(context, AboutActivity.class);
                                context.startActivity(i);
                                break;
                            case R.id.setting:
                                i = new Intent(context, SettingsActivity.class);
                                context.startActivity(i);
                                break;
                            case R.id.quit:
                                finish();
                                break;
                        }
                        drawerLayout.closeDrawer(navigationView);
                        return true;
                    }
                });
    }

    private void displayFab(boolean isShown) {
        fab.setVisibility(isShown ? View.VISIBLE : View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S.show(context, fab, "我只是出来卖个萌", "滚粗", false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //do nothing
                    }
                });
            }
        });
    }

    private void setFragment(int type) {
        currentFragmentType = type;
        if (currentFragment != null) {
            fragmentManager
                    .beginTransaction()
                    .hide(currentFragment)
                    .commit();
        }
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (type) {
            case FRAGMENT_TYPE_HOME:
                if (mainFragment == null) {
                    mainFragment = MainFragment.newInstance();
                    fragmentTransaction
                            .add(R.id.flFragmentContainer, mainFragment, MainFragment.TAG);
                } else {
                    fragmentTransaction.show(mainFragment);
                }
                currentFragment = mainFragment;
                toolbar.setTitle(MainFragment.TITLE);
                break;
            case FRAGMENT_TYPE_HTTP:
                if (httpClientFragment == null) {
                    httpClientFragment = HttpClientFragment.newInstance();
                    fragmentTransaction.add(R.id.flFragmentContainer, httpClientFragment,
                            HttpClientFragment.TAG);
                } else {
                    fragmentTransaction.show(httpClientFragment);
                }
                currentFragment = httpClientFragment;
                toolbar.setTitle(HttpClientFragment.TITLE);
                break;
            default:
                break;
        }
        fragmentTransaction.commit();
    }
}
