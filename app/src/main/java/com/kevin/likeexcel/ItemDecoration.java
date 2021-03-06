package com.kevin.likeexcel;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.kevin.likeexcel.ItemDecoration.DividerMode.GRID;
import static com.kevin.likeexcel.ItemDecoration.DividerMode.HORIZONTAL;
import static com.kevin.likeexcel.ItemDecoration.DividerMode.VERTICAL;


public class ItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Paint mPaint;
    private Drawable mDivider;     //分割线Drawable
    private int mDividerHeight = 1;  //分割线高度

    private boolean drawbottom = false;//是否绘制最后一行线
    private int directionMode = GRID;//绘制方向


    @IntDef({GRID, HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DividerMode {
        int GRID = 1 << 0;
        int HORIZONTAL = 1 << 1;
        int VERTICAL = 1 << 2;
    }
    public ItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }
    /**
     * 自定义分割线
     *
     * @param context
     * @param mDirectionMode 绘制方向
     * @param mDrawbottom 是否绘制最后一行线
     */
    public ItemDecoration(Context context, @DividerMode int mDirectionMode, boolean mDrawbottom) {
        directionMode=mDirectionMode;
        drawbottom=mDrawbottom;
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }
    /**
     * 自定义分割线
     *
     * @param context
     * @param mDirectionMode 绘制方向
     * @param mDrawbottom 是否绘制最后一行线
     * @param drawableId 分割线图片
     */
    public ItemDecoration(Context context, @DividerMode int mDirectionMode, boolean mDrawbottom,@DrawableRes int drawableId) {
        directionMode=mDirectionMode;
        drawbottom=mDrawbottom;
        mDivider = ContextCompat.getDrawable(context, drawableId);
        mDividerHeight = mDivider.getIntrinsicHeight();
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param drawableId 分割线图片
     */
    public ItemDecoration(Context context, int drawableId) {
        mDivider = ContextCompat.getDrawable(context, drawableId);
        mDividerHeight = mDivider.getIntrinsicHeight();
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    public ItemDecoration(Context context, int dividerHeight, int dividerColor) {
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(context.getResources().getColor(dividerColor));
        mPaint.setStyle(Paint.Style.FILL);
    }

    public Drawable getmDivider() {
        return mDivider;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        switch (directionMode) {
            case HORIZONTAL:
                drawHorizontal(c, parent);
                break;
            case VERTICAL:
                drawVertical(c, parent);
                break;
            default:
                drawHorizontal(c, parent);
                drawVertical(c, parent);
                break;
        }
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    // 绘制水平线
    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin + mDividerHeight;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
            if (mPaint != null) {
                c.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    // 绘制垂直线
    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
            if (mPaint != null) {
                c.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    // 判断是否是最后一列
    private boolean isLastColum(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) {
                // 如果是最后一列，则不需要绘制右边
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0) {
                    // 如果是最后一列，则不需要绘制右边
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }

    // 判断是否是最后一行
    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            childCount = childCount - childCount % spanCount;
            if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                return true;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else
            // StaggeredGridLayoutManager 且横向滚动
            {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        switch (directionMode) {
            case HORIZONTAL:
                if(drawbottom){
                    if (isLastColum(parent, itemPosition, spanCount, childCount)) {
                        outRect.set(0, 0, 0, mDividerHeight);
                    }else {
                        outRect.set(0, 0, mDividerHeight, mDividerHeight);
                    }
                }else {
                    outRect.set(0, 0, 0, mDividerHeight);
                }
                break;
            case VERTICAL:
                if(drawbottom){
                    if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
                        outRect.set(0, 0, mDividerHeight, 0);
                    }else {
                        outRect.set(0, 0, mDividerHeight, mDividerHeight);
                    }
                }else {
                    outRect.set(0, 0, mDividerHeight, 0);
                }
                break;
            default:
                if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
                    // 如果是最后一行，则不需要绘制底部
                    outRect.set(0, 0, mDividerHeight, 0);
                } else if (isLastColum(parent, itemPosition, spanCount, childCount)) {
                    // 如果是最后一列，则不需要绘制右边
                    outRect.set(0, 0, 0, mDividerHeight);
                } else {
                    outRect.set(0, 0, mDividerHeight, mDividerHeight);
                }
                break;
        }

    }

}
