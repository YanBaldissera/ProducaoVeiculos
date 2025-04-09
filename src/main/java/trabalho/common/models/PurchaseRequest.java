package trabalho.common.models;

public class PurchaseRequest extends Message {
    public int storeId;
    public int position;

    public PurchaseRequest(int storeId, int position) {
        setMessageType("PurchaseRequest");
        this.storeId = storeId;
        this.position = position;
    }

}
