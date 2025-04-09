package trabalho.store.model;

import trabalho.common.models.Vehicle;
import trabalho.factory.log.SaleLog;
import trabalho.store.log.StoreLog;

public class Customer implements Runnable {
    private final int customerId;
    private final Garage garage;
    private final Store[] stores;

    public Customer(int customerId, Store[] stores) {
        this.customerId = customerId;
        this.stores = stores;
        this.garage = new Garage();
    }

    @Override
    public void run() {
        while (true) {
            try {
                int index = (int)(Math.random() * stores.length);
                Store selectedStore = stores[index];

                Vehicle vehicle = selectedStore.sellVehicle();
                int storeBufferPosition = selectedStore.getBufferSize();

                StoreLog.log(vehicle, selectedStore.getStoreId(), storeBufferPosition, customerId);

                garage.addVehicle(vehicle);

                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}