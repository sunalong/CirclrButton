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
	 * ����ʱ����ID
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
	 * ���ô�������attrs����ʼ���ؼ�
	 * @param context
	 * @param attrs
	 */
	private void init(Context context, AttributeSet attrs) {
		this.setFocusable(true);//���ÿɻ�ȡ����
		/**
		 * Controls how the image should be resized or moved to match the size of this ImageView.
		 * ����ImageView�Ĵ�С������image�Ĵ�С
		 * 
		 * ���ȵĿ̻�ͼ��(����ͼ�����ò����)��֤ͼ��ĳߴ�Ҫô����ҪôС�����Ӧ��view�ĳߴ硣<br>
		 * ͼ�񱻾�����view.����xml��ʹ���﷨��android:scaleType =��centerInside����
		 */
		this.setScaleType(ScaleType.CENTER_INSIDE);
		setClickable(true);//���ÿɵ��

		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//����Ե�о��
		circlePaint.setStyle(Paint.Style.FILL);//���ͼ��

		focusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		focusPaint.setStyle(Paint.Style.STROKE);//ֻ���һ���߿�

		/**
		 * Converts an unpacked complex data value holding a dimension to its final floating point value.
		 * ��һ��δ����װ�ĳ��гߴ�ĸ�������ֵת��Ϊ����ֵ
		 *  The two parameters unit and value are as in TYPE_DIMENSION.
		 * Parameters:
		 * unit ��The unit to convert from.ת��ǰ�ĵ�λ
		 * 
		 * value The value to apply the unit to.��ֵ��С
		 * metrics Current display metrics to use in the conversion ��ת��������ʹ�õĶ���(����)
		 * -- supplies display density and scaling information.
		 * Returns:
		 * The complex floating point value multiplied by the appropriate metrics depending on its unit. 
		 * ����ֵ����ֵ�������Ӧ�Ķ�����ֵ  �ڸ����ĵ�λ�µ���ֵ��С
		 */
		pressedRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PRESSED_RING_WIDTH_DIP, getResources()
				.getDisplayMetrics());

		int color = Color.BLACK;
		if (attrs != null) {
			final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleButton);
			/**
			 * Retrieve the color value for the attribute at index. 
			 * ��ȡattri��ָ����λ�õ���ɫ��ֵ
			 * This symbol is the offset where the com.itcode.circlebutton.R.attr.cb_color attribute's value can be found in the CircleButton array.
			 * ����һ��λ�Ƶķ��ţ�����com.itcode.circlebutton.R.attr.cb_colorֵ����CircleButton �������ҵ�
			 * @attr  name com.itcode.circlebutton:cb_color
			 */
			color = a.getColor(R.styleable.CircleButton_cb_color, color);
			/**
			 * Retrieve a dimensional unit attribute at index.
			 * ��ȡָ��λ�õĳߴ絥λ����
			 * Unit conversions are based on the current DisplayMetrics associated with the resources this TypedArray object came from.
			 * Parameters:
			 * index Index of attribute to retrieve.
			 * defValue Value to return if the attribute is not defined or not a resource.
			 * Returns:
			 * Attribute dimension value multiplied by the appropriate metric, or defValue if not defined.
			 * ���Գߴ�ֵ�������Ӧ�Ķ���
			 */
			pressedRingWidth = (int) a.getDimension(R.styleable.CircleButton_cb_pressedRingWidth, pressedRingWidth);
			a.recycle();
		}

		setColor(color);

		//���ʱ�������Ŀ�ȴ�С
		focusPaint.setStrokeWidth(pressedRingWidth);
		//�����Ķ���ʱ��
		final int pressedAnimationTime = getResources().getInteger(ANIMATION_TIME_ID);
		/**
		 * This subclass of ValueAnimator provides support for animating properties on target objects.
		 * ΪĿ��Object�ṩ�������Ե�ValueAnimator�����ࡣ
		 * The constructors of this class take parameters to define the target object that will be animated as well as the name of the property that will be animated.
		 * ����Ĺ��캯��ʹ�ò�������Ŀ��object��������������
		 * Appropriate set/get functions are then determined internally and the animation will call these functions as necessary to animate the property.
		 * ��Ӧ��set/get�������ڲ�������������Ϊ��Ҫ�Ķ���������������Щ����
		 */
		/**
		 * Constructs and returns an ObjectAnimator that animates between float values. 
		 * ���첢����һ����floatֵ֮�䶯����ObjectAnimator
		 * A single value implies that that value is the one being animated to. 
		 * һ����ֵ�������ֵ�Ǳ���������ֵ
		 * Two values imply a starting and ending values. 
		 * ����ֵ������������ʼ�������ֵ
		 * More than two values imply a starting value, values to animate through along the way,and an ending value (these values will be distributed evenly across the duration of the animation).
		 * �������ϵ�ֵ��������ʼֵ�������ĳ���ʱ�䡢����ֵ
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
