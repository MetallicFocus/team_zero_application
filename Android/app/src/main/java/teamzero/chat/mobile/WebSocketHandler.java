package teamzero.chat.mobile;

public class WebSocketHandler {

    private static WebSocket socket;

    public static synchronized WebSocket getSocket() {
        return socket;
    }

    public static synchronized void setSocket(WebSocket socket) {
        WebSocketHandler.socket = socket;
    }

}