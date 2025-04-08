package trabalho.comandos;

import trabalho.enums.TipoComando;

import java.util.List;

/**
 * Classe responsável por presentar um comando de venda de veiculo da fábrica para a loja
 */
public class Comando {

    public TipoComando tipoComando;

    public List<String> context; //pode-se enviar informações de contexto dentro da mensagem

    private static Comando _instance;

    private Comando() { /**private constructor*/}

    public static Comando getInstance(TipoComando tipoComando, List<String> context) {
        if (_instance == null) {
            _instance = new Comando();
        }

        _instance.tipoComando = tipoComando;
        _instance.context = context;
        return _instance;
    }
}
