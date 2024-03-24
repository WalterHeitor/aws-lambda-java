package br.com.softwalter.adpters.sqs;

import br.com.softwalter.core.domain.Person;
import br.com.softwalter.core.usecase.ports.output.EnviaMenssagensSQS;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class ClientSqs implements EnviaMenssagensSQS {

    private final AmazonSQS sqsClient = AmazonSQSClientBuilder.defaultClient();
    private static final Log LOG = LogFactory.getLog(ClientSqs.class);

    public void sendToSQS(List<Person> personList) {

        String queueUrl = "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/my-test-queue";
        for (Person person : personList) {

            String messageBody = person.toString();
            SendMessageRequest sendMessageRequest = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(messageBody);

            SendMessageResult sendMessageResult = sqsClient.sendMessage(sendMessageRequest);
            LOG.info("Mensagem enviada com sucesso. ID da mensagem: " + sendMessageResult.getMessageId());
        }

        sqsClient.shutdown();
    }
}
