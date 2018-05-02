package com.renren0351.rrzzapp.wigets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * <pre>
 *     author : 李小勇
 *     time   : 2017/07/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class BarChartView extends View{
    private static final String TAG = BarChartView.class.getSimpleName();
    //画笔
    private Paint mPaint;

    private Paint mTextPaint;
    //图例图形画笔
    private Paint mLegendPaint;
    //图例文字画笔
    private Paint mLegendTextPaint;
    //视图宽度
    private int width;
    //视图高度
    private int height;
    //坐标原点位置
    private int originX;
    private int originY;
    private List<String> xData;
    private List<Float> yData;
    private float maxYValue = -1;
    private String legendText;
    private int[] colors = {Color.BLUE,Color.CYAN,Color.YELLOW,Color.RED,Color.GREEN};

    public BarChartView(Context context) {
        this(context,null);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint();

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(20);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mLegendPaint = new Paint();
        mLegendPaint.setAntiAlias(true);
        mLegendPaint.setStyle(Paint.Style.FILL);
        mLegendPaint.setStrokeWidth(1);
        mLegendPaint.setColor(Color.BLACK);

        mLegendTextPaint = new Paint();
        mLegendTextPaint.setAntiAlias(true);
        mLegendTextPaint.setTextAlign(Paint.Align.CENTER);
        mLegendTextPaint.setColor(Color.BLACK);
        mLegendTextPaint.setTextSize(20);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == View.MeasureSpec.EXACTLY || widthSpecMode == View.MeasureSpec.AT_MOST) {
            width = widthSpecSize;
        } else {
            width = 600;
        }
        if (heightSpecMode == View.MeasureSpec.AT_MOST || heightSpecMode == View.MeasureSpec.UNSPECIFIED) {
            height = 400;
        } else {
            height = heightSpecSize;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public void onDraw(Canvas canvas) {
        width = getMeasuredWidth() - 160;
        height = getMeasuredHeight() - 80;
        originX = 80;
        originY = height + 40;
        if (xData != null && yData != null && xData.size() > 0 && xData.size() == yData.size()) {
            canvas.drawColor(Color.WHITE);
            drawAxisX(canvas, mPaint);
            drawAxisY(canvas, mPaint);
            drawAxisScaleMarkX(canvas, mPaint);
            drawAxisScaleMarkY(canvas, mPaint);
            drawAxisArrowsX(canvas, mPaint);
            drawAxisArrowsY(canvas, mPaint);
            drawAxisScaleMarkValueX(canvas, mPaint);
            drawAxisScaleMarkValueY(canvas, mPaint);
            drawBar(canvas, mPaint);
            drawLegend(canvas);
        }else {
            Log.i(TAG, "onDraw: 没有数据");
        }

        //drawTitle(canvas, mPaint);
    }

    /**
     * 画图例
     * @param canvas
     */
    private void drawLegend(Canvas canvas) {
        if (legendText == null){
            return;
        }
        canvas.drawCircle(originX + 30, originY - height - 5, 10, mLegendPaint);
        canvas.drawText(legendText,originX + 100,originY - height,mLegendTextPaint);
    }



    /**
     * 绘制横坐标轴（X轴）
     *
     * @param canvas
     * @param paint
     */
    private void drawAxisX(Canvas canvas, Paint paint) {
        paint.setColor(Color.GRAY);
        //设置画笔宽度
        paint.setStrokeWidth(5);
        //设置画笔抗锯齿
        paint.setAntiAlias(true);
        //画横轴(X)
        canvas.drawLine(originX, originY, originX + width, originY, paint);
    }

    /**
     * 绘制纵坐标轴(Y轴)
     *
     * @param canvas
     * @param paint
     */
    private void drawAxisY(Canvas canvas, Paint paint) {
        //画竖轴(Y)
        canvas.drawLine(originX, originY, originX, originY - height, paint);//参数说明：起始点左边x,y，终点坐标x,y，画笔
    }


    /**
     * 绘制横坐标轴刻度线(X轴)
     *
     * @param canvas
     * @param paint
     */
    private void drawAxisScaleMarkX(Canvas canvas, Paint paint) {
        if (xData == null || xData.size() < 1){
            throw new RuntimeException("X轴没有数据");
        }else {
            float cellWidth = width / xData.size();
            for (int i = 0; i < xData.size() - 1; i++) {
                canvas.drawLine(cellWidth * (i + 1) + originX, originY, cellWidth * (i + 1) + originX, originY - 10, paint);
            }
        }

    }

    /**
     * 绘制纵坐标轴刻度线(Y轴)
     *
     * @param canvas
     * @param paint
     */
    private void drawAxisScaleMarkY(Canvas canvas, Paint paint) {
        float cellHeight = ( height - 20 ) / 4;
        for (int i = 0; i < 4; i++) {
            canvas.drawLine(originX, (originY - cellHeight * (i + 1)), originX + 10, (originY - cellHeight * (i + 1)), paint);
        }
    }

    /**
     * 绘制横坐标轴刻度值(X轴)
     *
     * @param canvas
     * @param paint
     */
    private void drawAxisScaleMarkValueX(Canvas canvas, Paint paint) {
        //设置画笔绘制文字的属性
        paint.setColor(Color.GRAY);
        paint.setTextSize(20);
        paint.setFakeBoldText(true);

        float cellWidth = width / xData.size();
        for (int i = 0; i < xData.size() ; i++) {
            canvas.drawText(xData.get(i), cellWidth * (i + 1 ) + originX - 35, originY + 30, paint);
        }
    }

    /**
     * 绘制纵坐标轴刻度值(Y轴)
     *
     * @param canvas
     * @param paint
     */
    private void drawAxisScaleMarkValueY(Canvas canvas, Paint paint) {
        float cellHeight = (height - 20) / 4;
        float cellValue = maxYValue / 4;
        for (int i = 0; i < 5; i++) {
            canvas.drawText(String.format("%.2f", cellValue * i), originX - 50, originY - cellHeight * i + 10, paint);
        }
    }

    /**
     * 绘制横坐标轴箭头(X轴)
     *
     * @param canvas
     * @param paint
     */
    private void drawAxisArrowsX(Canvas canvas, Paint paint) {
        //画三角形（X轴箭头）
        Path mPathX = new Path();
        mPathX.moveTo(originX + width + 30, originY);//起始点
        mPathX.lineTo(originX + width, originY - 10);//下一点
        mPathX.lineTo(originX + width, originY + 10);//下一点
        mPathX.close();
        canvas.drawPath(mPathX, paint);
    }

    /**
     * 绘制纵坐标轴箭头(Y轴)
     *
     * @param canvas
     * @param paint
     */
    private void drawAxisArrowsY(Canvas canvas, Paint paint) {
        //画三角形（Y轴箭头）
        Path mPathX = new Path();
        mPathX.moveTo(originX, originY - height - 30);//起始点
        mPathX.lineTo(originX - 10, originY - height);//下一点
        mPathX.lineTo(originX + 10, originY - height);//下一点
        mPathX.close();
        canvas.drawPath(mPathX, paint);
    }

    /**
     * 绘制柱状图
     *
     * @param canvas
     * @param paint
     */
    private void drawBar(Canvas canvas, Paint paint) {
        float cellWidth = width / xData.size();
        originX = originX + 2;
        mTextPaint.setColor(Color.BLACK);
        for (int i = 0; i < xData.size(); i++) {
            paint.setColor(colors[i % colors.length]);
            float leftTopY = originY - (height - 20) * yData.get(i) / maxYValue;
            canvas.drawRect(originX + cellWidth * (i), leftTopY, originX + cellWidth * (i + 1), originY-2, mPaint);//左上角x,y右下角x,y，画笔
            canvas.drawText(String.valueOf(yData.get(i)), originX + cellWidth * (i) + cellWidth / 2,
                    leftTopY - 10, mTextPaint);
        }
    }

    public void setXData(List<String> xData){
        this.xData = xData;
    }

    public void setYData(List<Float> yData){
        this.yData = yData;
        for (Float f : yData){
            if (maxYValue < f){
                maxYValue = f;
            }
        }
    }

    public void setLegendText(String text){
        legendText = text;
    }

    public void setLegendColor(int color){
        mLegendPaint.setColor(color);
        mLegendTextPaint.setColor(color);
    }

    public void show(){
        invalidate();
    }
}
