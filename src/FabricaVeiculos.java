package Fabricacao.ProducaoVeiculos.src;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import org.glassfish.tyrus.server.Server;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint("/fabrica")
public class FabricaVeiculos {
    public static final AtomicInteger estoquePecas = new AtomicInteger(500);
    public static final Semaphore semaforoEsteira = new Semaphore(5);

    private static List<Session> lojasConectadas = new ArrayList<>();
    public static volatile boolean producaoEncerrada = false;

    @OnOpen
    public void onOpen(Session session) {
        lojasConectadas.add(session);
        System.out.println("Nova loja conectada: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        if (message.equals("pedir_veiculo")) {
            String veiculo = Esteira.removerCarro();

            if (veiculo != null) {
                session.getBasicRemote().sendText("Fábrica vendeu o veículo: " + veiculo);

                // Agora sim: se atingiu o limite e esvaziou a esteira, aí sim podemos encerrar de vez
                if (Esteira.getIdAtualCarro() >= 500 && Esteira.getQuantidadeVeiculos() == 0) {
                    session.getBasicRemote().sendText("Produção encerrada, nenhum veículo será mais vendido.");
                    producaoEncerrada = true;
                }

            } else {
                if (Esteira.getIdAtualCarro() >= 500) {
                    session.getBasicRemote().sendText("Produção encerrada, nenhum veículo será mais vendido.");
                    producaoEncerrada = true;
                } else {
                    session.getBasicRemote().sendText("Estoque vazio, aguarde produção.");
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        lojasConectadas.remove(session);
        System.out.println("Loja desconectada: " + session.getId());
    }

    public static void main(String[] args) {
        // Inicia o servidor WebSocket
        new Thread(() -> {
            Server server = new Server("localhost", 8080, "/ws", null, FabricaVeiculos.class);
            try {
                server.start();
                System.out.println("Fábrica WebSocket iniciada em ws://localhost:8080/ws/fabrica");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Inicia a produção
        for (int estacao = 1; estacao <= 4; estacao++) {
            Semaphore[] ferramentas = new Semaphore[5];
            for (int i = 0; i < 5; i++) {
                ferramentas[i] = new Semaphore(1);
            }
            for (int func = 1; func <= 5; func++) {
                new Thread(new Funcionario(estacao, (estacao - 1) * 5 + func,
                        ferramentas[func - 1], ferramentas[func % 5])).start();
            }
        }

        while (true) {
            if (producaoEncerrada && Esteira.getQuantidadeVeiculos() == 0) {
                System.out.println("Todos os veículos vendidos. Encerrando aplicação.");
                System.exit(0); // Finaliza o programa com sucesso
            }

            try {
                Thread.sleep(1000); // Espera 1 segundo antes de verificar novamente
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }


}