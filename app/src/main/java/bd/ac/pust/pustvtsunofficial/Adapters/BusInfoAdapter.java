package bd.ac.pust.pustvtsunofficial.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.Bus;
import bd.ac.pust.pustvtsunofficial.R;

public class BusInfoAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList <CustomViews> arv=new ArrayList<>();
    Spinner parent=null;
    public BusInfoAdapter(@NonNull Context context) {
        this.context=context;
        inflater= LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arv.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return arv.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void add(@Nullable CustomViews object) {
        arv.add(object);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return arv.get(position).load(inflater,parent);
    }

    public static class CustomViews extends Object{
        String n,t,r;
        View v;

        TextView name,route;
        ImageView type;
        Bus mbus;
        public CustomViews(Bus bus){
            mbus=bus;
        }
        protected View load(LayoutInflater layoutInflater,ViewGroup parent){
            if(v==null){
                v=layoutInflater.inflate(R.layout.show_bus_list,null);
                name=v.findViewById(R.id.bus_name);
                route=v.findViewById(R.id.route);
                type=v.findViewById(R.id.type);
            }
            return v;
        }


        public void setName(String name) {
            this.name.setText(name);
        }
        public void setRoute(String route) {
            this.route.setText(route);
        }
        public void setType(String route) {
            //this.route.setText(route);
        }
    }


}
