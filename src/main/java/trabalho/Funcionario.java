package trabalho;

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
        while (true) {
            if (FabricaVeiculos.producaoEncerrada) break;

            try {
                solicitarPeca();

                // Produção
                System.out.println("[PROD] Estação " + idEstacao + " - Funcionário " + idFuncionario + " produzindo carro...");
                Thread.sleep(random.nextInt(200) + 100);

                ferramentaEsquerda.acquire();
                ferramentaDireita.acquire();

                if (FabricaVeiculos.producaoEncerrada) {
                    ferramentaEsquerda.release();
                    ferramentaDireita.release();
                    break;
                }

                // Escolhe cor e modelo
                String[] cores = {"Vermelho", "Verde", "Azul"};
                String[] modelos = {"SEDAN", "SUV"};
                String cor = cores[random.nextInt(3)];
                String modelo = modelos[random.nextInt(2)];

                int idCarro;
                synchronized (Esteira.idCarro) {
                    idCarro = Esteira.getIdAtualCarro();
                }
                int posicaoEsteira = Esteira.getQuantidadeVeiculos() + 1;

                // Adiciona o carro na esteira
                Esteira.adicionarCarro(cor + " " + modelo);

                // Registro no Log de Produção
                LogProducao.registrar(idCarro, cor, modelo, idEstacao, idFuncionario, posicaoEsteira);

                ferramentaEsquerda.release();
                ferramentaDireita.release();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void solicitarPeca() throws InterruptedException {
        FabricaVeiculos.semaforoEsteira.acquire();
        try {
            if (FabricaVeiculos.estoquePecas.get() > 0) {
                FabricaVeiculos.estoquePecas.decrementAndGet();
                System.out.printf("[PECA] Estação %d - Funcionário %d pegou peça%n", idEstacao, idFuncionario);
            }
        } finally {
            FabricaVeiculos.semaforoEsteira.release();
        }
    }
}