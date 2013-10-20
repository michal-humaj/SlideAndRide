package humaj.michal.uilogic;

import humaj.michal.activity.ChoosePictureActivity;
import humaj.michal.util.ImageUtils;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class WinDialog extends DialogFragment {

	public static final String MOVES_COUNT = "MOVES_COUNT";
	public static final String SOLVE_TIME = "SOLVE_TIME";
	public static final String IS_NEW_HIGHSCORE = "IS_NEW_HIGHSCORE";
	private static final int NUM_PICTURES = ImageUtils.mPictureIDs.length;

	int mMovesCount;
	String mSolveTime;
	boolean mIsNewHighscore;

	public static WinDialog newInstance(String solveTime, int movesCount,
			boolean isNewHighscore) {
		WinDialog dialog = new WinDialog();
		Bundle bundle = new Bundle();
		bundle.putInt(MOVES_COUNT, movesCount);
		bundle.putString(SOLVE_TIME, solveTime);
		if (isNewHighscore) {
			bundle.putBoolean(IS_NEW_HIGHSCORE, true);
		} else {
			bundle.putBoolean(IS_NEW_HIGHSCORE, false);
		}
		dialog.setArguments(bundle);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		String solveTime = bundle.getString(SOLVE_TIME);
		int movesCount = bundle.getInt(MOVES_COUNT);
		boolean isNewHighscore = bundle.getBoolean(IS_NEW_HIGHSCORE);
		String imageUnlocked = isNewHighscore && ChoosePictureActivity.mPicsUnlocked < NUM_PICTURES ? "You have unlocked new picture!" : "";
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Time:     " + solveTime + "\nMoves:  "
				+ movesCount + "\n\n" + imageUnlocked);
		if (isNewHighscore) {
			builder.setTitle("New Highscore reached!");
		} else {
			builder.setTitle("Good job, puzzle solved!");
		}
		builder.setNeutralButton("OK", null);
		return builder.create();
	}

}
