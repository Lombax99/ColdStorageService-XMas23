/* Generated by AN DISI Unibo */ 
package it.unibo.sonar

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
class Sonar ( name: String, scope: CoroutineScope, isconfined: Boolean=false  ) : ActorBasicFsm( name, scope, confined=isconfined ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		 	var Distanza = 20.0;
				
				return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CommUtils.outgreen("alarm - sonar started")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("work") { //this:State
					action { //it:State
						    
									while(SonarService.getDistance() > Distanza){}
						forward("stop", "stop(1)" ,"controller" ) 
						CommUtils.outgreen("alarm - sent stop")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="stopped", cond=doswitch() )
				}	 
				state("stopped") { //this:State
					action { //it:State
						    
									while(SonarService.getDistance() < Distanza){}
						forward("continue", "continue(1)" ,"controller" ) 
						CommUtils.outgreen("alarm - sent continue")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
				 	 		stateTimer = TimerActor("timer_stopped", 
				 	 					  scope, context!!, "local_tout_sonar_stopped", 3000.toLong() )
					}	 	 
					 transition(edgeName="t00",targetState="work",cond=whenTimeout("local_tout_sonar_stopped"))   
				}	 
			}
		}
} 
