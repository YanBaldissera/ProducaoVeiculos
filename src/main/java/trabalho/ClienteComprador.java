package trabalho;

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

                String carro = loja.venderParaCliente();
                if (carro != null) {
                    garagem.add(carro);
                    System.out.println(nome + " comprou um carro da " + loja.getNome() + ": " + carro);
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