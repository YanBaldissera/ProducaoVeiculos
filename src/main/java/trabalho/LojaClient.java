package trabalho;

import com.google.gson.Gson;
import trabalho.comandos.Comando;
import trabalho.decoder.MessageDecoder;
import trabalho.enums.TipoComando;

import javax.websocket.*;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

@ClientEndpoint(decoders = {MessageDecoder.class})
public class LojaClient implements Runnable {
    private static final String SERVER_URI = "ws://localhost:8080/ws/fabrica";
    private Session session;
    private final String nomeLoja;
    private final Random random = new Random();
    private final Queue<Comando> esteira = new LinkedList<>();
    private final int CAPACIDADE_ESTEIRA = 10;
    private boolean aguardandoResposta = false;
    private boolean producaoEncerrada = false;

    public LojaClient(String nomeLoja) {
        this.nomeLoja = nomeLoja;
    }

    public boolean isAtiva() {
        synchronized (esteira) {
            return !producaoEncerrada || !esteira.isEmpty();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println(nomeLoja + " conectada à fábrica!");
    }

    @OnMessage
    public void onMessage(Comando comando) {
        if (TipoComando.VENDER_VEICULO.equals(comando.tipoComando)) {
            synchronized (esteira) {
                if (esteira.size() < CAPACIDADE_ESTEIRA) {
                    esteira.add(comando);
                    System.out.println(nomeLoja + " comprou um veículo: " + comando.context.get(1));
                    System.out.println("Carros disponíveis em esteira: " + esteira.size());
                }
            }
        } else {
            System.out.println(nomeLoja + " recebeu resposta: " + comando.context.get(0));

            if (TipoComando.PRODUCAO_ENCERRADA.equals(comando.tipoComando)) {
                producaoEncerrada = true;
            }
        }

        aguardandoResposta = false;
    }

    @OnClose
    public void onClose() {
        System.out.println(nomeLoja + " foi desconectada da fábrica.");
    }

    @Override
    public void run() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(SERVER_URI));

            while (true) {
                if (producaoEncerrada && esteira.isEmpty()) {
                    System.out.println(nomeLoja + " encerrando operações. Nenhum carro restante.");
                    if (session != null && session.isOpen()) {
                        session.close(); // fecha a conexão com a fábrica
                    }
                    break;
                }

                if (session != null && session.isOpen()) {
                    synchronized (esteira) {
                        if (esteira.size() < CAPACIDADE_ESTEIRA && !aguardandoResposta) {
                            System.out.println(nomeLoja + " tentando comprar um veículo...");
                            session.getBasicRemote().sendText(new Gson().toJson(Comando.getInstance(TipoComando.PEDIR_VEICULO, List.of(nomeLoja))));
                            aguardandoResposta = true;
                        }
                    }
                }

                Thread.sleep(500 + random.nextInt(800));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized Comando venderParaCliente() {
        synchronized (esteira) {
            return esteira.poll();
        }
    }

    public String getNome() {
        return nomeLoja;
    }

    public static void main(String[] args) {
        LojaClient lojaA = new LojaClient("Loja A");
        LojaClient lojaB = new LojaClient("Loja B");
        LojaClient lojaC = new LojaClient("Loja C");

        List<LojaClient> lojas = List.of(lojaA, lojaB, lojaC);

        new Thread(lojaA).start();
        new Thread(lojaB).start();
        new Thread(lojaC).start();

        for (int i = 1; i <= 20; i++) {
            new Thread(new ClienteComprador("Cliente " + i, lojas)).start();
        }
    }
}