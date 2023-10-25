%====================================================================================
% coldstorage description   
%====================================================================================
context(ctxcoldstoragearea, "localhost",  "TCP", "8040").
 qactor( transporttrolley, ctxcoldstoragearea, "it.unibo.transporttrolley.Transporttrolley").
  qactor( controller, ctxcoldstoragearea, "it.unibo.controller.Controller").
  qactor( coldroom, ctxcoldstoragearea, "it.unibo.coldroom.Coldroom").
