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
with Diagram('coldstorage11Arch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxcoldstoragearea', graph_attr=nodeattr):
          controller=Custom('controller','./qakicons/symActorSmall.png')
          coldroom=Custom('coldroom','./qakicons/symActorSmall.png')
          tickethandler=Custom('tickethandler','./qakicons/symActorSmall.png')
          facade=Custom('facade','./qakicons/symActorSmall.png')
          serviceaccessguimock=Custom('serviceaccessguimock','./qakicons/symActorSmall.png')
          transporttrolley=Custom('transporttrolley','./qakicons/symActorSmall.png')
     facade >> Edge(color='magenta', style='solid', xlabel='<depositRequest<font color="darkgreen"> accept reject</font><BR/> checkmyticket<font color="darkgreen"> ticketchecked</font> &nbsp; >',  fontcolor='magenta') >> tickethandler
     serviceaccessguimock >> Edge(color='magenta', style='solid', label='<depositRequest<font color="darkgreen"> accept reject</font> <BR/> checkmyticket<font color="darkgreen"> ticketchecked</font> <BR/> loaddone<font color="darkgreen"> chargetaken</font> >',  fontcolor='magenta') >> facade
     facade >> Edge(color='magenta', style='solid', label='<loaddone<font color="darkgreen"> chargetaken</font> &nbsp; >',  fontcolor='magenta') >> controller
     facade >> Edge(color='magenta', style='solid', xlabel='<getweight<font color="darkgreen"> currentweight</font> &nbsp; >',  fontcolor='magenta') >> coldroom
     controller >> Edge(color='magenta', style='solid', label='<doJob<font color="darkgreen"> jobdone robotDead</font> &nbsp; >',  fontcolor='magenta') >> transporttrolley
     tickethandler >> Edge(color='magenta', style='solid', label='<weightrequest<font color="darkgreen"> weightOK weightKO</font> &nbsp; >',  fontcolor='magenta') >> coldroom
     controller >> Edge(color='blue', style='solid',  xlabel='<updateWeight &nbsp; >',  fontcolor='blue') >> coldroom
     tickethandler >> Edge(color='blue', style='solid',  xlabel='<updateWeight &nbsp; >',  fontcolor='blue') >> coldroom
diag
