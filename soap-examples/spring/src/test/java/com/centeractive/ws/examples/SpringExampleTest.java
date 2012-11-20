package com.centeractive.ws.examples;

import com.centeractive.ws.builder.SoapBuilder;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SpringExampleTest {

    private final static Logger log = Logger.getLogger(SpringExampleTest.class);

    @Autowired
    private SoapBuilder builder;

    @Test
    public void test() {
        log.info(builder.buildEmptyFault());
    }

}
