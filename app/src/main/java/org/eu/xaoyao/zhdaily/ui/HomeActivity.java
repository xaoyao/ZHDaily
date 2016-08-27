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

    private HomeFragment mHomeFragment;

    /**
     * 当前是不是在主页面
     */
    private boolean isHome = true;
    private FragmentManager mFragmentManager;

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
        mHomeFragment = new HomeFragment();
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.content, mHomeFragment).commit();

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
                                        if (!isHome) {
                                            mFragmentManager.beginTransaction()
                                                    .replace(R.id.content, mHomeFragment).commit();
                                            isHome = true;
                                        }
                                        mToolbar.setTitle("知乎日报");
                                        mDrawerLayout.closeDrawers();
                                    } else {
                                        toThemeNews(item.getItemId());
                                    }
                                    return true;
                                }
                            });

                }

            }
        });


    }

    /**
     * 进入主题新闻页面
     *
     * @param id
     */
    private void toThemeNews(@NonNull int id) {
        for (NewsThemesBean.NewsThemeBean theme : mNewsThemes.others) {
            if (theme.id == id) {
                ThemeNewsFragment fragment = new ThemeNewsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("themeId", theme.id);
                fragment.setArguments(bundle);

                mFragmentManager.beginTransaction()
                        .replace(R.id.content, fragment).commit();
                mToolbar.setTitle(theme.name);
                mDrawerLayout.closeDrawers();
                isHome = false;
            }
        }
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


    @Override
    public void onBackPressed() {
        if (!isHome) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.content, mHomeFragment).commit();
            isHome = true;
            mNavigationView.setCheckedItem(R.id.home);
            return;
        }
        super.onBackPressed();
    }
}
