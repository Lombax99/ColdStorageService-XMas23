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
          controller=Custom('controller','./qakicons/symActorSmall.png')
          mymain=Custom('mymain','./qakicons/symActorSmall.png')
          basicrobot=Custom('basicrobot','./qakicons/symActorSmall.png')
          planexec=Custom('planexec','./qakicons/symActorSmall.png')
          robotpos=Custom('robotpos','./qakicons/symActorSmall.png')
     controller >> Edge(color='blue', style='solid', xlabel='doJob', fontcolor='blue') >> mymain
     mymain >> Edge(color='blue', style='solid', xlabel='setrobotstate', fontcolor='blue') >> robotpos
     mymain >> Edge(color='magenta', style='solid', xlabel='moverobot', fontcolor='magenta') >> robotpos
     mymain >> Edge(color='blue', style='solid', xlabel='setdirection', fontcolor='blue') >> robotpos
     sys >> Edge(color='red', style='dashed', xlabel='alarm', fontcolor='red') >> planexec
     planexec >> Edge(color='blue', style='solid', xlabel='nextmove', fontcolor='blue') >> planexec
     planexec >> Edge(color='blue', style='solid', xlabel='nomoremove', fontcolor='blue') >> planexec
     planexec >> Edge(color='magenta', style='solid', xlabel='step', fontcolor='magenta') >> basicrobot
     robotpos >> Edge(color='magenta', style='solid', xlabel='doplan', fontcolor='magenta') >> planexec
diag
