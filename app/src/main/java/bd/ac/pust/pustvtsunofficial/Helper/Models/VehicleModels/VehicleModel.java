package bd.ac.pust.pustvtsunofficial.Helper.Models.VehicleModels;

public class VehicleModel {
    private String vehicleName;
    private String vehicleType;
    private String lastUpdate;

    public VehicleModel(String vehicleName, String vehicleType, String lastUpdate) {
        this.vehicleName = vehicleName;
        this.vehicleType = vehicleType;
        this.lastUpdate = lastUpdate;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
