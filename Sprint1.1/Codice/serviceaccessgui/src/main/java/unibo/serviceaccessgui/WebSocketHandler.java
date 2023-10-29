package unibo.serviceaccessgui;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/*
In Spring we can create a customized handler by extends abstract class
AbstractWebSocketHandler or one of it's subclass,
either TextWebSocketHandler or BinaryWebSocketHandler:
 */
public class WebSocketHandler extends AbstractWebSocketHandler {
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private JSONParser jsonparser = new JSONParser();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("WebSocketHandler | Added the session: " + session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("WebSocketHandler | afterConnectionClosed:" + session);
        super.afterConnectionClosed(session, status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String movecmd = message.getPayload();
        System.out.println("WebSocketHandler | handleTextMessage Received: " + movecmd);
        try {

            JSONObject json = (JSONObject) jsonparser.parse(movecmd);
            String move = json.get("robotmove").toString();
            System.out.println("WebSocketHandler | handleTextMessage doing: " + move);
            //RobotUtils.sendMsg(RobotController.robotName,move);
        } catch (Exception e) {
            System.out.println("WebSocketHandler | handleTextMessage ERROR:"+e.getMessage());
        }

    }
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
        System.out.println("WebSocketHandler | handleBinaryMessage Received " );
        //session.sendMessage(message);
        //Send to all the connected clients
        Iterator<WebSocketSession> iter = sessions.iterator();
        while( iter.hasNext() ){
            iter.next().sendMessage(message);
        }
    }

    public void sendToAll(String message)  {
        try{
            //CommUtils.outblue("WebSocketHandler | sendToAll String: " + message);
            //JSONObject jsm = new JSONObject(message);
            //IApplMessage mm = new ApplMessage(message);
            //String mstr    = mm.msgContent();//.replace("'","");
            sendToAll( new TextMessage(message)) ;
        }catch( Exception e ){
            System.out.println("WebSocketHandler | sendToAll String ERROR:"+e.getMessage());
        }
    }
    public void sendToAll(TextMessage message) {
        //CommUtils.outblue("WebSocketHandler | sendToAll " + message.getPayload() + " TextMessage sessions:" + sessions.size());
        Iterator<WebSocketSession> iter = sessions.iterator();
        //CommUtils.outyellow("WebSocketHandler | sendToAll TextMessage " );
        while( iter.hasNext() ){
            try{
                WebSocketSession session = iter.next();
                // + message.getPayload() + " for session " + session.getRemoteAddress() );
                synchronized(session){ //evito scritture concorrenti
                    //CommUtils.delay(5000);
                    //CommUtils.outyellow("WebSocketHandler | sendToAll session " );
                    session.sendMessage(message);
                }
            }catch(Exception e){
                System.out.println("WebSocketHandler | TextMessage " + message + " ERROR:"+e.getMessage());
            }
        }
    }

}