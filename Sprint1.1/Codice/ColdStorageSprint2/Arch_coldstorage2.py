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
with Diagram('coldstorage2Arch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxcoldstoragearea', graph_attr=nodeattr):
          controller=Custom('controller','./qakicons/symActorSmall.png')
          coldroom=Custom('coldroom','./qakicons/symActorSmall.png')
          tickethandler=Custom('tickethandler','./qakicons/symActorSmall.png')
          facade=Custom('facade','./qakicons/symActorSmall.png')
          serviceaccessgui=Custom('serviceaccessgui','./qakicons/symActorSmall.png')
     with Cluster('ctxbasicrobot', graph_attr=nodeattr):
          transporttrolley=Custom('transporttrolley(ext)','./qakicons/externalQActor.png')
     serviceaccessgui >> Edge(color='magenta', style='solid', decorate='true', label='<loaddone &nbsp; >',  fontcolor='magenta') >> controller
     controller >> Edge(color='magenta', style='solid', decorate='true', label='<doJob &nbsp; >',  fontcolor='magenta') >> transporttrolley
     tickethandler >> Edge(color='magenta', style='solid', decorate='true', label='<weightrequest &nbsp; >',  fontcolor='magenta') >> coldroom
     serviceaccessgui >> Edge(color='magenta', style='solid', decorate='true', label='<depositRequest &nbsp; checkmyticket &nbsp; >',  fontcolor='magenta') >> tickethandler
     facade >> Edge(color='magenta', style='solid', decorate='true', label='<depositRequest &nbsp; checkmyticket &nbsp; >',  fontcolor='magenta') >> tickethandler
     facade >> Edge(color='magenta', style='solid', decorate='true', label='<loaddone &nbsp; >',  fontcolor='magenta') >> controller
     facade >> Edge(color='magenta', style='solid', decorate='true', label='<getweight &nbsp; >',  fontcolor='magenta') >> coldroom
     controller >> Edge(color='blue', style='solid',  label='<updateWeight &nbsp; >',  fontcolor='blue') >> coldroom
     tickethandler >> Edge(color='blue', style='solid',  label='<updateWeight &nbsp; >',  fontcolor='blue') >> coldroom
diag
