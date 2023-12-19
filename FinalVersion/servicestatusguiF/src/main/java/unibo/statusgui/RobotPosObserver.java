package unibo.statusgui;


import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.springframework.stereotype.Component;
import unibo.basicomm23.coap.CoapConnection;
import unibo.basicomm23.utils.CommUtils;

@Component
public class RobotPosObserver implements CoapHandler{
    String CSIPADDRESS = "127.0.0.1";
    int CSPORT = 8040;
    String ctxqakdest = "ctxcoldstoragearea";

    public RobotPosObserver(){
        System.out.println("rpobserver started");

        CoapConnection robotposconn = new CoapConnection(CSIPADDRESS+":"+CSPORT, ctxqakdest+"/robotpos" );
        robotposconn.observeResource( this );
    }


    @Override
    public void onLoad(CoapResponse response) {
        CommUtils.outcyan("RobotPosObserver changed! " + response.getResponseText() );
        WebSocketConfiguration.wshandler.sendToAll("rp_" + response.getResponseText());
    }

    @Override
    public void onError() {
        System.out.println("RobotPosObserver observe error!");
    }
}