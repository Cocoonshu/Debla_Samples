package com.cocoonshu.example.imageclipper.view;

import com.cocoonshu.example.imageclipper.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class ImageClipper extends View implements OnGestureListener {

	private static final String TAG = "ImageClipper";
	
    private static final int     MOTION_ACTION_NONE              = 0b0000;
    private static final int     MOTION_ACTION_LEFT              = 0b0001;
    private static final int     MOTION_ACTION_TOP               = 0b0010;
    private static final int     MOTION_ACTION_RIGHT             = 0b0100;
    private static final int     MOTION_ACTION_BOTTOM            = 0b1000;
    private static final int     MOTION_ACTION_LEFT_TOP          = 0b0011;
    private static final int     MOTION_ACTION_TOP_RIGHT         = 0b0110;
    private static final int     MOTION_ACTION_RIGHT_BOTTOM      = 0b1100;
    private static final int     MOTION_ACTION_BOTTOM_LEFT       = 0b1001;
    private static final int     MOTION_ACTION_MOVE              = 0b1111;

    private static final boolean ClipFrameAllowFullareaTouchable = true;
    private static final float   DefaultGridLineWidthDP          = 1f;
    private static final float   DefaultFrameLineWidthDP         = 3f;
    private static final float   DefaultCornerLineWidthDP        = 3f;
    private static final float   DefaultCornerLineHeightDP       = 9f;
    private static final int     DefaultGridLineColor            = 0x99FFFFFF;
    private static final int     DefaultFrameLineColor           = 0x99FFFFFF;
    private static final int     DefaultCornelLineColor          = 0xFFFFFFFF;
    private static final int     WidthCount                      = 3;
    private static final int     HeightCount                     = 3;
    private static final int     GridLineCount                   = (WidthCount - 1) * (HeightCount - 1) * 4;
    private static final int     FrameLineCount                  = 2 * 2 * 4;
    private static final int     CornerLineCount                 = (WidthCount + HeightCount) * 2 * 2 * 2;

    private float                mGridLineWidth                  = 0;
    private float                mFrameLineWidth                 = 0;
    private float                mCornerLineWidth                = 0;
    private float                mCornerLineHeight               = 0;
    private float[]              mGridLines                      = null;
    private float[]              mFrameLines                     = null;
    private float[]              mCornerLines                    = null;

    private ColorStateList       mGridLineColor                  = null;
    private ColorStateList       mFrameLineColor                 = null;
    private ColorStateList       mCornerLineColor                = null;

    private Paint                mGridLinePaint                  = null;
    private Paint                mFrameLinePaint                 = null;
    private Paint                mCornerLinePaint                = null;

    private boolean              mIsUnderTouched                 = false;
    private int[]                mCurrentViewState               = ENABLED_STATE_SET;
    private GestureDetector      mGestureDetector                = null;
    private RectF                mFrameRect                      = new RectF();
    private RectF                mFrameLimitRect                 = new RectF();
    private int                  mMotionActions                  = MOTION_ACTION_NONE;
	
	public ImageClipper(Context context) {
		this(context, null);
	}
	
	public ImageClipper(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ImageClipper(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupDefaultValues(context);
		setupLayoutValues(context, attrs, defStyle);
	}

	private void setupDefaultValues(Context context) {
		float density = context.getResources().getDisplayMetrics().density;
		
		mGridLines        = new float[GridLineCount];
        mFrameLines       = new float[FrameLineCount];
        mCornerLines      = new float[CornerLineCount];
		
		mGridLineWidth    = DefaultGridLineWidthDP * density;
		mFrameLineWidth   = DefaultFrameLineWidthDP * density;
		mCornerLineWidth  = DefaultCornerLineWidthDP * density;
		mCornerLineHeight = DefaultCornerLineHeightDP * density;
		mGridLineColor    = ColorStateList.valueOf(DefaultGridLineColor);
		mFrameLineColor   = ColorStateList.valueOf(DefaultFrameLineColor);
		mCornerLineColor  = ColorStateList.valueOf(DefaultCornelLineColor);
		mGridLinePaint    = new Paint();
		mFrameLinePaint   = new Paint();
		mCornerLinePaint  = new Paint();
		mGestureDetector  = new GestureDetector(context, this);
		
		mGridLinePaint.setStyle(Style.STROKE);
		mFrameLinePaint.setStyle(Style.STROKE);
		mCornerLinePaint.setStyle(Style.STROKE);
		
		mGridLinePaint.setStrokeWidth(mGridLineWidth);
		mFrameLinePaint.setStrokeWidth(mFrameLineWidth);
		mCornerLinePaint.setStrokeWidth(mCornerLineWidth);
	}
	
	private void setupLayoutValues(Context context, AttributeSet attrs, int defStyle) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageClipper, defStyle, 0);
        int loopSize = typedArray.getIndexCount();
        for (int i = 0; i < loopSize; i++) {
            int attributeKey = typedArray.getIndex(i);
            switch (attributeKey) {
            case R.styleable.ImageClipper_lineWidth:
            	mGridLineWidth = typedArray.getDimension(attributeKey, mGridLineWidth);
                break;
            case R.styleable.ImageClipper_frameLineWidth:
            	mFrameLineWidth = typedArray.getDimension(attributeKey, mFrameLineWidth);
                break;
            case R.styleable.ImageClipper_cornerLineWidth:
            	mCornerLineWidth = typedArray.getDimension(attributeKey, mCornerLineWidth);
                break;
            case R.styleable.ImageClipper_cornerLineHeight:
            	mCornerLineHeight = typedArray.getDimension(attributeKey, mCornerLineHeight);
                break;
            case R.styleable.ImageClipper_lineColor:
            	ColorStateList lineColor = typedArray.getColorStateList(attributeKey);
            	if (lineColor != null) {
            		mGridLineColor = lineColor;
            	}
                break;
            case R.styleable.ImageClipper_frameColor:
            	ColorStateList frameLineColor = typedArray.getColorStateList(attributeKey);
            	if (frameLineColor != null) {
            		mFrameLineColor = frameLineColor;
            	}
                break;
            case R.styleable.ImageClipper_cornerColor:
            	ColorStateList cornerLineColor = typedArray.getColorStateList(attributeKey);
            	if (cornerLineColor != null) {
            		mCornerLineColor = cornerLineColor;
            	}
                break;
            }
        }
        typedArray.recycle();
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int backgroundWidth  = 0;
        int backgroundHeight = 0;
        int widthSpecMode    = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode   = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize    = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize   = MeasureSpec.getSize(heightMeasureSpec);
        int measuredWidth    = 0;
        int measuredHeight   = 0;

        Drawable background = getBackground();
        if (background != null) {
            backgroundWidth = background.getIntrinsicWidth();
            backgroundHeight = background.getIntrinsicHeight();
        }
        
        // Width
        switch (widthSpecMode) {
        case MeasureSpec.UNSPECIFIED:
            measuredWidth = backgroundWidth;
            break;
        case MeasureSpec.AT_MOST:
            measuredWidth = backgroundWidth < widthSpecSize ? backgroundWidth : widthSpecSize;
            break;
        case MeasureSpec.EXACTLY:
            measuredWidth = widthSpecSize;
            break;
        }
        
        // Height
        switch (heightSpecMode) {
        case MeasureSpec.UNSPECIFIED:
            measuredHeight = backgroundHeight;
            break;
        case MeasureSpec.AT_MOST:
            measuredHeight = backgroundHeight < heightSpecSize ? backgroundHeight : heightSpecSize;
            break;
        case MeasureSpec.EXACTLY:
            measuredHeight = heightSpecSize;
            break;
        }
        
        setMeasuredDimension(measuredWidth, measuredHeight);
        mFrameLimitRect.set(0, 0, measuredWidth, measuredHeight);
        mFrameRect.set(0, 0, measuredWidth, measuredHeight);
        mFrameRect.inset(
        		Math.max(mCornerLineWidth, mFrameLineWidth) * 0.5f,
        		Math.max(mCornerLineWidth, mFrameLineWidth) * 0.5f);
    }
	
	@Override
	protected void onDraw(Canvas canvas) {
		// Setup color list state
		int gridLineColor   = DefaultGridLineColor;
		int frameLineColor  = DefaultFrameLineColor;
		int cornerLineColor = DefaultCornelLineColor;
		gridLineColor = mGridLineColor.getColorForState(
				mCurrentViewState, mGridLineColor.getDefaultColor());
		frameLineColor = mFrameLineColor.getColorForState(
				mCurrentViewState, mFrameLineColor.getDefaultColor());
		cornerLineColor = mCornerLineColor.getColorForState(
				mCurrentViewState, mCornerLineColor.getDefaultColor());
		
		// Update lines
		updateLines();
		
		// Draw background
		super.onDraw(canvas);
		
		// Draw frame
		mFrameLinePaint.setColor(frameLineColor);
		canvas.drawRect(mFrameRect, mFrameLinePaint);
		
		// Draw lines
		mGridLinePaint.setColor(gridLineColor);
        canvas.drawLines(mGridLines, mGridLinePaint);
		
		// Draw corner
		mCornerLinePaint.setColor(cornerLineColor);
        canvas.drawLines(mCornerLines, mCornerLinePaint);
	}
	
	private void updateLines() {
	    float clipLeft        = mFrameRect.left;
        float clipRight       = mFrameRect.right;
        float clipTop         = mFrameRect.top;
        float clipBottom      = mFrameRect.bottom;
        float unitWidth       = (clipRight - clipLeft) / WidthCount;
        float unitHeight      = (clipBottom - clipTop) / HeightCount;
        float halfLineWidth   = mFrameLineWidth * 0.5f;
	    int   lineOffset      = 0;
	    int   pointOffset     = 0;
	    
	    // Column grid line
	    pointOffset = 0;
	    for (int width = 1; width < WidthCount; width++) {
	        float top     = clipTop + halfLineWidth;
	        float bottom  = clipBottom - halfLineWidth;
	        float xOffset = unitWidth * width + clipLeft;
	        mGridLines[4 * (width - 1) + 0] = xOffset;
	        mGridLines[4 * (width - 1) + 1] = top;
	        mGridLines[4 * (width - 1) + 2] = xOffset;
	        mGridLines[4 * (width - 1) + 3] = bottom;
	    }
	    
	    // Row grid line
	    pointOffset = (WidthCount - 1) * (HeightCount - 1) * 2;
	    for (int height = 1; height < HeightCount; height++) {
	        float left    = clipLeft + halfLineWidth;
	        float right   = clipRight - halfLineWidth;
	        float yOffset = unitHeight * height + clipTop;
	        mGridLines[4 * (height - 1) + 0 + pointOffset] = left;
	        mGridLines[4 * (height - 1) + 1 + pointOffset] = yOffset;
	        mGridLines[4 * (height - 1) + 2 + pointOffset] = right;
	        mGridLines[4 * (height - 1) + 3 + pointOffset] = yOffset;
	    }
	    
	    // Corner column line
	    for (int width = 0; width < 2; width++) {
	        float xOffset = clipLeft + width * (clipRight - clipLeft);
	        for (int section = 0; section < HeightCount; section++) {
	            if (section == 0) {
	                float top    = clipTop - halfLineWidth;
	                float bottom = clipTop + mCornerLineHeight + halfLineWidth;
	                mCornerLines[lineOffset * 4 + 0] = xOffset;
	                mCornerLines[lineOffset * 4 + 1] = top;
	                mCornerLines[lineOffset * 4 + 2] = xOffset;
	                mCornerLines[lineOffset * 4 + 3] = bottom;
	                lineOffset++;
	            } else if (section == HeightCount - 1) {
	                float top    = clipBottom - mCornerLineHeight - halfLineWidth;
	                float bottom = clipBottom + halfLineWidth;
	                mCornerLines[lineOffset * 4 + 0] = xOffset;
	                mCornerLines[lineOffset * 4 + 1] = top;
	                mCornerLines[lineOffset * 4 + 2] = xOffset;
	                mCornerLines[lineOffset * 4 + 3] = bottom;
	                lineOffset++;
	            } else {
	                float center = clipTop + section * (clipBottom - clipTop) / (HeightCount - 1);
	                float top    = center - mCornerLineHeight * 0.5f;
	                float bottom = center + mCornerLineHeight * 0.5f;
	                mCornerLines[lineOffset * 4 + 0] = xOffset;
	                mCornerLines[lineOffset * 4 + 1] = top;
	                mCornerLines[lineOffset * 4 + 2] = xOffset;
	                mCornerLines[lineOffset * 4 + 3] = bottom;
	                lineOffset++;
	            }
	        }
	    }
	    // Corner row line
	    for (int height = 0; height < 2; height++) {
	        float yOffset = clipTop + height * (clipBottom - clipTop);
	        for (int section = 0; section < WidthCount; section++) {
	            if (section == 0) {
	                float left  = clipLeft - halfLineWidth;
	                float right = clipLeft + mCornerLineHeight + halfLineWidth;
	                mCornerLines[lineOffset * 4 + 0] = left;
	                mCornerLines[lineOffset * 4 + 1] = yOffset;
	                mCornerLines[lineOffset * 4 + 2] = right;
	                mCornerLines[lineOffset * 4 + 3] = yOffset;
	                lineOffset++;
	            } else if (section == WidthCount - 1) {
	                float left  = clipRight - mCornerLineHeight - halfLineWidth;
	                float right = clipRight + halfLineWidth;
	                mCornerLines[lineOffset * 4 + 0] = left;
	                mCornerLines[lineOffset * 4 + 1] = yOffset;
	                mCornerLines[lineOffset * 4 + 2] = right;
	                mCornerLines[lineOffset * 4 + 3] = yOffset;
	                lineOffset++;
	            } else {
	                float center = clipLeft + section * (clipRight - clipLeft) / (WidthCount - 1);
	                float left   = center - mCornerLineHeight * 0.5f;
	                float right  = center + mCornerLineHeight * 0.5f;
	                mCornerLines[lineOffset * 4 + 0] = left;
	                mCornerLines[lineOffset * 4 + 1] = yOffset;
	                mCornerLines[lineOffset * 4 + 2] = right;
	                mCornerLines[lineOffset * 4 + 3] = yOffset;
	                lineOffset++;
	            }
	        }
	    }
	}
	
	@Override
	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent event) {
		boolean isAccepted = mGestureDetector.onTouchEvent(event);
		int     action     = event.getAction();
		if (action == MotionEvent.ACTION_CANCEL
			|| action == MotionEvent.ACTION_UP) {
			onUp(event);
		}
		invalidate();
		return isAccepted;
	}

	@Override
	public boolean onDown(MotionEvent event) {
		if (isTouchedInArea(event)) {
			mCurrentViewState = PRESSED_ENABLED_STATE_SET;
			return true;
		} else {
			return false;
		}
	}

	private void onUp(MotionEvent event) {
		mCurrentViewState = ENABLED_STATE_SET;
		mMotionActions = MOTION_ACTION_NONE;
	}
	
	@Override
	public void onShowPress(MotionEvent event) {
		// Do nothing
	}

	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		// Do nothing
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent eventStart, MotionEvent eventEnd, float distanceX, float distanceY) {
	    changeClipFrame(distanceX, distanceY);
		return false;
	}

	@Override
	public void onLongPress(MotionEvent event) {
		// Do nothing
	}

	@Override
	public boolean onFling(MotionEvent eventStart, MotionEvent eventEnd, float velocityX, float velocityY) {
		// Do nothing
		return false;
	}
	
	private final boolean isTouchedInArea(MotionEvent event) {
        if (mFrameRect == null) {
            return false;
        }

        float   currentX           = event.getX();
        float   currentY           = event.getY();
        float   currentClipWidth   = mFrameRect.width();
        float   currentClipHeight  = mFrameRect.height();
        int     verticalAction     = MOTION_ACTION_NONE;
        int     horizontalAction   = MOTION_ACTION_NONE;

        if (ClipFrameAllowFullareaTouchable) {
            float   unitClipWidth      = currentClipWidth / WidthCount;
            float   unitClipHeight     = currentClipHeight / HeightCount;
            float   halfUnitClipWidth  = unitClipWidth * 0.5f;
            float   halfUnitClipHeight = unitClipHeight * 0.5f;
            float   touchLeftBorder    = mFrameRect.left - halfUnitClipWidth;
            float   touchRightBorder   = mFrameRect.right + halfUnitClipWidth;
            float   touchTopBorder     = mFrameRect.top - halfUnitClipHeight;
            float   touchBottomBorder  = mFrameRect.bottom + halfUnitClipHeight;
            float   moveLeftBorder     = mFrameRect.left + unitClipWidth;
            float   moveRightBorder    = mFrameRect.right - unitClipWidth;
            float   moveTopBorder      = mFrameRect.top + unitClipHeight;
            float   moveBottomBorder   = mFrameRect.bottom - unitClipHeight;

            if (currentX >= touchLeftBorder && currentX <= moveLeftBorder) {
                verticalAction = MOTION_ACTION_LEFT;
            } else if (currentX >= moveLeftBorder && currentX <= moveRightBorder) {
                verticalAction = MOTION_ACTION_LEFT | MOTION_ACTION_RIGHT;
            } else if (currentX > moveRightBorder && currentX <= touchRightBorder) {
                verticalAction = MOTION_ACTION_RIGHT;
            }
            if (currentY >= touchTopBorder && currentY <= moveTopBorder) {
                horizontalAction = MOTION_ACTION_TOP;
            } else if (currentY >= moveTopBorder && currentY <= moveBottomBorder) {
                horizontalAction = MOTION_ACTION_TOP | MOTION_ACTION_BOTTOM;
            } else if (currentY > moveBottomBorder && currentY <= touchBottomBorder) {
                horizontalAction = MOTION_ACTION_BOTTOM;
            }
        } else {
            float acturalCornerLineHeight = mCornerLineHeight;
            float cornerBlockWidth        = acturalCornerLineHeight;
            float cornerBlockHeight       = acturalCornerLineHeight;
            float touchLeftBorder         = mFrameRect.left - cornerBlockWidth;
            float touchRightBorder        = mFrameRect.right + cornerBlockWidth;
            float touchTopBorder          = mFrameRect.top - cornerBlockHeight;
            float touchBottomBorder       = mFrameRect.bottom + currentClipHeight;
            float moveLeftBorder          = mFrameRect.left + cornerBlockWidth * 2;
            float moveRightBorder         = mFrameRect.right - cornerBlockWidth * 2;
            float moveTopBorder           = mFrameRect.top + cornerBlockHeight * 2;
            float moveBottomBorder        = mFrameRect.bottom - cornerBlockHeight * 2;
            if (currentX >= touchLeftBorder && currentX <= moveLeftBorder) {
                verticalAction = MOTION_ACTION_LEFT;
            } else if (currentX >= moveRightBorder && currentX <= touchRightBorder) {
                verticalAction = MOTION_ACTION_RIGHT;
            } else {
                verticalAction = MOTION_ACTION_NONE;
            }
            if (currentY >= touchTopBorder && currentY <= moveTopBorder) {
                horizontalAction = MOTION_ACTION_TOP;
            } else if (currentY >= moveBottomBorder && currentY <= touchBottomBorder) {
                horizontalAction = MOTION_ACTION_BOTTOM;
            } else {
                horizontalAction = MOTION_ACTION_NONE;
            }
        }

        mMotionActions = verticalAction | horizontalAction;
        return mMotionActions != MOTION_ACTION_NONE;
    }
	
	private final void changeClipFrame(float distanceX, float distanceY) {
        if (mFrameRect == null) {
            return;
        }

        float acturalCornerLineHeight = mCornerLineHeight;
        float horizontalSpacing       = 3 * acturalCornerLineHeight;
        float verticalSpacing         = 3 * acturalCornerLineHeight;

        if ((mMotionActions & MOTION_ACTION_LEFT) == MOTION_ACTION_LEFT) {
            mFrameRect.left -= distanceX;
            mFrameRect.left = MathUtils.clamp(mFrameRect.left, mFrameLimitRect.left, mFrameRect.right - horizontalSpacing);
        }
        if ((mMotionActions & MOTION_ACTION_RIGHT) == MOTION_ACTION_RIGHT) {
            mFrameRect.right -= distanceX;
            mFrameRect.right = MathUtils.clamp(mFrameRect.right, mFrameRect.left + horizontalSpacing, mFrameLimitRect.right);
        }
        if ((mMotionActions & MOTION_ACTION_TOP) == MOTION_ACTION_TOP) {
            mFrameRect.top -= distanceY;
            mFrameRect.top = MathUtils.clamp(mFrameRect.top, mFrameLimitRect.top, mFrameRect.bottom - verticalSpacing);
        }
        if ((mMotionActions & MOTION_ACTION_BOTTOM) == MOTION_ACTION_BOTTOM) {
            mFrameRect.bottom -= distanceY;
            mFrameRect.bottom = MathUtils.clamp(mFrameRect.bottom, mFrameRect.top + verticalSpacing, mFrameLimitRect.bottom);
        }
    }
}
