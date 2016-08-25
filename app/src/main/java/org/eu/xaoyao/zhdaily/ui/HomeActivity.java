package org.eu.xaoyao.zhdaily.ui;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import org.eu.xaoyao.zhdaily.R;
import org.eu.xaoyao.zhdaily.bean.NewsThemesBean;
import org.eu.xaoyao.zhdaily.http.ZHApiManager;
import org.eu.xaoyao.zhdaily.utils.ToastUtil;

import rx.Subscriber;

public class HomeActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    private ZHApiManager mZhApiManager;

    private NewsThemesBean mNewsThemes;

    /**
     * 当前是不是在主页面
     */
    private boolean isHome=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mZhApiManager = ZHApiManager.getInstance();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        initToolbar();
        initNavigation();

        //进入主界面
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content,new HomeFragment());
        transaction.commit();

    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator = VectorDrawableCompat.create(getResources(),
                    R.drawable.ic_vector_menu, getTheme());
            indicator.setTint(Color.WHITE);
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * 初始化侧滑菜单中的新闻主题列表
     */
    private void initNavigation() {
        mZhApiManager.getNewsThemes(new Subscriber<NewsThemesBean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(NewsThemesBean newsThemesBean) {
                mNewsThemes = newsThemesBean;
                if (mNewsThemes != null) {
                    Menu menu = mNavigationView.getMenu();
                    //根据服务器返回的信息，添加新闻主题列表
                    for (NewsThemesBean.NewsThemeBean theme : mNewsThemes.others) {
                        menu.add(R.id.news_themes, theme.id, Menu.NONE, theme.name)
                                .setCheckable(true);
                    }

                    mNavigationView.setNavigationItemSelectedListener(
                            new NavigationView.OnNavigationItemSelectedListener() {
                                @Override
                                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                                    if (item.getItemId() == R.id.home) {
                                        mDrawerLayout.closeDrawers();
                                    } else {
                                        for (NewsThemesBean.NewsThemeBean theme : mNewsThemes.others) {
                                            if (theme.id == item.getItemId()) {
//                                                ToastUtil.showToast(getApplicationContext(), theme.name);
                                                Snackbar.make(mNavigationView,theme.name,Snackbar.LENGTH_SHORT).show();
                                                mDrawerLayout.closeDrawers();
                                            }
                                        }
                                    }
                                    return true;
                                }
                            });
                }

            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
