package trabalho.factory.model;

import trabalho.common.constant.ConstantStock;

public class Stock {
    private int pieces;

    public Stock() {
        this.pieces = ConstantStock.PACES_QUANTITY;
    }

    public synchronized boolean requestPiece() {
        if (pieces > 0) {
            pieces--;
            return true;
        } else {
            return false;
        }
    }

    public synchronized void addPiece() {
        if (pieces < ConstantStock.PACES_QUANTITY) {
            pieces++;
        }
    }

    public synchronized int getPieces() {
        return pieces;
    }
}