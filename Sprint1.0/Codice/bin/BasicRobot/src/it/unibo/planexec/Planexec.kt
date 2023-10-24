/* Generated by AN DISI Unibo */ 
package it.unibo.planexec

import it.unibo.kactor.*
import alice.tuprolog.*
import unibo.basicomm23.*
import unibo.basicomm23.interfaces.*
import unibo.basicomm23.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Planexec ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		  var Plan          = ""
				var PlanOrig      = ""
				var CurMoveTodo   = ""		
				var StepTime      = "315"
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
					 transition(edgeName="t09",targetState="execplan",cond=whenRequest("doplan"))
				}	 
				state("execplan") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("doplan(PATH,STEPTIME)"), Term.createTerm("doplan(PLAN,STEPTIME)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Plan       = payloadArg(0).replace("[","").replace("]","").replace(",","").replace(" ","")
											   PlanOrig   = Plan
											   StepTime   = payloadArg(1) 
								CommUtils.outblack("plan ${Plan}")
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
				 	 		stateTimer = TimerActor("timer_execplan", 
				 	 					  scope, context!!, "local_tout_planexec_execplan", 100.toLong() )
					}	 	 
					 transition(edgeName="t010",targetState="nextMove",cond=whenTimeout("local_tout_planexec_execplan"))   
					transition(edgeName="t011",targetState="planinterruptedalarm",cond=whenEvent("alarm"))
				}	 
				state("nextMove") { //this:State
					action { //it:State
						 
								   if( Plan.length > 0  ){
								   	CurMoveTodo =  Plan.elementAt(0).toString() 
								   	Plan        =  Plan.removePrefix(CurMoveTodo)
								   }else CurMoveTodo = ""		   
						forward("nextmove", "nextmove($CurMoveTodo)" ,"planexec" ) 
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t012",targetState="planinterruptedalarm",cond=whenEvent("alarm"))
					transition(edgeName="t013",targetState="doMove",cond=whenDispatch("nextmove"))
				}	 
				state("doMove") { //this:State
					action { //it:State
						if(  CurMoveTodo == ""  
						 ){forward("nomoremove", "nomoremove(end)" ,"planexec" ) 
						}
						else
						 {if(  CurMoveTodo == "w"  
						  ){delay(300) 
						 request("step", "step($StepTime)" ,"basicrobot" )  
						 }
						 else
						  {uniborobots.robotSupport.move( CurMoveTodo  )
						  forward("nextmove", "nextmove(goon)" ,"planexec" ) 
						  }
						 }
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t014",targetState="planinterruptedalarm",cond=whenEvent("alarm"))
					transition(edgeName="t015",targetState="planend",cond=whenDispatch("nomoremove"))
					transition(edgeName="t016",targetState="nextMove",cond=whenDispatch("nextmove"))
					transition(edgeName="t017",targetState="nextMove",cond=whenReply("stepdone"))
					transition(edgeName="t018",targetState="planinterruptedobstacle",cond=whenReply("stepfailed"))
				}	 
				state("planend") { //this:State
					action { //it:State
						if(  currentMsg.msgContent() == "alarm(disengaged)"  
						 ){}
						else
						 {if(  currentMsg.msgId() == "alarm"  
						  ){CommUtils.outblack("$name |  planend alarm $Plan $CurMoveTodo")
						  val Plantodo = CurMoveTodo + Plan  
						 answer("doplan", "doplanfailed", "doplanfailed($Plantodo)"   )  
						 }
						 else
						  {CommUtils.outblue("$name | planend ok plan=$PlanOrig ")
						  answer("doplan", "doplandone", "doplandone($PlanOrig)"   )  
						  updateResourceRep( "plandone($PlanOrig)"  
						  )
						  }
						 }
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("planinterruptedobstacle") { //this:State
					action { //it:State
						CommUtils.outmagenta("$name |  planinterruptedobstacle $CurMoveTodo ")
						 var Plantodo =  CurMoveTodo + Plan
						updateResourceRep( "planfailed($Plan,$Plantodo )"  
						)
						answer("doplan", "doplanfailed", "doplanfailed($Plantodo)"   )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("planinterruptedalarm") { //this:State
					action { //it:State
						CommUtils.outmagenta("$name |  planinterruptedalarm $CurMoveTodo ")
						 var Plantodo = CurMoveTodo + Plan
						updateResourceRep( "planfailed($PlanOrig,$Plantodo )"  
						)
						answer("doplan", "doplanfailed", "doplanfailed($Plantodo)"   )  
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
