var websocket = new WebSocket("ws://localhost:9000/socket");

websocket.onopen = function() {
	console.log("open");
}

websocket.onclose = function() {
	console.log("close");
}

websocket.onmessage = function(e) {
	console.log(e)
}