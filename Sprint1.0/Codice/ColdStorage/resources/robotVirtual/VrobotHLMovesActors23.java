package robotVirtual;


import it.unibo.kactor.ActorBasic;
import it.unibo.kactor.MsgUtil;
import org.json.simple.JSONObject;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.ApplAbstractObserver;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.ws.WsConnection;

public class VrobotHLMovesActors23 extends ApplAbstractObserver implements IVrobotMovesAsynch {
    protected Interaction wsCommSupport;
    protected int elapsed       = 0;     //modified by update
    protected String moveResult = null;  //for observer part
    protected int threadCount = 1;
    protected ActorBasic owner;
    protected String toApplMsg   ;
    protected boolean tracing   = false;
    protected boolean doingStep = false;
    
    public VrobotHLMovesActors23(String vitualRobotIp, ActorBasic owner) {
        this.wsCommSupport = WsConnection.create(vitualRobotIp+":8091");
        this.owner          = owner;
        ((WsConnection) wsCommSupport).addObserver(this);
        toApplMsg = "msg(wenvinfo, dispatch, support, RECEIVER, CONTENT, 0)"
                .replace("RECEIVER",owner.getName());

        CommUtils.aboutThreads("     VrobotHLMovesActors23 |");
        CommUtils.outyellow(
                "     VrobotHLMovesActors23 | CREATED in " + Thread.currentThread().getName());
    }

    public void setTrace(boolean v){
        tracing = v;
    }
    public Interaction getConn() {
        return wsCommSupport;
    }


    @Override
    public void move( String cmd ) throws Exception{
        //CommUtils.outred("VrobotHLMovesActors23 move " + cmd);
        if( cmd.equals("w") ) forward( 5000 );
        else if( cmd.equals("s") ) backward( 5000 );
        else if( cmd.equals("a") || cmd.equals("l")) turnLeft(  );
        else if( cmd.equals("d") || cmd.equals("r")) turnRight(  );
        else if( cmd.equals("h")  ) halt(  );
        else if( cmd.equals("p") ) stepAsynch( 350 ); //TODO from file
        //else if( cmd.equals("p") ) step( 350 );
    }

    @Override
    public void turnLeft() throws Exception {
        //CommUtils.outred("turnLeft");
        sendSynchToWenv(VrobotMsgs.turnleftcmd);
    }

    @Override
    public void turnRight() throws Exception {
        sendSynchToWenv(VrobotMsgs.turnrightcmd);
    }

    @Override
    public void forward(int time) throws Exception {
        startTimer();
        wsCommSupport.forward(VrobotMsgs.forwardcmd.replace("TIME", "" + time));
    }

    @Override
    public void backward(int time) throws Exception {
        startTimer();
        wsCommSupport.forward(VrobotMsgs.backwardcmd.replace("TIME", "" + time));
    }

    @Override
    public void halt() throws Exception {
        //CommUtils.outgreen("     VrobotHLMovesActors23 | halt");
        wsCommSupport.forward(VrobotMsgs.haltcmd);
        CommUtils.delay(50); //wait for halt completion since halt on ws does not send answer
        //CommUtils.outgreen("     VrobotHLMovesActors23 | halt done " + moveResult );
    }
// Observer part

    protected String sendSynchToWenv(String msg) throws Exception {
        moveResult = null;
        //Invio fire-and.forget e attendo modifica di  moveResult da update
        startTimer();
        //CommUtils.outgreen("     VrobotHLMovesActors23 | sendSynchToWenv " + msg);
        wsCommSupport.forward(msg);
        return waitForResult();
    }
    protected String waitForResult() throws Exception {
        synchronized (this) {
            while (moveResult == null) {
                wait();
            }
            if( tracing ) CommUtils.outblack("     VrobotHLMovesActors23 | sendSynchToWenv RESUMES moveResult=" + moveResult);
            return moveResult;
        }
    }
    protected void activateWaiting(String endmove){
        synchronized (this) {  //sblocca request sincrona per checkRobotAtHome
            //CommUtils.outmagenta("activateWaiting ... ");
            moveResult = endmove;
            notifyAll();
        }
    }
 
    @Override
    public void update(String info) {
         try {
            elapsed = getDuration();
            //if( tracing )
                /*
                CommUtils.outcyan(
                    "     VrobotHLMovesActors23 | update:" + info
                            + " elapsed=" + elapsed + " doingStep=" + doingStep
                            + " " + Thread.currentThread().getName());
                */
             JSONObject jsonObj = CommUtils.parseForJson(info);
            if (jsonObj == null) {
                CommUtils.outred("     VrobotHLMovesActors23 | update ERROR Json:" + info);
                return;
            }
            if (info.contains("_notallowed")) {
                CommUtils.outred("     VrobotHLMovesActors23 | update WARNING!!! _notallowed unexpected in " + info);
                halt();
                return;
            }
            if (jsonObj.get("sonarName") != null) {
                long d = (long) jsonObj.get("distance") ;
                IApplMessage sonarEvent = CommUtils.buildEvent(
                        "vrhlsprt","sonardata","'"+"sonar(" +d + " )"+"'");
                //Imviare un msg ad owner perchè generi un evento a favore di sonarobs/engager
                MsgUtil.emitLocalEvent(sonarEvent,owner,null);  //percepito da sonarobs/engager
                return;
            }
            if (jsonObj.get("collision") != null) {
            /*
                IApplMessage collisionEvent = CommUtils.buildEvent(
                        "vrhlsprt","obstacle","obstacle(unknown)" );
                MsgUtil.emitLocalEvent(collisionEvent,owner,null);
            */
                return;
            }
            if (jsonObj.get("endmove") != null) {
                //{"endmove":"true/false ","move":"..."}
                boolean endmove = jsonObj.get("endmove").toString().contains("true");
                String move     = jsonObj.get("move").toString();
                //CommUtils.outred("     VrobotHLMovesActors23 | update move=" + move);
                //move moveForward-collision or moveBackward-collision
                if (endmove) {
                    if( ( move.equals("turnLeft") || move.equals("turnRight")) ){
                        activateWaiting("" + endmove);
                        return;
                    }
                    String wenvInfo = toApplMsg.replace("wenvinfo","stepdone") //"stepdone"
                            .replace("CONTENT", "stepdone(" + elapsed + ")");
                    IApplMessage msg = new ApplMessage(wenvInfo);
                    if( ! doingStep ) {
                        //TODO Actor23Utils.sendMsg(msg,owner);
                        MsgUtil.sendMsg(msg,owner,null); //continuation
                    }else{
                        activateWaiting("" + endmove);
                    }
                    return;
                } else if (move.contains("interrupted")) {/*
                    String wenvInfo = toApplMsg.replace("wenvinfo","stepfailed") //stepfailed
                            .replace("CONTENT", "stepfailed(" + elapsed + ", interrupt )");
                    IApplMessage msg = new ApplMessage(wenvInfo);
                    //TODO Actor23Utils.sendMsg(msg, owner);
                    MsgUtil.sendMsg(msg,owner,null);
                    */
                    return;
                 }else if (move.contains("collision")) {
                    String wenvInfo = toApplMsg
                            .replace("wenvinfo", "stepfailed") //stepfailed
                            .replace("CONTENT","stepfailed(" + elapsed + ", collision )");
                    IApplMessage msg = new ApplMessage(wenvInfo);
                    if( ! doingStep ) {
                        //TODO Actor23Utils.sendMsg(msg, owner);
                        MsgUtil.sendMsg(msg, owner, null); //completion }
                    }
                    activateWaiting("false"  );
                    //CommUtils.outred("     VrobotHLMovesActors23 | update END move=" + move);
                }
                return;
            }
        } catch (Exception e) {
            CommUtils.outred("     VrobotHLMovesActors23 | update ERROR:" + e.getMessage());
        }
    }


    //  Timer part
    private Long timeStart = 0L;

    public void startTimer() {
        elapsed = 0;
        timeStart = System.currentTimeMillis();
    }

    public int getDuration() {
        long duration = (System.currentTimeMillis() - timeStart);
        return (int) duration;
    }



    @Override
    public boolean step(long time) throws Exception {
        doingStep = true;
        //if( tracing )
            //CommUtils.outgreen("     VrobotHLMovesActors23 | step time=" + time);
        String cmd    = VrobotMsgs.forwardcmd.replace("TIME", "" + time);
        String result = sendSynchToWenv(cmd);
        //if( tracing )
            //CommUtils.outgreen("     VrobotHLMovesActors23 | step result="+result);
        //result=true elapsed=... OPPURE collision elapsed=...
        doingStep = false;
        return result.contains("true");
    }

    @Override
    public void stepAsynch(int time) {
        try {
            startTimer(); //per getDuration()
            if( tracing ) CommUtils.outblack("     VrobotHLMovesActors23 | stepAsynch" );
            wsCommSupport.forward(VrobotMsgs.forwardcmd.replace("TIME", "" + time));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

