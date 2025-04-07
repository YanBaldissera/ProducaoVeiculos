package trabalho;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Esteira {
    public static final int CAPACIDADE = 40;
    public static final AtomicInteger idCarro = new AtomicInteger(1); // Controle global de IDs
    private static final Queue<String> veiculos = new LinkedList<>();
    private static final Semaphore semaforoEsteira = new Semaphore(CAPACIDADE, true);

    public static void adicionarCarro(String carro) {
        if (FabricaVeiculos.producaoEncerrada) return;

        try {
            if (!semaforoEsteira.tryAcquire()) {
                System.out.println("[ESTEIRA] Estação aguardando espaço na esteira...");
                semaforoEsteira.acquire();
            }

            synchronized (veiculos) {
                if (FabricaVeiculos.producaoEncerrada) {
                    semaforoEsteira.release();
                    return;
                }

                int id;
                synchronized (idCarro) {
                    id = idCarro.getAndIncrement();
                }

                veiculos.add(id + " (" + carro + ")");
                System.out.println("[ESTEIRA] Carro " + id + " (" + carro + ") adicionado (Total: " + veiculos.size() + "/" + CAPACIDADE + ")");

                if (id >= 500) {
                    FabricaVeiculos.producaoEncerrada = true;
                    System.out.println("Capacidade máxima de produção atingida!");
                    System.out.println("Encerrando Produção!!");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static String removerCarro() {
        synchronized (veiculos) {
            if (!veiculos.isEmpty()) {
                String carro = veiculos.remove();
                semaforoEsteira.release();
                System.out.println("[ESTEIRA] Carro " + carro + " removido (Restantes: " + veiculos.size() + "/" + CAPACIDADE + ")");
                return carro;
            }
        }
        return null;
    }

    public static int getIdAtualCarro() {
        return idCarro.get();
    }

    public static int getQuantidadeVeiculos() {
        synchronized (veiculos) {
            return veiculos.size();
        }
    }
}