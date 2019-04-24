package br.agr.terras.corelibrary.infraestructure.resources.info;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import br.agr.terras.corelibrary.R;
import br.agr.terras.corelibrary.infraestructure.utils.InternetUtils;
import br.agr.terras.materialdroid.utils.ColorUtil;

/**
 * Created by edson on 16/09/16.
 */
public class TermoUsoPrivacidadeFragment extends Fragment implements View.OnClickListener {
    //TODO alterar url de Política de Privacidade
    public static final String URL_ADDRESS_POLITICA_PRIVACIDADE = "http://dev-buscar-banco.branch.terras.agr.br/pages/pp_terras_tool_credito_versao_produtor";
    public static final String URL_ADDRESS_TERMO_USO = "http://dev-buscar-banco.branch.terras.agr.br/pages/tou_terras_tool_credito_versao_produtor";
    private static final String TITLE_POLITICA_DE_PRIVACIDADE = "Política de Privacidade";
    private static final String TITLE_TERMO_DE_USO = "Termos de uso";
    public static ModoWebView modoWebView = ModoWebView.POLITICA_DE_PRIVACIDADE;
    private View rootView;
    private Toolbar toolbar;
    private WebView webView;

    private boolean mostrarToolbar = true;

    private String url_tou;
    private String url_pop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null) {
            createComponents(inflater, container);
            configureWebView();
        }
        return rootView;
    }

    private void createComponents(LayoutInflater inflater, ViewGroup container) {
        int colorPrimary = ColorUtil.getColorPrimary(getActivity());
        rootView = inflater.inflate(R.layout.fragment_termo_uso_privacidade, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbarTermoUsoPrivacidade);
        toolbar.setVisibility(mostrarToolbar ? View.VISIBLE : View.GONE);
        toolbar.setBackgroundColor(colorPrimary);
        webView = (WebView) rootView.findViewById(R.id.webViewTermoUsoPrivacidade);
        toolbar.setTitle(modoWebView.equals(ModoWebView.POLITICA_DE_PRIVACIDADE) ? TITLE_POLITICA_DE_PRIVACIDADE : TITLE_TERMO_DE_USO);
        toolbar.setNavigationOnClickListener(this);
    }

    private void configureWebView() {

        webView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        webView.getSettings().setAppCachePath(getContext().getCacheDir().getAbsolutePath());
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default

        if (!InternetUtils.isNetworkAvailable() || InternetUtils.getConnectivityStatus() == InternetUtils.TYPE_MOBILE) { // loading offline
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        webView.loadUrl(modoWebView.equals(ModoWebView.POLITICA_DE_PRIVACIDADE) ? url_pop : url_tou);
    }

    public void setUrlAddressPoliticaPrivacidade(String url_pop) {
        this.url_pop = url_pop;
    }

    public void setUrlAddressTermoUso(String url_tou) {
        this.url_tou = url_tou;
    }

    public void showToolbar(boolean show) {
        mostrarToolbar = show;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                getActivity().onBackPressed();
                break;
        }
    }

    public enum ModoWebView {
        POLITICA_DE_PRIVACIDADE,
        TERMO_DE_USO;
    }
}
