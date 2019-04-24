package br.agr.terras.corelibrary.infraestructure.resources.info;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.agr.terras.corelibrary.R;
import br.agr.terras.corelibrary.infraestructure.utils.VersionUtils;

/**
 * Created by edson on 31/05/16.
 */
public class SobreActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FrameLayout frameLayout;
    private AboutPage aboutPage;

    private static int cor = R.color.TerrasBlue;
    private static int textColor = android.R.color.white;
    private static int logo = R.drawable.logo_terras_500px;
    private static int icone = 0;
    private static String description = " ";
    private static String website = "https://www.terras.agr.br/";
    private static String email = "atendimento@terras.agr.br";
    private static String facebook = "TerrasAppSolutions";
    private static String instagram = "terrasappsolutions";
    private static String twitter = "terrasApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);
        configurarToolbar();
        LinearLayout layout = (LinearLayout) findViewById(R.id.llSobre);
        frameLayout = (FrameLayout) findViewById(R.id.frame);
        Element adsElement = new Element();
        adsElement.setTitle(VersionUtils.getApplicationName());
        Element touElement = new Element();
        touElement.setTitle(getString(R.string.termos_uso));
        touElement.setOnClickListener(onTermosUsoClickListener);
        Element popElement = new Element();
        popElement.setTitle(getString(R.string.politica_privacidade));
        popElement.setOnClickListener(onPrivacidadeClickListener);
        aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(logo)
                .addItem(new Element().setTitle(getString(R.string.version) + " " + VersionUtils.getVersionName()))
                .addItem(adsElement)
                .addItem(touElement)
                .addItem(popElement)
                .setDescription(description)
                .addGroup(getString(R.string.about_contact_us));
        if (website != null)
            aboutPage.addWebsite(website);
        if (facebook != null)
            aboutPage.addFacebook(facebook);
        if (instagram != null)
            aboutPage.addInstagram(instagram);
        if (twitter != null)
            aboutPage.addTwitter(twitter);
        View view = aboutPage.create();
        layout.addView(view);
    }

    private void configurarToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(getResources().getColor(cor));
        toolbar.setTitleTextColor(getResources().getColor(textColor));
        if (icone != 0)
            toolbar.setLogo(icone);
        for (int i=0; i<toolbar.getChildCount(); i++){
            if (toolbar.getChildAt(i) instanceof TextView){
                TextView textView = (TextView) toolbar.getChildAt(i);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/square721.ttf");
                textView.setTypeface(typeface);
                textView.setAllCaps(true);
            }
        }
    }

    private View.OnClickListener onTermosUsoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TermoUsoPrivacidadeFragment termoUsoPrivacidadeFragment = new TermoUsoPrivacidadeFragment();
            termoUsoPrivacidadeFragment.setUrlAddressPoliticaPrivacidade(TermoUsoPrivacidadeFragment.URL_ADDRESS_TERMO_USO);
            termoUsoPrivacidadeFragment.showToolbar(false);
            TermoUsoPrivacidadeFragment.modoWebView = TermoUsoPrivacidadeFragment.ModoWebView.POLITICA_DE_PRIVACIDADE;
            openFragment(termoUsoPrivacidadeFragment);
        }
    };

    private View.OnClickListener onPrivacidadeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TermoUsoPrivacidadeFragment termoUsoPrivacidadeFragment = new TermoUsoPrivacidadeFragment();
            termoUsoPrivacidadeFragment.setUrlAddressTermoUso(TermoUsoPrivacidadeFragment.URL_ADDRESS_POLITICA_PRIVACIDADE);
            termoUsoPrivacidadeFragment.showToolbar(false);
            TermoUsoPrivacidadeFragment.modoWebView = TermoUsoPrivacidadeFragment.ModoWebView.TERMO_DE_USO;
            openFragment(termoUsoPrivacidadeFragment);
        }
    };

    private void openFragment(Fragment fragment) {
        frameLayout.setVisibility(View.VISIBLE);
        aboutPage.show(false);
        getSupportFragmentManager().beginTransaction().replace(frameLayout.getId(), fragment).commitAllowingStateLoss();
    }

    public static void setToolbarIcone(int iconeId) {
        SobreActivity.icone = iconeId;
    }

    public static void setToolbarColorId(int cor) {
        SobreActivity.cor = cor;
    }

    public static void setToolbarTextColorId(int cor) {
        SobreActivity.textColor = cor;
    }

    public static void setLogoId(int logoId) {
        logo = logoId;
    }

    public static void setDescription(String description) {
        SobreActivity.description = description;
    }

    public static void setEmail(String email) {
        SobreActivity.email = email;
    }

    public static void setWebsite(String website) {
        SobreActivity.website = website;
    }

    public static void setFacebook(String facebook) {
        SobreActivity.facebook = facebook;
    }

    public static void setInstagram(String instagram) {
        SobreActivity.instagram = instagram;
    }

    public static void setTwitter(String twitter) {
        SobreActivity.twitter = twitter;
    }

    public static void open(Context context) {
        Intent intent = new Intent(context, SobreActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onBackPressed() {
        aboutPage.show(true);
        finish();
    }
}
