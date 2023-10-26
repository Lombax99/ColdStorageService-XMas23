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
     with Cluster('ctxcoldstoragearea', graph_attr=nodeattr):
          controller=Custom('controller','./qakicons/symActorSmall.png')
          coldroom=Custom('coldroom','./qakicons/symActorSmall.png')
          tickethandler=Custom('tickethandler','./qakicons/symActorSmall.png')
          serviceaccessgui=Custom('springserver','./qakicons/externalQActor.png')
          transporttrolley=Custom('transporttrolley(ext)','../qakicons/symActorSmall.png')
     controller >> Edge(color='magenta', style='solid', xlabel='doJob', fontcolor='magenta') >> transporttrolley
     controller >> Edge(color='blue', style='solid', xlabel='updateWeight', fontcolor='blue') >> coldroom
     tickethandler >> Edge(color='magenta', style='solid', xlabel='weightrequest', fontcolor='magenta') >> coldroom
     tickethandler >> Edge(color='blue', style='solid', xlabel='updateWeight', fontcolor='blue') >> coldroom
     serviceaccessgui >> Edge(color='magenta', style='solid', xlabel='depositRequest', fontcolor='magenta') >> tickethandler
     serviceaccessgui >> Edge(color='magenta', style='solid', xlabel='checkmyticket', fontcolor='magenta') >> tickethandler
     serviceaccessgui >> Edge(color='magenta', style='solid', xlabel='loaddone', fontcolor='magenta') >> controller
diag
