# soap-ws [![Build Status](https://secure.travis-ci.org/centeractive/soap-ws.png)](http://travis-ci.org/centeractive/soap-ws)

## A lightweight and easy-to-use Java library to handle SOAP on a purely XML level.

### Intro
Welcome to soap-ws! This is a lightweight and easy-to-use Java library to handle SOAP message generation and SOAP message transmission on a purely XML level. With the usage of this library
within few lines of code you can easily import your WSDL and generate SOAP messages directly in an XML format. 
Then you can use the SoapClient to transmit this message over HTTP(s) to a web-service endpoint. 
Finally, you can run SoapServer to receive SOAP messages and and respond to them. 
And all of that requires no classes or stubs generation - everything happens directly in an XML format.


### Why should you use soap-ws?
Read this carefully and check if you know what we are talking about.

* Have you ever had problems with the versioning of web-service endpoints? Have you ever had to address the problem how to deal with many versions of the same classes generated from two versions of the same WSDL in one code base? Did you try to prefix the classes, change the package, or do any other mambo-jumbo tricks that are clearly against the best-practices of software design?
* Have you every tried to chain and orchestrate a few web-service invocations applying some XSLT transformation to the consecutive responses forwarding them to the next endpoint? Have you ever seen how cumbersome it is using Java generated ws clients/servers?
* Have you ever had to re-generate the ws-stubs, recompile and redeploy you application because of a tiny change in the WSDL?
* Have you every been confused why you generate all these domain and stub classes to invoke one simple web-service operation and to get a plain response that could be processed with XSTL one-liner?
* Have you ever had to had to send a simple XML message to a SOAP server in a fire and forget mode?
* Have you ever had to expose a mock SOAP endpoint that would respond to the request sending a sample response -let's say in an unit test?
* Have you ever had to download a hierarchical WSDL file with hierarchical XSD schemas and store it on your local hard drive with all the import and includes fixed properly so that you can reuse it locally?
* Have you ever…

Yes, that's what soap-ws can do for you. But it can do much more, just dive in and check the plethora of stuff that we have implemented.

### SOAP specifications - what is supported
* supports WSDL 1.1
* supports SOAP 1.1 and 1.2
* supports all four WS flavors: rpc-encoded, rpc-literal, document-literal and document-encoded
* supports SSL

### Main features

soap-builder:

* soap message generation in the XML format on the basis of imported WSDL 
* fetch and store of a hierarchical WSDL with hierarchical XSD included in it

soap-server:

* endpoint exposition - communication and message handling purely in the XML format
* auto-responder - respond to a soap request with a sample content - in an unit-test
* HTTP and HTTPS support
* extensive operation matcher - match a request to a BindingOperation from the WSDL

soap-client:

* communication and message handling purely in the XML format
* basic authentication and SSL support
* proxy with basic authentication support
* proper SOAPAction support in both SOAP versions

### License
The project is open-source and distributed under the Apache license, Version 2.0.
One module (soap-builder) is distributed under the LGPL 2.1 license (see the Note).
You can confidently use soap-ws in your commercial project.



## User Guide

### Quick-start

#### Add soap-ws to your maven project
In order to use soap-ws in your project you have to declare soap-ws in the dependencies section of your pom.xml. You can add soap-builder, soap-client, soap-server or all of them, depending on the fact which components you want to use.
```xml
    <dependencies>
        <dependency>
            <groupId>com.centeractive</groupId>
            <artifactId>soap-builder</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.centeractive</groupId>
            <artifactId>soap-client</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.centeractive</groupId>
            <artifactId>soap-server</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
```
soap-ws is not yet located in the central maven repo, thus you also have to add an additional repository to your config.
```xml
    <repositories>
        <repository>
            <id>reficio</id>
            <url>http://repo.reficio.org/maven/</url>
        </repository>
    </repositories>
```

#### soap-builder
SoapBuilder interface describes the functionality of generation of the XML SOAP messages. An instance of SoapBuilder is always bound to one wsdl and one of its bindings. As you probably know there can be more bindings in one WSDL file. In order to handle all of theme you will need an instance of SoapBuilder per binding. How to construct an instance of SoapBuilder you may ask?
First, we have to construct a SoapParser - the simplest way is to invoke the constructor specifying the URL of the WSDL file (1). 
```java
    SoapParser parser = new SoapParser(wsdlUrl);  // (1)
        
    List<QName> bindings = parser.getBindings(); // (2)        
    QName bindingName = bindings.iterator.next(); // take the first binding
    
    SoapBuilder builder = parser.getBinding(bindingName); // (3)    
    
    List<SoapOperation> operations = builder.getOperations(); // (4)
    SoapOperation op = operations.iterator.next(); // take the first operation
```
SoapParser reads the specified WSDL file recursively, fetching all included WSDL and XSD files, and constructs an underlying javax.wsdl.Definition object that is the Java-based representation of the WSDL (see WSDL4j to read more about the Definitoin object). 

In order to generate a SOAP message you have to specify the QName of the Binding.
To check what binding are defined in the WSDL invoke the getBindings() method (2).

When you decide which binding you want to use you can easily create a SoapBuilder instance just by invoking the getBuilder() method on the SoapParser object (3).

The last step is to generate a SOAP message using the SoapBuilder. In order to do it though you have to specify the SOAP operation. In order to get the list of operations specified in that binding just invoke the getOperations() method on the SoapBuilder object (4).

Now you are all set. To generate a SOAP message in the XML format just invoke one of the methods defined in the SoapBuilder interface specifiying the SoapOperation. You can also build generic empty messages invoking buildEmptyMessage or buildFault:
```java
	public interface SoapBuilder {

	    String buildInputMessage(SoapOperation operation);
    	String buildInputMessage(SoapOperation operation, SoapContext context);
	    String buildOutputMessage(SoapOperation operation);
    	String buildOutputMessage(SoapOperation operation, SoapContext context);
    
    	String buildFault(String code, String message);
    	String buildEmptyFault();
    	String buildEmptyMessage();

		List<SoapOperation> getOperations();
		QName getBindingName();
    	Binding getBinding(); 	
	}
```

Last, but not least. In most of the cases, you can relay on the default settings of the SoapContext the specifies how messages are generate, but if you would like to change it you have to populate the SoapContext object and pass it either to the SoapParser (from that moment on, SoapBuilder will use this context as the default one), or to single methods, changing the context of the generation for time span of single method invocation. In order to populate a SoapContext object use the fluent builder. 
```java
    SoapContext context = SoapContext.builder()
        .alwaysBuildHeaders(true)
        .buildOptional(true)
        .exampleContent(true)
        .typeComment(true)
        .skipComments(false)
        .build();
```

OK, now the easy part begins...

#### soap-client
You can create an instance of a soap-client using a fluent builder. If you want to use a plain HTTP connection without tweaking any advance options you are good to go with the following snippet:
```java
    SoapClient client = SoapClient.builder()
        .endpointUrl("http://example.com/endpoint")
        .build();
```
Then, you can send a SOAP envelope (as a String) invoking the post() method:
```java
    client.post(soapAction, envelope);
```

You can also skip the SOAPAction header and send the envelope only:
```java
    client.post(envelope);
```
Isn't it easy? But it's gonna be even better :)

#### soap-server
Use a similar builder to create an instance of the soap-server. 
```java
    SoapServer server = SoapServer.builder()
                    .httpPort(8080)
                    .build();
```
You can start and stop the server using start/stop methods
```java
    server.start();
    server.stop();
```

Now we would like to turn our server into a mock server that responds to request generating a sample content that is complaint with the schema of the operation that is being invoked.
To do that we have to create an AutoResponder and register it under the given context path.
Autoresponder requires a populated SoapBuilder instance (that contains the WSDL and the binding name which it should use). Keep in mind the there can be only one binding under one context path;
```java
    String contextPath = "/exampleEndpoint";
    AutoResponder responder = new AutoResponder(soapBuilder);
    server.registerResponder(contextPath, responder);
```
From that moment our server will respond to request send to the "exampleEndpoint" context path.

If you would like to handle the request yourself you just have to implement the RequestResponder interface. 
```java
    public interface RequestResponder {
        java.xml.Source respond(SoapMessage request);
    }
```

It may be a bit cumbersome, as it is not that easy to match an XML request to the binding and operation, that is the reason why we provided an AbstractResponder that does all of that backstage.
```java
    public abstract class AbstractResponder implements RequestResponder {
     	// (…)
     	
     	/**
     	* Abstract method that should be implemented by overriding classes.
     	* This method is invoked whenever a request is send by the client.
     	* InvokedOperation may be passed to a SoapBuilder to construct the
     	* response to the request that was sent by the client.
     	*
     	* @param invokedOperation operation from the binding that is matched to the SOAP message
     	* @param message          SOAP message passed by the client
     	* @return response in the XML source format containing the whole SOAP envelope
     	*/
    	public abstract Source respond(SoapOperation invokedOperation, SoapMessage message);
    }
```

AbstractResponder does all the hard work for you to match the message to the BindingOperation from the WSDL. If it find it the respond() operation is invoked, if not a SOAP fault is send back to the client saying the operation has not been found.
AbstractResponder uses our implementation of the SoapOperationMatcher that matches the request to the operation in the following way:  

* SOAP Action mapping
* RCP bindings are matched using single top-level tag with the name of the invoked operation
* Document bindings are matched by input types and then by input names
 
Having the SoapOperation object provided by the AbstractResponder you can easily generate and modify the response that you create using SoapBuilder that available in the responder as a instance field called builder.

That's a lot of stuff. I hope you enjoyed it! Have a look at the examples located in the soap-examples project. Try it out now and leave send us some feedback!

### Project modules
* soap-builder - responsible for the generation of SOAP XML messages.
* soap-client - responsible for the communication with a SOAP endpoint.
* soap-server - responsible for exposing SOAP endpoints and handling the requests.
* soap-test - contains integration tests - tests soap-client and soap-server in many tricky ways.
* soap-examples - contains a few example how to use soap-ws.
* soap-legacy - legacy code extracted from 3rd party projects

### Example usage
#### Post a SOAP message with SoapClient
```java
	String url = String.format("http://localhost:%d%s", port, contextPath);
    SoapClient client = SoapClient.builder()
            .endpointUrl(url)
            .build();

    String request =
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
                    "xmlns:stoc=\"http://centeractive.com/stockquote.wsdl\""+
                    "xmlns:stoc1=\"http://centeractive.com/stockquote.xsd\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <stoc:GetLastTradePrice>\n" +
                    "         <stoc1:TradePriceRequest>\n" +
                    "            <tickerSymbol>?</tickerSymbol>\n" +
                    "         </stoc1:TradePriceRequest>\n" +
                    "      </stoc:GetLastTradePrice>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";

    String response = client.post(request);
```    

#### Generate and post a SOAP message
```java
	// construct the client
    String url = String.format("http://localhost:%d%s", port, contextPath);
    SoapClient client = SoapClient.builder()
            .endpointUrl(url)
            .build();

    SoapParser parser = new SoapParser(wsdlUrl);
    SoapBuilder soapBuilder = parser.getBuilder(bindingName);

    // get the operation to invoked -> assumption our operation is the first operation in the WSDL's
    SoapOperation operation = soapBuilder.getOperations().iterator().next();

    // construct the request
    String request = soapBuilder.buildInputMessage(operation);
    // post the request to the server
    String response = client.post(request);
    // get the response
    String expectedResponse = soapBuilder.buildOutputMessage(operation);

    assertTrue(XMLUnit.compareXML(expectedResponse, response).identical());
```

#### Create a SoapServer
```java
	SoapServer server = SoapServer.builder()
            .httpPort(9090)
            .build();
    server.start();
```

#### Create a SoapServer with AutoResponder (great to unit test web-services)
```java
	SoapServer server = SoapServer.builder()
            .httpPort(9090)
            .build();
    server.start();

    QName bindingName = new QName("http://centeractive.com/stockquote.wsdl", "StockQuoteSoapBinding");
    URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath("/", "stockquote-service.wsdl");

    SoapParser parser = new SoapParser(wsdlUrl);
    AutoResponder responder = new AutoResponder(parser.getBuilder(bindingName));

    server.registerRequestResponder("/service", responder);
```

#### Create a SoapServer with a custom responder
```java
	SoapServer server = SoapServer.builder()
            .httpPort(9090)
            .build();
    server.start();

    URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath("/", "stockquote-service.wsdl");
    SoapParser parser = new SoapParser(wsdlUrl);
    // assumption -> we take the first binding
    final SoapBuilder builder = parser.getBuilder(parser.getBindings().get(0));
    AbstractResponder customResponder = new AbstractResponder(builder) {
        @Override
        public Source respond(SoapOperation invokedOperation, SoapMessage message) {
            try {
                // build the response using builder
                String response = builder.buildOutputMessage(invokedOperation);
                // here you can tweak the response -> for example with XSLT
                //...
                return XmlUtils.xmlStringToSource(response);
            } catch (Exception e) {
                // will automatically generate SOAP-FAULT
                throw new RuntimeException("my custom error", e);
            }
        }
    };

    server.registerRequestResponder("/service", customResponder);
```

You can find all these working examples in the soap-examples project. Enjoy!


## Last but not least

### How can I hack around?
* GitHub -> https://github.com/centeractive/soap-ws
* Jenkins -> https://reficio.ci.cloudbees.com/job/soap-ws/
* Sonar -> http://nemo.sonarsource.org/dashboard/index/com.centeractive:soap-ws
* Site -> http://projects.reficio.org/soap-ws/1.0.0-SNAPSHOT/manual.html

### Who's behind it?
Tom Bujok [tom.bujok@gmail.com]

centeractive ag
www.centeractive.com

### Note
This project contains classes extracted from the soapUI code base by centeractive ag
in October 2011. They are located in the soap-builder module. Every extracted class is
annotated with an comment to fulfill he requirements of the LGPL 2.1 license under
which soapUI is released. That is also the reason why soap-legacy module is
released under LGPL 2.1 license. All other soap-ws modules are released under Apache
v.2 license. The main reason behind class the extraction was to separate the code that
is responsible for the generation of the SOAP messages from the rest of the soapUI's
code that is tightly coupled with other modules, such as soapUI's graphical user
interface, etc. The goal was to create an open-source java project whose main
responsibility is to handle SOAP message generation and SOAP transmission purely on
an XML level.

centeractive ag would like to express strong appreciation to SmartBear Software and
to the whole team of soapUI's developers for creating soapUI and for releasing its
source code under a free and open-source license. centeractive ag extracted and
modifies some parts of the soapUI's code in good faith, making every effort not
to impair any existing functionality and to supplement it according to our
requirements, applying best practices of software design.