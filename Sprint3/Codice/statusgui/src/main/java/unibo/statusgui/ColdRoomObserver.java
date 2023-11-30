package unibo.statusgui;


import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.springframework.stereotype.Component;
import unibo.basicomm23.coap.CoapConnection;
import unibo.basicomm23.utils.CommUtils;

@Component
public class ColdRoomObserver implements CoapHandler{
    String CSIPADDRESS = "127.0.0.1";
    int CSPORT = 8040;
    String ctxqakdest = "ctxcoldstoragearea";

    public ColdRoomObserver(){
        System.out.println("observer started");

        CoapConnection coldroomconn = new CoapConnection(CSIPADDRESS+":"+CSPORT, ctxqakdest+"/coldroom" );
        coldroomconn.observeResource( this );
    }


    @Override
    public void onLoad(CoapResponse response) {
        CommUtils.outcyan("ColdRoomObserver changed! " + response.getResponseText() );
        WebSocketConfiguration.wshandler.sendToAll("cr_" + response.getResponseText());
    }

    @Override
    public void onError() {
        System.out.println("ColdRoomObserver observe error!");
    }
}