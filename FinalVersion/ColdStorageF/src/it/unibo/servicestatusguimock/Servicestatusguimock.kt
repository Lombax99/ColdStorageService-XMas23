/* Generated by AN DISI Unibo */ 
package it.unibo.servicestatusguimock

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
class Servicestatusguimock ( name: String, scope: CoroutineScope, isconfined: Boolean=false  ) : ActorBasicFsm( name, scope, confined=isconfined ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
				return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CommUtils.outcyan("$name in ${currentState.stateName} | $currentMsg | ${Thread.currentThread().getName()} n=${Thread.activeCount()}")
						 	   
						CommUtils.outyellow("SAG - in attesa")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t062",targetState="work",cond=whenDispatch("startToDoThings"))
				}	 
				state("work") { //this:State
					action { //it:State
						request("getweight", "getweight(1)" ,"facade" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t063",targetState="getrobotstate",cond=whenReply("currentweight"))
				}	 
				state("getrobotstate") { //this:State
					action { //it:State
						request("getrobotstate", "robotstate(1)" ,"facade" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t064",targetState="getrejectedtickets",cond=whenReply("robotstate"))
				}	 
				state("getrejectedtickets") { //this:State
					action { //it:State
						request("getrejectedtickets", "rejectedtickets(1)" ,"facade" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t065",targetState="s0",cond=whenReply("robotstate"))
				}	 
			}
		}
} 