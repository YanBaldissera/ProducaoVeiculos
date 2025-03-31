import java.util.concurrent.Semaphore;
import java.util.Random;

public class Funcionario implements Runnable {
    private final int idEstacao, idFuncionario;
    private final Semaphore ferramentaEsquerda, ferramentaDireita;
    private static final Random random = new Random();


    public Funcionario(int idEstacao, int idFuncionario, Semaphore ferramentaEsquerda, Semaphore ferramentaDireita) {
        this.idEstacao = idEstacao;
        this.idFuncionario = idFuncionario;
        this.ferramentaEsquerda = ferramentaEsquerda;
        this.ferramentaDireita = ferramentaDireita;
    }

    @Override
    public void run() {
        try {
            while (FabricaVeiculos.estoquePecas.get() > 0 && Esteira.carrosProduzidos.get() < 40) {

                solicitarPeca();


                ferramentaEsquerda.acquire();
                ferramentaDireita.acquire();


                produzirCarro();


                ferramentaDireita.release();
                ferramentaEsquerda.release();


                Esteira.adicionarCarro(idEstacao, idFuncionario);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void solicitarPeca() throws InterruptedException {
        FabricaVeiculos.semaforoEsteira.acquire();
        try {
            if (FabricaVeiculos.estoquePecas.get() > 0) {
                FabricaVeiculos.estoquePecas.decrementAndGet();
                System.out.printf("[PECA] Estação %d - Funcionário %d pegou peça (%d restantes)%n",
                        idEstacao, idFuncionario, FabricaVeiculos.estoquePecas.get());
            }
        } finally {
            FabricaVeiculos.semaforoEsteira.release();
        }
    }

    private void produzirCarro() throws InterruptedException {
        System.out.printf("[PROD] Estação %d - Funcionário %d produzindo carro...%n",
                idEstacao, idFuncionario);
        Thread.sleep(1000 + random.nextInt(2000)); // Tempo de produção
    }
}
