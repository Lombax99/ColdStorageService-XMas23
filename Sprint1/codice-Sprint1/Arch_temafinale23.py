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
with Diagram('temafinale23Arch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
     with Cluster('ctxColdStorageArea', graph_attr=nodeattr):
          controller=Custom('controller','./qakicons/symActorSmall.png')
          transport_trolley=Custom('transport_trolley','./qakicons/symActorSmall.png')
          cold_room=Custom('cold_room','./qakicons/symActorSmall.png')
     controller >> Edge(color='blue', style='solid', xlabel='doJob', fontcolor='blue') >> transport_trolley
     transport_trolley >> Edge(color='blue', style='solid', xlabel='updateWeight', fontcolor='blue') >> cold_room
diag