%====================================================================================
% coldstorage2 description   
%====================================================================================
context(ctxcoldstoragearea, "localhost",  "TCP", "8040").
 qactor( controller, ctxcoldstoragearea, "it.unibo.controller.Controller").
  qactor( coldroom, ctxcoldstoragearea, "it.unibo.coldroom.Coldroom").
  qactor( tickethandler, ctxcoldstoragearea, "it.unibo.tickethandler.Tickethandler").
  qactor( serviceaccessgui, ctxcoldstoragearea, "it.unibo.serviceaccessgui.Serviceaccessgui").
