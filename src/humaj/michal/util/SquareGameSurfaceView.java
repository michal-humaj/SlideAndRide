package humaj.michal.util;

import humaj.michal.gameoffifteen.SurfaceRenderer;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SquareGameSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {

	private SurfaceRenderer mSurfaceRenderer = null;

	public SquareGameSurfaceView(Context context) {
		super(context);
		getHolder().addCallback(this);
	}

	public SquareGameSurfaceView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		getHolder().addCallback(this);
	}

	public SquareGameSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {		
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {		
		mSurfaceRenderer.pause();
	}

	public void setSurfaceRenderer(SurfaceRenderer sr) {
		mSurfaceRenderer = sr;
		mSurfaceRenderer.setSurfaceHolder(getHolder());
	}	

	@Override
	protected void onMeasure(final int widthMeasureSpec,
			final int heightMeasureSpec) {
		int width = getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        width = height < width ? height : width;
        setMeasuredDimension(width, width);
	}

	@Override
	protected void onSizeChanged(final int w, final int h, final int oldw,
			final int oldh) {
		super.onSizeChanged(w, w, oldw, oldh);
	}
}
