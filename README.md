# geoAlgorithmTestApp
This demo app is just an example showing a quick integration of the geoAlgorithm library. The steps used are as follows:

# Configuration
In order to receive location updates you need to register a receiver in your manifest file, just like this:
<pre><code>
&lt;receiver android:name="com.tuillo.algorithmlibrary.receivers.LocationReceiver" android:enabled="true" /&gt;

</code></pre>
Also, you need to request the following permissions:
<pre><code>
&lt;uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/&gt;
&lt;uses-permission android:name="android.permission.INTERNET"/&gt;
&lt;uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/&gt;
&lt;uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/&gt;
&lt;uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/&gt;
&lt;uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/&gt;

</code></pre>

# Initialization
This library is pretty straight forward. First we initialize the library itself, this can be achieved by calling the following method:
<pre><code>
AlgorithmControlTower.init(&lt;CONTEXT&gt;, &lt;YOUR_API_KEY&gt;);
</code></pre>

#Retrieving locations

Once initialized you can receive location updates by registering a BroadcastReceiver and listening for the following intent filter:

<pre><code>
AlgorithmControlTower.LIB_INTENT_ACTION

</code></pre>

So the resulting code will be something like this:

<pre><code>
@Override
protected void onResume() {
    super.onResume();

    LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(AlgorithmControlTower.LIB_INTENT_ACTION));

}

private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(AlgorithmControlTower.LIB_INTENT_ACTION)){
            if(intent.getExtras().getBoolean(AlgorithmControlTower.HAS_LOCATION_TAG)){
                Location location = AlgorithmControlTower.getLastLocation(context);

                //Your code goes here...
            }
        }
    }
};

</code></pre>

And that's it! geoAlgorithm is up and running.

# Downloads
If you're interested in trying this sample app, we'll provide soon a downloadable apk or contact us and we'll get in touch as soon as possible!

# Contact
Have any questions or you're in need of support? You can send us an email at <a href="mailto:info@geoalgorithm.com">info@geoalgorithm</a> or visit us at <a href="http://tuillo.com/algorithm/">tuillo.com/algorithm/</a>
