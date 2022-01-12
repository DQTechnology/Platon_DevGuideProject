package com.platon.aton.widge;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.digquant.R;
import com.digquant.util.DensityUtil;


/**
 * @author matrixelement
 */
public class ShadowButton extends LinearLayout {

    private int mXOffSet;
    private int mYOffSet;
    private int mBlurRadius;
    private int mEnableShadowColor;
    private int mDisableShadowColor;
    private String mText;
    private int mTextAppearance;
    private TextView mTextView;
    private int mTextWidth;
    private int mTextHeight;
    private int mShapeRadius;
    private Drawable mTextBackground;
    private boolean mEnabled;

    public ShadowButton(Context context) {
        super(context, null);
    }

    public ShadowButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShadowButton, defStyleAttr, 0);

        mXOffSet = a.getDimensionPixelSize(R.styleable.ShadowButton_xOffset, 0);
        mYOffSet = a.getDimensionPixelSize(R.styleable.ShadowButton_yOffset, DensityUtil.DP2PX(context, 2.0f));
        mBlurRadius = a.getDimensionPixelSize(R.styleable.ShadowButton_blurRadius, DensityUtil.DP2PX(context, 6.0f));
        mEnableShadowColor = a.getColor(R.styleable.ShadowButton_enableShadowColor, ContextCompat.getColor(context, R.color.color_660051ff));
        mDisableShadowColor = a.getColor(R.styleable.ShadowButton_disableShadowColor, ContextCompat.getColor(context, R.color.color_66969696));
        mText = a.getString(R.styleable.ShadowButton_text);
        mTextAppearance = a.getResourceId(R.styleable.ShadowButton_textAppearance, -1);
        mTextWidth = LayoutParams.MATCH_PARENT;
        mTextHeight = DensityUtil.DP2PX(context, 44f);
        mShapeRadius = DensityUtil.DP2PX(context, 22f);
        mTextBackground = a.getDrawable(R.styleable.ShadowButton_textBackground);
        mEnabled = a.getBoolean(R.styleable.ShadowButton_enabled, true);
        a.recycle();

        init(context);
    }

    private void init(Context context) {

        mTextView = new TextView(context);
        mTextView.setTextAppearance(context, mTextAppearance);
        mTextView.setText(mText);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTypeface(Typeface.DEFAULT_BOLD);
        mTextView.setBackgroundDrawable(mTextBackground);

        LayoutParams params = new LayoutParams(mTextWidth, mTextHeight);
        mTextView.setLayoutParams(params);

        addView(mTextView);

        setPadding(mBlurRadius, mBlurRadius - mYOffSet, mBlurRadius, mBlurRadius + mYOffSet);

        setEnabled(mEnabled);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setEnabled(mEnabled);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.mEnabled = enabled;
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setEnabled(enabled);
        }
        ShadowDrawable.setShadowDrawable(this,
                Color.TRANSPARENT,
                mShapeRadius,
                enabled ? mEnableShadowColor : mDisableShadowColor
                , mBlurRadius,
                mXOffSet,
                mYOffSet);
    }

    public void setText(CharSequence text) {
        if (mTextView != null) {
            mTextView.setText(text);
        }
    }

    public TextView getTextView(){
        return mTextView;
    }
}
