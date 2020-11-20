// Orange Live Objects device simulator (simple data publisher)

// Wrap the script in a method, so that you can do "return false;" in case of an error or stop request
function publish()
{
    var Math = Java.type("java.lang.Math");
    var streamId = "device1stream";
    
    var temperature = Math.floor((Math.random() * 141) - 20);
    var hygro       = Math.floor((Math.random() * 101));
    var revmin      = Math.floor((Math.random() * 10001));
    var co2         = Math.floor((Math.random() * 2001));
    var pressure    = Math.floor((Math.random() * 4001));
    var dooropen    = false;
    var lat         = 45.1889148; // Grenoble
    var lon         = 5.7299093;
    var alt         = 250;
    var accuracy    = 23;


    for (;;)
    {
        var temperature = Math.max(   -20, Math.min(   120, ~~(temperature + Math.floor(((Math.random()*2 -1) * (  120  -  (-20) + 1) * 10/100)))));
        var hygro       = Math.max(     0, Math.min(   100, ~~(hygro       + Math.floor(((Math.random()*2 -1) * (  100           + 1) * 10/100)))));
        var revmin      = Math.max(     0, Math.min( 10000, ~~(revmin      + Math.floor(((Math.random()*2 -1) * (10000           + 1) * 10/100)))));
        var co2         = Math.max(     0, Math.min(  2000, ~~(co2         + Math.floor(((Math.random()*2 -1) * ( 2000           + 1) * 10/100)))));
        var pressure    = Math.max(     0, Math.min(  4000, ~~(pressure    + Math.floor(((Math.random()*2 -1) * ( 4000           + 1) * 10/100)))));
        var lat         = Math.max(45.129, Math.min(45.230,   (lat         +           (((Math.random()*2 -1) * (45.230 - 45.129    ) * 10/100)))));
        var lon         = Math.max( 5.661, Math.min( 5.864,   (lon         +           (((Math.random()*2 -1) * ( 5.864 -  5.661    ) * 10/100)))));
        var dooropen    = (Math.random() < 0.1 ? ~dooropen : dooropen);                                                          

        mqttspy.publish("dev/data", "{\"streamId\":\""+streamId+"\", \"value\": {\"revmin\": "+revmin+", \"CO2\": "+co2+", \"doorOpen\": "+(dooropen?"true":"false")+", \"hygrometry\": "+hygro+", \"temperature\": "+temperature+", \"pressure\": "+pressure+"}, \"location\": { \"lon\": "+lon+", \"lat\": "+lat+", \"alt\": "+alt+", \"accuracy\": "+accuracy+", \"provider\": \"GPS\" }, \"model\": \"demomodel\", \"tags\": [\"simulator\"]}");

        var Thread = Java.type("java.lang.Thread");
        try 
        {
            Thread.sleep(3000);
        }
        catch(err) 
        {
            return true;
        }
    }    

    // This means all OK, script has completed without any issues and as expected
    return true;
}

//mqttspy.setScriptTimetout(timeoutInMilliseconds);
publish();
