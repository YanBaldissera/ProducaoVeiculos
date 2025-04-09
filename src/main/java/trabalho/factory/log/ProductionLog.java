package trabalho.factory.log;

import trabalho.common.models.Vehicle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProductionLog {
    private static final Lock lock = new ReentrantLock();

    private static boolean headerIsAdded = false;

    private static final String fileName = "Production_log.csv";

    public static void log(Vehicle vehicle) {
        lock.lock();
        try (var writer = new FileWriter(fileName, true)) {

            if (!headerIsAdded) {
                writer.append("CARRO_ID,COR,MODELO,ESTACAO_ID,TRABALHADOR_ID,BUFFER_ESTACAO\n");
                headerIsAdded = true;
            }

            writer.append(String.format("%s,%s,%s,%s,%s,%s%n", vehicle.id, vehicle.color, vehicle.model, vehicle.stationId, vehicle.workerId, vehicle.stationBufferPosition));
        } catch (IOException e) {
            System.out.println("Não foi possível gerar o log");
        } finally {
            System.out.println("Produção de veículo: ID: " + vehicle.id + ", Cor: " + vehicle.color +
                    ", Modelo: " + vehicle.model.getDescription() + ", Estação: " + vehicle.stationId +
                    ", Trabalhador: " + vehicle.workerId + ", Posição: " + vehicle.stationBufferPosition);
            lock.unlock();
        }
    }

    public static void DeleteFile() {
        new File(fileName).deleteOnExit();
    }
}
