package bd.ac.pust.pustvtsunofficial;

import androidx.appcompat.app.AppCompatActivity;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusFactory;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Config;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        setStatusBarTransparent();
        Config.getInstance().setMainContext(this);

            BusFactory busFactory = new BusFactory();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BusFactory.createBus(1, "0351510093645193");
                        BusFactory.createBus(2, "0351510093643297");
                        System.out.println(BusFactory.whereAreThisBus(1));
                        System.out.println(BusFactory.whereAreThisBus(1));
                        System.out.println(BusFactory.whereAreThisBus(2));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();

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