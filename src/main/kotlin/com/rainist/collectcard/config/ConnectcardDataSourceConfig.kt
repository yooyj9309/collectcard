package com.rainist.collectcard.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "collectCardEntityManagerFactory",
        transactionManagerRef = "collectCardTransactionManager",
        basePackages = ["com.rainist.collectcard.db.collectcard.repository"]
)
class CollectCardDataSourceConfig {
    @Primary
    @Bean(name = ["collectCardProperties"])
    @ConfigurationProperties("collectcard.datasource")
    fun dataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Primary
    @Bean(name = ["collectCardDataSource"])
    @ConfigurationProperties("collectcard.datasource.hikari")
    fun dataSource(@Qualifier("collectCardProperties") collectCardProperties: DataSourceProperties): DataSource {
        return collectCardProperties
                .initializeDataSourceBuilder()
                .type(HikariDataSource::class.java)
                .build()
    }

    @Primary
    @Bean(name = ["collectCardEntityManagerFactory"])
    fun entityManagerFactory(builder: EntityManagerFactoryBuilder, @Qualifier("collectCardDataSource") dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        return builder
                .dataSource(dataSource)
                .packages("com.rainist.collectcard.db.collectcard.entity")
                .persistenceUnit("collectcard")
                .properties(jpaProperties())
                .build()
    }

    @Primary
    @Bean(name = ["collectCardTransactionManager"])
    fun transactionManager(@Qualifier("collectCardEntityManagerFactory") entityManagerFactory: EntityManagerFactory): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }

    protected fun jpaProperties(): Map<String, Any> {
        val props = HashMap<String, Any>()
        props["hibernate.physical_naming_strategy"] = SpringPhysicalNamingStrategy::class.java.name
        props["hibernate.implicit_naming_strategy"] = SpringImplicitNamingStrategy::class.java.name
        return props
    }
}
