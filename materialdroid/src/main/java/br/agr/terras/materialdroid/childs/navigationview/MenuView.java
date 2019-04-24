package br.agr.terras.materialdroid.childs.navigationview;

import android.graphics.drawable.Drawable;

public interface MenuView {
    void initialize(MenuBuilder var1);

    int getWindowAnimations();

    public interface ItemView {
        void initialize(MenuItemImpl var1, int var2);

        MenuItemImpl getItemData();

        void setTitle(CharSequence var1);

        void setEnabled(boolean var1);

        void setCheckable(boolean var1);

        void setChecked(boolean var1);

        void setShortcut(boolean var1, char var2);

        void setIcon(Drawable var1);

        boolean prefersCondensedTitle();

        boolean showsIcon();
    }
}
