package humaj.michal.gameoffifteen;

import android.provider.BaseColumns;

public final class HighscoreContract {

	public HighscoreContract() {}
		
	public static abstract class HighscoreEntry implements BaseColumns {
        public static final String TABLE_NAME = "highscore";       
        public static final String COLUMN_NAME_IS_GALLERY_PIC = "is_gallery_pic";
        public static final String COLUMN_NAME_PIC_RES_ID = "pic_id";
        public static final String COLUMN_NAME_PIC_FILENAME = "pic_filename"; 
        public static final String COLUMN_NAME_3x3_TIME = "time_3";
        public static final String COLUMN_NAME_4x4_TIME = "time_4";
        public static final String COLUMN_NAME_5x5_TIME = "time_5";
        public static final String COLUMN_NAME_6x6_TIME = "time_6";
        public static final String COLUMN_NAME_3x3_MOVES = "moves_3";
        public static final String COLUMN_NAME_4x4_MOVES = "moves_4";
        public static final String COLUMN_NAME_5x5_MOVES = "moves_5";
        public static final String COLUMN_NAME_6x6_MOVES = "moves_6";
    }
}
