package uniborobots;

import unibo.basicomm23.interfaces.Interaction2021;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.ws.WsConnection;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

//Da http://losviluppatore.it/i-websocket-comunicazione-asincrona-full-duplex-per-il-web/
//https://www.baeldung.com/java-websockets

/**
 * @ServerEndpoint da un nome all'end point
 * Questo può essere acceduto via ws://localhost:8080/uniborobots/echo
 * "localhost" è l'indirizzo dell'host dove è deployato il server ws,
 * "uniborobots" è il nome del package
 * ed "echo" è l'indirizzo specifico di questo endpoint
 */
@ServerEndpoint(value="/")
public class WsServer {

    public static void create( ){

        new Thread(){
            public void run(){
                new WsServer();
            }
        }.start();
    }
    public WsServer(){
        CommUtils.outblue("     +++ WsServer | CREATED");
    }
    /**
     * @OnOpen questo metodo ci permette di intercettare la creazione di una nuova sessione.
     * La classe session permette di inviare messaggi ai client connessi.
     * Nel metodo onOpen, faremo sapere all'utente che le operazioni di handskake
     * sono state completate con successo ed è quindi possibile iniziare le comunicazioni.
     */
    @OnOpen
    public void onOpen(Session session){
        CommUtils.outblue("     +++ WsServer | " + session.getId() + " ha aperto una connessione");
        try {
            session.getBasicRemote().sendText("Connessione Stabilita!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Quando un client invia un messaggio al server questo metodo intercetterà tale messaggio
     * e compierà le azioni di conseguenza. In questo caso l'azione è rimandare una eco del messaggi indietro.
     */
    @OnMessage
    public void onMessage(String message, Session session){
        CommUtils.outblue("     +++ WsServer | " + "Ricevuto messaggio da: " + session.getId() + ": " + message);
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Metodo che intercetta la chiusura di una connessine da parte di un client
     *
     * Nota: non si possono inviare messaggi al client da questo metodo
     */
    @OnClose
    public void onClose(Session session){
        CommUtils.outblue("     +++ WsServer | " + " Session " +session.getId()+" terminata");
    }


    public void doJob(){

    }

    public static void main(String[] args)  {
        WsServer.create();
        try {
            Interaction2021 connWs = WsConnection.create("localhost:8080/uniborobots/");
            connWs.forward("hello");
        }catch( Exception e ){
            CommUtils.outmagenta("ERROR " +e.getMessage());
        }
    }
}

