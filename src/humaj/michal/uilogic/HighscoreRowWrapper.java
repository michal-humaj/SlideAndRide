package humaj.michal.uilogic;

import humaj.michal.R;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HighscoreRowWrapper {

	private View mBase;
	private ImageView iv = null;
	private TextView tvTime3x3 = null;
	private TextView tvTime4x4 = null;
	private TextView tvTime5x5 = null;
	private TextView tvTime6x6 = null;
	private TextView tvMoves3x3 = null;
	private TextView tvMoves4x4 = null;
	private TextView tvMoves5x5 = null;
	private TextView tvMoves6x6 = null;

	public HighscoreRowWrapper(View base) {
		mBase = base;

	}

	public ImageView getIv() {
		if (iv == null) {
			iv = (ImageView) mBase.findViewById(R.id.ivHighscore);
		}
		return iv;
	}

	public TextView getTvTime3x3() {
		if (tvTime3x3 == null) {
			tvTime3x3 = (TextView) mBase.findViewById(R.id.tv3x3Time);
		}
		return tvTime3x3;
	}
	
	public TextView getTvTime4x4() {
		if (tvTime4x4 == null) {
			tvTime4x4 = (TextView) mBase.findViewById(R.id.tv4x4Time);
		}
		return tvTime4x4;
	}
	
	public TextView getTvTime5x5() {
		if (tvTime5x5 == null) {
			tvTime5x5 = (TextView) mBase.findViewById(R.id.tv5x5Time);
		}
		return tvTime5x5;
	}
	
	public TextView getTvTime6x6() {
		if (tvTime6x6 == null) {
			tvTime6x6 = (TextView) mBase.findViewById(R.id.tv6x6Time);
		}
		return tvTime6x6;
	}
	
	public TextView getTvMoves3x3() {
		if (tvMoves3x3 == null) {
			tvMoves3x3 = (TextView) mBase.findViewById(R.id.tv3x3Moves);
		}
		return tvMoves3x3;
	}
	
	public TextView getTvMoves4x4() {
		if (tvMoves4x4 == null) {
			tvMoves4x4 = (TextView) mBase.findViewById(R.id.tv4x4Moves);
		}
		return tvMoves4x4;
	}
	
	public TextView getTvMoves5x5() {
		if (tvMoves5x5 == null) {
			tvMoves5x5 = (TextView) mBase.findViewById(R.id.tv5x5Moves);
		}
		return tvMoves5x5;
	}
	
	public TextView getTvMoves6x6() {
		if (tvMoves6x6 == null) {
			tvMoves6x6 = (TextView) mBase.findViewById(R.id.tv6x6Moves);
		}
		return tvMoves6x6;
	}

}
