package bd.ac.pust.pustvtsunofficial.BusLocationProvider;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.Bus;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusInfo;
import bd.ac.pust.pustvtsunofficial.MainActivity;

public class Config {
    private Config(){
        busMapper.put("students",new ArrayList<>());
        busMapper.put("teachers",new ArrayList<>());

        busMapper.get("students").add(new BusInfo("N/A","SHOW ALL","N?A"));
        busMapper.get("students").add(new BusInfo("0351510093645193","BUS 1 (BOYS)","Ananta Bazar - Shohor - Meril - Campus"));
        busMapper.get("students").add(new BusInfo("0351510093643297","BUS 2 (BOYS)","Ananta Bazar - Shohor - Meril - Campus"));
        busMapper.get("students").add(new BusInfo("0351510093647488","BUS 3 (BOYS)","Ananta Bazar - Shohor - Meril - Campus"));
        busMapper.get("students").add(new BusInfo("0351510093648122","BUS 4 (BOYS)","Ananta Bazar - Shohor - Meril - Campus"));
        busMapper.get("students").add(new BusInfo("0351510093645326","BUS 6 (BOYS)","Ananta Bazar - Shohor - Meril - Campus"));
        busMapper.get("students").add(new BusInfo("0351510093645268","BUS 7 (BOYS)","Ananta Bazar - Shohor - Meril - Campus"));
        busMapper.get("students").add(new BusInfo("0351510093648080","BUS 8 (BOYS)","Ananta Bazar - Shohor - Meril - Campus"));
        busMapper.get("students").add(new BusInfo("0351510093646084","BUS 9 (BOYS)","Ananta Bazar - Shohor - Meril - Campus"));

    }
    private Activity mainContext;
    private static Config instance;

    public void setMainContext(Activity mainContext) {
        this.mainContext = mainContext;
    }

    public Activity getMainContext() {
        return mainContext;
    }

    public static Config getInstance() {
        if(instance==null) instance=new Config();
        return instance;
    }
    Map<String, ArrayList<BusInfo>> busMapper=new HashMap<>();

    public Map<String, ArrayList<BusInfo>> getBusMapper() {
        return busMapper;
    }
}
