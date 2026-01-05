package com.library.library_system.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig {

    @Value("${spring.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.mongodb.database}")
    private String databaseName;

    @Bean
    @Primary
    public MongoClient mongoClient() {
        try {
            SSLContext sslContext = createSslContext();

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(mongoUri))
                    .applyToClusterSettings(cluster -> cluster.serverSelectionTimeout(30, TimeUnit.SECONDS))
                    .applyToSslSettings(ssl -> ssl
                            .enabled(true)
                            .invalidHostNameAllowed(false)
                            .context(sslContext))
                    .build();

            return MongoClients.create(settings);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to initialize MongoDB client with SSL", e);
        }
    }

    private SSLContext createSslContext() throws NoSuchAlgorithmException, KeyManagementException {
        try {
            SSLContext sc = SSLContext.getInstance("TLSv1.3");
            sc.init(null, null, null);
            return sc;
        } catch (NoSuchAlgorithmException ex) {
            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, null, null);
            return sc;
        }
    }

    @Bean
    @Primary
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, databaseName);
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}
