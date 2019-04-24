package br.agr.terras.corelibrary.infraestructure.resources.layout;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.agr.terras.corelibrary.R;
import br.agr.terras.materialdroid.FloatingActionButton;
import br.agr.terras.materialdroid.TabHostOld;
import br.agr.terras.materialdroid.childs.Tab;

/**
 * Created by leo on 01/08/16.
 */
public class TabActivity extends DrawerActivity implements Tab.TabListener, ViewPager.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener {
    protected List<Fragment> fragmentList;

    protected TabHostOld tabHostOld;
    protected ViewPager viewPager;
    protected PagerAdapter pagerAdapter;
    private int selectedPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void create(){
        super.create();
        createComponentes();
        clearTabs();
    }

    private void createComponentes(){
        tabHostOld = (TabHostOld) findViewById(R.id.tabhost);
        viewPager = (ViewPager) findViewById(R.id.pager);
        fab = (FloatingActionButton) findViewById(R.id.fabDrawerActivity);
        tabHostOld.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
    }

    protected void clearTabs(){
        tabHostOld.removeAllViews();
        fragmentList = new ArrayList<>();
        viewPager.setOnPageChangeListener(this);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
    }


    protected void addFragment(Fragment fragment, String title, boolean mainFragment) {
        fragmentList.add(fragment);
        viewPager.getAdapter().notifyDataSetChanged();
        Tab tab = tabHostOld.newTab().setText(title).setTabListener(this);
        tabHostOld.addTab(tab);
        if (mainFragment)
            viewPager.setCurrentItem(tab.getPosition());
    }

    public void selectTab(int position){
        viewPager.setCurrentItem(position);
    }

    public int getSelectedTabPosition(){
        return selectedPosition;
    }

    protected void addFragment(Fragment fragment, String title) {
        addFragment(fragment, title, false);
    }

    protected void setViewPagerBackground(int imageId){
        viewPager.setBackgroundResource(imageId);
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        @Override
        public void startUpdate(ViewGroup container) {
        }

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);

        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    @Override
    public void onTabSelected(Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        selectedPosition = tab.getPosition();
    }

    @Override
    public void onTabReselected(Tab tab) {

    }

    @Override
    public void onTabUnselected(Tab tab) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tabHostOld.setSelectedNavigationItem(position);
        selectedPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (tabHostOld.getTabs()!=null && !tabHostOld.getTabs().isEmpty())
            selectTab(tabHostOld.getCurrentTab().getPosition());
    }
}
