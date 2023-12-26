package unibo.statusgui;


import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.springframework.stereotype.Component;
import unibo.basicomm23.coap.CoapConnection;
import unibo.basicomm23.utils.CommUtils;

@Component
public class ControllerObserver implements CoapHandler{
    String CSIPADDRESS = "127.0.0.1";
    int CSPORT = 8040;
    String ctxqakdest = "ctxcoldstoragearea";

    public ControllerObserver(){
        System.out.println("controller observer started");

        CoapConnection controllerconn = new CoapConnection(CSIPADDRESS+":"+CSPORT, ctxqakdest+"/controller" );
        controllerconn.observeResource( this );
    }


    @Override
    public void onLoad(CoapResponse response) {
        CommUtils.outcyan("ControllerObserver changed! " + response.getResponseText() );
        WebSocketConfiguration.wshandler.sendToAll("con_" + response.getResponseText());
    }

    @Override
    public void onError() {
        System.out.println("ControllerObserver observe error!");
    }
}