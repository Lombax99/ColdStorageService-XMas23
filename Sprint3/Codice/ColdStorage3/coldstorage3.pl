%====================================================================================
% coldstorage3 description   
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
request( getrejectedtickets, getrejectedtickets(NO_PARAMS) ).
reply( rejectedtickets, rejectedtickets(INT) ).  %%for getrejectedtickets
request( loaddone, loaddone(PESO) ).
reply( chargetaken, chargetaken(NO_PARAM) ).  %%for loaddone
request( loaddoneF, loaddoneF(PESO) ).
reply( chargetakenF, chargetakenF(NO_PARAM) ).  %%for loaddoneF
request( getweight, getweight(NO_PARAM) ).
reply( currentweight, currentweight(PESO_EFF,PESO_PRO) ).  %%for getweight
request( getweightF, getweightF(NO_PARAM) ).
reply( currentweightF, currentweightF(PESO_EFF,PESO_PRO) ).  %%for getweightF
dispatch( startToDoThings, startToDoThings(NO_PARAM) ).
dispatch( cmd, cmd(MOVE) ).
dispatch( end, end(ARG) ).
request( step, step(TIME) ).
reply( stepdone, stepdone(V) ).  %%for step
reply( stepfailed, stepfailed(DURATION,CAUSE) ).  %%for step
request( doplan, doplan(PATH,STEPTIME) ).
reply( doplandone, doplandone(ARG) ).  %%for doplan
reply( doplanfailed, doplanfailed(ARG) ).  %%for doplan
dispatch( nextmove, nextmove(M) ).
dispatch( nomoremove, nomoremove(M) ).
dispatch( robotready, robotready(BOOL) ).
dispatch( setdirection, dir(D) ).
request( moverobot, moverobot(TARGETX,TARGETY) ).
reply( moverobotdone, moverobotok(ARG) ).  %%for moverobot
reply( moverobotfailed, moverobotfailed(PLANDONE,PLANTODO) ).  %%for moverobot
request( getrobotstate, getrobotstate(ARG) ).
reply( robotstate, robotstate(POS,DIR) ).  %%for getrobotstate
dispatch( stopplan, stopplan(NO_PARAM) ).
dispatch( continueplan, continueplan(NO_PARAM) ).
dispatch( setrobotstate, setpos(X,Y,D) ).
dispatch( arrivedhome, arrivedhome(NO_PARAM) ).
dispatch( moving, moving(NO_PARAM) ).
dispatch( stopped, stopped(NO_PARAM) ).
dispatch( stop, stop(NO_PARAM) ).
dispatch( continue, continue(NO_PARAM) ).
event( alarm, alarm(X) ).
%====================================================================================
context(ctxcoldstoragearea, "localhost",  "TCP", "8040").
 qactor( controller, ctxcoldstoragearea, "it.unibo.controller.Controller").
  qactor( coldroom, ctxcoldstoragearea, "it.unibo.coldroom.Coldroom").
  qactor( tickethandler, ctxcoldstoragearea, "it.unibo.tickethandler.Tickethandler").
  qactor( facade, ctxcoldstoragearea, "it.unibo.facade.Facade").
  qactor( transporttrolley, ctxcoldstoragearea, "it.unibo.transporttrolley.Transporttrolley").
  qactor( planexec, ctxcoldstoragearea, "it.unibo.planexec.Planexec").
  qactor( robotpos, ctxcoldstoragearea, "it.unibo.robotpos.Robotpos").
  qactor( basicrobot, ctxcoldstoragearea, "it.unibo.basicrobot.Basicrobot").
