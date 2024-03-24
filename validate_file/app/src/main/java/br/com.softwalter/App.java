
package br.com.softwalter;

import br.com.softwalter.entity.Pessoa;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App implements RequestHandler<S3Event, Void> {

    private static final Log LOG = LogFactory.getLog(App.class);
    private final AmazonSQS sqsClient = AmazonSQSClientBuilder.defaultClient();
    private final String queueUrl = "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/my-test-queue";

    @Override
    public Void handleRequest(S3Event s3Event, Context context) {
        try {
            for (S3Event.S3EventNotificationRecord record : s3Event.getRecords()) {

                String bucketName = record.getS3().getBucket().getName();
                String objectKey = record.getS3().getObject().getKey();

                List<Pessoa> pessoas = getObjectContent(bucketName, objectKey);

                sendToSQS(pessoas);


            }
        } catch (IOException e) {
            LOG.error("Erro ao processar evento S3 ou no envio SQS: " + e.getMessage());
        }
        return null;
    }

    private List<Pessoa> getObjectContent(String bucketName, String objectKey) throws IOException {

        // Criar um cliente Amazon S3
        AmazonS3 s3Client = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:4566/", "us-east-1")) // localstack endpoint configuration
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withPathStyleAccessEnabled(true) // Disable virtualhost style connection and enable path style s3 bucket
                .build();

        try {
            S3Object object = s3Client.getObject(bucketName, objectKey);
            InputStream objectData = object.getObjectContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(objectData));

            if (!object.getKey().toLowerCase().endsWith(".csv")) {
                throw new IllegalArgumentException("O arquivo não é um arquivo CSV.");
            }

            String headerLine = reader.readLine();
            String[] columns = headerLine.split(","); // Ou qualquer outro separador que seu CSV use

            if (columns.length != 6 ||
                    !columns[0].equals("id") ||
                    !columns[1].equals("first_name") ||
                    !columns[2].equals("last_name") ||
                    !columns[3].equals("email") ||
                    !columns[4].equals("gender") ||
                    !columns[5].equals("ip_address")) {
                throw new IllegalArgumentException("O arquivo CSV não tem o formato esperado.");
            }

            String line;
            List<Pessoa> pessoas = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); // Ou qualquer outro separador que seu CSV use
                if (parts.length == 6) { // Certifique-se de que a linha tenha todos os campos necessários
                    String id = parts[0];
                    String first_name = parts[1];
                    String last_name = parts[2];
                    String email = parts[3];
                    String gender = parts[4];
                    String ip_address = parts[5];

                    Pessoa pessoa = new Pessoa(id, first_name, last_name, email, gender, ip_address);
                    pessoas.add(pessoa);
                } else {
                    System.out.println("Ignorando linha inválida: " + line);
                }
            }
            reader.close();

            return pessoas;
        } catch (AmazonServiceException e) {
            throw new IOException("Erro ao obter objeto do S3: " + e.getMessage());
        }
        finally {
            if (s3Client != null) {
                s3Client.shutdown();
            }
        }
    }

    private void sendToSQS(List<Pessoa> pessoas) throws JsonProcessingException {
        for (Pessoa pessoa : pessoas) {
            String messageBody = pessoa.toString(); // Ou como você deseja formatar a mensagem

            SendMessageRequest sendMessageRequest = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(messageBody);

            // Envie a mensagem para a fila SQS
            SendMessageResult sendMessageResult = sqsClient.sendMessage(sendMessageRequest);
            System.out.println("Mensagem enviada com sucesso. ID da mensagem: " + sendMessageResult.getMessageId());
        }

// Feche o cliente SQS
        sqsClient.shutdown();
    }

    public static void main(String[] args) {

        String bucketName = "my-test-bucket";
        String key = "mock_data.csv";

        S3EventNotification.S3BucketEntity s3Bucket =
                new S3EventNotification.S3BucketEntity(bucketName, null, null);
        S3EventNotification.S3ObjectEntity s3ObjectEntity =
                new S3EventNotification.S3ObjectEntity(key,null,null,null,null);
        S3EventNotification.S3Entity s3Entity =
                new S3EventNotification.S3Entity(null, s3Bucket, s3ObjectEntity, null);
        S3EventNotification.S3EventNotificationRecord s3EventNotificationRecord =
                new S3EventNotification.S3EventNotificationRecord(
                        null, null, null, null, null, null, null, s3Entity, null
                );
        List<S3EventNotification.S3EventNotificationRecord> s3EventNotificationRecords = List.of(s3EventNotificationRecord);
        S3Event s3Event = new S3Event(s3EventNotificationRecords);

         new App().handleRequest(s3Event, null);
    }
}

