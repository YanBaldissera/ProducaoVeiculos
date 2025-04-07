package trabalho;

import javax.websocket.*;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

@ClientEndpoint
public class LojaClient implements Runnable {
    private static final String SERVER_URI = "ws://localhost:8080/ws/fabrica";
    private Session session;
    private final String nomeLoja;
    private final Random random = new Random();
    private final Queue<String> esteira = new LinkedList<>();
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
    public void onMessage(String message) {
        if (message.contains("Fábrica vendeu o veículo")) {
            synchronized (esteira) {
                if (esteira.size() < CAPACIDADE_ESTEIRA) {
                    esteira.add(message);
                    System.out.println(nomeLoja + " comprou um veículo: " + message);
                    System.out.println("Carros disponíveis em esteira: " + esteira.size());
                }
            }
        } else {
            System.out.println(nomeLoja + " recebeu resposta: " + message);

            if (message.contains("Produção encerrada")) {
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
                            session.getBasicRemote().sendText("pedir_veiculo");
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

    public synchronized String venderParaCliente() {
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