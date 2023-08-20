/* Generated by AN DISI Unibo */ 
package it.unibo.controller

import it.unibo.kactor.*
import alice.tuprolog.*
import unibo.basicomm23.*
import unibo.basicomm23.interfaces.*
import unibo.basicomm23.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Controller ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		 var KG = 0
				var P_DIC = 0
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CommUtils.outcyan("$name in ${currentState.stateName} | $currentMsg | ${Thread.currentThread().getName()} n=${Thread.activeCount()}")
						 	   
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("work") { //this:State
					action { //it:State
						CommUtils.outgreen("controller - in attesa")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t00",targetState="startjob",cond=whenRequest("loaddone"))
				}	 
				state("startjob") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("loaddone(P_EFF,P_DIC)"), Term.createTerm("loaddone(P_EFF,P_DIC)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 KG = payloadArg(0).toInt()
												P_DIC = payloadArg(1).toInt()
								CommUtils.outgreen("controller - dichiarato: $P_DIC, effettivo: $KG")
						}
						forward("updateWeight", "updateWeight($KG,$P_DIC)" ,"coldroom" ) 
						answer("loaddone", "chargetaken", "chargetaken(NO_PARAM)"   )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("jobdone") { //this:State
					action { //it:State
						forward("updateWeight", "updateWeight($KG,$P_DIC)" ,"coldroom" ) 
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
				 	 		stateTimer = TimerActor("timer_jobdone", 
				 	 					  scope, context!!, "local_tout_controller_jobdone", 15000.toLong() )
					}	 	 
					 transition(edgeName="repeat1",targetState="work",cond=whenTimeout("local_tout_controller_jobdone"))   
				}	 
				state("handlerobotdead") { //this:State
					action { //it:State
						CommUtils.outgreen("robotdead")
						CommUtils.outcyan("$name in ${currentState.stateName} | $currentMsg | ${Thread.currentThread().getName()} n=${Thread.activeCount()}")
						 	   
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
				}	 
			}
		}
}
