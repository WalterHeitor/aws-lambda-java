package br.com.softwalter.adpters.s3;

import br.com.softwalter.core.domain.Person;
import br.com.softwalter.core.usecase.ports.input.RecebeEventoS3;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ClientS3 implements RecebeEventoS3 {

    private static final Logger LOG = LoggerFactory.getLogger(ClientS3.class.getName());
    AmazonS3 s3Client = AmazonS3ClientBuilder
            .standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://s3.us-east-1.localhost.localstack.cloud:4566", "us-east-1")) // localstack endpoint configuration
            .withCredentials(new DefaultAWSCredentialsProviderChain())
            .withPathStyleAccessEnabled(true)
            .build();

    @Override
    public List<Person> getObjectContent(String bucketName, String objectKey) throws IOException {

        try {

            LOG.debug("getObjectContent S3 bucketName: {}", bucketName);
            S3Object object = s3Client.getObject(bucketName, objectKey);
            if (!object.getKey().toLowerCase().endsWith(".csv")) {
                throw new IllegalArgumentException("O arquivo não é um arquivo CSV.");
            }
            InputStream objectData = object.getObjectContent();
            BufferedReader reader;
            reader = getBufferedReader(objectData);

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
            List<Person> people = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String id = parts[0];
                    String firstName = parts[1];
                    String lasName = parts[2];
                    String email = parts[3];
                    String gender = parts[4];
                    String ipAddress = parts[5];

                    Person person = new Person(id, firstName, lasName, email, gender, ipAddress);
                    people.add(person);
                    LOG.debug("Added person {}",  person);
                } else {
                    LOG.error("Ignorando linha inválida: {}", line);
                }
            }
            reader.close();

            LOG.debug("Fim do processamento do arquivo csv!");
            return people;
        } catch (AmazonServiceException e) {
            throw new IOException("Erro ao obter objeto do S3: " + e.getMessage());
        } finally {
            s3Client.shutdown();
        }
    }

    private static BufferedReader getBufferedReader(InputStream objectData) {
        BufferedReader reader;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(objectData);
            reader = new BufferedReader(inputStreamReader);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao carregar buffer" + e);
        }
        return reader;
    }

}
