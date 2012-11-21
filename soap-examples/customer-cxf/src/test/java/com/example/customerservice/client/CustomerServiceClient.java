/**
 * Copyright (c) 2012 centeractive ag. All Rights Reserved.
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
package com.example.customerservice.client;

import com.example.customerservice.CustomerService;
import com.example.customerservice.CustomerServiceService;
import org.springframework.util.StopWatch;

import java.io.File;
import java.net.URL;

public class CustomerServiceClient {
    protected CustomerServiceClient() {
    }

    public static void main(String args[]) throws Exception {
        CustomerServiceService customerServiceService;
        if (args.length != 0 && args[0].length() != 0) {
            File wsdlFile = new File(args[0]);
            URL wsdlURL;
            if (wsdlFile.exists()) {
                wsdlURL = wsdlFile.toURI().toURL();
            } else {
                wsdlURL = new URL(args[0]);
            }
            // Create the service client with specified wsdlurl
            customerServiceService = new CustomerServiceService(wsdlURL);
        } else {
            // Create the service client with its default wsdlurl
            customerServiceService = new CustomerServiceService();
        }

        CustomerService customerService = customerServiceService.getCustomerServicePort();

        // Initialize the test class and call the tests
//        CustomerServiceTester client = new CustomerServiceTester();
//        client.setCustomerService(customerService);
//        client.testCustomerService();


        StopWatch watch = new StopWatch();
        watch.start();
        for (int i = 0; i < 10000; i++) {
            customerService.getCustomersByName("Smith");
        }
        watch.stop();
        System.out.println(watch.toString());

        System.exit(0);
    }
}
