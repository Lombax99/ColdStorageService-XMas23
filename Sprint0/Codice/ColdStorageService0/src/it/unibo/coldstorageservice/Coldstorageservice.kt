/* Generated by AN DISI Unibo */ 
package it.unibo.coldstorageservice

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
class Coldstorageservice ( name: String, scope: CoroutineScope, isconfined: Boolean=false  ) : ActorBasicFsm( name, scope, confined=isconfined ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
			
				
				var TICKETTIME = 10000;
				
				var Token = "_"
				var InitialToken = "T"
				var Ticket = ""
				var Sequenza = 0
				
				return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CommUtils.outblue("coldstorageservice - ticketime: $TICKETTIME")
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
					 transition(edgeName="t01",targetState="checkforweight",cond=whenRequest("depositRequest"))
					transition(edgeName="t02",targetState="checktheticket",cond=whenRequest("checkmyticket"))
					transition(edgeName="t03",targetState="loadchargetaken",cond=whenRequest("loaddone"))
				}	 
				state("checkforweight") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("depositRequest(PESO)"), Term.createTerm("depositRequest(PESO)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 var Peso = payloadArg(0).toInt()  
								CommUtils.outblue("coldstorageservice - richiedo $Peso")
								request("weightrequest", "weightrequest($Peso)" ,"coldroom" )  
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t14",targetState="reject",cond=whenReply("weightKO"))
					transition(edgeName="t15",targetState="returnticket",cond=whenReply("weightOK"))
				}	 
				state("reject") { //this:State
					action { //it:State
						CommUtils.outblue("coldstorageservice - non c'è comunque posto, vai a casa")
						answer("depositRequest", "reject", "reject(reject)"   )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("returnticket") { //this:State
					action { //it:State
						  Ticket = "T".plus(Token)
									
									var Now = java.util.Date().getTime()/1000
									
									Ticket = Ticket.plus( Now ).plus(Token).plus( Sequenza)
									Sequenza++
									
						CommUtils.outblue("coldstorageservice - accettato")
						answer("depositRequest", "accept", "accept($Ticket)"   )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("checktheticket") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("checkmyticket(TICKET)"), Term.createTerm("checkmyticket(TICKET)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
									var Ticket = payloadArg(0)
												var Ticketvalid = false;
												
												var StartTime = Ticket.split(Token, ignoreCase = true, limit = 0).get(1).toInt()
												
												var Now = java.util.Date().getTime()/1000
												if( Now < StartTime + TICKETTIME){
													Ticketvalid = true
												}
												
								CommUtils.outblue("coldstorageservice - il biglietto è valido? $Ticketvalid")
								answer("checkmyticket", "ticketchecked", "ticketchecked($Ticketvalid)"   )  
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("loadchargetaken") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("loaddone(PESO)"), Term.createTerm("loaddone(PESO)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 var Peso = payloadArg(0).toInt()
								CommUtils.outblue("coldstorageservice - chargetaken peso dichiarato: $Peso")
						}
						answer("loaddone", "chargetaken", "chargetaken(NO_PARAM)"   )  
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