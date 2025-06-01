package com.delivery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.delivery.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {

    // Esta classe configura:
    // 1. Repositórios JPA na package com.delivery.repository
    // 2. JPA Auditing para campos createdAt e updatedAt
    // 3. Gerenciamento de transações

}