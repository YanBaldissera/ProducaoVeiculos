package trabalho.factory.websocket;

import com.google.gson.Gson;
import trabalho.common.encoder.MessageDecoder;
import trabalho.common.models.PurchaseRequest;
import trabalho.common.models.Vehicle;
import trabalho.factory.log.ProductionLog;
import trabalho.factory.log.SaleLog;
import trabalho.factory.model.Station;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint(
        value = "/factory",
        decoders = MessageDecoder.class
)
public class FactoryWebScoket {

    private static final List<Session> sessions = new CopyOnWriteArrayList<>();
    private static final List<Station> stations = new CopyOnWriteArrayList<>();

    public static void registerStation(Station station) {
        ProductionLog.DeleteFile();
        SaleLog.DeleteFile();
        stations.add(station);
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(Session session, Object message) {
        PurchaseRequest p = new Gson().fromJson(new Gson().toJson(message), PurchaseRequest.class);
        handlePurchaseRequest(session, p);
    }

    private void handlePurchaseRequest(Session session, PurchaseRequest request) {
        Vehicle vehicle = findAvailableVehicle();
        if (vehicle != null) {
            vehicle.setStoreId(request.storeId);
            vehicle.setStoreBufferPosition(request.position);
            SaleLog.log(vehicle, vehicle.getStoreId(),  vehicle.getStoreBufferPosition());
            session.getAsyncRemote().sendText(new Gson().toJson(vehicle, Vehicle.class));
        }
    }

    private Vehicle findAvailableVehicle() {
        for (Station station : stations) {
            Vehicle vehicle = station.getProductionBuffer().poll();
            if (vehicle != null) return vehicle;
        }
        return null;
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }
}
