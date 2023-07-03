package robotVirtual

import it.unibo.kactor.ActorBasic
import kotlinx.coroutines.runBlocking
import org.json.simple.parser.JSONParser
import unibo.basicomm23.http.HttpConnection
import unibo.basicomm23.interfaces.Interaction2021
import unibo.basicomm23.utils.ApplAbstractObserver
import unibo.basicomm23.utils.CommUtils
import unibo.basicomm23.utils.SystemTimer
import unibo.basicomm23.ws.WsConnection
import uniborobots.MsgRobotUtil
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


//A support for using the virtual robot that incorporates WsSupportObserver


object virtualrobotSupport23 : ApplAbstractObserver() {
	private var port     = 0
	lateinit var owner      : ActorBasic
	lateinit var robotsonar	: ActorBasic
	private lateinit var hostName : String 	
	private lateinit var support21http   : Interaction2021
	private lateinit var support21ws     : Interaction2021
    private val forwardlongtimeMsg  = "{\"robotmove\":\"moveForward\", \"time\": 1000}"
    private val backwardlongtimeMsg = "{\"robotmove\":\"moveBackward\", \"time\": 1000}"
	private val forwardStepMsg  = "{\"robotmove\":\"moveForward\", \"time\": TIME}"
	private var moveResult   = ""
	private var timeStart  = 0L
	private var elapsed = 0 //modified by update
	private var traceOn = false
	//https://stackoverflow.com/questions/44589669/correctly-implementing-wait-and-notify-in-kotlin
	private val lock = ReentrantLock()
	private val condition  = lock.newCondition()
	private	val jsonparser = JSONParser()
	private val timer      = SystemTimer()
	private val timerHttp  = SystemTimer()

	init{
		println("virtualrobotSupport23 | init ... ")
	}

	fun startTimer() {
		elapsed = 0
		timeStart = System.currentTimeMillis()
	}

//MOVES
	@Throws(java.lang.Exception::class)
	fun request(msg: String?): String? {
		moveResult = ""
		//Invio fire-and.forget e attendo modifica di  moveResult da update
		startTimer()
		//CommUtils.outgreen("virtualrobotSupport23 | request " + msg);
		support21ws.forward(msg)
	    lock.withLock{
			while (moveResult == "") {
				//CommUtils.outgreen("virtualrobotSupport23 | request wait ... "  );
				condition.await()   //like wait()
			}
			return moveResult
		}
	}
/*
	@Throws(java.lang.Exception::class)
	fun turnLeft() {
		//request(MsgRobotUtil.turnLeftMsg)
		support21http.forward(MsgRobotUtil.turnLeftMsg)
	}
	@Throws(java.lang.Exception::class)
	fun turnRight() {
		//request(MsgRobotUtil.turnRightMsg)
		support21http.forward(MsgRobotUtil.turnRightMsg)
	}
*/
	@Throws(java.lang.Exception::class)
	fun stepsynch(time: Int): Boolean {
		val cmd: String = forwardStepMsg.replace("TIME", "" + time)
		val result      = request(cmd)   //Asynch
		CommUtils.outgreen("virtualrobotSupport23 | step result="+result);
		return result!!.contains("true")
	}

	fun create( owner: ActorBasic, hostNameStr: String, portStr: String, trace : Boolean = false  ){
 		this.owner   = owner	 
 		this.traceOn = trace
		hostName     = hostNameStr
		port         = Integer.parseInt(portStr)
             try {
            	//support21http    = HttpConnection.create( "$hostNameStr:$portStr" ) ///api/move built-in
                support21http    = HttpConnection.create( "$hostNameStr:$portStr/api/move" )
				support21ws      = WsConnection.create( "$hostNameStr:8091" )
				(support21ws as WsConnection).addObserver(this)
			 }catch( e:Exception ){
                 println("		--- virtualrobotSupport23 | ERROR $e")
             }	
	}
	
	fun trace( msg: String ){
		if( traceOn )  println("		--- virtualrobotSupport23 trace | $msg")
	}

    fun move(cmd: String) {	//cmd is written in application-language
		println("		--- virtualrobotSupport23 |  moveee  $cmd ")
		val msg = translate( cmd )
		//trace("move  $msg")
		if( cmd == "w" || cmd == "s" || cmd == "h" ){  //doing aysnch
			timer.startTime()
			support21ws.forward(msg)	//aysnch => no immediate answer
			if( cmd == "h" ) CommUtils.delay(50)  //attendo la fine dell'halt
			return
		}
		/*
		if( cmd == "q"){
			//support21ws.
			return
		}*/
		//Comunicazione sincrona con il VirtualRobot via HTTP
		CommUtils.outred("		--- virtualrobotSupport23 |  moveee  $cmd ")
		//timerHttp.startTime()
		timer.startTime()
		 val answer = support21http.request(msg) //,"$hostName:$port/api/move"
		//timerHttp.stopTime()
		CommUtils.outred("		--- virtualrobotSupport23 | http answer=${answer}")
		/*//REMEMBER: answer={"endmove":"true","move":"alarm"} alarm means halt
		var ajson = jsonparser.parse(answer) as JSONObject
		if( ajson.containsKey("endmove") && ajson.get("endmove")=="false"){
			owner.scope.launch{  owner.emit("obstacle","obstacle(virtual)") }
		}*/
    } 
    //translates application-language in cril
    fun translate(cmd: String) : String{ //cmd is written in application-language
		var jsonMsg = MsgRobotUtil.haltMsg //"{ 'type': 'alarm', 'arg': -1 }"
			when( cmd ){
				"msg(w)", "w" -> jsonMsg = forwardlongtimeMsg  		
				"msg(s)", "s" -> jsonMsg = backwardlongtimeMsg  
				"msg(a)", "a" -> jsonMsg = MsgRobotUtil.turnLeftMsg
				"msg(d)", "d" -> jsonMsg = MsgRobotUtil.turnRightMsg
				"msg(l)", "l" -> jsonMsg = MsgRobotUtil.turnLeftMsg
				"msg(r)", "r" -> jsonMsg = MsgRobotUtil.turnRightMsg
				//"msg(z)", "z" -> not implemented
				"msg(q)", "q" -> jsonMsg = forwardlongtimeMsg
				"msg(h)", "h" -> jsonMsg = MsgRobotUtil.haltMsg //"{ 'type': 'alarm','arg': 100 }"
				else -> println("		--- virtualrobotSupport23 | command $cmd unknown")
			}
 			return jsonMsg
		}
	fun terminate(){
		robotsonar.terminate()
	}


	override fun update(info: String) {
		try {
			timer.stopTime()
			elapsed = timer.duration.toInt()
			CommUtils.outmagenta("virtualrobotSupport23 | update info=$info elapsed=$elapsed")
			val jsonObj: org.json.simple.JSONObject = CommUtils.parseForJson(info)
			if (jsonObj == null) {
				CommUtils.outred("virtualrobotSupport23 | update ERROR Json:$info")
				return
			}
			if (info.contains("notallowed")) {
				CommUtils.outred("virtualrobotSupport23 | update WARNING!!! _notallowed unexpected in $info")
				return
			}
			if (jsonObj["collision"] != null) {
				//Alla collision non faccio nulla: attendo moveForward-collision
				var move = jsonObj.get("collision")
				CommUtils.outmagenta("WsSupportObserver move=$move" );
				runBlocking {
					var target = "unknown";
					CommUtils.outgreen("WsSupportObserver emits:obstacle($target)}" );
					owner!!.emit("obstacle","obstacle($target)")
				}
				return
			}
			if (jsonObj["endmove"] != null) {
				//{"endmove":"true/false ","move":"..."}
				val endmove = jsonObj["endmove"].toString().contains("true")
				val move    = jsonObj["move"].toString()
				if( move != "turnLeft" && move != "turnRight") {
					CommUtils.outred("virtualrobotSupport23 | update move=" + move);
					lock.withLock {
						moveResult = "" + endmove
						//notifyAll()
						condition.signalAll()
					}
				}
				return
			}
 
		} catch (e: java.lang.Exception) {
			CommUtils.outred("virtualrobotSupport23 | update ERROR:" + e.message)
		}
	}
}//virtualrobotSupport23

 







