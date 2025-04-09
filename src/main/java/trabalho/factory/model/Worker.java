package trabalho.factory.model;

import trabalho.common.constant.ConstantWorker;
import trabalho.common.enums.VehicleEnum;
import trabalho.common.models.Vehicle;
import trabalho.factory.log.ProductionLog;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Runnable {
    private final int employeeId;
    private final int stationId;
    private final PieceConveyor pieceConveyor;
    private final BlockingQueue<Vehicle> stationBuffer;
    private static int vehicleSequence = 0;

    private final AtomicInteger positionCounter = new AtomicInteger(0);

    public Worker(int employeeId, int stationId, PieceConveyor pieceConveyor, BlockingQueue<Vehicle> stationBuffer) {
        this.employeeId = employeeId;
        this.stationId = stationId;
        this.pieceConveyor = pieceConveyor;
        this.stationBuffer = stationBuffer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Request a piece from the conveyor
                boolean pieceReceived = pieceConveyor.requestPiece();
                if (!pieceReceived) {
                    Thread.sleep(100);
                    continue;
                }

                Thread.sleep(200);

                int currentVehicleId;
                synchronized (Worker.class) {
                    currentVehicleId = ++vehicleSequence;
                }
                String color = getColorBySequence(currentVehicleId);
                VehicleEnum model = (currentVehicleId % 2 == 0) ? VehicleEnum.SUV : VehicleEnum.SEDAN;
                int bufferPosition = positionCounter.getAndIncrement() % ConstantWorker.WORKER_BUFFER;
                Vehicle vehicle = new Vehicle(currentVehicleId, color, model, stationId, employeeId, bufferPosition);


                stationBuffer.put(vehicle);
                ProductionLog.log(vehicle);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String getColorBySequence(int seq) {
        int mod = seq % 3;
        if (mod == 1)
            return "R";
        else if (mod == 2)
            return "G";
        else
            return "B";
    }
}
