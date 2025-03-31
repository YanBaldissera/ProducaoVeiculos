import java.util.concurrent.atomic.AtomicInteger;

public class Esteira {
    public static final int CAPACIDADE = 40;
    public static final AtomicInteger carrosProduzidos = new AtomicInteger(0);
    private static final String[] CORES = {"Vermelho", "Verde", "Azul"};
    private static final String[] TIPOS = {"SUV", "SEDAN"};
    private static final AtomicInteger idCarro = new AtomicInteger(1);

    public static synchronized void adicionarCarro(int idEstacao, int idFuncionario) {
        if (carrosProduzidos.get() >= CAPACIDADE) return;

        int id = idCarro.getAndIncrement();
        String cor = CORES[id % 3];
        String tipo = TIPOS[id % 2];
        int produzidos = carrosProduzidos.incrementAndGet();

        LogManager.logCarroProduzido(id, cor, tipo, idEstacao, idFuncionario, produzidos, CAPACIDADE);
        System.out.printf("[ESTEIRA] Carro %d (%s %s) adicionado (Total: %d/%d)%n",
                id, cor, tipo, produzidos, CAPACIDADE);
    }
}