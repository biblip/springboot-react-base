 package com.example.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
 import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
 import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
 import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import org.springframework.boot.CommandLineRunner;
 import software.amazon.awssdk.services.dynamodb.model.*;

 @Configuration
 public class DynamoDbConfig {

     @Value("${aws.dynamodb.endpoint:http://localhost:4566}")
     private String dynamoEndpoint;

     @Value("${aws.region:us-west-2}")
     private String region;

     @Value("${aws.accessKeyId:dummy}")
     private String accessKeyId;

     @Value("${aws.secretAccessKey:dummy}")
     private String secretAccessKey;

     @Bean
     public DynamoDbClient dynamoDbClient() {
         return DynamoDbClient.builder()
                 .endpointOverride(URI.create(dynamoEndpoint))
                 .credentialsProvider(StaticCredentialsProvider.create(
                         AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                 .region(Region.of(region))
                 .httpClientBuilder(UrlConnectionHttpClient.builder())
                 .build();
     }
    
    @Bean
    public CommandLineRunner dynamoDbInitializer(DynamoDbClient dynamoDbClient) {
        return args -> {
            String tableName = "MyTable";
            try {
                ListTablesResponse tables = dynamoDbClient.listTables();
                if (!tables.tableNames().contains(tableName)) {
                    CreateTableRequest request = CreateTableRequest.builder()
                            .tableName(tableName)
                            .attributeDefinitions(AttributeDefinition.builder()
                                    .attributeName("id")
                                    .attributeType(ScalarAttributeType.S)
                                    .build())
                            .keySchema(KeySchemaElement.builder()
                                    .attributeName("id")
                                    .keyType(KeyType.HASH)
                                    .build())
                            .provisionedThroughput(ProvisionedThroughput.builder()
                                    .readCapacityUnits(5L)
                                    .writeCapacityUnits(5L)
                                    .build())
                            .build();
                    dynamoDbClient.createTable(request);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    // Removed legacy PostConstruct table initialization; using CommandLineRunner bean instead
 }