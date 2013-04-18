/**
 * Copyright (c) 2012-2013 Reficio (TM) - Reestablish your software!. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.reficio.ws.legacy;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.wsdl.Binding;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingOperation;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import java.util.*;


/**
 * Class for validating SOAP requests/responses against their definition and
 * schema, requires that the messages follow basic-profile requirements
 *
 * @author Ole.Matzura
 */

class WsdlValidator {
    private final static Logger log = Logger.getLogger(WsdlValidator.class);

    private final WsdlContext wsdlContext;

    WsdlValidator(SoapMessageBuilder builder, Binding binding) {
        this.wsdlContext = new WsdlContext(builder, binding);
    }

    List<AssertionError> assertRequest(BindingOperation bindingOperation, String message, boolean strict) {
        List<XmlError> errors = new ArrayList<XmlError>();
        try {
            validateXml(message, errors);
            wsdlContext.getSoapVersion().validateSoapEnvelope(message, errors);

            if (errors.isEmpty()) {
                if (bindingOperation == null) {
                    errors.add(XmlError.forMessage("Missing operation [" + bindingOperation.getName()
                            + "] in wsdl definition"));
                } else {
                    Part[] inputParts = WsdlUtils.getInputParts(bindingOperation);
                    validateMessage(message, bindingOperation, inputParts, errors, false, strict);

                    // ATTACHMENTS ARE SKIPPED FOR NOW
                    // validateInputAttachments(request, errors, bindingOperation,
                    // inputParts);
                }
            }
        } catch (Exception e) {
            errors.add(XmlError.forMessage(e.getMessage()));
        }
        return convertErrors(errors);
    }

    List<AssertionError> assertResponse(BindingOperation bindingOperation, String message, boolean strict) {
        List<XmlError> errors = new ArrayList<XmlError>();
        try {
            validateXml(message, errors);
            if (StringUtils.isBlank(message)) {
                if (!WsdlUtils.isOneWay(bindingOperation)) {
                    errors.add(XmlError.forMessage("Response is missing or empty"));
                }
            } else {
                wsdlContext.getSoapVersion().validateSoapEnvelope(message, errors);

                if (errors.isEmpty()) {
                    if (bindingOperation == null) {
                        errors.add(XmlError.forMessage("Missing operation [" + bindingOperation.getName()
                                + "] in wsdl definition"));
                    } else {
                        Part[] outputParts = WsdlUtils.getOutputParts(bindingOperation);
                        validateMessage(message, bindingOperation, outputParts, errors, true, strict);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors.add(XmlError.forMessage(e.getMessage()));
        }
        return convertErrors(errors);
    }

    // -----------------------------------------------
    // UGLY INTERNAL API
    // -----------------------------------------------
    @SuppressWarnings("unchecked")
    private void validateXml(String request, List<XmlError> errors) {
        try {
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setLoadLineNumbers();
            xmlOptions.setErrorListener(errors);
            xmlOptions.setLoadLineNumbers(XmlOptions.LOAD_LINE_NUMBERS_END_ELEMENT);
            XmlUtils.createXmlObject(request, xmlOptions);
        } catch (XmlException e) {
            if (e.getErrors() != null)
                errors.addAll(e.getErrors());
            errors.add(XmlError.forMessage(e.getMessage()));
        } catch (Exception e) {
            errors.add(XmlError.forMessage(e.getMessage()));
        }
    }

    private List<AssertionError> convertErrors(List<XmlError> errors) {
        if (errors.size() > 0) {
            List<AssertionError> response = new ArrayList<AssertionError>();
            for (Iterator<XmlError> i = errors.iterator(); i.hasNext(); ) {
                XmlError error = i.next();

                if (error instanceof XmlValidationError) {
                    XmlValidationError e = ((XmlValidationError) error);
                    QName offendingQName = e.getOffendingQName();
                    if (offendingQName != null) {
                        if (offendingQName.equals(new QName(wsdlContext.getSoapVersion().getEnvelopeNamespace(),
                                "encodingStyle"))) {
                            log.debug("ignoring encodingStyle validation..");
                            continue;
                        } else if (offendingQName.equals(new QName(wsdlContext.getSoapVersion().getEnvelopeNamespace(),
                                "mustUnderstand"))) {
                            log.debug("ignoring mustUnderstand validation..");
                            continue;
                        }
                    }
                }

                AssertionError assertionError = new AssertionError(error);
                if (!response.contains(assertionError))
                    response.add(assertionError);
            }

            return new ArrayList<AssertionError>(response);
        }
        return new ArrayList<AssertionError>();
    }

    @SuppressWarnings("unchecked")
    private void validateMessage(String message, BindingOperation bindingOperation,
                                 Part[] parts, List<XmlError> errors, boolean isResponse, boolean strict) {
        try {
            if (!wsdlContext.hasSchemaTypes()) {
                errors.add(XmlError.forMessage("Missing schema types for message"));
            } else {
                if (!WsdlUtils.isOutputSoapEncoded(bindingOperation)) {
                    XmlOptions xmlOptions = new XmlOptions();
                    xmlOptions.setLoadLineNumbers();
                    xmlOptions.setLoadLineNumbers(XmlOptions.LOAD_LINE_NUMBERS_END_ELEMENT);
                    XmlObject xml = XmlUtils.createXmlObject(message, xmlOptions);

                    XmlObject[] paths = xml.selectPath("declare namespace env='"
                            + wsdlContext.getSoapVersion().getEnvelopeNamespace() + "';"
                            + "$this/env:Envelope/env:Body/env:Fault");

                    if (paths.length > 0) {
                        validateSoapFault(bindingOperation, paths[0], errors);
                    } else if (WsdlUtils.isRpc(wsdlContext.getDefinition(), bindingOperation)) {
                        validateRpcLiteral(bindingOperation, parts, xml, errors, isResponse, strict);
                    } else {
                        validateDocLiteral(bindingOperation, parts, xml, errors, isResponse, strict);
                    }

                    // ATTACHMENTS SKIPPED FOR NOW
//					if( isResponse )
//						validateOutputAttachments( messageExchange, xml, errors, bindingOperation, parts );
//					else
//						validateInputAttachments( messageExchange, errors, bindingOperation, parts );
                } else {
                    errors.add(XmlError.forMessage("Validation of SOAP-Encoded messages not supported"));
                }
            }
        } catch (XmlException e) {
            if (e.getErrors() != null)
                errors.addAll(e.getErrors());
            errors.add(XmlError.forMessage(e.getMessage()));
        } catch (Exception e) {
            errors.add(XmlError.forMessage(e.getMessage()));
        }
    }

    private void validateDocLiteral(BindingOperation bindingOperation, Part[] parts, XmlObject msgXml,
                                    List<XmlError> errors, boolean isResponse, boolean strict) throws Exception {

        Part part = null;
        // start by finding body part
        for (int c = 0; c < parts.length; c++) {
            // content part?
            if ((isResponse && !WsdlUtils.isAttachmentOutputPart(parts[c], bindingOperation))
                    || (!isResponse && !WsdlUtils.isAttachmentInputPart(parts[c], bindingOperation))) {
                // already found?
                if (part != null) {
                    if (strict) {
                        errors.add(XmlError.forMessage("DocLiteral message must contain 1 body part definition"));
                    }
                    return;
                }
                part = parts[c];
            }
        }

        QName elementName = part.getElementName();
        if (elementName != null) {
            // just check for correct message element, other elements are avoided
            // (should create an error)
            XmlObject[] paths = msgXml.selectPath("declare namespace env='"
                    + wsdlContext.getSoapVersion().getEnvelopeNamespace() + "';" + "declare namespace ns='"
                    + elementName.getNamespaceURI() + "';" + "$this/env:Envelope/env:Body/ns:" + elementName.getLocalPart());

            if (paths.length == 1) {
                SchemaGlobalElement elm = wsdlContext.getSchemaTypeLoader().findElement(elementName);
                if (elm != null) {
                    validateMessageBody(errors, elm.getType(), paths[0]);

                    // ensure no other elements in body
                    NodeList children = XmlUtils.getChildElements((Element) paths[0].getDomNode().getParentNode());
                    for (int d = 0; d < children.getLength(); d++) {
                        QName childName = XmlUtils.getQName(children.item(d));
                        if (!elementName.equals(childName)) {
                            XmlCursor cur = paths[0].newCursor();
                            cur.toParent();
                            cur.toChild(childName);
                            errors.add(XmlError.forCursor("Invalid element [" + childName + "] in SOAP Body", cur));
                            cur.dispose();
                        }
                    }
                } else {
                    errors.add(XmlError.forMessage("Missing part type [" + elementName + "] in associated schema"));
                }
            } else {
                errors.add(XmlError.forMessage("Missing message part with name [" + elementName + "]"));
            }
        } else if (part.getTypeName() != null) {
            QName typeName = part.getTypeName();

            XmlObject[] paths = msgXml.selectPath("declare namespace env='"
                    + wsdlContext.getSoapVersion().getEnvelopeNamespace() + "';" + "declare namespace ns='"
                    + typeName.getNamespaceURI() + "';" + "$this/env:Envelope/env:Body/ns:" + part.getName());

            if (paths.length == 1) {
                SchemaType type = wsdlContext.getSchemaTypeLoader().findType(typeName);
                if (type != null) {
                    validateMessageBody(errors, type, paths[0]);
                    // XmlObject obj = paths[0].copy().changeType( type );
                    // obj.validate( new XmlOptions().setErrorListener( errors ));
                } else
                    errors.add(XmlError.forMessage("Missing part type in associated schema"));
            } else
                errors.add(XmlError.forMessage("Missing message part with name:type [" + part.getName() + ":" + typeName + "]"));
        }
    }

    private void validateMessageBody(List<XmlError> errors, SchemaType type, XmlObject msg) throws XmlException {
        // need to create new body element of correct type from xml text
        // since we want to retain line-numbers
        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setLoadLineNumbers();
        xmlOptions.setLoadLineNumbers(XmlOptions.LOAD_LINE_NUMBERS_END_ELEMENT);

        XmlCursor cur = msg.newCursor();
        Map<String, String> map = new HashMap<String, String>();

        while (cur.hasNextToken()) {
            if (cur.toNextToken().isNamespace())
                map.put(cur.getName().getLocalPart(), cur.getTextValue());
        }

        xmlOptions.setUseDefaultNamespace();
        xmlOptions.setSaveOuter();

        // problem: prefixes might get redefined/changed when saving which can
        // cause xsi:type refs to
        // reference wrong/non-existing namespace.. solution would probably be to
        // manually walk through document and
        // update xsi:type refs with new prefix. The setUseDefaultNamespace()
        // above helps here but is not a definitive fix

        String xmlText = msg.copy().changeType(type).xmlText(xmlOptions);

        xmlOptions.setLoadAdditionalNamespaces(map);

        XmlObject obj = type.getTypeSystem().parse(xmlText, type, xmlOptions);
        obj = obj.changeType(type);

        // create internal error list
        ArrayList<Object> list = new ArrayList<Object>();

        xmlOptions = new XmlOptions();
        xmlOptions.setErrorListener(list);
        xmlOptions.setValidateTreatLaxAsSkip();

        try {
            obj.validate(xmlOptions);
        } catch (Exception e) {
            log.error("Internal error", e);
            list.add("Internal Error - see error log for details - [" + e + "]");
        }

        // transfer errors for "real" line numbers
        for (int c = 0; c < list.size(); c++) {
            XmlError error = (XmlError) list.get(c);

            if (error instanceof XmlValidationError) {
                XmlValidationError validationError = ((XmlValidationError) error);

                if (wsdlContext.getSoapVersion().shouldIgnore(validationError))
                    continue;

                // ignore cid: related errors
                if (validationError.getErrorCode().equals("base64Binary")
                        || validationError.getErrorCode().equals("hexBinary")) {
                    XmlCursor cursor = validationError.getCursorLocation();
                    if (cursor.toParent()) {
                        String text = cursor.getTextValue();

                        // special handling for soapui/MTOM -> add option for
                        // disabling?
                        if (text.startsWith("cid:") || text.startsWith("file:")) {
                            // ignore
                            continue;
                        }
                    }
                }
            }

            int line = error.getLine() == -1 ? 0 : error.getLine() - 1;
            errors.add(XmlError.forLocation(error.getMessage(), error.getSourceName(), getLine(msg) + line,
                    error.getColumn(), error.getOffset()));
        }
    }

    private int getLine(XmlObject object) {
        List<?> list = new ArrayList<Object>();
        object.newCursor().getAllBookmarkRefs(list);
        for (int c = 0; c < list.size(); c++) {
            if (list.get(c) instanceof XmlLineNumber) {
                return ((XmlLineNumber) list.get(c)).getLine();
            }
        }

        return -1;
    }

    private void validateRpcLiteral(BindingOperation bindingOperation, Part[] parts, XmlObject msgXml,
                                    List<XmlError> errors, boolean isResponse, boolean strict) throws Exception {
        if (parts.length == 0)
            return;

        XmlObject[] bodyParts = getRpcBodyPart(bindingOperation, msgXml, isResponse);

        if (bodyParts.length != 1) {
            errors.add(XmlError.forMessage("Missing message wrapper element ["
                    + WsdlUtils.getTargetNamespace(wsdlContext.getDefinition()) + "@" + bindingOperation.getName()
                    + (isResponse ? "Response" : "")));
        } else {
            XmlObject wrapper = bodyParts[0];

            for (int i = 0; i < parts.length; i++) {
                Part part = parts[i];

                // skip attachment parts
                if (isResponse) {
                    if (WsdlUtils.isAttachmentOutputPart(part, bindingOperation))
                        continue;
                } else {
                    if (WsdlUtils.isAttachmentInputPart(part, bindingOperation))
                        continue;
                }

                // find part in message
                XmlObject[] children = wrapper.selectChildren(new QName(part.getName()));

                // not found?
                if (children.length != 1) {
                    // try element name (loophole in basic-profile spec?)
                    QName elementName = part.getElementName();
                    if (elementName != null) {
                        bodyParts = msgXml.selectPath("declare namespace env='"
                                + wsdlContext.getSoapVersion().getEnvelopeNamespace() + "';" + "declare namespace ns='"
                                + wsdlContext.getDefinition().getTargetNamespace() + "';" + "declare namespace ns2='"
                                + elementName.getNamespaceURI() + "';" + "$this/env:Envelope/env:Body/ns:"
                                + bindingOperation.getName() + (isResponse ? "Response" : "") + "/ns2:"
                                + elementName.getLocalPart());

                        if (bodyParts.length == 1) {
                            SchemaGlobalElement elm = wsdlContext.getSchemaTypeLoader().findElement(elementName);
                            if (elm != null) {
                                validateMessageBody(errors, elm.getType(), bodyParts[0]);
                            } else
                                errors.add(XmlError.forMessage("Missing part type in associated schema for [" + elementName
                                        + "]"));
                        } else
                            errors.add(XmlError.forMessage("Missing message part with name [" + elementName + "]"));
                    } else {
                        errors.add(XmlError.forMessage("Missing message part [" + part.getName() + "]"));
                    }
                } else {
                    QName typeName = part.getTypeName();
                    SchemaType type = wsdlContext.getSchemaTypeLoader().findType(typeName);
                    if (type != null) {
                        validateMessageBody(errors, type, children[0]);
                    } else {
                        errors.add(XmlError.forMessage("Missing type in associated schema for part [" + part.getName() + "]"));
                    }
                }
            }
        }
    }

    private XmlObject[] getRpcBodyPart(BindingOperation bindingOperation, XmlObject msgXml, boolean isResponse)
            throws Exception {
        // rpc requests should use the operation name as root element and soapbind
        // namespaceuri attribute as ns
        String ns = WsdlUtils.getSoapBodyNamespace(isResponse ? bindingOperation.getBindingOutput()
                .getExtensibilityElements() : bindingOperation.getBindingInput().getExtensibilityElements());

        if (ns == null || ns.trim().length() == 0)
            ns = WsdlUtils.getTargetNamespace(wsdlContext.getDefinition());

        // get root element
        XmlObject[] paths = msgXml.selectPath("declare namespace env='"
                + wsdlContext.getSoapVersion().getEnvelopeNamespace() + "';" + "declare namespace ns='" + ns + "';"
                + "$this/env:Envelope/env:Body/ns:" + bindingOperation.getName() + (isResponse ? "Response" : ""));
        return paths;
    }

    @SuppressWarnings("unchecked")
    private void validateSoapFault(BindingOperation bindingOperation, XmlObject msgXml, List<XmlError> errors)
            throws Exception {
        Map faults = bindingOperation.getBindingFaults();
        Iterator<BindingFault> i = faults.values().iterator();

        // create internal error list
        List<?> list = new ArrayList<Object>();

        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setErrorListener(list);
        xmlOptions.setValidateTreatLaxAsSkip();
        msgXml.validate(xmlOptions);

        for (Object o : list) {
            if (o instanceof XmlError)
                errors.add((XmlError) o);
            else
                errors.add(XmlError.forMessage(o.toString()));
        }

        while (i.hasNext()) {
            BindingFault bindingFault = i.next();
            String faultName = bindingFault.getName();

            Part[] faultParts = WsdlUtils.getFaultParts(bindingOperation, faultName);
            if (faultParts.length == 0) {
                log.warn("Missing fault parts in wsdl for fault [" + faultName + "] in bindingOperation ["
                        + bindingOperation.getName() + "]");
                continue;
            }

            if (faultParts.length != 1) {
                log.info("Too many fault parts in wsdl for fault [" + faultName + "] in bindingOperation ["
                        + bindingOperation.getName() + "]");
                continue;
            }

            Part part = faultParts[0];
            QName elementName = part.getElementName();

            if (elementName != null) {
                XmlObject[] paths = msgXml.selectPath("declare namespace env='"
                        + wsdlContext.getSoapVersion().getEnvelopeNamespace() + "'; declare namespace flt='"
                        + wsdlContext.getSoapVersion().getFaultDetailNamespace() + "';" + "declare namespace ns='"
                        + elementName.getNamespaceURI() + "';" + "//env:Fault/flt:detail/ns:" + elementName.getLocalPart());

                if (paths.length == 1) {
                    SchemaGlobalElement elm = wsdlContext.getSchemaTypeLoader().findElement(elementName);
                    if (elm != null) {
                        validateMessageBody(errors, elm.getType(), paths[0]);
                    } else
                        errors.add(XmlError.forMessage("Missing fault part element [" + elementName + "] for fault ["
                                + part.getName() + "] in associated schema"));

                    return;
                }
            }
            // this is not allowed by Basic Profile.. remove?
            else if (part.getTypeName() != null) {
                QName typeName = part.getTypeName();

                XmlObject[] paths = msgXml.selectPath("declare namespace env='"
                        + wsdlContext.getSoapVersion().getEnvelopeNamespace() + "'; declare namespace flt='"
                        + wsdlContext.getSoapVersion().getFaultDetailNamespace() + "';" + "declare namespace ns='"
                        + typeName.getNamespaceURI() + "';" + "//env:Fault/flt:detail/ns:" + part.getName());

                if (paths.length == 1) {
                    SchemaType type = wsdlContext.getSchemaTypeLoader().findType(typeName);
                    if (type != null) {
                        validateMessageBody(errors, type, paths[0]);
                    } else
                        errors.add(XmlError.forMessage("Missing fault part type [" + typeName + "] for fault ["
                                + part.getName() + "] in associated schema"));

                    return;
                }
            }
        }

        // if we get here, no matching fault was found.. this is not an error but
        // should be warned..
        XmlObject[] paths = msgXml.selectPath("declare namespace env='"
                + wsdlContext.getSoapVersion().getEnvelopeNamespace() + "'; declare namespace flt='"
                + wsdlContext.getSoapVersion().getFaultDetailNamespace() + "';//env:Fault/flt:detail");

        if (paths.length == 0)
            log.warn("Missing matching Fault in wsdl for bindingOperation [" + bindingOperation.getName() + "]");
        else {
            String xmlText = paths[0].xmlText(new XmlOptions().setSaveOuter());
            log.warn("Missing matching Fault in wsdl for Fault Detail element ["
                    + XmlUtils.removeUnneccessaryNamespaces(xmlText) + "] in bindingOperation ["
                    + bindingOperation.getName() + "]");
        }
    }


    // ATTACHMENTS ARE SKIPPED FOR NOW

//	private void validateInputAttachments( WsdlMessageExchange messageExchange, List<XmlError> errors,
//			BindingOperation bindingOperation, Part[] inputParts )
//	{
//		for( Part part : inputParts )
//		{
//			MIMEContent[] contents = WsdlUtils.getInputMultipartContent( part, bindingOperation );
//			if( contents.length == 0 )
//				continue;
//
//			Attachment[] attachments = messageExchange.getRequestAttachmentsForPart( part.getName() );
//			if( attachments.length == 0 )
//			{
//				errors.add( XmlError.forMessage( "Missing attachment for part [" + part.getName() + "]" ) );
//			}
//			else if( attachments.length == 1 )
//			{
//				Attachment attachment = attachments[0];
//				String types = "";
//				for( MIMEContent content : contents )
//				{
//					String type = content.getType();
//					if( type.equals( attachment.getContentType() ) || type.toUpperCase().startsWith( "MULTIPART" ) )
//					{
//						types = null;
//						break;
//					}
//					if( types.length() > 0 )
//						types += ",";
//
//					types += type;
//				}
//
//				if( types != null )
//				{
//					String msg = "Missing attachment for part [" + part.getName() + "] with content-type [" + types + "],"
//							+ " content type is [" + attachment.getContentType() + "]";
//
//					if(ALLOW_INCORRECT_CONTENT_TYPE)
//						log.warn( msg );
//					else
//						errors.add( XmlError.forMessage( msg ) );
//				}
//			}
//			else
//			{
//				String types = "";
//				for( MIMEContent content : contents )
//				{
//					String type = content.getType();
//					if( type.toUpperCase().startsWith( "MULTIPART" ) )
//					{
//						types = null;
//						break;
//					}
//					if( types.length() > 0 )
//						types += ",";
//
//					types += type;
//				}
//
//				if( types == null )
//				{
//					String msg = "Too many attachments for part [" + part.getName() + "] with content-type [" + types + "]";
//					if(ALLOW_INCORRECT_CONTENT_TYPE)
//						log.warn( msg );
//					else
//						errors.add( XmlError.forMessage( msg ) );
//				}
//			}
//
//			if( attachments.length > 0 )
//				validateAttachmentsReadability( errors, attachments );
//		}
//	}
//
//	private void validateOutputAttachments( WsdlMessageExchange messageExchange, XmlObject xml, List<XmlError> errors,
//			BindingOperation bindingOperation, Part[] outputParts ) throws Exception
//	{
//		for( Part part : outputParts )
//		{
//			MIMEContent[] contents = WsdlUtils.getOutputMultipartContent( part, bindingOperation );
//			if( contents.length == 0 )
//				continue;
//
//			Attachment[] attachments = messageExchange.getResponseAttachmentsForPart( part.getName() );
//
//			// check for rpc
//			if( attachments.length == 0 && WsdlUtils.isRpc( wsdlContext.getDefinition(), bindingOperation ) )
//			{
//				XmlObject[] rpcBodyPart = getRpcBodyPart( bindingOperation, xml, true );
//				if( rpcBodyPart.length == 1 )
//				{
//					XmlObject[] children = rpcBodyPart[0].selectChildren( new QName( part.getName() ) );
//					if( children.length == 1 )
//					{
//						String href = ( ( Element )children[0].getDomNode() ).getAttribute( "href" );
//						if( href != null )
//						{
//							if( href.startsWith( "cid:" ) )
//								href = href.substring( 4 );
//
//							attachments = messageExchange.getResponseAttachmentsForPart( href );
//						}
//					}
//				}
//			}
//
//			if( attachments.length == 0 )
//			{
//				errors.add( XmlError.forMessage( "Missing attachment for part [" + part.getName() + "]" ) );
//			}
//			else if( attachments.length == 1 )
//			{
//				Attachment attachment = attachments[0];
//				String types = "";
//				for( MIMEContent content : contents )
//				{
//					String type = content.getType();
//					if( type.equals( attachment.getContentType() ) || type.toUpperCase().startsWith( "MULTIPART" ) )
//					{
//						types = null;
//						break;
//					}
//
//					if( types.length() > 0 )
//						types += ",";
//
//					types += type;
//				}
//
//				if( types != null )
//				{
//					String msg = "Missing attachment for part [" + part.getName() + "] with content-type [" + types
//							+ "], content type is [" + attachment.getContentType() + "]";
//
//					if(ALLOW_INCORRECT_CONTENT_TYPE)
//						log.warn( msg );
//					else
//						errors.add( XmlError.forMessage( msg ) );
//				}
//			}
//			else
//			{
//				String types = "";
//				for( MIMEContent content : contents )
//				{
//					String type = content.getType();
//					if( type.toUpperCase().startsWith( "MULTIPART" ) )
//					{
//						types = null;
//						break;
//					}
//
//					if( types.length() > 0 )
//						types += ",";
//
//					types += type;
//				}
//
//				if( types != null )
//				{
//					String msg = "Too many attachments for part [" + part.getName() + "] with content-type [" + types + "]";
//
//					if(ALLOW_INCORRECT_CONTENT_TYPE)
//						log.warn( msg );
//					else
//						errors.add( XmlError.forMessage( msg ) );
//				}
//			}
//
//			if( attachments.length > 0 )
//				validateAttachmentsReadability( errors, attachments );
//		}
//	}
//
//	private void validateAttachmentsReadability( List<XmlError> errors, Attachment[] attachments )
//	{
//		for( Attachment attachment : attachments )
//		{
//			try
//			{
//				attachment.getInputStream();
//			}
//			catch( Exception e )
//			{
//				errors.add( XmlError.forMessage( e.toString() ) );
//			}
//		}
//	}


}
