/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package framework.telegram.ui.selectedroundcardview;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;


/**
 * Very simple drawable that draws a rounded rectangle background with arbitrary corners and also
 * reports proper outline for Lollipop.
 * <p>
 * Simpler and uses less resources compared to GradientDrawable or ShapeDrawable.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class RoundRectDrawable extends Drawable {
    private float mRadius;
    private final Paint mPaint;
    private final RectF mBoundsF;
    private final Rect mBoundsI;
    private float mPadding;
    private boolean mInsetForPadding = false;
    private boolean mInsetForRadius = true;

    private ColorStateList mBackground;
    private PorterDuffColorFilter mTintFilter;
    private ColorStateList mTint;
    private PorterDuff.Mode mTintMode = PorterDuff.Mode.SRC_IN;

    private boolean[] roundedCorner;


    RoundRectDrawable(ColorStateList backgroundColor, float radius) {
        mRadius = radius;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        setBackground(backgroundColor);

        mBoundsF = new RectF();
        mBoundsI = new Rect();
    }


    RoundRectDrawable(ColorStateList backgroundColor, float radius, boolean[] roundedCorner) {
        mRadius = radius;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        setBackground(backgroundColor);

        mBoundsF = new RectF();
        mBoundsI = new Rect();

        this.roundedCorner = roundedCorner;
    }

    private void setBackground(ColorStateList color) {
        mBackground = (color == null) ? ColorStateList.valueOf(Color.TRANSPARENT) : color;
        mPaint.setColor(mBackground.getColorForState(getState(), mBackground.getDefaultColor()));
    }

    void setPadding(float padding, boolean insetForPadding, boolean insetForRadius) {
        if (padding == mPadding && mInsetForPadding == insetForPadding
                && mInsetForRadius == insetForRadius) {
            return;
        }
        mPadding = padding;
        mInsetForPadding = insetForPadding;
        mInsetForRadius = insetForRadius;
        updateBounds(null);
        invalidateSelf();
    }

    float getPadding() {
        return mPadding;
    }

    @Override
    public void draw(Canvas canvas) {
        final Paint paint = mPaint;

        final boolean clearColorFilter;
        if (mTintFilter != null && paint.getColorFilter() == null) {
            paint.setColorFilter(mTintFilter);
            clearColorFilter = true;
        } else {
            clearColorFilter = false;
        }

        if (null == roundedCorner) {
            canvas.drawRoundRect(mBoundsF, mRadius, mRadius, paint);

        } else {
            drawTopRoundRect(canvas, mBoundsF, mRadius, mRadius, paint, roundedCorner);

        }


        if (clearColorFilter) {
            paint.setColorFilter(null);
        }
    }


    public static void drawTopRoundRect(Canvas canvas, RectF rect, float mRadius, float mRadius1, Paint paint, boolean[] roundedCorner) {


        if (roundedCorner.length != 4) {
            Log.e("RoundRectDrawable", "Not enough conner data");

            return;
        }

        Path clipPath = new Path();
        float[] corners = new float[8];
        int i = 0;
        for (boolean r : roundedCorner) {
            if (r) {

                corners[i++] = mRadius;


                corners[i++] = mRadius1;

            } else {

                corners[i++] = 0;
                corners[i++] = 0;

            }
        }

        clipPath.addRoundRect(rect, corners, Path.Direction.CW);


        canvas.drawPath(clipPath, paint);


    }


    private void updateBounds(Rect bounds) {
        if (bounds == null) {
            bounds = getBounds();
        }
        mBoundsF.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
        mBoundsI.set(bounds);
        if (mInsetForPadding) {
            float vInset = RoundRectDrawableWithShadow.calculateVerticalPadding(mPadding, mRadius, mInsetForRadius);
            float hInset = RoundRectDrawableWithShadow.calculateHorizontalPadding(mPadding, mRadius, mInsetForRadius);
            mBoundsI.inset((int) Math.ceil(hInset), (int) Math.ceil(vInset));
            // to make sure they have same bounds.
            mBoundsF.set(mBoundsI);
        }




    }


    //    @Override
//    protected void onBoundsChange(Rect bounds) {
//        super.onBoundsChange(bounds);
//        //mRectBottomR.set( (bounds.width() -mMargin) / 2, (bounds.height() -mMargin)/ 2,bounds.width() - mMargin, bounds.height() - mMargin);
//        // mRectBottomL.set( 0,  (bounds.height() -mMargin) / 2, (bounds.width() -mMargin)/ 2, bounds.height() - mMargin);
//    }
    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateBounds(bounds);
    }

    @Override
    public void getOutline(Outline outline) {

        if (null != roundedCorner) {
            Path clipPath = new Path();
            float[] corners = new float[8];
            int i = 0;
            for (boolean r : roundedCorner) {
                if (r) {

                    corners[i++] = mRadius;


                    corners[i++] = mRadius;

                } else {

                    corners[i++] = 0;
                    corners[i++] = 0;

                }
            }
            clipPath.addRoundRect(mBoundsF, corners, Path.Direction.CW);


            outline.setConvexPath(clipPath);



        } else {
            outline.setRoundRect(mBoundsI, mRadius);

        }


    }


    void setRadius(float radius) {
        if (radius == mRadius) {
            return;
        }
        mRadius = radius;
        updateBounds(null);
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public float getRadius() {
        return mRadius;
    }

    public void setColor(@Nullable ColorStateList color) {
        setBackground(color);
        invalidateSelf();
    }

    public ColorStateList getColor() {
        return mBackground;
    }

    @Override
    public void setTintList(ColorStateList tint) {
        mTint = tint;
        mTintFilter = createTintFilter(mTint, mTintMode);
        invalidateSelf();
    }

    @Override
    public void setTintMode(PorterDuff.Mode tintMode) {
        mTintMode = tintMode;
        mTintFilter = createTintFilter(mTint, mTintMode);
        invalidateSelf();
    }

    @Override
    protected boolean onStateChange(int[] stateSet) {
        final int newColor = mBackground.getColorForState(stateSet, mBackground.getDefaultColor());
        final boolean colorChanged = newColor != mPaint.getColor();
        if (colorChanged) {
            mPaint.setColor(newColor);
        }
        if (mTint != null && mTintMode != null) {
            mTintFilter = createTintFilter(mTint, mTintMode);
            return true;
        }
        return colorChanged;
    }




    @Override
    public boolean isStateful() {
        return (mTint != null && mTint.isStateful())
                || (mBackground != null && mBackground.isStateful()) || super.isStateful();
    }

    /**
     * Ensures the tint filter is consistent with the current tint color and
     * mode.
     */
    private PorterDuffColorFilter createTintFilter(ColorStateList tint, PorterDuff.Mode tintMode) {
        if (tint == null || tintMode == null) {
            return null;
        }
        final int color = tint.getColorForState(getState(), Color.TRANSPARENT);
        return new PorterDuffColorFilter(color, tintMode);
    }
}
