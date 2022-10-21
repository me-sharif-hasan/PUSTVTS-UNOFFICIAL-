package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import androidx.core.text.HtmlKt;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.CookieAndSession.CookieManger;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.Interfaces.BusTrackerInterface;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerFactory;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Config;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Utility;

public class BusFactory {
    private static Map<Integer,Bus> buses = new HashMap<>();

    public static void createBus(int key, String busId, String busName, String busRoute,boolean willsave) throws Exception {
        Bus newBus = new Bus(busId, busName, busRoute);
        if(willsave) buses.put(key,newBus);
    }
    public static void createBus(int key, String busId, String busName, String busRoute) throws Exception {
        createBus(key,busId,busName,busRoute,true);
    }

    public static void createBus(int key, String busId, String busName) throws Exception {
        createBus(key, busId, busName, "Default",true);
    }

    public static void createBus(int key, String busId) throws Exception {
        createBus(key, busId, "N/A");
    }

    public static void checkBusCanBeConnected() throws Exception{
        createBus(-1,"","","",false);
    }

    public static int getNumberOfBuses(){
        return buses.size();
    }
    public static Map<Integer, Bus> getBuses(){
        return buses;
    }
    public static void createBusList(BusCreatedEvent bce) throws Exception{
        URL url=new URL("https://pustvts.com/app_usersuser_dashboard");
        HttpsURLConnection httpsURLConnection= (HttpsURLConnection) url.openConnection();
        httpsURLConnection.setRequestProperty("Cookie", CookieManger.getInstance().getCookie(2)+";"+CookieManger.getInstance().getCookie(3));
        Scanner s=new Scanner(httpsURLConnection.getInputStream());
        StringBuilder b=new StringBuilder();
        while (s.hasNextLine()){
            b.append(s.nextLine()+"\n");
        }
        Document doc= Jsoup.parse(b.toString());
        Elements rows=doc.getElementsByTag("tr");
        Map <String,String> buses = new HashMap<>();
        createBus(0,"N/A","SHOW ALL");
        bce.onBusCreated("ALL","N/A");
        int i=1;
        for(Element tr:rows){
            Elements column=tr.getElementsByTag("td");
            if(column.size()==0) continue;
            String busName=column.get(0).text().toString();
            String busLink=column.get(3).getElementsByTag("a").get(0).attr("href").split("/")[2];
            Log.d("II_MAP",busName+" "+busLink);
            //buses.put(busLink,busName);
            int cpy=i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        createBus(cpy, busLink, busName);
                        bce.onBusCreated(busName, busLink);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
            i++;
        }
        Log.d("II_DUMP_F","DUMP OKAY");
    }

    public static void checkUsernamePassword(String username,String pass) throws Exception {
        BusTrackerInterface busTrackerInterface= TrackerFactory.getTracker(Utility.TRACKER_TYPE);
        busTrackerInterface.login(username,pass);
    }
    public interface BusCreatedEvent{
        public void onBusCreated(String busName,String busId);
    }
}
