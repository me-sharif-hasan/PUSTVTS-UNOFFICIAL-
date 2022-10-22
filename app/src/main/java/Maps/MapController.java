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
                    startUpdate2(googleMap);
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

    Map <Integer,Bus> busIdList=new HashMap<>();

    public void startUpdate2(GoogleMap gmap){
        this.gmap=gmap;
        Bitmap startIcon=BitmapFactory.decodeResource(Config.getInstance().getMainContext().getResources(), R.mipmap.bus_marker_start);
        Bitmap normalIcon=BitmapFactory.decodeResource(Config.getInstance().getMainContext().getResources(), R.mipmap.bus_marker);
        BusFactory.setBusLoadListener(new BusFactory.BusLoadListener() {
            @Override
            public void onBusLoaded(Bus bus,int busKey) {
                busIdList.put(busKey,bus);
                if(bus.getBusId().equals("N/A")) return;
                bus.setUpdateInterval(new Bus.UpdateActionListener(){
                    @Override
                    public void setLocationUpdateInterval(Bus context) {
                        try {
                            if(!focusedBus.equals(context.getBusId())&&hide) return;
                            context.whereAreYou();
                            float lon = (float) context.getBusLon();
                            float lat = (float) context.getBusLat();
                            Log.d("II_ENGINE",context.getEngineStatus()+" for "+context.getBusName());
                            Bitmap markerIcon;
                            if(context.getEngineStatus()){
                                markerIcon =startIcon;
                            }else{
                                markerIcon=normalIcon;
                            }
                            if(markerMap.containsKey(context.getBusId())){
                                Config.getInstance().getMainContext().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Marker marker=markerMap.get(context.getBusId());
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(markerIcon));
                                        marker.setPosition(new LatLng(lat,lon));
                                        if(!hide){
                                            marker.setVisible(true);
                                        }
                                        if(willAutoFocus&&context.getBusId().equals(focusedBus)){
                                            focus(marker.getPosition());
                                        }
                                    }
                                });
                            }else{
                                MarkerOptions markerOptions=createNewMarker(lon,lat);
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(markerIcon));
                                markerOptions.title(context.getBusName());
                                Config.getInstance().getMainContext().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Marker marker=gmap.addMarker(markerOptions);
                                        markerMap.put(context.getBusId(),marker);
                                        markerOptionsMap.put(context.getBusId(),markerOptions);
                                        if(hide&&!focusedBus.equals(context.getBusId())){
                                            marker.setVisible(false);
                                        }else{
                                            marker.setVisible(true);
                                        }
                                        if(willZoom){
                                            focus(marker.getPosition());
                                            willZoom=false;
                                        }
                                    }
                                });
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },1000);
            }
        });
    }

    private void startUpdate(GoogleMap gmap) throws Exception {
        this.gmap=gmap;
        int nob=0;
        while (true){
            if(BusFactory.getNumberOfBuses()!=nob){
                buses=BusFactory.getBuses();
                nob=BusFactory.getNumberOfBuses();
                Log.i("II_INFO","NUMBER OF BUS IS UPDATED TO: "+nob);
            }
            Log.e("II_MAP",String.valueOf(BusFactory.getBuses().size()));

            if(buses!=null) {
                final Map <Integer,Bus> copyBuses=buses;
                try {
                    for (int i : copyBuses.keySet()) {
                        Bus copyBus = copyBuses.get(i);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String busId = copyBus.getBusId();
                                    if (busId.equals("N/A") || updateState.containsKey(busId) && Boolean.TRUE.equals(updateState.get(busId)))
                                        return;
                                    copyBus.whereAreYou();
                                    double lon = copyBus.getBusLon();
                                    double lat = copyBus.getBusLat();
                                    if (markerOptionsMap.containsKey(busId)) {
                                        Config.getInstance().getMainContext().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.i("II_MESSAGE", "MARKER UPDATED FOR BUS: " + copyBus.getBusName());
                                                //markerOptionsMap.get(busId).position(new LatLng(lat, lon));
                                                markerMap.get(busId).setPosition(new LatLng(lat,lon));

                                                Bitmap icon;
                                                if (copyBus.getEngineStatus()) {
                                                    icon = BitmapFactory.decodeResource(Config.getInstance().getMainContext().getResources(), R.mipmap.bus_marker_start);
                                                } else {
                                                    icon = BitmapFactory.decodeResource(Config.getInstance().getMainContext().getResources(), R.mipmap.bus_marker);
                                                }
                                                Log.d("SHARIF", "HELLO "+copyBus.getEngineStatus());
                                                markerOptionsMap.get(busId).icon(BitmapDescriptorFactory.fromBitmap(icon));


                                                if (!hide) {
                                                    markerMap.get(busId).setVisible(true);
                                                }
                                                Log.w("II_FOCUS", "FOCUSING ON: " + busId + "; " + willAutoFocus + "; " + focusedBus);
                                                if (willAutoFocus && busId.equals(focusedBus)) {
                                                    focus(markerMap.get(busId).getPosition());
                                                }

                                            }
                                        });
                                    } else {
                                        //create new marker
                                        MarkerOptions markerOptions = createNewMarker(lon, lat);
                                        Bitmap icon;
                                        if (copyBus.getEngineStatus()) {
                                            icon = BitmapFactory.decodeResource(Config.getInstance().getMainContext().getResources(), R.mipmap.bus_marker_start);
                                        } else {
                                            icon = BitmapFactory.decodeResource(Config.getInstance().getMainContext().getResources(), R.mipmap.bus_marker);
                                        }
                                        Log.d("SHARIF", "HELLO");
                                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
                                        markerOptions.title(copyBus.getBusName());
                                        Config.getInstance().getMainContext().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                markerMap.put(busId, gmap.addMarker(markerOptions));
                                                if (hide && !buses.equals(focusedBus)) {
                                                    markerMap.get(busId).setVisible(false);
                                                } else {
                                                    markerMap.get(busId).setVisible(true);
                                                }
                                                Log.i("II_INFO", "BUS MARKER ADDED FOR: " + busId);
                                                if (willZoom) {
                                                    focus(markerMap.get(busId).getPosition());
                                                    willZoom = false;
                                                }
                                            }
                                        });
                                        markerOptionsMap.put(busId, markerOptions);
                                    }
                                    sleep(1000);
                                    updateState.put(busId, false);
                                } catch (Exception e) {
                                    Log.e("II_ERROR", e.getLocalizedMessage());
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        sleep(1000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            sleep(500);
        }
    }

    private void focus(LatLng latLng){
        if(gmap==null||context==null) return;
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) (willZoom==true?ZOOM_LEVEL:gmap.getCameraPosition().zoom)));
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
        Log.e("II_ERR",String.valueOf(i)+" "+busIdList.size());
        if(busIdList==null||!busIdList.containsKey(i)) return;
        TextView tv=context.findViewById(R.id.bus_name_show);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch soloView=context.findViewById(R.id.bus_solo_view);
        String busId= Objects.requireNonNull(busIdList.get(i)).getBusId();
        if(busId.equals("N/A")){
            willAutoFocus=false;
            focusedBus="";
            tv.setText("Showing All");
            soloView.setChecked(false);
            isolateView(soloView);
            soloView.setVisibility(View.INVISIBLE);
            return;
        }
        Log.i("II_CHECK","BUS SELECTED "+i+" "+busId);
        if(!markerMap.containsKey(busId)){
            Toast.makeText(context, "This bus is not loaded yet!", Toast.LENGTH_SHORT).show();
            return;
        }
        tv.setText(Objects.requireNonNull(busIdList.get(i)).getBusName());
        //Ignore focus flag
        Objects.requireNonNull(markerMap.get(busId)).setVisible(true);
        focusedBus=busId;
        willAutoFocus=true;
        soloView.setVisibility(View.VISIBLE);
        soloView.setChecked(true);
        isolateView(soloView);
        focus(Objects.requireNonNull(markerMap.get(busId)).getPosition());
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
                    focus(markerMap.get(focusedBus).getPosition());
                }
            });
        }
    }

    public void reset() {
        markerOptionsMap =new HashMap<>();
        markerMap=new HashMap<>();
        updateState=new HashMap<>();
        buses=null;
        ZOOM_LEVEL=19;
        willZoom=true;
        willAutoFocus=false;
        focusedBus="";
        hide=false;
        context=null;
    }
}
