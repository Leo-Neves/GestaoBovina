package br.agr.terras.materialdroid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

/**
 * Created by leo on 07/01/17.
 */

public class WebTextView extends LinearLayout {
    protected final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";
    private Context context;
    private WebView webView;
    private String text = "";

    public WebTextView (Context context){
        super(context);
        init(context, null);
    }

    public WebTextView(Context context, AttributeSet attributSet) {
        super(context, attributSet);
        init(context, attributSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_web_text_view, this);
        if (attributeSet!=null){
            int id = attributeSet.getAttributeResourceValue(ANDROIDXML, "text", -1);
            String t = attributeSet.getAttributeValue(ANDROIDXML, "text");
            text = id==-1?t:context.getString(id);
        }
        webView = (WebView) view.findViewById(R.id.webTextView);
        setText(text);
    }

    public void setText(String text){
        this.text = text;
        webView.loadData(String.format(context.getString(R.string.textwebview_html), text), "text/html; charset=utf-8", "utf-8");
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(false);
    }

    public String getText(){
        return text;
    }
}
