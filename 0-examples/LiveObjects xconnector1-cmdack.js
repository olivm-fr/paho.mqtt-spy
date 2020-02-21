// Orange Live Objects cloud-to-cloud simulator (simple x-connector publisher)
// Wrap the script in a method, so that you can do "return false;" in case of an error or stop request
function publish()
{
        mqttspy.publish("connector/v1/responses/command", "{\"id\": \"5e4fe58af7f5cc1209efb835\",\"nodeId\": \"myconnect01\",\"response\": { \"status\": \"ok\", \"message\": {\"level\": 75}}}", 0, false);

        // This means all OK, script has completed without any issues and as expected
        return true;
}

publish();