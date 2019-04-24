package br.agr.terras.materialdroid.childs.navigationview;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyCharacterMap.KeyData;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MenuBuilder implements SupportMenu {
    private static final String TAG = "MenuBuilder";
    private static final String PRESENTER_KEY = "android:menu:presenters";
    private static final String ACTION_VIEW_STATES_KEY = "android:menu:actionviewstates";
    private static final String EXPANDED_ACTION_VIEW_ID = "android:menu:expandedactionview";
    private static final int[] sCategoryToOrder = new int[]{1, 4, 5, 3, 2, 0};
    private final Context mContext;
    private final Resources mResources;
    private boolean mQwertyMode;
    private boolean mShortcutsVisible;
    private MenuBuilder.Callback mCallback;
    private ArrayList<MenuItemImpl> mItems;
    private ArrayList<MenuItemImpl> mVisibleItems;
    private boolean mIsVisibleItemsStale;
    private ArrayList<MenuItemImpl> mActionItems;
    private ArrayList<MenuItemImpl> mNonActionItems;
    private boolean mIsActionItemsStale;
    private int mDefaultShowAsAction = 0;
    private ContextMenuInfo mCurrentMenuInfo;
    CharSequence mHeaderTitle;
    Drawable mHeaderIcon;
    View mHeaderView;
    private SparseArray<Parcelable> mFrozenViewStates;
    private boolean mPreventDispatchingItemsChanged = false;
    private boolean mItemsChangedWhileDispatchPrevented = false;
    private boolean mStructureChangedWhileDispatchPrevented = false;
    private boolean mOptionalIconsVisible = false;
    private boolean mIsClosing = false;
    private ArrayList<MenuItemImpl> mTempShortcutItemList = new ArrayList();
    private CopyOnWriteArrayList<WeakReference<MenuPresenter>> mPresenters = new CopyOnWriteArrayList();
    private MenuItemImpl mExpandedItem;
    private boolean mGroupDividerEnabled = false;
    private boolean mOverrideVisibleItems;

    public MenuBuilder(Context context) {
        this.mContext = context;
        this.mResources = context.getResources();
        this.mItems = new ArrayList();
        this.mVisibleItems = new ArrayList();
        this.mIsVisibleItemsStale = true;
        this.mActionItems = new ArrayList();
        this.mNonActionItems = new ArrayList();
        this.mIsActionItemsStale = true;
        this.setShortcutsVisibleInner(true);
    }

    public MenuBuilder setDefaultShowAsAction(int defaultShowAsAction) {
        this.mDefaultShowAsAction = defaultShowAsAction;
        return this;
    }

    public void addMenuPresenter(MenuPresenter presenter) {
        this.addMenuPresenter(presenter, this.mContext);
    }

    public void addMenuPresenter(MenuPresenter presenter, Context menuContext) {
        this.mPresenters.add(new WeakReference(presenter));
        presenter.initForMenu(menuContext, this);
        this.mIsActionItemsStale = true;
    }

    public void removeMenuPresenter(MenuPresenter presenter) {
        Iterator var2 = this.mPresenters.iterator();

        while(true) {
            WeakReference ref;
            MenuPresenter item;
            do {
                if (!var2.hasNext()) {
                    return;
                }

                ref = (WeakReference)var2.next();
                item = (MenuPresenter)ref.get();
            } while(item != null && item != presenter);

            this.mPresenters.remove(ref);
        }
    }

    private void dispatchPresenterUpdate(boolean cleared) {
        if (!this.mPresenters.isEmpty()) {
            this.stopDispatchingItemsChanged();
            Iterator var2 = this.mPresenters.iterator();

            while(var2.hasNext()) {
                WeakReference<MenuPresenter> ref = (WeakReference)var2.next();
                MenuPresenter presenter = (MenuPresenter)ref.get();
                if (presenter == null) {
                    this.mPresenters.remove(ref);
                } else {
                    presenter.updateMenuView(cleared);
                }
            }

            this.startDispatchingItemsChanged();
        }
    }

    private boolean dispatchSubMenuSelected(SubMenuBuilder subMenu, MenuPresenter preferredPresenter) {
        if (this.mPresenters.isEmpty()) {
            return false;
        } else {
            boolean result = false;
            if (preferredPresenter != null) {
                result = preferredPresenter.onSubMenuSelected(subMenu);
            }

            Iterator var4 = this.mPresenters.iterator();

            while(var4.hasNext()) {
                WeakReference<MenuPresenter> ref = (WeakReference)var4.next();
                MenuPresenter presenter = (MenuPresenter)ref.get();
                if (presenter == null) {
                    this.mPresenters.remove(ref);
                } else if (!result) {
                    result = presenter.onSubMenuSelected(subMenu);
                }
            }

            return result;
        }
    }

    private void dispatchSaveInstanceState(Bundle outState) {
        if (!this.mPresenters.isEmpty()) {
            SparseArray<Parcelable> presenterStates = new SparseArray();
            Iterator var3 = this.mPresenters.iterator();

            while(var3.hasNext()) {
                WeakReference<MenuPresenter> ref = (WeakReference)var3.next();
                MenuPresenter presenter = (MenuPresenter)ref.get();
                if (presenter == null) {
                    this.mPresenters.remove(ref);
                } else {
                    int id = presenter.getId();
                    if (id > 0) {
                        Parcelable state = presenter.onSaveInstanceState();
                        if (state != null) {
                            presenterStates.put(id, state);
                        }
                    }
                }
            }

            outState.putSparseParcelableArray("android:menu:presenters", presenterStates);
        }
    }

    private void dispatchRestoreInstanceState(Bundle state) {
        SparseArray<Parcelable> presenterStates = state.getSparseParcelableArray("android:menu:presenters");
        if (presenterStates != null && !this.mPresenters.isEmpty()) {
            Iterator var3 = this.mPresenters.iterator();

            while(var3.hasNext()) {
                WeakReference<MenuPresenter> ref = (WeakReference)var3.next();
                MenuPresenter presenter = (MenuPresenter)ref.get();
                if (presenter == null) {
                    this.mPresenters.remove(ref);
                } else {
                    int id = presenter.getId();
                    if (id > 0) {
                        Parcelable parcel = (Parcelable)presenterStates.get(id);
                        if (parcel != null) {
                            presenter.onRestoreInstanceState(parcel);
                        }
                    }
                }
            }

        }
    }

    public void savePresenterStates(Bundle outState) {
        this.dispatchSaveInstanceState(outState);
    }

    public void restorePresenterStates(Bundle state) {
        this.dispatchRestoreInstanceState(state);
    }

    public void saveActionViewStates(Bundle outStates) {
        SparseArray<Parcelable> viewStates = null;
        int itemCount = this.size();

        for(int i = 0; i < itemCount; ++i) {
            MenuItem item = this.getItem(i);
            View v = item.getActionView();
            if (v != null && v.getId() != -1) {
                if (viewStates == null) {
                    viewStates = new SparseArray();
                }

                v.saveHierarchyState(viewStates);
                if (item.isActionViewExpanded()) {
                    outStates.putInt("android:menu:expandedactionview", item.getItemId());
                }
            }

            if (item.hasSubMenu()) {
                SubMenuBuilder subMenu = (SubMenuBuilder)item.getSubMenu();
                subMenu.saveActionViewStates(outStates);
            }
        }

        if (viewStates != null) {
            outStates.putSparseParcelableArray(this.getActionViewStatesKey(), viewStates);
        }

    }

    public void restoreActionViewStates(Bundle states) {
        if (states != null) {
            SparseArray<Parcelable> viewStates = states.getSparseParcelableArray(this.getActionViewStatesKey());
            int itemCount = this.size();

            int expandedId;
            MenuItem itemToExpand;
            for(expandedId = 0; expandedId < itemCount; ++expandedId) {
                itemToExpand = this.getItem(expandedId);
                View v = itemToExpand.getActionView();
                if (v != null && v.getId() != -1) {
                    v.restoreHierarchyState(viewStates);
                }

                if (itemToExpand.hasSubMenu()) {
                    SubMenuBuilder subMenu = (SubMenuBuilder)itemToExpand.getSubMenu();
                    subMenu.restoreActionViewStates(states);
                }
            }

            expandedId = states.getInt("android:menu:expandedactionview");
            if (expandedId > 0) {
                itemToExpand = this.findItem(expandedId);
                if (itemToExpand != null) {
                    itemToExpand.expandActionView();
                }
            }

        }
    }

    protected String getActionViewStatesKey() {
        return "android:menu:actionviewstates";
    }

    public void setCallback(MenuBuilder.Callback cb) {
        this.mCallback = cb;
    }

    protected MenuItem addInternal(int group, int id, int categoryOrder, CharSequence title) {
        int ordering = getOrdering(categoryOrder);
        MenuItemImpl item = this.createNewMenuItem(group, id, categoryOrder, ordering, title, this.mDefaultShowAsAction);
        if (this.mCurrentMenuInfo != null) {
            item.setMenuInfo(this.mCurrentMenuInfo);
        }

        this.mItems.add(findInsertIndex(this.mItems, ordering), item);
        this.onItemsChanged(true);
        return item;
    }

    private MenuItemImpl createNewMenuItem(int group, int id, int categoryOrder, int ordering, CharSequence title, int defaultShowAsAction) {
        return new MenuItemImpl(this, group, id, categoryOrder, ordering, title, defaultShowAsAction);
    }

    public MenuItem add(CharSequence title) {
        return this.addInternal(0, 0, 0, title);
    }

    public MenuItem add(int titleRes) {
        return this.addInternal(0, 0, 0, this.mResources.getString(titleRes));
    }

    public MenuItem add(int group, int id, int categoryOrder, CharSequence title) {
        return this.addInternal(group, id, categoryOrder, title);
    }

    public MenuItem add(int group, int id, int categoryOrder, int title) {
        return this.addInternal(group, id, categoryOrder, this.mResources.getString(title));
    }

    public SubMenu addSubMenu(CharSequence title) {
        return this.addSubMenu(0, 0, 0, title);
    }

    public SubMenu addSubMenu(int titleRes) {
        return this.addSubMenu(0, 0, 0, this.mResources.getString(titleRes));
    }

    public SubMenu addSubMenu(int group, int id, int categoryOrder, CharSequence title) {
        MenuItemImpl item = (MenuItemImpl)this.addInternal(group, id, categoryOrder, title);
        SubMenuBuilder subMenu = new SubMenuBuilder(this.mContext, this, item);
        item.setSubMenu(subMenu);
        return subMenu;
    }

    public SubMenu addSubMenu(int group, int id, int categoryOrder, int title) {
        return this.addSubMenu(group, id, categoryOrder, this.mResources.getString(title));
    }

    public void setGroupDividerEnabled(boolean enabled) {
        this.mGroupDividerEnabled = enabled;
    }

    public boolean isGroupDividerEnabled() {
        return this.mGroupDividerEnabled;
    }

    public int addIntentOptions(int group, int id, int categoryOrder, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {
        PackageManager pm = this.mContext.getPackageManager();
        List<ResolveInfo> lri = pm.queryIntentActivityOptions(caller, specifics, intent, 0);
        int N = lri != null ? lri.size() : 0;
        if ((flags & 1) == 0) {
            this.removeGroup(group);
        }

        for(int i = 0; i < N; ++i) {
            ResolveInfo ri = (ResolveInfo)lri.get(i);
            Intent rintent = new Intent(ri.specificIndex < 0 ? intent : specifics[ri.specificIndex]);
            rintent.setComponent(new ComponentName(ri.activityInfo.applicationInfo.packageName, ri.activityInfo.name));
            MenuItem item = this.add(group, id, categoryOrder, ri.loadLabel(pm)).setIcon(ri.loadIcon(pm)).setIntent(rintent);
            if (outSpecificItems != null && ri.specificIndex >= 0) {
                outSpecificItems[ri.specificIndex] = item;
            }
        }

        return N;
    }

    public void removeItem(int id) {
        this.removeItemAtInt(this.findItemIndex(id), true);
    }

    public void removeGroup(int group) {
        int i = this.findGroupIndex(group);
        if (i >= 0) {
            int maxRemovable = this.mItems.size() - i;
            int var4 = 0;

            while(var4++ < maxRemovable && ((MenuItemImpl)this.mItems.get(i)).getGroupId() == group) {
                this.removeItemAtInt(i, false);
            }

            this.onItemsChanged(true);
        }

    }

    private void removeItemAtInt(int index, boolean updateChildrenOnMenuViews) {
        if (index >= 0 && index < this.mItems.size()) {
            this.mItems.remove(index);
            if (updateChildrenOnMenuViews) {
                this.onItemsChanged(true);
            }

        }
    }

    public void removeItemAt(int index) {
        this.removeItemAtInt(index, true);
    }

    public void clearAll() {
        this.mPreventDispatchingItemsChanged = true;
        this.clear();
        this.clearHeader();
        this.mPresenters.clear();
        this.mPreventDispatchingItemsChanged = false;
        this.mItemsChangedWhileDispatchPrevented = false;
        this.mStructureChangedWhileDispatchPrevented = false;
        this.onItemsChanged(true);
    }

    public void clear() {
        if (this.mExpandedItem != null) {
            this.collapseItemActionView(this.mExpandedItem);
        }

        this.mItems.clear();
        this.onItemsChanged(true);
    }

    void setExclusiveItemChecked(MenuItem item) {
        int group = item.getGroupId();
        int N = this.mItems.size();
        this.stopDispatchingItemsChanged();

        for(int i = 0; i < N; ++i) {
            MenuItemImpl curItem = (MenuItemImpl)this.mItems.get(i);
            if (curItem.getGroupId() == group && curItem.isExclusiveCheckable() && curItem.isCheckable()) {
                curItem.setCheckedInt(curItem == item);
            }
        }

        this.startDispatchingItemsChanged();
    }

    public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {
        int N = this.mItems.size();

        for(int i = 0; i < N; ++i) {
            MenuItemImpl item = (MenuItemImpl)this.mItems.get(i);
            if (item.getGroupId() == group) {
                item.setExclusiveCheckable(exclusive);
                item.setCheckable(checkable);
            }
        }

    }

    public void setGroupVisible(int group, boolean visible) {
        int N = this.mItems.size();
        boolean changedAtLeastOneItem = false;

        for(int i = 0; i < N; ++i) {
            MenuItemImpl item = (MenuItemImpl)this.mItems.get(i);
            if (item.getGroupId() == group && item.setVisibleInt(visible)) {
                changedAtLeastOneItem = true;
            }
        }

        if (changedAtLeastOneItem) {
            this.onItemsChanged(true);
        }

    }

    public void setGroupEnabled(int group, boolean enabled) {
        int N = this.mItems.size();

        for(int i = 0; i < N; ++i) {
            MenuItemImpl item = (MenuItemImpl)this.mItems.get(i);
            if (item.getGroupId() == group) {
                item.setEnabled(enabled);
            }
        }

    }

    public boolean hasVisibleItems() {
        if (this.mOverrideVisibleItems) {
            return true;
        } else {
            int size = this.size();

            for(int i = 0; i < size; ++i) {
                MenuItemImpl item = (MenuItemImpl)this.mItems.get(i);
                if (item.isVisible()) {
                    return true;
                }
            }

            return false;
        }
    }

    public MenuItem findItem(int id) {
        int size = this.size();

        for(int i = 0; i < size; ++i) {
            MenuItemImpl item = (MenuItemImpl)this.mItems.get(i);
            if (item.getItemId() == id) {
                return item;
            }

            if (item.hasSubMenu()) {
                MenuItem possibleItem = item.getSubMenu().findItem(id);
                if (possibleItem != null) {
                    return possibleItem;
                }
            }
        }

        return null;
    }

    public int findItemIndex(int id) {
        int size = this.size();

        for(int i = 0; i < size; ++i) {
            MenuItemImpl item = (MenuItemImpl)this.mItems.get(i);
            if (item.getItemId() == id) {
                return i;
            }
        }

        return -1;
    }

    public int findGroupIndex(int group) {
        return this.findGroupIndex(group, 0);
    }

    public int findGroupIndex(int group, int start) {
        int size = this.size();
        if (start < 0) {
            start = 0;
        }

        for(int i = start; i < size; ++i) {
            MenuItemImpl item = (MenuItemImpl)this.mItems.get(i);
            if (item.getGroupId() == group) {
                return i;
            }
        }

        return -1;
    }

    public int size() {
        return this.mItems.size();
    }

    public MenuItem getItem(int index) {
        return (MenuItem)this.mItems.get(index);
    }

    public boolean isShortcutKey(int keyCode, KeyEvent event) {
        return this.findItemWithShortcutForKey(keyCode, event) != null;
    }

    public void setQwertyMode(boolean isQwerty) {
        this.mQwertyMode = isQwerty;
        this.onItemsChanged(false);
    }

    private static int getOrdering(int categoryOrder) {
        int index = (categoryOrder & -65536) >> 16;
        if (index >= 0 && index < sCategoryToOrder.length) {
            return sCategoryToOrder[index] << 16 | categoryOrder & '\uffff';
        } else {
            throw new IllegalArgumentException("order does not contain a valid category.");
        }
    }

    boolean isQwertyMode() {
        return this.mQwertyMode;
    }

    public void setShortcutsVisible(boolean shortcutsVisible) {
        if (this.mShortcutsVisible != shortcutsVisible) {
            this.setShortcutsVisibleInner(shortcutsVisible);
            this.onItemsChanged(false);
        }
    }

    private void setShortcutsVisibleInner(boolean shortcutsVisible) {
        this.mShortcutsVisible = shortcutsVisible && this.mResources.getConfiguration().keyboard != 1 && ViewConfigurationCompat.shouldShowMenuShortcutsWhenKeyboardPresent(ViewConfiguration.get(this.mContext), this.mContext);
    }

    public boolean isShortcutsVisible() {
        return this.mShortcutsVisible;
    }

    Resources getResources() {
        return this.mResources;
    }

    public Context getContext() {
        return this.mContext;
    }

    boolean dispatchMenuItemSelected(MenuBuilder menu, MenuItem item) {
        return this.mCallback != null && this.mCallback.onMenuItemSelected(menu, item);
    }

    public void changeMenuMode() {
        if (this.mCallback != null) {
            this.mCallback.onMenuModeChange(this);
        }

    }

    private static int findInsertIndex(ArrayList<MenuItemImpl> items, int ordering) {
        for(int i = items.size() - 1; i >= 0; --i) {
            MenuItemImpl item = (MenuItemImpl)items.get(i);
            if (item.getOrdering() <= ordering) {
                return i + 1;
            }
        }

        return 0;
    }

    public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
        MenuItemImpl item = this.findItemWithShortcutForKey(keyCode, event);
        boolean handled = false;
        if (item != null) {
            handled = this.performItemAction(item, flags);
        }

        if ((flags & 2) != 0) {
            this.close(true);
        }

        return handled;
    }

    void findItemsWithShortcutForKey(List<MenuItemImpl> items, int keyCode, KeyEvent event) {
        boolean qwerty = this.isQwertyMode();
        int modifierState = event.getModifiers();
        KeyData possibleChars = new KeyData();
        boolean isKeyCodeMapped = event.getKeyData(possibleChars);
        if (isKeyCodeMapped || keyCode == 67) {
            int N = this.mItems.size();

            for(int i = 0; i < N; ++i) {
                MenuItemImpl item = (MenuItemImpl)this.mItems.get(i);
                if (item.hasSubMenu()) {
                    ((MenuBuilder)item.getSubMenu()).findItemsWithShortcutForKey(items, keyCode, event);
                }

                char shortcutChar = qwerty ? item.getAlphabeticShortcut() : item.getNumericShortcut();
                int shortcutModifiers = qwerty ? item.getAlphabeticModifiers() : item.getNumericModifiers();
                boolean isModifiersExactMatch = (modifierState & 69647) == (shortcutModifiers & 69647);
                if (isModifiersExactMatch && shortcutChar != 0 && (shortcutChar == possibleChars.meta[0] || shortcutChar == possibleChars.meta[2] || qwerty && shortcutChar == '\b' && keyCode == 67) && item.isEnabled()) {
                    items.add(item);
                }
            }

        }
    }

    MenuItemImpl findItemWithShortcutForKey(int keyCode, KeyEvent event) {
        ArrayList<MenuItemImpl> items = this.mTempShortcutItemList;
        items.clear();
        this.findItemsWithShortcutForKey(items, keyCode, event);
        if (items.isEmpty()) {
            return null;
        } else {
            int metaState = event.getMetaState();
            KeyData possibleChars = new KeyData();
            event.getKeyData(possibleChars);
            int size = items.size();
            if (size == 1) {
                return (MenuItemImpl)items.get(0);
            } else {
                boolean qwerty = this.isQwertyMode();

                for(int i = 0; i < size; ++i) {
                    MenuItemImpl item = (MenuItemImpl)items.get(i);
                    char shortcutChar = qwerty ? item.getAlphabeticShortcut() : item.getNumericShortcut();
                    if (shortcutChar == possibleChars.meta[0] && (metaState & 2) == 0 || shortcutChar == possibleChars.meta[2] && (metaState & 2) != 0 || qwerty && shortcutChar == '\b' && keyCode == 67) {
                        return item;
                    }
                }

                return null;
            }
        }
    }

    public boolean performIdentifierAction(int id, int flags) {
        return this.performItemAction(this.findItem(id), flags);
    }

    public boolean performItemAction(MenuItem item, int flags) {
        return this.performItemAction(item, (MenuPresenter)null, flags);
    }

    public boolean performItemAction(MenuItem item, MenuPresenter preferredPresenter, int flags) {
        MenuItemImpl itemImpl = (MenuItemImpl)item;
        if (itemImpl != null && itemImpl.isEnabled()) {
            boolean invoked = itemImpl.invoke();
            ActionProvider provider = itemImpl.getSupportActionProvider();
            boolean providerHasSubMenu = provider != null && provider.hasSubMenu();
            if (itemImpl.hasCollapsibleActionView()) {
                invoked |= itemImpl.expandActionView();
                if (invoked) {
                    this.close(true);
                }
            } else if (!itemImpl.hasSubMenu() && !providerHasSubMenu) {
                if ((flags & 1) == 0) {
                    this.close(true);
                }
            } else {
                if ((flags & 4) == 0) {
                    this.close(false);
                }

                if (!itemImpl.hasSubMenu()) {
                    itemImpl.setSubMenu(new SubMenuBuilder(this.getContext(), this, itemImpl));
                }

                SubMenuBuilder subMenu = (SubMenuBuilder)itemImpl.getSubMenu();
                if (providerHasSubMenu) {
                    provider.onPrepareSubMenu(subMenu);
                }

                invoked |= this.dispatchSubMenuSelected(subMenu, preferredPresenter);
                if (!invoked) {
                    this.close(true);
                }
            }

            return invoked;
        } else {
            return false;
        }
    }

    public final void close(boolean closeAllMenus) {
        if (!this.mIsClosing) {
            this.mIsClosing = true;
            Iterator var2 = this.mPresenters.iterator();

            while(var2.hasNext()) {
                WeakReference<MenuPresenter> ref = (WeakReference)var2.next();
                MenuPresenter presenter = (MenuPresenter)ref.get();
                if (presenter == null) {
                    this.mPresenters.remove(ref);
                } else {
                    presenter.onCloseMenu(this, closeAllMenus);
                }
            }

            this.mIsClosing = false;
        }
    }

    public void close() {
        this.close(true);
    }

    public void onItemsChanged(boolean structureChanged) {
        if (!this.mPreventDispatchingItemsChanged) {
            if (structureChanged) {
                this.mIsVisibleItemsStale = true;
                this.mIsActionItemsStale = true;
            }

            this.dispatchPresenterUpdate(structureChanged);
        } else {
            this.mItemsChangedWhileDispatchPrevented = true;
            if (structureChanged) {
                this.mStructureChangedWhileDispatchPrevented = true;
            }
        }

    }

    public void stopDispatchingItemsChanged() {
        if (!this.mPreventDispatchingItemsChanged) {
            this.mPreventDispatchingItemsChanged = true;
            this.mItemsChangedWhileDispatchPrevented = false;
            this.mStructureChangedWhileDispatchPrevented = false;
        }

    }

    public void startDispatchingItemsChanged() {
        this.mPreventDispatchingItemsChanged = false;
        if (this.mItemsChangedWhileDispatchPrevented) {
            this.mItemsChangedWhileDispatchPrevented = false;
            this.onItemsChanged(this.mStructureChangedWhileDispatchPrevented);
        }

    }

    void onItemVisibleChanged(MenuItemImpl item) {
        this.mIsVisibleItemsStale = true;
        this.onItemsChanged(true);
    }

    void onItemActionRequestChanged(MenuItemImpl item) {
        this.mIsActionItemsStale = true;
        this.onItemsChanged(true);
    }

    @NonNull
    public ArrayList<MenuItemImpl> getVisibleItems() {
        if (!this.mIsVisibleItemsStale) {
            return this.mVisibleItems;
        } else {
            this.mVisibleItems.clear();
            int itemsSize = this.mItems.size();

            for(int i = 0; i < itemsSize; ++i) {
                MenuItemImpl item = (MenuItemImpl)this.mItems.get(i);
                if (item.isVisible()) {
                    this.mVisibleItems.add(item);
                }
            }

            this.mIsVisibleItemsStale = false;
            this.mIsActionItemsStale = true;
            return this.mVisibleItems;
        }
    }

    public void flagActionItems() {
        ArrayList<MenuItemImpl> visibleItems = this.getVisibleItems();
        if (this.mIsActionItemsStale) {
            boolean flagged = false;
            Iterator var3 = this.mPresenters.iterator();

            while(var3.hasNext()) {
                WeakReference<MenuPresenter> ref = (WeakReference)var3.next();
                MenuPresenter presenter = (MenuPresenter)ref.get();
                if (presenter == null) {
                    this.mPresenters.remove(ref);
                } else {
                    flagged |= presenter.flagActionItems();
                }
            }

            if (flagged) {
                this.mActionItems.clear();
                this.mNonActionItems.clear();
                int itemsSize = visibleItems.size();

                for(int i = 0; i < itemsSize; ++i) {
                    MenuItemImpl item = (MenuItemImpl)visibleItems.get(i);
                    if (item.isActionButton()) {
                        this.mActionItems.add(item);
                    } else {
                        this.mNonActionItems.add(item);
                    }
                }
            } else {
                this.mActionItems.clear();
                this.mNonActionItems.clear();
                this.mNonActionItems.addAll(this.getVisibleItems());
            }

            this.mIsActionItemsStale = false;
        }
    }

    public ArrayList<MenuItemImpl> getActionItems() {
        this.flagActionItems();
        return this.mActionItems;
    }

    public ArrayList<MenuItemImpl> getNonActionItems() {
        this.flagActionItems();
        return this.mNonActionItems;
    }

    public void clearHeader() {
        this.mHeaderIcon = null;
        this.mHeaderTitle = null;
        this.mHeaderView = null;
        this.onItemsChanged(false);
    }

    private void setHeaderInternal(int titleRes, CharSequence title, int iconRes, Drawable icon, View view) {
        Resources r = this.getResources();
        if (view != null) {
            this.mHeaderView = view;
            this.mHeaderTitle = null;
            this.mHeaderIcon = null;
        } else {
            if (titleRes > 0) {
                this.mHeaderTitle = r.getText(titleRes);
            } else if (title != null) {
                this.mHeaderTitle = title;
            }

            if (iconRes > 0) {
                this.mHeaderIcon = ContextCompat.getDrawable(this.getContext(), iconRes);
            } else if (icon != null) {
                this.mHeaderIcon = icon;
            }

            this.mHeaderView = null;
        }

        this.onItemsChanged(false);
    }

    protected MenuBuilder setHeaderTitleInt(CharSequence title) {
        this.setHeaderInternal(0, title, 0, (Drawable)null, (View)null);
        return this;
    }

    protected MenuBuilder setHeaderTitleInt(int titleRes) {
        this.setHeaderInternal(titleRes, (CharSequence)null, 0, (Drawable)null, (View)null);
        return this;
    }

    protected MenuBuilder setHeaderIconInt(Drawable icon) {
        this.setHeaderInternal(0, (CharSequence)null, 0, icon, (View)null);
        return this;
    }

    protected MenuBuilder setHeaderIconInt(int iconRes) {
        this.setHeaderInternal(0, (CharSequence)null, iconRes, (Drawable)null, (View)null);
        return this;
    }

    protected MenuBuilder setHeaderViewInt(View view) {
        this.setHeaderInternal(0, (CharSequence)null, 0, (Drawable)null, view);
        return this;
    }

    public CharSequence getHeaderTitle() {
        return this.mHeaderTitle;
    }

    public Drawable getHeaderIcon() {
        return this.mHeaderIcon;
    }

    public View getHeaderView() {
        return this.mHeaderView;
    }

    public MenuBuilder getRootMenu() {
        return this;
    }

    public void setCurrentMenuInfo(ContextMenuInfo menuInfo) {
        this.mCurrentMenuInfo = menuInfo;
    }

    public void setOptionalIconsVisible(boolean visible) {
        this.mOptionalIconsVisible = visible;
    }

    boolean getOptionalIconsVisible() {
        return this.mOptionalIconsVisible;
    }

    public boolean expandItemActionView(MenuItemImpl item) {
        if (this.mPresenters.isEmpty()) {
            return false;
        } else {
            boolean expanded = false;
            this.stopDispatchingItemsChanged();
            Iterator var3 = this.mPresenters.iterator();

            while(var3.hasNext()) {
                WeakReference<MenuPresenter> ref = (WeakReference)var3.next();
                MenuPresenter presenter = (MenuPresenter)ref.get();
                if (presenter == null) {
                    this.mPresenters.remove(ref);
                } else if (expanded = presenter.expandItemActionView(this, item)) {
                    break;
                }
            }

            this.startDispatchingItemsChanged();
            if (expanded) {
                this.mExpandedItem = item;
            }

            return expanded;
        }
    }

    public boolean collapseItemActionView(MenuItemImpl item) {
        if (!this.mPresenters.isEmpty() && this.mExpandedItem == item) {
            boolean collapsed = false;
            this.stopDispatchingItemsChanged();
            Iterator var3 = this.mPresenters.iterator();

            while(var3.hasNext()) {
                WeakReference<MenuPresenter> ref = (WeakReference)var3.next();
                MenuPresenter presenter = (MenuPresenter)ref.get();
                if (presenter == null) {
                    this.mPresenters.remove(ref);
                } else if (collapsed = presenter.collapseItemActionView(this, item)) {
                    break;
                }
            }

            this.startDispatchingItemsChanged();
            if (collapsed) {
                this.mExpandedItem = null;
            }

            return collapsed;
        } else {
            return false;
        }
    }

    public MenuItemImpl getExpandedItem() {
        return this.mExpandedItem;
    }

    public void setOverrideVisibleItems(boolean override) {
        this.mOverrideVisibleItems = override;
    }

    public interface ItemInvoker {
        boolean invokeItem(MenuItemImpl var1);
    }

    public interface Callback {
        boolean onMenuItemSelected(MenuBuilder var1, MenuItem var2);

        void onMenuModeChange(MenuBuilder var1);
    }
}

