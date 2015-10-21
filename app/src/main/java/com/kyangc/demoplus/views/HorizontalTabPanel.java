package com.kyangc.demoplus.views;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.utils.DensityUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by chengkangyang on 十月.20.2015
 */
public class HorizontalTabPanel extends LinearLayout implements View.OnClickListener {

    int dividerMargin = 10;

    int dividerWidth = 1;

    int buttonCount = 0;

    int dividerColor = 0;

    int[] iconImageRes = null;

    int currentTabId = 0;

    boolean isDividerShown = true;

    boolean isKeepState = true;

    int backgroundRes = 0;

    ArrayList<ImageButton> buttonList = new ArrayList<>();

    OnTabClickListener mOnTabClickListener;

    public HorizontalTabPanel(Context context) {
        super(context);
    }

    public HorizontalTabPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        readInParams(context, attrs);

        //Judge image res
        buttonCount = getButtonsCount();
        if (buttonCount == 0) {
            this.setVisibility(GONE);
            return;
        }

        //Orientation
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.LEFT);

        //Add buttons
        for (int i = 0; i < buttonCount; i++) {
            //Init imagebutton
            ImageButton imageButton = new ImageButton(getContext());
            imageButton
                    .setLayoutParams(
                            new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,
                                    1));
            imageButton.setImageResource(iconImageRes[i]);
            imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageButton.setTag(i);
            imageButton.setOnClickListener(this);
            imageButton.setBackgroundResource(backgroundRes);

            //Add buttons to layout
            this.addView(imageButton);
            buttonList.add(imageButton);

            //Add divider to layout
            if (isDividerShown && i != buttonCount - 1) {
                View divider = new View(getContext());
                LinearLayout.LayoutParams lp = new LayoutParams(1,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                lp.setMargins(0, dividerMargin, 0, dividerMargin);
                divider.setLayoutParams(lp);
                divider.setBackgroundColor(dividerColor);
                this.addView(divider);
            }
        }
    }

    private void readInParams(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.HorizontalTabPanel, 0, 0);
        for (int j = 0; j < typedArray.getIndexCount(); j++) {
            int attr = typedArray.getIndex(j);
            switch (attr) {
                case R.styleable.HorizontalTabPanel_button_count:
                    buttonCount = typedArray.getInteger(attr, 0);
                    break;
                case R.styleable.HorizontalTabPanel_button_icon_list:
                    TypedArray array = getResources()
                            .obtainTypedArray(typedArray.getResourceId(attr, 0));
                    iconImageRes = new int[array == null ? 0 : array.length()];
                    for (int i = 0; i < iconImageRes.length; i++) {
                        iconImageRes[i] = array.getResourceId(i, -1);
                    }
                    if (array != null) {
                        array.recycle();
                    }
                    break;
                case R.styleable.HorizontalTabPanel_divider_color:
                    dividerColor = typedArray
                            .getColor(attr, getResources().getColor(R.color.light_grey));
                    break;
                case R.styleable.HorizontalTabPanel_divider_margin:
                    dividerMargin = typedArray.getDimensionPixelSize(attr, DensityUtils.dp2px(10));
                    break;
                case R.styleable.HorizontalTabPanel_divider_width:
                    dividerWidth = typedArray.getDimensionPixelSize(attr, 1);
                    break;
                case R.styleable.HorizontalTabPanel_show_divider:
                    isDividerShown = typedArray.getBoolean(attr, true);
                    break;
                case R.styleable.HorizontalTabPanel_panel_background:
                    backgroundRes = typedArray.getResourceId(attr, 0);
                    break;
                case R.styleable.HorizontalTabPanel_keep_state:
                    isKeepState = typedArray.getBoolean(attr, true);
                default:
                    break;
            }
        }
        typedArray.recycle();
    }

    public int getButtonsCount() {
        return Math.min(iconImageRes == null ? 0 : iconImageRes.length, buttonCount);
    }

    public int getCurrentTabId() {
        if (getButtonsCount() <= 0) {
            return -1;
        }
        return currentTabId;
    }

    public ImageButton getCurrentButton() {
        return buttonList == null || buttonList.size() <= 0 ? null
                : buttonList.get(getCurrentTabId());
    }

    @Override
    public void onClick(View v) {
        selectTab((int) v.getTag());
        if (mOnTabClickListener != null) {
            mOnTabClickListener.onTabClick((int) v.getTag());
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getButtonsCount() != 0) {
            selectTab(0);
        }
    }

    interface OnTabClickListener {

        void onTabClick(int i);
    }

    public void selectTab(int id) {
        currentTabId = id;
        if (isKeepState) {
            for (int i = 0; i < getButtonsCount(); i++) {
                buttonList.get(i).setSelected(id == i);
            }
        }
    }
}
