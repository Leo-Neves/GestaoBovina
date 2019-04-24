package br.agr.terras.corelibrary.infraestructure.resources.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.agr.terras.corelibrary.R;
import br.agr.terras.corelibrary.infraestructure.utils.DimensionUtils;
import br.agr.terras.corelibrary.infraestructure.utils.ImageUtils;
import br.agr.terras.materialdroid.ButtonFlat;

/**
 * Created by edson on 06/06/16.
 */
public class TourPage extends AppCompatActivity {

    /**
     * The number of pages (wizard steps) shown as default.
     */
    private static int NUM_PAGES;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and proxima wizard steps.
     */
    private NewSmartViewPager mPager;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    private ButtonFlat btnPular;
    private ButtonFlat btnPrivacidade;
    private ButtonFlat btnProximo;
    private TextView textPagina;
    private LinearLayout llPagina;
    private static List<View> imageViewList;

    private static Integer logo_id = R.drawable.logo_terras_500px;
    private static Integer background = R.color.branco;
    private static List<String> cabecalho = new ArrayList<>();
    private static List<Integer> printscreen_id = new ArrayList<>();

    public static final int NUMERIC = 33;
    public static final int INDICATOR = 66;

    private static OnFinishTourListener onFinishTourListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tour_page);

        iniciarPager();
        iniciarComponentes();
        iniciarIndicadoresPagina();

    }

    private void iniciarComponentes() {
        btnPular = (ButtonFlat) findViewById(R.id.btnPular);
        btnProximo = (ButtonFlat) findViewById(R.id.btnProximo);
        textPagina = (TextView) findViewById(R.id.textPagina);
        llPagina = (LinearLayout) findViewById(R.id.llPagina);

        btnPular.setOnClickListener(setPularTutorialOnClick());
        btnProximo.setOnClickListener(setProximoOnClick());
    }

    private void iniciarPager() {
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (NewSmartViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mPager.getCurrentItem() == NUM_PAGES - 1) {
                    btnProximo.setText("Avançar");
                    btnPular.setVisibility(View.INVISIBLE);
                } else {
                    btnProximo.setText("Próximo");
                    btnPular.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < NUM_PAGES; i++) {
                    imageViewList.get(i).setBackgroundResource(position == i ? R.drawable.circulo_preenchido : R.drawable.circulo_nao_preenchido);
                }
            }
        });
        mPager.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                Log.i("SmartViewPager", "Generic motion listsner " + event.getX());
                return true;
            }
        });
        ImageUtils.setBackground(mPager, background);
//        mPager.setPageTransformer(true,new FadeAnimationPageTransformer());
    }

    private void iniciarIndicadoresPagina() {
        imageViewList = new ArrayList<>();
        for (int i = 0; i < NUM_PAGES; i++) {
            View imageView = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DimensionUtils.convertDpToPx(13), DimensionUtils.convertDpToPx(13));
            params.setMargins(1, 1, 1, 1);
            imageView.setLayoutParams(params);
            imageView.setBackgroundResource(i == 0 ? R.drawable.circulo_preenchido : R.drawable.circulo_nao_preenchido);
            llPagina.addView(imageView);
            imageViewList.add(imageView);
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    public static void setOnFinishTourListener(OnFinishTourListener onFinishTourListener) {
        TourPage.onFinishTourListener = onFinishTourListener;
    }

    public static void setPages(int logo_id, int background, List<String> cabecalho, List<Integer> printscreen_id) {
        TourPage.logo_id = logo_id;
        TourPage.cabecalho = cabecalho == null ? new ArrayList<String>() : cabecalho;
        TourPage.printscreen_id = printscreen_id == null ? new ArrayList<Integer>() : printscreen_id;
        TourPage.background = background;
        NUM_PAGES = TourPage.cabecalho.size() < TourPage.printscreen_id.size() ? TourPage.cabecalho.size() : TourPage.printscreen_id.size();
    }

    public static void open(Context context) {
        Intent intent = new Intent(context, TourPage.class);
        context.startActivity(intent);
    }

    private void setPagAtual(int page, int mode) {
        switch (mode) {
            case NUMERIC:
                textPagina.setText(page + "/" + NUM_PAGES);
                textPagina.setTextSize(20);
                break;
            case INDICATOR:
                llPagina.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                for (int i = 0; i < NUM_PAGES; i++) {
                    imageViewList.add(i, new ImageView(this));
                    imageViewList.get(i).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
                for (int i = 0; i < NUM_PAGES; i++) {
                    if (i == mPager.getCurrentItem())
                        imageViewList.get(i).setBackgroundResource(R.drawable.circulo_preenchido);
                    else
                        imageViewList.get(i).setBackgroundResource(R.drawable.circulo_nao_preenchido);
                }
                break;
        }
    }

    private View.OnClickListener setProximoOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPager.getCurrentItem() == NUM_PAGES - 1) {
                    finish();
                    if (onFinishTourListener != null)
                        onFinishTourListener.onFinish();
                } else
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            }
        };
    }

    private View.OnClickListener setPularTutorialOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (onFinishTourListener != null)
                    onFinishTourListener.onFinish();
            }
        };
    }

    /**
     * A simple pager adapter that represents as many pages as it is necessary {@link ScreenSlidePageFragment} objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        @Override
        public void startUpdate(ViewGroup container) {
        }

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ScreenSlidePageFragment.create(position, cabecalho.get(position), logo_id, printscreen_id.get(position));
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public interface OnFinishTourListener {
        void onFinish();
    }

}
