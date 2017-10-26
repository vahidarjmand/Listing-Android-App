/*
 * Copyright (C) 2016 Samuel Wall
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.samuelwall.materialtaptargetprompt;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import java.text.Bidi;

import tmediaa.ir.ahamdian.R;


/**
 * A Material Design tap target onboarding implementation.
 * <p>
 * <div class="special reference">
 * <h3>Onboarding</h3>
 * <p>For more information about onboarding and tap targets, read the
 * <a href="https://www.google.com/design/spec/growth-communications/onboarding.html">Onboarding</a>
 * Material Design guidelines.</p>
 * </div>
 */
public class MaterialTapTargetPrompt
{

    /**
     * Prompt is reveal animation is running.
     */
    public static final int STATE_REVEALING = 1;

    /**
     * Prompt reveal animation has finished and the prompt is displayed.
     */
    public static final int STATE_REVEALED = 2;

    /**
     * The prompt target has been pressed in the focal area.
     */
    public static final int STATE_FOCAL_PRESSED = 3;

    /**
     * The prompt has been removed from view after the prompt has been pressed in the focal area.
     */
    public static final int STATE_FINISHED = 4;

    /**
     * The {@link #dismiss()} method has been called and the prompt is being removed from view.
     */
    public static final int STATE_DISMISSING = 5;

    /**
     * The prompt has been removed from view after the prompt has either been pressed somewhere
     * other than the prompt target or the system back button has been pressed.
     */
    public static final int STATE_DISMISSED = 6;

    /**
     * The {@link #finish()} method has been called and the prompt is being removed from view.
     */
    public static final int STATE_FINISHING = 7;

    /**
     * The prompt has been pressed outside the focal area.
     */
    public static final int STATE_NON_FOCAL_PRESSED = 8;

    /**
     * The {@link ResourceFinder} used to find views and resources.
     */
    ResourceFinder mResourceFinder;

    /**
     * The view that renders the prompt.
     */
    PromptView mView;

    /**
     * The prompt view target.
     */
    View mTargetView;

    /**
     * The prompt target as a position.
     */
    PointF mTargetPosition;

    /**
     * The focal radius prior to animation changes.
     */
    float mBaseFocalRadius;

    /**
     * The background radius prior to animation changes.
     */
    float mBaseBackgroundRadius;

    /**
     * The background centre position prior to animation changes.
     */
    PointF mBaseBackgroundPosition = new PointF();

    /**
     * The focal radius + 10 percent.
     */
    float mFocalRadius10Percent;

    /**
     * The current amount that the prompt has been revealed.
     * Any value between 1 to 0 inclusive.
     */
    float mRevealedAmount;

    /**
     * The multiplication to apply to text alpha values.
     * Any value between 1 to 0 inclusive.
     */
    float mAlphaModifier;

    /**
     * The primary text to display.
     */
    CharSequence mPrimaryText;

    /**
     * The secondary text to display.
     */
    CharSequence mSecondaryText;

    /**
     * The maximum width that the displayed text can be.
     */
    float mMaxTextWidth;

    /**
     * Padding between text (left or right side) and the background edge.
     */
    float mTextPadding;

    /**
     * True: Text should be positioned above the target.
     * False: Text should be positioned below the target.
     */
    boolean mVerticalTextPositionAbove;

    /**
     * True: Text should be positioned to the left of the target.
     * False: Text should be positioned to the right of the target.
     */
    boolean mHorizontalTextPositionLeft;

    /**
     * Is the target more than 88dp from the left, right, top or bottom.
     */
    boolean mInside88dpBounds;

    /**
     * The distance between the focal edge and the displayed text.
     */
    float mFocalToTextPadding;

    /**
     * The primary text colour alpha value.
     */
    int mPrimaryTextColourAlpha;

    /**
     * The secondary text colour alpha value.
     */
    int mSecondaryTextColourAlpha;

    /**
     * Used to calculate the animation progress for the reveal and dismiss animations.
     */
    ValueAnimator mAnimationCurrent;

    /**
     * Used to calculate the animation progress for the idle animation.
     */
    ValueAnimator mAnimationFocalRipple;

    /**
     * The animation interpolator to use for animations.
     */
    Interpolator mAnimationInterpolator;

    /**
     * The last percentage progress for idle animation.
     * Value between 1 to 0 inclusive.
     * Used in the idle animation to track when the animation should change direction.
     */
    float mFocalRippleProgress;

    /**
     * The focal ripple alpha value.
     */
    int mBaseFocalRippleAlpha;

    /**
     * The paint used to draw the primary text.
     */
    TextPaint mPaintPrimaryText;

    /**
     * The paint used to draw the secondary text.
     */
    TextPaint mPaintSecondaryText;

    /**
     * The listener for prompt events.
     * Can be null.
     */
    PromptStateChangeListener mPromptStateChangeListener;

    /**
     * Is the prompt currently being removed from view.
     */
    boolean mDismissing;

    /**
     * The view group which contains the target view.
     */
    ViewGroup mParentView;

    /**
     * The view to clip the prompt drawing to.
     */
    View mClipToView;

    /**
     * The system status bar height.
     */
    final float mStatusBarHeight;

    /**
     * Listener for the view layout changing.
     */
    final ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;

    /**
     * Allow auto dismissing.
     */
    boolean mAutoDismiss;

    /**
     * Allow auto finishing.
     */
    boolean mAutoFinish;

    /**
     * True if the idle animation is enabled.
     */
    boolean mIdleAnimationEnabled = true;

    /**
     * The primary text layout alignment.
     * Left, centre or right.
     */
    Layout.Alignment mPrimaryTextAlignment;

    /**
     * The secondary text layout alignment.
     * Left, centre or right.
     */
    Layout.Alignment mSecondaryTextAlignment;

    /**
     * Clip view bounds inset by 88dp.
     */
    RectF mClipViewBoundsInset88dp;

    /**
     * 88dp pixel value.
     */
    float m88dp;

    /**
     * The background colour alpha value.
     */
    int mBaseBackgroundColourAlpha;

    /**
     * The focal colour alpha value.
     */
    int mBaseFocalColourAlpha;

    /**
     * Default constructor.
     *
     * @param resourceFinder The {@link ResourceFinder} used to find views and resources.
     */
    MaterialTapTargetPrompt(final ResourceFinder resourceFinder)
    {
        mResourceFinder = resourceFinder;
        mView = new PromptView(mResourceFinder.getContext());
        mView.mPromptTouchedListener = new PromptView.PromptTouchedListener()
        {
            @Override
            public void onFocalPressed()
            {
                if (!mDismissing)
                {
                    onPromptStateChanged(STATE_FOCAL_PRESSED);
                    if (mAutoFinish)
                    {
                        finish();
                    }
                }
            }

            @Override
            public void onNonFocalPressed()
            {
                if (!mDismissing)
                {
                    onPromptStateChanged(STATE_NON_FOCAL_PRESSED);
                    if (mAutoDismiss)
                    {
                        dismiss();
                    }
                }
            }
        };

        Rect rect = new Rect();
        mResourceFinder.getPromptParentView().getWindowVisibleDisplayFrame(rect);
        mStatusBarHeight = rect.top;

        mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                if (mTargetView != null)
                {
                    final boolean isTargetAttachedToWindow;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    {
                        isTargetAttachedToWindow = mTargetView.isAttachedToWindow();
                    }
                    else
                    {
                        isTargetAttachedToWindow = mTargetView.getWindowToken() != null;
                    }
                    if (!isTargetAttachedToWindow)
                    {
                        return;
                    }
                }
                updateFocalCentrePosition();
            }
        };
    }

    /**
     * Displays the prompt.
     */
    public void show()
    {
        mParentView.addView(mView);
        addGlobalLayoutListener();
        onPromptStateChanged(STATE_REVEALING);
        startRevealAnimation();
    }

    /**
     * Adds layout listener to view parent to capture layout changes.
     */
    void addGlobalLayoutListener()
    {
        final ViewTreeObserver viewTreeObserver = mParentView.getViewTreeObserver();
        if (viewTreeObserver.isAlive())
        {
            viewTreeObserver.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }
    }

    /**
     * Removes global layout listener added in {@link #addGlobalLayoutListener()}.
     */
    void removeGlobalLayoutListener()
    {
        final ViewTreeObserver viewTreeObserver = mParentView.getViewTreeObserver();
        if (viewTreeObserver.isAlive())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                viewTreeObserver.removeOnGlobalLayoutListener(mGlobalLayoutListener);
            }
            else
            {
                //noinspection deprecation
                viewTreeObserver.removeGlobalOnLayoutListener(mGlobalLayoutListener);
            }
        }
    }

    /**
     * Removes the prompt from view, using a expand and fade animation.
     * <p>
     * This is treated as if the user has touched the target focal point.
     */
    public void finish()
    {
        if (mDismissing)
        {
            return;
        }
        onPromptStateChanged(STATE_FINISHING);
        mDismissing = true;
        if (mAnimationCurrent != null)
        {
            mAnimationCurrent.removeAllListeners();
            mAnimationCurrent.cancel();
            mAnimationCurrent = null;
        }
        mAnimationCurrent = ValueAnimator.ofFloat(1f, 0f);
        mAnimationCurrent.setDuration(225);
        mAnimationCurrent.setInterpolator(mAnimationInterpolator);
        mAnimationCurrent.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                final float value = (float) animation.getAnimatedValue();
                updateAnimation(1f + ((1f - value) / 4), value);
            }
        });
        mAnimationCurrent.addListener(new AnimatorListener()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                cleanUpPrompt(STATE_FINISHED);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                cleanUpPrompt(STATE_FINISHED);
            }
        });
        mAnimationCurrent.start();
    }

    /**
     * Removes the prompt from view, using a contract and fade animation.
     * <p>
     * This is treated as if the user has touched outside the target focal point.
     */
    public void dismiss()
    {
        if (mDismissing)
        {
            return;
        }
        onPromptStateChanged(STATE_DISMISSING);
        mDismissing = true;
        if (mAnimationCurrent != null)
        {
            mAnimationCurrent.removeAllListeners();
            mAnimationCurrent.cancel();
            mAnimationCurrent = null;
        }
        mAnimationCurrent = ValueAnimator.ofFloat(1f, 0f);
        mAnimationCurrent.setDuration(225);
        mAnimationCurrent.setInterpolator(mAnimationInterpolator);
        mAnimationCurrent.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                final float value = (float) animation.getAnimatedValue();
                updateAnimation(value, value);
            }
        });
        mAnimationCurrent.addListener(new AnimatorListener()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                cleanUpPrompt(STATE_DISMISSED);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                cleanUpPrompt(STATE_DISMISSED);
            }
        });
        mAnimationCurrent.start();
    }

    /**
     * Removes the prompt from view and triggers the {@link #onPromptStateChanged(int)} event.
     */
    void cleanUpPrompt(final int state)
    {
        if (mAnimationCurrent != null)
        {
            mAnimationCurrent.removeAllUpdateListeners();
            mAnimationCurrent = null;
        }
        removeGlobalLayoutListener();
        mParentView.removeView(mView);
        if (mDismissing)
        {
            onPromptStateChanged(state);
            mDismissing = false;
        }
    }

    void startRevealAnimation()
    {
        updateAnimation(0, 0);
        mAnimationCurrent = ValueAnimator.ofFloat(0f, 1f);
        mAnimationCurrent.setInterpolator(mAnimationInterpolator);
        mAnimationCurrent.setDuration(225);
        mAnimationCurrent.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                final float value = (float) animation.getAnimatedValue();
                updateAnimation(value, value);
            }
        });
        mAnimationCurrent.addListener(new AnimatorListener()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                animation.removeAllListeners();
                mAnimationCurrent = null;
                updateAnimation(1, 1);
                if (mIdleAnimationEnabled)
                {
                    startIdleAnimations();
                }
                onPromptStateChanged(STATE_REVEALED);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                animation.removeAllListeners();
                updateAnimation(1, 1);
                mAnimationCurrent = null;
            }
        });
        mAnimationCurrent.start();
    }

    void startIdleAnimations()
    {
        if (mAnimationCurrent != null)
        {
            mAnimationCurrent.removeAllUpdateListeners();
            mAnimationCurrent.cancel();
            mAnimationCurrent = null;
        }
        mAnimationCurrent = ValueAnimator.ofFloat(0, mFocalRadius10Percent, 0);
        mAnimationCurrent.setInterpolator(mAnimationInterpolator);
        mAnimationCurrent.setDuration(1000);
        mAnimationCurrent.setStartDelay(225);
        mAnimationCurrent.setRepeatCount(ValueAnimator.INFINITE);
        mAnimationCurrent.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            boolean direction = true;

            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                final float newFocalFraction = (Float) animation.getAnimatedValue();
                boolean newDirection = direction;
                if (newFocalFraction < mFocalRippleProgress && direction)
                {
                    newDirection = false;
                }
                else if (newFocalFraction > mFocalRippleProgress && !direction)
                {
                    newDirection = true;
                }
                if (newDirection != direction && !newDirection)
                {
                    mAnimationFocalRipple.start();
                }
                direction = newDirection;
                mFocalRippleProgress = newFocalFraction;
                mView.mFocalRadius = mBaseFocalRadius + mFocalRippleProgress;
                mView.invalidate();
            }
        });
        mAnimationCurrent.start();
        if (mAnimationFocalRipple != null)
        {
            mAnimationFocalRipple.removeAllUpdateListeners();
            mAnimationFocalRipple.cancel();
            mAnimationFocalRipple = null;
        }
        final float baseRadius = mBaseFocalRadius + mFocalRadius10Percent;
        mAnimationFocalRipple = ValueAnimator.ofFloat(baseRadius, baseRadius + (mFocalRadius10Percent * 6));
        mAnimationFocalRipple.setInterpolator(mAnimationInterpolator);
        mAnimationFocalRipple.setDuration(500);
        mAnimationFocalRipple.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mView.mFocalRippleSize = (float) animation.getAnimatedValue();
                mView.mFocalRippleAlpha = (int) (mBaseFocalRippleAlpha * (1f - animation.getAnimatedFraction()));
            }
        });
    }

    /**
     * Updates the positioning and alpha values using the animation values.
     *
     * @param revealAmount
     * @param alphaValue
     */
    void updateAnimation(final float revealAmount, final float alphaValue)
    {
        mRevealedAmount = revealAmount;
        mAlphaModifier = alphaValue;
        mView.mBackgroundRadius = mBaseBackgroundRadius * mRevealedAmount;
        mView.mFocalRadius = mBaseFocalRadius * mRevealedAmount;
        mView.mPaintFocal.setAlpha((int) (mBaseFocalColourAlpha * mAlphaModifier));
        mView.mPaintBackground.setAlpha((int) (mBaseBackgroundColourAlpha * mAlphaModifier));
        createTextLayout(calculateMaxWidth());
        if (mView.mIconDrawable != null)
        {
            mView.mIconDrawable.setAlpha(mView.mPaintBackground.getAlpha());
        }
        mView.mBackgroundPosition.set(mView.mFocalCentre.x + ((mBaseBackgroundPosition.x - mView.mFocalCentre.x) * mRevealedAmount),
                mView.mFocalCentre.y + ((mBaseBackgroundPosition.y - mView.mFocalCentre.y) * mRevealedAmount));
        mView.invalidate();
    }

    void updateFocalCentrePosition()
    {
        updateClipBounds();
        if (mTargetView != null)
        {
            final int[] viewPosition = new int[2];
            mView.getLocationInWindow(viewPosition);
            final int[] targetPosition = new int[2];
            mTargetView.getLocationInWindow(targetPosition);

            mView.mFocalCentre.x = targetPosition[0] - viewPosition[0] + (mTargetView.getWidth() / 2);
            mView.mFocalCentre.y = targetPosition[1] - viewPosition[1] + (mTargetView.getHeight() / 2);
        }
        else
        {
            mView.mFocalCentre.x = mTargetPosition.x;
            mView.mFocalCentre.y = mTargetPosition.y;
        }

        mVerticalTextPositionAbove = mView.mFocalCentre.y > mView.mClipBounds.centerY();
        mHorizontalTextPositionLeft = mView.mFocalCentre.x > mView.mClipBounds.centerX();
        mInside88dpBounds = (mView.mFocalCentre.x > mClipViewBoundsInset88dp.left && mView.mFocalCentre.x < mClipViewBoundsInset88dp.right) || (mView.mFocalCentre.y > mClipViewBoundsInset88dp.top && mView.mFocalCentre.y < mClipViewBoundsInset88dp.bottom);

        updateTextPositioning();
        updateIconPosition();
    }

    /**
     * Calculates the maximum width that the prompt can be.
     *
     * @return Maximum width in pixels that the prompt can be.
     */
    float calculateMaxWidth()
    {
        return Math.max(80, Math.min(mMaxTextWidth, (mView.mClipToBounds ? mView.mClipBounds.right - mView.mClipBounds.left : mParentView.getWidth()) - (mTextPadding * 2)));
    }

    /**
     * Creates the text layouts for the primary and secondary text.
     *
     * @param maxWidth The maximum width that the text can be.
     */
    void createTextLayout(final float maxWidth)
    {
        if (mPrimaryText != null)
        {
            mView.mPrimaryTextLayout = createStaticTextLayout(mPrimaryText, mPaintPrimaryText,
                    (int) maxWidth, mPrimaryTextAlignment);
        }
        else
        {
            mView.mPrimaryTextLayout = null;
        }
        if (mSecondaryText != null)
        {
            mView.mSecondaryTextLayout = createStaticTextLayout(mSecondaryText, mPaintSecondaryText, (int) maxWidth, mSecondaryTextAlignment);
        }
        else
        {
            mView.mSecondaryTextLayout = null;
        }
    }

    /**
     * Recalculates the primary and secondary text positions.
     */
    void updateTextPositioning()
    {
        final float maxWidth = calculateMaxWidth();
        createTextLayout(maxWidth);
        final float primaryTextWidth = calculateMaxTextWidth(mView.mPrimaryTextLayout);
        final float secondaryTextWidth = calculateMaxTextWidth(mView.mSecondaryTextLayout);
        final float textWidth = Math.max(primaryTextWidth, secondaryTextWidth);

        if (mInside88dpBounds)
        {
            mView.mPrimaryTextLeft = mView.mClipBounds.left;
            final float width = Math.min(textWidth, maxWidth);
            if (mHorizontalTextPositionLeft)
            {
                mView.mPrimaryTextLeft = mView.mFocalCentre.x - width + mFocalToTextPadding;
            }
            else
            {
                mView.mPrimaryTextLeft = mView.mFocalCentre.x - width - mFocalToTextPadding;
            }
            if (mView.mPrimaryTextLeft < mView.mClipBounds.left + mTextPadding)
            {
                mView.mPrimaryTextLeft = mView.mClipBounds.left + mTextPadding;
            }
            if (mView.mPrimaryTextLeft + width > mView.mClipBounds.right - mTextPadding)
            {
                mView.mPrimaryTextLeft = mView.mClipBounds.right - mTextPadding - width;
            }
        }
        else
        {
            if (mHorizontalTextPositionLeft)
            {
                mView.mPrimaryTextLeft = (mView.mClipToBounds ? mView.mClipBounds.right : mParentView.getRight()) - mTextPadding - textWidth;
            }
            else
            {
                mView.mPrimaryTextLeft = (mView.mClipToBounds ? mView.mClipBounds.left : mParentView.getLeft()) + mTextPadding;
            }
        }

        mView.mPrimaryTextTop = mView.mFocalCentre.y;
        if (mVerticalTextPositionAbove)
        {
            mView.mPrimaryTextTop = mView.mPrimaryTextTop - mBaseFocalRadius - mFocalToTextPadding;
            if (mView.mPrimaryTextLayout != null)
            {
                mView.mPrimaryTextTop -= mView.mPrimaryTextLayout.getHeight();
            }
        }
        else
        {
            mView.mPrimaryTextTop += mBaseFocalRadius + mFocalToTextPadding;
        }

        if (mSecondaryText != null)
        {
            if (mVerticalTextPositionAbove)
            {
                mView.mPrimaryTextTop = mView.mPrimaryTextTop - mView.mTextSeparation - mView.mSecondaryTextLayout.getHeight();
            }

            if (mView.mPrimaryTextLayout != null)
            {
                mView.mSecondaryTextOffsetTop = mView.mPrimaryTextLayout.getHeight() + mView.mTextSeparation;
            }
        }

        updateBackgroundRadius(textWidth);

        mView.mSecondaryTextLeft = mView.mPrimaryTextLeft;
        mView.mPrimaryTextLeftChange = 0;
        mView.mSecondaryTextLeftChange = 0;
        final float change = maxWidth - textWidth;
        if (isRtlText(mView.mPrimaryTextLayout))
        {
            mView.mPrimaryTextLeftChange = change;
        }
        if (isRtlText(mView.mSecondaryTextLayout))
        {
            mView.mSecondaryTextLeftChange = change;
        }
    }

    /**
     * Determines if the text in the supplied layout is displayed right to left.
     *
     * @param layout The layout to check.
     * @return True if the text in the supplied layout is displayed right to left. False otherwise.
     */
    private boolean isRtlText(final Layout layout)
    {
        boolean result = false;
        if (layout != null)
        {
            // Treat align opposite as right to left by default
            result = layout.getAlignment() == Layout.Alignment.ALIGN_OPPOSITE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            {
                // If the first character is a right to left character
                final boolean textIsRtl = layout.isRtlCharAt(0);
                // If the text and result are right to left then false otherwise use the textIsRtl value
                result = (!(result && textIsRtl) && !(!result && !textIsRtl)) || textIsRtl;
                if (!result && layout.getAlignment() == Layout.Alignment.ALIGN_NORMAL
                        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                {
                    // If the layout and text are right to left and the alignment is normal then rtl
                    result = textIsRtl && mResourceFinder.getResources().getConfiguration()
                            .getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
                }
                else if (layout.getAlignment() == Layout.Alignment.ALIGN_OPPOSITE && textIsRtl)
                {
                    result = false;
                }
            }
        }
        return result;
    }

    /**
     * Creates a static text layout. Uses the {@link android.text.StaticLayout.Builder} if
     * available.
     *
     * @param text          The text to be laid out, optionally with spans
     * @param paint         The base paint used for layout
     * @param maxTextWidth  The width in pixels
     * @param textAlignment Alignment for the resulting {@link StaticLayout}
     * @return the newly constructed {@link StaticLayout} object
     */
    private StaticLayout createStaticTextLayout(final CharSequence text, final TextPaint paint,
                                                final int maxTextWidth,
                                                final Layout.Alignment textAlignment)
    {
        final SpannableStringBuilder wrappedText = new SpannableStringBuilder(text);
        wrappedText.setSpan(new AlphaSpan(mAlphaModifier), 0, wrappedText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        final StaticLayout layout;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            final StaticLayout.Builder builder = StaticLayout.Builder.obtain(wrappedText, 0, text.length(), paint, maxTextWidth);
            builder.setAlignment(textAlignment);
            layout = builder.build();
        }
        else
        {
            layout = new StaticLayout(wrappedText, paint, maxTextWidth, textAlignment, 1f, 0f, false);
        }
        return layout;
    }

    /**
     * Calculates the maximum width line in a text layout.
     *
     * @param textLayout The text layout
     * @return The maximum length line
     */
    float calculateMaxTextWidth(final Layout textLayout)
    {
        float maxTextWidth = 0f;
        if (textLayout != null)
        {
            for (int i = 0, count = textLayout.getLineCount(); i < count; i++)
            {
                maxTextWidth = Math.max(maxTextWidth, textLayout.getLineWidth(i));
            }
        }
        return maxTextWidth;
    }

    /**
     * Updates the background position and radius.
     *
     * @param maxTextWidth The maximum width for the displayed text.
     */
    void updateBackgroundRadius(final float maxTextWidth)
    {
        /*mView.textWidth = (int) maxTextWidth;
        mView.padding = (int) mTextPadding;*/
        if (mInside88dpBounds)
        {
            float x1 = mView.mFocalCentre.x;
            float x2 = mView.mPrimaryTextLeft - mTextPadding;
            float y1, y2;
            if (mVerticalTextPositionAbove)
            {
                y1 = mView.mFocalCentre.y + mBaseFocalRadius + mTextPadding;
                y2 = mView.mPrimaryTextTop;
            }
            else
            {
                y1 = mView.mFocalCentre.y - (mBaseFocalRadius + mFocalToTextPadding + mTextPadding);
                float baseY2 = mView.mPrimaryTextTop + mView.mPrimaryTextLayout.getHeight();
                if (mView.mSecondaryTextLayout != null)
                {
                    baseY2 += mView.mSecondaryTextLayout.getHeight() + mView.mTextSeparation;
                }
                y2 = baseY2;
            }

            final float y3 = y2;
            float x3 = x2 + maxTextWidth + mTextPadding + mTextPadding;

            final float focalLeft = mView.mFocalCentre.x - mBaseFocalRadius - mFocalToTextPadding;
            final float focalRight = mView.mFocalCentre.x + mBaseFocalRadius + mFocalToTextPadding;
            if (x2 > focalLeft && x2 < focalRight)
            {
                if (mVerticalTextPositionAbove)
                {
                    x1 -= mBaseFocalRadius - mFocalToTextPadding;
                }
                else
                {
                    x2 -= mBaseFocalRadius - mFocalToTextPadding;
                }
            }
            else if (x3 > focalLeft && x3 < focalRight)
            {
                if (mVerticalTextPositionAbove)
                {
                    x1 += mBaseFocalRadius + mFocalToTextPadding;
                }
                else
                {
                    x3 += mBaseFocalRadius + mFocalToTextPadding;
                }
            }

            final double offset = Math.pow(x2, 2) + Math.pow(y2, 2);
            final double bc = (Math.pow(x1, 2) + Math.pow(y1, 2) - offset) / 2.0;
            final double cd = (offset - Math.pow(x3, 2) - Math.pow(y3, 2)) / 2.0;
            final double det = (x1 - x2) * (y2 - y3) - (x2 - x3) * (y1 - y2);
            final double idet = 1 / det;
            mBaseBackgroundPosition.set((float) ((bc * (y2 - y3) - cd * (y1 - y2)) * idet),
                    (float) ((cd * (x1 - x2) - bc * (x2 - x3)) * idet));
            mBaseBackgroundRadius = (float) Math.sqrt(Math.pow(x2 - mBaseBackgroundPosition.x, 2)
                    + Math.pow(y2 - mBaseBackgroundPosition.y, 2));
            /*mView.point1.set(x1, y1);
            mView.point2.set(x2, y2);
            mView.point3.set(x3, y3);*/
        }
        else
        {
            mBaseBackgroundPosition.set(mView.mFocalCentre.x, mView.mFocalCentre.y);
            final float length = Math.abs(mView.mPrimaryTextLeft
                    + (mHorizontalTextPositionLeft ? 0 : maxTextWidth)
                    - mView.mFocalCentre.x) + mTextPadding;
            float height = mBaseFocalRadius + mFocalToTextPadding;
            if (mView.mPrimaryTextLayout != null)
            {
                height += mView.mPrimaryTextLayout.getHeight();
            }
            //Check if secondary text should be included with text separation
            if (mView.mSecondaryTextLayout != null)
            {
                height += mView.mSecondaryTextLayout.getHeight() + mView.mTextSeparation;
            }
            mBaseBackgroundRadius = (float) Math.sqrt(Math.pow(length, 2) + Math.pow(height, 2));
            /*mView.point1.set(mView.mFocalCentre.x + (mHorizontalTextPositionLeft ? -length : length),
                            mView.mFocalCentre.y + (mVerticalTextPositionAbove ? - height : height));*/
        }
        mView.mBackgroundPosition.set(mBaseBackgroundPosition);
        mView.mBackgroundRadius = mBaseBackgroundRadius * mRevealedAmount;
    }

    void updateIconPosition()
    {
        if (mView.mIconDrawable != null)
        {
            mView.mIconDrawableLeft = mView.mFocalCentre.x - (mView.mIconDrawable.getIntrinsicWidth() / 2);
            mView.mIconDrawableTop = mView.mFocalCentre.y - (mView.mIconDrawable.getIntrinsicHeight() / 2);
        }
        else if (mView.mTargetRenderView != null)
        {
            final int[] viewPosition = new int[2];
            mView.getLocationInWindow(viewPosition);
            final int[] targetPosition = new int[2];
            mView.mTargetRenderView.getLocationInWindow(targetPosition);

            mView.mIconDrawableLeft = targetPosition[0] - viewPosition[0];
            mView.mIconDrawableTop = targetPosition[1] - viewPosition[1];
        }
    }

    void updateClipBounds()
    {
        if (mClipToView != null)
        {
            mView.mClipToBounds = true;

            //Reset the top to 0
            mView.mClipBounds.set(0, 0, 0, 0);

            //Find the location of the clip view on the screen
            final Point offset = new Point();
            mClipToView.getGlobalVisibleRect(mView.mClipBounds, offset);

            if (offset.y == 0)
            {
                mView.mClipBounds.top += mStatusBarHeight;
            }

            mClipViewBoundsInset88dp = new RectF(mView.mClipBounds);
            mClipViewBoundsInset88dp.inset(m88dp, m88dp);
        }
        else
        {
            final View contentView = mResourceFinder.findViewById(android.R.id.content);
            if (contentView != null)
            {
                contentView.getGlobalVisibleRect(mView.mClipBounds, new Point());
                mClipViewBoundsInset88dp = new RectF(mView.mClipBounds);
                mClipViewBoundsInset88dp.inset(m88dp, m88dp);
            }
            mView.mClipToBounds = false;
        }
    }

    /**
     * Handles emitting the prompt state changed events.
     *
     * @param state The state that the prompt is now in.
     */
    protected void onPromptStateChanged(final int state)
    {
        if (mPromptStateChangeListener != null)
        {
            mPromptStateChangeListener.onPromptStateChanged(this, state);
        }
    }

    /**
     * {@link CharacterStyle} class the modifies the painted foreground and background alpha values.
     */
    private static class AlphaSpan extends CharacterStyle
    {
        /**
         * The alpha modification value between 1 and 0.
         */
        private final float mValue;

        /**
         * Constructor.
         *
         * @param value The alpha modification value between 1 and 0.
         */
        AlphaSpan(final float value)
        {
            mValue = value;
        }

        @Override
        public void updateDrawState(TextPaint paint)
        {
            paint.setAlpha((int) (paint.getAlpha() * mValue));
            paint.bgColor = Color.argb((int) (Color.alpha(paint.bgColor) * mValue),
                    Color.red(paint.bgColor), Color.green(paint.bgColor), Color.blue(paint.bgColor));
        }
    }

    /**
     * View used to render the tap target.
     */
    static class PromptView extends View
    {
        /*int padding, textWidth;
        Paint paddingPaint = new Paint();
        Paint itemPaint = new Paint();
        PointF point1 = new PointF();
        PointF point2 = new PointF();
        PointF point3 = new PointF();*/
        PointF mFocalCentre = new PointF();
        PointF mBackgroundPosition = new PointF();
        Paint mPaintBackground, mPaintFocal;
        float mFocalRadius, mBackgroundRadius;
        float mFocalRippleSize;
        int mFocalRippleAlpha;
        Drawable mIconDrawable;
        float mIconDrawableLeft;
        float mIconDrawableTop;
        float mPrimaryTextLeft;
        float mPrimaryTextLeftChange;
        float mPrimaryTextTop;
        float mSecondaryTextLeft;
        float mSecondaryTextLeftChange;
        float mSecondaryTextOffsetTop;
        Layout mPrimaryTextLayout;
        Layout mSecondaryTextLayout;
        boolean mDrawRipple = true;
        PromptTouchedListener mPromptTouchedListener;
        boolean mCaptureTouchEventOnFocal;
        Rect mClipBounds = new Rect();
        View mTargetView, mTargetRenderView;
        float mTextSeparation;
        boolean mClipToBounds;
        boolean mCaptureTouchEventOutsidePrompt;

        /**
         * Should the back button press dismiss the prompt.
         */
        boolean mBackButtonDismissEnabled;
        /**
         * @see MaterialTapTargetPrompt#mAutoDismiss
         */
        boolean mAutoDismiss;

        public PromptView(final Context context)
        {
            super(context);
            setId(R.id.material_target_prompt_view);
            setFocusableInTouchMode(true);
            requestFocus();
            /*paddingPaint.setColor(Color.GREEN);
            paddingPaint.setAlpha(100);
            itemPaint.setColor(Color.BLUE);
            itemPaint.setAlpha(100);*/
        }

        @Override
        public void onDraw(final Canvas canvas)
        {
            if (mBackgroundRadius > 0)
            {
                if (mClipToBounds)
                {
                    canvas.clipRect(mClipBounds);
                }

                //Draw the backgrounds
                canvas.drawCircle(mBackgroundPosition.x, mBackgroundPosition.y, mBackgroundRadius, mPaintBackground);
                //Draw the ripple
                if (mDrawRipple)
                {
                    final int oldAlpha = mPaintFocal.getAlpha();
                    mPaintFocal.setAlpha(mFocalRippleAlpha);
                    canvas.drawCircle(mFocalCentre.x, mFocalCentre.y, mFocalRippleSize, mPaintFocal);
                    mPaintFocal.setAlpha(oldAlpha);
                }
                //Draw the focal
                canvas.drawCircle(mFocalCentre.x, mFocalCentre.y, mFocalRadius, mPaintFocal);

                /*canvas.drawRect(mPrimaryTextLeft - padding, mPrimaryTextTop, mPrimaryTextLeft, mPrimaryTextTop + mSecondaryTextOffsetTop + mSecondaryTextLayout.getHeight(), paddingPaint);
                canvas.drawRect(mPrimaryTextLeft, mPrimaryTextTop, mPrimaryTextLeft + textWidth, mPrimaryTextTop + mSecondaryTextOffsetTop + mSecondaryTextLayout.getHeight(), itemPaint);
                canvas.drawRect(mPrimaryTextLeft + textWidth, mPrimaryTextTop, mPrimaryTextLeft + textWidth + padding, mPrimaryTextTop + mSecondaryTextOffsetTop + mSecondaryTextLayout.getHeight(), paddingPaint);*/

                //Draw the icon
                if (mIconDrawable != null)
                {
                    canvas.translate(mIconDrawableLeft, mIconDrawableTop);
                    mIconDrawable.draw(canvas);
                    canvas.translate(-mIconDrawableLeft, -mIconDrawableTop);
                }
                else if (mTargetRenderView != null)
                {
                    canvas.translate(mIconDrawableLeft, mIconDrawableTop);
                    mTargetRenderView.draw(canvas);
                    canvas.translate(-mIconDrawableLeft, -mIconDrawableTop);
                }

                /*canvas.drawCircle(point1.x, point1.y, 50, mPaintFocal);
                canvas.drawCircle(point2.x, point2.y, 50, mPaintFocal);
                canvas.drawCircle(point3.x, point3.y, 50, mPaintFocal);*/

                //Draw the text
                canvas.translate(mPrimaryTextLeft - mPrimaryTextLeftChange, mPrimaryTextTop);
                if (mPrimaryTextLayout != null)
                {
                    mPrimaryTextLayout.draw(canvas);
                }
                if (mSecondaryTextLayout != null)
                {
                    canvas.translate(-(mPrimaryTextLeft - mPrimaryTextLeftChange)
                            + mSecondaryTextLeft - mSecondaryTextLeftChange, mSecondaryTextOffsetTop);
                    mSecondaryTextLayout.draw(canvas);
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {
            final float x = event.getX();
            final float y = event.getY();
            //If the touch point is within the prompt background stop the event from passing through it
            boolean captureEvent = (!mClipToBounds || mClipBounds.contains((int) x, (int) y))
                    && pointInCircle(x, y, mBackgroundPosition, mBackgroundRadius);
            //If the touch event was at least in the background and in the focal
            if (captureEvent && pointInCircle(x, y, mFocalCentre, mFocalRadius))
            {
                //Override allowing the touch event to pass through the view with the user defined value
                captureEvent = mCaptureTouchEventOnFocal;
                if (mPromptTouchedListener != null)
                {
                    mPromptTouchedListener.onFocalPressed();
                }
            }
            else
            {
                // If the prompt background was not touched
                if (!captureEvent)
                {
                    captureEvent = mCaptureTouchEventOutsidePrompt;
                }
                if (mPromptTouchedListener != null)
                {
                    mPromptTouchedListener.onNonFocalPressed();
                }
            }
            return captureEvent;
        }

        @Override
        public boolean dispatchKeyEventPreIme(KeyEvent event)
        {
            if (mBackButtonDismissEnabled && event.getKeyCode() == KeyEvent.KEYCODE_BACK)
            {
                KeyEvent.DispatcherState state = getKeyDispatcherState();
                if (state != null)
                {
                    if (event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getRepeatCount() == 0)
                    {
                        state.startTracking(event, this);
                        return true;
                    }
                    else if (event.getAction() == KeyEvent.ACTION_UP
                            && !event.isCanceled() && state.isTracking(event))
                    {
                        if (mPromptTouchedListener != null)
                        {
                            mPromptTouchedListener.onNonFocalPressed();
                        }
                        return mAutoDismiss || super.dispatchKeyEventPreIme(event);
                    }
                }
            }

            return super.dispatchKeyEventPreIme(event);
        }

        /**
         * Determines if a point is in the centre of a circle with a radius from the point ({@link
         * #mFocalCentre , {@link #mFocalCentre.y}}.
         *
         * @param x            The x position in the view.
         * @param y            The y position in the view.
         * @param circleCentre The circle centre position
         * @param radius       The radius of the circle.
         * @return True if the point (x, y) is in the circle.
         */
        boolean pointInCircle(final float x, final float y, final PointF circleCentre,
                              final float radius)
        {
            return Math.pow(x - circleCentre.x, 2) + Math.pow(y - circleCentre.y, 2) < Math.pow(radius, 2);
        }

        /**
         * Interface definition for a callback to be invoked when a {@link PromptView} is touched.
         */
        public interface PromptTouchedListener
        {
            /**
             * Called when the focal is pressed.
             */
            void onFocalPressed();

            /**
             * Called when anywhere outside the focal is pressed or the system back button is
             * pressed.
             */
            void onNonFocalPressed();
        }
    }

    /**
     * A builder to create a {@link MaterialTapTargetPrompt} instance.
     */
    public static class Builder
    {
        /**
         * Used to find the required resources to display the prompt.
         */
        final ResourceFinder mResourceFinder;

        /**
         * Has the target been set successfully.
         */
        private boolean mTargetSet;

        /**
         * The view to place the prompt around.
         */
        private View mTargetView;

        /**
         * The left and top positioning for the focal centre point.
         */
        private PointF mTargetPosition;

        /**
         * The text to display.
         */
        private CharSequence mPrimaryText, mSecondaryText;
        private int mPrimaryTextColour, mSecondaryTextColour, mBackgroundColour, mFocalColour;

        private float mFocalRadius;
        private float mPrimaryTextSize, mSecondaryTextSize;
        private float mMaxTextWidth;
        private float mTextPadding;
        private float mFocalToTextPadding;
        private Interpolator mAnimationInterpolator;
        private Drawable mIconDrawable;

        /**
         * Should the back button press dismiss the prompt.
         */
        private boolean mBackButtonDismissEnabled = true;

        /**
         * Listener for when the prompt state changes.
         */
        private PromptStateChangeListener mPromptStateChangeListener;

        private boolean mCaptureTouchEventOnFocal;
        private float mTextSeparation;
        private boolean mAutoDismiss, mAutoFinish;
        private boolean mCaptureTouchEventOutsidePrompt;
        private Typeface mPrimaryTextTypeface, mSecondaryTextTypeface;
        private int mPrimaryTextTypefaceStyle, mSecondaryTextTypefaceStyle;
        private ColorStateList mIconDrawableTintList = null;
        private PorterDuff.Mode mIconDrawableTintMode = null;
        private boolean mHasIconDrawableTint;
        private int mIconDrawableColourFilter;
        private View mTargetRenderView;
        private boolean mIdleAnimationEnabled = true;
        private int mPrimaryTextGravity = Gravity.START, mSecondaryTextGravity = Gravity.START;
        private View mClipToView;
        private float m88dp;

        /**
         * Creates a builder for a tap target prompt that uses the default tap target prompt theme.
         *
         * @param fragment the fragment to show the prompt within.
         */
        public Builder(final Fragment fragment)
        {
            this(fragment.getActivity(), 0);
        }

        /**
         * Creates a builder for a material tap target prompt that uses an explicit theme resource.
         * <p>
         * The {@code themeResId} may be specified as {@code 0} to use the parent {@code context}'s
         * resolved value for {@link R.attr#MaterialTapTargetPromptTheme}.
         *
         * @param fragment   the fragment to show the prompt within.
         * @param themeResId the resource ID of the theme against which to inflate this dialog, or
         *                   {@code 0} to use the parent {@code context}'s default material tap
         *                   target prompt theme
         */
        public Builder(final Fragment fragment, int themeResId)
        {
            this(fragment.getActivity(), themeResId);
        }

        /**
         * Creates a builder for a tap target prompt that uses the default tap target prompt theme.
         *
         * @param dialogFragment the dialog fragment to show the prompt within.
         */
        public Builder(final DialogFragment dialogFragment)
        {
            this(dialogFragment, 0);
        }

        /**
         * Creates a builder for a material tap target prompt that uses an explicit theme resource.
         * <p>
         * The {@code themeResId} may be specified as {@code 0} to use the parent {@code context}'s
         * resolved value for {@link R.attr#MaterialTapTargetPromptTheme}.
         *
         * @param dialogFragment the dialog fragment to show the prompt within.
         * @param themeResId     the resource ID of the theme against which to inflate this dialog,
         *                       or {@code 0} to use the parent {@code context}'s default material
         *                       tap target prompt theme
         */
        public Builder(final DialogFragment dialogFragment, int themeResId)
        {
            this(dialogFragment.getDialog(), themeResId);
        }

        /**
         * Creates a builder for a tap target prompt that uses the default tap target prompt theme.
         *
         * @param dialog the dialog to show the prompt within.
         */
        public Builder(final Dialog dialog)
        {
            this(dialog, 0);
        }

        /**
         * Creates a builder for a material tap target prompt that uses an explicit theme resource.
         * <p>
         * The {@code themeResId} may be specified as {@code 0} to use the parent {@code context}'s
         * resolved value for {@link R.attr#MaterialTapTargetPromptTheme}.
         *
         * @param dialog     the dialog to show the prompt within.
         * @param themeResId the resource ID of the theme against which to inflate this dialog, or
         *                   {@code 0} to use the parent {@code context}'s default material tap
         *                   target prompt theme
         */
        public Builder(final Dialog dialog, int themeResId)
        {
            this(new DialogResourceFinder(dialog), themeResId);
        }

        /**
         * Creates a builder for a tap target prompt that uses the default tap target prompt theme.
         *
         * @param activity the activity to show the prompt within.
         */
        public Builder(final Activity activity)
        {
            this(activity, 0);
        }

        /**
         * Creates a builder for a material tap target prompt that uses an explicit theme resource.
         * <p>
         * The {@code themeResId} may be specified as {@code 0} to use the parent {@code context}'s
         * resolved value for {@link R.attr#MaterialTapTargetPromptTheme}.
         *
         * @param activity   the activity to show the prompt within.
         * @param themeResId the resource ID of the theme against which to inflate this dialog, or
         *                   {@code 0} to use the parent {@code context}'s default material tap
         *                   target prompt theme
         */
        public Builder(final Activity activity, int themeResId)
        {
            this(new ActivityResourceFinder(activity), themeResId);
        }

        /**
         * Creates a builder for a material tap target prompt that uses an explicit theme resource.
         * <p>
         * The {@code themeResId} may be specified as {@code 0} to use the parent {@code context}'s
         * resolved value for {@link R.attr#MaterialTapTargetPromptTheme}.
         *
         * @param resourceFinder The {@link ResourceFinder} used to find views and resources.
         * @param themeResId     the resource ID of the theme against which to inflate this dialog,
         *                       or {@code 0} to use the parent {@code context}'s default material
         *                       tap target prompt theme
         */
        public Builder(final ResourceFinder resourceFinder, int themeResId)
        {
            mResourceFinder = resourceFinder;
            //Attempt to load the theme from the activity theme
            if (themeResId == 0)
            {
                final TypedValue outValue = new TypedValue();
                mResourceFinder.getTheme().resolveAttribute(R.attr.MaterialTapTargetPromptTheme, outValue, true);
                themeResId = outValue.resourceId;
            }

            final float density = mResourceFinder.getResources().getDisplayMetrics().density;
            m88dp = 88 * density;
            final TypedArray a = mResourceFinder.obtainStyledAttributes(themeResId, R.styleable.PromptView);
            mPrimaryTextColour = a.getColor(R.styleable.PromptView_mttp_primaryTextColour, Color.WHITE);
            mSecondaryTextColour = a.getColor(R.styleable.PromptView_mttp_secondaryTextColour, Color.argb(179, 255, 255, 255));
            mPrimaryText = a.getString(R.styleable.PromptView_mttp_primaryText);
            mSecondaryText = a.getString(R.styleable.PromptView_mttp_secondaryText);
            mBackgroundColour = a.getColor(R.styleable.PromptView_mttp_backgroundColour, Color.argb(244, 63, 81, 181));
            mFocalColour = a.getColor(R.styleable.PromptView_mttp_focalColour, Color.WHITE);
            mFocalRadius = a.getDimension(R.styleable.PromptView_mttp_focalRadius, density * 44);
            mPrimaryTextSize = a.getDimension(R.styleable.PromptView_mttp_primaryTextSize, 22 * density);
            mSecondaryTextSize = a.getDimension(R.styleable.PromptView_mttp_secondaryTextSize, 18 * density);
            mMaxTextWidth = a.getDimension(R.styleable.PromptView_mttp_maxTextWidth, 400 * density);
            mTextPadding = a.getDimension(R.styleable.PromptView_mttp_textPadding, 40 * density);
            mFocalToTextPadding = a.getDimension(R.styleable.PromptView_mttp_focalToTextPadding, 20 * density);
            mTextSeparation = a.getDimension(R.styleable.PromptView_mttp_textSeparation, 16 * density);
            mAutoDismiss = a.getBoolean(R.styleable.PromptView_mttp_autoDismiss, true);
            mAutoFinish = a.getBoolean(R.styleable.PromptView_mttp_autoFinish, true);
            mCaptureTouchEventOutsidePrompt = a.getBoolean(R.styleable.PromptView_mttp_captureTouchEventOutsidePrompt, false);
            mCaptureTouchEventOnFocal = a.getBoolean(R.styleable.PromptView_mttp_captureTouchEventOnFocal, false);
            mPrimaryTextTypefaceStyle = a.getInt(R.styleable.PromptView_mttp_primaryTextStyle, 0);
            mSecondaryTextTypefaceStyle = a.getInt(R.styleable.PromptView_mttp_secondaryTextStyle, 0);
            mPrimaryTextTypeface = setTypefaceFromAttrs(a.getString(R.styleable.PromptView_mttp_primaryTextFontFamily), a.getInt(R.styleable.PromptView_mttp_primaryTextTypeface, 0), mPrimaryTextTypefaceStyle);
            mSecondaryTextTypeface = setTypefaceFromAttrs(a.getString(R.styleable.PromptView_mttp_secondaryTextFontFamily), a.getInt(R.styleable.PromptView_mttp_secondaryTextTypeface, 0), mSecondaryTextTypefaceStyle);

            mIconDrawableColourFilter = a.getColor(R.styleable.PromptView_mttp_iconColourFilter, mBackgroundColour);
            mIconDrawableTintList = a.getColorStateList(R.styleable.PromptView_mttp_iconTint);
            mIconDrawableTintMode = parseTintMode(a.getInt(R.styleable.PromptView_mttp_iconTintMode, -1), PorterDuff.Mode.MULTIPLY);
            mHasIconDrawableTint = true;

            final int targetId = a.getResourceId(R.styleable.PromptView_mttp_target, 0);
            a.recycle();

            if (targetId != 0)
            {
                mTargetView = mResourceFinder.findViewById(targetId);
                if (mTargetView != null)
                {
                    mTargetSet = true;
                }
            }

            mClipToView = (View) mResourceFinder.findViewById(android.R.id.content).getParent();
        }

        /**
         * Set the view for the prompt to focus on.
         *
         * @param target The view that the prompt will highlight.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTarget(final View target)
        {
            mTargetView = target;
            mTargetSet = mTargetView != null;
            return this;
        }

        /**
         * Set the view for the prompt to focus on using the given resource id.
         *
         * @param target The view that the prompt will highlight.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTarget(@IdRes final int target)
        {
            mTargetView = mResourceFinder.findViewById(target);
            mTargetPosition = null;
            mTargetSet = mTargetView != null;
            return this;
        }

        /**
         * Set the centre point as a screen position
         *
         * @param left Centre point from screen left
         * @param top  Centre point from screen top
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTarget(final float left, final float top)
        {
            mTargetView = null;
            mTargetPosition = new PointF(left, top);
            mTargetSet = true;
            return this;
        }

        /**
         * Change the view that is rendered as the target.
         * By default the view from {@link #setTarget(View)} is rendered as the target.
         *
         * @param view The view to use to render the prompt target
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTargetRenderView(final View view)
        {
            mTargetRenderView = view;
            return this;
        }

        /**
         * Has the target been set successfully?
         *
         * @return True if set successfully.
         */
        public boolean isTargetSet()
        {
            return mTargetSet;
        }

        /**
         * Set the primary text using the given resource id.
         *
         * @param resId The string resource id for the primary text
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPrimaryText(@StringRes final int resId)
        {
            mPrimaryText = mResourceFinder.getString(resId);
            return this;
        }

        /**
         * Set the primary text to the given string
         *
         * @param text The primary text
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPrimaryText(final String text)
        {
            mPrimaryText = text;
            return this;
        }

        /**
         * Set the primary text to the given CharSequence.
         * It is recommended that you don't go crazy with custom Spannables.
         *
         * @param text The primary text as CharSequence
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPrimaryText(final CharSequence text)
        {
            mPrimaryText = text;
            return this;
        }

        /**
         * Set the primary text font size using the given resource id.
         *
         * @param resId The resource id for the primary text size
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPrimaryTextSize(@DimenRes final int resId)
        {
            mPrimaryTextSize = mResourceFinder.getResources().getDimension(resId);
            return this;
        }

        /**
         * Set the primary text font size.
         *
         * @param size The primary text font size
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPrimaryTextSize(final float size)
        {
            mPrimaryTextSize = size;
            return this;
        }

        /**
         * Set the primary text colour.
         *
         * @param colour The primary text colour resource id
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPrimaryTextColour(@ColorInt final int colour)
        {
            mPrimaryTextColour = colour;
            return this;
        }

        /**
         * Sets the typeface and style used to display the primary text.
         *
         * @param typeface The primary text typeface
         */
        public Builder setPrimaryTextTypeface(final Typeface typeface)
        {
            return setPrimaryTextTypeface(typeface, 0);
        }

        /**
         * Sets the typeface used to display the primary text.
         *
         * @param typeface The primary text typeface
         * @param style    The typeface style
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPrimaryTextTypeface(final Typeface typeface, final int style)
        {
            mPrimaryTextTypeface = typeface;
            mPrimaryTextTypefaceStyle = style;
            return this;
        }

        /**
         * Set the secondary text using the given resource id.
         *
         * @param resId The secondary text resource id
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSecondaryText(@StringRes final int resId)
        {
            mSecondaryText = mResourceFinder.getString(resId);
            return this;
        }

        /**
         * Set the secondary text.
         *
         * @param text The secondary text
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSecondaryText(final String text)
        {
            mSecondaryText = text;
            return this;
        }

        /**
         * Set the secondary text.
         * It is recommended that you don't go crazy with custom Spannables.
         *
         * @param text The secondary text as a CharSequence
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSecondaryText(final CharSequence text)
        {
            mSecondaryText = text;
            return this;
        }

        /**
         * Set the secondary text font size using the give resource id.
         *
         * @param resId The secondary text string resource id
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSecondaryTextSize(@DimenRes final int resId)
        {
            mSecondaryTextSize = mResourceFinder.getResources().getDimension(resId);
            return this;
        }

        /**
         * Set the secondary text font size.
         *
         * @param size The secondary text font size
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSecondaryTextSize(final float size)
        {
            mSecondaryTextSize = size;
            return this;
        }

        /**
         * Set the secondary text colour.
         *
         * @param colour The secondary text colour resource id
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSecondaryTextColour(@ColorInt final int colour)
        {
            mSecondaryTextColour = colour;
            return this;
        }

        /**
         * Sets the typeface used to display the secondary text.
         *
         * @param typeface The secondary text typeface
         */
        public Builder setSecondaryTextTypeface(final Typeface typeface)
        {
            return setSecondaryTextTypeface(typeface, 0);
        }

        /**
         * Sets the typeface and style used to display the secondary text.
         *
         * @param typeface The secondary text typeface
         * @param style    The typeface style
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSecondaryTextTypeface(final Typeface typeface, final int style)
        {
            mSecondaryTextTypeface = typeface;
            mSecondaryTextTypefaceStyle = style;
            return this;
        }

        /**
         * Set the text left and right padding.
         *
         * @param padding The padding on the text left and right
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTextPadding(final float padding)
        {
            mTextPadding = padding;
            return this;
        }

        /**
         * Set the text left and right padding using the given resource id.
         *
         * @param resId The text padding dimension resource id
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTextPadding(@DimenRes final int resId)
        {
            mTextPadding = mResourceFinder.getResources().getDimension(resId);
            return this;
        }

        /**
         * Set the distance between the primary and secondary text.
         *
         * @param separation The distance separation between the primary and secondary text
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTextSeparation(final float separation)
        {
            mTextSeparation = separation;
            return this;
        }

        /**
         * Set the distance between the primary and secondary text using the given resource id.
         *
         * @param resId The dimension resource id for the text separation
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTextSeparation(@DimenRes final int resId)
        {
            mTextSeparation = mResourceFinder.getResources().getDimension(resId);
            return this;
        }

        /**
         * Set the padding between the text and the focal point.
         *
         * @param padding The distance between the text and focal
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setFocalPadding(final float padding)
        {
            mFocalToTextPadding = padding;
            return this;
        }

        /**
         * Set the padding between the text and the focal point using the given resource id.
         *
         * @param resId The dimension resource id for the focal to text distance
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setFocalPadding(@DimenRes final int resId)
        {
            mFocalToTextPadding = mResourceFinder.getResources().getDimension(resId);
            return this;
        }

        /**
         * Set the interpolator to use in animations.
         *
         * @param interpolator The animation interpolator to use
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setAnimationInterpolator(final Interpolator interpolator)
        {
            mAnimationInterpolator = interpolator;
            return this;
        }

        /**
         * Enable/disable animation above target.
         * true by default
         *
         * @param enabled Idle animation enabled
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setIdleAnimationEnabled(final boolean enabled)
        {
            mIdleAnimationEnabled = enabled;
            return this;
        }

        /**
         * Set the icon to draw in the focal point using the given resource id.
         *
         * @param resId The drawable resource id for the icon
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setIcon(@DrawableRes final int resId)
        {
            mIconDrawable = mResourceFinder.getDrawable(resId);
            return this;
        }

        /**
         * Set the icon to draw in the focal point.
         *
         * @param drawable The drawable for the icon
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setIconDrawable(final Drawable drawable)
        {
            mIconDrawable = drawable;
            return this;
        }

        /**
         * Applies a tint to the icon drawable
         *
         * @param tint the tint to apply to the icon drawable, {@code null} will remove the tint.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setIconDrawableTintList(@Nullable ColorStateList tint)
        {
            mIconDrawableTintList = tint;
            mHasIconDrawableTint = tint != null;
            return this;
        }

        /**
         * Sets the PorterDuff mode to use to apply the tint.
         *
         * @param tintMode the tint mode to use on the icon drawable, {@code null} will remove the
         *                 tint.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setIconDrawableTintMode(@Nullable PorterDuff.Mode tintMode)
        {
            mIconDrawableTintMode = tintMode;
            if (tintMode == null)
            {
                mIconDrawableTintList = null;
                mHasIconDrawableTint = false;
            }
            return this;
        }

        /**
         * Sets the colour to use to tint the icon drawable.
         *
         * @param colour The colour to use to tint the icon drawable, call {@link
         *               #setIconDrawableTintList(ColorStateList)} or {@link
         *               #setIconDrawableTintMode(PorterDuff.Mode)} with {@code null} to remove the
         *               tint.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setIconDrawableColourFilter(@ColorInt final int colour)
        {
            mIconDrawableColourFilter = colour;
            mIconDrawableTintList = null;
            mHasIconDrawableTint = true;
            return this;
        }

        /**
         * Set the listener to listen for when the prompt state changes.
         *
         * @param listener The listener to use
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPromptStateChangeListener(final PromptStateChangeListener listener)
        {
            mPromptStateChangeListener = listener;
            return this;
        }

        /**
         * Set if the prompt should stop touch events on the focal point from passing to underlying
         * views. Default is false.
         *
         * @param captureTouchEvent True to capture touch events in the prompt
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCaptureTouchEventOnFocal(final boolean captureTouchEvent)
        {
            mCaptureTouchEventOnFocal = captureTouchEvent;
            return this;
        }

        /**
         * Set the max width that the primary and secondary text can be.
         *
         * @param width The max width that the text can reach
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMaxTextWidth(final float width)
        {
            mMaxTextWidth = width;
            return this;
        }

        /**
         * Set the max width that the primary and secondary text can be using the given resource
         * id.
         *
         * @param resId The dimension resource id for the max width that the text can reach
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMaxTextWidth(@DimenRes final int resId)
        {
            mMaxTextWidth = mResourceFinder.getResources().getDimension(resId);
            return this;
        }

        /**
         * Set the background colour.
         * The Material Design Guidelines specify that this should be 244 or hex F4.
         *
         * @param colour The background colour colour resource id
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setBackgroundColour(@ColorInt final int colour)
        {
            mBackgroundColour = colour;
            return this;
        }

        /**
         * Set the focal point colour.
         *
         * @param colour The focal colour colour resource id
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setFocalColour(@ColorInt final int colour)
        {
            mFocalColour = colour;
            return this;
        }

        /**
         * Set the focal point radius.
         *
         * @param radius The focal radius
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setFocalRadius(final float radius)
        {
            mFocalRadius = radius;
            return this;
        }

        /**
         * Set the focal point radius using the given resource id.
         *
         * @param resId The focal radius dimension resource id
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setFocalRadius(@DimenRes final int resId)
        {
            mFocalRadius = mResourceFinder.getResources().getDimension(resId);
            return this;
        }

        /**
         * Set whether the prompt should dismiss itself when a touch event occurs outside the focal.
         * Default is true.
         *
         * Listen for the {@link #STATE_NON_FOCAL_PRESSED} event in the
         * {@link #setPromptStateChangeListener(PromptStateChangeListener)} to handle the prompt
         * being pressed outside the focal area.
         *
         * @param autoDismiss True - prompt will dismiss when touched outside the focal, false - no
         *                    action taken.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setAutoDismiss(final boolean autoDismiss)
        {
            mAutoDismiss = autoDismiss;
            return this;
        }

        /**
         * Set whether the prompt should finish itself when a touch event occurs inside the focal.
         * Default is true.
         *
         * Listen for the {@link #STATE_FOCAL_PRESSED} event in the
         * {@link #setPromptStateChangeListener(PromptStateChangeListener)} to handle the prompt
         * target being pressed.
         *
         * @param autoFinish True - prompt will finish when touched inside the focal, false - no
         *                   action taken.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setAutoFinish(final boolean autoFinish)
        {
            mAutoFinish = autoFinish;
            return this;
        }

        /**
         * Set if the prompt should stop touch events outside the prompt from passing to underlying
         * views. Default is false.
         *
         * @param captureTouchEventOutsidePrompt True to capture touch events out side the prompt
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCaptureTouchEventOutsidePrompt(
                final boolean captureTouchEventOutsidePrompt)
        {
            mCaptureTouchEventOutsidePrompt = captureTouchEventOutsidePrompt;
            return this;
        }

        /**
         * Set the primary and secondary text horizontal layout gravity.
         * Default: {@link Gravity#START}
         *
         * @param gravity The horizontal gravity
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTextGravity(final int gravity)
        {
            mPrimaryTextGravity = gravity;
            mSecondaryTextGravity = gravity;
            return this;
        }

        /**
         * Set the primary text horizontal layout gravity.
         * Default: {@link Gravity#START}
         *
         * @param gravity The horizontal gravity
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPrimaryTextGravity(final int gravity)
        {
            mPrimaryTextGravity = gravity;
            return this;
        }

        /**
         * Set the secondary text horizontal layout gravity.
         * Default: {@link Gravity#START}
         *
         * @param gravity The horizontal gravity
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setSecondaryTextGravity(final int gravity)
        {
            mSecondaryTextGravity = gravity;
            return this;
        }

        /**
         * Set the view to clip the prompt to.
         * Default: {@link android.R.id#content}
         * <p>
         * Null can be used to stop the prompt being clipped to a view.
         *
         * @param view The view to clip to
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setClipToView(final View view)
        {
            mClipToView = view;
            return this;
        }

        /**
         * Back button can be used to dismiss the prompt.
         * Default: {@link true}
         *
         * @param enabled True for back button dismiss enabled
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setBackButtonDismissEnabled(final boolean enabled)
        {
            mBackButtonDismissEnabled = enabled;
            return this;
        }

        /**
         * Creates an {@link MaterialTapTargetPrompt} with the arguments supplied to this
         * builder.
         * <p>
         * Calling this method does not display the prompt. If no additional
         * processing is needed, {@link #show()} may be called instead to both
         * create and display the prompt.
         * </p>
         * <p>
         * Will return {@link null} if a valid target has not been set or the primary text is {@link
         * null}.
         * To check that a valid target has been set call {@link #isTargetSet()}.
         * </p>
         *
         * @return The created builder or null if no target
         */
        public MaterialTapTargetPrompt create()
        {
            if (!mTargetSet || (mPrimaryText == null && mSecondaryText == null))
            {
                return null;
            }
            final MaterialTapTargetPrompt mPrompt = new MaterialTapTargetPrompt(mResourceFinder);
            if (mTargetView != null)
            {
                mPrompt.mTargetView = mTargetView;
                mPrompt.mView.mTargetView = mTargetView;
            }
            else
            {
                mPrompt.mTargetPosition = mTargetPosition;
            }
            mPrompt.mParentView = mResourceFinder.getPromptParentView();
            mPrompt.mView.mDrawRipple = mIdleAnimationEnabled;
            mPrompt.mIdleAnimationEnabled = mIdleAnimationEnabled;
            mPrompt.mClipToView = mClipToView;

            mPrompt.mPrimaryText = mPrimaryText;
            mPrompt.mPrimaryTextColourAlpha = Color.alpha(mPrimaryTextColour);
            mPrompt.mSecondaryText = mSecondaryText;
            mPrompt.mSecondaryTextColourAlpha = Color.alpha(mSecondaryTextColour);
            mPrompt.mMaxTextWidth = mMaxTextWidth;
            mPrompt.mTextPadding = mTextPadding;
            mPrompt.mFocalToTextPadding = mFocalToTextPadding;
            mPrompt.mBaseFocalRippleAlpha = 150;
            mPrompt.m88dp = m88dp;
            mPrompt.mBaseBackgroundColourAlpha = Color.alpha(mBackgroundColour);
            mPrompt.mBaseFocalColourAlpha = Color.alpha(mFocalColour);

            mPrompt.mView.mTextSeparation = mTextSeparation;

            mPrompt.mPromptStateChangeListener = mPromptStateChangeListener;
            mPrompt.mView.mCaptureTouchEventOnFocal = mCaptureTouchEventOnFocal;

            if (mAnimationInterpolator != null)
            {
                mPrompt.mAnimationInterpolator = mAnimationInterpolator;
            }
            else
            {
                mPrompt.mAnimationInterpolator = new AccelerateDecelerateInterpolator();
            }

            mPrompt.mBaseFocalRadius = mFocalRadius;
            //Calculate 10% of the focal radius
            mPrompt.mFocalRadius10Percent = mFocalRadius / 100 * 10;


            if (mIconDrawable != null)
            {
                mIconDrawable.mutate();
                mIconDrawable.setBounds(0, 0, mIconDrawable.getIntrinsicWidth(), mIconDrawable.getIntrinsicHeight());
                if (mHasIconDrawableTint)
                {
                    if (mIconDrawableTintList != null)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            mIconDrawable.setTintList(mIconDrawableTintList);
                        }
                    }
                    else
                    {
                        mIconDrawable.setColorFilter(mIconDrawableColourFilter, mIconDrawableTintMode);
                        mIconDrawable.setAlpha(Color.alpha(mIconDrawableColourFilter));
                    }
                }
            }

            mPrompt.mView.mBackButtonDismissEnabled = mBackButtonDismissEnabled;
            mPrompt.mView.mAutoDismiss = mAutoDismiss;
            mPrompt.mView.mIconDrawable = mIconDrawable;

            mPrompt.mView.mPaintFocal = new Paint();
            mPrompt.mView.mPaintFocal.setColor(mFocalColour);
            mPrompt.mView.mPaintFocal.setAlpha(Color.alpha(mFocalColour));
            mPrompt.mView.mPaintFocal.setAntiAlias(true);

            mPrompt.mView.mPaintBackground = new Paint();
            mPrompt.mView.mPaintBackground.setColor(mBackgroundColour);
            mPrompt.mView.mPaintBackground.setAlpha(Color.alpha(mBackgroundColour));
            mPrompt.mView.mPaintBackground.setAntiAlias(true);

            if (mPrimaryText != null)
            {
                mPrompt.mPaintPrimaryText = new TextPaint();
                mPrompt.mPaintPrimaryText.setColor(mPrimaryTextColour);
                mPrompt.mPaintPrimaryText.setAlpha(Color.alpha(mPrimaryTextColour));
                mPrompt.mPaintPrimaryText.setAntiAlias(true);
                mPrompt.mPaintPrimaryText.setTextSize(mPrimaryTextSize);
                setTypeface(mPrompt.mPaintPrimaryText, mPrimaryTextTypeface, mPrimaryTextTypefaceStyle);
                mPrompt.mPrimaryTextAlignment = getTextAlignment(mPrimaryTextGravity, mPrimaryText);
            }

            if (mSecondaryText != null)
            {
                mPrompt.mPaintSecondaryText = new TextPaint();
                mPrompt.mPaintSecondaryText.setColor(mSecondaryTextColour);
                mPrompt.mPaintSecondaryText.setAlpha(Color.alpha(mSecondaryTextColour));
                mPrompt.mPaintSecondaryText.setAntiAlias(true);
                mPrompt.mPaintSecondaryText.setTextSize(mSecondaryTextSize);
                setTypeface(mPrompt.mPaintSecondaryText, mSecondaryTextTypeface, mSecondaryTextTypefaceStyle);
                mPrompt.mSecondaryTextAlignment = getTextAlignment(mSecondaryTextGravity, mSecondaryText);
            }

            mPrompt.mAutoDismiss = mAutoDismiss;
            mPrompt.mAutoFinish = mAutoFinish;

            mPrompt.mView.mCaptureTouchEventOutsidePrompt = mCaptureTouchEventOutsidePrompt;

            if (mTargetRenderView == null)
            {
                mPrompt.mView.mTargetRenderView = mPrompt.mView.mTargetView;
            }
            else
            {
                mPrompt.mView.mTargetRenderView = mTargetRenderView;
            }

            return mPrompt;
        }

        /**
         * Creates an {@link MaterialTapTargetPrompt} with the arguments supplied to this
         * builder and immediately displays the prompt.
         * <p>
         * Calling this method is functionally identical to:
         * </p>
         * <pre>
         *     MaterialTapTargetPrompt prompt = builder.create();
         *     prompt.show();
         * </pre>
         * <p>
         * Will return {@link null} if a valid target has not been set or the primary text is {@link
         * null}.
         * To check that a valid target has been set call {@link #isTargetSet()}.
         * </p>
         *
         * @return The created builder or null if no target
         */
        public MaterialTapTargetPrompt show()
        {
            final MaterialTapTargetPrompt mPrompt = create();
            if (mPrompt != null)
            {
                mPrompt.show();
            }
            return mPrompt;
        }

        /**
         * Based on setTypeface in android TextView, Copyright (C) 2006 The Android Open Source
         * Project. https://android.googlesource.com/platform/frameworks/base.git/+/master/core/java/android/widget/TextView.java
         */
        private void setTypeface(TextPaint textPaint, Typeface typeface, int style)
        {
            if (style > 0)
            {
                if (typeface == null)
                {
                    typeface = Typeface.defaultFromStyle(style);
                }
                else
                {
                    typeface = Typeface.create(typeface, style);
                }

                textPaint.setTypeface(typeface);

                int typefaceStyle = typeface != null ? typeface.getStyle() : 0;
                int need = style & ~typefaceStyle;
                textPaint.setFakeBoldText((need & Typeface.BOLD) != 0);
                textPaint.setTextSkewX((need & Typeface.ITALIC) != 0 ? -0.25f : 0);
            }
            else
            {
                textPaint.setTypeface(typeface);
            }
        }

        /**
         * Based on setTypefaceFromAttrs in android TextView, Copyright (C) 2006 The Android Open
         * Source Project. https://android.googlesource.com/platform/frameworks/base.git/+/master/core/java/android/widget/TextView.java
         */
        private Typeface setTypefaceFromAttrs(String familyName, int typefaceIndex, int styleIndex)
        {
            Typeface tf = null;
            if (familyName != null)
            {
                tf = Typeface.create(familyName, styleIndex);
                if (tf != null)
                {
                    return tf;
                }
            }
            switch (typefaceIndex)
            {
                case 1:
                    tf = Typeface.SANS_SERIF;
                    break;

                case 2:
                    tf = Typeface.SERIF;
                    break;

                case 3:
                    tf = Typeface.MONOSPACE;
                    break;
            }
            return tf;
        }

        /**
         * Based on parseTintMode in android appcompat v7 DrawableUtils, Copyright (C) 2014 The
         * Android Open Source Project. https://android.googlesource.com/platform/frameworks/support.git/+/master/v7/appcompat/src/android/support/v7/widget/DrawableUtils.java
         */
        PorterDuff.Mode parseTintMode(int value, PorterDuff.Mode defaultMode)
        {
            switch (value)
            {
                case 3: return PorterDuff.Mode.SRC_OVER;
                case 5: return PorterDuff.Mode.SRC_IN;
                case 9: return PorterDuff.Mode.SRC_ATOP;
                case 14: return PorterDuff.Mode.MULTIPLY;
                case 15: return PorterDuff.Mode.SCREEN;
                case 16: return PorterDuff.Mode.valueOf("ADD");
                default: return defaultMode;
            }
        }

        /**
         * Gets the absolute text alignment value based on the supplied gravity and the activities
         * layout direction.
         *
         * @param gravity The gravity to convert to absolute values
         * @return absolute layout direction
         */
        @SuppressLint("RtlHardcoded")
        Layout.Alignment getTextAlignment(final int gravity, final CharSequence text)
        {
            final int absoluteGravity;
            if (isVersionAfterJellyBeanMR1())
            {
                int realGravity = gravity;
                final int layoutDirection = getLayoutDirection();
                if (text != null && layoutDirection == View.LAYOUT_DIRECTION_RTL
                        && new Bidi(text.toString(), Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT).isRightToLeft())
                {
                    if (gravity == Gravity.START)
                    {
                        realGravity = Gravity.END;
                    }
                    else if (gravity == Gravity.END)
                    {
                        realGravity = Gravity.START;
                    }
                }
                absoluteGravity = Gravity.getAbsoluteGravity(realGravity, layoutDirection);
            }
            else
            {
                if ((gravity & Gravity.START) == Gravity.START)
                {
                    absoluteGravity = Gravity.LEFT;
                }
                else if ((gravity & Gravity.END) == Gravity.END)
                {
                    absoluteGravity = Gravity.RIGHT;
                }
                else
                {
                    absoluteGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
                }
            }
            final Layout.Alignment alignment;
            switch (absoluteGravity)
            {
                case Gravity.RIGHT:
                    alignment = Layout.Alignment.ALIGN_OPPOSITE;
                    break;
                case Gravity.CENTER_HORIZONTAL:
                    alignment = Layout.Alignment.ALIGN_CENTER;
                    break;
                default:
                    alignment = Layout.Alignment.ALIGN_NORMAL;
                    break;
            }
            return alignment;
        }

        /**
         * Return the layout direction. Will be either {@link View#LAYOUT_DIRECTION_LTR} or {@link
         * View#LAYOUT_DIRECTION_RTL}.
         *
         * @return Returns {@link View#LAYOUT_DIRECTION_RTL} if the configuration is {@link
         * android.content.res.Configuration#SCREENLAYOUT_LAYOUTDIR_RTL}, otherwise {@link
         * View#LAYOUT_DIRECTION_LTR}.
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        int getLayoutDirection()
        {
            return mResourceFinder.getResources().getConfiguration().getLayoutDirection();
        }

        boolean isVersionAfterJellyBeanMR1()
        {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
        }
    }

    /**
     * Interface definition for a callback to be invoked when a prompts state changes.
     */
    public interface PromptStateChangeListener
    {
        /**
         * Called when the prompts state changes.
         *
         * @param prompt The prompt which state has changed.
         * @param state  can be either {@link #STATE_REVEALING}, {@link #STATE_REVEALED}, {@link
         *               #STATE_FOCAL_PRESSED}, {@link #STATE_FINISHED}, {@link #STATE_DISMISSING},
         *               {@link #STATE_DISMISSED}
         */
        void onPromptStateChanged(final MaterialTapTargetPrompt prompt, final int state);
    }

    static class AnimatorListener implements Animator.AnimatorListener
    {

        @Override
        public void onAnimationStart(Animator animation)
        {

        }

        @Override
        public void onAnimationEnd(Animator animation)
        {

        }

        @Override
        public void onAnimationCancel(Animator animation)
        {

        }

        @Override
        public void onAnimationRepeat(Animator animation)
        {

        }
    }
}
