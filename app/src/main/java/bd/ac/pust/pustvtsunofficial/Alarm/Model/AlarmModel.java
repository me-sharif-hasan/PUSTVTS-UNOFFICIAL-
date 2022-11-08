package bd.ac.pust.pustvtsunofficial.Alarm.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Config;

public class AlarmModel {
    private String vechileName;
    private String alarmTime;

    public AlarmModel(final String vechileName, final String alarmTime){
        this.vechileName = vechileName;
        this.alarmTime = alarmTime;
    }

    public String getVechileName() {
        return this.vechileName;
    }

    public void setVechileName(final String vechileName) {
        this.vechileName = vechileName;
    }

    public String getAlarmTime() {
        return this.alarmTime;
    }

    public void setAlarmTime(final String alarmTime) {
        this.alarmTime = alarmTime;
    }
}
