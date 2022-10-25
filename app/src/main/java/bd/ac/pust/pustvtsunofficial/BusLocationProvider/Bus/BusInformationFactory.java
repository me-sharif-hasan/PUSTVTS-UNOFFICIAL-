package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BusInformationFactory {
    private BusInformationFactory(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("II_NETDUMP","COLLECTING BUS DATA");
                    URL u=new URL("http://10.0.2.2/BusInfo.json");
                    HttpURLConnection httpURLConnection= (HttpURLConnection) u.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setRequestProperty("USER-AGENT","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36");
                    httpURLConnection.setConnectTimeout(1000);
                    httpURLConnection.connect();
                    Log.d("II_NETDUMP","CONNECTED");
                    InputStream is=httpURLConnection.getInputStream();
                    byte []buff=new byte[1024];
                    int l=is.read(buff);
                    String out=new String(buff,0,l);
                    JSONObject jsonObject=new JSONObject(out);
                    busData=new JSONObject(jsonObject.getString("bus"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    JSONObject busData;

    public JSONObject getBusInfo(String busId) throws JSONException {
        return new JSONObject(busData.getString(busId));
    }

    private static BusInformationFactory instance;
    public static BusInformationFactory initiate(){
        if(instance==null) instance=new BusInformationFactory();
        return instance;
    }
}
