package bd.ac.pust.pustvtsunofficial.Maps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.Bus;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusFactory;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Config;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.StoppageManager.StoppageManager;
import bd.ac.pust.pustvtsunofficial.Helper.VehiclesInfoBottomSheet;
import bd.ac.pust.pustvtsunofficial.Maps.Modules.DirectionFinder;
import bd.ac.pust.pustvtsunofficial.Maps.Modules.DirectionFinderListener;
import bd.ac.pust.pustvtsunofficial.Maps.Modules.Route;
import bd.ac.pust.pustvtsunofficial.R;

public class MapController implements OnMapReadyCallback, DirectionFinderListener {
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
    public MapController(AppCompatActivity context){
        this.context=context;
    }
    public MapController(){}

    public void setContext(AppCompatActivity context) {
        this.context = context;
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gmap=googleMap;
        notifyStoppageChange();
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Bus bus= (Bus) marker.getTag();
                try {
                    VehiclesInfoBottomSheet bottomSheet = new VehiclesInfoBottomSheet(bus.getBusName(), bus.getBusType(), bus.getBusRoute(), bus.getStartTime(bus.getEngineStatus()),bus.getEngineStatus());
                    bottomSheet.show(context.getSupportFragmentManager(),bottomSheet.getTag());
                }catch (Exception e){
                    Log.e("II_ERROR","BUS INFORMATION NOT LOADED");
                    e.printStackTrace();
                }
                return false;
            }
        });
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(@NonNull Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                Log.d("II_DRAG",marker.getPosition().latitude+"\t"+marker.getPosition().longitude);
            }

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {

            }
        });
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
        Bitmap startIcon=BitmapFactory.decodeResource(context.getResources(), R.mipmap.bus_marker_start);
        Bitmap normalIcon=BitmapFactory.decodeResource(context.getResources(), R.mipmap.bus_marker);
        BusFactory.setBusLoadListener(new BusFactory.BusLoadListener() {
            @Override
            public void onBusLoaded(Bus bus,int busKey) {
                busIdList.put(busKey,bus);
                if(bus.getBusId().equals("N/A")) return;
                bus.setUpdateInterval(new Bus.UpdateActionListener(){
                    boolean currentColor=true;
                    @Override
                    public void setLocationUpdateInterval(Bus targetBus) {
                        try {
                            if(!focusedBus.equals(targetBus.getBusId())&&hide) return;
                            targetBus.whereAreYou();
                            double lon = targetBus.getBusLon();
                            double lat = targetBus.getBusLat();
                            Log.d("II_ENGINE",targetBus.getEngineStatus()+" for "+targetBus.getBusName());
                            Bitmap PmarkerIcon;
                            if(targetBus.getEngineStatus()){
                                PmarkerIcon =startIcon;
                            }else{
                                PmarkerIcon=normalIcon;
                            }
                            Bitmap markerIcon=PmarkerIcon.copy(Bitmap.Config.ARGB_8888, true);
                            Canvas c=new Canvas(markerIcon);
                            Paint p=new Paint();
                            if(currentColor){
                                p.setColor(Color.TRANSPARENT);
                                currentColor=false;
                            }else{
                                p.setColor(Color.RED);
                                currentColor=true;
                            }
                            c.drawCircle((float) (markerIcon.getWidth()/2+0.09), markerIcon.getHeight()*0.24f, 15.0F,p);

                            if(markerMap.containsKey(targetBus.getBusId())){
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Marker marker=markerMap.get(targetBus.getBusId());
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(markerIcon));
                                        marker.setPosition(new LatLng(lat,lon));
                                        if(!hide){
                                            marker.setVisible(true);
                                        }
                                        if(willAutoFocus&&targetBus.getBusId().equals(focusedBus)){
                                            focus(marker.getPosition());
                                        }
                                    }
                                });
                            }else{
                                MarkerOptions markerOptions=createNewMarker(lon,lat);
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(markerIcon));
                                markerOptions.title(targetBus.getBusName());
                                Config.getInstance().getMainContext().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Marker marker=gmap.addMarker(markerOptions);
                                        marker.setTag(targetBus);
                                        markerMap.put(targetBus.getBusId(),marker);
                                        markerOptionsMap.put(targetBus.getBusId(),markerOptions);
                                        if(hide&&!focusedBus.equals(targetBus.getBusId())){
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


    private void focus(LatLng latLng){
        if(gmap==null||context==null) return;
        gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) (willZoom==true?ZOOM_LEVEL:gmap.getCameraPosition().zoom)));
        context.findViewById(R.id.recenter).setVisibility(View.INVISIBLE);
    }
    private MarkerOptions createNewMarker(double lon,double lat){
        LatLng l=new LatLng(lat,lon);
        MarkerOptions mop=new MarkerOptions().position(l);
        mop.anchor(0.5f,0.56f);
        return mop;
    }

    void sleep(int t){
        try {
            TimeUnit.MILLISECONDS.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void selectBusId(int i) {
        Log.e("II_ERR",String.valueOf(i)+" "+busIdList.size());
        if(busIdList==null||!busIdList.containsKey(i)||gmap==null) return;
        TextView tv=context.findViewById(R.id.bus_name_show);
        Log.i("II_CHECK","BUS SELECTED "+i+" "+"OKAY ");
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

    Map <String,LatLng> stoppages=new HashMap<>();
    public void addStoppage(String name,LatLng l){
        stoppages.put(name,l);
    }
    synchronized public void notifyStoppageChange(){
        if(gmap==null) return;
        for(String key:stoppages.keySet()){
            LatLng l=stoppages.get(key);
            MarkerOptions mop=createNewMarker(l.longitude,l.latitude);
            Bitmap b=BitmapFactory.decodeResource(context.getResources(),R.mipmap.bus_stop);
            mop.icon(BitmapDescriptorFactory.fromBitmap(b));
            mop.title(key);
            mop.draggable(true);
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gmap.addMarker(mop);
                }
            });
        }
        stoppages.clear();
    }

    public void showRoute(LatLng stoppage) {
        if(focusedBus.equals("")) return;
        Log.d("II_000",stoppage.latitude+" "+stoppage.longitude);
        new DirectionFinder(this,"das","dfsf");
    }

    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> route) {

    }
}
