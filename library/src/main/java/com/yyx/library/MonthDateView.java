package com.yyx.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class MonthDateView extends View {
    private static final int NUM_COLUMNS = 7;
    private static final int NUM_ROWS = 6;
    public static final int RECT_SEL_BG = 0;
    public static final int CIRCLE_SEL_BG = 1;
    //当天的日期颜色
    private final int DEF_CURRENT_DAY_COLOR = Color.parseColor("#ff0000");
    //其他日期的颜色
    private final int DEF_DAY_COLOR = Color.parseColor("#000000");
    //选中日期的颜色
    private final int DEF_SEL_DAY_COLOR =  Color.parseColor("#ffffff");
    //选中日期的背景色
    private final int DEF_SEL_BG_COLOR = Color.parseColor("#1FC2F3");
    //标记某天提示点的颜色
    private final int DEF_TIP_COLOR = Color.parseColor("#ff0000");

    private int mBgShape = CIRCLE_SEL_BG;
    private int mDayColor = DEF_DAY_COLOR;
    private int mSelectDayColor = DEF_SEL_DAY_COLOR;
    private int mSelectBGColor = DEF_SEL_BG_COLOR;
    private int mCurrentColor = DEF_CURRENT_DAY_COLOR;
    private int mTipColor = DEF_TIP_COLOR;
    private Paint mPaint;
    private int mCurrYear, mCurrMonth, mCurrDay;
    private int mSelYear, mSelMonth, mSelDay;
    private int mColumnSize, mRowSize;
    private DisplayMetrics mDisplayMetrics;
    private int mDaySize = 18;
    private TextView tv_date, tv_week;
    private int weekRow;
    private int[][] daysString;
    private int mCircleRadius = 6;
    private DateClick dateClick;
    private List<Integer> daysHasThingList;
    
    public MonthDateView(Context context){
        super(context);
        init();
    }

    public MonthDateView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MonthDateView);
        mCurrentColor = a.getColor(R.styleable.MonthDateView_current_day_color, DEF_CURRENT_DAY_COLOR);
        mDayColor = a.getColor(R.styleable.MonthDateView_day_color, DEF_DAY_COLOR);
        mSelectDayColor = a.getColor(R.styleable.MonthDateView_select_day_color, DEF_SEL_DAY_COLOR);
        mSelectBGColor = a.getColor(R.styleable.MonthDateView_select_day_bg_color, DEF_SEL_BG_COLOR);
        mTipColor = a.getColor(R.styleable.MonthDateView_tip_color, DEF_TIP_COLOR);
        mBgShape = a.getInt(R.styleable.MonthDateView_shape_type, CIRCLE_SEL_BG);
        a.recycle();
        
        init();    
    }
    
    private void init(){
        mDisplayMetrics = getResources().getDisplayMetrics();
        Calendar calendar = Calendar.getInstance();
        mPaint = new Paint();
        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrMonth = calendar.get(Calendar.MONTH);
        mCurrDay = calendar.get(Calendar.DATE);
        setSelectYearMonth(mCurrYear, mCurrMonth, mCurrDay);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initSize();
        daysString = new int[6][7];
        mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);
        mPaint.setAntiAlias(true);  //抗锯齿
        String dayString;
        int mMonthDays = DateUtils.getMonthDays(mSelYear, mSelMonth);
        int weekNumber = DateUtils.getFirstDayWeek(mSelYear, mSelMonth);
        Log.d("DateView", "DateView:" + mSelMonth + "月1号周" + weekNumber);
        for (int day = 0; day < mMonthDays; day++) {
            dayString = (day + 1) + "";
            int column = (day + weekNumber - 1) % 7;
            int row = (day + weekNumber - 1) / 7;
            daysString[row][column] = day + 1;
            int startX = (int) (mColumnSize * column + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize * row + mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            if (dayString.equals(mSelDay + "")) {
                //绘制背景色矩形
                int startRecX = mColumnSize * column;
                int startRecY = mRowSize * row;
                int endRecX = startRecX + mColumnSize;
                int endRecY = startRecY + mRowSize;
                mPaint.setColor(mSelectBGColor);
                if (mBgShape == CIRCLE_SEL_BG) {
                    Rect rect = new Rect(startRecX, startRecY, endRecX, endRecY);
                    int radius = rect.width() > rect.height() ? rect.height() / 2 : rect.width() / 2;
                    canvas.drawCircle(rect.centerX(), rect.centerY(), radius, mPaint);

                } else if (mBgShape == RECT_SEL_BG) {
                    canvas.drawRect(startRecX, startRecY, endRecX, endRecY, mPaint);
                }
                //记录第几行，即第几周
                weekRow = row + 1;
            }
            //绘制事务圆形标志
            drawCircle(row, column, day + 1, canvas);
            if (dayString.equals(mSelDay + "")) {
                mPaint.setColor(mSelectDayColor);
            } else if (dayString.equals(mCurrDay + "") && mCurrDay != mSelDay && mCurrMonth == mSelMonth) {
                //正常月，选中其他日期，则今日为红色
                mPaint.setColor(mCurrentColor);
            } else {
                mPaint.setColor(mDayColor);
            }
            canvas.drawText(dayString, startX, startY, mPaint);
            if (tv_date != null) {
                tv_date.setText(mSelYear + "年" + (mSelMonth + 1) + "月");
            }

            if (tv_week != null) {
                tv_week.setText("第" + weekRow + "周");
            }
        }
    }

    private void drawCircle(int row, int column, int day, Canvas canvas) {
        if (daysHasThingList != null && daysHasThingList.size() > 0) {
            if (!daysHasThingList.contains(day)) return;
            mPaint.setColor(mTipColor);
            float circleX = (float) (mColumnSize * column + mColumnSize * 0.8);
            float circley = (float) (mRowSize * row + mRowSize * 0.2);
            canvas.drawCircle(circleX, circley, mCircleRadius, mPaint);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private int downX = 0, downY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventCode = event.getAction();
        switch (eventCode) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int upX = (int) event.getX();
                int upY = (int) event.getY();
                if (Math.abs(upX - downX) < 10 && Math.abs(upY - downY) < 10) {//点击事件
                    performClick();
                    doClickAction((upX + downX) / 2, (upY + downY) / 2);
                }
                break;
        }
        return true;
    }

    /**
     * 初始化列宽行高
     */
    private void initSize() {
        mColumnSize = getWidth() / NUM_COLUMNS;
        mRowSize = getHeight() / NUM_ROWS;
    }

    /**
     * 设置年月
     *
     * @param year
     * @param month
     */
    public void setSelectYearMonth(int year, int month, int day) {
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
    }

    public void setSelectDate(int year, int month, int day) {
        setSelectYearMonth(year, month, day);
        invalidate();
    }

    /**
     * 执行点击事件
     *
     * @param x
     * @param y
     */
    private void doClickAction(int x, int y) {
        int row = y / mRowSize;
        int column = x / mColumnSize;
        setSelectYearMonth(mSelYear, mSelMonth, daysString[row][column]);
        invalidate();
        //执行activity发送过来的点击处理事件
        if (dateClick != null) {
            dateClick.onClickOnDate(mSelYear, mSelMonth, mSelDay);
        }
    }

    /**
     * 左点击，日历向后翻页
     */
    public void onPreviousClick() {
        int year = mSelYear;
        int month = mSelMonth;
        int day = mSelDay;
        if (month == 0) {//若果是1月份，则变成12月份
            year = mSelYear - 1;
            month = 11;
        }else{
            month--;
        }
        if (day > DateUtils.getMonthDays(year, month)){
            day = DateUtils.getMonthDays(year, month);
        }
        setSelectYearMonth(year, month, day);
        invalidate();
    }

    /**
     * 右点击，日历向前翻页
     */
    public void onNextClick() {
        int year = mSelYear;
        int month = mSelMonth;
        int day = mSelDay;
        if (month == 11) {//若果是12月份，则变成1月份
            year = mSelYear + 1;
            month = 0;
        } else{
            month = month + 1;
        }

        if (day > DateUtils.getMonthDays(year, month)){
            day = DateUtils.getMonthDays(year, month);
        }

        setSelectYearMonth(year, month, day);
        invalidate();
    }

    /**
     * 获取选择的年份
     *
     * @return
     */
    public int getmSelYear() {
        return mSelYear;
    }

    /**
     * 获取选择的月份
     *
     * @return
     */
    public int getmSelMonth() {
        return mSelMonth;
    }

    /**
     * 获取选择的日期
     */
    public int getmSelDay() {
        return this.mSelDay;
    }

    /**
     * 普通日期的字体颜色，默认黑色
     *
     * @param mDayColor
     */
    public void setmDayColor(int mDayColor) {
        this.mDayColor = mDayColor;
    }

    /**
     * 选择日期的颜色，默认为白色
     *
     * @param mSelectDayColor
     */
    public void setmSelectDayColor(int mSelectDayColor) {
        this.mSelectDayColor = mSelectDayColor;
    }

    /**
     * 选中日期的背景颜色，默认蓝色
     *
     * @param mSelectBGColor
     */
    public void setmSelectBGColor(int mSelectBGColor) {
        this.mSelectBGColor = mSelectBGColor;
    }

    /**
     * 选中日期的背景形状(RECR_SEL_BG:矩形,CIRCLE_SEL_BG:圆形)
     * @param shape
     */
    public void setSelectedBgShape(int shape){
        mBgShape = shape;
    }

    /**
     * 当前日期不是选中的颜色，默认红色
     *
     * @param mCurrentColor
     */
    public void setmCurrentColor(int mCurrentColor) {
        this.mCurrentColor = mCurrentColor;
    }


    /**
     * 日期的大小，默认18sp
     *
     * @param mDaySize
     */
    public void setmDaySize(int mDaySize) {
        this.mDaySize = mDaySize;
    }

    /**
     * 设置显示当前日期的控件
     *
     * @param tv_date 显示日期
     * @param tv_week 显示周
     */
    public void setTextView(TextView tv_date, TextView tv_week) {
        this.tv_date = tv_date;
        this.tv_week = tv_week;
        invalidate();
    }

    /**
     * 设置事务天数
     *
     * @param daysHasThingList
     */
    public void setDaysHasThingList(List<Integer> daysHasThingList) {
        this.daysHasThingList = daysHasThingList;
    }

    /***
     * 设置圆圈的半径，默认为6
     *
     * @param mCircleRadius
     */
    public void setmCircleRadius(int mCircleRadius) {
        this.mCircleRadius = mCircleRadius;
    }

    /**
     * 设置圆圈的半径
     *
     * @param mTipColor
     */
    public void setmTipColor(int mTipColor) {
        this.mTipColor = mTipColor;
    }

    /**
     * 设置日期的点击回调事件
     *
     * @author shiwei.deng
     */
    public interface DateClick {
        public void onClickOnDate(int year, int month, int day);
    }

    /**
     * 设置日期点击事件
     *
     * @param dateClick
     */
    public void setDateClick(DateClick dateClick) {
        this.dateClick = dateClick;
    }


    /**
     * 跳转至今天
     */
    public void setTodayToView() {
        setSelectYearMonth(mCurrYear, mCurrMonth, mCurrDay);
        invalidate();
    }
}
