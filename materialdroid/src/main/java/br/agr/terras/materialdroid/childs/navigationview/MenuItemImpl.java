package br.agr.terras.materialdroid.childs.navigationview;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.appcompat.R.string;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.menu.MenuView.ItemView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewDebug.CapturedViewProperty;
import android.widget.LinearLayout;

public final class MenuItemImpl implements SupportMenuItem {
    private static final String TAG = "MenuItemImpl";
    private static final int SHOW_AS_ACTION_MASK = 3;
    private final int mId;
    private final int mGroup;
    private final int mCategoryOrder;
    private final int mOrdering;
    private CharSequence mTitle;
    private CharSequence mTitleCondensed;
    private Intent mIntent;
    private char mShortcutNumericChar;
    private int mShortcutNumericModifiers = 4096;
    private char mShortcutAlphabeticChar;
    private int mShortcutAlphabeticModifiers = 4096;
    private Drawable mIconDrawable;
    private int mIconResId = 0;
    MenuBuilder mMenu;
    private SubMenuBuilder mSubMenu;
    private Runnable mItemCallback;
    private OnMenuItemClickListener mClickListener;
    private CharSequence mContentDescription;
    private CharSequence mTooltipText;
    private ColorStateList mIconTintList = null;
    private Mode mIconTintMode = null;
    private boolean mHasIconTint = false;
    private boolean mHasIconTintMode = false;
    private boolean mNeedToApplyIconTint = false;
    private int mFlags = 16;
    private static final int CHECKABLE = 1;
    private static final int CHECKED = 2;
    private static final int EXCLUSIVE = 4;
    private static final int HIDDEN = 8;
    private static final int ENABLED = 16;
    private static final int IS_ACTION = 32;
    private int mShowAsAction = 0;
    private View mActionView;
    private ActionProvider mActionProvider;
    private OnActionExpandListener mOnActionExpandListener;
    private boolean mIsActionViewExpanded = false;
    static final int NO_ICON = 0;
    private ContextMenuInfo mMenuInfo;

    MenuItemImpl(MenuBuilder menu, int group, int id, int categoryOrder, int ordering, CharSequence title, int showAsAction) {
        this.mMenu = menu;
        this.mId = id;
        this.mGroup = group;
        this.mCategoryOrder = categoryOrder;
        this.mOrdering = ordering;
        this.mTitle = title;
        this.mShowAsAction = showAsAction;
    }

    public boolean invoke() {
        if (this.mClickListener != null && this.mClickListener.onMenuItemClick(this)) {
            return true;
        } else if (this.mMenu.dispatchMenuItemSelected(this.mMenu, this)) {
            return true;
        } else if (this.mItemCallback != null) {
            this.mItemCallback.run();
            return true;
        } else {
            if (this.mIntent != null) {
                try {
                    this.mMenu.getContext().startActivity(this.mIntent);
                    return true;
                } catch (ActivityNotFoundException var2) {
                    Log.e("MenuItemImpl", "Can't find activity to handle intent; ignoring", var2);
                }
            }

            return this.mActionProvider != null && this.mActionProvider.onPerformDefaultAction();
        }
    }

    public boolean isEnabled() {
        return (this.mFlags & 16) != 0;
    }

    public MenuItem setEnabled(boolean enabled) {
        if (enabled) {
            this.mFlags |= 16;
        } else {
            this.mFlags &= -17;
        }

        this.mMenu.onItemsChanged(false);
        return this;
    }

    public int getGroupId() {
        return this.mGroup;
    }

    @CapturedViewProperty
    public int getItemId() {
        return this.mId;
    }

    public int getOrder() {
        return this.mCategoryOrder;
    }

    public int getOrdering() {
        return this.mOrdering;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public MenuItem setIntent(Intent intent) {
        this.mIntent = intent;
        return this;
    }

    Runnable getCallback() {
        return this.mItemCallback;
    }

    public MenuItem setCallback(Runnable callback) {
        this.mItemCallback = callback;
        return this;
    }

    public char getAlphabeticShortcut() {
        return this.mShortcutAlphabeticChar;
    }

    public MenuItem setAlphabeticShortcut(char alphaChar) {
        if (this.mShortcutAlphabeticChar == alphaChar) {
            return this;
        } else {
            this.mShortcutAlphabeticChar = Character.toLowerCase(alphaChar);
            this.mMenu.onItemsChanged(false);
            return this;
        }
    }

    public MenuItem setAlphabeticShortcut(char alphaChar, int alphaModifiers) {
        if (this.mShortcutAlphabeticChar == alphaChar && this.mShortcutAlphabeticModifiers == alphaModifiers) {
            return this;
        } else {
            this.mShortcutAlphabeticChar = Character.toLowerCase(alphaChar);
            this.mShortcutAlphabeticModifiers = KeyEvent.normalizeMetaState(alphaModifiers);
            this.mMenu.onItemsChanged(false);
            return this;
        }
    }

    public int getAlphabeticModifiers() {
        return this.mShortcutAlphabeticModifiers;
    }

    public char getNumericShortcut() {
        return this.mShortcutNumericChar;
    }

    public int getNumericModifiers() {
        return this.mShortcutNumericModifiers;
    }

    public MenuItem setNumericShortcut(char numericChar) {
        if (this.mShortcutNumericChar == numericChar) {
            return this;
        } else {
            this.mShortcutNumericChar = numericChar;
            this.mMenu.onItemsChanged(false);
            return this;
        }
    }

    public MenuItem setNumericShortcut(char numericChar, int numericModifiers) {
        if (this.mShortcutNumericChar == numericChar && this.mShortcutNumericModifiers == numericModifiers) {
            return this;
        } else {
            this.mShortcutNumericChar = numericChar;
            this.mShortcutNumericModifiers = KeyEvent.normalizeMetaState(numericModifiers);
            this.mMenu.onItemsChanged(false);
            return this;
        }
    }

    public MenuItem setShortcut(char numericChar, char alphaChar) {
        this.mShortcutNumericChar = numericChar;
        this.mShortcutAlphabeticChar = Character.toLowerCase(alphaChar);
        this.mMenu.onItemsChanged(false);
        return this;
    }

    public MenuItem setShortcut(char numericChar, char alphaChar, int numericModifiers, int alphaModifiers) {
        this.mShortcutNumericChar = numericChar;
        this.mShortcutNumericModifiers = KeyEvent.normalizeMetaState(numericModifiers);
        this.mShortcutAlphabeticChar = Character.toLowerCase(alphaChar);
        this.mShortcutAlphabeticModifiers = KeyEvent.normalizeMetaState(alphaModifiers);
        this.mMenu.onItemsChanged(false);
        return this;
    }

    char getShortcut() {
        return this.mMenu.isQwertyMode() ? this.mShortcutAlphabeticChar : this.mShortcutNumericChar;
    }

    String getShortcutLabel() {
        char shortcut = this.getShortcut();
        if (shortcut == 0) {
            return "";
        } else {
            Resources res = this.mMenu.getContext().getResources();
            StringBuilder sb = new StringBuilder();
            if (ViewConfiguration.get(this.mMenu.getContext()).hasPermanentMenuKey()) {
                sb.append(res.getString(string.abc_prepend_shortcut_label));
            }

            int modifiers = this.mMenu.isQwertyMode() ? this.mShortcutAlphabeticModifiers : this.mShortcutNumericModifiers;
            appendModifier(sb, modifiers, 65536, res.getString(string.abc_menu_meta_shortcut_label));
            appendModifier(sb, modifiers, 4096, res.getString(string.abc_menu_ctrl_shortcut_label));
            appendModifier(sb, modifiers, 2, res.getString(string.abc_menu_alt_shortcut_label));
            appendModifier(sb, modifiers, 1, res.getString(string.abc_menu_shift_shortcut_label));
            appendModifier(sb, modifiers, 4, res.getString(string.abc_menu_sym_shortcut_label));
            appendModifier(sb, modifiers, 8, res.getString(string.abc_menu_function_shortcut_label));
            switch(shortcut) {
                case '\b':
                    sb.append(res.getString(string.abc_menu_delete_shortcut_label));
                    break;
                case '\n':
                    sb.append(res.getString(string.abc_menu_enter_shortcut_label));
                    break;
                case ' ':
                    sb.append(res.getString(string.abc_menu_space_shortcut_label));
                    break;
                default:
                    sb.append(shortcut);
            }

            return sb.toString();
        }
    }

    private static void appendModifier(StringBuilder sb, int modifiers, int flag, String label) {
        if ((modifiers & flag) == flag) {
            sb.append(label);
        }

    }

    boolean shouldShowShortcut() {
        return this.mMenu.isShortcutsVisible() && this.getShortcut() != 0;
    }

    public SubMenu getSubMenu() {
        return this.mSubMenu;
    }

    public boolean hasSubMenu() {
        return this.mSubMenu != null;
    }

    public void setSubMenu(SubMenuBuilder subMenu) {
        this.mSubMenu = subMenu;
        subMenu.setHeaderTitle(this.getTitle());
    }

    @CapturedViewProperty
    public CharSequence getTitle() {
        return this.mTitle;
    }

    CharSequence getTitleForItemView(ItemView itemView) {
        return itemView != null && itemView.prefersCondensedTitle() ? this.getTitleCondensed() : this.getTitle();
    }

    public MenuItem setTitle(CharSequence title) {
        this.mTitle = title;
        this.mMenu.onItemsChanged(false);
        if (this.mSubMenu != null) {
            this.mSubMenu.setHeaderTitle(title);
        }

        return this;
    }

    public MenuItem setTitle(int title) {
        return this.setTitle(this.mMenu.getContext().getString(title));
    }

    public CharSequence getTitleCondensed() {
        CharSequence ctitle = this.mTitleCondensed != null ? this.mTitleCondensed : this.mTitle;
        return (CharSequence)(VERSION.SDK_INT < 18 && ctitle != null && !(ctitle instanceof String) ? ctitle.toString() : ctitle);
    }

    public MenuItem setTitleCondensed(CharSequence title) {
        this.mTitleCondensed = title;
        if (title == null) {
            title = this.mTitle;
        }

        this.mMenu.onItemsChanged(false);
        return this;
    }

    public Drawable getIcon() {
        if (this.mIconDrawable != null) {
            return this.applyIconTintIfNecessary(this.mIconDrawable);
        } else if (this.mIconResId != 0) {
            Drawable icon = AppCompatResources.getDrawable(this.mMenu.getContext(), this.mIconResId);
            this.mIconResId = 0;
            this.mIconDrawable = icon;
            return this.applyIconTintIfNecessary(icon);
        } else {
            return null;
        }
    }

    public MenuItem setIcon(Drawable icon) {
        this.mIconResId = 0;
        this.mIconDrawable = icon;
        this.mNeedToApplyIconTint = true;
        this.mMenu.onItemsChanged(false);
        return this;
    }

    public MenuItem setIcon(int iconResId) {
        this.mIconDrawable = null;
        this.mIconResId = iconResId;
        this.mNeedToApplyIconTint = true;
        this.mMenu.onItemsChanged(false);
        return this;
    }

    public MenuItem setIconTintList(@Nullable ColorStateList iconTintList) {
        this.mIconTintList = iconTintList;
        this.mHasIconTint = true;
        this.mNeedToApplyIconTint = true;
        this.mMenu.onItemsChanged(false);
        return this;
    }

    public ColorStateList getIconTintList() {
        return this.mIconTintList;
    }

    public MenuItem setIconTintMode(Mode iconTintMode) {
        this.mIconTintMode = iconTintMode;
        this.mHasIconTintMode = true;
        this.mNeedToApplyIconTint = true;
        this.mMenu.onItemsChanged(false);
        return this;
    }

    public Mode getIconTintMode() {
        return this.mIconTintMode;
    }

    private Drawable applyIconTintIfNecessary(Drawable icon) {
        if (icon != null && this.mNeedToApplyIconTint && (this.mHasIconTint || this.mHasIconTintMode)) {
            icon = DrawableCompat.wrap(icon);
            icon = icon.mutate();
            if (this.mHasIconTint) {
                DrawableCompat.setTintList(icon, this.mIconTintList);
            }

            if (this.mHasIconTintMode) {
                DrawableCompat.setTintMode(icon, this.mIconTintMode);
            }

            this.mNeedToApplyIconTint = false;
        }

        return icon;
    }

    public boolean isCheckable() {
        return (this.mFlags & 1) == 1;
    }

    public MenuItem setCheckable(boolean checkable) {
        int oldFlags = this.mFlags;
        this.mFlags = this.mFlags & -2 | (checkable ? 1 : 0);
        if (oldFlags != this.mFlags) {
            this.mMenu.onItemsChanged(false);
        }

        return this;
    }

    public void setExclusiveCheckable(boolean exclusive) {
        this.mFlags = this.mFlags & -5 | (exclusive ? 4 : 0);
    }

    public boolean isExclusiveCheckable() {
        return (this.mFlags & 4) != 0;
    }

    public boolean isChecked() {
        return (this.mFlags & 2) == 2;
    }

    public MenuItem setChecked(boolean checked) {
        if ((this.mFlags & 4) != 0) {
            this.mMenu.setExclusiveItemChecked(this);
        } else {
            this.setCheckedInt(checked);
        }

        return this;
    }

    void setCheckedInt(boolean checked) {
        int oldFlags = this.mFlags;
        this.mFlags = this.mFlags & -3 | (checked ? 2 : 0);
        if (oldFlags != this.mFlags) {
            this.mMenu.onItemsChanged(false);
        }

    }

    public boolean isVisible() {
        if (this.mActionProvider != null && this.mActionProvider.overridesItemVisibility()) {
            return (this.mFlags & 8) == 0 && this.mActionProvider.isVisible();
        } else {
            return (this.mFlags & 8) == 0;
        }
    }

    boolean setVisibleInt(boolean shown) {
        int oldFlags = this.mFlags;
        this.mFlags = this.mFlags & -9 | (shown ? 0 : 8);
        return oldFlags != this.mFlags;
    }

    public MenuItem setVisible(boolean shown) {
        if (this.setVisibleInt(shown)) {
            this.mMenu.onItemVisibleChanged(this);
        }

        return this;
    }

    public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener clickListener) {
        this.mClickListener = clickListener;
        return this;
    }

    public String toString() {
        return this.mTitle != null ? this.mTitle.toString() : null;
    }

    void setMenuInfo(ContextMenuInfo menuInfo) {
        this.mMenuInfo = menuInfo;
    }

    public ContextMenuInfo getMenuInfo() {
        return this.mMenuInfo;
    }

    public void actionFormatChanged() {
        this.mMenu.onItemActionRequestChanged(this);
    }

    public boolean shouldShowIcon() {
        return this.mMenu.getOptionalIconsVisible();
    }

    public boolean isActionButton() {
        return (this.mFlags & 32) == 32;
    }

    public boolean requestsActionButton() {
        return (this.mShowAsAction & 1) == 1;
    }

    public boolean requiresActionButton() {
        return (this.mShowAsAction & 2) == 2;
    }

    public void setIsActionButton(boolean isActionButton) {
        if (isActionButton) {
            this.mFlags |= 32;
        } else {
            this.mFlags &= -33;
        }

    }

    public boolean showsTextAsAction() {
        return (this.mShowAsAction & 4) == 4;
    }

    public void setShowAsAction(int actionEnum) {
        switch(actionEnum & 3) {
            case 0:
            case 1:
            case 2:
                this.mShowAsAction = actionEnum;
                this.mMenu.onItemActionRequestChanged(this);
                return;
            default:
                throw new IllegalArgumentException("SHOW_AS_ACTION_ALWAYS, SHOW_AS_ACTION_IF_ROOM, and SHOW_AS_ACTION_NEVER are mutually exclusive.");
        }
    }

    public SupportMenuItem setActionView(View view) {
        this.mActionView = view;
        this.mActionProvider = null;
        if (view != null && view.getId() == -1 && this.mId > 0) {
            view.setId(this.mId);
        }

        this.mMenu.onItemActionRequestChanged(this);
        return this;
    }

    public SupportMenuItem setActionView(int resId) {
        Context context = this.mMenu.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        this.setActionView(inflater.inflate(resId, new LinearLayout(context), false));
        return this;
    }

    public View getActionView() {
        if (this.mActionView != null) {
            return this.mActionView;
        } else if (this.mActionProvider != null) {
            this.mActionView = this.mActionProvider.onCreateActionView(this);
            return this.mActionView;
        } else {
            return null;
        }
    }

    public MenuItem setActionProvider(android.view.ActionProvider actionProvider) {
        throw new UnsupportedOperationException("This is not supported, use MenuItemCompat.setActionProvider()");
    }

    public android.view.ActionProvider getActionProvider() {
        throw new UnsupportedOperationException("This is not supported, use MenuItemCompat.getActionProvider()");
    }

    public ActionProvider getSupportActionProvider() {
        return this.mActionProvider;
    }

    public SupportMenuItem setSupportActionProvider(ActionProvider actionProvider) {
        if (this.mActionProvider != null) {
            this.mActionProvider.reset();
        }

        this.mActionView = null;
        this.mActionProvider = actionProvider;
        this.mMenu.onItemsChanged(true);
        if (this.mActionProvider != null) {
            this.mActionProvider.setVisibilityListener(new ActionProvider.VisibilityListener() {
                public void onActionProviderVisibilityChanged(boolean isVisible) {
                    MenuItemImpl.this.mMenu.onItemVisibleChanged(MenuItemImpl.this);
                }
            });
        }

        return this;
    }

    public SupportMenuItem setShowAsActionFlags(int actionEnum) {
        this.setShowAsAction(actionEnum);
        return this;
    }

    public boolean expandActionView() {
        if (!this.hasCollapsibleActionView()) {
            return false;
        } else {
            return this.mOnActionExpandListener != null && !this.mOnActionExpandListener.onMenuItemActionExpand(this) ? false : this.mMenu.expandItemActionView(this);
        }
    }

    public boolean collapseActionView() {
        if ((this.mShowAsAction & 8) == 0) {
            return false;
        } else if (this.mActionView == null) {
            return true;
        } else {
            return this.mOnActionExpandListener != null && !this.mOnActionExpandListener.onMenuItemActionCollapse(this) ? false : this.mMenu.collapseItemActionView(this);
        }
    }

    public boolean hasCollapsibleActionView() {
        if ((this.mShowAsAction & 8) != 0) {
            if (this.mActionView == null && this.mActionProvider != null) {
                this.mActionView = this.mActionProvider.onCreateActionView(this);
            }

            return this.mActionView != null;
        } else {
            return false;
        }
    }

    public void setActionViewExpanded(boolean isExpanded) {
        this.mIsActionViewExpanded = isExpanded;
        this.mMenu.onItemsChanged(false);
    }

    public boolean isActionViewExpanded() {
        return this.mIsActionViewExpanded;
    }

    public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
        this.mOnActionExpandListener = listener;
        return this;
    }

    public SupportMenuItem setContentDescription(CharSequence contentDescription) {
        this.mContentDescription = contentDescription;
        this.mMenu.onItemsChanged(false);
        return this;
    }

    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    public SupportMenuItem setTooltipText(CharSequence tooltipText) {
        this.mTooltipText = tooltipText;
        this.mMenu.onItemsChanged(false);
        return this;
    }

    public CharSequence getTooltipText() {
        return this.mTooltipText;
    }
}
