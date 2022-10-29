package bd.ac.pust.pustvtsunofficial.Alarm.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.ac.pust.pustvtsunofficial.Alarm.Model.AlarmModel;
import bd.ac.pust.pustvtsunofficial.R;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.viewHolder>{
    Context context;
    ArrayList<AlarmModel> list;

    public AlarmAdapter(final Context context, final ArrayList<AlarmModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.alarm_sample_design,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        AlarmModel model = list.get(position);
        holder.vechileName.setText(model.getVechileName());
        holder.alarmTime.setText(model.getAlarmTime());

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Alarm delete clicked.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView vechileName,alarmTime;
        ImageView btn_delete;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            vechileName = itemView.findViewById(R.id.tv_alarm_bus_name);
            alarmTime = itemView.findViewById(R.id.tv_alarm_time);
            btn_delete = itemView.findViewById(R.id.iv_alarm_delete);
        }
    }
}
