package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.SimulatedTracker;

import android.util.Log;

import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.CookieAndSession.CookieManger;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.Interfaces.BusTrackerInterface;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerConfig;

import javax.net.ssl.SSLSocket;
import javax.security.auth.login.LoginException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimulatedTracker implements BusTrackerInterface {
    SSLSocket sslSocket;

    public SimulatedTracker() throws IOException {
        sslSocket = TrackerConfig.getSSLSocketForSimulation();
        sslSocket.setKeepAlive(true);
    }

    @Override
    public String getTrackingInformation(String busId) throws Exception {
        try {
            if (!sslSocket.isConnected()|| sslSocket.isClosed()||!sslSocket.isBound()) sslSocket = TrackerConfig.getSSLSocketForSimulation();
            try {
                CookieManger.getInstance().getCookie(2);
                CookieManger.getInstance().getCookie(3);
            } catch (Exception e) {
                System.err.println("Cookies expired, trying re-login");
                login(TrackerConfig.USERNAME, TrackerConfig.PASSWORD);
            }
            String header = TrackerConfig.getSimulatedTrackingHeaders(busId, CookieManger.getInstance().getCookie(2), CookieManger.getInstance().getCookie(3));
            sslSocket.getOutputStream().write(header.getBytes(StandardCharsets.UTF_8));
            byte[] bf = new byte[10000];
            int dataSize = sslSocket.getInputStream().read(bf);
            String[] parts = new String(bf, 0, dataSize).split("\n");
            if (parts[parts.length - 1] == null || Objects.equals(parts[parts.length - 1], ""))
                throw new Exception("DATA NOT RECEIVED FROM SERVER");
            return parts[parts.length - 1];
        }catch (Exception e){
            sslSocket=TrackerConfig.getSSLSocketForSimulation();
            e.printStackTrace();
            throw e;
        }
    }

    public void login(String username,String pass) throws Exception {
        byte[] bf = new byte[10000];
        /* First page loader */
        String firstPageHeader = TrackerConfig.getSimulatedLoginHeaderFirstPage();
        sslSocket.getOutputStream().write(firstPageHeader.getBytes(StandardCharsets.UTF_8));
        int dataSize = sslSocket.getInputStream().read(bf);
        String output = new String(bf, 0, dataSize);
        String middleWare = getMiddleWareToken(output);
        String cookie = getCookie(output).get(0);
        CookieManger.getInstance().setCookie(1, cookie);
        /* Perform login using cookie and session*/
        String loginHeader = TrackerConfig.getSimalatedLoginHeaderDoLogin(cookie, middleWare,username,pass);
        PrintWriter pr=new PrintWriter(sslSocket.getOutputStream());
        pr.println(loginHeader);
        pr.flush();
        Log.d("IIERR","READING");
        Scanner bfr=new Scanner(sslSocket.getInputStream());
        output="";
        while (bfr.hasNextLine()){
            String data=bfr.nextLine();
            if(Objects.equals(data, "")) break;
            output+=data+"\n";
        }
        List<String> cookieSession = getCookie(output);
        String loginCookie = cookieSession.get(0);
        String []cookieParts=loginCookie.split(";")[0].split("=");
        if(cookieParts[0].equals("messages")){
            throw new LoginException("Username or password is incorrect");
        }
        String loginSession = cookieSession.get(1);
        CookieManger.getInstance().setCookie(2, loginCookie);
        CookieManger.getInstance().setCookie(3, loginSession);
    }

    private String getMiddleWareToken(String doc) throws Exception {
        Pattern ptrn = Pattern.compile("<input type=\"hidden\" name=\"csrfmiddlewaretoken\" value=\"(.*)\">");
        Matcher matcher = ptrn.matcher(doc);
        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            throw new Exception("CSRF Middleware missing from source");
        }
    }

    private List<String> getCookie(String doc) {
        String[] parts = doc.split("\n\n")[0].split("\n");
        Map<String, List<String>> heads = new HashMap<>();
        for (String h : parts) {
            String[] head = h.split(":", 2);
            if (head.length == 2) {
                if (!heads.containsKey(head[0].trim())) {
                    heads.put(head[0].trim(), new ArrayList<>());
                }
                heads.get(head[0].trim()).add(head[1].trim());
            }
        }
        return heads.get("Set-Cookie");
    }
}
