package bd.ac.pust.pustvtsunofficial.Alarm.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.ac.pust.pustvtsunofficial.Alarm.Model.AlarmModel;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Config;
import bd.ac.pust.pustvtsunofficial.R;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.viewHolder>{
    Context context;
    ArrayList<AlarmModel> list;

    private final String ALARM_FILE = Config.getInstance().getMainContext().getFilesDir()+"alarm.ck";

    Map<String,String> alarms=new HashMap<>();
    public AlarmAdapter(final Context context, final ArrayList<AlarmModel> list) throws Exception {
        Log.d("II_ALARM","ALARM ADAPTER RELOADED");
        this.context = context;
        this.list = list;
        File alarmFile=new File(ALARM_FILE);
        if(!alarmFile.exists()) alarmFile.createNewFile();
        InputStream is=new FileInputStream(alarmFile);
        byte []buff=new byte[1024];
        int l=is.read(buff);
        try {
            String data = new String(buff, 0, l);
            String[] alm = data.split("\n");
            for (String salm : alm) {
                String nm = salm.split("!")[0];
                String tt = salm.split("!")[1];
                alarms.put(tt, nm);
                list.add(new AlarmModel(nm,tt));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveAlarm(String tt,String nm) throws Exception {
        alarms.put(tt,nm);
        File alarmFile=new File(ALARM_FILE);
        if(!alarmFile.exists()) alarmFile.createNewFile();
        FileOutputStream fileOutputStream=new FileOutputStream(ALARM_FILE,false);
        for(String t:alarms.keySet()){
            String n=alarms.get(t);
            fileOutputStream.write((n+"!"+t+"\n").getBytes(StandardCharsets.UTF_8));
        }
        fileOutputStream.close();
    }
    public void add(AlarmModel alarmModel) throws Exception{
        for(AlarmModel a:list){
            if(a.getAlarmTime().equals(alarmModel.getAlarmTime())) throw new Exception("Alarm already exists!");
        }
        if(!alarms.containsKey(alarmModel.getAlarmTime())){
            saveAlarm(alarmModel.getAlarmTime(),alarmModel.getVechileName());
        }
        list.add(alarmModel);
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
