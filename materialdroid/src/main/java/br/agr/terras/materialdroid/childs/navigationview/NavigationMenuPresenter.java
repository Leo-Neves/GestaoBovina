package br.agr.terras.materialdroid.childs.navigationview;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.annotation.RestrictTo.Scope;
import android.support.design.R.dimen;
import android.support.design.R.layout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

@RestrictTo({Scope.LIBRARY_GROUP})
public class NavigationMenuPresenter implements MenuPresenter {
    private static final String STATE_HIERARCHY = "android:menu:list";
    private static final String STATE_ADAPTER = "android:menu:adapter";
    private static final String STATE_HEADER = "android:menu:header";
    private NavigationMenuView menuView;
    LinearLayout headerLayout;
    private Callback callback;
    MenuBuilder menu;
    private int id;
    NavigationMenuPresenter.NavigationMenuAdapter adapter;
    LayoutInflater layoutInflater;
    int textAppearance;
    boolean textAppearanceSet;
    ColorStateList textColor;
    ColorStateList iconTintList;
    Drawable itemBackground;
    int itemHorizontalPadding;
    int itemIconPadding;
    private int paddingTopDefault;
    int paddingSeparator;
    final OnClickListener onClickListener = new OnClickListener() {
        public void onClick(View v) {
            NavigationMenuItemView itemView = (NavigationMenuItemView)v;
            NavigationMenuPresenter.this.setUpdateSuspended(true);
            MenuItemImpl item = itemView.getItemData();
            boolean result = NavigationMenuPresenter.this.menu.performItemAction(item, NavigationMenuPresenter.this, 0);
            if (item != null && item.isCheckable() && result) {
                NavigationMenuPresenter.this.adapter.setCheckedItem(item);
            }

            NavigationMenuPresenter.this.setUpdateSuspended(false);
            NavigationMenuPresenter.this.updateMenuView(false);
        }
    };

    public NavigationMenuPresenter() {
    }

    public void initForMenu(Context context, MenuBuilder menu) {
        this.layoutInflater = LayoutInflater.from(context);
        this.menu = menu;
        Resources res = context.getResources();
        this.paddingSeparator = res.getDimensionPixelOffset(dimen.design_navigation_separator_vertical_padding);
    }

    public MenuView getMenuView(ViewGroup root) {
        if (this.menuView == null) {
            this.menuView = (NavigationMenuView)this.layoutInflater.inflate(layout.design_navigation_menu, root, false);
            if (this.adapter == null) {
                this.adapter = new NavigationMenuPresenter.NavigationMenuAdapter();
            }

            this.headerLayout = (LinearLayout)this.layoutInflater.inflate(layout.design_navigation_item_header, this.menuView, false);
            this.menuView.setAdapter(this.adapter);
        }

        return this.menuView;
    }

    public void updateMenuView(boolean cleared) {
        if (this.adapter != null) {
            this.adapter.update();
        }

    }

    public void setCallback(Callback cb) {
        this.callback = cb;
    }

    public boolean onSubMenuSelected(SubMenuBuilder subMenu) {
        return false;
    }

    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        if (this.callback != null) {
            this.callback.onCloseMenu(menu, allMenusAreClosing);
        }

    }

    public boolean flagActionItems() {
        return false;
    }

    public boolean expandItemActionView(MenuBuilder menu, MenuItemImpl item) {
        return false;
    }

    public boolean collapseItemActionView(MenuBuilder menu, MenuItemImpl item) {
        return false;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        SparseArray header;
        if (this.menuView != null) {
            header = new SparseArray();
            this.menuView.saveHierarchyState(header);
            state.putSparseParcelableArray("android:menu:list", header);
        }

        if (this.adapter != null) {
            state.putBundle("android:menu:adapter", this.adapter.createInstanceState());
        }

        if (this.headerLayout != null) {
            header = new SparseArray();
            this.headerLayout.saveHierarchyState(header);
            state.putSparseParcelableArray("android:menu:header", header);
        }

        return state;
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof Bundle) {
            Bundle state = (Bundle)parcelable;
            SparseArray<Parcelable> hierarchy = state.getSparseParcelableArray("android:menu:list");
            if (hierarchy != null) {
                this.menuView.restoreHierarchyState(hierarchy);
            }

            Bundle adapterState = state.getBundle("android:menu:adapter");
            if (adapterState != null) {
                this.adapter.restoreInstanceState(adapterState);
            }

            SparseArray<Parcelable> header = state.getSparseParcelableArray("android:menu:header");
            if (header != null) {
                this.headerLayout.restoreHierarchyState(header);
            }
        }

    }

    public void setCheckedItem(@NonNull MenuItemImpl item) {
        this.adapter.setCheckedItem(item);
    }

    @Nullable
    public MenuItemImpl getCheckedItem() {
        return this.adapter.getCheckedItem();
    }

    public View inflateHeaderView(@LayoutRes int res) {
        View view = this.layoutInflater.inflate(res, this.headerLayout, false);
        this.addHeaderView(view);
        return view;
    }

    public void addHeaderView(@NonNull View view) {
        this.headerLayout.addView(view);
        this.menuView.setPadding(0, 0, 0, this.menuView.getPaddingBottom());
    }

    public void removeHeaderView(@NonNull View view) {
        this.headerLayout.removeView(view);
        if (this.headerLayout.getChildCount() == 0) {
            this.menuView.setPadding(0, this.paddingTopDefault, 0, this.menuView.getPaddingBottom());
        }

    }

    public int getHeaderCount() {
        return this.headerLayout.getChildCount();
    }

    public View getHeaderView(int index) {
        return this.headerLayout.getChildAt(index);
    }

    @Nullable
    public ColorStateList getItemTintList() {
        return this.iconTintList;
    }

    public void setItemIconTintList(@Nullable ColorStateList tint) {
        this.iconTintList = tint;
        this.updateMenuView(false);
    }

    @Nullable
    public ColorStateList getItemTextColor() {
        return this.textColor;
    }

    public void setItemTextColor(@Nullable ColorStateList textColor) {
        this.textColor = textColor;
        this.updateMenuView(false);
    }


    public void setItemTextAppearance(@StyleRes int resId) {
        this.textAppearance = resId;
        this.textAppearanceSet = true;
        this.updateMenuView(false);
    }

    @Nullable
    public Drawable getItemBackground() {
        return this.itemBackground;
    }

    public void setItemBackground(@Nullable Drawable itemBackground) {
        this.itemBackground = itemBackground;
        this.updateMenuView(false);
    }

    public int getItemHorizontalPadding() {
        return this.itemHorizontalPadding;
    }

    public void setItemHorizontalPadding(int itemHorizontalPadding) {
        this.itemHorizontalPadding = itemHorizontalPadding;
        this.updateMenuView(false);
    }

    public int getItemIconPadding() {
        return this.itemIconPadding;
    }

    public void setItemIconPadding(int itemIconPadding) {
        this.itemIconPadding = itemIconPadding;
        this.updateMenuView(false);
    }

    public void setUpdateSuspended(boolean updateSuspended) {
        if (this.adapter != null) {
            this.adapter.setUpdateSuspended(updateSuspended);
        }

    }

    public void dispatchApplyWindowInsets(WindowInsetsCompat insets) {
        int top = insets.getSystemWindowInsetTop();
        if (this.paddingTopDefault != top) {
            this.paddingTopDefault = top;
            if (this.headerLayout.getChildCount() == 0) {
                this.menuView.setPadding(0, this.paddingTopDefault, 0, this.menuView.getPaddingBottom());
            }
        }

        ViewCompat.dispatchApplyWindowInsets(this.headerLayout, insets);
    }

    private static class NavigationMenuHeaderItem implements NavigationMenuPresenter.NavigationMenuItem {
        NavigationMenuHeaderItem() {
        }
    }

    private static class NavigationMenuSeparatorItem implements NavigationMenuPresenter.NavigationMenuItem {
        private final int paddingTop;
        private final int paddingBottom;

        public NavigationMenuSeparatorItem(int paddingTop, int paddingBottom) {
            this.paddingTop = paddingTop;
            this.paddingBottom = paddingBottom;
        }

        public int getPaddingTop() {
            return this.paddingTop;
        }

        public int getPaddingBottom() {
            return this.paddingBottom;
        }
    }

    private static class NavigationMenuTextItem implements NavigationMenuPresenter.NavigationMenuItem {
        private final MenuItemImpl menuItem;
        boolean needsEmptyIcon;

        NavigationMenuTextItem(MenuItemImpl item) {
            this.menuItem = item;
        }

        public MenuItemImpl getMenuItem() {
            return this.menuItem;
        }
    }

    private interface NavigationMenuItem {
    }

    private class NavigationMenuAdapter extends Adapter<NavigationMenuPresenter.ViewHolder> {
        private static final String STATE_CHECKED_ITEM = "android:menu:checked";
        private static final String STATE_ACTION_VIEWS = "android:menu:action_views";
        private static final int VIEW_TYPE_NORMAL = 0;
        private static final int VIEW_TYPE_SUBHEADER = 1;
        private static final int VIEW_TYPE_SEPARATOR = 2;
        private static final int VIEW_TYPE_HEADER = 3;
        private final ArrayList<NavigationMenuItem> items = new ArrayList();
        private MenuItemImpl checkedItem;
        private boolean updateSuspended;

        NavigationMenuAdapter() {
            this.prepareMenuItems();
        }

        public long getItemId(int position) {
            return (long)position;
        }

        public int getItemCount() {
            return this.items.size();
        }

        public int getItemViewType(int position) {
            NavigationMenuPresenter.NavigationMenuItem item = (NavigationMenuPresenter.NavigationMenuItem)this.items.get(position);
            if (item instanceof NavigationMenuPresenter.NavigationMenuSeparatorItem) {
                return 2;
            } else if (item instanceof NavigationMenuPresenter.NavigationMenuHeaderItem) {
                return 3;
            } else if (item instanceof NavigationMenuPresenter.NavigationMenuTextItem) {
                NavigationMenuPresenter.NavigationMenuTextItem textItem = (NavigationMenuPresenter.NavigationMenuTextItem)item;
                return textItem.getMenuItem().hasSubMenu() ? 1 : 0;
            } else {
                throw new RuntimeException("Unknown item type.");
            }
        }

        public NavigationMenuPresenter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch(viewType) {
                case 0:
                    return new NavigationMenuPresenter.NormalViewHolder(NavigationMenuPresenter.this.layoutInflater, parent, NavigationMenuPresenter.this.onClickListener);
                case 1:
                    return new NavigationMenuPresenter.SubheaderViewHolder(NavigationMenuPresenter.this.layoutInflater, parent);
                case 2:
                    return new NavigationMenuPresenter.SeparatorViewHolder(NavigationMenuPresenter.this.layoutInflater, parent);
                case 3:
                    return new NavigationMenuPresenter.HeaderViewHolder(NavigationMenuPresenter.this.headerLayout);
                default:
                    return null;
            }
        }

        public void onBindViewHolder(NavigationMenuPresenter.ViewHolder holder, int position) {
            NavigationMenuPresenter.NavigationMenuTextItem itemx;
            switch(this.getItemViewType(position)) {
                case 0:
                    NavigationMenuItemView itemView = (NavigationMenuItemView)holder.itemView;
                    itemView.setIconTintList(NavigationMenuPresenter.this.iconTintList);
                    if (NavigationMenuPresenter.this.textAppearanceSet) {
                        itemView.setTextAppearance(NavigationMenuPresenter.this.textAppearance);
                    }

                    if (NavigationMenuPresenter.this.textColor != null) {
                        itemView.setTextColor(NavigationMenuPresenter.this.textColor);
                    }

                    ViewCompat.setBackground(itemView, NavigationMenuPresenter.this.itemBackground != null ? NavigationMenuPresenter.this.itemBackground.getConstantState().newDrawable() : null);
                    itemx = (NavigationMenuPresenter.NavigationMenuTextItem)this.items.get(position);
                    itemView.setNeedsEmptyIcon(itemx.needsEmptyIcon);
                    itemView.setHorizontalPadding(NavigationMenuPresenter.this.itemHorizontalPadding);
                    itemView.setIconPadding(NavigationMenuPresenter.this.itemIconPadding);
                    itemView.initialize(itemx.getMenuItem(), 0);
                    break;
                case 1:
                    TextView subHeader = (TextView)holder.itemView;
                    itemx = (NavigationMenuPresenter.NavigationMenuTextItem)this.items.get(position);
                    subHeader.setText(itemx.getMenuItem().getTitle());
                    break;
                case 2:
                    NavigationMenuPresenter.NavigationMenuSeparatorItem item = (NavigationMenuPresenter.NavigationMenuSeparatorItem)this.items.get(position);
                    holder.itemView.setPadding(0, item.getPaddingTop(), 0, item.getPaddingBottom());
                case 3:
            }

        }

        public void onViewRecycled(NavigationMenuPresenter.ViewHolder holder) {
            if (holder instanceof NavigationMenuPresenter.NormalViewHolder) {
                ((NavigationMenuItemView)holder.itemView).recycle();
            }

        }

        public void update() {
            this.prepareMenuItems();
            this.notifyDataSetChanged();
        }

        private void prepareMenuItems() {
            if (!this.updateSuspended) {
                this.updateSuspended = true;
                this.items.clear();
                this.items.add(new NavigationMenuPresenter.NavigationMenuHeaderItem());
                int currentGroupId = -1;
                int currentGroupStart = 0;
                boolean currentGroupHasIcon = false;
                int i = 0;

                for(int totalSize = NavigationMenuPresenter.this.menu.getVisibleItems().size(); i < totalSize; ++i) {
                    MenuItemImpl item = (MenuItemImpl) NavigationMenuPresenter.this.menu.getVisibleItems().get(i);
                    if (item.isChecked()) {
                        this.setCheckedItem(item);
                    }

                    if (item.isCheckable()) {
                        item.setExclusiveCheckable(false);
                    }

                    if (!item.hasSubMenu()) {
                        int groupId = item.getGroupId();
                        if (groupId != currentGroupId) {
                            currentGroupStart = this.items.size();
                            currentGroupHasIcon = item.getIcon() != null;
                            if (i != 0) {
                                ++currentGroupStart;
                                this.items.add(new NavigationMenuPresenter.NavigationMenuSeparatorItem(NavigationMenuPresenter.this.paddingSeparator, NavigationMenuPresenter.this.paddingSeparator));
                            }
                        } else if (!currentGroupHasIcon && item.getIcon() != null) {
                            currentGroupHasIcon = true;
                            this.appendTransparentIconIfMissing(currentGroupStart, this.items.size());
                        }

                        NavigationMenuPresenter.NavigationMenuTextItem textItem = new NavigationMenuPresenter.NavigationMenuTextItem(item);
                        textItem.needsEmptyIcon = currentGroupHasIcon;
                        this.items.add(textItem);
                        currentGroupId = groupId;
                    } else {
                        SubMenu subMenu = item.getSubMenu();
                        if (subMenu.hasVisibleItems()) {
                            if (i != 0) {
                                this.items.add(new NavigationMenuPresenter.NavigationMenuSeparatorItem(NavigationMenuPresenter.this.paddingSeparator, 0));
                            }

                            this.items.add(new NavigationMenuPresenter.NavigationMenuTextItem(item));
                            boolean subMenuHasIcon = false;
                            int subMenuStart = this.items.size();
                            int j = 0;

                            for(int size = subMenu.size(); j < size; ++j) {
                                MenuItemImpl subMenuItem = (MenuItemImpl)subMenu.getItem(j);
                                if (subMenuItem.isVisible()) {
                                    if (!subMenuHasIcon && subMenuItem.getIcon() != null) {
                                        subMenuHasIcon = true;
                                    }

                                    if (subMenuItem.isCheckable()) {
                                        subMenuItem.setExclusiveCheckable(false);
                                    }

                                    if (item.isChecked()) {
                                        this.setCheckedItem(item);
                                    }

                                    this.items.add(new NavigationMenuPresenter.NavigationMenuTextItem(subMenuItem));
                                }
                            }

                            if (subMenuHasIcon) {
                                this.appendTransparentIconIfMissing(subMenuStart, this.items.size());
                            }
                        }
                    }
                }

                this.updateSuspended = false;
            }
        }

        private void appendTransparentIconIfMissing(int startIndex, int endIndex) {
            for(int i = startIndex; i < endIndex; ++i) {
                NavigationMenuPresenter.NavigationMenuTextItem textItem = (NavigationMenuPresenter.NavigationMenuTextItem)this.items.get(i);
                textItem.needsEmptyIcon = true;
            }

        }

        public void setCheckedItem(MenuItemImpl checkedItem) {
            if (this.checkedItem != checkedItem && checkedItem.isCheckable()) {
                if (this.checkedItem != null) {
                    this.checkedItem.setChecked(false);
                }

                this.checkedItem = checkedItem;
                checkedItem.setChecked(true);
            }
        }

        public MenuItemImpl getCheckedItem() {
            return this.checkedItem;
        }

        public Bundle createInstanceState() {
            Bundle state = new Bundle();
            if (this.checkedItem != null) {
                state.putInt("android:menu:checked", this.checkedItem.getItemId());
            }

            SparseArray<ParcelableSparseArray> actionViewStates = new SparseArray();
            int i = 0;

            for(int size = this.items.size(); i < size; ++i) {
                NavigationMenuPresenter.NavigationMenuItem navigationMenuItem = (NavigationMenuPresenter.NavigationMenuItem)this.items.get(i);
                if (navigationMenuItem instanceof NavigationMenuPresenter.NavigationMenuTextItem) {
                    MenuItemImpl item = ((NavigationMenuPresenter.NavigationMenuTextItem)navigationMenuItem).getMenuItem();
                    View actionView = item != null ? item.getActionView() : null;
                    if (actionView != null) {
                        ParcelableSparseArray container = new ParcelableSparseArray();
                        actionView.saveHierarchyState(container);
                        actionViewStates.put(item.getItemId(), container);
                    }
                }
            }

            state.putSparseParcelableArray("android:menu:action_views", actionViewStates);
            return state;
        }

        public void restoreInstanceState(Bundle state) {
            int checkedItem = state.getInt("android:menu:checked", 0);
            int ix;
            if (checkedItem != 0) {
                this.updateSuspended = true;
                int i = 0;

                for(ix = this.items.size(); i < ix; ++i) {
                    NavigationMenuPresenter.NavigationMenuItem item = (NavigationMenuPresenter.NavigationMenuItem)this.items.get(i);
                    if (item instanceof NavigationMenuPresenter.NavigationMenuTextItem) {
                        MenuItemImpl menuItem = ((NavigationMenuPresenter.NavigationMenuTextItem)item).getMenuItem();
                        if (menuItem != null && menuItem.getItemId() == checkedItem) {
                            this.setCheckedItem(menuItem);
                            break;
                        }
                    }
                }

                this.updateSuspended = false;
                this.prepareMenuItems();
            }

            SparseArray<ParcelableSparseArray> actionViewStates = state.getSparseParcelableArray("android:menu:action_views");
            if (actionViewStates != null) {
                ix = 0;

                for(int size = this.items.size(); ix < size; ++ix) {
                    NavigationMenuPresenter.NavigationMenuItem navigationMenuItem = (NavigationMenuPresenter.NavigationMenuItem)this.items.get(ix);
                    if (navigationMenuItem instanceof NavigationMenuPresenter.NavigationMenuTextItem) {
                        MenuItemImpl itemx = ((NavigationMenuPresenter.NavigationMenuTextItem)navigationMenuItem).getMenuItem();
                        if (itemx != null) {
                            View actionView = itemx.getActionView();
                            if (actionView != null) {
                                ParcelableSparseArray container = (ParcelableSparseArray)actionViewStates.get(itemx.getItemId());
                                if (container != null) {
                                    actionView.restoreHierarchyState(container);
                                }
                            }
                        }
                    }
                }
            }

        }

        public void setUpdateSuspended(boolean updateSuspended) {
            this.updateSuspended = updateSuspended;
        }
    }

    private static class HeaderViewHolder extends NavigationMenuPresenter.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class SeparatorViewHolder extends NavigationMenuPresenter.ViewHolder {
        public SeparatorViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(layout.design_navigation_item_separator, parent, false));
        }
    }

    private static class SubheaderViewHolder extends NavigationMenuPresenter.ViewHolder {
        public SubheaderViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(layout.design_navigation_item_subheader, parent, false));
        }
    }

    private static class NormalViewHolder extends NavigationMenuPresenter.ViewHolder {
        public NormalViewHolder(LayoutInflater inflater, ViewGroup parent, OnClickListener listener) {
            super(inflater.inflate(layout.design_navigation_item, parent, false));
            this.itemView.setOnClickListener(listener);
        }
    }

    private abstract static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
