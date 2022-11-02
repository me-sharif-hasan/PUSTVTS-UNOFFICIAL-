package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.NavDrawer;

import static bd.ac.pust.pustvtsunofficial.R.layout.recycler_view_sample_desing;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.Bus;
import bd.ac.pust.pustvtsunofficial.BusLocatorActivity;
import bd.ac.pust.pustvtsunofficial.R;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.viewHolder>{
    Context context;
    ArrayList<Bus> list;

    public VehicleAdapter(final Context context, final ArrayList<Bus> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_sample_desing,
                parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Bus bus = list.get(position);
        int i = position;
        holder.vehicleName.setText(bus.getBusName());
        try {
            holder.vehicleType.setText("[ "+bus.getBusType()+" ]");
        } catch (Exception e) {
            holder.vehicleType.setText("[ N/A ]");
        }
        try {
            holder.vehicleRoad.setText(bus.getBusRoute());
        } catch (Exception e) {
            holder.vehicleRoad.setText("Not find.");
        }
        if(bus.getEngineStatus()) {
            holder.statusColor.setBackgroundColor(Color.parseColor("#4CAF50"));
        }else{
            holder.statusColor.setBackgroundColor(Color.parseColor("#FF5722"));
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusLocatorActivity.mpc.selectBusId(i);
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
        }
    }
}
