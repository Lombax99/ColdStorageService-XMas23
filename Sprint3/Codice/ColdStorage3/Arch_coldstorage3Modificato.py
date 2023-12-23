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
          facade=Custom('facade','./qakicons/symActorSmall.png')
          serviceaccessguimock=Custom('serviceaccessguimock','./qakicons/symActorSmall.png')
          servicestatusguimock=Custom('servicestatusguimock','./qakicons/symActorSmall.png')
     with Cluster('ctxalarm', graph_attr=nodeattr):
          led=Custom('led(ext)','./qakicons/externalQActor.png')
          sonar=Custom('sonar(ext)','./qakicons/externalQActor.png')
     serviceaccessguimock >> Edge(minlen="4", color='magenta', style='solid', label='<depositRequest<font color="darkgreen"> accept reject</font> &nbsp; checkmyticket<font color="darkgreen"> ticketchecked</font> &nbsp; loaddone<font color="darkgreen"> chargetaken</font> &nbsp; >',  fontcolor='magenta') >> facade
     facade >> Edge(minlen="4", color='magenta', style='solid', label='<depositRequest<font color="darkgreen"> accept reject</font> &nbsp; checkmyticket<font color="darkgreen"> ticketchecked</font> &nbsp; getrejectedtickets<font color="darkgreen"> rejectedtickets</font> &nbsp; >',  fontcolor='magenta') >> tickethandler
     servicestatusguimock >> Edge(minlen="4", color='magenta', style='solid', label='<getweight<font color="darkgreen"> currentweight</font> &nbsp; getrobotstate<font color="darkgreen"> robotstate</font> &nbsp; getrejectedtickets<font color="darkgreen"> rejectedtickets</font> &nbsp; >',  fontcolor='magenta') >> facade
     controller >> Edge(minlen="6", color='magenta', style='solid', xlabel='<doJob<font color="darkgreen"> jobdone robotDead</font> &nbsp; >',  fontcolor='magenta') >> transporttrolley
     tickethandler >> Edge(minlen="4", color='magenta', style='solid', xlabel='<weightrequest<font color="darkgreen"> weightOK weightKO</font> &nbsp; >',  fontcolor='magenta') >> coldroom
     facade >> Edge(minlen="4", color='magenta', style='solid', xlabel='<loaddone<font color="darkgreen"> chargetaken</font> &nbsp; >',  fontcolor='magenta') >> controller
     facade >> Edge(minlen="4", color='magenta', style='solid', xlabel='<getweight<font color="darkgreen"> currentweight</font> &nbsp; >',  fontcolor='magenta') >> coldroom
     controller >> Edge(minlen="4", color='blue', style='solid',  label='<updateWeight &nbsp; >',  fontcolor='blue') >> coldroom
     controller >> Edge(minlen="4", color='blue', style='solid',  xlabel='<stopped &nbsp; arrivedhome &nbsp; moving &nbsp; >',  fontcolor='blue') >> led
     tickethandler >> Edge(minlen="4", color='blue', style='solid',  label='<updateWeight &nbsp; >',  fontcolor='blue') >> coldroom
     sonar >> Edge(minlen="4", color='blue', style='solid',  xlabel='<stop &nbsp; continue &nbsp; >',  fontcolor='blue') >> controller
diag
