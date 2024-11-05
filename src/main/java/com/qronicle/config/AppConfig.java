package com.qronicle.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;


// Configuration settings for the MVC side of the web application, including where to find web pages & settings
// for database transactions
@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan("com.qronicle")
@PropertySource({"classpath:application.properties"})
public class AppConfig implements WebMvcConfigurer {
    private final Environment env;

    public AppConfig(Environment env) {
        this.env = env;
    }

    // Sets up the DataSource object with values from the properties file
    @Bean
    public DataSource dataSource() {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();

        // connection
        dataSource.setInitialPoolSize(propToInt(env.getProperty("connection.initialPoolSize")));
        dataSource.setMinPoolSize(propToInt(env.getProperty("connection.minPoolSize")));
        dataSource.setMaxPoolSize(propToInt(env.getProperty("connection.maxPoolSize")));
        dataSource.setMaxIdleTime(propToInt(env.getProperty("connection.maxIdleTime")));

        // jdbc
        try {
            dataSource.setDriverClass(env.getProperty("jdbc.driver"));
        } catch (PropertyVetoException e) {
        }
        dataSource.setJdbcUrl(env.getProperty("jdbc.url"));
        dataSource.setUser(env.getProperty("jdbc.user"));
        dataSource.setPassword(env.getProperty("jdbc.password"));

        return dataSource;
    }

    @Bean
    public AmazonS3 s3Client() {
        String accessKey = env.getProperty("aws.credentials.key.access");
        String secretKey = env.getProperty("aws.credentials.key.secret");
        String region = env.getProperty("aws.bucket.region");
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region).build();
    }

    // Configuration for Hibernate using values from the properties file
    @Bean
    public Properties getHibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        properties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));

        return properties;
    }

    // Configuration for the SessionFactory object based on values in properties file
    @Bean
    LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

        sessionFactory.setHibernateProperties(getHibernateProperties());
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(env.getProperty("hibernate.packagesToScan"));

        return sessionFactory;
    }

    // Assigns the above SessionFactory to the HibernateTransactionManager for database queries
    @Bean
    @Autowired
    public HibernateTransactionManager txManager(SessionFactory sessionFactory) {
        HibernateTransactionManager manager = new HibernateTransactionManager();
        manager.setSessionFactory(sessionFactory);

        return manager;
    }

    // allows for limiting file/ request size
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        int maxFileSize = propToInt(env.getProperty("app.upload.image.size"));
        int maxRequestSize = propToInt(env.getProperty("app.upload.image.max")) * maxFileSize;
        resolver.setMaxUploadSize(maxRequestSize);     // limit 20MB for total request upload
        resolver.setMaxUploadSizePerFile(maxFileSize);  // limit 2MB per file
        return resolver;
    }

    // allows for RequestParam/ RequestPart validation in controller method parameters
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/view/");
        resolver.setSuffix(".jsp");

        return resolver;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(env.getProperty("app.mail.host"));
        mailSender.setPort(propToInt(env.getProperty("app.mail.port")));
        mailSender.setUsername(env.getProperty("app.mail.sender.username"));
        mailSender.setPassword(env.getProperty("app.mail.sender.password"));

        Properties mailProperties = mailSender.getJavaMailProperties();
        mailProperties.put("mail.transport.protocol", "smtp");
        mailProperties.put("mail.smtp.auth", "true");
        mailProperties.put("mail.smtp.starttls.enable", "true");
        mailProperties.put("mail.debug", "true");

        return mailSender;
    }

    // Identifies locations for CSS and JS resources
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/*")
                .addResourceLocations("/css/");
    }

    // Helper to convert Strings to ints
    private int propToInt(String prop) {
        return Integer.parseInt(prop);
    }
}
