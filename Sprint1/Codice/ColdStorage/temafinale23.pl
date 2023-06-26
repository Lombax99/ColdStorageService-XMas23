%====================================================================================
% temafinale23 description   
%====================================================================================
context(ctxcoldstoragearea, "localhost",  "TCP", "8040").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
 qactor( transporttrolley, ctxbasicrobot, "external").
  qactor( controller, ctxcoldstoragearea, "it.unibo.controller.Controller").
  qactor( cold_room, ctxcoldstoragearea, "it.unibo.cold_room.Cold_room").
