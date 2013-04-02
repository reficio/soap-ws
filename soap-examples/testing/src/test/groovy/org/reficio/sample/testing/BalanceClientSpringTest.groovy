/**
 * Copyright (c) 2012-2013 Reficio (TM) - Reestablish your software!. All Rights Reserved.
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
package org.reficio.sample.testing

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.reficio.sample.BankService
import org.reficio.ws.builder.SoapBuilder
import org.reficio.ws.builder.SoapOperation
import org.reficio.ws.client.core.SoapClient
import org.reficio.ws.server.core.SoapServer
import org.reficio.ws.server.responder.AbstractResponder
import org.reficio.ws.server.responder.RequestResponder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.ws.soap.SoapMessage

import javax.xml.transform.Source

import static org.junit.Assert.assertTrue
import static org.reficio.sample.util.ExampleUtils.toPrettyXml
import static org.reficio.ws.common.XmlUtils.xmlStringToSource

/**
 * @author: Tom Bujok (tom.bujok@gmail.com)
 *
 * Reficio™ - Reestablish your software!
 * www.reficio.org
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
class BalanceClientSpringTest {

    @Autowired
    private BankService balanceService

    @Autowired
    private SoapBuilder builder;

    @Autowired
    private SoapClient client;

    @Autowired
    private SoapServer server;

    @Before
    public void before() {
        RequestResponder responder = new AbstractResponder(builder) {
            @Override
            public Source respond(SoapOperation invokedOperation, SoapMessage message) {
                String response = builder.buildOutputMessage(invokedOperation)

                def slurper = new XmlSlurper().parseText(response)
                slurper.Body.ConversionRateResponse.ConversionRateResult = 2.0
                response = toPrettyXml(slurper)

                // println response
                return xmlStringToSource(response)
            }
        }
        server.registerRequestResponder("/service", responder)

    }

    @After
    public void cleanup() {
        server.stop();
    }

    @Test
    void getBeerExpensesInEuro() {
        int expenses = balanceService.getExpenses("CH01-1231-4141-2386", "BEER", "EUR")
        // println "Beer expenses = " + expenses + " EUR"
        assertTrue(expenses < 700)
    }


}
