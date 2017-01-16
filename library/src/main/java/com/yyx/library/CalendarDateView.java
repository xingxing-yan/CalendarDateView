package com.yyx.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Administrator on 2017/1/13.
 * 日历滑动控件
 */

public class CalendarDateView extends ViewPager {
    public static final int RECT_SEL_BG = 0;    //矩形背景
    public static final int CIRCLE_SEL_BG = 1;  //圆形背景
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

    private int mBgShape;
    private int mDayColor;
    private int mSelectDayColor;
    private int mSelectBGColor;
    private int mCurrentColor;
    private int mTipColor;

    private MonthDateView.DateClick onDateClickListener;
    HashMap<Integer, MonthDateView> views = new HashMap<>();
    private LinkedList<MonthDateView> cache = new LinkedList();

    private OnItemClickListener onItemClickListener;

    private int mCurrentPos = Integer.MAX_VALUE/2;
    private TextView mTvDate;
    private int mSelYear, mSelMonth, mSelDay;

    public CalendarDateView(Context context) {
       this(context, null);
    }

    public CalendarDateView(Context context, AttributeSet attrs) {
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

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    public void setDateTextView(TextView date){
        this.mTvDate = date;
        mTvDate.setText(geSelectedDate());
    }

    private void init(){
        Calendar calendar = Calendar.getInstance();
        mSelYear = calendar.get(Calendar.YEAR);
        mSelMonth = calendar.get(Calendar.MONTH);
        mSelDay = calendar.get(Calendar.DATE);

        onDateClickListener = new MonthDateView.DateClick() {
            @Override
            public void onClickOnDate(int year, int month, int day) {
                mSelYear = year;
                mSelMonth = month;
                mSelDay = day;
                if (mTvDate != null){
                    mTvDate.setText(geSelectedDate());
                }
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(mSelYear, mSelMonth, mSelDay);
                }
            }
        };

        setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                //实现无限滑动
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                MonthDateView view;
                if (!cache.isEmpty()){
                    view = cache.removeFirst();
                }else{

                    view = new MonthDateView(container.getContext());
                    view.setmDayColor(mDayColor);
                    view.setmSelectBGColor(mSelectBGColor);
                    view.setmSelectDayColor(mSelectDayColor);
                    view.setmCurrentColor(mCurrentColor);
                    view.setmTipColor(mTipColor);
                    view.setSelectedBgShape(mBgShape);
                }
                view.setDateClick(onDateClickListener);
                view.setSelectDate(mSelYear, mSelMonth, mSelDay);
                container.addView(view);
                views.put(position, view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
                cache.addLast((MonthDateView) object);
                views.remove(position);
            }

        });
        //设置初始位置在Integer.MAXVALUE的1/2处，因为日历可向前向后滑动
        setCurrentItem(mCurrentPos);

        addOnPageChangeListener(new SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                MonthDateView view = views.get(position);
                if (position > mCurrentPos){
                    nextMonth(view);
                }else if (position < mCurrentPos){
                    previousMonth(view);
                }

                mCurrentPos = position;
                if (mTvDate != null){
                    mTvDate.setText(geSelectedDate());
                }
            }
        });

    }

    /**
     * 获取当前选中的日期
     * @return
     */
    private String geSelectedDate(){
        StringBuilder sb = new StringBuilder();
        sb.append(mSelYear);
        sb.append("-");
        sb.append(mSelMonth+1);
        sb.append("-");
        sb.append(mSelDay);
        return sb.toString();
    }

    /**
     * 下一个月
     * @param view
     */
    private void nextMonth(MonthDateView view){
        int year = mSelYear;
        int month = mSelMonth;
        int day = mSelDay;
        if(month == 11){//若果是12月份，则变成1月份
            year = mSelYear+1;
            month = 0;
        }else{
            month++;
        }
        if (day > DateUtils.getMonthDays(year, month)){
            day = DateUtils.getMonthDays(year, month);
        }
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
        view.setSelectDate(mSelYear, mSelMonth, mSelDay);
    }

    /**
     * 上一个月
     * @param view
     */
    private void previousMonth(MonthDateView view){
        int year = mSelYear;
        int month = mSelMonth;
        int day = mSelDay;
        if(month == 0){//若果是1月份，则变成12月份
            year = mSelYear-1;
            month = 11;
        }else{
            month--;
        }
        if (day > DateUtils.getMonthDays(year, month)){
            day = DateUtils.getMonthDays(year, month);
        }
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
        view.setSelectDate(mSelYear, mSelMonth, mSelDay);
    }

    public interface OnItemClickListener{
        void onItemClick(int year, int month, int day);
    }

    public int getSelYear() {
        return mSelYear;
    }

    public void setSelYear(int mSelYear) {
        this.mSelYear = mSelYear;
    }

    public int getSelMonth() {
        return mSelMonth;
    }

    public void setSelMonth(int mSelMonth) {
        this.mSelMonth = mSelMonth;
    }

    public int getSelDay() {
        return mSelDay;
    }

    public void setSelDay(int mSelDay) {
        this.mSelDay = mSelDay;
    }

    /**
     * 设置选中日期背景形状
     * @param mBgShape
     */
    public void setBgShape(int mBgShape) {
        this.mBgShape = mBgShape;
    }

    /**
     * 设置其他日期颜色
     * @param mDayColor
     */
    public void setDayColor(int mDayColor) {
        this.mDayColor = mDayColor;
    }

    /**
     * 设置选中日期的颜色
     * @param mSelectDayColor
     */
    public void setSelectDayColor(int mSelectDayColor) {
        this.mSelectDayColor = mSelectDayColor;
    }

    /**
     * 设置选中日期的背景色
     * @param mSelectBGColor
     */
    public void setSelectBGColor(int mSelectBGColor) {
        this.mSelectBGColor = mSelectBGColor;
    }

    /**
     * 设置当天日期颜色
     * @param mCurrentColor
     */
    public void setCurrentColor(int mCurrentColor) {
        this.mCurrentColor = mCurrentColor;
    }

    /**
     * 设置提示点颜色
     * @param mTipColor
     */
    public void setTipColor(int mTipColor) {
        this.mTipColor = mTipColor;
    }
}
