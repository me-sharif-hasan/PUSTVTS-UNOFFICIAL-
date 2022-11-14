package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.NavDrawer;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.Bus;
import bd.ac.pust.pustvtsunofficial.BusLocatorActivity;
import bd.ac.pust.pustvtsunofficial.R;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.viewHolder>{
    Context context;
    ArrayList<Bus> list=new ArrayList<>();
    LinearLayout l;

    public VehicleAdapter(final Context context, LinearLayout vehicles) {
        l=vehicles;
        this.context = context;
    }

    public ArrayList<Bus> getBusList(){
        return list;
    }
    public synchronized void addBus(Bus b){
        list.add(b);
        Collections.sort(list);
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_sample_desing,
                parent,false);
        return new viewHolder(view);
    }
    Map<String,Bus.UpdateActionListener> updateIntervalAdded=new HashMap<>();
    @Override
    public synchronized void onBindViewHolder(@NonNull viewHolder holder, int position) {
        final Bus bus = list.get(position);
        int i = position;
        holder.vehicleName.setText(bus.getBusName());
        try {
            holder.vehicleType.setText("[ "+bus.getBusType()+" ]");
        } catch (Exception e) {
            holder.vehicleType.setText("প্রযোজ্য নয়");
        }
        try {
            holder.vehicleRoad.setText(bus.getBusRoute());
        } catch (Exception e) {
            holder.vehicleRoad.setText("প্রযোজ্য নয়");
        }

        Bus.UpdateActionListener bua=new Bus.UpdateActionListener() {
            boolean intr=false;
            @Override
            public void setLocationUpdateInterval(Bus context) {
                if (context.getEngineStatus()) {
                    holder.statusColor.setBackgroundColor(Color.GREEN);
                } else {
                    holder.statusColor.setBackgroundColor(Color.RED);
                }
            }

            @Override
            public void interrupt() {
                intr=true;
            }

            @Override
            public boolean getInturrpt() {
                return intr;
            }
        };

        if(updateIntervalAdded.containsKey(bus.getBusId())){
            updateIntervalAdded.get(bus.getBusId()).interrupt();
        }
        updateIntervalAdded.put(bus.getBusId(),bua);
        bus.setUpdateInterval(bua,1000);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView t=l.findViewById(R.id.bus_picker);
                t.setText(bus.getBusName());
                BusLocatorActivity.mpc.selectBusId(bus.getBusId());
                BusLocatorActivity.closeDrawer();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView vehicleName,vehicleType,vehicleRoad;
        View statusColor;
        ConstraintLayout layout;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.cl_vehicle_sample_design);
            vehicleName = itemView.findViewById(R.id.tv_vehicle_vehicle_name);
            vehicleType = itemView.findViewById(R.id.tv_vehicle_vehicle_type);
            vehicleRoad = itemView.findViewById(R.id.tv_vehicle_road);
            statusColor = itemView.findViewById(R.id.v_vehicle_status_color);
            statusColor.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
