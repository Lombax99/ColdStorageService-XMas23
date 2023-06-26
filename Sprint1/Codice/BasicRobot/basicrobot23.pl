%====================================================================================
% basicrobot23 description   
%====================================================================================
context(ctxbasicrobot, "localhost",  "TCP", "8020").
 qactor( controller, ctxbasicrobot, "it.unibo.controller.Controller").
  qactor( mymain, ctxbasicrobot, "it.unibo.mymain.Mymain").
  qactor( basicrobot, ctxbasicrobot, "it.unibo.basicrobot.Basicrobot").
  qactor( planexec, ctxbasicrobot, "it.unibo.planexec.Planexec").
  qactor( robotpos, ctxbasicrobot, "it.unibo.robotpos.Robotpos").
