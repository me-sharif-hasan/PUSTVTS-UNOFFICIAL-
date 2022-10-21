package bd.ac.pust.pustvtsunofficial.Helper.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.ac.pust.pustvtsunofficial.Helper.Models.VehicleModels.VehicleModel;
import bd.ac.pust.pustvtsunofficial.R;

public class VehiclesAdapter extends RecyclerView.Adapter<VehiclesAdapter.viewHolder>{
    Context context;
    ArrayList<VehicleModel> arrayList;

    public VehiclesAdapter(Context context, ArrayList<VehicleModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vehicles_sample_design,
                parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        VehicleModel vehicleModel = arrayList.get(position);
        holder.vehicleName.setText(vehicleModel.getVehicleName());
        holder.vehicleType.setText(vehicleModel.getVehicleType());
        holder.lastUpdate.setText(vehicleModel.getLastUpdate());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView vehicleName,vehicleType,lastUpdate;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            vehicleName = itemView.findViewById(R.id.tv_sample_vehicle_name);
            vehicleType = itemView.findViewById(R.id.tv_sample_vehicle_type);
            lastUpdate = itemView.findViewById(R.id.tv_sample_last_update);
        }
    }
}
