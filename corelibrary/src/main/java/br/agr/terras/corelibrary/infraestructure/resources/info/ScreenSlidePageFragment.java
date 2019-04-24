package br.agr.terras.corelibrary.infraestructure.resources.info;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.agr.terras.corelibrary.R;
import br.agr.terras.corelibrary.infraestructure.utils.ImageUtils;


/**
 * Created by edson on 06/06/16.
 */
public class ScreenSlidePageFragment extends Fragment {

    public static final String ARG_PAGE = "page";

    private int mPageNumber;

    private String cabecalho;
    private Integer logo_id;
    private Integer printscreen_id;

    public static ScreenSlidePageFragment create(int pageNumber, String cabecalho, int logo_id, Integer printscreen_id) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        fragment.cabecalho = cabecalho;
        fragment.logo_id = logo_id;
        fragment.printscreen_id = printscreen_id;
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.tour_page_fragment, container, false);

//        // Set the title view to show the page number.
//        ((TextView) rootView.findViewById(android.R.id.text1)).setText(
//                getString(R.string.title_template_step, mPageNumber + 1));
        TextView textCabecalho;
        ImageView imageLogo;
        ImageView imagePrintscreen;

        textCabecalho = (TextView) rootView.findViewById(R.id.textMessageTour);
        imageLogo = (ImageView) rootView.findViewById(R.id.imageApp);
        imagePrintscreen = (ImageView) rootView.findViewById(R.id.imageScreen);
        imagePrintscreen.setScaleType(ImageView.ScaleType.FIT_XY);

        imagePrintscreen.setScaleType(ImageView.ScaleType.FIT_XY);

        if (cabecalho.equals("")) {
            textCabecalho.setVisibility(View.GONE);
            textCabecalho.setText("");
        } else {
            textCabecalho.setVisibility(View.VISIBLE);
            textCabecalho.setText(cabecalho);
        }

        if (logo_id == 0) {
            imageLogo.setVisibility(View.GONE);
            if (cabecalho.equals("")) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.weight = 0.0f;
                imagePrintscreen.setLayoutParams(params);
            }
        } else {
            imageLogo.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1.0f;
            imagePrintscreen.setLayoutParams(params);
        }

        ImageUtils.setImageViewResource(imageLogo, logo_id);
        ImageUtils.setImageViewResource(imagePrintscreen, printscreen_id);

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
