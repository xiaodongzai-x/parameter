package com.parameter.config;
import com.parameter.service.ParameterService;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.xml.ws.Endpoint;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName CxfConfig.java
 * @Description TODO
 * @createTime 2022Äê03ÔÂ14ÈÕ 15:10:00
 */

@Configuration
public class CxfConfig {

    @Autowired
    private ParameterService parameterService;

    @Bean
    public ServletRegistrationBean disServlet() {
        return new ServletRegistrationBean(new CXFServlet(),"/parameter/*");
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), parameterService);
        endpoint.publish("/api");
        return endpoint;
    }

}
