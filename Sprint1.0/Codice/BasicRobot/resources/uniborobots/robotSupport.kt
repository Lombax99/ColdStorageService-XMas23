package uniborobots
/*
 -------------------------------------------------------------------------------------------------
 */

import it.unibo.kactor.ActorBasic
import kotlinx.coroutines.delay
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import robotVirtual.VrobotHLMovesActors23
import unibo.basicomm23.utils.CommSystemConfig
import unibo.basicomm23.utils.CommUtils
import java.io.File


object robotSupport{
	lateinit var robotKind  :  String
	lateinit var vr : VrobotHLMovesActors23
	var endPipehandler      :  ActorBasic? = null 
	var jsonParser = JSONParser()

	fun readStepTime(   ) : String{
 		val config = File("stepTimeConfig.json").readText(Charsets.UTF_8)
		val jsonObject   = jsonParser.parse( config ) as JSONObject
		return jsonObject.get("step").toString()
	}
	
	fun create( owner: ActorBasic, configFileName: String, endPipe: ActorBasic? = null ){
		endPipehandler   =  endPipe
 		val config = File("${configFileName}").readText(Charsets.UTF_8)
		 CommUtils.outred("${configFileName}   $config" )
		val jsonObject   = jsonParser.parse( config ) as JSONObject
		val hostAddr     = jsonObject.get("iprobot").toString()
		robotKind        = jsonObject.get("type").toString()
		val robotPort    = jsonObject.get("port").toString()
		CommSystemConfig.tracing = jsonObject.get("commtrace").toString() == "true"
		println( "		--- robotSupport | CREATING for $robotKind host=$hostAddr port=$robotPort owner=$owner" )

		when( robotKind ){
			//"mockrobot"  ->  { robotMock.mockrobotSupport.create(  ) }
			"virtual"    ->  {
				//robotVirtual.virtualrobotSupport23.create( owner, hostAddr, robotPort)
				vr = VrobotHLMovesActors23( hostAddr,  owner )
				//vr.setTrace(true)
			}

  			"realnano"   ->  {
				robotNano.nanoSupport.create( owner )
 				val realsonar = robotNano.sonarHCSR04SupportActor("realsonar")
				//Context injection  
				owner.context!!.addInternalActor(realsonar)  
  				println("		--- realnano robotSupport | has created the realsonar")
			}
			/*
			"realmbot" -> {
				robotMbot.mbotSupport.create(owner, robotPort)
				/*
				val realsonar = robotDataSourceArduino("realsonar", owner, conn)
				//Context injection
				owner.context!!.addInternalActor(realsonar)  
  				println("		--- realmbot robotSupport | has created the realsonar")*/


			} */
 			else -> println( "		--- robotSupport | robot $robotKind unknown" )
 		}
	}
	
	fun subscribe( obj : ActorBasic ) : ActorBasic {
		if( endPipehandler != null ) endPipehandler!!.subscribe( obj )
		return obj
	}
	 


	fun move( cmd : String ){ //cmd = w | a | s | d | h
 		//CommUtils.outred("robotSupport move cmd=$cmd robotKind=$robotKind / $vr"  )
		when( robotKind ){
			//"mockrobot"  -> { robotMock.mockrobotSupport.move( cmd ) 					  }
			"virtual"    -> { vr.move(  cmd ) 	  }
  			"realnano"   -> { robotNano.nanoSupport.move( cmd)	}
            /*"realmbot"   -> { robotMbot.mbotSupport.move( cmd )	}*/
			else         -> println( "		--- robotSupport: move| robot unknown")
		}		
	}

	fun dostep( time : Long, synch : Boolean = true ) : Boolean{ //cmd = w | a | s | d | h
		//println("robotSupport move cmd=$cmd robotKind=$robotKind" )
		when( robotKind ){
			"virtual"    -> {  return vr.step(  time  ) 	  } //synch
			"realnano"   -> {
				robotNano.nanoSupport.move( "w" )
				Thread.sleep(time)
				robotNano.nanoSupport.move( "h" )
				return true
			}   //TODO
			/*"realmbot"   -> { return false 	}	//TODO*/
			else         -> {
				println("		--- robotSupport: dostep | robot unknown")
				return false
			}
		}
	}
	fun terminate(){
		when( robotKind ){
			"mockrobot"  -> {  					                  }
			"virtual"    -> { /* robotVirtual.virtualrobotSupport23.terminate(  ) */	  }
 			"realmbot"   -> { /* mbotSupport.terminate(  ) */	}
 			"realnano"   -> { robotNano.nanoSupport.terminate( )	}
			else         -> println( "		--- robotSupport | robot unknown")
		}		
		
	}
	
	fun createSonarPipe(robotsonar: ActorBasic?){
 		if( robotsonar != null ){ 
			//runBlocking{
				//ACTIVATE THE DATA SOURCE  
				//MsgUtil.sendMsg("robotSupport", "sonarstart", "sonarstart(do)", robotsonar)
		 		//SET THE PIPE
		 		/*
		 		robotsonar.
		 			subscribeLocalActor("datacleaner").
		 			subscribeLocalActor("distancefilter").
		 			subscribeLocalActor("basicrobot")		//in order to perceive obstacle

		 		 */
			//}
			println("robotSupport | SONAR PIPE DONE NO runBlocking")
		}else{
	 		println("robotSupport | WARNING: sonar NOT FOUND")
	 	}		
	}
}