var socket;
var socketIP = "localhost:8075";


function sendWSMessage(message) {
    var jsonMsg = JSON.stringify( {'name': message});
    socket.send(jsonMsg);
    console.log("Sent Message: " + jsonMsg);
}

function connect(){
    var addr     = "ws://" + socketIP + "/socket"  ;
    //alert("connect addr=" + addr   );

    // Assicura che sia aperta un unica connessione
    if(socket !== undefined && socket.readyState !== WebSocket.CLOSED){
        alert("WARNING: Connessione WebSocket già stabilita");
    }
    socket = new WebSocket(addr);

    socket.onopen = function (event) {
        console.log("Connected to " + addr);
    };

    socket.onmessage = function (event) {
        //alert(`Got Message: ${event.data}`);
        msg = event.data;
        //alert(`Got Message: ${msg}`);
        console.log("ws, message: " + msg);
        updateWeightField(msg);
    };

    function updateWeightField(msg){

        splittedmsg = msg.split("_");
        switch(splittedmsg[0]){
            case "th":
                console.log("tickethandler");
                document.getElementById("rt").innerHTML=splittedmsg[1];
                break
            case "cr":
                console.log("coldroom");
                document.getElementById("ew").innerHTML=splittedmsg[1];
                document.getElementById("pw").innerHTML=splittedmsg[2];
                break
            default:
                //
        }




    }

}//connect
