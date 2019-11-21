
# Extended Purpose

Allows applications to group endpoints using a given function.
The name of the function should be given in variable metricsProcessEndpoint and should be a global function.

# Purpose

Use this plugin to get a per-endpoint breakdown of latency and response codes in your Artillery HTTP tests.

# Usage

Install the plugin globally or locally, depending on your setup

```
// global plugin installation
 npm install artillery-plugin-metrics-by-endpoint -g
 
 // local plugin installation
 npm install --save-dev artillery-plugin-metrics-by-endpoint
```
 
Enable the plugin in the config
 
```
config:
  plugins:
    metrics-by-endpoint: {}
  variables:
     metricsProcessEndpoint : "myProcessEndpoint"
```

Function *myProcessEndpoint* should be defined as a global function in the processor code.

# License

MPL 2.0
