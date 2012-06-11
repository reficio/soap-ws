Welcome to soap-ws!
This is a lightweight and easy-to-use Java library to handle SOAP message generation and SOAP message transmission purely
on an XML level. With the usage of this library you can easily import you WSDL (1.0, 1.1) and generate XML messages for the operations
included in that WSDL. Then you can use the SoapClient to transmit this message over HTTP to a web-service endpoint.
Finally, you can run SoapServer to get and respond to SOAP messages. You can also use soap server in a mock-mode in which all messages
will be responded to with a sample message compliant with the operation that's being invoked.
The library is extensively tested with 25 sets of tricky hierarchical WSDL files with internal/external XSD schemas.
Have fun with soap-ws and wait for more stuff that's coming in!

Spec support:
- supports WSDL 1.1
- supports SOAP 1.1 and 1.2
- supports all four WS flavors: [rpc|document] * [encoded|literal]
- supports SSL

What is in the code:
- soap message generation in XML format on the basis of WSDL 
- soap server exposition - communication and message handling purely on XML level
- soap message auto-responder
- soap client - communication and message handling purely on XML level
- soap client - basic auth, proxy over plain connection
