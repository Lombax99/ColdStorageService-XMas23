%====================================================================================
% temafinale23 description   
%====================================================================================
context(ctxcoldstoragearea, "localhost",  "TCP", "9090").
 qactor( controller, ctxcoldstoragearea, "it.unibo.controller.Controller").
  qactor( controller, ctxcoldstoragearea, "it.unibo.controller.Controller").
  qactor( transport_trolley, ctxcoldstoragearea, "it.unibo.transport_trolley.Transport_trolley").
  qactor( cold_room, ctxcoldstoragearea, "it.unibo.cold_room.Cold_room").
