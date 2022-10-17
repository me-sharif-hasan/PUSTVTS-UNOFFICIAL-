package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter;

import android.app.Activity;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;

public abstract class TrackerConfig {
    public static int TRACK_BY_SIMULATION = 1;
    public static int TRACK_BY_API = 2;
    public static String USERNAME = "students";
    public static String PASSWORD = "stu.pass123";


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

    public static String getSimalatedLoginHeaderDoLogin(String csrfToken, String csrfMiddleWareToken) {
        return "POST /app_userssign_me_in HTTP/1.1\n" +
                "Host: pustvts.com\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:105.0) Gecko/20100101 Firefox/105.0\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8\n" +
                "Accept-Language: en-US,en;q=0.5\n" +
                "Referer: https://pustvts.com/\n" +
                "Content-Type: application/x-www-form-urlencoded\n" +
                "Content-Length: 123\n" +
                "Origin: https://pustvts.com\n" +
                "Connection: keep-alive\n" +
                "Cookie: " + csrfToken + "\n" +
                "Upgrade-Insecure-Requests: 1\n" +
                "Sec-Fetch-Dest: document\n" +
                "Sec-Fetch-Mode: navigate\n" +
                "Sec-Fetch-Site: same-origin\n" +
                "Sec-Fetch-User: ?1\n" +
                "\n" +
                "csrfmiddlewaretoken=" + csrfMiddleWareToken + "&username=students&password=stu.pass123";
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
        sslSocket.connect(new InetSocketAddress("pustvts.com", 443), 10000);
        sslSocket.startHandshake();
        return sslSocket;
    }
}
