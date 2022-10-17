package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.SimulatedTracker;

import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.CookieAndSession.CookieManger;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.Interfaces.BusTrackerInterface;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerConfig;

import javax.net.ssl.SSLSocket;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimulatedTracker implements BusTrackerInterface {
    SSLSocket sslSocket;

    public SimulatedTracker() throws LoginException, IOException {
        if (TrackerConfig.PASSWORD == null || TrackerConfig.USERNAME == null) {
            throw new LoginException("Username or Password is not given. Please set it first in TrackerConfig class.");
        }
        sslSocket = TrackerConfig.getSSLSocketForSimulation();
    }

    @Override
    public String getTrackingInformation(String busId) throws Exception {
        if (!sslSocket.isConnected()) sslSocket = TrackerConfig.getSSLSocketForSimulation();
        try {
            CookieManger.getInstance().getCookie(2);
            CookieManger.getInstance().getCookie(3);
        } catch (Exception e) {
            System.err.println("Cookies expired, trying re-login");
            login();
        }
        String header = TrackerConfig.getSimulatedTrackingHeaders(busId, CookieManger.getInstance().getCookie(2), CookieManger.getInstance().getCookie(3));
        sslSocket.getOutputStream().write(header.getBytes(StandardCharsets.UTF_8));
        byte[] bf = new byte[10000];
        int dataSize = sslSocket.getInputStream().read(bf);
        String[] parts = new String(bf, 0, dataSize).split("\n");
        if (parts[parts.length - 1] == null || Objects.equals(parts[parts.length - 1], ""))
            throw new Exception("DATA NOT RECEIVED FROM SERVER");
        return parts[parts.length - 1];
    }

    private void login() throws Exception {
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
        String loginHeader = TrackerConfig.getSimalatedLoginHeaderDoLogin(cookie, middleWare);
        sslSocket.getOutputStream().write(loginHeader.getBytes(StandardCharsets.UTF_8));
        dataSize = sslSocket.getInputStream().read(bf);
        output = new String(bf, 0, dataSize);
        List<String> cookieSession = getCookie(output);
        String loginCookie = cookieSession.get(0);
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
