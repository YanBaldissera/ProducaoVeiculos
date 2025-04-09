package trabalho.store.log;

import trabalho.common.models.Vehicle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StoreLog {
    private static final Lock lock = new ReentrantLock();

    private static boolean headerIsAdded = false;

    private static final String fileName = "Store_log.csv";

    public static void log(Vehicle vehicle, int storeId, int storeBufferPosition, int customerId) {
        lock.lock();
        try (var writer = new FileWriter(fileName, true)) {

            if (!headerIsAdded) {
                writer.append("CARRO_ID,LOJA_ID,LOJA_BUFFER,CUSTOMER\n");
                headerIsAdded = true;
            }

            writer.append(String.format("%s,%s,%s,%s%n", vehicle.id, storeId, storeBufferPosition, customerId));
        } catch (IOException e) {
            System.out.println("Não foi possível gerar o log");
        } finally {
            System.out.println("Compra de veículo - ID: " + vehicle.id +
                    ", Loja: " + storeId +
                    ", Loja Buffer: " + storeBufferPosition +
                    ", Comprador: " + customerId);
            lock.unlock();
        }
    }

    public static void DeleteFile() {
        new File(fileName).deleteOnExit();
    }
}