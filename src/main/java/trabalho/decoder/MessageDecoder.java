package trabalho.decoder;

import com.google.gson.Gson;
import trabalho.comandos.Comando;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<Comando> {
    private static Gson gson = new Gson();


    @Override
    public Comando decode(String s) throws DecodeException {
        return gson.fromJson(s, Comando.class);
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }
}
