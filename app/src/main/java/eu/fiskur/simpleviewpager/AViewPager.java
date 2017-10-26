package eu.fiskur.simpleviewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

/*
    Unused: This forces the viewpager to draw correctly based on the height of the largest child.
 */
public class AViewPager extends ViewPager {

  public AViewPager(Context context) {
    super(context);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int height = 0;
    for (int i = 0; i < getChildCount(); i++) {
      View child = getChildAt(i);
      child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
      int h = child.getMeasuredHeight();
      if (h > height)
        height = h;
    }

    int newHeight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
    super.onMeasure(widthMeasureSpec, newHeight);
  }
}