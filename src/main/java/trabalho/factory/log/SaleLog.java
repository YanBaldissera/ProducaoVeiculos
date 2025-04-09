package trabalho.factory.log;

import trabalho.common.models.Vehicle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SaleLog {
    private static final Lock lock = new ReentrantLock();

    private static boolean headerIsAdded = false;

    private static final String fileName = "Sale_log.csv";

    public static void log(Vehicle vehicle, int storeId, int storeBufferPosition) {
        lock.lock();
        try (var writer = new FileWriter(fileName, true)) {

            if (!headerIsAdded) {
                writer.append("CARRO_ID,COR,MODELO,ESTACAO_ID,TRABALHADOR_ID,BUFFER_ESTACAO, LOJA_ID, POSICAO_LOJA\n");
                headerIsAdded = true;
            }

            writer.append(String.format(
                    "%s,%s,%s,%s,%s,%s,%s,%s%n",
                    vehicle.id, vehicle.color, vehicle.model, vehicle.stationId, vehicle.workerId, vehicle.stationBufferPosition, storeId, storeBufferPosition
            ));
        } catch (IOException e) {
            System.out.println("Não foi possível gerar o log");
        } finally {
            System.out.println("Venda de carro pela fabrica, ID: " + vehicle.id + ", cor: " + vehicle.color +
                    ", modelo: " + vehicle.model + ", estação: " + vehicle.stationId +
                    ", trabalhador: " + vehicle.workerId + ", posição: " +
                    vehicle.stationBufferPosition + ", loja: " + storeId + ", posição na loja: " +
                    storeBufferPosition);
            lock.unlock();
        }
    }

    public static void DeleteFile() {
        new File(fileName).deleteOnExit();
    }
}
