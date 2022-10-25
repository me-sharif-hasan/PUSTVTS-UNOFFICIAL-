package bd.ac.pust.pustvtsunofficial.BusLocationProvider.StoppageManager;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

public class StoppageManager {
    private static Map<String,LatLng> stopages=new HashMap<>();
    public static void initiate(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        init();
                        return;
                    } catch (Exception e) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(4000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
    public static void init() throws Exception{
        Log.d("II_NETDUMP","COLLECTING BUS DATA");
        URL u=new URL("http://10.0.2.2/stoppage.txt");
        HttpURLConnection httpURLConnection= (HttpURLConnection) u.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("USER-AGENT","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36");
        httpURLConnection.setConnectTimeout(1000);
        httpURLConnection.connect();
        Log.d("II_NETDUMP","CONNECTED");
        BufferedReader is=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),StandardCharsets.UTF_8));
        char []buff=new char[1024];
        int l=is.read(buff);
        String out=new String(buff,0,l);
        String []outs=out.split("\n");
        for(String ot:outs){
            String []poss=ot.split("\t");
            String name=poss[0];
            String lang=poss[poss.length-1];
            String lat="";
            for(int i=1;i<poss.length-1;i++){
                if(!poss[i].equals("")) lat=poss[i];
            }
            LatLng lt=new LatLng(Float.parseFloat(lat),Float.parseFloat(lang));
            Log.d("II_STOPS",name+" "+lang+" "+lat);
            stopages.put(name,lt);
            if(sle!=null) sle.onStoppageCreated(name,lt);
        }
        Log.d("II_STOP",out);
    }
    private static StoppageLoadEvent sle=null;
    public static void setOnStoppageLoadListener(StoppageLoadEvent stpe){
        sle=stpe;
    }

    public interface StoppageLoadEvent{
        public void onStoppageCreated(String stoppageName,LatLng l);
    }
    public static Map <String,LatLng> getStoppages(){
        return stopages;
    }
}
