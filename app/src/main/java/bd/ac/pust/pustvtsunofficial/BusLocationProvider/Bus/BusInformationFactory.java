package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class BusInformationFactory {
    private BusInformationFactory() throws Exception{
            Log.d("II_NETDUMP", "COLLECTING BUS DATA x");
            URL u = new URL("https://raw.githubusercontent.com/me-sharif-hasan/blog-content/main/BusInfo.json");
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) u.openConnection();
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setRequestProperty("USER-AGENT", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36");
            httpsURLConnection.setConnectTimeout(10000);
            httpsURLConnection.connect();
            Log.d("II_NETDUMP", "CONNECTED");
            InputStream is = httpsURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
            String str;
            StringBuilder out=new StringBuilder();
            while ((str = bufferedReader.readLine()) != null) {
                out.append(str);
            }
//
//            byte[] buff = new byte[20024];
//            int l = is.read(buff);
//            String out = new String(buff, 0, l);
            Log.d("II_BUS_INFO", String.valueOf(out));
            JSONObject jsonObject = new JSONObject(String.valueOf(out));
            busData = new JSONObject(jsonObject.getString("bus"));
            if(bil!=null) bil.onBusInfoLoaded();
    }
    BusInfoLoaded bil=null;
    public void setBusInfoLoadedEvent(BusInfoLoaded b){
        bil=b;
    }

    public interface BusInfoLoaded{
        void onBusInfoLoaded();
    }

    JSONObject busData;

    public JSONObject getBusInfo(String busId) throws JSONException {
        return new JSONObject(busData.getString(busId));
    }

    private static BusInformationFactory instance=null;
    public static BusInformationFactory initiate() throws Exception {
        if(instance==null) instance=new BusInformationFactory();
        return instance;
    }
}
