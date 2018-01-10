package app.num.barcodescannerproject;

import android.annotation.SuppressLint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<String> result,type,timeDate;

    public DataAdapter(ArrayList<String> result, ArrayList<String> type, ArrayList<String> timeDate) {
        this.result = result;
        this.type = type;
        this.timeDate = timeDate;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {
        viewHolder.resultTV.setText(result.get(i));
        viewHolder.typeTV.setText(type.get(i));

        long geTime= Long.parseLong(timeDate.get(i));
        TimeZone tz = TimeZone.getTimeZone("Asia/Kuala_Lumpur");
        GregorianCalendar calendar = new GregorianCalendar(tz);
        calendar.setTimeInMillis(geTime);
        DateTime jodaTime = new DateTime(geTime,
                DateTimeZone.forTimeZone(tz));
        DateTimeFormatter date = DateTimeFormat.forPattern("dd MMM yy");
        DateTimeFormatter time = DateTimeFormat.forPattern("hh:mm a");
        String dateStr = date.print(jodaTime);
        String timeStr = time.print(jodaTime);
        viewHolder.timeTV.setText(dateStr+"\n"+timeStr);
    }

    @Override
    public int getItemCount() {
        return result.size();
    }

    public void removeItem(int position) {
        result.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView resultTV,typeTV,timeTV;
        public RelativeLayout viewBackground;
        public CardView viewForeground;
        private ViewHolder(View view) {
            super(view);
            resultTV = view.findViewById(R.id.result);
            typeTV = view.findViewById(R.id.type);
            timeTV = view.findViewById(R.id.timeDate);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }

}

