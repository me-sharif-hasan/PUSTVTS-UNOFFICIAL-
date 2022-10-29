package bd.ac.pust.pustvtsunofficial.Helper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import bd.ac.pust.pustvtsunofficial.R;

public class VehiclesInfoBottomSheet extends BottomSheetDialogFragment {

    private TextView vName,vType,road,stime,lastupdate,prevStopage,prevStopagePassTime;
    private ConstraintLayout enggOn,enggOff;
    private final String vName_value;
    private final String vType_value;
    private final String road_value;
    private String sTime_value;
    private String lastUpdate_value;
    private String prevStopage_value;
    private String prevStopagePassTime_value;
    boolean isRunning;

    public VehiclesInfoBottomSheet(String vName_value, String vType_value, String road_value,
                                   String sTime_value, boolean engine) {
        this.vName_value = vName_value;
        this.vType_value = vType_value;
        this.road_value = road_value;
        this.sTime_value = sTime_value;
        isRunning = engine;
    }

    public VehiclesInfoBottomSheet(String vName_value, String vType_value, String road_value,
                                   String lastUpdate_value, String prevStopage_value,
                                   String prevStopagePassTime_value) {
        this.vName_value = vName_value;
        this.vType_value = vType_value;
        this.road_value = road_value;
        this.lastUpdate_value = lastUpdate_value;
        this.prevStopage_value = prevStopage_value;
        this.prevStopagePassTime_value = prevStopagePassTime_value;
        isRunning = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            return inflater.inflate(R.layout.fragment_vehicles_info_bottom_sheet, container, false);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vName = view.findViewById(R.id.tv_bottom_sheet_vehicle_name);
        vType = view.findViewById(R.id.tv_bottom_sheet_vehicle_type);
        road = view.findViewById(R.id.tv_bottom_sheet_road);
        stime = view.findViewById(R.id.tv_bottom_sheet_next_start_time);
        enggOff = view.findViewById(R.id.cl_while_engg_off);
        ImageView imgv3=view.findViewById(R.id.imageView3);

        TextView trip=view.findViewById(R.id.trip);

        if(isRunning) trip.setText("CURRENT TRIP");
        else trip.setText("NEXT TRIP");

        vName.setText(vName_value);
        vType.setText("("+vType_value+")");
        road.setText(road_value);
        if(isRunning) imgv3.setImageResource(R.mipmap.bus_marker_start); else imgv3.setImageResource(R.mipmap.bus_marker);
        //enggOff.setVisibility(View.VISIBLE);
        //enggOn.setVisibility(View.INVISIBLE);
        stime.setText(sTime_value);
    }
}