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
with Diagram('basicrobot23Arch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
     with Cluster('ctxbasicrobot', graph_attr=nodeattr):
          transporttrolley=Custom('transporttrolley','./qakicons/symActorSmall.png')
          basicrobot=Custom('basicrobot','./qakicons/symActorSmall.png')
          planexec=Custom('planexec','./qakicons/symActorSmall.png')
          robotpos=Custom('robotpos','./qakicons/symActorSmall.png')
     with Cluster('ctxcoldstoragearea', graph_attr=nodeattr):
          cold_room=Custom('cold_room(ext)','./qakicons/externalQActor.png')
     transporttrolley >> Edge(color='blue', style='solid', xlabel='setrobotstate', fontcolor='blue') >> robotpos
     transporttrolley >> Edge(color='magenta', style='solid', xlabel='moverobot', fontcolor='magenta') >> robotpos
     transporttrolley >> Edge(color='blue', style='solid', xlabel='setdirection', fontcolor='blue') >> robotpos
     sys >> Edge(color='red', style='dashed', xlabel='alarm', fontcolor='red') >> planexec
     planexec >> Edge(color='blue', style='solid', xlabel='nextmove', fontcolor='blue') >> planexec
     planexec >> Edge(color='blue', style='solid', xlabel='nomoremove', fontcolor='blue') >> planexec
     planexec >> Edge(color='magenta', style='solid', xlabel='step', fontcolor='magenta') >> basicrobot
     robotpos >> Edge(color='magenta', style='solid', xlabel='doplan', fontcolor='magenta') >> planexec
diag
