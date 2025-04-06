package Fabricacao.ProducaoVeiculos.src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LogManager {
    private static final String ARQUIVO_LOG_FABRICA = "producao_carros.log";
    private static final String ARQUIVO_LOG_VENDA_LOJA = "venda_lojas.log";
    private static final String ARQUIVO_LOG_VENDA_CLIENTE = "venda_clientes.log";

    public static void logCarroProduzido(int id, String corTipo,
                                         int idEstacao, int idFuncionario,
                                         int posicaoEsteira, int capacidade) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_LOG_FABRICA, true))) {
            writer.write(String.format(
                    "Carro %d: %s %s (Estação %d - Func %d) [%d/%d]%n",
                    id, corTipo, idEstacao, idFuncionario, posicaoEsteira, capacidade
            ));
        } catch (IOException e) {
            System.err.println("Erro ao gravar log de produção: " + e.getMessage());
        }
    }

    public static void logVendaParaLoja(int id, String cor, String tipo,
                                        int idEstacao, int idFuncionario,
                                        int posicaoEsteiraFabrica,
                                        String nomeLoja, int posicaoEsteiraLoja) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_LOG_VENDA_LOJA, true))) {
            writer.write(String.format(
                    "Carro %d: %s %s (Estação %d - Func %d) [Fábrica: %d] -> Vendido para %s [Esteira: %d]%n",
                    id, cor, tipo, idEstacao, idFuncionario, posicaoEsteiraFabrica, nomeLoja, posicaoEsteiraLoja
            ));
        } catch (IOException e) {
            System.err.println("Erro ao gravar log de venda para loja: " + e.getMessage());
        }
    }

    public static void logVendaParaCliente(int id, String cor, String tipo,
                                           int idEstacao, int idFuncionario,
                                           String nomeLoja, String nomeCliente, int posicaoGaragem) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_LOG_VENDA_CLIENTE, true))) {
            writer.write(String.format(
                    "Carro %d: %s %s (Estação %d - Func %d) -> Vendido por %s para %s [Garagem: %d]%n",
                    id, cor, tipo, idEstacao, idFuncionario, nomeLoja, nomeCliente, posicaoGaragem
            ));
        } catch (IOException e) {
            System.err.println("Erro ao gravar log de venda para cliente: " + e.getMessage());
        }
    }
}
