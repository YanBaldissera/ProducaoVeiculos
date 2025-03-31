import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LogManager {
    private static final String ARQUIVO_LOG = "producao_carros.log";

    public static void logCarroProduzido(int id, String cor, String tipo,
                                         int idEstacao, int idFuncionario,
                                         int posicaoEsteira, int capacidade) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_LOG, true))) {
            writer.write(String.format(
                    "Carro %d: %s %s (Estação %d - Func %d) [%d/%d]%n",
                    id, cor, tipo, idEstacao, idFuncionario, posicaoEsteira, capacidade
            ));
        } catch (IOException e) {
            System.err.println("Erro ao gravar log: " + e.getMessage());
        }
    }
}