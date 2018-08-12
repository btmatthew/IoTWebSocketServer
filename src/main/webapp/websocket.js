var ws;

function connect() {
    var username = document.getElementById("username").value;
    ws = new WebSocket("ws://" + document.location.host + document.location.pathname+ "iot/" + username);


    ws.onmessage = function(event) {
        var log = document.getElementById("log");
        console.log(event.data);
        var message = JSON.parse(event.data);
        log.innerHTML += message.from + " : " + message.action + "\n";
    };
}

function send() {
    var content = document.getElementById("msg").value;
    var json = JSON.stringify({
        "action":content
    });

    ws.send(json);
}