package Maps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.Bus;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusFactory;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Config;
import bd.ac.pust.pustvtsunofficial.R;

public class MapController implements OnMapReadyCallback {
    Map<String, MarkerOptions> markerOptionsMap =new HashMap<>();
    Map<String, Marker> markerMap=new HashMap<>();
    Map<String,Boolean> updateState=new HashMap<>();
    Map <Integer,Bus> buses=null;
    double ZOOM_LEVEL=19;
    boolean willZoom=true;
    boolean willAutoFocus=false;
    String focusedBus="";
    boolean hide=false;
    AppCompatActivity context;
    public MapController(){

    }

    public void setContext(AppCompatActivity context) {
        this.context = context;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startUpdate(googleMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if(i==REASON_GESTURE){
                    willAutoFocus=false;
                    if(!focusedBus.equals("")) context.findViewById(R.id.recenter).setVisibility(View.VISIBLE);
                }
            }
        });
    }
    GoogleMap gmap;
    private void startUpdate(GoogleMap gmap) throws Exception {
        this.gmap=gmap;
        int nob=0;
        while (true){
            if(BusFactory.getNumberOfBuses()!=nob){
                buses=BusFactory.getBuses();
                nob=BusFactory.getNumberOfBuses();
                Log.i("II_INFO","NUMBER OF BUS IS UPDATED TO: "+nob);
            }
            if(buses!=null) {
                for (Integer i : buses.keySet()) {
                    Bus copyBus=buses.get(i);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String busId=copyBus.getBusId();
                                if(busId.equals("N/A")||updateState.containsKey(busId)&& Boolean.TRUE.equals(updateState.get(busId))) return;
                                copyBus.whereAreYou();
                                double lon=copyBus.getBusLon();
                                double lat=copyBus.getBusLat();
                                if(markerOptionsMap.containsKey(busId)){
                                    markerOptionsMap.get(busId).position(new LatLng(lat,lon));
                                    Log.i("II_MESSAGE","MARKER UPDATED FOR BUS: "+copyBus.getBusName());
                                    Config.getInstance().getMainContext().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(!hide){
                                                markerMap.get(busId).setVisible(true);
                                            }
                                            Log.w("II_FOCUS","FOCUSING ON: "+busId+"; "+willAutoFocus+"; "+focusedBus);
                                            if(willAutoFocus&&busId.equals(focusedBus)){
                                                focus(markerOptionsMap.get(busId));
                                            }
                                        }
                                    });
                                }else{
                                    //create new marker
                                    MarkerOptions markerOptions=createNewMarker(lon,lat);
                                    Bitmap icon;
                                    if( copyBus.getEngineStatus()){
                                        icon=BitmapFactory.decodeResource(Config.getInstance().getMainContext().getResources(),R.mipmap.bus_marker_start);
                                    }else {
                                        icon = BitmapFactory.decodeResource(Config.getInstance().getMainContext().getResources(), R.mipmap.bus_marker);
                                    }
                                    Log.d("SHARIF","HELLO");
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
                                    markerOptions.title(copyBus.getBusName());
                                    Config.getInstance().getMainContext().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            markerMap.put(busId,gmap.addMarker(markerOptions));
                                            if(hide&&!buses.equals(focusedBus)){
                                                markerMap.get(busId).setVisible(false);
                                            }else{
                                                markerMap.get(busId).setVisible(true);
                                            }
                                            Log.i("II_INFO","BUS MARKER ADDED FOR: "+busId);
                                            if(willZoom) {
                                                focus(markerOptions);
                                                willZoom=false;
                                            }
                                        }
                                    });
                                    markerOptionsMap.put(busId,markerOptions);
                                }
                                sleep(1000);
                                updateState.put(busId,false);
                            }catch (Exception e){
                                Log.e("II_ERROR",e.getLocalizedMessage());
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    sleep(1000);
                }
            }
            sleep(500);
        }
    }

    private void focus(MarkerOptions markerOptions){
        LatLng latLng=markerOptions.getPosition();
        Log.i("II_INFO",latLng.latitude+" "+latLng.longitude);
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), (float) (willZoom==true?ZOOM_LEVEL:gmap.getCameraPosition().zoom)));
        context.findViewById(R.id.recenter).setVisibility(View.INVISIBLE);
    }
    private MarkerOptions createNewMarker(double lon,double lat){
        LatLng l=new LatLng(lat,lon);
        MarkerOptions mop=new MarkerOptions().title("BUS").position(l);
        return mop;
    }

    void sleep(int t){
        try {
            TimeUnit.MILLISECONDS.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void selectBusId(int i, AppCompatActivity context) {
        if(buses==null||!buses.containsKey(i)) return;
        TextView tv=context.findViewById(R.id.bus_name_show);
        tv.setText(Objects.requireNonNull(buses.get(i)).getBusName());
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch soloView=context.findViewById(R.id.bus_solo_view);
        String busId=buses.get(i).getBusId();
        if(busId.equals("N/A")){
            willAutoFocus=false;
            focusedBus="";
            tv.setText("");
            soloView.setChecked(false);
            isolateView(soloView);
            soloView.setVisibility(View.INVISIBLE);
            return;
        }
        Log.i("II_CHECK","BUS SELECTED "+i+" "+busId);
        if(!markerOptionsMap.containsKey(busId)){
            Toast.makeText(context, "This bus is not loaded yet!", Toast.LENGTH_SHORT).show();
        }
        //Ignore focus flag
        MarkerOptions markerOptions= markerOptionsMap.get(busId);
        if(markerOptions==null) return;
        markerMap.get(busId).setVisible(true);
        focusedBus=busId;
        willAutoFocus=true;
        soloView.setVisibility(View.VISIBLE);
        soloView.setChecked(true);
        isolateView(soloView);
        focus(markerOptions);
    }
    public void isolateView(Switch sw){
        Log.d("II_SWITCH", String.valueOf(sw.isChecked()));
        if(sw.isChecked()) {
            for (String key : markerMap.keySet()) {
                if (!Objects.equals(key, focusedBus)) {
                    markerMap.get(key).setVisible(false);
                }
            }
        }else {
            for (String key : markerMap.keySet()) {
                if (!Objects.equals(key, focusedBus)) {
                    markerMap.get(key).setVisible(true);
                }
            }
        }
        hide= sw.isChecked();
    }
    public void refocus(AppCompatActivity context){
        willAutoFocus=true;
        if(!focusedBus.equals("")&&markerOptionsMap.containsKey(focusedBus)){
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    focus(markerOptionsMap.get(focusedBus));
                }
            });
        }
    }
}
