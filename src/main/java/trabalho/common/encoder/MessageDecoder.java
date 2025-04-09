package trabalho.common.encoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import trabalho.common.models.Message;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder<T> implements Decoder.Text<Object> {

    @Override
    public Object decode(String s) throws DecodeException {
        return new Gson().fromJson(s, Object.class);
    }

    @Override
    public boolean willDecode(String s) {
        return s != null && !s.isBlank();
    }


    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }
}