Spec support:
- supports SOAP 1.1 and 1.2
- supports all four WS flavors: [rpc|document] * [encoded|literal]
- supports SSL

What is in the code:
- soap message generation in XML format on the basis of WSDL 
- soap server exposition - communication and message handling purely on XML level
- soap message auto-responder
- soap client - communication and message handling purely on XML level
- soap client - basic auth, proxy over plain connection

TODO:
- request validation
- wsdl exposition

- basic auth in server
- WS-security support
- certificate authentication support
- proxy support over ssl

- WS-attachment
- WS-addressing
- WS-reliable-messaging
- WSDL 2.0 support