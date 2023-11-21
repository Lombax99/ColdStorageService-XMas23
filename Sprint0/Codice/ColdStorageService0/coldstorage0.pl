%====================================================================================
% coldstorage0 description   
%====================================================================================
request( depositRequest, depositRequest(PESO) ).
reply( accept, accept(TICKET) ).  %%for depositRequest
reply( reject, reject(NO_PARAM) ).  %%for depositRequest
request( weightrequest, weightrequest(PESO) ).
reply( weightOK, weightOK(NO_PARAM) ).  %%for weightrequest
reply( weightKO, weightKO(NO_PARAM) ).  %%for weightrequest
request( checkmyticket, checkmyticket(TICKET) ).
reply( ticketchecked, ticketchecked(BOOL) ).  %%for checkmyticket
request( loaddone, loaddone(PESO) ).
reply( chargetaken, chargetaken(NO_PARAM) ).  %%for loaddone
dispatch( startToDoThings, startToDoThings(NO_PARAM) ).
%====================================================================================
context(ctxcoldstoragearea, "localhost",  "TCP", "8040").
 qactor( coldroom, ctxcoldstoragearea, "it.unibo.coldroom.Coldroom").
  qactor( coldstorageservice, ctxcoldstoragearea, "it.unibo.coldstorageservice.Coldstorageservice").
  qactor( serviceaccessgui, ctxcoldstoragearea, "it.unibo.serviceaccessgui.Serviceaccessgui").
