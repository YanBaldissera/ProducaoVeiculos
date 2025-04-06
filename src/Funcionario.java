package Fabricacao.ProducaoVeiculos.src;

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

                // produção
                System.out.println("[PROD] Estação " + idEstacao + " - Funcionário " + idFuncionario + " produzindo carro...");
                Thread.sleep(new Random().nextInt(200) + 100);

                ferramentaEsquerda.acquire();
                ferramentaDireita.acquire();

                // montagem usando ferramentas


                if (FabricaVeiculos.producaoEncerrada) {
                    ferramentaEsquerda.release();
                    ferramentaDireita.release();
                    break;
                }

                // escolhe cor e modelo
                String[] cores = {"Vermelho", "Verde", "Azul"};
                String[] modelos = {"SEDAN", "SUV"};
                String carro = cores[new Random().nextInt(3)] + " " + modelos[new Random().nextInt(2)];

                Esteira.adicionarCarro(carro);

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
                System.out.printf("[PECA] Estação %d - Funcionário %d pegou peça%n",
                        idEstacao, idFuncionario);
            }
        } finally {
            FabricaVeiculos.semaforoEsteira.release();
        }
    }

}