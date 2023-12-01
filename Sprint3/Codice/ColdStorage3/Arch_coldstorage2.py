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
          transporttrolley=Custom('transporttrolley','./qakicons/symActorSmall.png')
          basicrobot=Custom('basicrobot','./qakicons/symActorSmall.png')
          planexec=Custom('planexec','./qakicons/symActorSmall.png')
          robotpos=Custom('robotpos','./qakicons/symActorSmall.png')
     facade >> Edge(color='magenta', style='solid', decorate='true', label='<depositRequest &nbsp; checkmyticket &nbsp; >',  fontcolor='magenta') >> tickethandler
     transporttrolley >> Edge(color='magenta', style='solid', decorate='true', label='<moverobot &nbsp; >',  fontcolor='magenta') >> robotpos
     controller >> Edge(color='magenta', style='solid', decorate='true', label='<doJob &nbsp; >',  fontcolor='magenta') >> transporttrolley
     planexec >> Edge(color='magenta', style='solid', decorate='true', label='<step &nbsp; >',  fontcolor='magenta') >> basicrobot
     tickethandler >> Edge(color='magenta', style='solid', decorate='true', label='<weightrequest &nbsp; >',  fontcolor='magenta') >> coldroom
     robotpos >> Edge(color='magenta', style='solid', decorate='true', label='<doplan &nbsp; >',  fontcolor='magenta') >> planexec
     facade >> Edge(color='magenta', style='solid', decorate='true', label='<loaddone &nbsp; >',  fontcolor='magenta') >> controller
     facade >> Edge(color='magenta', style='solid', decorate='true', label='<getweight &nbsp; >',  fontcolor='magenta') >> coldroom
     serviceaccessgui >> Edge(color='magenta', style='solid', decorate='true', label='<depositRequest &nbsp; checkmyticket &nbsp; >',  fontcolor='magenta') >> tickethandler
     serviceaccessgui >> Edge(color='magenta', style='solid', decorate='true', label='<loaddone &nbsp; >',  fontcolor='magenta') >> controller
     controller >> Edge(color='blue', style='solid',  label='<updateWeight &nbsp; >',  fontcolor='blue') >> coldroom
     basicrobot >> Edge(color='blue', style='solid',  label='<robotready &nbsp; >',  fontcolor='blue') >> transporttrolley
     planexec >> Edge(color='blue', style='solid',  label='<nextmove &nbsp; nomoremove &nbsp; >',  fontcolor='blue') >> planexec
     transporttrolley >> Edge(color='blue', style='solid',  label='<setrobotstate &nbsp; setdirection &nbsp; >',  fontcolor='blue') >> robotpos
     tickethandler >> Edge(color='blue', style='solid',  label='<updateWeight &nbsp; >',  fontcolor='blue') >> coldroom
diag
