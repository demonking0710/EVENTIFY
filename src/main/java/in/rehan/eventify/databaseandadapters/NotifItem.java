package in.rehan.eventify.databaseandadapters;

import java.util.Calendar;

public class NotifItem {

    private String title;
    private Calendar reminderTime;
    private String setOrNot;
    private boolean showOrNot = true;

    public NotifItem(){}

    public NotifItem(String title, Calendar reminderTime, String setOrNot) {
        this.title = title;
        this.reminderTime = reminderTime;
        this.setOrNot = setOrNot;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(Calendar reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getSetOrNot() {
        return setOrNot;
    }

    public void setSetOrNot(String setOrNot) {
        this.setOrNot = setOrNot;
    }

    public boolean getShowOrNot() {
        return showOrNot;
    }

    public void setShowOrNot(boolean showOrNot) {
        this.showOrNot = showOrNot;
    }
}
