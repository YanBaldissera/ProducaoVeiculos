package trabalho.factory.model;

import trabalho.common.constant.ConstantWorker;
import trabalho.common.models.Vehicle;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Station {
    private final int stationId;
    private final BlockingQueue<Vehicle> productionBuffer;
    private final PieceConveyor pieceConveyor;
    private final Worker[] employees;

    public Station(int stationId, PieceConveyor pieceConveyor) {
        this.stationId = stationId;
        this.productionBuffer = new ArrayBlockingQueue<>(ConstantWorker.WORKER_BUFFER);
        this.pieceConveyor = pieceConveyor;
        this.employees = new Worker[ConstantWorker.WORKER_CAPACITY];
        for (int i = 0; i < ConstantWorker.WORKER_CAPACITY; i++) {
            employees[i] = new Worker(i, stationId, pieceConveyor, productionBuffer);
        }
    }

    public void startProduction() {
        for (Worker emp : employees) {
            new Thread(emp, "Employee-" + emp).start();
        }
    }

    public BlockingQueue<Vehicle> getProductionBuffer() {
        return productionBuffer;
    }
}
