package humaj.michal.gameoffifteen;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import humaj.michal.activity.GameActivity.TimeHandler;
import android.os.Message;

public class Timer implements Runnable {

	private static final String mFormat = "%02d:%02d";

	private AtomicBoolean mIsPaused = new AtomicBoolean(true);
	private Thread mThread = null;
	private AtomicLong mStart = new AtomicLong(0);
	private AtomicLong mPause = new AtomicLong(0);
	private TimeHandler mHandler;

	public Timer(TimeHandler h) {
		mHandler = h;
	}

	public void setHandler(TimeHandler mHandler) {
		this.mHandler = mHandler;
	}

	@Override
	public void run() {
		int time;
		while (!mIsPaused.get()) {
			time = (int) (System.currentTimeMillis() - mStart.get());
			Message msg = mHandler.obtainMessage();
			msg.obj = getFormattedTime(time);
			mHandler.sendMessage(msg);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void pause() {
		if (!mIsPaused.get()) {
			mPause.set(System.currentTimeMillis());
			mIsPaused.set(true);
			while (true) {
				try {
					mThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
			mThread = null;
		}
	}

	public void resume() {	
		if (mIsPaused.get()) {
			mStart.set(mStart.get() + System.currentTimeMillis() - mPause.get());
			mIsPaused.set(false);
			mThread = new Thread(this);
			mThread.start();
		}
	}

	private String getFormattedTime(int time) {
		time = time / 1000;
		int seconds = time % 60;
		int minutes = time / 60;
		return String.format(mFormat, minutes, seconds);
	}

	public String getFormattedTime() {
		return getFormattedTime((int) (System.currentTimeMillis() - mStart
				.get()));
	}
}
