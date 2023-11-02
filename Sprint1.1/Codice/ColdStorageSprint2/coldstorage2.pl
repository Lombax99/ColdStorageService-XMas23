%====================================================================================
% coldstorage2 description   
%====================================================================================
request( doJob, doJob(KG) ).
dispatch( updateWeight, updateWeight(P_EFF,P_PRO) ).
request( depositRequest, depositRequest(PESO) ).
request( depositRequestF, depositRequestF(PESO) ).
request( weightrequest, weightrequest(PESO) ).
request( checkmyticket, checkmyticket(TICKET) ).
request( loaddone, loaddone(PESO) ).
request( getweight, getweight(NO_PARAM) ).
dispatch( startToDoThings, startToDoThings(NO_PARAM) ).
%====================================================================================
context(ctxcoldstoragearea, "localhost",  "TCP", "8040").
 qactor( controller, ctxcoldstoragearea, "it.unibo.controller.Controller").
  qactor( coldroom, ctxcoldstoragearea, "it.unibo.coldroom.Coldroom").
  qactor( tickethandler, ctxcoldstoragearea, "it.unibo.tickethandler.Tickethandler").
  qactor( facade, ctxcoldstoragearea, "it.unibo.facade.Facade").
  qactor( serviceaccessgui, ctxcoldstoragearea, "it.unibo.serviceaccessgui.Serviceaccessgui").
