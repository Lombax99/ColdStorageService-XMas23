<html>
<head>
<title>ServiceAccessGui web page</title>

<style>
* {
  box-sizing: border-box;
  font-family: 'Times New Roman', serif;
}

.row {
  display: flex;
}

/* Create two equal columns that sits next to each other */
.column {
  float: left;
  width: 50%;
  padding: 10px;
  text-align: center
}

@media screen and (max-width: 600px) {
  .column {
    width: 100%;
  }
}

.button {
  background-color: white; 
  color: black; 
  border: 2px solid #008CBA;
  padding: 15px 32px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 18px;
  margin: 4px 2px;
  cursor: pointer;
  border-radius: 4px;
  transition-duration: 0.4s;
}
.button:hover {
  background-color: #008CBA;
  color: white;
}

</style>

</head>
<body style="text-align:center;">

<h1 style="color:white; font-size:300%; background-color:#008CBA">Cold Storage Service</h1>
<p style="font-size:150%;">Current weight in cold room:
<label style="font-size:120%;" id="cw"></label></p>

<br>

<div class="row">
  <div class="column" >
    <label style="font-size:150%;" for="foodweight">Ask for ticket here:</label>
	<br/>
	<input style="font-size:150%;" type="number" id="foodweight" name="foodweight" placeholder="set food weight" required>
	<input type="button" class="button" onclick="depositRequest()" id="request" value="Request">
	<br/>
	<br/>
	<input type="button" class="button" onclick="check()" id="check" value="Check Ticket">
	<br/>
	<br/>
	<input type="button" class="button" onclick="loadDone()" id="load" value="Load done">
	
  </div>
  
  <div class="column" >
	<textarea style="font-size:150%;" id="outputText" rows=10 cols=70 wrap=on readonly>Welcome to Christmas's service</textarea>
  
  </div>
</div>


<script>
	const ot = document.getElementById("outputText");
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
		ot.innerHTML = loaddoneText;
		document.getElementById("request").disabled = false;
		document.getElementById("check").disabled = true;
		document.getElementById("load").disabled = true;
	}
	
	function check() {
		ot.innerHTML = checkText;
		document.getElementById("check").disabled = true;
		document.getElementById("load").disabled = false;
	}
	
	function depositRequest() {
		ot.innerHTML = requestText;
		document.getElementById("request").disabled = true;
		document.getElementById("check").disabled = false;
	}
</script>	

</body>
</html>
