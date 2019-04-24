package br.agr.terras.materialdroid.childs.navigationview;

import android.view.Menu;

public interface SupportMenu extends Menu {
    int USER_MASK = 65535;
    int USER_SHIFT = 0;
    int CATEGORY_MASK = -65536;
    int CATEGORY_SHIFT = 16;
    int SUPPORTED_MODIFIERS_MASK = 69647;
    int FLAG_KEEP_OPEN_ON_SUBMENU_OPENED = 4;

    void setGroupDividerEnabled(boolean var1);
}
