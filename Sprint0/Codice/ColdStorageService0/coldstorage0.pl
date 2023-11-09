%====================================================================================
% coldstorage0 description   
%====================================================================================
request( depositRequest, depositRequest(PESO) ).
request( weightrequest, weightrequest(PESO) ).
request( checkmyticket, checkmyticket(TICKET) ).
request( loaddone, loaddone(PESO) ).
dispatch( startToDoThings, startToDoThings(NO_PARAM) ).
%====================================================================================
context(ctxcoldstoragearea, "localhost",  "TCP", "8040").
 qactor( coldroom, ctxcoldstoragearea, "it.unibo.coldroom.Coldroom").
  qactor( coldstorageservice, ctxcoldstoragearea, "it.unibo.coldstorageservice.Coldstorageservice").
  qactor( serviceaccessgui, ctxcoldstoragearea, "it.unibo.serviceaccessgui.Serviceaccessgui").
