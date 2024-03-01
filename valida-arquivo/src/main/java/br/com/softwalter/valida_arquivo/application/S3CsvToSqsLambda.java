package br.com.softwalter.valida_arquivo.application;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class S3CsvToSqsLambda implements RequestHandler<S3Event, Void> {

    AmazonS3 amazonS3 = AmazonS3ClientBuilder
            .standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://s3.us-east-1.localhost.localstack.cloud:4566/", "us-east-1")) // localstack endpoint configuration
            .withCredentials(new DefaultAWSCredentialsProviderChain())
            .withPathStyleAccessEnabled(true) // Disable virtualhost style connection and enable path style s3 bucket
            .build();
    AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
    private final String sqsQueueUrl = "YOUR_SQS_QUEUE_URL";

    @Override
    public Void handleRequest(S3Event s3Event, Context context) {
        try {
            for (S3EventNotification.S3EventNotificationRecord record : s3Event.getRecords()) {
                String bucketName = record.getS3().getBucket().getName();
                String objectKey = record.getS3().getObject().getKey();


                BufferedReader reader = new BufferedReader(new InputStreamReader(amazonS3.getObject(bucketName, objectKey).getObjectContent()));

                List<Person> peopleList = new ArrayList<>();

                // Ler e processar o cabeçalho do CSV
                String header = reader.readLine();
                String[] headers = header.split(",");

                // Verificar se o cabeçalho contém os campos esperados
                if (headers.length != 6 || !headers[0].equals("ID") || !headers[1].equals("First Name") || !headers[2].equals("Last Name")
                        || !headers[3].equals("Email") || !headers[4].equals("Gender") || !headers[5].equals("IP Address")) {
                    System.err.println("Erro: Cabeçalho do CSV com formato inválido.");
                    return null;
                }

                // Processar as linhas do CSV
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    int id = Integer.parseInt(parts[0]);
                    String firstName = parts[1];
                    String lastName = parts[2];
                    String email = parts[3];
                    String gender = parts[4];
                    String ipAddress = parts[5];
                    Person person = new Person(id, firstName, lastName, email, gender, ipAddress);
                    peopleList.add(person);
                }

                // Enviar a lista de pessoas para a fila SQS
                AmazonSQS sqsClient = AmazonSQSClientBuilder.defaultClient();
                for (Person person : peopleList) {
                    String jsonPerson = person.toJsonString();
                    sendMessageToSQS(sqsClient, jsonPerson);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendMessageToSQS(AmazonSQS sqsClient, String messageBody) {
        SendMessageRequest request = new SendMessageRequest()
                .withQueueUrl(sqsQueueUrl)
                .withMessageBody(messageBody);
        sqsClient.sendMessage(request);
    }

    public static class Person {
        private int id;
        private String firstName;
        private String lastName;
        private String email;
        private String gender;
        private String ipAddress;

        public Person() {
        }

        public Person(int id, String firstName, String lastName, String email, String gender, String ipAddress) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.gender = gender;
            this.ipAddress = ipAddress;
        }

        // Getters e Setters

        public String toJsonString() {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsString(this);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}

