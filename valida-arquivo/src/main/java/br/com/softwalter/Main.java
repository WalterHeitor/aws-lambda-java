package br.com.softwalter;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) {
        String json = "{\"Records\":[{\"eventVersion\":\"2.0\",\"eventSource\":\"aws:s3\",\"awsRegion\":\"us-east-1\",\"eventTime\":\"1970-01-01T00:00:00Z\",\"eventName\":\"ObjectCreated:Put\",\"userIdentity\":{\"principalId\":\"EXAMPLE\"},\"requestParameters\":{\"sourceIPAddress\":\"127.0.0.1\"},\"responseElements\":{\"x-amz-request-id\":\"EXAMPLE123456789\",\"x-amz-id-2\":\"EXAMPLE123/5678abcdefghijklambdaisawesome/mnopqrstuvwxyzABCDEFGH\"},\"s3\":{\"s3SchemaVersion\":\"1.0\",\"configurationId\":\"testConfigRule\",\"bucket\":{\"name\":\"mybucket\",\"ownerIdentity\":{\"principalId\":\"EXAMPLE\"},\"arn\":\"\"arn:aws:s3:::mybucket\"},\"object\":{\"key\":\"MOCK_DATA.csv\",\"size\":1024,\"eTag\":\"0123456789abcdef0123456789abcdef\",\"sequencer\":\"0A1B2C3D4E5F678901\"}}}] }";

        S3EventNotification s3EventNotification = new S3EventNotification(new );
        try {
            S3EventNotification notification = S3EventNotification.parseJson(loadJsonFromFile("s3-event.json"));
            S3Event event = new S3Event(notification.getRecords());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            S3Event s3Event = objectMapper.readValue(json, S3Event.class);

            // Teste: Imprimindo o primeiro registro do evento
            S3EventNotification.S3EventNotificationRecord record = s3Event.getRecords().get(0);
            System.out.println("Evento S3 recebido: " + record.getEventName());
            System.out.println("Bucket: " + record.getS3().getBucket().getName());
            System.out.println("Objeto: " + record.getS3().getObject().getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
