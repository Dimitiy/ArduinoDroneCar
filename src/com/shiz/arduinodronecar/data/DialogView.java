package com.shiz.arduinodronecar.data;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.android.util.Logging;
import com.shiz.arduinodronecar.R;

public class DialogView extends View {
	private Paint paint;
	private Resources r;

	public DialogView(Context context) {
		super(context);
		System.out.println("test GraphView");
		this.setWillNotDraw(false);
		init();
	}

	public DialogView(Context context, AttributeSet attrs) {
		super(context, attrs);
		System.out.println("test GraphView");
		this.setWillNotDraw(false);
		init();
	}

	public void init() {
		setFocusable(true);
		// Get external resources
		r = this.getResources();

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(r.getColor(R.color.marker_color));
		paint.setAlpha(200);
		paint.setStrokeWidth(1);
		paint.setStyle(Paint.Style.STROKE);
		paint.setShadowLayer(2, 1, 1, r.getColor(R.color.shadow_color));

	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Logging.doLog("DialogView", "test onDraw", "test onDraw");
		int height = getMeasuredHeight();
		int width = getMeasuredWidth();
		int paddingWidth = (int) (width- r.getDimension(R.dimen.padding_horizontal_switch_dialog));
		Path p = new Path();
		Point mid = new Point();
		
		// ...
		Point start = new Point(width/25, height - 20);
		Point end = new Point(paddingWidth, height/11);
		Path path = new Path();
		path.moveTo(paddingWidth - 40, height / 13);

		path.lineTo(paddingWidth, height/11);
		path.moveTo(paddingWidth - 40, (height / 9));

		path.lineTo(paddingWidth, height/11);

		canvas.drawPath(path, paint);
		path.reset();

		mid.set((start.x + end.x) / 2, (start.y + end.y) / 2);

		// Draw line connecting the two points:
		p.reset();
		p.moveTo(start.x, start.y);
		p.quadTo((start.x + mid.x) / 2, start.y, mid.x, mid.y);
		p.quadTo((mid.x + end.x) / 2, end.y, end.x, end.y);
		canvas.drawPath(p, paint);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// The compass is a circle that fills as much space as possible.
		// Set the measured dimensions by figuring out the shortest boundary,
		// height or width.
		int measuredWidth = measure(widthMeasureSpec);
		int measuredHeight = measure(heightMeasureSpec);

		int d = Math.min(measuredWidth, measuredHeight);

		setMeasuredDimension(d, d);
	}

	private int measure(int measureSpec) {
		int result = 0;

		// Decode the measurement specifications.
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.UNSPECIFIED) {
			// Return a default size of 200 if no bounds are specified.
			result = 200;
		} else {
			// As you want to fill the available space
			// always return the full available bounds.
			result = specSize;
		}
		return result;
	}
}