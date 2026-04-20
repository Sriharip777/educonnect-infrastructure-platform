package com.tcon.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.loadbalancer.core.RandomLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

// ✅ NO @Configuration here — must NOT be in root context
// ✅ CUSTOMERSUPPORTSERVICE added
// ✅ Only one ReactorLoadBalancer bean per config class
@LoadBalancerClients({
        @LoadBalancerClient(name = "auth-user-service",              configuration = LoadBalancerConfig.class),
        @LoadBalancerClient(name = "learning-management-service",    configuration = LoadBalancerConfig.class),
        @LoadBalancerClient(name = "communication-service",          configuration = LoadBalancerConfig.class),
        @LoadBalancerClient(name = "financial-service",              configuration = LoadBalancerConfig.class),
        @LoadBalancerClient(name = "notification-service",           configuration = LoadBalancerConfig.class),
        @LoadBalancerClient(name = "integration-service",            configuration = LoadBalancerConfig.class),
        @LoadBalancerClient(name = "content-service",                configuration = LoadBalancerConfig.class),
        @LoadBalancerClient(name = "CUSTOMERSUPPORTSERVICE",         configuration = LoadBalancerConfig.class)
})
@Slf4j
public class LoadBalancerConfig {

    /**
     * Round Robin Load Balancer — distributes requests evenly across all healthy instances.
     * This is the ONLY ReactorLoadBalancer bean in this config.
     * Spring Cloud will call this inside each service's own child context,
     * where PROPERTY_NAME resolves to the correct service name.
     */
    @Bean
    public ReactorLoadBalancer<ServiceInstance> roundRobinLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory) {

        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        log.info("🔄 Configuring Round Robin Load Balancer for service: {}", name);

        return new RoundRobinLoadBalancer(
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
                name
        );
    }
}