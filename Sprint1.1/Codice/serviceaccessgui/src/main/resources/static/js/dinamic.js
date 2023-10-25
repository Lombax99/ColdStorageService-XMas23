<<<<<<< HEAD
var peso = 0;
var ticket = "";

var IP = "http://localhost:8085/api/";

document.getElementById("weightsubmit").addEventListener("submit", function(e)
{
    e.preventDefault();
    updateweight();
});

document.getElementById("depositsubmit").addEventListener("submit", function(e)
{
    e.preventDefault();
    var fw = document.getElementById("foodweight").value;
    sendMessage("depositreq", "fw="+fw);
});

document.getElementById("checksubmit").addEventListener("submit", function(e)
{
    e.preventDefault();
    var t = document.getElementById("varticket").value;
    ticket = t;
    sendMessage("checkreq","ticket="+t);
});

document.getElementById("loadsubmit").addEventListener("submit", function(e)
{
    e.preventDefault();
    sendMessage("loadreq","weight="+peso);
});


function responsehandler(type, response){
    console.log(type);
    console.log(response);
    switch (type){
        case "weightreq":
            var weights=getMsgValue(response).split(",");
            //document.getElementById("cw").innerHTML=weights[0];
            document.getElementById("ew").innerHTML=weights[1];
            break;
        case "depositreq":
            var responsebutton = getMsgType(response);
            var t = getMsgValue(response);
            document.getElementById("maintext").innerHTML= "La tua richiesta è stata:" + responsebutton;
            document.getElementById("varticket").value = t;
            if(responsebutton == "accept")
                enableButtons("requestaccepted");
            else
                enableButtons("default");
            updateweight();
            break;
        case "checkreq":
            var ticketvalid = getMsgValue(response);
            document.getElementById("maintext").innerHTML = ticketvalid;
            if(ticketvalid == "true") {
                peso = getWeightFromTicket(ticket);
                console.log("ciao1" + ticket);
                console.log("ciao2: " + peso);
                enableButtons("ticketaccepted");
            }
            else
                enableButtons("default");
            updateweight();
            break;
        case "loadreq":
            document.getElementById("maintext").innerHTML = "Il tuo peso è stato preso in carico! ADDIO";
            peso=0;
            enableButtons("default");
            updateweight();
            break;
        default:
            console.log("richiesta non riconosciuta");
    }

}

function updateweight(){
    sendMessage("weightreq");
}


function sendMessage(request, parameters) {
    try
    {
        if(parameters)
            var url = IP+request+"?"+ parameters;
        else
            var url = IP+request;

        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            if (this.readyState == 4 ) {
                var response = this.responseText;
                //console.log("response: " + response);
                responsehandler(request, response);
            }
        };
        xhttp.open("POST", url, true);
        xhttp.setRequestHeader('Content-Type', 'text/plain');
        xhttp.send();
    }
    catch(erMsg) {
        console.log(erMsg);
    }
}


//msg(accept,reply,tickethandler,roberto,accept(T_1698228378_2_0),17)
//accept
function getMsgType(msg){
    return msg.split('(')[1].split(',')[0];
}

//T_1698228378_2_0
function getMsgValue(msg){
    return msg.split(/[\(\)]/)[2];
}

function getWeightFromTicket(ticket){
    return ticket.split("_")[2];
}

function enableButtons(mode){
    switch(mode){
        case"requestaccepted":
            //document.getElementById("requestbutton").setAttribute("disabled", "disabled");
            //document.getElementById("checkbutton").removeAttribute("disabled");
            //document.getElementById("loadbutton").setAttribute("disabled", "disabled");
            break;
        case "ticketaccepted":
            //document.getElementById("requestbutton").setAttribute("disabled", "disabled");
            //document.getElementById("checkbutton").setAttribute("disabled", "disabled");
            document.getElementById("loadbutton").removeAttribute("disabled");
            break;
        default:
            //document.getElementById("requestbutton").removeAttribute("disabled");
            //document.getElementById("checkbutton").setAttribute("disabled", "disabled");
            document.getElementById("loadbutton").setAttribute("disabled", "disabled");
    }
=======
var peso = 0;
var ticket = "";


document.getElementById("weightsubmit").addEventListener("submit", function(e)
{
    e.preventDefault();
    updateweight();
});

document.getElementById("depositsubmit").addEventListener("submit", function(e)
{
    e.preventDefault();
    var fw = document.getElementById("foodweight").value;
    sendMessage("depositreq", "fw="+fw);

});

document.getElementById("checksubmit").addEventListener("submit", function(e)
{
    e.preventDefault();
    var ticket = document.getElementById("varticket").value;
    sendMessage("checkreq","ticket="+ticket);
    updateweight();
});

document.getElementById("loadsubmit").addEventListener("submit", function(e)
{
    e.preventDefault();

    sendMessage("loadreq","weight="+peso);
    updateweight();
});


function responsehandler(type, response){
    console.log(type);
    console.log(response);
    switch (type){
        case "weightreq":
            var weights=getMsgValue(response).split(",");
            document.getElementById("cw").innerHTML=weights[0];
            document.getElementById("ew").innerHTML=weights[1];
            break;
        case "depositreq":
            var responsebutton = getMsgType(response); //accept o reject
            var t = getMsgValue(response); //ticket
            document.getElementById("maintext").innerHTML= "La tua richiesta è stata:" + responsebutton;
            document.getElementById("varticket").value = t;
            if(responsebutton == "accept") {
                enableButtons("requestaccepted");
                ticket = t;
            }
            else
                enableButtons("default");
            updateweight();
            break;
        case "checkreq":
            var ticketvalid = getMsgValue(response);
            document.getElementById("maintext").innerHTML = ticketvalid;
            if(ticketvalid == "true") {
                peso = getWeightFromTicket(ticket);
                enableButtons("ticketaccepted");
            }
            else
                enableButtons("default");
            updateweight();
            break;
        case "loadreq":
            document.getElementById("maintext").innerHTML = "Il tuo peso è stato preso in carico! ADDIO";
            enableButtons("default");
            updateweight();
            break;
        default:
            console.log("richiesta non riconosciuta");

    }

}

function updateweight(){
    sendMessage("weightreq");
}


function sendMessage(request, parameters){
    try
    {
        if(parameters)
            var url = "http://localhost:8085/api/"+request+"?"+ parameters;
        else
            var url = "http://localhost:8085/api/"+request;
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            if (this.readyState == 4 ) {
                var response = this.responseText;
                console.log("response: " + response);
                responsehandler(request, response);
            }
        };
        xhttp.open("POST", url, true);
        xhttp.setRequestHeader('Content-Type', 'text/plain');
        xhttp.send();
    }
    catch(erMsg)
    {
        console.log(erMsg);
    }
}

function getMsgType(msg){
    console.log(msg);
    return msg.split('(')[1].split(',')[0];
}

function getMsgValue(msg){
    return msg.split(/[\(\)]/)[2];
}

function getWeightFromTicket(ticket){
    return ticket.split("_")[2];
}

function enableButtons(mode){
    switch(mode){
        case"requestaccepted":
            document.getElementById("requestbutton").setAttribute("disabled", "disabled");
            document.getElementById("checkbutton").removeAttribute("disabled");
            document.getElementById("loadbutton").setAttribute("disabled", "disabled");
            break;
        case "ticketaccepted":
            document.getElementById("requestbutton").setAttribute("disabled", "disabled");
            document.getElementById("checkbutton").setAttribute("disabled", "disabled");
            document.getElementById("loadbutton").removeAttribute("disabled");
            break;
        default:
            document.getElementById("requestbutton").removeAttribute("disabled");
            document.getElementById("checkbutton").setAttribute("disabled", "disabled");
            document.getElementById("loadbutton").setAttribute("disabled", "disabled");
    }
>>>>>>> a4ede728a216a40274444a4cbce6cc9c0438a724
}