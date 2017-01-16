# CalendarDateView
一个Android日期选择控件，支持无限滑动
##效果图：
![效果图](https://github.com/xingxing-yan/CalendarDateView/blob/master/git/CalendarDemo.gif)
##使用方式：
使用起来也非常方便， 只需要在布局文件中加入如下代码即可：
```
 <com.yyx.library.WeekDayView
       android:id="@+id/main_wdv"
       android:layout_width="match_parent"
       android:layout_height="40dp"
       app:top_line_color="@color/black"
       app:bottom_line_color="@color/black"
       app:work_day_color="@color/black"
       app:weekend_day_color="@android:color/holo_red_light" />
    <com.yyx.library.CalendarDateView
        android:id="@+id/main_cdv"
        android:layout_width="match_parent"
        android:layout_height="300dp"
        app:current_day_color="@android:color/holo_red_light"
        app:day_color="@color/black"
        app:select_day_color="@color/white"
        app:select_day_bg_color="@android:color/holo_green_light"
        app:shape_type="circle"/>
        
```
        
WeekDayView周一到周日的文字控件，支持设置周末颜色和工作日的颜色。<br/>
CalendarDateView是日期选择控件，支持日期颜色设置和选中日期的颜色，选择日期的背景色等<br/>
注意：WeekDayView和CalendarDateView的高度需要一个固定值，不能设置为wrap_content
```
mCdv.setDateTextView(mTvDate);
```

设置一个TextView展示当前选中的日期

##监听日期的点击事件

```
mCdv.setOnItemClickListener(new CalendarDateView.OnItemClickListener() {
  @Override
  public void onItemClick(int year, int month, int day) {
    String date = year + "-" + (month+1) + "-" + day;
    Toast.makeText(MainActivity.this, date, Toast.LENGTH_SHORT).show();
  }
});
   
```

##自定义属性列表：

```
<declare-styleable name="WeekDayView">
        <!--上横线颜色-->
        <attr name="top_line_color" format="color"/>
        <!--下横线颜色-->
        <attr name="bottom_line_color" format="color"/>
        <!--工作日颜色-->
        <attr name="work_day_color" format="color"/>
        <!--周末颜色-->
        <attr name="weekend_day_color" format="color"/>
       <!--是否画上下横向-->
        <attr name="is_draw_t_b_line" format="boolean"/>
    </declare-styleable>
    <declare-styleable name="MonthDateView">
        <!--当天的颜色-->
        <attr name="current_day_color" format="color"/>
        <!--其他日期的颜色-->
        <attr name="day_color" format="color"/>
        <!--被选中日期的颜色-->
        <attr name="select_day_color" format="color"/>
        <!--被选中日期的背景色-->
        <attr name="select_day_bg_color" format="color"/>
        <!--提示点的颜色-->
        <attr name="tip_color" format="color"/>
        <!--被选中日期的背景形状-->
        <attr name="shape_type" format="enum">
            <enum name="rect" value="0"/>
            <enum name="circle" value="1"/>
        </attr>
    </declare-styleable>
    
```
