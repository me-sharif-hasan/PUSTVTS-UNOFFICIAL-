package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter;

import android.app.Activity;
import android.util.Log;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Config;

public abstract class TrackerConfig {
    public static int TRACK_BY_SIMULATION = 1;
    public static int TRACK_BY_API = 2;
    public static String USERNAME = "";
    public static String PASSWORD = "";
    private static String loginDir= Config.getInstance().getMainContext().getFilesDir()+"lgin.cr";

    public static void updateUserAndPass(String username,String pass) throws Exception{
        System.out.println("III "+loginDir);
        File f=new File(loginDir);
        if(!f.exists()){
            f.createNewFile();
        }
        FileOutputStream fos=new FileOutputStream(f,false);
        fos.write((username+"\n"+pass).getBytes(StandardCharsets.UTF_8));
        fos.close();
        Log.d("IILOG","CREDENTIAL SAVED");
    }
    public static String[] getUserAndPass() throws Exception{
        File f=new File(loginDir);
        if(!f.exists()){
            f.createNewFile();
        }
        FileInputStream fis=new FileInputStream(f);
        byte []buff=new byte[1024];
        int l;
        StringBuilder output= new StringBuilder();
        while((l=fis.read(buff))!=-1){
            output.append(new String(buff, 0, l));
        }
        String s=output.toString();
        String []parts=s.split("\n");
        if(parts.length>=2) {
            return parts;
        }
        return null;
    }

    public static String getSimulatedLoginHeaderFirstPage() {
        return "GET / HTTP/1.1\n" +
                "Host: pustvts.com\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:105.0) Gecko/20100101 Firefox/105.0\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8\n" +
                "Accept-Language: en-US,en;q=0.5\n" +
                "Connection: keep-alive\n" +
                "Upgrade-Insecure-Requests: 1\n" +
                "Sec-Fetch-Dest: document\n" +
                "Sec-Fetch-Mode: navigate\n" +
                "Sec-Fetch-Site: none\n" +
                "Sec-Fetch-User: ?1\n" +
                "\n";
    }

    public static String getSimalatedLoginHeaderDoLogin(String csrfToken, String csrfMiddleWareToken,String username,String pass) {
        String payload="csrfmiddlewaretoken=" + csrfMiddleWareToken + "&username="+username+"&password="+pass;
        return "POST /app_userssign_me_in HTTP/1.1\nHost: pustvts.com\nUser-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:105.0) Gecko/20100101 Firefox/105.0\nAccept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8\nAccept-Language: en-US,en;q=0.5\nReferer: https://pustvts.com/\nContent-Type: application/x-www-form-urlencoded\nContent-Length: "+payload.length()+"\nOrigin: https://pustvts.com\nConnection: keep-alive\nCookie: " + csrfToken + "\nUpgrade-Insecure-Requests: 1\nSec-Fetch-Dest: document\nSec-Fetch-Mode: navigate\nSec-Fetch-Site: same-origin\nSec-Fetch-User: ?1\n\n" +payload;
    }

    public static String getSimulatedTrackingHeaders(String busId, String cookie, String session) {
        return "GET /app_deviceslive_tracking_ajax?dev_hid=" + busId + " HTTP/1.1\n" +
                "Accept: */*\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Accept-Language: bn,en-US;q=0.9,en;q=0.8\n" +
                "Connection: keep-alive\n" +
                "Cookie: " + session + "; " + cookie + "\n" +
                "Host: pustvts.com\n" +
                "Referer: https://pustvts.com/app_deviceslive_tracking/" + busId + "\n" +
                "Sec-Fetch-Dest: empty\n" +
                "Sec-Fetch-Mode: cors\n" +
                "Sec-Fetch-Site: same-origin\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36\n" +
                "X-Requested-With: XMLHttpRequest\n" +
                "sec-ch-ua: \"Chromium\";v=\"106\", \"Google Chrome\";v=\"106\", \"Not;A=Brand\";v=\"99\"\n" +
                "sec-ch-ua-mobile: ?0\n" +
                "sec-ch-ua-platform: \"Windows\"\n\n";
    }


    public static SSLSocket getSSLSocketForSimulation() throws IOException {
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket();
        sslSocket.connect(new InetSocketAddress("pustvts.com", 443), 4000);
        sslSocket.startHandshake();
        return sslSocket;
    }
}
