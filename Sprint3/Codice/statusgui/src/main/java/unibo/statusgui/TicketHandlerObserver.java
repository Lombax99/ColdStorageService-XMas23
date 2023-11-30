package unibo.statusgui;


import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.springframework.stereotype.Component;
import unibo.basicomm23.coap.CoapConnection;
import unibo.basicomm23.utils.CommUtils;

@Component
public class TicketHandlerObserver implements CoapHandler{
    String CSIPADDRESS = "127.0.0.1";
    int CSPORT = 8040;
    String ctxqakdest = "ctxcoldstoragearea";

    public TicketHandlerObserver(){
        System.out.println("thobserver started");

        CoapConnection tickethandlerconn = new CoapConnection(CSIPADDRESS+":"+CSPORT, ctxqakdest+"/tickethandler" );
        tickethandlerconn.observeResource( this );
    }


    @Override
    public void onLoad(CoapResponse response) {
        CommUtils.outcyan("TicketHandlerObserver changed! " + response.getResponseText() );
        WebSocketConfiguration.wshandler.sendToAll("th_" + response.getResponseText());
    }

    @Override
    public void onError() {
        System.out.println("TicketHandlerObserver observe error!");
    }
}