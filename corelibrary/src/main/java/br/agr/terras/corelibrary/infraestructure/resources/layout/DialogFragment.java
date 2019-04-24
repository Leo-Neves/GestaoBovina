package br.agr.terras.corelibrary.infraestructure.resources.layout;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.agr.terras.materialdroid.Dialog;

/**
 * Created by leo on 15/02/17.
 */

public abstract class DialogFragment extends Fragment {

    public void showAsDialog(Activity activity){
        View rootView = onView(activity.getLayoutInflater(), null, null);
        Dialog dialog = new Dialog.Builder(activity)
                .customView(rootView,false)
                .cancelable(false)
                .autoDismiss(false)
                .canceledOnTouchOutside(false)
                .build();
        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        return onView(inflater, container, savedInstanceState);
    }

    public View onView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return new View(inflater.getContext());
    }
}
