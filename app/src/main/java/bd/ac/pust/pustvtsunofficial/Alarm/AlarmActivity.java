package bd.ac.pust.pustvtsunofficial.Alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import bd.ac.pust.pustvtsunofficial.Alarm.Adapter.AlarmAdapter;
import bd.ac.pust.pustvtsunofficial.Alarm.Model.AlarmModel;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.Bus;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusFactory;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusInformationFactory;
import bd.ac.pust.pustvtsunofficial.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AlarmActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AlarmAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Spinner bus_selector = findViewById(R.id.bus_list_spinnner);
        recyclerView = findViewById(R.id.rv_alarm_show);

        ArrayList<AlarmModel> arrayList = new ArrayList<>();
        //arrayList.add(new AlarmModel("Bus 3","02:25"));
        //arrayList.add(new AlarmModel("Bus 9","01:25"));
        //arrayList.add(new AlarmModel("Bus 3","05:10"));
        try {
            adapter = new AlarmAdapter(AlarmActivity.this);
        } catch (Exception e) {
            Log.d("II_ALARM_ADAPTER_ERR",e.getLocalizedMessage());
            e.printStackTrace();
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(AlarmActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        ArrayAdapter<String> busSelectorAdapter = new ArrayAdapter<String>(AlarmActivity.this,
                android.R.layout.simple_spinner_item, android.R.id.text1);
        busSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bus_selector.setAdapter(busSelectorAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = this.getPackageName();
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + packageName));
                this.startActivity(intent);
            }
        }

        TimePicker timePicker = findViewById(R.id.timePicker);

        Button alrm = findViewById(R.id.set_arlarm);
        alrm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer h = timePicker.getCurrentHour();
                Integer m = timePicker.getCurrentMinute();
                Log.d("II_TIME_4", h + " " + m);
                start(h, m);
            }
        });

        Map<String, Integer> bbus = new HashMap<>();
        bus_selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView t = (TextView) view;
                String busName = (String) t.getText();
                try {
                    Log.d("II_TIME_2", busName + " " + bbus.get(busName));
                    Bus b = BusFactory.getBuses().get(bbus.get(busName));
                    String[] time = b.getStartTime(false).split(" ");
                    String[] tm = time[0].split(":");

                    // Log.d("II_BUS_3",time);
                    Integer hour = Integer.valueOf(tm[0]);
                    if (time[1].toUpperCase(Locale.US).equals("PM")) hour += 12;
                    timePicker.setCurrentHour(Integer.valueOf(hour));
                    timePicker.setCurrentMinute(Integer.valueOf(tm[1]));
                } catch (Exception e) {
                    Log.d("II_TIME_2", e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<Integer, Bus> buses = BusFactory.getBuses();
                for (Integer key : buses.keySet()) {
                    Bus bus = buses.get(key);
                    bbus.put(bus.getBusName(), key);
                    busSelectorAdapter.add(bus.getBusName());
                    busSelectorAdapter.notifyDataSetChanged();
                }
            }
        }).start();
    }


    public void start(Integer h, Integer m) {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(this, AlarmReceiver.class);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, h);
        c.set(Calendar.MINUTE, m);
        long time = c.getTimeInMillis();
        if (System.currentTimeMillis() > time) {
            time = time + 24 * 60 * 60 * 1000;
        }
        Log.d("II_GET_CRT",c.getTime().toString());
        myIntent.putExtra("time",c.getTime().toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, h*100+m, myIntent, PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time, pendingIntent);
        } else {
            manager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
        try {
            adapter.add(new SimpleDateFormat("hh:mm a").format(c.getTime()),h*100+m);
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(this, "Alarm at: " + h + ":" + m, Toast.LENGTH_LONG).show();
    }

}