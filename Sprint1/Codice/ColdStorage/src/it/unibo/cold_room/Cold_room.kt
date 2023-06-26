/* Generated by AN DISI Unibo */ 
package it.unibo.cold_room

import it.unibo.kactor.*
import alice.tuprolog.*
import unibo.basicomm23.*
import unibo.basicomm23.interfaces.*
import unibo.basicomm23.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Cold_room ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		
				var PesoCorrente = 0
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CommUtils.outcyan("$name in ${currentState.stateName} | $currentMsg | ${Thread.currentThread().getName()} n=${Thread.activeCount()}")
						 	   
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="update1",targetState="updateWeight",cond=whenDispatch("updateWeight"))
				}	 
				state("updateWeight") { //this:State
					action { //it:State
						CommUtils.outcyan("$name in ${currentState.stateName} | $currentMsg | ${Thread.currentThread().getName()} n=${Thread.activeCount()}")
						 	   
						if( checkMsgContent( Term.createTerm("updateWeight(PESO)"), Term.createTerm("updateWeight(PESO)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 PesoCorrente += payloadArg(0).toInt() 
						}
						CommUtils.outblack("peso aggiornato")
						CommUtils.outblack("nuovo peso: $PesoCorrente")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="update2",targetState="updateWeight",cond=whenDispatch("updateWeight"))
				}	 
			}
		}
}
