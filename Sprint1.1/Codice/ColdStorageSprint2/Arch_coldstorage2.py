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
     controller >> Edge(color='magenta', style='solid', decorate='true', label='<doJob<font color="darkgreen"> jobdone robotDead</font> &nbsp; >',  fontcolor='magenta') >> transporttrolley
     tickethandler >> Edge(color='magenta', style='solid', decorate='true', label='<weightrequest<font color="darkgreen"> weightOK weightKO</font> &nbsp; >',  fontcolor='magenta') >> coldroom
     facade >> Edge(color='magenta', style='solid', decorate='true', label='<depositRequest<font color="darkgreen"> accept reject</font> &nbsp; checkmyticket<font color="darkgreen"> ticketchecked</font> &nbsp; >',  fontcolor='magenta') >> tickethandler
     serviceaccessgui >> Edge(color='magenta', style='solid', decorate='true', label='<depositRequestF<font color="darkgreen"> acceptF rejectF</font> &nbsp; checkmyticketF<font color="darkgreen"> ticketcheckedF</font> &nbsp; loaddoneF<font color="darkgreen"> chargetakenF</font> &nbsp; >',  fontcolor='magenta') >> facade
     facade >> Edge(color='magenta', style='solid', decorate='true', label='<loaddone<font color="darkgreen"> chargetaken</font> &nbsp; >',  fontcolor='magenta') >> controller
     facade >> Edge(color='magenta', style='solid', decorate='true', label='<getweight<font color="darkgreen"> currentweight</font> &nbsp; >',  fontcolor='magenta') >> coldroom
     controller >> Edge(color='blue', style='solid',  label='<updateWeight &nbsp; >',  fontcolor='blue') >> coldroom
     tickethandler >> Edge(color='blue', style='solid',  label='<updateWeight &nbsp; >',  fontcolor='blue') >> coldroom
diag
