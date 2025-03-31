import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class FabricaVeiculos {
    public static final AtomicInteger estoquePecas = new AtomicInteger(500);
    public static final Semaphore semaforoEsteira = new Semaphore(5);

    public static void main(String[] args) {
        for (int estacao = 1; estacao <= 4; estacao++) {
            Semaphore[] ferramentas = new Semaphore[5];
            for (int i = 0; i < 5; i++) {
                ferramentas[i] = new Semaphore(1); // Cada ferramenta é um recurso compartilhado
            }
            for (int func = 1; func <= 5; func++) {
                new Thread(new Funcionario(estacao, (estacao-1)*5 + func,
                        ferramentas[func-1], ferramentas[func % 5])).start();
            }
        }
    }
}