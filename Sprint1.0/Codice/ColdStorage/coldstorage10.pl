%====================================================================================
% coldstorage10 description   
%====================================================================================
dispatch( cmd, cmd(MOVE) ).
dispatch( end, end(ARG) ).
request( step, step(TIME) ).
reply( stepdone, stepdone(V) ).  %%for step
reply( stepfailed, stepfailed(DURATION,CAUSE) ).  %%for step
request( doplan, doplan(PATH,STEPTIME) ).
reply( doplandone, doplandone(ARG) ).  %%for doplan
reply( doplanfailed, doplanfailed(ARG) ).  %%for doplan
event( alarm, alarm(X) ).
dispatch( nextmove, nextmove(M) ).
dispatch( nomoremove, nomoremove(M) ).
dispatch( robotready, robotready(BOOL) ).
dispatch( setdirection, dir(D) ).
request( moverobot, moverobot(TARGETX,TARGETY) ).
reply( moverobotdone, moverobotok(ARG) ).  %%for moverobot
reply( moverobotfailed, moverobotfailed(PLANDONE,PLANTODO) ).  %%for moverobot
request( getrobotstate, getrobotstate(ARG) ).
reply( robotstate, robotstate(POS,DIR) ).  %%for getrobotstate
dispatch( setrobotstate, setpos(X,Y,D) ).
request( doJob, doJob(KG) ).
reply( jobdone, jobdone(NO_PARAM) ).  %%for doJob
reply( robotDead, robotDead(NO_PARAM) ).  %%for doJob
dispatch( updateWeight, updateWeight(PESO) ).
%====================================================================================
context(ctxcoldstoragearea, "localhost",  "TCP", "8040").
 qactor( controller, ctxcoldstoragearea, "it.unibo.controller.Controller").
  qactor( coldroom, ctxcoldstoragearea, "it.unibo.coldroom.Coldroom").
  qactor( transporttrolley, ctxcoldstoragearea, "it.unibo.transporttrolley.Transporttrolley").
  qactor( planexec, ctxcoldstoragearea, "it.unibo.planexec.Planexec").
  qactor( robotpos, ctxcoldstoragearea, "it.unibo.robotpos.Robotpos").
  qactor( basicrobot, ctxcoldstoragearea, "it.unibo.basicrobot.Basicrobot").
