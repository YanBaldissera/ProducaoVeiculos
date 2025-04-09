package trabalho.factory.model;

import trabalho.common.constant.ConstantWorker;

import java.util.concurrent.Semaphore;

public class PieceConveyor {
    private final Semaphore semaphore;
    private final Stock stock;

    public PieceConveyor(Stock stock) {
        this.stock = stock;
        this.semaphore = new Semaphore(ConstantWorker.WORKER_CAPACITY);
    }

    public boolean requestPiece() throws InterruptedException {
        semaphore.acquire();
        try {
            return stock.requestPiece();
        } finally {
            semaphore.release();
        }
    }
}