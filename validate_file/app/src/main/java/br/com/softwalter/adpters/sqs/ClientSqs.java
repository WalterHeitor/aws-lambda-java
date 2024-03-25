package br.com.softwalter.adpters.sqs;

import br.com.softwalter.core.domain.Person;
import br.com.softwalter.core.usecase.ports.output.EnviaMenssagensSQS;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;


import java.util.List;


public class ClientSqs implements EnviaMenssagensSQS {

//    Logger logger = LogManager.getLogger(ClientSqs.class);
    private final AmazonSQS sqsClient = AmazonSQSClientBuilder.defaultClient();
    private static final String QUEUE = "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/my-test-queue";

    public void sendToSQS(List<Person> personList) {


//        ThreadContext.put("threadName", Thread.currentThread().getName());
        for (Person person : personList) {

            String messageBody = person.toString();
            SendMessageRequest sendMessageRequest = new SendMessageRequest()
                    .withQueueUrl(QUEUE)
                    .withMessageBody(messageBody);

            SendMessageResult sendMessageResult = sqsClient.sendMessage(sendMessageRequest);
            System.out.println("Mensagem enviada com sucesso. ID da mensagem: {}"+  sendMessageResult.getMessageId());
        }

        sqsClient.shutdown();
    }
}
