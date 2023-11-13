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
with Diagram('basicrobot2Arch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxbasicrobot', graph_attr=nodeattr):
          transporttrolley=Custom('transporttrolley','./qakicons/symActorSmall.png')
          basicrobot=Custom('basicrobot','./qakicons/symActorSmall.png')
          planexec=Custom('planexec','./qakicons/symActorSmall.png')
          robotpos=Custom('robotpos','./qakicons/symActorSmall.png')
     with Cluster('ctxcoldstoragearea', graph_attr=nodeattr):
          coldroom=Custom('coldroom(ext)','./qakicons/externalQActor.png')
     transporttrolley >> Edge(color='magenta', style='solid', decorate='true', label='<moverobot &nbsp; >',  fontcolor='magenta') >> robotpos
     robotpos >> Edge(color='magenta', style='solid', decorate='true', label='<doplan &nbsp; >',  fontcolor='magenta') >> planexec
     planexec >> Edge(color='magenta', style='solid', decorate='true', label='<step &nbsp; >',  fontcolor='magenta') >> basicrobot
     basicrobot >> Edge(color='blue', style='solid',  label='<robotready &nbsp; >',  fontcolor='blue') >> transporttrolley
     planexec >> Edge(color='blue', style='solid',  label='<nextmove &nbsp; nomoremove &nbsp; >',  fontcolor='blue') >> planexec
     transporttrolley >> Edge(color='blue', style='solid',  label='<setrobotstate &nbsp; setdirection &nbsp; >',  fontcolor='blue') >> robotpos
diag
