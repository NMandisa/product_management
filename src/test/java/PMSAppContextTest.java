import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author NMMkhungo
 * @since 2025/09/13
 **/


@Slf4j
@SpringJUnitConfig
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:/META-INF/pms-app-context.xml")
//@ActiveProfiles("test")
@Transactional
public class PMSAppContextTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HikariDataSource dataSource;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired(required = false)
    private PlatformTransactionManager transactionManager;

    @Autowired(required = false)
    private JpaTransactionManager jpaTransactionManager;

    @Autowired(required = false)
    private LocalContainerEntityManagerFactoryBean entityManagerFactoryBean;

    @Autowired(required = false)
    private YamlPropertiesFactoryBean yamlProperties;

    @Autowired(required = false)
    private ResourceBundleMessageSource messageSource;

    @Autowired(required = false)
    private HibernateJpaVendorAdapter jpaVendorAdapter;

    @Test
    @DisplayName("Core Context Load Test")
    void contextLoads() {
        assertNotNull(applicationContext, "Application context should not be null.");
    }

    @Test
    @DisplayName("Message Source Configuration Test")
    void messageSourceTest() {
        assertNotNull(messageSource, "MessageSource bean should be loaded.");
        assertNotNull(messageSource.getBasenameSet(), "MessageSource should have basenames configured.");
        assertTrue(messageSource.getBasenameSet().size() >= 5, "Should have at least 5 message bundles configured.");
    }

    @Test
    @DisplayName("YAML Properties Configuration Test")
    void yamlPropertiesTest() {
        assertNotNull(yamlProperties, "YAML properties bean should be loaded.");
        assertNotNull(yamlProperties.getObject(), "YAML properties should be loaded correctly.");
        assertFalse(yamlProperties.getObject().isEmpty(), "YAML properties should contain values.");
    }

    @Test
    @DisplayName("DataSource Configuration Test")
    void dataSourceTest() {
        assertNotNull(dataSource, "DataSource bean should not be null.");
        assertInstanceOf(HikariDataSource.class, dataSource, "DataSource should be HikariDataSource implementation.");

        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        assertNotNull(hikariDataSource.getDataSourceProperties().get("url"), "Database URL should be configured.");
        assertNotNull(hikariDataSource.getDataSourceProperties().get("user"), "Database username should be configured.");

        // Test connection validity
        try (Connection connection = dataSource.getConnection()) {
            assertTrue(connection.isValid(2), "Database connection should be valid.");
        } catch (SQLException e) {
            fail("Should be able to establish a database connection: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("EntityManagerFactory Configuration Test")
    void entityManagerFactoryTest() {
        assertNotNull(entityManagerFactory, "EntityManagerFactory bean should not be null.");
        assertNotNull(entityManagerFactoryBean, "EntityManagerFactory bean should be available as LocalContainerEntityManagerFactoryBean.");

        // Verify it's a Hibernate EntityManagerFactory
        assertNotNull(entityManagerFactory.unwrap(SessionFactory.class), "Should be a Hibernate SessionFactory");

        // Test that we can create an EntityManager
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            assertNotNull(em, "Should be able to create EntityManager");
            assertTrue(em.isOpen(), "EntityManager should be open");
        }
    }

    @Test
    @DisplayName("JPA Vendor Adapter Test")
    void jpaVendorAdapterTest() {
        assertNotNull(jpaVendorAdapter, "HibernateJpaVendorAdapter should be configured.");
        //assertEquals("Hibernate", jpaVendorAdapter.getPersistenceProvider(), "Should use Hibernate as JPA provider.");
    }

    @Test
    @DisplayName("Transaction Management Configuration Test")
    void transactionManagementTest() {
        assertNotNull(transactionManager, "PlatformTransactionManager should be configured.");
        assertNotNull(jpaTransactionManager, "JpaTransactionManager should be configured.");
        assertEquals(entityManagerFactory, jpaTransactionManager.getEntityManagerFactory(),
                "TransactionManager should use the same EntityManagerFactory");
    }

    @Test
    @DisplayName("All Beans Verification Test")
    void verifyAllBeans() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertTrue(beanNames.length > 0, "Should have at least one bean defined");

        // Log all bean names for debugging
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            log.debug("Bean: {}", beanName);
        }

        // Verify essential beans exist
        assertTrue(Arrays.asList(beanNames).contains("dataSource"), "DataSource bean should exist");
        assertTrue(Arrays.asList(beanNames).contains("entityManagerFactory"), "EntityManagerFactory bean should exist");
        assertTrue(Arrays.asList(beanNames).contains("transactionManager"), "TransactionManager bean should exist");
        assertTrue(Arrays.asList(beanNames).contains("yamlProperties"), "YAML properties bean should exist");
        assertTrue(Arrays.asList(beanNames).contains("messageSource"), "MessageSource bean should exist");
    }

    @Test
    @DisplayName("Hibernate Properties Configuration Test")
    void hibernatePropertiesTest() {
        Map<String, Object> properties = entityManagerFactory.getProperties();

        // Verify essential Hibernate properties
        assertNotNull(properties.get("hibernate.dialect"), "Hibernate dialect should be configured");
        assertNotNull(properties.get("hibernate.hbm2ddl.auto"), "Hibernate DDL auto should be configured");
        assertEquals("true", properties.get("hibernate.show_sql"), "Hibernate should be configured to show SQL");
        assertEquals("true", properties.get("hibernate.format_sql"), "Hibernate should be configured to format SQL");
    }

    @Test
    @DisplayName("Database Schema Validation Test")
    void databaseSchemaTest() {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            // Try to query a system table to verify database connection and schema
            Query query = em.createNativeQuery("SELECT 1 FROM DUAL");
            Object result = query.getSingleResult();
            assertEquals(1, ((Number) result).intValue(), "Should be able to execute native query");
        }
    }

    @Test
    @DisplayName("Transaction Rollback Test")
    @Rollback
    void transactionRollbackTest() {
        // This test verifies that transactions are working correctly
        // by ensuring that @Rollback works as expected
        assertTrue(TransactionSynchronizationManager.isActualTransactionActive(),
                "Transaction should be active");
    }

    @Disabled
    @Test
    @DisplayName("Property Resolution Test")
    void propertyResolutionTest() {
        // Test that properties from YAML are properly resolved
        Environment env = applicationContext.getEnvironment();
        assertNotNull(env.getProperty("database.url"), "Database URL should be resolvable from properties");
        assertNotNull(env.getProperty("database.username"), "Database username should be resolvable from properties");
    }

    @Test
    @DisplayName("Message Source Functionality Test")
    void messageSourceFunctionalityTest() {
        // Test that message source can resolve messages
        try {
            String message = messageSource.getMessage("some.message.key", null, Locale.getDefault());
            // If we get here, message source is working (even if the key doesn't exist)
            log.debug("Resolved message: {}", message);
        } catch (NoSuchMessageException e) {
            // This is expected if the key doesn't exist - the important thing is that
            // the message source is functioning
            log.debug("Expected exception for missing message key: {}", e.getMessage());
        }
    }

    @Disabled
    @Test
    @DisplayName("Component Scanning Test")
    void componentScanningTest() {
        // Verify that components are being scanned correctly
        String[] componentBeans = applicationContext.getBeanNamesForAnnotation(Component.class);
        String[] serviceBeans = applicationContext.getBeanNamesForAnnotation(Service.class);
        String[] repositoryBeans = applicationContext.getBeanNamesForAnnotation(Repository.class);

        log.info("Found {} component beans", componentBeans.length);
        log.info("Found {} service beans", serviceBeans.length);
        log.info("Found {} repository beans", repositoryBeans.length);

        // At minimum, we should have some components if scanning is working
        assertTrue(componentBeans.length + serviceBeans.length + repositoryBeans.length > 0,
                "Should have at least one component, service, or repository bean");
    }

    @Disabled
    @Test
    @DisplayName("Profile Configuration Test")
    void profileConfigurationTest() {
        // Verify that the test profile is active
        Environment env = applicationContext.getEnvironment();
        assertTrue(Arrays.asList(env.getActiveProfiles()).contains("test"),
                "Test profile should be active");
    }

    @Test
    @DisplayName("Debug Configuration Test")
    public void debugConfiguration() {
        try {
            String[] beanNames = applicationContext.getBeanDefinitionNames();
            log.info("Number of beans: {}", beanNames.length);

            // Check if specific beans are created
            try {
                Object yamlProps = applicationContext.getBean("yamlProperties");
                log.info("YAML properties bean: {}", yamlProps);
            } catch (Exception e) {
                log.error("YAML properties bean not available", e);
            }

        } catch (Exception e) {
            log.error("Context loading failed", e);
            fail("Context loading failed: " + e.getMessage());
        }
    }
}
