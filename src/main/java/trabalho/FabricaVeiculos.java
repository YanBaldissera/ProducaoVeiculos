package trabalho;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import org.glassfish.tyrus.server.Server;
import trabalho.comandos.Comando;
import trabalho.decoder.MessageDecoder;
import trabalho.enums.TipoComando;
import trabalho.logs.LogVenda;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint(value = "/fabrica", decoders = {MessageDecoder.class})
public class FabricaVeiculos {
    public static final AtomicInteger estoquePecas = new AtomicInteger(500);
    public static final Semaphore semaforoEsteira = new Semaphore(5);
    private static List<Session> lojasConectadas = new ArrayList<>();
    public static volatile boolean producaoEncerrada = false;

    private Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session) {
        lojasConectadas.add(session);
        System.out.println("Nova loja conectada: " + session.getId());
    }

    @OnMessage
    public void onMessage(Comando comando, Session session) throws IOException {
        if (TipoComando.PEDIR_VEICULO.equals(comando.tipoComando)) {
            String carro = Esteira.removerCarro();
            int idCarro = Esteira.getIdAtualCarro();
            int posicaoEsteira = Esteira.getQuantidadeVeiculos() + 1;
            String[] corModelo = carro != null ? carro.replaceAll("[()]", "").split(" ") : new String[]{"-", "-"};

            if (carro != null) {
                int posicaoLoja = 1;
                session.getBasicRemote().sendText(gson.toJson(Comando.getInstance(TipoComando.VENDER_VEICULO,
                        List.of(
                                "Fábrica vendeu o veículo: " + carro,
                                String.valueOf(idCarro), corModelo[0], corModelo[1], "0", "0", String.valueOf(posicaoEsteira), comando.context.get(0), String.valueOf(posicaoLoja)))));
                LogVenda.registrar(idCarro, corModelo[0], corModelo[1], 0, 0, posicaoEsteira, "Loja", posicaoLoja);

                if (Esteira.getIdAtualCarro() >= 500 && Esteira.getQuantidadeVeiculos() == 0) {
                    session.getBasicRemote().sendText("");
                    session.getBasicRemote().sendText(gson.toJson(Comando.getInstance(TipoComando.PRODUCAO_ENCERRADA,
                            List.of("Produção encerrada, nenhum veículo será mais vendido."))));
                    producaoEncerrada = true;
                }

            } else {
                if (Esteira.getIdAtualCarro() >= 500) {
                    session.getBasicRemote().sendText(gson.toJson(Comando.getInstance(TipoComando.PRODUCAO_ENCERRADA,
                            List.of("Produção encerrada, nenhum veículo será mais vendido."))));
                    producaoEncerrada = true;
                } else {
                    session.getBasicRemote().sendText(gson.toJson(Comando.getInstance(TipoComando.ESTOQUE_VAZIO,
                            List.of("Estoque vazio, aguarde produção."))));
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
        LogVenda.removeExistsFileLog();
        new Thread(() -> {
            Server server = new Server("localhost", 8080, "/ws", null, FabricaVeiculos.class);
            try {
                server.start();
                System.out.println("Fábrica WebSocket iniciada em ws://localhost:8080/ws/fabrica");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        for (int estacao = 1; estacao <= 4; estacao++) {
            Semaphore[] ferramentas = new Semaphore[5];
            for (int i = 0; i < 5; i++) {
                ferramentas[i] = new Semaphore(1);
            }
            for (int func = 1; func <= 5; func++) {
                new Thread(new Funcionario(estacao, (estacao - 1) * 5 + func, ferramentas[func - 1], ferramentas[func % 5])).start();
            }
        }

        while (true) {
            if (producaoEncerrada && Esteira.getQuantidadeVeiculos() == 0) {
                System.out.println("Todos os veículos vendidos. Encerrando aplicação.");
                System.exit(0);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}