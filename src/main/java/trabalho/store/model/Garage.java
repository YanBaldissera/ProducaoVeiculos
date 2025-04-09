package trabalho.store.model;

import trabalho.common.models.Vehicle;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Garage {
    private final BlockingQueue<Vehicle> vehicles;

    public Garage() {
        this.vehicles = new LinkedBlockingQueue<>();
    }

    public void addVehicle(Vehicle vehicle) throws InterruptedException {
        vehicles.put(vehicle);
    }

    public Vehicle getVehicle() throws InterruptedException {
        return vehicles.take();
    }
}