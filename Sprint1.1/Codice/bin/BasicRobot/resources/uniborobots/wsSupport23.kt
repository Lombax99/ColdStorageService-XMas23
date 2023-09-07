package uniborobots

import it.unibo.kactor.ActorBasic
import unibo.basicomm23.interfaces.Interaction2021
import unibo.basicomm23.utils.ApplAbstractObserver
import unibo.basicomm23.utils.CommUtils
import unibo.basicomm23.ws.WsConnection
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object wsSupport23 : ApplAbstractObserver() {
	private var port     = 0
	lateinit var owner      : ActorBasic
	lateinit var robotsonar	: ActorBasic
	private lateinit var hostName : String 	
	private lateinit var support21ws     : Interaction2021
    private val forwardlongtimeMsg  = "{\"robotmove\":\"moveForward\", \"time\": 1000}"
    private val backwardlongtimeMsg = "{\"robotmove\":\"moveBackward\", \"time\": 1000}"
	private val forwardStepMsg  = "{\"robotmove\":\"moveForward\", \"time\": TIME}"
	private var moveResult   = ""
	private var timeStart  = 0L
	private var elapsed = 0 //modified by update
 	//https://stackoverflow.com/questions/44589669/correctly-implementing-wait-and-notify-in-kotlin
	private val lock = ReentrantLock()
	private val condition = lock.newCondition()

	init{
		println("wsSupport23 | init ... ")
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
		//CommUtils.outgreen("wsSupport23 | request " + msg);
		support21ws.forward(msg)
	    lock.withLock{
			while (moveResult == "") {
				//CommUtils.outgreen("wsSupport23 | request wait ... "  );
				condition.await()   //like wait()
			}
			return moveResult
		}
	}



	@Throws(java.lang.Exception::class)
	fun stepsynch(time: Int): Boolean {
		val cmd: String = forwardStepMsg.replace("TIME", "" + time)
		val result      = request(cmd)   //Asynch
		CommUtils.outgreen("wsSupport23 | step result="+result);
		return result!!.contains("true")
	}

	fun create( owner: ActorBasic, hostNameStr: String, portStr: String, trace : Boolean = false  ){
 		this.owner   = owner	 
  		hostName     = hostNameStr
		port         = Integer.parseInt(portStr)
             try {
				support21ws   = WsConnection.create( "$hostNameStr:$portStr" )
				(support21ws as WsConnection).addObserver(this)
				 println("		--- wsSupport23 | support21ws CREATED")
			 }catch( e:Exception ){
                 println("		--- wsSupport23 | ERROR $e")
             }	
	}

	override fun update(info: String) {
		try {
			CommUtils.outmagenta("wsSupport23 | update info=$info elapsed=?")
			//val jsonObj: org.json.simple.JSONObject = CommUtils.parseForJson(info)

		} catch (e: java.lang.Exception) {
			CommUtils.outred("wsSupport23 | update ERROR:" + e.message)
		}
	}
}//wsSupport23

 







