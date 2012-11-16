/**
 * Copyright (c) 2012 centeractive ag. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.customerservice

import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

class MessageDecorator {

    public static void main(String[] args) {
        String message = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cus="http://customerservice.example.com/">
               <soapenv:Header/>
               <soapenv:Body>
                  <cus:updateCustomer>
                     <customer>
                        <customerId>1</customerId>
                        <name>Tom Bujok</name>
                        <address>jDays Gothenburg</address>
                        <numOrders>2</numOrders>
                        <revenue>300</revenue>
                        <test>0</test>
                        <birthDate>10.10.2010</birthDate>
                        <type>VIP</type>
                     </customer>
                  </cus:updateCustomer>
               </soapenv:Body>
            </soapenv:Envelope>"""


        def request = new XmlSlurper().parseText(message)
        def customer = request.Body.updateCustomer.customer
        customer.customerId = "007"
        customer.type = "ORDINARY"




        println toPrettyXml(request)
    }

    def static toPrettyXml(xml) {
        XmlUtil.serialize(new StreamingMarkupBuilder().bind { mkp.yield xml })
    }
    def static toXml(xml) {
        new StreamingMarkupBuilder().bind { mkp.yield xml }
    }

}

