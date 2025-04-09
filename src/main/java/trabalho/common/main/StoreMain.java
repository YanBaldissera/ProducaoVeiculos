package trabalho.common.main;

import trabalho.common.constant.ConstantCustomer;
import trabalho.common.constant.ConstantStore;
import trabalho.store.model.Customer;
import trabalho.store.model.Store;
import trabalho.store.websocket.StoreWebSocketClient;

public class StoreMain {
    public static void main(String[] args) {
        // 1. Criar 3 lojas com buffer de 40 posições cada
        Store[] stores = new Store[ConstantStore.QUANTITY];
        for (int i = 0; i < ConstantStore.QUANTITY; i++) {
            stores[i] = new Store(
                    i + 1,
                    ConstantStore.CAPACITY
            );

            StoreWebSocketClient.connect(
                    stores[i],
                    "ws://localhost:8080/ws/factory"
            );
        }

        // 2. Criar 20 clientes
        for (int i = 0; i < ConstantCustomer.QUANTITY; i++) {
            new Thread(new Customer(
                    i + 1,
                    stores
            )).start();
        }

        // 3. Manter aplicação rodando
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}