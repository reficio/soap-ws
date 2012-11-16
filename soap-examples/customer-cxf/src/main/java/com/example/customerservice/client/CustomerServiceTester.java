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

import java.util.List;

import junit.framework.Assert;

import com.example.customerservice.Customer;
import com.example.customerservice.CustomerService;
import com.example.customerservice.NoSuchCustomerException;

public final class CustomerServiceTester {
    
    // The CustomerService proxy will be injected either by spring or by a direct call to the setter 
    CustomerService customerService;
    
    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void testCustomerService() throws NoSuchCustomerException {
        List<Customer> customers = null;
        
        // First we test the positive case where customers are found and we retreive
        // a list of customers
        System.out.println("Sending request for customers named Smith");
        customers = customerService.getCustomersByName("Smith");
        System.out.println("Response received");
        Assert.assertEquals(2, customers.size());
        Assert.assertEquals("Smith", customers.get(0).getName());
        
        // Then we test for an unknown Customer name and expect the NoSuchCustomerException
        try {
            customers = customerService.getCustomersByName("None");
            Assert.fail("We should get a NoSuchCustomerException here");
        } catch (NoSuchCustomerException e) {
            System.out.println(e.getMessage());
            Assert.assertNotNull("FaultInfo must not be null", e.getFaultInfo());
            Assert.assertEquals("None", e.getFaultInfo().getCustomerName());
            System.out.println("NoSuchCustomer exception was received as expected");
        }
        
        // The implementation of updateCustomer is set to sleep for some seconds. 
        // Still this method should return instantly as the method is declared
        // as a one way method in the WSDL
        Customer customer = new Customer();
        customer.setName("Smith");
        customerService.updateCustomer(customer);
        
        System.out.println("All calls were successful");
    }

}
