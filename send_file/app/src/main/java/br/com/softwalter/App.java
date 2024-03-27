package br.com.softwalter;

import br.com.softwalter.config.ThymeleafConfiguration;
import br.com.softwalter.entity.Person;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class App implements RequestHandler<SQSEvent, Void> {

    private static final Logger logger = LoggerFactory.getLogger(App.class.getName());
    private final AmazonS3 s3 = AmazonS3ClientBuilder
            .standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://s3.us-east-1.localhost.localstack.cloud:4566", "us-east-1")) // localstack endpoint configuration
            .withCredentials(new DefaultAWSCredentialsProviderChain())
            .withPathStyleAccessEnabled(true)
            .build();
    private final String bucketName = "my-test-bucket";
    private final TemplateEngine templateEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public App() {

        templateEngine = new ThymeleafConfiguration().templateEngine();
    }

    @Override
    public Void handleRequest(SQSEvent sqsEvent, Context context) {
        try {
            for (SQSEvent.SQSMessage msg : sqsEvent.getRecords()) {
                String messageBody = msg.getBody();
                Person person = parseMessageToPerson(messageBody);
                String htmlContent = generateHtmlFromTemplate(person);
                byte[] pdfBytes = createPdfFromHtml(htmlContent);
                sendPdfToS3(pdfBytes);
            }
        } catch (IOException e) {
            logger.error("Erro ao processar evento SQS: {}", e.getMessage());
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private byte[] createPdfFromHtml(String htmlContent) throws IOException, DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();
        HTMLWorker htmlWorker = new HTMLWorker(document);
        htmlWorker.parse(new StringReader(htmlContent));
        document.close();
        return outputStream.toByteArray();
    }

    private Person parseMessageToPerson(String messageBody) throws JsonProcessingException {
        return objectMapper.readValue(messageBody, Person.class);
    }

    private String generateHtmlFromTemplate(Person person) {
        final org.thymeleaf.context.Context thymeleafContext = new org.thymeleaf.context.Context();
        thymeleafContext.setVariable("id", person.getId());
        thymeleafContext.setVariable("first_name", person.getFirstName());
        thymeleafContext.setVariable("last_name", person.getLastName());
        thymeleafContext.setVariable("email", person.getEmail());
        thymeleafContext.setVariable("gender", person.getGender());
        thymeleafContext.setVariable("ip_address", person.getIpAddress());
        return templateEngine.process("person_template", thymeleafContext);
    }

    private void sendPdfToS3(byte[] pdfBytes) {
        String pdfKey = "pdf_" + System.currentTimeMillis() + ".pdf";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfBytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(pdfBytes.length);
        s3.putObject(bucketName, pdfKey, inputStream, metadata);
    }


    public static void main(String[] args) {
        // Crie uma instância de SQSEvent.SQSMessage para simular uma mensagem do SQS
        SQSEvent.SQSMessage msg = new SQSEvent.SQSMessage();
        msg.setBody("{\"id\":\"20\", \"first_name\":\"Francesca\", \"last_name\":\"Spirritt\", \"email\":\"fspirrittj@cisco.com\", \"gender\":\"Agender\", \"ip_address\":\"34.144.83.205\"}");

        // Crie uma instância de SQSEvent contendo a mensagem
        SQSEvent sqsEvent = new SQSEvent();
        sqsEvent.setRecords(Collections.singletonList(msg));



        // Crie uma instância de App e invoque o método handleRequest
        App app = new App();
        app.handleRequest(sqsEvent, null);
    }

    // Classe de contexto simulada
}
