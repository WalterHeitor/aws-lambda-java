package br.com.softwalter.core.usecase.person;

import br.com.softwalter.adpters.s3.ClientS3;
import br.com.softwalter.adpters.sqs.ClientSqs;
import br.com.softwalter.core.domain.Person;
import br.com.softwalter.core.usecase.ports.input.RecebeEventoS3;
import br.com.softwalter.core.usecase.ports.output.EnviaMenssagensSQS;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;

import java.io.IOException;
import java.util.List;

public class RecebeEventoS3EnviaSqsUseCaseImpl implements RecebeEventoS3EnviaSqsUseCase{

    RecebeEventoS3 recebeEventoS3 = new ClientS3();
    EnviaMenssagensSQS enviaMenssagensSQS = new ClientSqs();

    @Override
    public void execute(S3Event s3Event) throws IOException {

        for (S3EventNotification.S3EventNotificationRecord s3record : s3Event.getRecords()) {

            String bucketName = s3record.getS3().getBucket().getName();
            String objectKey = s3record.getS3().getObject().getKey();

            List<Person> persons = recebeEventoS3.getObjectContent(bucketName, objectKey);


            enviaMenssagensSQS.sendToSQS(persons);
        }
    }
}
