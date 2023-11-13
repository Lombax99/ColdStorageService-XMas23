%====================================================================================
% coldstorage2 description   
%====================================================================================
request( doJob, doJob(KG) ).
dispatch( updateWeight, updateWeight(P_EFF,P_PRO) ).
request( depositRequest, depositRequest(PESO) ).
request( depositRequestF, depositRequestF(PESO) ).
request( weightrequest, weightrequest(PESO) ).
request( checkmyticket, checkmyticket(TICKET) ).
request( checkmyticketF, checkmyticketF(TICKET) ).
request( loaddone, loaddone(PESO) ).
request( loaddoneF, loaddoneF(PESO) ).
request( getweight, getweight(NO_PARAM) ).
request( getweightF, getweightF(NO_PARAM) ).
dispatch( startToDoThings, startToDoThings(NO_PARAM) ).
dispatch( cmd, cmd(MOVE) ).
dispatch( end, end(ARG) ).
request( step, step(TIME) ).
request( doplan, doplan(PATH,STEPTIME) ).
dispatch( nextmove, nextmove(M) ).
dispatch( nomoremove, nomoremove(M) ).
dispatch( robotready, robotready(BOOL) ).
dispatch( setdirection, dir(D) ).
request( moverobot, moverobot(TARGETX,TARGETY) ).
request( getrobotstate, getrobotstate(ARG) ).
dispatch( setrobotstate, setpos(X,Y,D) ).
dispatch( arrivedhome, arrivedhome(NO_PARAM) ).
dispatch( moving, moving(NO_PARAM) ).
dispatch( stopped, stopped(NO_PARAM) ).
dispatch( stop, stop(NO_PARAM) ).
dispatch( continue, continue(NO_PARAM) ).
event( alarm, alarm(X) ).
%====================================================================================
context(ctxcoldstoragearea, "localhost",  "TCP", "8040").
context(ctxalarm, "127.0.0.1",  "TCP", "8300").
 qactor( led, ctxalarm, "external").
  qactor( sonar, ctxalarm, "external").
  qactor( controller, ctxcoldstoragearea, "it.unibo.controller.Controller").
  qactor( coldroom, ctxcoldstoragearea, "it.unibo.coldroom.Coldroom").
  qactor( tickethandler, ctxcoldstoragearea, "it.unibo.tickethandler.Tickethandler").
  qactor( facade, ctxcoldstoragearea, "it.unibo.facade.Facade").
  qactor( transporttrolley, ctxcoldstoragearea, "it.unibo.transporttrolley.Transporttrolley").
  qactor( basicrobot, ctxcoldstoragearea, "it.unibo.basicrobot.Basicrobot").
  qactor( planexec, ctxcoldstoragearea, "it.unibo.planexec.Planexec").
  qactor( robotpos, ctxcoldstoragearea, "it.unibo.robotpos.Robotpos").
