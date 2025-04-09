package trabalho.store.websocket;

import com.google.gson.Gson;
import trabalho.common.encoder.MessageDecoder;
import trabalho.common.models.PurchaseRequest;
import trabalho.common.models.Vehicle;
import trabalho.store.log.StoreLog;
import trabalho.store.model.Store;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint(
        decoders = MessageDecoder.class
)
public class StoreWebSocketClient {

    private Session session;
    private final Store store;

    public StoreWebSocketClient(Store store) {
        StoreLog.DeleteFile();
        this.store = store;
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        startPurchaseRequests();
    }

    @OnMessage
    public void onMessage(Object message) {
        try {
            Vehicle v = new Gson().fromJson(new Gson().toJson(message), Vehicle.class);
            store.addVehicle(v);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void startPurchaseRequests() {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (store.getBufferSize() < 40) {
                        session.getAsyncRemote().sendText(
                                new Gson().toJson(new PurchaseRequest(store.getStoreId(), store.getBufferSize()), PurchaseRequest.class)
                        );
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public static void connect(Store store, String uri) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(
                    new StoreWebSocketClient(store),
                    URI.create(uri)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
