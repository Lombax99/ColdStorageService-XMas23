import socket

HOST = "127.0.0.1"      # The server's hostname or IP address
PORT = 8040             # The port used by the server

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((HOST, PORT))
    s.sendall(b"msg(startToDoThings,dispatch,alien,serviceaccessgui,startToDoThings,0)")

print("data sent")




'''
QActor serviceaccessgui context ctxcoldstoragearea {

	[#	var PESO = 0

		var Ticket = ""

		var Ticketok = false

		#]

	

	State s0 initial {

		printCurrentMessage

		println("SAG - in attesa") color yellow

	} Transition t0 whenMsg startToDoThings -> work
    '''