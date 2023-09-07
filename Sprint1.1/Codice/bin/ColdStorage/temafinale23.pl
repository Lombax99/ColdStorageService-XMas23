%====================================================================================
% temafinale23 description   
%====================================================================================
context(ctxcoldstoragearea, "localhost",  "TCP", "8040").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
 qactor( transporttrolley, ctxbasicrobot, "external").
  qactor( controller, ctxcoldstoragearea, "it.unibo.controller.Controller").
  qactor( coldroom, ctxcoldstoragearea, "it.unibo.coldroom.Coldroom").
