// Orange Live Objects cloud-to-cloud simulator (simple x-connector manual command responder)

function publish()
{
        mqttspy.publish("connector/v1/responses/command", "{\"id\": \"5ebaab2df7f5cc038c9a56ae\",\"nodeId\": \"myconnect01\",\"response\": { \"status\": \"ok\", \"message\": {\"level\": 75}}}", 0, false);

        // This means all OK, script has completed without any issues and as expected
        return true;
}

publish();