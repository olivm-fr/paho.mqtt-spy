// Orange Live Objects cloud-to-cloud simulator (simple x-connector publisher)

// Wrap the script in a method, so that you can do "return false;" in case of an error or stop request
function publish()
{
        mqttspy.publish("connector/v1/nodes/myconnect01/status", "{ \"status\": \"ONLINE\", \"capabilities\": {\"command\": {\"available\": true}}}", 0, false);

        mqttspy.publish("connector/v1/nodes/myconnect01/data", "{ \"model\": \"data_v0\", \"value\": { \"payload\" : \"9X0N9ovJBvwcZd3RrGcff3ZxglbLuq/y934JtIwzjdNTeKy/0cG9Xk7fQZytyvNW\" }, \"location\": { \"lat\": 48.86667, \"lon\": 2.33333, \"alt\": 35.2, \"accuracy\": 12.3, \"provider\": \"GPS\" }, \"tags\": [ \"fromconnector\" ], \"metadata\": {\"encoding\": \"aes\" }}", 0, false);

        // This means all OK, script has completed without any issues and as expected
        return true;
}

publish();