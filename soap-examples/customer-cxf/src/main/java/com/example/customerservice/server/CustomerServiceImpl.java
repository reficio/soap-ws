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
package com.example.customerservice.server;

import com.example.customerservice.*;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    
    /**
     * The WebServiceContext can be used to retrieve special attributes like the 
     * user principal. Normally it is not needed
     */
    @Resource
    WebServiceContext wsContext;

    public List<Customer> getCustomersByName(String name) throws NoSuchCustomerException {
        if ("None".equals(name)) {
            NoSuchCustomer noSuchCustomer = new NoSuchCustomer();
            noSuchCustomer.setCustomerName(name);
            throw new NoSuchCustomerException("Did not find any matching customer for name=" + name,
                                              noSuchCustomer);
        }

        List<Customer> customers = new ArrayList<Customer>();
        for (int c = 0; c < 2; c++) {
            Customer cust = new Customer();
            cust.setName(name);
            cust.getAddress().add("Pine Street 200");
            Date bDate = new GregorianCalendar(2009, 01, 01).getTime();
            cust.setBirthDate(bDate);
            cust.setNumOrders(1);
            cust.setRevenue(10000);
            cust.setTest(new BigDecimal(1.5));
            cust.setType(CustomerType.BUSINESS);
            customers.add(cust);
        }

        return customers;
    }

    public void updateCustomer(Customer customer) {
        System.out.println("update request was received");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // Nothing to do here
        }
        System.out.println("Customer was updated");
    }

}
