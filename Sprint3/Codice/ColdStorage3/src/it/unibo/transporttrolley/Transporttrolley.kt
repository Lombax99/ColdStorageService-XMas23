/* Generated by AN DISI Unibo */ 
package it.unibo.transporttrolley

import it.unibo.kactor.*
import alice.tuprolog.*
import unibo.basicomm23.*
import unibo.basicomm23.interfaces.*
import unibo.basicomm23.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import it.unibo.kactor.sysUtil.createActor   //Sept2023
class Transporttrolley ( name: String, scope: CoroutineScope, isconfined: Boolean=false  ) : ActorBasicFsm( name, scope, confined=isconfined ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
			var Peso = 0
				var CapienzaRobot = 5
				var Giridafare = 0
				return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						forward("setrobotstate", "setpos(0,0,down)" ,"robotpos" ) 
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="ready20",targetState="work",cond=whenDispatch("robotready"))
				}	 
				state("work") { //this:State
					action { //it:State
						CommUtils.outgreen("robot ! waiting")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="startworking21",targetState="work",cond=whenReply("moverobotdone"))
					transition(edgeName="startworking22",targetState="startjob",cond=whenRequest("doJob"))
				}	 
				state("startjob") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("doJob(KG)"), Term.createTerm("doJob(KG)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Peso = payloadArg(0).toInt()
												Giridafare = Math.ceil(Peso.toDouble() / CapienzaRobot.toDouble()).toInt()
								CommUtils.outgreen("peso ricevuto: $Peso")
								CommUtils.outgreen("giri da fare: $Giridafare")
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="movingtoarrival", cond=doswitch() )
				}	 
				state("movingtoarrival") { //this:State
					action { //it:State
						request("moverobot", "moverobot(0,4)" ,"robotpos" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="gofetch23",targetState="movingtocoldroom",cond=whenReply("moverobotdone"))
				}	 
				state("movingtocoldroom") { //this:State
					action { //it:State
						request("moverobot", "moverobot(5,3)" ,"robotpos" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="godrop24",targetState="checkforjob",cond=whenReply("moverobotdone"))
				}	 
				state("checkforjob") { //this:State
					action { //it:State
							Giridafare -= 1
						CommUtils.outgreen("giro completato, mancano $Giridafare giri")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="waitforjob", cond=doswitchGuarded({	Giridafare <= 0   
					}) )
					transition( edgeName="goto",targetState="movingtoarrival", cond=doswitchGuarded({! (	Giridafare <= 0   
					) }) )
				}	 
				state("waitforjob") { //this:State
					action { //it:State
						answer("doJob", "jobdone", "jobdone(1)"   )  
						CommUtils.outgreen("transporttrolley ! aspetto")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
				 	 		stateTimer = TimerActor("timer_waitforjob", 
				 	 					  scope, context!!, "local_tout_transporttrolley_waitforjob", 3000.toLong() )
					}	 	 
					 transition(edgeName="gofetchagain25",targetState="goinghome",cond=whenTimeout("local_tout_transporttrolley_waitforjob"))   
					transition(edgeName="gofetchagain26",targetState="startjob",cond=whenRequest("doJob"))
				}	 
				state("goinghome") { //this:State
					action { //it:State
						CommUtils.outgreen("going home")
						request("moverobot", "moverobot(0,0)" ,"robotpos" )  
						forward("setdirection", "dir(down)" ,"robotpos" ) 
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
			}
		}
} 
