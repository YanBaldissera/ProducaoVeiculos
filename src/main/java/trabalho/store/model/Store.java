package trabalho.store.model;

import trabalho.common.models.Vehicle;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Store {
    private final int storeId;
    private final int capacity;
    private final BlockingQueue<Vehicle> storeBuffer;
    private final AtomicInteger positionCounter = new AtomicInteger(0);

    public Store(int storeId, int capacity) {
        this.storeId = storeId;
        this.capacity = capacity;
        this.storeBuffer = new ArrayBlockingQueue<>(capacity);
    }

    public void addVehicle(Vehicle vehicle) throws InterruptedException {
        int pos = positionCounter.getAndUpdate(prev -> (prev + 1) % capacity);
        vehicle.setStoreBufferPosition(pos);
        storeBuffer.put(vehicle);
    }

    public Vehicle sellVehicle() throws InterruptedException {
        return storeBuffer.take();
    }

    public int getAndIncrementPosition() {
        return positionCounter.getAndIncrement() % this.capacity;
    }

    public int getStoreId() {
        return storeId;
    }

    public int getBufferSize() {
        return storeBuffer.size();
    }
}