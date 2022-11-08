package bd.ac.pust.pustvtsunofficial.Updater;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import androidx.core.content.FileProvider;
import bd.ac.pust.pustvtsunofficial.BuildConfig;

public class AppUpdater {
    private AppUpdater(){}
    private static AppUpdater appUpdater;
    public static AppUpdater getInstance(){
        if(appUpdater==null) appUpdater=new AppUpdater();
        return appUpdater;
    }
    public void checkUpdate(UpdateCheckListener updateCheckListener){
        int versionCode = BuildConfig.VERSION_CODE;
        Log.d("II_VER", String.valueOf(versionCode));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL u=new URL("https://github.com/me-sharif-hasan/blog-content/raw/main/app_update.json");
                    HttpsURLConnection httpsURLConnection= (HttpsURLConnection) u.openConnection();
                    byte []buff=new byte[1024];
                    int l=httpsURLConnection.getInputStream().read(buff);
                    String json=new String(buff,0,l);
                    JSONObject jsonObject=new JSONObject(json);
                    Log.d("II_VER",json);
                    int availableV=jsonObject.getInt("version_code");
                    String link=jsonObject.getString("link");
                    String msg="Version: "+availableV;
                    try{
                        msg=jsonObject.getString("msg");
                    }catch (Exception e){}
                    if(availableV>versionCode) {
                        if (updateCheckListener != null)
                            updateCheckListener.onUpdateAvailable(link,availableV,msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }

    public void downloadAndUpdate(String link, Context context,UpdateProgress updateProgress) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(link);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    context.getExternalFilesDirs(null);
                    String PATH = Environment.getExternalStorageDirectory() + "/Android/data/"+context.getPackageName()+"/";
                    String app=PATH + "app.apk";
                    Log.d("PATH",app);
                    //if(!app.equals("")) return;
                    File folder = new File(PATH);
                    folder.mkdirs();
                    File outputFile = new File(folder, "app.apk");
                    FileOutputStream fos = new FileOutputStream(outputFile);

                    InputStream is = c.getInputStream();

                    byte[] buffer = new byte[1024];
                    int len1 = 0;
                    while ((len1 = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len1);
                        if(updateProgress!=null) updateProgress.onDownloadData(len1);
                    }
                    fos.close();
                    is.close();

                    File file=new File(app);
                    if (file.exists()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        String type = "application/vnd.android.package-archive";
                        Log.d("INSTALLING","A{{");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Uri downloadedApk = FileProvider.getUriForFile(context, "ir.greencode", file);
                            intent.setDataAndType(downloadedApk, type);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else {
                            intent.setDataAndType(Uri.fromFile(file), type);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        if(updateProgress!=null)updateProgress.onComplete();
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.d("II_ERR_U","ERR "+e.getLocalizedMessage());
                    e.printStackTrace();
                    if(updateProgress!=null) updateProgress.onError(e.getLocalizedMessage());
                    //Toast.makeText(context.getApplicationContext(), "Update error!", Toast.LENGTH_LONG).show();
                }
            }
        }).start();
    }

    public interface UpdateCheckListener{
        public void onUpdateAvailable(String url, int availableV,String msg);
    }
    public interface UpdateProgress{
        public void onDownloadData(long size);
        public void onComplete();
        public void onError(String localizedMessage);
    }
}
