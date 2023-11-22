/* Generated by AN DISI Unibo */ 
package it.unibo.coldroom

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
class Coldroom ( name: String, scope: CoroutineScope, isconfined: Boolean=false  ) : ActorBasicFsm( name, scope, confined=isconfined ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		
				var PesoEffettivo = 0
				var PesoPromesso = 0 
				var MAXW = 50
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
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="update12",targetState="updateWeight",cond=whenDispatch("updateWeight"))
					transition(edgeName="update13",targetState="checkweight",cond=whenRequest("weightrequest"))
					transition(edgeName="update14",targetState="returnweight",cond=whenRequest("getweight"))
				}	 
				state("updateWeight") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("updateWeight(P_EFF,P_PRO)"), Term.createTerm("updateWeight(P_EFF,P_PRO)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 PesoEffettivo += payloadArg(0).toInt() 
												PesoPromesso -= payloadArg(1).toInt()
								updateResourceRep( "" + PesoEffettivo + "_" + PesoPromesso + "" 
								)
						}
						CommUtils.outgreen("coldroom update - peso promesso: $PesoPromesso, nuovo peso effettivo: $PesoEffettivo")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("checkweight") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("weightrequest(PESO)"), Term.createTerm("weightrequest(PESO)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 var PesoRichiesto = payloadArg(0).toInt() 
								CommUtils.outgreen("coldroom - richiesti: $PesoRichiesto, effettivo: $PesoEffettivo, promesso: $PesoPromesso")
								if(  PesoEffettivo + PesoPromesso + PesoRichiesto  <= MAXW  
								 ){ PesoPromesso += PesoRichiesto
								updateResourceRep( "" + PesoEffettivo + "_" + PesoPromesso + "" 
								)
								CommUtils.outgreen("coldroom - accettato, peso promesso: $PesoPromesso")
								answer("weightrequest", "weightOK", "weightOK(NO_PARAM)"   )  
								}
								else
								 {CommUtils.outgreen("coldroom - rifiutato")
								 answer("weightrequest", "weightKO", "weightKO(NO_PARAM)"   )  
								 }
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("returnweight") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("getweight(NO_PARAM)"), Term.createTerm("getweight(NO_PARAM)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								answer("getweight", "currentweight", "currentweight($PesoEffettivo,$PesoPromesso)"   )  
						}
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
