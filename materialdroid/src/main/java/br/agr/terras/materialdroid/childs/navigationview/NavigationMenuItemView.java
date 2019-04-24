package br.agr.terras.materialdroid.childs.navigationview;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.support.design.R.dimen;
import android.support.design.R.drawable;
import android.support.design.R.id;
import android.support.design.R.layout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.appcompat.R.attr;
import android.support.v7.widget.TooltipCompat;
import android.support.v7.widget.LinearLayoutCompat.LayoutParams;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;

public class NavigationMenuItemView extends ForegroundLinearLayout implements MenuView.ItemView {
    private static final int[] CHECKED_STATE_SET = new int[]{16842912};
    private final int iconSize;
    private boolean needsEmptyIcon;
    boolean checkable;
    private final CheckedTextView textView;
    private FrameLayout actionArea;
    private MenuItemImpl itemData;
    private ColorStateList iconTintList;
    private boolean hasIconTintList;
    private Drawable emptyDrawable;
    private final AccessibilityDelegateCompat accessibilityDelegate;

    public NavigationMenuItemView(Context context) {
        this(context, (AttributeSet)null);
    }

    public NavigationMenuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationMenuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.accessibilityDelegate = new AccessibilityDelegateCompat() {
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.setCheckable(NavigationMenuItemView.this.checkable);
            }
        };
        this.setOrientation(0);
        LayoutInflater.from(context).inflate(layout.design_navigation_menu_item, this, true);
        this.iconSize = context.getResources().getDimensionPixelSize(dimen.design_navigation_icon_size);
        this.textView = (CheckedTextView)this.findViewById(id.design_menu_item_text);
        this.textView.setDuplicateParentStateEnabled(true);
        ViewCompat.setAccessibilityDelegate(this.textView, this.accessibilityDelegate);
    }

    public void initialize(MenuItemImpl itemData, int menuType) {
        this.itemData = itemData;
        this.setVisibility(itemData.isVisible() ? View.VISIBLE : View.GONE);
        if (this.getBackground() == null) {
            ViewCompat.setBackground(this, this.createDefaultBackground());
        }

        this.setCheckable(itemData.isCheckable());
        this.setChecked(itemData.isChecked());
        this.setEnabled(itemData.isEnabled());
        this.setTitle(itemData.getTitle());
        this.setIcon(itemData.getIcon());
        this.setActionView(itemData.getActionView());
        this.setContentDescription(itemData.getContentDescription());
        TooltipCompat.setTooltipText(this, itemData.getTooltipText());
        this.adjustAppearance();
    }

    private boolean shouldExpandActionArea() {
        return this.itemData.getTitle() == null && this.itemData.getIcon() == null && this.itemData.getActionView() != null;
    }

    private void adjustAppearance() {
        LayoutParams params;
        if (this.shouldExpandActionArea()) {
            this.textView.setVisibility(View.GONE);
            if (this.actionArea != null) {
                params = (LayoutParams)this.actionArea.getLayoutParams();
                params.width = -1;
                this.actionArea.setLayoutParams(params);
            }
        } else {
            this.textView.setVisibility(View.VISIBLE);
            if (this.actionArea != null) {
                params = (LayoutParams)this.actionArea.getLayoutParams();
                params.width = -2;
                this.actionArea.setLayoutParams(params);
            }
        }

    }

    public void recycle() {
        if (this.actionArea != null) {
            this.actionArea.removeAllViews();
        }

        this.textView.setCompoundDrawables((Drawable)null, (Drawable)null, (Drawable)null, (Drawable)null);
    }

    private void setActionView(View actionView) {
        if (actionView != null) {
            if (this.actionArea == null) {
                this.actionArea = (FrameLayout)((ViewStub)this.findViewById(id.design_menu_item_action_area_stub)).inflate();
            }

            this.actionArea.removeAllViews();
            this.actionArea.addView(actionView);
        }

    }

    private StateListDrawable createDefaultBackground() {
        TypedValue value = new TypedValue();
        if (this.getContext().getTheme().resolveAttribute(attr.colorControlHighlight, value, true)) {
            StateListDrawable drawable = new StateListDrawable();
            drawable.addState(CHECKED_STATE_SET, new ColorDrawable(value.data));
            drawable.addState(EMPTY_STATE_SET, new ColorDrawable(0));
            return drawable;
        } else {
            return null;
        }
    }

    public MenuItemImpl getItemData() {
        return this.itemData;
    }

    public void setTitle(CharSequence title) {
        this.textView.setText(title);
    }

    public void setCheckable(boolean checkable) {
        this.refreshDrawableState();
        if (this.checkable != checkable) {
            this.checkable = checkable;
            this.accessibilityDelegate.sendAccessibilityEvent(this.textView, 2048);
        }

    }

    public void setChecked(boolean checked) {
        this.refreshDrawableState();
        this.textView.setChecked(checked);
    }

    public void setShortcut(boolean showShortcut, char shortcutKey) {
    }

    public void setIcon(Drawable icon) {
        if (icon != null) {
            if (this.hasIconTintList) {
                ConstantState state = icon.getConstantState();
                icon = DrawableCompat.wrap(state == null ? icon : state.newDrawable()).mutate();
                DrawableCompat.setTintList(icon, this.iconTintList);
            }

            icon.setBounds(0, 0, this.iconSize, this.iconSize);
        } else if (this.needsEmptyIcon) {
            if (this.emptyDrawable == null) {
                this.emptyDrawable = ResourcesCompat.getDrawable(this.getResources(), drawable.navigation_empty_icon, this.getContext().getTheme());
                if (this.emptyDrawable != null) {
                    this.emptyDrawable.setBounds(0, 0, this.iconSize, this.iconSize);
                }
            }

            icon = this.emptyDrawable;
        }

        TextViewCompat.setCompoundDrawablesRelative(this.textView, icon, (Drawable)null, (Drawable)null, (Drawable)null);
    }

    public boolean prefersCondensedTitle() {
        return false;
    }

    public boolean showsIcon() {
        return true;
    }

    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (this.itemData != null && this.itemData.isCheckable() && this.itemData.isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }

        return drawableState;
    }

    void setIconTintList(ColorStateList tintList) {
        this.iconTintList = tintList;
        this.hasIconTintList = this.iconTintList != null;
        if (this.itemData != null) {
            this.setIcon(this.itemData.getIcon());
        }

    }

    public void setTextAppearance(int textAppearance) {
        TextViewCompat.setTextAppearance(this.textView, textAppearance);
    }

    public void setTextColor(ColorStateList colors) {
        this.textView.setTextColor(colors);
    }

    public void setNeedsEmptyIcon(boolean needsEmptyIcon) {
        this.needsEmptyIcon = needsEmptyIcon;
    }

    public void setHorizontalPadding(int padding) {
        this.setPadding(padding, 0, padding, 0);
    }

    public void setIconPadding(int padding) {
        this.textView.setCompoundDrawablePadding(padding);
    }
}
