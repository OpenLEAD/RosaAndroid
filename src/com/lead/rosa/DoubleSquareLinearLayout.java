package com.lead.rosa;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class DoubleSquareLinearLayout extends LinearLayout {

	public DoubleSquareLinearLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public DoubleSquareLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec / 2);
	}

	public DoubleSquareLinearLayout(Context context) {
		super(context);
	}

}
