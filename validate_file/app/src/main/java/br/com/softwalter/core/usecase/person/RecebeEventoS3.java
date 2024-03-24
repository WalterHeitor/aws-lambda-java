package br.com.softwalter.core.usecase.person;

import br.com.softwalter.core.domain.Person;

import java.io.IOException;
import java.util.List;

public interface RecebeEventoS3 {

    public List<Person> getObjectContent(String bucketName, String objectKey) throws IOException;
}
