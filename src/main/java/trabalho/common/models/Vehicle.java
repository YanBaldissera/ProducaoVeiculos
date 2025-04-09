package trabalho.common.models;

import trabalho.common.enums.VehicleEnum;

public class Vehicle extends Message {
    public final int id;
    public final String color;

    public final VehicleEnum model;
    public final int stationId;
    public final int workerId;
    public final int stationBufferPosition;
    private int storeId = -1;
    private int storeBufferPosition = -1;

    public Vehicle(int id, String color, VehicleEnum model, int station, int employeeId, int stationBufferPosition) {
        this.id = id;
        this.color = color;
        this.model = model;
        this.stationId = station;
        this.workerId = employeeId;
        this.stationBufferPosition = stationBufferPosition;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getStoreBufferPosition() {
        return storeBufferPosition;
    }

    public void setStoreBufferPosition(int storeBufferPosition) {
        this.storeBufferPosition = storeBufferPosition;
    }
}
