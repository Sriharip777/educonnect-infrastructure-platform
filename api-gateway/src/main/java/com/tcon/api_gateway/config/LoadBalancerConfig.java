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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@LoadBalancerClients({
        @LoadBalancerClient(name = "auth-user-service", configuration = LoadBalancerConfig.class),
        @LoadBalancerClient(name = "learning-management-service", configuration = LoadBalancerConfig.class),
        @LoadBalancerClient(name = "communication-service", configuration = LoadBalancerConfig.class),
        @LoadBalancerClient(name = "financial-service", configuration = LoadBalancerConfig.class),
        @LoadBalancerClient(name = "notification-service", configuration = LoadBalancerConfig.class),
        @LoadBalancerClient(name = "integration-service", configuration = LoadBalancerConfig.class),
        @LoadBalancerClient(name = "content-service", configuration = LoadBalancerConfig.class)
})
public class LoadBalancerConfig {

    /**
     * Primary Load Balancer - Round Robin (Default Strategy)
     * Distributes requests evenly across all healthy instances
     */
    @Primary
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

    /**
     * Alternative Load Balancer - Random (Backup Strategy)
     * Can be activated by configuration if needed
     */
    @Bean
    public ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory) {

        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        log.debug("🎲 Random Load Balancer configured for service: {}", name);

        return new RandomLoadBalancer(
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
                name
        );
    }
}
