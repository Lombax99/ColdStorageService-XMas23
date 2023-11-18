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
import it.unibo.kactor.sysUtil.createActor   //Sept2023
class Controller ( name: String, scope: CoroutineScope, isconfined: Boolean=false  ) : ActorBasicFsm( name, scope, confined=isconfined ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		 var PESO = 0
				
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
						if( checkMsgContent( Term.createTerm("loaddone(PESO)"), Term.createTerm("loaddone(PESO)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 PESO = payloadArg(0).toInt()
								CommUtils.outgreen("controller - startjob dichiarato: $PESO")
						}
						answer("loaddone", "chargetaken", "chargetaken(NO_PARAM)"   )  
						forward("updateWeight", "updateWeight($PESO,$PESO)" ,"coldroom" ) 
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
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
