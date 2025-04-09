package trabalho.common.main;

import org.glassfish.tyrus.server.Server;
import trabalho.common.constant.ConstantStation;
import trabalho.factory.model.PieceConveyor;
import trabalho.factory.model.Station;
import trabalho.factory.model.Stock;
import trabalho.factory.websocket.FactoryWebScoket;

public class FactoryMain {
    public static void main(String[] args) {
        // 1. Configurar estoque de peças
        Stock stock = new Stock();

        // 2. Criar esteira de peças com semáforo
        PieceConveyor conveyor = new PieceConveyor(stock);

        // 3. Criar 4 estações de produção
        for (int i = 0; i < ConstantStation.QUANTITY; i++) {
            Station station = new Station(
                    i + 1, // IDs 1-4
                    conveyor
            );
            station.startProduction();
            FactoryWebScoket.registerStation(station);
        }

        // 4. Iniciar servidor WebSocket
        Server server = new Server("localhost", 8080, "/ws", null, FactoryWebScoket.class);

        try {
            server.start();
            System.out.println("Fábrica iniciada na porta 8080");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}
