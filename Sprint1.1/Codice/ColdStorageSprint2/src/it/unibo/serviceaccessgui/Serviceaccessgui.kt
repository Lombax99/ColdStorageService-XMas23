/* Generated by AN DISI Unibo */ 
package it.unibo.serviceaccessgui

import it.unibo.kactor.*
import alice.tuprolog.*
import unibo.basicomm23.*
import unibo.basicomm23.interfaces.*
import unibo.basicomm23.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Serviceaccessgui ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
			var PESO = 0
				var Ticket = ""
				var Ticketok = false
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
						CommUtils.outyellow("SAG - ricomincio")
						 PESO = Math.floor(Math.random() *(20 - 10 + 1) + 10).toInt()
						CommUtils.outyellow("SAG - richiedo $PESO")
						request("depositRequest", "depositRequest($PESO)" ,"tickethandler" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t08",targetState="gotoindoor",cond=whenReply("accept"))
					transition(edgeName="t09",targetState="tryagainlater",cond=whenReply("reject"))
				}	 
				state("tryagainlater") { //this:State
					action { //it:State
						CommUtils.outyellow("SAG - rifiutato")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
				 	 		stateTimer = TimerActor("timer_tryagainlater", 
				 	 					  scope, context!!, "local_tout_serviceaccessgui_tryagainlater", 5000.toLong() )
					}	 	 
					 transition(edgeName="wait10",targetState="work",cond=whenTimeout("local_tout_serviceaccessgui_tryagainlater"))   
				}	 
				state("gotoindoor") { //this:State
					action { //it:State
						CommUtils.outyellow("SAG - accettato")
						if( checkMsgContent( Term.createTerm("accept(TICKET)"), Term.createTerm("accept(TICKET)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
									Ticket = payloadArg(0)
								CommUtils.outyellow("SAG - $Ticket")
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
				 	 		stateTimer = TimerActor("timer_gotoindoor", 
				 	 					  scope, context!!, "local_tout_serviceaccessgui_gotoindoor", 1000.toLong() )
					}	 	 
					 transition(edgeName="t211",targetState="giveticket",cond=whenTimeout("local_tout_serviceaccessgui_gotoindoor"))   
				}	 
				state("giveticket") { //this:State
					action { //it:State
						CommUtils.outyellow("SAG - consegno il biglietto")
						request("checkmyticket", "checkmyticket($Ticket)" ,"tickethandler" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="tc12",targetState="checkresponse",cond=whenReply("ticketchecked"))
				}	 
				state("checkresponse") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("ticketchecked(BOOL)"), Term.createTerm("ticketchecked(BOOL)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Ticketok = payloadArg(0).toBoolean()
						}
						CommUtils.outyellow("SAG - biglietto accettato? : $Ticketok")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitchGuarded({ !Ticketok  
					}) )
					transition( edgeName="goto",targetState="unloading", cond=doswitchGuarded({! ( !Ticketok  
					) }) )
				}	 
				state("unloading") { //this:State
					action { //it:State
						CommUtils.outyellow("SAG - scarico")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
				 	 		stateTimer = TimerActor("timer_unloading", 
				 	 					  scope, context!!, "local_tout_serviceaccessgui_unloading", 3000.toLong() )
					}	 	 
					 transition(edgeName="t413",targetState="loaddone",cond=whenTimeout("local_tout_serviceaccessgui_unloading"))   
				}	 
				state("loaddone") { //this:State
					action { //it:State
						request("loaddone", "loaddone($PESO)" ,"controller" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t614",targetState="work",cond=whenReply("chargetaken"))
				}	 
			}
		}
}