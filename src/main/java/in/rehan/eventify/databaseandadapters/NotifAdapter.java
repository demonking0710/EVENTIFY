package in.rehan.eventify.databaseandadapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.rehan.eventify.R;
import in.rehan.eventify.constants.Constants;



public class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.NotifViewHolder> {

    private ItemClickListener mClickListener;
    private LayoutInflater mInflater;

    private ArrayList<NotifItem> mData;

    public NotifAdapter(Context context, ArrayList<NotifItem> data) {

        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public NotifAdapter.NotifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = mInflater.inflate(R.layout.notif_item, parent, false);

        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotifAdapter.NotifViewHolder holder, int position) {


        holder.notifTitle.setText(mData.get(position).getTitle());


        holder.dateTIme.setText(String.format("%1$tA %1$tb %1$td %1$tY at %1$tI:%1$tM %1$Tp", mData.get(position).getReminderTime()));


        if (mData.get(position).getSetOrNot().equals(Constants.REMINDER_SET)) {
            holder.setOrNot.setBackgroundResource(R.drawable.green_button_bg);
            holder.setOrNot.setText(Constants.REMINDER_SET);
        } else if (mData.get(position).getSetOrNot().equals(Constants.REMINDER_UNSET)) {
            holder.setOrNot.setBackgroundResource(R.drawable.yellow_button_bg);
            holder.setOrNot.setText(Constants.REMINDER_UNSET);
        } else {
            holder.setOrNot.setBackgroundResource(R.drawable.red_button_bg);
            holder.setOrNot.setText(Constants.REMINDER_UNNOTICED);
        }


        if (mData.get(position).getSetOrNot().equals(Constants.REMINDER_SET)) {
            holder.setReminder.setVisibility(View.INVISIBLE);
        } else {
            holder.setReminder.setVisibility(View.VISIBLE);
        }
        holder.setOrNot.setElevation(12.0f);
        holder.setReminder.setElevation(12.0f);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class NotifViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView notifTitle;
        TextView dateTIme;
        TextView setReminder;
        TextView setOrNot;
        RelativeLayout viewBackground;
        public LinearLayout viewForeground;

        NotifViewHolder(View itemView) {
            super(itemView);


            notifTitle = (TextView) itemView.findViewById(R.id.notif_title);
            dateTIme = (TextView) itemView.findViewById(R.id.date_time);
            setReminder = (TextView) itemView.findViewById(R.id.set_reminder);
            setOrNot = (TextView) itemView.findViewById(R.id.set_or_not);
            viewBackground = (RelativeLayout)itemView.findViewById(R.id.background_view);
            viewForeground = (LinearLayout) itemView.findViewById(R.id.foreground_view);
            setReminder.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public ArrayList<NotifItem> getmData() {
        return mData;
    }

    public void setmData(ArrayList<NotifItem> mData) {
        this.mData = mData;
    }


    public void removeItem(int pos){
        mData.remove(pos);
        notifyItemRemoved(pos);
    }


    public void restoreItem(int pos, NotifItem item){
        mData.add(pos, item);
        notifyItemInserted(pos);
    }

}
