/**const ot = document.getElementById("outputText");
document.getElementById("check").disabled = true;
document.getElementById("load").disabled = true;

var initialText = "Welcome to Christmas's service";
var requestText = "Richiesta ticket inviata. \nAttendi la risposta.";
var checkText = "Richiesta di verifica del ticket inviata. \nAttendi la risposta.";
var loaddoneText = "Richiesta di scaricare inviata. \nAttendi per sapere quando andare via.";


var currentW = "3";
document.getElementById('cw').innerHTML = currentW;


function updateCurrentW(c) {
    currentW = c;
    document.getElementById('cw').innerHTML = currentW;
}

function loadDone() {
    document.getElementById("req").disabled = false;
    document.getElementById("check").disabled = true;
    document.getElementById("load").disabled = true;
}

function check() {
    document.getElementById("check").disabled = true;
    document.getElementById("load").disabled = false;
}

function depositRequest() {
    document.getElementById("req").disabled = true;
    document.getElementById("check").disabled = false;
}*/