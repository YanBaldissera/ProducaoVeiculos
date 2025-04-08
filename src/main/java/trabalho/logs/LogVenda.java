package trabalho.logs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogVenda {

    private static final String ARQUIVO_CSV = "log_venda.csv";
    private static boolean cabecalhoAdicionado = false;

    public static synchronized void registrar(int idCarro, String cor, String modelo, int idEstacao, int idFuncionario, int posicaoEsteira, String loja, int posicaoLoja) {
        try (FileWriter writer = new FileWriter(ARQUIVO_CSV, true)) {

            if (!cabecalhoAdicionado) {
                writer.append("ID,Cor,Modelo,Estacao,Funcionario,PosicaoEsteira,Loja,PosicaoLoja\n");
                cabecalhoAdicionado = true;
            }


            writer.append(String.format("%d,%s,%s,%d,%d,%d,%s,%d%n", idCarro, cor, modelo, idEstacao, idFuncionario, posicaoEsteira, loja, posicaoLoja));
        } catch (IOException e) {
            System.err.println("[LOG VENDA] Erro ao salvar log: " + e.getMessage());
        }
        System.out.printf("[LOG VENDA] ID Veículo: %d | Cor: %s | Modelo: %s | Estação: %d | Funcionário: %d | Posição Esteira: %d | Loja: %s | Posição Loja: %d%n",
                idCarro, cor, modelo, idEstacao, idFuncionario, posicaoEsteira, loja, posicaoLoja);
    }

    public static void removeExistsFileLog() {
        try {
            File file = new File(ARQUIVO_CSV);
            file.deleteOnExit();
        } catch (Exception e) {
            System.out.println(String.format("Não foi possível deletar arquivos de log: %s", e.getMessage()));
        }
    }
}