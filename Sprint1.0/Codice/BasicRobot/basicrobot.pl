%====================================================================================
% basicrobot description   
%====================================================================================
context(ctxbasicrobot, "localhost",  "TCP", "8020").
context(ctxcoldstoragearea, "127.0.0.1",  "TCP", "8040").
 qactor( coldroom, ctxcoldstoragearea, "external").
  qactor( transporttrolley, ctxbasicrobot, "it.unibo.transporttrolley.Transporttrolley").
  qactor( basicrobot, ctxbasicrobot, "it.unibo.basicrobot.Basicrobot").
  qactor( planexec, ctxbasicrobot, "it.unibo.planexec.Planexec").
  qactor( robotpos, ctxbasicrobot, "it.unibo.robotpos.Robotpos").
