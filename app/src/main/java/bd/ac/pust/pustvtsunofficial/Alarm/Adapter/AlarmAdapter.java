package bd.ac.pust.pustvtsunofficial.Alarm.Adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.CollationElementIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import bd.ac.pust.pustvtsunofficial.Alarm.AlarmReceiver;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Config;
import bd.ac.pust.pustvtsunofficial.R;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.viewHolder>{
    Context context;
    ArrayList <Pair<String,Integer>> list;
    private final String ALARM_FILE = Config.getInstance().getMainContext().getFilesDir()+"alarm.ck";
    public AlarmAdapter(Context context){
        this.context=context;
        try {
            loadAlarms();
        }catch (Exception e){};
    }
    private void loadAlarms() throws Exception {
        Date d=new Date();
        int hour=d.getHours();
        int getMin=d.getMinutes();
        String now=hour+":"+getMin;
        list=new ArrayList<>();
        File file=new File(ALARM_FILE);
        FileInputStream fileInputStream=new FileInputStream(file);
        byte []buff=new byte[1024];
        int l=fileInputStream.read(buff);
        try {
            String[] s = new String(buff, 0, l).split("\n");
            for (String t : s) {
                if (!t.equals("")) {
                    String []tag=t.split("=");
                    int m=Integer.parseInt(tag[1])%100;
                    int h=Integer.parseInt(tag[1])/100;
                    String th=h+":"+m;
                    if(now.compareTo(th)<=0) {
                        list.add(new Pair<String, Integer>(tag[0], Integer.parseInt(tag[1])));
                    }
                }
            }
        }catch (Exception e){}
    }
    synchronized private void deleteAlarms(String time,int tag) throws Exception {
        Log.d("II_ALM",time+" "+tag);
        //for(Pair<String,Integer> t:list) Log.d("II_ALM",t+" "+list.contains(time));
        if(list.contains(new Pair<String,Integer>(time,tag))){
            list.remove(new Pair<String,Integer>(time,tag));
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, tag, myIntent, PendingIntent.FLAG_IMMUTABLE);
            manager.cancel(pendingIntent);
        }
       // for(Pair<String,Integer> t:list) Log.d("II_ALM",t+" "+list.contains(time));
        saveAlarmList();
        loadAlarms();
    }
    synchronized private void saveAlarmList() throws Exception {
        File file=new File(ALARM_FILE);
        if(!file.exists()) file.createNewFile();
        FileOutputStream fileOutputStream=new FileOutputStream(file,false);
        fileOutputStream.write("".getBytes(StandardCharsets.UTF_8));
        for(Pair<String,Integer> time:list) fileOutputStream.write((time.first+"="+time.second+"\n").getBytes(StandardCharsets.UTF_8));
        fileOutputStream.flush();
        fileOutputStream.close();
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.alarm_sample_design,parent,false);
       return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.alarmTime.setText(list.get(position).first);
        holder.alarmTag=list.get(position).second;
        if(!holder.btn_delete.hasOnClickListeners()){
            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String t=holder.alarmTime.getText().toString();
                    int tag=holder.alarmTag;
                    try {
                        deleteAlarms(t,tag);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public synchronized void add(String time, int id){
        if(list.contains(time)) return;
        list.add(new Pair<String,Integer>(time,id));
        try {
            saveAlarmList();
        }catch (Exception e){}
        Collections.sort(list, new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> stringIntegerPair, Pair<String, Integer> t1) {
                return stringIntegerPair.first.compareTo(t1.first);
            }
        });
    }

//    Context context;
//    ArrayList<AlarmModel> list;
//
//    private final String ALARM_FILE = Config.getInstance().getMainContext().getFilesDir()+"alarm.ck";
//
//    Map<String,String> alarms=new HashMap<>();
//    public AlarmAdapter(final Context context, final ArrayList<AlarmModel> list) throws Exception {
//        Log.d("II_ALARM","ALARM ADAPTER RELOADED");
//        this.context = context;
//        this.list = list;
//        loadAlarm();
//    }
//
//    private void loadAlarm() throws Exception {
//        list=new ArrayList<>();
//        File alarmFile=new File(ALARM_FILE);
//        if(!alarmFile.exists()) alarmFile.createNewFile();
//        InputStream is=new FileInputStream(alarmFile);
//        byte []buff=new byte[1024];
//        int l=is.read(buff);
//        try {
//            String data = new String(buff, 0, l);
//            String[] alm = data.split("\n");
//            for (String salm : alm) {
//                String nm = salm.split("!")[0];
//                String tt = salm.split("!")[1];
//                alarms.put(tt, nm);
//                list.add(new AlarmModel(nm,tt));
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    private void saveAlarm(String tt,String nm,boolean delete) throws Exception {
//        if(!delete) alarms.put(tt,nm);
//        File alarmFile=new File(ALARM_FILE);
//        if(!alarmFile.exists()) alarmFile.createNewFile();
//        FileOutputStream fileOutputStream=new FileOutputStream(ALARM_FILE,false);
//        for(String t:alarms.keySet()){
//            String n=alarms.get(t);
//            if(t.equals(tt)&&delete){
//                alarms.remove(tt);
//                continue;
//            }
//            fileOutputStream.write((n+"!"+t+"\n").getBytes(StandardCharsets.UTF_8));
//        }
//        fileOutputStream.close();
//    }
//    public void add(AlarmModel alarmModel) throws Exception{
//        for(AlarmModel a:list){
//            if(a.getAlarmTime().equals(alarmModel.getAlarmTime())) throw new Exception("Alarm already exists!");
//        }
//        if(!alarms.containsKey(alarmModel.getAlarmTime())){
//            saveAlarm(alarmModel.getAlarmTime(),alarmModel.getVechileName(),false);
//        }
//        list.add(alarmModel);
//    }
//
//    @NonNull
//    @Override
//    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.alarm_sample_design,parent,false);
//        return new viewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
//        AlarmModel model = list.get(position);
//        //holder.vechileName.setText(model.getVechileName());
//        holder.alarmTime.setText(model.getAlarmTime());
//
//        if(!holder.btn_delete.hasOnClickListeners()) {
//            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    try {
//                        final AlarmModel alarmModel = model;
//                        //Log.d("II_ALM_DLT",holder.vechileName.getText().toString()+" "+holder.alarmTime.getText().toString());
//                        saveAlarm(holder.alarmTime.getText().toString(), holder.vechileName.getText().toString(), true);
//                        loadAlarm();
//                        notifyDataSetChanged();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    Toast.makeText(context, "Alarm delete clicked.", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }

    public class viewHolder extends RecyclerView.ViewHolder{
        Integer alarmTag;
        TextView alarmTime;
        ImageView btn_delete;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            //vechileName = itemView.findViewById(R.id.tv_alarm_bus_name);
            alarmTime = itemView.findViewById(R.id.tv_alarm_time);
            btn_delete = itemView.findViewById(R.id.iv_alarm_delete);
        }
    }
}
