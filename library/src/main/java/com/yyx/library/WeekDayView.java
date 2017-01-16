package com.yyx.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class WeekDayView extends View {
	private final int DEF_TOP_LINE_COLOR = Color.parseColor("#CCE4F2");
	private final int DEF_BOTTOM_LINE_COLOR = Color.parseColor("#CCE4F2");
	private final int DEF_WORK_DAY_COLOR = Color.parseColor("#1FC2F3");
	private final int DEF_WEEKEND_COLOR = Color.parseColor("#fa4451");
	
	//上横线颜色
	private int mTopLineColor;
	//下横线颜色
	private int mBottomLineColor;
	//周一到周五的颜色
	private int mWorkDayColor;
	//周六、周日的颜色
	private int mWeekendColor;
	private boolean isDrawTBLine;
	//线的宽度
	private int mStrokeWidth = 4;
	private int mWeekSize = 14;
	private Paint paint;
	private DisplayMetrics mDisplayMetrics;
	private String[] weekString = new String[]{"日","一","二","三","四","五","六"};

	public WeekDayView(Context context) {
		this(context, null);
	}

	public WeekDayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WeekDayView);
		mBottomLineColor = a.getColor(R.styleable.WeekDayView_bottom_line_color, DEF_BOTTOM_LINE_COLOR);
		mTopLineColor = a.getColor(R.styleable.WeekDayView_top_line_color, DEF_TOP_LINE_COLOR);
		mWorkDayColor = a.getColor(R.styleable.WeekDayView_work_day_color, DEF_WORK_DAY_COLOR);
		mWeekendColor = a.getColor(R.styleable.WeekDayView_weekend_day_color, DEF_WEEKEND_COLOR);
		isDrawTBLine = a.getBoolean(R.styleable.WeekDayView_is_draw_t_b_line, true);
		a.recycle();
		init();
	}

	private void init(){
		mDisplayMetrics = getResources().getDisplayMetrics();
		paint = new Paint();
	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		
		if(heightMode == MeasureSpec.AT_MOST){
			heightSize = mDisplayMetrics.densityDpi * 30;
		}
		if(widthMode == MeasureSpec.AT_MOST){
			widthSize = mDisplayMetrics.densityDpi * 300;
		}
		setMeasuredDimension(widthSize, heightSize);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		paint.setAntiAlias(true);	//抗锯齿
		int width = getWidth();
		int height = getHeight();
		if (isDrawTBLine){
			//进行画上下线
			paint.setStyle(Style.STROKE);
			paint.setColor(mTopLineColor);
			paint.setStrokeWidth(mStrokeWidth);
			canvas.drawLine(0, 0, width, 0, paint);

			//画下横线
			paint.setColor(mBottomLineColor);
			canvas.drawLine(0, height, width, height, paint);
		}
		paint.setStyle(Style.FILL);
		paint.setTextSize(mWeekSize * mDisplayMetrics.scaledDensity);
		int columnWidth = width / 7;
		for(int i=0;i < weekString.length;i++){
			String text = weekString[i];
			int fontWidth = (int) paint.measureText(text);
			int startX = columnWidth * i + (columnWidth - fontWidth)/2;
			int startY = (int) (height/2 - (paint.ascent() + paint.descent())/2);
			if(text.indexOf("日") > -1|| text.indexOf("六") > -1){
				paint.setColor(mWeekendColor);
			}else{
				paint.setColor(mWorkDayColor);
			}
			canvas.drawText(text, startX, startY, paint);
		}
	}

	/**
	 * 设置顶线的颜色
	 * @param mTopLineColor
	 */
	public void setmTopLineColor(int mTopLineColor) {
		this.mTopLineColor = mTopLineColor;
	}

	/**
	 * 设置底线的颜色
	 * @param mBottomLineColor
	 */
	public void setmBottomLineColor(int mBottomLineColor) {
		this.mBottomLineColor = mBottomLineColor;
	}

	/**
	 * 设置周一-五的颜色
	 * @return
	 */
	public void setmWorkDayColor(int mWorkDayColor) {
		this.mWorkDayColor = mWorkDayColor;
	}

	/**
	 * 设置周六、周日的颜色
	 * @param mWeekendColor
	 */
	public void setmWeekendColor(int mWeekendColor) {
		this.mWeekendColor = mWeekendColor;
	}

	/**
	 * 设置边线的宽度
	 * @param mStrokeWidth
	 */
	public void setmStrokeWidth(int mStrokeWidth) {
		this.mStrokeWidth = mStrokeWidth;
	}


	/**
	 * 设置字体的大小
	 * @param mWeekSize
	 */
	public void setmWeekSize(int mWeekSize) {
		this.mWeekSize = mWeekSize;
	}


	/**
	 * 设置星期的形式
	 * @param weekString
	 * 默认值	"日","一","二","三","四","五","六"
	 */
	public void setWeekString(String[] weekString) {
		this.weekString = weekString;
	}
}
