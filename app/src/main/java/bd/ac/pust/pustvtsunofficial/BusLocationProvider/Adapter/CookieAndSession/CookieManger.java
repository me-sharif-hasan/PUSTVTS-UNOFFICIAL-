package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.CookieAndSession;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Config;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.CustomFileInputStream;

public class CookieManger {
    private final String COOKIE_FILE = Config.getInstance().getMainContext().getFilesDir()+"cookies.ck";

    private CookieManger() throws Exception {
        /*Loading the cookies*/
        File f = new File(COOKIE_FILE);
        f.createNewFile();
        if (f.exists() && !f.isDirectory()) {
            String[] allCookies = new String(new CustomFileInputStream(f).readAllBytes()).split("\n");
            for (String c : allCookies) {
                if (c.equals("")) continue;
                String[] cookieParts = c.split("<iicookie>");
                int tkey = Integer.parseInt(cookieParts[0]);
                cookies.put(tkey, cookieParts[1]);
            }
        }
    }

    private final Map<Integer, String> cookies = new HashMap<>();

    public void setCookie(int key, String cookie) {
        cookies.put(key, cookie);
        try {
            saveCookieToDisk(key, cookie);
        } catch (Exception e) {
            System.err.println("BANGBANG! Cookie not saved!");
            e.printStackTrace();
        }
    }

    private void saveCookieToDisk(int key, String cookie) throws Exception {
        File f = new File(COOKIE_FILE);

        Map<Integer, String> temp = new HashMap<>();
        String[] allCookies = new String(new CustomFileInputStream(f).readAllBytes()).split("\n");
        for (String c : allCookies) {
            if (c.equals("")) continue;
            String[] cookieParts = c.split("<iicookie>");
            int tkey = Integer.parseInt(cookieParts[0]);
            temp.put(tkey, cookieParts[1]);
        }
        temp.put(key, cookie);

        PrintWriter pw = new PrintWriter(new FileWriter(COOKIE_FILE, false));
        for (int k : temp.keySet()) {
            String save = k + "<iicookie>" + temp.get(k);
            pw.println(save);
        }
        pw.close();
    }

    public String getCookie(int key) throws Exception {
        if (cookies.containsKey(key)) {
            final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss z");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String cookieString = cookies.get(key).trim();
            String[] parts = cookieString.split(";");
            String expireDate = parts[1].trim().split("=")[1].trim();
            Date currentTime = new Date();
            Date expires = sdf.parse(expireDate);
            if (currentTime.compareTo(expires) > 0) {
                throw new Exception("Cookie expired. Please re-login");
            }
            return parts[0];
        } else {
            throw new Exception("Cookie of KEY=" + key + " not found!");
        }
    }

    public boolean checkAllCookie(){
        boolean ans=false;
        int cnt=0;
        for(int key:cookies.keySet()){
            try{
                String ck=getCookie(key);
                ans=true;
                cnt++;
            }catch (Exception e){
                return false;
            }
        }
        Log.d("IILOG",String.valueOf(cnt));
        return ans&&(cnt>=2);
    }

    public void clearCookies() throws Exception {
        File f=new File(COOKIE_FILE);
        FileWriter fw=new FileWriter(f);
        fw.write("");
        fw.close();
        instance=new CookieManger();
    }
    static CookieManger instance;

    public static CookieManger getInstance() throws Exception {
        if (instance == null) instance = new CookieManger();
        return instance;
    }
}
