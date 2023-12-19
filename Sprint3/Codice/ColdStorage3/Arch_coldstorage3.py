### conda install diagrams
from diagrams import Cluster, Diagram, Edge
from diagrams.custom import Custom
import os
os.environ['PATH'] += os.pathsep + 'C:/Program Files/Graphviz/bin/'

graphattr = {     #https://www.graphviz.org/doc/info/attrs.html
    'fontsize': '22',
}

nodeattr = {   
    'fontsize': '22',
    'bgcolor': 'lightyellow'
}

eventedgeattr = {
    'color': 'red',
    'style': 'dotted'
}
with Diagram('coldstorage3Arch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxcoldstoragearea', graph_attr=nodeattr):
          controller=Custom('controller','./qakicons/symActorSmall.png')
          coldroom=Custom('coldroom','./qakicons/symActorSmall.png')
          tickethandler=Custom('tickethandler','./qakicons/symActorSmall.png')
          transporttrolley=Custom('transporttrolley','./qakicons/symActorSmall.png')
          planexec=Custom('planexec','./qakicons/symActorSmall.png')
          robotpos=Custom('robotpos','./qakicons/symActorSmall.png')
          basicrobot=Custom('basicrobot','./qakicons/symActorSmall.png')
          facade=Custom('facade','./qakicons/symActorSmall.png')
          serviceaccessguimock=Custom('serviceaccessguimock','./qakicons/symActorSmall.png')
          servicestatusguimock=Custom('servicestatusguimock','./qakicons/symActorSmall.png')
     with Cluster('ctxalarm', graph_attr=nodeattr):
          led=Custom('led(ext)','./qakicons/externalQActor.png')
          sonar=Custom('sonar(ext)','./qakicons/externalQActor.png')
     serviceaccessguimock >> Edge(color='magenta', style='solid', decorate='true', label='<depositRequest<font color="darkgreen"> accept reject</font> &nbsp; checkmyticket<font color="darkgreen"> ticketchecked</font> &nbsp; loaddone<font color="darkgreen"> chargetaken</font> &nbsp; >',  fontcolor='magenta') >> facade
     servicestatusguimock >> Edge(color='magenta', style='solid', decorate='true', label='<getweight<font color="darkgreen"> currentweight</font> &nbsp; getrobotstate<font color="darkgreen"> robotstate</font> &nbsp; getrejectedtickets<font color="darkgreen"> rejectedtickets</font> &nbsp; >',  fontcolor='magenta') >> facade
     controller >> Edge(color='magenta', style='solid', decorate='true', label='<doJob<font color="darkgreen"> jobdone robotDead</font> &nbsp; >',  fontcolor='magenta') >> transporttrolley
     transporttrolley >> Edge(color='magenta', style='solid', decorate='true', label='<moverobot<font color="darkgreen"> moverobotdone moverobotfailed</font> &nbsp; >',  fontcolor='magenta') >> robotpos
     tickethandler >> Edge(color='magenta', style='solid', decorate='true', label='<weightrequest<font color="darkgreen"> weightOK weightKO</font> &nbsp; >',  fontcolor='magenta') >> coldroom
     robotpos >> Edge(color='magenta', style='solid', decorate='true', label='<doplan<font color="darkgreen"> doplandone doplanfailed</font> &nbsp; >',  fontcolor='magenta') >> planexec
     planexec >> Edge(color='magenta', style='solid', decorate='true', label='<step<font color="darkgreen"> stepdone stepfailed</font> &nbsp; >',  fontcolor='magenta') >> basicrobot
     controller >> Edge(color='blue', style='solid',  label='<updateWeight &nbsp; >',  fontcolor='blue') >> coldroom
     basicrobot >> Edge(color='blue', style='solid',  label='<robotready &nbsp; >',  fontcolor='blue') >> transporttrolley
     controller >> Edge(color='blue', style='solid',  label='<stopped &nbsp; arrivedhome &nbsp; moving &nbsp; >',  fontcolor='blue') >> led
     planexec >> Edge(color='blue', style='solid',  label='<nextmove &nbsp; nomoremove &nbsp; >',  fontcolor='blue') >> planexec
     transporttrolley >> Edge(color='blue', style='solid',  label='<setrobotstate &nbsp; setdirection &nbsp; >',  fontcolor='blue') >> robotpos
     tickethandler >> Edge(color='blue', style='solid',  label='<updateWeight &nbsp; >',  fontcolor='blue') >> coldroom
     controller >> Edge(color='blue', style='solid',  label='<stopplan &nbsp; continueplan &nbsp; >',  fontcolor='blue') >> planexec
diag
