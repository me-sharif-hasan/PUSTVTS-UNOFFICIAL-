package Maps;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.Bus;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusFactory;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Config;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Utility;
import bd.ac.pust.pustvtsunofficial.R;

public class MapController implements OnMapReadyCallback {
    Map<String, MarkerOptions> markers=new HashMap<>();
    Map<String,Boolean> updateState=new HashMap<>();
    double ZOOM_LEVEL=17;
    boolean willZoom=true;
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




    }
    private void startUpdate(GoogleMap gmap) throws Exception {
        List <Bus> buses=null;
        int nob=0;
        while (true){
            if(BusFactory.getNumberOfBuses()!=nob){
                buses=BusFactory.getBuses();
                nob=BusFactory.getNumberOfBuses();
                Log.i("II_INFO","NUMBER OF BUS IS UPDATED TO: "+nob);
            }
            if(buses!=null) {
                for (Bus bus : buses) {
                    Bus copyBus=bus;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String busId=copyBus.getBusId();
                                if(updateState.containsKey(busId)&& Boolean.TRUE.equals(updateState.get(busId))) return;
                                copyBus.whereAreYou();
                                double lon=copyBus.getBusLon();
                                double lat=copyBus.getBusLat();
                                if(markers.containsKey(busId)){
                                    markers.get(busId).position(new LatLng(lon,lat));
                                    Log.i("II_MESSAGE","MARKER UPDATED FOR BUS: "+bus.getBusName());
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
                                            gmap.addMarker(markerOptions);
                                            Log.i("II_INFO","BUS MARKER ADDED FOR: "+busId);
                                            if(willZoom) {
                                                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), (float) ZOOM_LEVEL);
                                                gmap.animateCamera(yourLocation);
                                                willZoom=false;
                                            }
                                        }
                                    });
                                    markers.put(busId,markerOptions);
                                }
                                sleep(1000);
                                updateState.put(busId,false);
                            }catch (Exception e){
                                Log.e("II_ERROR",e.getLocalizedMessage());
                            }
                        }
                    }).start();
                    sleep(1000);
                }
            }
            sleep(500);
        }
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
}
