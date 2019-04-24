package br.agr.terras.materialdroid.childs.navigationview;

import android.content.Context;
import android.os.Parcelable;
import android.view.ViewGroup;

public interface MenuPresenter {
    void initForMenu(Context var1, MenuBuilder var2);

    MenuView getMenuView(ViewGroup var1);

    void updateMenuView(boolean var1);

    void setCallback(MenuPresenter.Callback var1);

    boolean onSubMenuSelected(SubMenuBuilder var1);

    void onCloseMenu(MenuBuilder var1, boolean var2);

    boolean flagActionItems();

    boolean expandItemActionView(MenuBuilder var1, MenuItemImpl var2);

    boolean collapseItemActionView(MenuBuilder var1, MenuItemImpl var2);

    int getId();

    Parcelable onSaveInstanceState();

    void onRestoreInstanceState(Parcelable var1);

    public interface Callback {
        void onCloseMenu(MenuBuilder var1, boolean var2);

        boolean onOpenSubMenu(MenuBuilder var1);
    }
}
