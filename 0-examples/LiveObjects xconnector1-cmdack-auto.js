// Orange Live Objects cloud-to-cloud simulator (simple x-connector automatic command responder)
// HowTo: set this script (full path) in the "script" column of the subscription pane in the connection configuration, with connector/v1/requests/command as topic

function onMessage()
{
    var answering = true; // set Math.random() > 0.1;  to have some unanswered commands
    if (answering) {
        var level = Math.random()*100;
        var ok = (Math.random() < 0.2) ? "The device refused" : "ok";
        var payload = JSON.parse(receivedMessage.getPayload());
        var body = "{\"id\": \"" + payload.id + "\",\"nodeId\": \"" + payload.nodeId + "\",\"response\": { \"status\": \"" + ok + "\", \"message\": {\"level\": " + level + "}}}";
        mqttspy.publish("connector/v1/responses/command", body, 0, false);

        // logger.info("Publishing command response : " + body);
    }
    
    // This means all OK, script has completed without any issues and as expected
    return true;
}
