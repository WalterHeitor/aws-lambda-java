package br.com.softwalter.adpters.sqs;

import br.com.softwalter.core.domain.Person;
import br.com.softwalter.core.usecase.ports.output.EnviaMenssagensSQS;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;


public class ClientSqs implements EnviaMenssagensSQS {

    private static final Logger logger = LoggerFactory.getLogger(ClientSqs.class.getName());
    private static final AmazonSQS sqsClient = AmazonSQSClientBuilder.defaultClient();
    private static final String QUEUE = "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/my-test-queue";

    public void sendToSQS(List<Person> personList) {


        for (Person person : personList) {

            String messageBody = person.toString();
            SendMessageRequest sendMessageRequest = new SendMessageRequest()
                    .withQueueUrl(QUEUE)
                    .withMessageBody(messageBody);

            SendMessageResult sendMessageResult = sqsClient.sendMessage(sendMessageRequest);
            logger.debug("Mensagem enviada com sucesso. ID da mensagem: {}"+  sendMessageResult.getMessageId());
        }

        sqsClient.shutdown();
    }
}
