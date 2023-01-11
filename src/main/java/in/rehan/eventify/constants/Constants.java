package in.rehan.eventify.constants;
import android.provider.BaseColumns;

public class Constants {


    public static String REMINDER_SET = "Reminder Set";
    public static String REMINDER_UNSET = "Reminder Not Set";
    public static String REMINDER_UNNOTICED = "Unnoticed";
    public static int NOTIF_SHOW = 1;
    public static int NOTIF_HIDE = 0;


    public class NotifEntry implements BaseColumns{

        public static final String TABLE_NAME = "notifs";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_EVENT_NAME = "name";
        public static final String COLUMN_DATE_DD = "date_dd";
        public static final String COLUMN_DATE_MM = "date_mm";
        public static final String COLUMN_DATE_YYYY = "date_yyyy";
        public static final String COLUMN_TIME_HR = "time_hr";
        public static final String COLUMN_TIME_MIN = "time_min";
        public static final String COLUMN_TIME_AM_PM = "time_am_pm";
        public static final String COLUMN_SET_UNSET = "set_unset";
        public static final String COLUMN_SHOW = "show_notif_in_list";

    }

}
