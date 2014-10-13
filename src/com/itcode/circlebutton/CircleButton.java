package com.itcode.circlebutton;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

public class CircleButton extends ImageView {

	private static final int PRESSED_COLOR_LIGHTUP = 255 / 25;
	private static final int PRESSED_RING_ALPHA = 75;
	private static final int DEFAULT_PRESSED_RING_WIDTH_DIP = 10;
	/**
	 * 动画时长的ID
	 */
	private static final int ANIMATION_TIME_ID = android.R.integer.config_shortAnimTime;

	private int centerY;
	private int centerX;
	private int outerRadius;
	private int pressedRingRadius;

	private Paint circlePaint;
	private Paint focusPaint;

	private float animationProgress;

	private int pressedRingWidth;
	private int defaultColor = Color.BLACK;
	private int pressedColor;
	private ObjectAnimator pressedAnimator;

	public CircleButton(Context context) {
		super(context);
		init(context, null);
	}

	public CircleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CircleButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	@Override
	public void setPressed(boolean pressed) {
		super.setPressed(pressed);

		if (circlePaint != null) {
			circlePaint.setColor(pressed ? pressedColor : defaultColor);
		}

		if (pressed) {
			showPressedRing();
		} else {
			hidePressedRing();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawCircle(centerX, centerY, pressedRingRadius + animationProgress, focusPaint);
		canvas.drawCircle(centerX, centerY, outerRadius - pressedRingWidth, circlePaint);
		super.onDraw(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		centerX = w / 2;
		centerY = h / 2;
		outerRadius = Math.min(w, h) / 2;
		pressedRingRadius = outerRadius - pressedRingWidth - pressedRingWidth / 2;
	}

	public float getAnimationProgress() {
		return animationProgress;
	}

	public void setAnimationProgress(float animationProgress) {
		this.animationProgress = animationProgress;
		this.invalidate();
	}

	public void setColor(int color) {
		this.defaultColor = color;
		this.pressedColor = getHighlightColor(color, PRESSED_COLOR_LIGHTUP);

		circlePaint.setColor(defaultColor);
		focusPaint.setColor(defaultColor);
		focusPaint.setAlpha(PRESSED_RING_ALPHA);

		this.invalidate();
	}

	private void hidePressedRing() {
		pressedAnimator.setFloatValues(pressedRingWidth, 0f);
		pressedAnimator.start();
	}

	private void showPressedRing() {
		pressedAnimator.setFloatValues(animationProgress, pressedRingWidth);
		pressedAnimator.start();
	}

	/**
	 * 利用传进来的attrs来初始化控件
	 * @param context
	 * @param attrs
	 */
	private void init(Context context, AttributeSet attrs) {
		this.setFocusable(true);//设置可获取焦点
		/**
		 * Controls how the image should be resized or moved to match the size of this ImageView.
		 * 根据ImageView的大小，控制image的大小
		 * 
		 * 均匀的刻画图像(保持图像的外貌比例)保证图像的尺寸要么等于要么小于相对应的view的尺寸。<br>
		 * 图像被居中于view.对于xml，使用语法：android:scaleType =“centerInside”。
		 */
		this.setScaleType(ScaleType.CENTER_INSIDE);
		setClickable(true);//设置可点击

		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//防边缘有锯齿
		circlePaint.setStyle(Paint.Style.FILL);//填充图像

		focusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		focusPaint.setStyle(Paint.Style.STROKE);//只描绘一个边框

		/**
		 * Converts an unpacked complex data value holding a dimension to its final floating point value.
		 * 将一个未经包装的持有尺寸的复杂数据值转化为浮点值
		 *  The two parameters unit and value are as in TYPE_DIMENSION.
		 * Parameters:
		 * unit ：The unit to convert from.转化前的单位
		 * 
		 * value The value to apply the unit to.数值大小
		 * metrics Current display metrics to use in the conversion 在转化过程中使用的度量(矩阵)
		 * -- supplies display density and scaling information.
		 * Returns:
		 * The complex floating point value multiplied by the appropriate metrics depending on its unit. 
		 * 返回值：数值乘以相对应的度量的值  在给定的单位下的数值大小
		 */
		pressedRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PRESSED_RING_WIDTH_DIP, getResources()
				.getDisplayMetrics());

		int color = Color.BLACK;
		if (attrs != null) {
			final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleButton);
			/**
			 * Retrieve the color value for the attribute at index. 
			 * 获取attri中指定的位置的颜色的值
			 * This symbol is the offset where the com.itcode.circlebutton.R.attr.cb_color attribute's value can be found in the CircleButton array.
			 * 这是一个位移的符号，属性com.itcode.circlebutton.R.attr.cb_color值可在CircleButton 数组中找到
			 * @attr  name com.itcode.circlebutton:cb_color
			 */
			color = a.getColor(R.styleable.CircleButton_cb_color, color);
			/**
			 * Retrieve a dimensional unit attribute at index.
			 * 获取指定位置的尺寸单位属性
			 * Unit conversions are based on the current DisplayMetrics associated with the resources this TypedArray object came from.
			 * Parameters:
			 * index Index of attribute to retrieve.
			 * defValue Value to return if the attribute is not defined or not a resource.
			 * Returns:
			 * Attribute dimension value multiplied by the appropriate metric, or defValue if not defined.
			 * 属性尺寸值乘以相对应的度量
			 */
			pressedRingWidth = (int) a.getDimension(R.styleable.CircleButton_cb_pressedRingWidth, pressedRingWidth);
			a.recycle();
		}

		setColor(color);

		//点击时设置外框的宽度大小
		focusPaint.setStrokeWidth(pressedRingWidth);
		//点击后的动画时长
		final int pressedAnimationTime = getResources().getInteger(ANIMATION_TIME_ID);
		/**
		 * This subclass of ValueAnimator provides support for animating properties on target objects.
		 * 为目标Object提供动画特性的ValueAnimator的子类。
		 * The constructors of this class take parameters to define the target object that will be animated as well as the name of the property that will be animated.
		 * 此类的构造函数使用参数定义目标object及动画的属性名
		 * Appropriate set/get functions are then determined internally and the animation will call these functions as necessary to animate the property.
		 * 适应的set/get函数被内部决定，动画会为必要的动画属性来调用这些函数
		 */
		/**
		 * Constructs and returns an ObjectAnimator that animates between float values. 
		 * 构造并返回一个在float值之间动画的ObjectAnimator
		 * A single value implies that that value is the one being animated to. 
		 * 一个单值表明这个值是被动画到的值
		 * Two values imply a starting and ending values. 
		 * 两个值表明动画的起始与结束的值
		 * More than two values imply a starting value, values to animate through along the way,and an ending value (these values will be distributed evenly across the duration of the animation).
		 * 两个以上的值表明是起始值、动画的持续时间、结束值
		 * Parameters:
		 * target The object whose property is to be animated.This object should have a public method on it called setName(), where name is the value of the propertyName parameter.
		 * 
		 * propertyName The name of the property being animated.
		 * values A set of values that the animation will animate between over time.
		 */
		pressedAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f, 0f);
		pressedAnimator.setDuration(pressedAnimationTime);
	}

	private int getHighlightColor(int color, int amount) {
		return Color.argb(Math.min(255, Color.alpha(color)), Math.min(255, Color.red(color) + amount),
				Math.min(255, Color.green(color) + amount), Math.min(255, Color.blue(color) + amount));
	}
}
