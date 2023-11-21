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
with Diagram('coldstorage0Arch', show=False, outformat='png', graph_attr=graphattr) as diag:
  with Cluster('env'):
     sys = Custom('','./qakicons/system.png')
### see https://renenyffenegger.ch/notes/tools/Graphviz/attributes/label/HTML-like/index
     with Cluster('ctxcoldstoragearea', graph_attr=nodeattr):
          coldroom=Custom('coldroom','./qakicons/symActorSmall.png')
          coldstorageservice=Custom('coldstorageservice','./qakicons/symActorSmall.png')
          serviceaccessgui=Custom('serviceaccessgui','./qakicons/symActorSmall.png')
     serviceaccessgui >> Edge(color='magenta', style='solid', decorate='true', label='<depositRequest<font color="darkgreen"> accept reject</font> &nbsp; checkmyticket<font color="darkgreen"> ticketchecked</font> &nbsp; loaddone<font color="darkgreen"> chargetaken</font> &nbsp; >',  fontcolor='magenta') >> coldstorageservice
     coldstorageservice >> Edge(color='magenta', style='solid', decorate='true', label='<weightrequest<font color="darkgreen"> weightOK weightKO</font> &nbsp; >',  fontcolor='magenta') >> coldroom
diag
