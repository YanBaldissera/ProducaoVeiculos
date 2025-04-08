package trabalho;

import trabalho.comandos.Comando;
import trabalho.logs.LogClientComprou;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClienteComprador implements Runnable {
    private final String nome;
    private final List<LojaClient> lojas;
    private final Random random = new Random();
    private final List<String> garagem = new ArrayList<>();

    public ClienteComprador(String nome, List<LojaClient> lojas) {
        this.nome = nome;
        this.lojas = lojas;
        LogClientComprou.removeExistsFileLog();
    }

    @Override
    public void run() {
        while (true) {
            try {

                LojaClient loja = lojas.get(random.nextInt(lojas.size()));


                if (!loja.isAtiva()) {
                    System.out.println(nome + " percebeu que a " + loja.getNome() + " encerrou e foi embora.");
                    break;
                }

                Comando carro = loja.venderParaCliente();
                if (carro != null) {
                    garagem.add(carro.context.get(0));
                    System.out.println(nome + " comprou um carro da " + loja.getNome() + ": " + carro);
                    LogClientComprou.registrar(
                            carro.context.get(1),//idCarro
                            carro.context.get(2),//cor
                            carro.context.get(3),//modelo
                            carro.context.get(7),//loja
                            nome//cliente
                    );
                } else {
                    System.out.println(nome + " foi na " + loja.getNome() + ", mas ela está sem carros. Esperando...");
                }

                Thread.sleep(2000 + random.nextInt(3500));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        System.out.println("\n📦 " + nome + " encerrou com " + garagem.size() + " carro(s) na garagem:");

    }
}