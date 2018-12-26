package com.asdamp.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Checkable;

import com.asdamp.x_day.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CheckableFloatingActionButton extends FloatingActionButton implements Checkable {

    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    private boolean checked = false;
    private boolean broadcasting;

    private OnCheckedChangeListener onCheckedChangeListener;

    public interface OnCheckedChangeListener {

        void onCheckedChanged(CheckableFloatingActionButton fab, boolean isChecked);
    }

    public CheckableFloatingActionButton(Context context) {
        this(context, null);
    }

    public CheckableFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setChecked(false);

    }

    @Override
    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            refreshDrawableState();
            if (broadcasting) {
                return;
            }

            broadcasting = true;
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, this.checked);
            }

            broadcasting = false;
            hide();
            show();
        }
    }

    public void toggle(boolean animate) {
        if (animate) {
            setChecked(checked);
            return;
        }
        checked = !checked;
        jumpDrawablesToCurrentState();

    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        onCheckedChangeListener = listener;
    }

    @Override
    public boolean performClick() {
       // toggle();
        return super.performClick();
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

}