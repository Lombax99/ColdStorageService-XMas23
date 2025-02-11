/* Generated by AN DISI Unibo */ 
package it.unibo.basicrobot

import it.unibo.kactor.*
import alice.tuprolog.*
import unibo.basicomm23.*
import unibo.basicomm23.interfaces.*
import unibo.basicomm23.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Basicrobot ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "ss0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		  
		  var StepTime      = 0L
		  var StartTime     = 0L     
		  var Duration      = 0L   
		  var RobotType     = "" 
		  var CurrentMove   = "unkknown"
		  var StepSynchRes  = false
		return { //this:ActionBasciFsm
				state("ss0") { //this:State
					action { //it:State
						discardMessages = true
						delegate("doplan", "planexec") 
						delegate("getrobotstate", "robotpos") 
						delegate("setrobotstate", "robotpos") 
						delegate("moverobot", "robotpos") 
						delegate("setdirection", "robotpos") 
						uniborobots.robotSupport.create(myself ,"basicrobotConfig.json" )
						 RobotType = uniborobots.robotSupport.robotKind  
						delay(3000) 
						CommUtils.outmagenta("basicrobot | STARTING ... ")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("work") { //this:State
					action { //it:State
						updateResourceRep( "basicrobot(started)"  
						)
						discardMessages = false
						CommUtils.outmagenta("basicrobot  | waiting ")
						forward("robotready", "robotready(TRUE)" ,"transporttrolley" ) 
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t06",targetState="doStep",cond=whenRequest("step"))
					transition(edgeName="t07",targetState="execcmd",cond=whenDispatch("cmd"))
					transition(edgeName="t08",targetState="endwork",cond=whenDispatch("end"))
				}	 
				state("execcmd") { //this:State
					action { //it:State
						CommUtils.outcyan("$name in ${currentState.stateName} | $currentMsg | ${Thread.currentThread().getName()} n=${Thread.activeCount()}")
						 	   
						if( checkMsgContent( Term.createTerm("cmd(MOVE)"), Term.createTerm("cmd(MOVE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 CurrentMove = payloadArg(0)  
								uniborobots.robotSupport.move( payloadArg(0)  )
								updateResourceRep( "moveactivated(${payloadArg(0)})"  
								)
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("doStep") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("step(TIME)"), Term.createTerm("step(T)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
									StepTime     = payloadArg(0).toLong()  	
								updateResourceRep( "step(${StepTime})"  
								)
						}
						StartTime = getCurrentTime()
						 StepSynchRes = uniborobots.robotSupport.dostep( StepTime )
							    	 
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="stepok", cond=doswitchGuarded({ StepSynchRes  
					}) )
					transition( edgeName="goto",targetState="stepKo", cond=doswitchGuarded({! ( StepSynchRes  
					) }) )
				}	 
				state("stepok") { //this:State
					action { //it:State
						 StepSynchRes = false  
						uniborobots.robotSupport.move( "h"  )
						updateResourceRep( "stepDone($StepTime)"  
						)
						answer("step", "stepdone", "stepdone($StepTime)"   )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("stepKo") { //this:State
					action { //it:State
						Duration = getDuration(StartTime)
						uniborobots.robotSupport.move( "h"  )
						 var TunedDuration   = StepTime - ((Duration * 0.80)).toLong()    
						if(  TunedDuration > 30  
						 ){uniborobots.robotSupport.move( "s"  )
						delay(TunedDuration)
						uniborobots.robotSupport.move( "h"  )
						updateResourceRep( "stepFail($Duration)"  
						)
						delay(300) 
						}
						answer("step", "stepfailed", "stepfailed($Duration,obst)"   )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("endwork") { //this:State
					action { //it:State
						updateResourceRep( "basicrobot(end)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
				}	 
			}
		}
}
