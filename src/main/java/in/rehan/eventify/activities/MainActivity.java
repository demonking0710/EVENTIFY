package in.rehan.eventify.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import in.rehan.eventify.R;
import in.rehan.eventify.constants.Constants;
import in.rehan.eventify.helpers.DbHelper;
import in.rehan.eventify.databaseandadapters.NotifAdapter;
import in.rehan.eventify.databaseandadapters.NotifItem;
import in.rehan.eventify.helpers.RecyclerItemTouchHelper;
import in.rehan.eventify.helpers.RecyclerItemTouchHelperListener;
import in.rehan.eventify.services.FnNotificationListenerService;


public class MainActivity extends AppCompatActivity implements NotifAdapter.ItemClickListener, RecyclerItemTouchHelperListener {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    private SQLiteDatabase db;
    private ArrayList<NotifItem> notifList;

    private NotifAdapter notifAdapter;

    private LinearLayout rootLayout;

    String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface kaushan = Typeface.createFromAsset(getApplication().getAssets(), "fonts/kaushan.otf");

        TextView toolbarText = (TextView) findViewById(R.id.toolbar);
        toolbarText.setTypeface(kaushan);

        rootLayout = (LinearLayout) findViewById(R.id.root_layout);

        if (!isNotificationServiceEnabled()) {
            AlertDialog enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        } else {
            startService(new Intent(MainActivity.this, FnNotificationListenerService.class));
        }

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.notif_recycler_view);

        DbHelper notifDbHelper = new DbHelper(this);
        db = notifDbHelper.getWritableDatabase();

        fetchData();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        notifAdapter = new NotifAdapter(this, notifList);

        notifAdapter.setClickListener(this);
        recyclerView.setAdapter(notifAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        findViewById(R.id.refresh_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchData();

                recyclerView.setAdapter(null);
                notifAdapter = new NotifAdapter(MainActivity.this, notifList);
                notifAdapter.setClickListener(MainActivity.this);
                recyclerView.setAdapter(notifAdapter);
                notifAdapter.notifyDataSetChanged();
            }
        });

    }


    private boolean isNotificationServiceEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private AlertDialog buildNotificationServiceAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Notification Listener Service");
        alertDialogBuilder.setMessage("For the the app. to work you need to enable the Notification Listener Service. Enable it now?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return (alertDialogBuilder.create());
    }


    private void fetchData() {

        notifList = new ArrayList<>();


        Cursor cursor = db.query(Constants.NotifEntry.TABLE_NAME, null, null,
                null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {

                    if (cursor.getInt(8) == Constants.NOTIF_SHOW) {
                        NotifItem notifItem = new NotifItem();
                        Log.e("Row ID", cursor.getInt(0) + "");
                        notifItem.setTitle(cursor.getString(1));
                        Calendar time = Calendar.getInstance();
                        time.set(Calendar.DATE, cursor.getInt(2)-1);
                        time.set(Calendar.MONTH, cursor.getInt(3));
                        time.set(Calendar.YEAR, cursor.getInt(4));
                        time.set(Calendar.HOUR, cursor.getInt(5));
                        time.set(Calendar.MINUTE, cursor.getInt(6));
                        time.set(Calendar.AM_PM, cursor.getInt(7));
                        notifItem.setReminderTime(time);
                        notifItem.setSetOrNot(cursor.getString(9));
                        notifList.add(notifItem);
                    }

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        Collections.reverse(notifList);

    }

    @Override
    public void onItemClick(View view, final int position) {

        final Calendar tempCal = notifList.get(position).getReminderTime();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Set Reminder?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        NotifItem item = notifList.get(position);

                        ContentValues cv = new ContentValues();

                        cv.put(Constants.NotifEntry.COLUMN_SET_UNSET, Constants.REMINDER_SET);
                        db.update(Constants.NotifEntry.TABLE_NAME, cv, Constants.NotifEntry.COLUMN_EVENT_NAME + " = \"" + item.getTitle() + "\"", null);

                        cv.clear();

                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra("beginTime", tempCal.getTimeInMillis());
                        intent.putExtra("allDay", false);
                        intent.putExtra("rrule", "FREQ=DAILY");
                        intent.putExtra("endTime", tempCal.getTimeInMillis() + 60 * 60 * 1000);
                        intent.putExtra("title", notifList.get(position).getTitle());
                        startActivity(intent);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        NotifItem item = notifList.get(position);

                        ContentValues cv = new ContentValues();

                        cv.put(Constants.NotifEntry.COLUMN_SET_UNSET, Constants.REMINDER_UNSET);
                        db.update(Constants.NotifEntry.TABLE_NAME, cv, Constants.NotifEntry.COLUMN_EVENT_NAME + " = \"" + item.getTitle() + "\"", null);

                        cv.clear();


                        recreate();

                    }
                }).create();
        dialog.show();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder holder, int direction, int position) {

        if (holder instanceof NotifAdapter.NotifViewHolder) {

            final int deleteIndex = holder.getAdapterPosition();

            final NotifItem deletedItem = notifList.get(deleteIndex);

            final String name = deletedItem.getTitle();


            notifAdapter.removeItem(deleteIndex);

            final ContentValues cv = new ContentValues();

            cv.put(Constants.NotifEntry.COLUMN_SHOW, Constants.NOTIF_HIDE);


            db.update(Constants.NotifEntry.TABLE_NAME, cv, Constants.NotifEntry.COLUMN_EVENT_NAME + " = \"" + name + "\"", null);

            cv.clear();


            Snackbar snackbar = Snackbar.make(rootLayout, "Item removed!!", Snackbar.LENGTH_SHORT);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    notifAdapter.restoreItem(deleteIndex, deletedItem);
                    cv.put(Constants.NotifEntry.COLUMN_SHOW, Constants.NOTIF_SHOW);
                    db.update(Constants.NotifEntry.TABLE_NAME, cv, Constants.NotifEntry.COLUMN_EVENT_NAME + " = \"" + name + "\"", null);
                    cv.clear();
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }

    }

    @Override
    public void onBackPressed() {

        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

    }
}
