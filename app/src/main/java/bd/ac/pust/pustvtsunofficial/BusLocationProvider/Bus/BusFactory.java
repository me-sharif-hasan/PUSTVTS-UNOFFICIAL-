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
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.Nullable;
import androidx.core.text.HtmlKt;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.CookieAndSession.CookieManger;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.Interfaces.BusTrackerInterface;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerFactory;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Config;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Utility;

public class BusFactory {
    public interface BusLoadListener{
        void onBusLoaded(Bus bus, int busKey);
    }
    private static BusLoadListener bll=null;

    private static final ArrayList <BusLoadListener> bla=new ArrayList<>();
    public static void setBusLoadListener(BusLoadListener bll) {
        Log.e("II_AC","SETTING");
        System.out.println("Printing stack trace:");
        BusFactory.bll = bll;
        bla.add(bll);
    }

    private static Map<Integer,Bus> buses = new HashMap<>();

    public static void createBus(int key, String busId, String busName, String busRoute,boolean willsave) throws Exception {
        Bus newBus = new Bus(busId, busName, busRoute);
        if(willsave)buses.put(key,newBus);
        if(bla!=null&&bla.size()!=0){
            Log.d("II_007","Calling");
            //bll.onBusLoaded(newBus,key);
            for(BusLoadListener b:bla) b.onBusLoaded(newBus,key);
        }
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
    public static void createBusList(@Nullable BusCreatedEvent bce) throws Exception{
        URL url=new URL("https://pustvts.com/app_usersuser_dashboard");
        HttpsURLConnection httpsURLConnection= (HttpsURLConnection) url.openConnection();
        httpsURLConnection.setRequestProperty("Cookie", CookieManger.getInstance().getCookie(2)+";"+CookieManger.getInstance().getCookie(3));
        Scanner s=new Scanner(httpsURLConnection.getInputStream());
        StringBuilder b=new StringBuilder();
        while (s.hasNextLine()){
            b.append(s.nextLine()).append("\n");
        }
        Document doc= Jsoup.parse(b.toString());
        Elements rows=doc.getElementsByTag("tr");
        if(rows.size()==0){
            throw new Exception("NO BUS FOUND");
        }
        createBus(0,"N/A","ALL");
        if(bce!=null) bce.onBusCreated("ALL","N/A");
        int i=1;
        for(Element tr:rows){
            Elements column=tr.getElementsByTag("td");
            if(column.size()==0) continue;
            String busName= column.get(0).text();
            String busLink=column.get(3).getElementsByTag("a").get(0).attr("href").split("/")[2];
            Log.d("II_MAP",busName+" "+busLink);
            //buses.put(busLink,busName);
            int cpy=i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            createBus(cpy, busLink, busName);
                            if(bce!=null) bce.onBusCreated(busName, busLink);
                            break;
                        } catch (Exception e) {
                            try {
                                TimeUnit.MILLISECONDS.sleep(1000);
                            }catch (Exception te){

                            }
                            e.printStackTrace();
                        }
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
        void onBusCreated(String busName, String busId);
    }
    public static void reset(){
        buses=new HashMap<>();
    }
}
