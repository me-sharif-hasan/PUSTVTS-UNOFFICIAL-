package bd.ac.pust.pustvtsunofficial.BusLocationProvider.StoppageManager;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerConfig;

public class StoppageManager {
    private static StoppageManager stoppageManager;
    private StoppageManager(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    load();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static StoppageManager init(){
        if(stoppageManager==null) stoppageManager=new StoppageManager();
        return stoppageManager;
    }
    private final Map<String,LatLng> stoppages =new HashMap<>();
    public void load() throws Exception{
        Log.d("II_NETDUMP","COLLECTING BUS DATA");
        String userName= TrackerConfig.getUserAndPass()[0];
        URL u=new URL("https://raw.githubusercontent.com/me-sharif-hasan/blog-content/main/" +
                "stoppage-"+userName+".txt");
        HttpsURLConnection httpsURLConnection= (HttpsURLConnection) u.openConnection();
        httpsURLConnection.setConnectTimeout(1000);
        httpsURLConnection.connect();
        Log.d("II_STOP","CONNECTED");
        BufferedReader is=new BufferedReader(new InputStreamReader(httpsURLConnection.
                getInputStream(),StandardCharsets.UTF_8));
        Log.d("II_STOP","READING");
        char []buff=new char[1024];
        int l=is.read(buff);
        String out=new String(buff,0,l);
        Log.d("II_STOP",out);
        String []outs=out.split("\n");
        for(String ot:outs){
            String []poss=ot.split("\t");
            String name=poss[0];
            String lang=poss[poss.length-1];
            String lat="";
            for(int i=1;i<poss.length-1;i++){
                if(!poss[i].equals("")) lat=poss[i];
            }
            LatLng lt=new LatLng(Double.parseDouble(lat),Double.parseDouble(lang));
            Log.d("II_STOPS",name+" "+lang+" "+lat);
            stoppages.put(name,lt);
            if(sle!=null) sle.onStoppageCreated(name,lt);
        }
        Log.d("II_STOP",out);
    }

    public LatLng getStoppage(String name) throws Exception {
        if(stoppages.containsKey(name)) return stoppages.get(name);
        else throw new Exception("No such stoppage");
    }

    private StoppageLoadEvent sle=null;
    public void setOnStoppageLoadListener(StoppageLoadEvent stpe){
        sle=stpe;
    }

    public interface StoppageLoadEvent{
        void onStoppageCreated(String stoppageName, LatLng l);
    }
}
