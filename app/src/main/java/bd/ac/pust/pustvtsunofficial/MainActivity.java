package bd.ac.pust.pustvtsunofficial;

import androidx.appcompat.app.AppCompatActivity;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusFactory;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Config;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
}