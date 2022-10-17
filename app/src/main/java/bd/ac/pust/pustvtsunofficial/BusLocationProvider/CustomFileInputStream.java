package bd.ac.pust.pustvtsunofficial.BusLocationProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CustomFileInputStream extends FileInputStream {
    public CustomFileInputStream(File file) throws FileNotFoundException {
        super(file);
    }
    public byte []readAllBytes() throws Exception{
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        byte []buff=new byte[1024];
        int l=0;
        while ((l=this.read(buff))!=-1){
            byteArrayOutputStream.write(buff,0,l);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
