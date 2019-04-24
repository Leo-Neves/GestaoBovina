package br.agr.terras.corelibrary.infraestructure.resources.layout;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

import br.agr.terras.corelibrary.R;
import br.agr.terras.corelibrary.infraestructure.utils.ColorUtils;
import br.agr.terras.corelibrary.infraestructure.utils.ImageUtils;
import br.agr.terras.corelibrary.infraestructure.utils.MyDateUtils;
import br.agr.terras.corelibrary.infraestructure.utils.VersionUtils;
import br.agr.terras.materialdroid.ButtonRectangle;
import br.agr.terras.materialdroid.FloatingActionButton;
import br.agr.terras.materialdroid.SnackBar;
import br.agr.terras.materialdroid.parents.CoordinatorLayout;
import br.agr.terras.materialdroid.utils.ColorUtil;

/**
 * Created by leo on 23/06/16.
 */
public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected Toolbar toolbar;
    protected DrawerLayout drawer;
    private CoordinatorLayout coordinatorLayout;
    protected RelativeLayout mainLayout;
    private LinearLayout content;
    protected LinearLayout layoutInfo;
    private LinearLayout layoutLastSync;
    protected LinearLayout linearLayoutUser;
    protected FrameLayout frameLayout;
    private ButtonRectangle buttonSignUp;
    protected FloatingActionButton fab;
    protected ImageView imageViewUser;
    protected TextView textViewName;
    protected TextView textViewProfile;
    protected TextView textViewEmail;
    protected TextView textViewLastSynchronization;
    protected View viewSeparadorUser;
    protected NavigationView navigationView;
    private Fragment mainFragment;
    private Fragment fragment;
    private OnBackPressed onBackPressed;

    private SnackBar snackBar;
    private boolean fabVisibility;
    protected static int options_id = R.menu.menu_drawer_example;
    protected static int content_id = android.R.layout.simple_list_item_1;
    protected static int theme_id = android.R.style.Theme_Holo_Light_NoActionBar;
    protected static String[] permissoes = {};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissoes();
    }

    protected void create() {
        setContentView(R.layout.activity_drawer);
        setTheme(theme_id);
        createComponentes();
        configureDrawer();
        ColorUtils.setColorEdge(ColorUtil.getColorPrimary(this));
    }

    private void checkPermissoes() {
        boolean hasPermission = true;
        if (VersionUtils.getSdkVersion() >= 23) {
            for (String permissao : permissoes) {
                if (ContextCompat.checkSelfPermission(this, permissao) != PackageManager.PERMISSION_GRANTED) {
                    hasPermission = false;
                }
            }
            if (hasPermission)
                create();
            else
                ActivityCompat.requestPermissions(this, permissoes, 33);
        } else
            create();
    }

    private void createComponentes() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mainLayout = findViewById(R.id.mainLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        content = (LinearLayout) findViewById(R.id.content);
        frameLayout = (FrameLayout) findViewById(R.id.frame);
        View v = getLayoutInflater().inflate(content_id, null);
        content.addView(v);
        fab = (FloatingActionButton) findViewById(R.id.fabDrawerActivity);
        linearLayoutUser = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.linearLayoutUserBackground);
        imageViewUser = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageViewUser);
        textViewName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewHeaderNome);
        textViewEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewHeaderEmail);
        textViewProfile = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewHeaderCargo);
        textViewLastSynchronization = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewDataUltimaAtualizacao);
        viewSeparadorUser = navigationView.getHeaderView(0).findViewById(R.id.viewSeparadorUser);
        layoutInfo = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.linearLayoutContainerHeaderInfo);
        layoutLastSync = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.linearLayoutContainerHeaderLastSync);
        buttonSignUp = (ButtonRectangle) navigationView.getHeaderView(0).findViewById(R.id.buttonSignUp);
        setUserProfile(null);
        setEmail(null);
        setUserName(null);
    }

    private void configureDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.inflateMenu(options_id);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    protected void setLogo(int resourceId) {
        //ImageUtils.setImageViewResource((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageViewDrawerHeaderLogo), resourceId);
        ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageViewDrawerHeaderLogo)).setImageResource(resourceId);
    }

    protected void setBackground(int resourceId) {
        ImageUtils.setImageViewResource((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageViewBackgroundDrawerHeader), resourceId);
    }

    protected void setBackgroundColor(int resourceId) {
        navigationView.getHeaderView(0).findViewById(R.id.imageViewBackgroundDrawerHeader).setBackgroundColor(resourceId);
    }

    protected void setHeaderHeight(int height){
        navigationView.getHeaderView(0).findViewById(R.id.contentHeaderLogo).setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
    }

    protected void setUserLogo(int resId){
        imageViewUser.setImageResource(resId);
    }

    protected void setUserName(String userName) {
        textViewName.setText(userName == null ? "" : userName);
        textViewName.setVisibility((userName == null || userName.isEmpty()) ? View.GONE : View.VISIBLE);
    }

    protected void setUserProfile(String userProfile) {
        textViewProfile.setText(userProfile == null ? "" : userProfile);
        textViewProfile.setVisibility((userProfile == null || userProfile.isEmpty()) ? View.GONE : View.VISIBLE);
    }

    protected void setEmail(String email) {
        textViewEmail.setText(email == null ? "" : email);
        textViewEmail.setVisibility((email == null || email.isEmpty()) ? View.GONE : View.VISIBLE);
    }


    protected void setLastSynchronization(Date lastSynchronization) {
        textViewLastSynchronization.setText(MyDateUtils.convertDateToString(lastSynchronization) + " às " + MyDateUtils.convertDateToHourString(lastSynchronization));
    }

    protected void setButtonSignUpEnabled(boolean enabled) {
        buttonSignUp.setVisibility(enabled ? View.VISIBLE : View.GONE);
        layoutInfo.setVisibility(enabled ? View.GONE : View.VISIBLE);
        layoutLastSync.setVisibility(enabled ? View.INVISIBLE : View.VISIBLE);
    }

    public void openMainFragment(Fragment fragment){
        if (fragment==null || (this.mainFragment!=null &&  this.mainFragment.equals(fragment) && this.fragment==null))
            return;
        this.mainFragment = fragment;
        this.fragment = null;
        frameLayout.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(frameLayout.getId(), fragment).commitAllowingStateLoss();
    }

    public void setDrawerEnabled(boolean enabled){
        drawer.setDrawerLockMode(enabled?DrawerLayout.LOCK_MODE_UNLOCKED:DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        if (!enabled)
            drawer.closeDrawers();
    }

    public void openFragment(Fragment fragment) {
        if (this.fragment!=null && this.fragment.equals(fragment))
            return;
        this.fragment = fragment;
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        fabVisibility = !fab.isHidden();
        fab.hide(true);
        frameLayout.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(frameLayout.getId(), fragment).commitAllowingStateLoss();
    }

    public void openSnackBar(String mensagem, String color){
        openSnackBar(mensagem, color, null, null);
    }

    public void openSnackBar(String mensagem, String color, View.OnClickListener action, String textButton){
        if (snackBar!=null)
            snackBar.dismiss();
        snackBar = new SnackBar(coordinatorLayout, mensagem, color, action, textButton)
                .setDuration(3000);
        snackBar.show();
    }

    public CoordinatorLayout getCoordinatorLayout(){
        return coordinatorLayout;
    }

    public void setOnBackPressed(OnBackPressed onBackPressed){
        this.onBackPressed = onBackPressed;
    }

    @Override
    public void onBackPressed() {
        if (drawer!=null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (onBackPressed!=null){
                onBackPressed.onBackPressed();
            }else{
                if (frameLayout!=null && fragment != null) {
                    if (mainFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(frameLayout.getId(), mainFragment).commitAllowingStateLoss();
                    } else {
                        getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
                        frameLayout.setVisibility(View.GONE);
                        content.setVisibility(View.VISIBLE);
                    }
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    toolbar.setVisibility(View.VISIBLE);
                    fragment = null;
                    if (fabVisibility)
                        fab.show(true);
                } else
                    super.onBackPressed();
            }
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 33) {
            int totalResults = 0;
            for (int result : grantResults)
                if (result == PackageManager.PERMISSION_GRANTED)
                    totalResults++;
            if (totalResults == grantResults.length)
                create();
            else
                finish();
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}
