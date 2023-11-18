%====================================================================================
% alarm description   
%====================================================================================
dispatch( arrivedhome, arrivedhome(NO_PARAM) ).
dispatch( moving, moving(NO_PARAM) ).
dispatch( stopped, stopped(NO_PARAM) ).
dispatch( stop, stop(NO_PARAM) ).
dispatch( continue, continue(NO_PARAM) ).
%====================================================================================
context(ctxalarm, "localhost",  "TCP", "8300").
 qactor( sonar, ctxalarm, "it.unibo.sonar.Sonar").
  qactor( led, ctxalarm, "it.unibo.led.Led").
