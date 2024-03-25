
package br.com.softwalter;

import br.com.softwalter.core.usecase.person.RecebeEventoS3EnviaSqsUseCase;
import br.com.softwalter.core.usecase.person.RecebeEventoS3EnviaSqsUseCaseImpl;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.util.List;


public class App implements RequestHandler<S3Event, Void> {

//    private static final Logger logger = LogManager.getLogger(App.class);
    private final RecebeEventoS3EnviaSqsUseCase recebeEventoS3EnviaSqsUseCase =
            new RecebeEventoS3EnviaSqsUseCaseImpl();

    @Override
    public Void handleRequest(S3Event s3Event, Context context) {

//        ThreadContext.put("threadName", Thread.currentThread().getName());
        try {

//            logger.info("Iniciando processamento!");
            recebeEventoS3EnviaSqsUseCase.execute(s3Event);
//            logger.info("Fim do processamento!");

        } catch (IOException e) {
            System.out.println("Erro ao processar evento S3 ou no envio SQS: {}"+ e.getMessage());
        }
        return null;
    }


    public static void main(String[] args) {

        String bucketName = "my-test-bucket";
        String key = "mock_data.csv";

        S3EventNotification.S3BucketEntity s3Bucket =
                new S3EventNotification.S3BucketEntity(bucketName, null, null);
        S3EventNotification.S3ObjectEntity s3ObjectEntity =
                new S3EventNotification.S3ObjectEntity(key, null, null, null, null);
        S3EventNotification.S3Entity s3Entity =
                new S3EventNotification.S3Entity(null, s3Bucket, s3ObjectEntity, null);
        S3EventNotification.S3EventNotificationRecord s3EventNotificationRecord =
                new S3EventNotification.S3EventNotificationRecord(
                        null, null, null, null, null, null, null, s3Entity, null);
        List<S3EventNotification.S3EventNotificationRecord> s3EventNotificationRecords = List.of(s3EventNotificationRecord);
        S3Event s3Event = new S3Event(s3EventNotificationRecords);

        new App().handleRequest(s3Event, null);
    }
}

