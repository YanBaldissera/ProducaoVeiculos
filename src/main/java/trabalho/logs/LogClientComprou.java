package trabalho.logs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogClientComprou {

    private static final String ARQUIVO_CSV = "compra_client.csv";
    private static boolean cabecalhoAdicionado = false;

    public static synchronized void registrar(String idCarro, String cor, String modelo, String loja, String cliente) {
        try (FileWriter writer = new FileWriter(ARQUIVO_CSV, true)) {

            if (!cabecalhoAdicionado) {
                writer.append("ID,Cor,Modelo, cliente, loja\n");
                cabecalhoAdicionado = true;
            }


            writer.append(String.format("%s,%s,%s,%s,%s%n", idCarro, cor, modelo, cliente, loja));
        } catch (IOException e) {
            System.err.println("[LOG CLIENTE COMPROU] Erro ao salvar log: " + e.getMessage());
        }
        System.out.printf("[LOG CLIENTE COMPROU] ID Veículo: %s | Cor: %s | Modelo: %s | cliente: %s | loja: %s%n",
                idCarro, cor, modelo, cliente, loja);
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
