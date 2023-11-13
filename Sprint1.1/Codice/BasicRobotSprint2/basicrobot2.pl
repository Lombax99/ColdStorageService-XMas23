%====================================================================================
% basicrobot2 description   
%====================================================================================
request( doJob, doJob(KG) ).
dispatch( cmd, cmd(MOVE) ).
dispatch( end, end(ARG) ).
request( step, step(TIME) ).
request( doplan, doplan(PATH,STEPTIME) ).
event( alarm, alarm(X) ).
dispatch( nextmove, nextmove(M) ).
dispatch( nomoremove, nomoremove(M) ).
dispatch( robotready, robotready(BOOL) ).
dispatch( setdirection, dir(D) ).
request( moverobot, moverobot(TARGETX,TARGETY) ).
request( getrobotstate, getrobotstate(ARG) ).
dispatch( setrobotstate, setpos(X,Y,D) ).
%====================================================================================
context(ctxbasicrobot, "localhost",  "TCP", "8020").
context(ctxcoldstoragearea, "127.0.0.1",  "TCP", "8040").
 qactor( coldroom, ctxcoldstoragearea, "external").
  qactor( transporttrolley, ctxbasicrobot, "it.unibo.transporttrolley.Transporttrolley").
  qactor( basicrobot, ctxbasicrobot, "it.unibo.basicrobot.Basicrobot").
  qactor( planexec, ctxbasicrobot, "it.unibo.planexec.Planexec").
  qactor( robotpos, ctxbasicrobot, "it.unibo.robotpos.Robotpos").
