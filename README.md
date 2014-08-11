# soap-ws [![Build Status](http://img.shields.io/travis/reficio/soap-ws.svg)](http://travis-ci.org/reficio/soap-ws)

## A lightweight and easy-to-use Java library to handle SOAP on a purely XML level.

### Intro
Welcome to soap-ws! This is a lightweight and easy-to-use Java library that wraps Spring-WS and enables handling SOAP on a purely XML level. soap-ws is based on four main abstractions:

* WsdlParser can easily parse your WSDL and produce SoapBuilders,
* SoapBuilder can generate SOAP messages directly in the XML format, 
* SoapClient can be used to transmit a SOAP message over HTTP(s) to a web-service endpoint, 
* SoapServer can be leveraged to process SOAP messages and and respond to them. 

All of that requires no generation of stubs - everything happens directly in XML having -  is the provider.


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

### What is supported?
* supports WSDL 1.1
* supports SOAP 1.1 and 1.2
* supports all four WS flavors: rpc-encoded, rpc-literal, document-literal and document-encoded
* supports SSL and basic-authentication
* supports SOCKS, HTTP and HTTPS proxies
* supports SpringFramework
* supports JUnit, Spock and Arquillian


### Main features

soap-builder:

* fetch, parse and store WSDL (hierarchical WSDLs and XSDs supported)
* generate a soap XML message on the basis of the imported WSDL 

soap-client:

* communication and message handling purely in the XML format
* basic authentication and SSL support
* HTTP(s) and SOCKS proxy support, with/without basic authentication 

soap-server:

* endpoint exposition - communication and message handling purely in the XML format
* extensive operation matcher - validate and match a request to a BindingOperation from the WSDL
* auto-responder - respond to a soap request with a sample content
* HTTP and HTTPS support

### License
The project is open-source and distributed under the Apache license, Version 2.0.
One module (soap-legacy) is distributed under the LGPL 2.1 license (see the Note).
You can confidently use soap-ws in your commercial project.


## User Guide

### Quick-start

#### Add soap-ws to your maven project
In order to use soap-ws in your project you have to declare soap-ws in the dependencies section of your pom.xml. You can mix and match soap-builder, soap-client, soap-server artifacts, depending on the fact what you want to achieve.
```xml
    <dependencies>
        <dependency>
            <groupId>org.reficio</groupId>
            <artifactId>soap-builder</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.reficio</groupId>
            <artifactId>soap-client</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.reficio</groupId>
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

If you are a Gradle user you probably know how to do it :)

#### Consume a Web-Serivce in 60 seconds
Let's consume the CurrencyConverter Web-Service. Thanks to the fluent builders the API is straigtforward and intuitive. 
Does it need any explanation? Welcome to soap-ws :)
```java
	Wsdl wsdl = Wsdl.parse("http://www.webservicex.net/CurrencyConvertor.asmx?WSDL");
    
    SoapBuilder builder = wsdl.binding()
    	.localPart("CurrencyConvertorSoap")
    	.find();
    SoapOperation operation = builder.operation()
    	.soapAction("http://www.webserviceX.NET/ConversionRate")
    	.find();
    Request request = builder.buildInputMessage(operation)
    
    SoapClient client = SoapClient.builder()
    	.endpointUrl("http://www.webservicex.net/CurrencyConvertor.asmx")
    	.build();
    String response = client.post(request);
```


#### Provide a Web-Service in 60 seconds
Let's provide the CurrencyConverter Web-Service that returns random results (compliant with the schema!).
```java
	Wsdl wsdl = Wsdl.parse("http://www.webservicex.net/CurrencyConvertor.asmx?WSDL");
	SoapBuilder builder = wsdl.binding()
    	.localPart("CurrencyConvertorSoap")
    	.find();
    	
    SoapServer server = SoapServer.builder()
    	.httpPort(9090)
   		.build();
    server.registerRequestResponder("/currencyConvertor", new AutoResponder(builder));
    server.start();
``` 

That's more or less what you need to generate a SOAP message and consume/provide a Web-Service.

### API
Let's have a closer look at the API and the main abstractions.

#### SoapBuilder
SoapBuilder interface describes the features of generation of XML SOAP messages. An instance of the SoapBuilder class is always bound to a specific wsdl file and one of its bindings. There can be more bindings in one WSDL file - in order to handle all of theme an instance of SoapBuilder is needed for every binding. 
The simplest way to construct an instance of the Wsdl is to call the static factory method "parse", passing the URL of the WSDL file (1).
```java
    Wsdl wsdl = Wsdl.parse(wsdlUrl);  // (1)
        
	List<QName> bindings = wsdl.getBindings(); // (2)
	SoapBuilder builder = wsdl.binding().localPart("CurrencyConvertorSoap").find(); // (3)
	wsdl.printBindings(); // (4)
    
    List<SoapOperation> operations = builder.getOperations(); // (5)
	SoapOperation operation = builder.operation().name("ConversionRate").find();  // (6)
    
```
Wsdl.parse(wsdlUrl) reads the specified WSDL file recursively, fetching all included WSDL and XSD files and constructs an underlying javax.wsdl.Definition object that is the Java-based representation of the WSDL (see WSDL4j to read more about the Definitoin object).

In order to generate a SOAP message you have to specify the Binding. To check what binding are defined in the WSDL invoke the getBindings() method (2). You can also use the binding finder, just call the binding() method and add additional parameters such as localPart(""), etc. Then invoke builder to get an instance of the SoapBuilder(). 
Finally, you can invoke the printBindings() method that will print all the  binding to the stdout (just as a quick hack) (4).

The last step is to generate a SOAP message using the SoapBuilder. In order to do it though you have to specify the SOAP operation. In order to get the list of operations specified in that binding just invoke the getOperations() method on the SoapBuilder object (5). You can also use the SOAP operation finder - just call the operation() method and chain additional parameters such as name(), etc. Then call find() and get a reference to the Soap Operation.

Now you are all set. To generate a SOAP message in the XML format just invoke one of the methods defined in the SoapBuilder interface specifiying the SoapOperation. You can also build generic empty messages invoking buildEmptyMessage or buildFault:
```java
	public interface SoapBuilder {

		String buildInputMessage(SoapOperation operation);
    	String buildInputMessage(SoapOperation operation, SoapContext context);

	    String buildOutputMessage(SoapOperation operation);
    	String buildOutputMessage(SoapOperation operation, SoapContext context);

	    String buildFault(String code, String message);
    	String buildFault(String code, String message, SoapContext context);

	    String buildEmptyFault();
    	String buildEmptyFault(SoapContext context);
	    String buildEmptyMessage();

		// (…)
	}
```

Last, but not least. In most of the cases, you can relay on the default settings of the SoapContext that specifies how messages are generated, but if you would like to change it you have to populate the SoapContext object and pass it either to the Wsdl (from that moment on, SoapBuilder will use this context as the default one), or to single methods, changing the context of the generation for the time span of a single method invocation. In order to populate a SoapContext object use the fluent builder presented below.
```java
    SoapContext context = SoapContext.builder()
        .alwaysBuildHeaders(true)
        .buildOptional(true)
        .exampleContent(true)
        .typeComment(true)
        .skipComments(false)
        .build();
```

#### SoapClient
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


#### SoapServer
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
* soap-it - contains integration tests - tests soap-client and soap-server in many tricky ways.
* soap-examples - contains a few example how to use soap-ws.
* soap-legacy - legacy code extracted from 3rd party projects

### Example usage

#### Generate and post a SOAP message
```java
	Wsdl wsdl = Wsdl.parse("http://www.webservicex.net/CurrencyConvertor.asmx?WSDL");
    
    SoapBuilder builder = wsdl.binding()
    	.localPart("CurrencyConvertorSoap")
    	.find();
    SoapOperation operation = builder.operation()
    	.soapAction("http://www.webserviceX.NET/ConversionRate")
    	.find();
    Request request = builder.buildInputMessage(operation)
    
    SoapClient client = SoapClient.builder()
    	.endpointUrl("http://www.webservicex.net/CurrencyConvertor.asmx")
    	.build();
    String response = client.post(request);
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

    URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath("/", "wsdl/stockquote-service.wsdl");
    Wsdl wsdl = Wsdl.parse(wsdlUrl);
    SoapBuilder builder = wsdl.binding().localPart("StockQuoteSoapBinding").find();
    AutoResponder responder = new AutoResponder(builder);

    server.registerRequestResponder("/service", responder);
    server.stop();
```

#### Create a SoapServer with a custom responder
```java
	SoapServer server = SoapServer.builder()
            .httpPort(9090)
            .build();
    server.start();

    URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath("/", "wsdl/stockquote-service.wsdl");
    Wsdl wsdl = Wsdl.parse(wsdlUrl);
    final SoapBuilder builder = wsdl.binding().localPart("StockQuoteSoapBinding").find();
    
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

#### Spring example
Spring configuration:
```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

        <!-- wsdl part (locally referenced wsdl) -->
        <bean id="wsdlResource" class="org.springframework.core.io.ClassPathResource">
            <constructor-arg>
                <value>wsdl/currency-convertor.wsdl</value>
            </constructor-arg>
        </bean>

        <bean id="wsdlUrl" class="java.net.URL" factory-bean="wsdlResource" factory-method="getURL"/>

        <!-- uncomment to use the local wsdl" -->
        <!--<bean id="wsdl" class="org.reficio.ws.builder.core.Wsdl" factory-method="parse">-->
        <!--<constructor-arg ref="wsdlUrl"/>-->
        <!--</bean>-->

        <!-- wsdl part (remote wsdl) -->
        <bean id="wsdl" class="org.reficio.ws.builder.core.Wsdl" factory-method="parse">
            <constructor-arg>
                <value>http://www.webservicex.net/CurrencyConvertor.asmx?WSDL</value>
            </constructor-arg>
        </bean>


        <!-- soap-builder part -->
        <bean id="soapContext" class="org.reficio.ws.SoapContext">
            <constructor-arg name="exampleContent" type="boolean" value="true"/>
            <constructor-arg name="typeComments" type="boolean" value="true"/>
            <constructor-arg name="buildOptional" type="boolean" value="true"/>
            <constructor-arg name="alwaysBuildHeaders" type="boolean" value="true"/>
            <constructor-arg name="valueComments" type="boolean" value="true"/>
        </bean>

        <bean id="soapBuilder" class="org.reficio.ws.builder.SoapBuilder" factory-bean="wsdl" factory-method="getBuilder">
            <constructor-arg name="bindingName">
                <value>{http://www.webserviceX.NET/}CurrencyConvertorSoap</value>
            </constructor-arg>
            <constructor-arg name="context" ref="soapContext"/>
        </bean>

        <!-- security part -->
        <bean id="securityFactory" class="org.reficio.ws.client.core.SecurityFactory">
            <property name="authUsername" value="tom"/>
            <property name="authPassword" value="007"/>
            <property name="authMethod" value="basic"/>
            <property name="strictHostVerification" value="true"/>
            <property name="sslContextProtocol" value="SSLv3"/>
        </bean>

        <bean id="security" class="org.reficio.ws.client.core.Security" factory-bean="securityFactory" factory-method="create"/>

        <!-- soap-client part -->
        <bean id="soapClientFactory" class="org.reficio.ws.client.core.SoapClientFactory">
            <property name="endpointUri" value="http://localhost:8778/currencyConverter/soap"/>
            <property name="endpointSecurity" ref="security"/>
        </bean>

        <bean id="soapClient" class="org.reficio.ws.client.core.SoapClient" factory-bean="soapClientFactory" factory-method="create"/>


        <!-- soap-server part -->
        <bean id="autoResponder" class="org.reficio.ws.server.responder.AutoResponder">
            <constructor-arg ref="soapBuilder"/>
        </bean>

        <bean id="soapServerFactory" class="org.reficio.ws.server.core.SoapServerFactory">
            <property name="httpPort" value="8778"/>
            <property name="responders">
                <map>
                    <entry key="/currencyConverter/soap" value-ref="autoResponder"/>
                </map>
            </property>
        </bean>

        <bean id="soapServer" factory-bean="soapServerFactory" factory-method="create" init-method="start"/>
    </beans>
```

Then you can inject the beans to your code, for example in such a way:
```java
	@Autowired
    private SoapBuilder builder;

    @Autowired
    private SoapClient client;

    @Autowired
    private SoapServer server;
```


## Last but not least

### How can I hack around?
* GitHub -> https://github.com/reficio/soap-ws
* Jenkins -> https://reficio.ci.cloudbees.com/job/soap-ws/
* Site -> http://projects.reficio.org/soap-ws/1.0.0-SNAPSHOT/manual.html

### Who's behind it?
Tom Bujok [tom.bujok@gmail.com]

### History
This project was initially developed at centeractive ag for the purposes of the
open-sphere project. The library has been successfully incorporated into open-sphere
and that initial version could be found in the centeractive's repository at github.

Currently the project is developed and maintained solely by Tom Bujok at reficio.org

### Note
This project contains classes extracted from the soapUI code base by centeractive ag
in October 2011. They are located in the soap-legacy module. Every extracted class is
annotated with an comment to fulfill the requirements of the LGPL 2.1 license under
which soapUI is released. That is also the reason why soap-legacy module is
released under LGPL 2.1 license. All other soap-ws modules are released under Apache
v.2 license. The main reason behind the class extraction was to separate the code that
is responsible for the generation of the SOAP messages from the rest of the soapUI's
code that is tightly coupled with other modules, such as soapUI's graphical user
interface, etc. The goal was to create an open-source java project whose main
responsibility is to handle SOAP message generation and SOAP transmission purely on
an XML level.

centeractive ag would like to express strong appreciation to SmartBear Software and
to the whole team of soapUI's developers for creating soapUI and for releasing its
source code under a free and open-source license. centeractive ag extracted and
modified some parts of the soapUI's code in good faith, making every effort not
to impair any existing functionality and to supplement it according to our
requirements, applying best practices of software design.


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/reficio/soap-ws/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

