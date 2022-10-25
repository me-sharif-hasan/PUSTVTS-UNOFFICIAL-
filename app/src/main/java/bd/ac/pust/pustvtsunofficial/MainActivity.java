package bd.ac.pust.pustvtsunofficial;

import androidx.appcompat.app.AppCompatActivity;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.CookieAndSession.CookieManger;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerConfig;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerFactory;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.Bus;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusFactory;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusInformationFactory;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Config;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.StoppageManager.StoppageManager;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Utility;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    Button retry;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        setStatusBarTransparent();
        Config.getInstance().setMainContext(this);
        retry=findViewById(R.id.retry);
        pb=findViewById(R.id.progressBar);

        BusInformationFactory.initiate();


        //Creating buses
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retry.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       moveOn();
                   }
               }).start();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                moveOn();
            }
        }).start();

    }

    void moveOn(){
        Log.d("IILOG","Moving on");
        try {
            Log.d("II_CON","CHECKING");
            BusFactory.checkBusCanBeConnected(); //for connecting purpose.
            Log.d("II_CON","CONNECTION OK");
        } catch (Exception e) {
            Log.d("II_CON","CAN'T BE CONNECTED");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pb.setVisibility(View.GONE);
                    retry.setVisibility(View.VISIBLE);
                }
            });
            e.printStackTrace();
            return;
        }

        try {
            if(CookieManger.getInstance().checkAllCookie()){
                Log.d("IILOG","ALL COOKIES OKAY");
                Intent i=new Intent(MainActivity.this, BusLocatorActivity.class);
                startActivity(i);
                finish();
                return;
            }
        }catch (Exception e){
            Log.d("IIERR",e.getLocalizedMessage());
        }

        try {
            String []userAndPass=TrackerConfig.getUserAndPass();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(userAndPass!=null) {
                            BusFactory.checkUsernamePassword(userAndPass[0], userAndPass[1]);
                            Log.d("IILOG","USING AUTO LOG IN");
                            Intent i=new Intent(MainActivity.this,BusLocatorActivity.class);
                            startActivity(i);
                            finish();
                        }else{
                            throw new Exception("WRONG USER AND PASSWORD!");
                        }
                    }catch (Exception e){
                        try {
                            TimeUnit.MILLISECONDS.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        Log.d("IIERR",e.getLocalizedMessage());
                        Intent i=new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }).start();

        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "BangBang! Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
            return;
        }
    }

    private void setStatusBarTransparent() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        View decorView = window.getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        window.setStatusBarColor(Color.TRANSPARENT);
    }
}