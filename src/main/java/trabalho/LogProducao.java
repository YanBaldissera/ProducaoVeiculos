package trabalho;

import java.io.FileWriter;
import java.io.IOException;

public class LogProducao {
    private static final String ARQUIVO_CSV = "log_producao.csv";
    private static boolean cabecalhoAdicionado = false;

    public static synchronized void registrar(int idCarro, String cor, String modelo, int idEstacao, int idFuncionario, int posicaoEsteira) {
        try (FileWriter writer = new FileWriter(ARQUIVO_CSV, true)) {

            if (!cabecalhoAdicionado) {
                writer.append("ID,Cor,Modelo,Estacao,Funcionario,PosicaoEsteira\n");
                cabecalhoAdicionado = true;
            }


            writer.append(String.format("%d,%s,%s,%d,%d,%d%n", idCarro, cor, modelo, idEstacao, idFuncionario, posicaoEsteira));
        } catch (IOException e) {
            System.err.println("[LOG PRODUÇÃO] Erro ao salvar log: " + e.getMessage());
        }
        System.out.printf("[LOG PRODUÇÃO] ID Veículo: %d | Cor: %s | Modelo: %s | Estação: %d | Funcionário: %d | Posição Esteira: %d%n",
                idCarro, cor, modelo, idEstacao, idFuncionario, posicaoEsteira);
    }
}