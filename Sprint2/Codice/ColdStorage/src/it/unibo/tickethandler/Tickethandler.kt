/* Generated by AN DISI Unibo */ 
package it.unibo.tickethandler

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
class Tickethandler ( name: String, scope: CoroutineScope, isconfined: Boolean=false  ) : ActorBasicFsm( name, scope, confined=isconfined ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
			
				
				var TICKETTIME = DomainSystemConfig.getTicketTime();
				
		
				var Token = "_"
				var Ticket = ""
				var Peso = 0
				var Sequenza = 0
				var Accepted = false
				
				var Tickets = mutableSetOf<String>()
				return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CommUtils.outblue("tickethandler - ticketime: $TICKETTIME")
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
					 transition(edgeName="t015",targetState="checkforweight",cond=whenRequest("depositRequest"))
					transition(edgeName="t016",targetState="checktheticket",cond=whenRequest("checkmyticket"))
				}	 
				state("checkforweight") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("depositRequest(PESO)"), Term.createTerm("depositRequest(PESO)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Peso = payloadArg(0).toInt()  
								CommUtils.outblue("tickethandler - richiedo $Peso")
								request("weightrequest", "weightrequest($Peso)" ,"coldroom" )  
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t117",targetState="checkdeadlines",cond=whenReply("weightKO"))
					transition(edgeName="t118",targetState="returnticket",cond=whenReply("weightOK"))
				}	 
				state("checkdeadlines") { //this:State
					action { //it:State
						CommUtils.outblue("tickethandler - rifiutato, controllo i biglietti")
						 var SpazioLiberato = 0
									Accepted = false
									var Now = java.util.Date().getTime()/1000
									
									
									
									
									val it = Tickets.iterator()
						    		while (it.hasNext()) {
						    			var CurrentTicket = it.next()
						    			
						    			var TicketTokens = CurrentTicket.split(Token, ignoreCase = true, limit = 0)
						    			var StartTime = TicketTokens.get(1).toInt()
										
										
										if( Now > StartTime + TICKETTIME){ //scaduto
											var PesoTicket = TicketTokens.get(2).toInt()
											SpazioLiberato += PesoTicket
											
											it.remove()
										}
						        		
						    		}
						    		
						
						
						    
									
										
									if (SpazioLiberato >= Peso){ //c'è abbastanza spazio per la richiesta corrente
										SpazioLiberato -= Peso
										Accepted = true
										}
						CommUtils.outblack("tickethandler - Spazio Liberato: $SpazioLiberato")
						forward("updateWeight", "updateWeight(0,$SpazioLiberato)" ,"coldroom" ) 
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="returnticket", cond=doswitchGuarded({ Accepted  
					}) )
					transition( edgeName="goto",targetState="reject", cond=doswitchGuarded({! ( Accepted  
					) }) )
				}	 
				state("reject") { //this:State
					action { //it:State
						CommUtils.outblue("tickethandler - non c'è comunque posto, vai a casa")
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
									
									Ticket = Ticket.plus( Now ).plus(Token).plus( Peso ).plus(Token).plus( Sequenza)
									Sequenza++
									
									Tickets.add(Ticket)
						CommUtils.outblue("tickethandler - accettato")
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
												
												if(Tickets.contains(Ticket)){
													var StartTime = Ticket.split(Token, ignoreCase = true, limit = 0).get(1).toInt()
													
													var Now = java.util.Date().getTime()/1000
													if( Now < StartTime + TICKETTIME){
														Tickets.remove(Ticket)
														Ticketvalid = true
													}
														
												}
												
								CommUtils.outblue("tickethandler - il biglietto è valido? $Ticketvalid")
								answer("checkmyticket", "ticketchecked", "ticketchecked($Ticketvalid)"   )  
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
