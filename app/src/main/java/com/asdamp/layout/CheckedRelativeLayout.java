package com.asdamp.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class CheckedRelativeLayout extends RelativeLayout implements Checkable{
	private static final int[] CHECKED_STATE_SET = {
        android.R.attr.state_checked
    };
    
    private boolean checked = false;

    @SuppressLint("NewApi")
    public CheckedRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CheckedRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckedRelativeLayout(Context context) {
        super(context);
    }

    
    public boolean isChecked() {
        return checked;
    }

    
    public void setChecked(boolean checked) {
        this.checked = checked;
        
        refreshDrawableState();
    
        //Propagate to childs
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if(child instanceof Checkable) {
                ((Checkable)child).setChecked(checked);
            }
        }
    }
    
    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    public void toggle() {
        this.checked = !this.checked;
    }
}
