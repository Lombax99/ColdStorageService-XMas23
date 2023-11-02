/* Generated by AN DISI Unibo */ 
package it.unibo.facade

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
class Facade ( name: String, scope: CoroutineScope, isconfined: Boolean=false  ) : ActorBasicFsm( name, scope, confined=isconfined ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		
				var Ticket = ""
				var Peso = 0
				
				return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CommUtils.outblue("ColdStorageFacade - in attesa")
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
					 transition(edgeName="t09",targetState="depositReqHandler",cond=whenRequest("depositRequestF"))
				}	 
				state("depositReqHandler") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("depositRequestF(PESO)"), Term.createTerm("depositRequestF(PESO)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Peso = payloadArg(0).toInt()  
								CommUtils.outblue("send request to TicketHandler")
								request("depositRequest", "depositRequest(PESO)" ,"tickethandler" )  
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t110",targetState="returnticket",cond=whenReply("accept"))
					transition(edgeName="t111",targetState="rejectticket",cond=whenReply("reject"))
				}	 
				state("rejectticket") { //this:State
					action { //it:State
						CommUtils.outblue("facade - ticket reject")
						answer("depositRequestF", "rejectF", "rejectF(reject)"   )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("returnticket") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("accept(TICKET)"), Term.createTerm("accept(TICKET)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Ticket = payloadArg(0).toString()  
						}
						CommUtils.outblue("facade - accettato")
						answer("depositRequestF", "acceptF", "acceptF($Ticket)"   )  
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
