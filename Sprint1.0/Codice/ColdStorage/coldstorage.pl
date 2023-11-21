%====================================================================================
% coldstorage description   
%====================================================================================
request( doJob, doJob(KG) ).
reply( jobdone, jobdone(NO_PARAM) ).  %%for doJob
reply( robotDead, robotDead(NO_PARAM) ).  %%for doJob
dispatch( updateWeight, updateWeight(PESO) ).
%====================================================================================
context(ctxcoldstoragearea, "localhost",  "TCP", "8040").
 qactor( transporttrolley, ctxcoldstoragearea, "it.unibo.transporttrolley.Transporttrolley").
  qactor( controller, ctxcoldstoragearea, "it.unibo.controller.Controller").
  qactor( coldroom, ctxcoldstoragearea, "it.unibo.coldroom.Coldroom").
