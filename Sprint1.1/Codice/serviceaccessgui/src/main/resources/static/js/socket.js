var socket;
var socketIP = "localhost:8085";


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
        console.log("ws-status:" + msg);
        updateWeightField(msg);
    };

    function updateWeightField(msg){
        if(msg.split("_").length == 2){
            document.getElementById("ew").innerHTML=msg.split("_")[1];
        }

    }

}//connect
