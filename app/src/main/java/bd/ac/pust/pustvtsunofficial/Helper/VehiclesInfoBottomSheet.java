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
    private String vName_value,vType_value,road_value,sTime_value,lastUpdate_value,prevStopage_value,
            prevStopagePassTime_value;
    boolean isRunning;

    public VehiclesInfoBottomSheet(String vName_value, String vType_value, String road_value,
                                   String sTime_value) {
        this.vName_value = vName_value;
        this.vType_value = vType_value;
        this.road_value = road_value;
        this.sTime_value = sTime_value;
        isRunning = false;
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

        return inflater.inflate(R.layout.fragment_vehicles_info_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vName = view.findViewById(R.id.tv_bottom_sheet_vehicle_name);
        vType = view.findViewById(R.id.tv_bottom_sheet_vehicle_type);
        road = view.findViewById(R.id.tv_bottom_sheet_road);
        stime = view.findViewById(R.id.tv_bottom_sheet_next_start_time);
        lastupdate = view.findViewById(R.id.tv_bottom_sheet_last_update);
        prevStopage = view.findViewById(R.id.tv_bottom_sheet_last_pass_location);
        prevStopagePassTime = view.findViewById(R.id.tv_bottom_sheet_last_pass_time);
        enggOn = view.findViewById(R.id.cl_while_engg_on);
        enggOff = view.findViewById(R.id.cl_while_engg_off);
        ImageView imgv3=view.findViewById(R.id.imageView3);

        vName.setText(vName_value);
        vType.setText("("+vType_value+")");
        road.setText(road_value);
        if(isRunning){
            imgv3.setImageResource(R.mipmap.bus_marker_start);
            enggOff.setVisibility(View.INVISIBLE);
            enggOn.setVisibility(View.VISIBLE);
            lastupdate.setText(lastUpdate_value);
            prevStopage.setText("Last reported: "+prevStopage_value);
            prevStopagePassTime.setText(prevStopagePassTime_value);
        }else{
            imgv3.setImageResource(R.mipmap.bus_marker);
            enggOff.setVisibility(View.VISIBLE);
            enggOn.setVisibility(View.INVISIBLE);
            stime.setText(sTime_value);        }
    }
}