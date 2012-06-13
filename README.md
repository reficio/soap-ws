Welcome to soap-ws project!

This is a lightweight and easy-to-use Java library to handle SOAP message generation
and SOAP message transmission purely on an XML level. With the usage of this library
you can easily import you WSDL (1.0, 1.1) and generate XML messages for the operations
included in that WSDL. Then you can use the SoapClient to transmit this message over
HTTP to a web-service endpoint. Finally, you can run SoapServer to get and respond to
SOAP messages. You can also use soap server in a mock-mode in which all messages will
be responded to with a sample message compliant with the operation that's being invoked.
The library is extensively tested with 25 sets of tricky hierarchical WSDL files with
internal/external XSD schemas.

This project contains classes extracted from the soapUI code base by centeractive ag
in October 2011. They are located in the soap-builder module. Every extracted class is
annotated with an comment to fulfill he requirements of the LGPL 2.1 license under
which soapUI is released. That is also the reason why soap-builder module is also
released under LGPL 2.1 license. All other soap-ws modules are released under Apache
v.2 license. The main reason behind class the extraction was to separate the code that
is responsible for the generation of the SOAP messages from the rest of the soapUI's
code that is tightly coupled with other modules, such as soapUI's graphical user
interface, etc. The goal was to create an open-source java project whose main
responsibility is to handle SOAP message generation and SOAP transmission purely on
an XML level.

centeractive ag would like to express strong appreciation to SmartBear Software and
to the whole team of soapUI's developers for creating soapUI and for releasing its
source code under a free and open-source licence. centeractive ag extracted and
modifies some parts of the soapUI's code in good faith, making every effort not
to impair any existing functionality and to supplement it according to our
requirements, applying best practices of software design.

Spec support:
- supports WSDL 1.1
- supports SOAP 1.1 and 1.2
- supports all four WS flavors: [rpc|document] * [encoded|literal]
- supports SSL

Features:
- soap message generation in XML format on the basis of WSDL 
- soap server exposition - communication and message handling purely on XML level
- soap message auto-responder
- soap client - communication and message handling purely on XML level
- soap client - basic auth, proxy over plain connection
