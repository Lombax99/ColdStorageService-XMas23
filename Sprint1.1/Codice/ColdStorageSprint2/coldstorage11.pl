%====================================================================================
% coldstorage11 description   
%====================================================================================
request( doJob, doJob(KG) ).
reply( jobdone, jobdone(NO_PARAM) ).  %%for doJob
reply( robotDead, robotDead(NO_PARAM) ).  %%for doJob
dispatch( updateWeight, updateWeight(P_EFF,P_PRO) ).
request( depositRequest, depositRequest(PESO) ).
reply( accept, accept(TICKET) ).  %%for depositRequest
reply( reject, reject(NO_PARAM) ).  %%for depositRequest
request( depositRequestF, depositRequestF(PESO) ).
reply( acceptF, acceptF(TICKET) ).  %%for depositRequestF
reply( rejectF, rejectF(NO_PARAM) ).  %%for depositRequestF
request( weightrequest, weightrequest(PESO) ).
reply( weightOK, weightOK(NO_PARAM) ).  %%for weightrequest
reply( weightKO, weightKO(NO_PARAM) ).  %%for weightrequest
request( checkmyticket, checkmyticket(TICKET) ).
reply( ticketchecked, ticketchecked(BOOL) ).  %%for checkmyticket
request( checkmyticketF, checkmyticketF(TICKET) ).
reply( ticketcheckedF, ticketcheckedF(BOOL) ).  %%for checkmyticketF
request( loaddone, loaddone(PESO) ).
reply( chargetaken, chargetaken(NO_PARAM) ).  %%for loaddone
request( loaddoneF, loaddoneF(PESO) ).
reply( chargetakenF, chargetakenF(NO_PARAM) ).  %%for loaddoneF
request( getweight, getweight(NO_PARAM) ).
reply( currentweight, currentweight(PESO_EFF,PESO_PRO) ).  %%for getweight
request( getweightF, getweightF(NO_PARAM) ).
reply( currentweightF, currentweightF(PESO_EFF,PESO_PRO) ).  %%for getweightF
dispatch( startToDoThings, startToDoThings(NO_PARAM) ).
%====================================================================================
context(ctxcoldstoragearea, "localhost",  "TCP", "8040").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
 qactor( transporttrolley, ctxbasicrobot, "external").
  qactor( controller, ctxcoldstoragearea, "it.unibo.controller.Controller").
  qactor( coldroom, ctxcoldstoragearea, "it.unibo.coldroom.Coldroom").
  qactor( tickethandler, ctxcoldstoragearea, "it.unibo.tickethandler.Tickethandler").
  qactor( facade, ctxcoldstoragearea, "it.unibo.facade.Facade").
  qactor( serviceaccessgui, ctxcoldstoragearea, "it.unibo.serviceaccessgui.Serviceaccessgui").
