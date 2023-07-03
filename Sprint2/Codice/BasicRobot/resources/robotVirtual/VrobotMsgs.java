package robotVirtual;

public class VrobotMsgs {
    public final static String turnrightcmd  = "{\"robotmove\":\"turnRight\",\"time\":\"300\"}";
    public final static String turnRightMsg  = turnrightcmd;
    public final static String turnleftcmd  = "{\"robotmove\":\"turnLeft\",\"time\":\"300\"}";
    public final static String turnLeftMsg  =   turnleftcmd;
    public final static String forwardcmd   = "{\"robotmove\":\"moveForward\",\"time\":TIME}";
    public final static String backwardcmd  = "{\"robotmove\":\"moveBackward\",\"time\":TIME}";
    public final static String haltcmd      = "{\"robotmove\":\"alarm\",\"time\":\"10\"}";
    public final static String haltMsg      = haltcmd;
    public final static String stepcmd      = "{\"robotmove\":\"moveForward\",\"time\":\"350\"}";

    public final static String forwardlongcmd   = "{\"robotmove\":\"moveForward\"  , \"time\": \"2300\"}";
    public final static String backwardlongcmd  = "{\"robotmove\":\"moveBackward\" , \"time\": \"2300\"}";

    public final static String stoprobot    = "{\"robotmove\":\"stop\",\"time\": \"10\"}";
    //public final static String resumerobot  = "{\"robotmove\":\"resume\",\"time\": \"10\"}";
    //public final static String stoprobot    = "{'robotmove':'stop','time': '10'}";
    public final static String resumerobot  = "{\"robotmove\":\"resume\",\"time\": \"10\"}";


}
