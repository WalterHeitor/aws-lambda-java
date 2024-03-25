package br.com.softwalter.core.usecase.person;

import com.amazonaws.services.lambda.runtime.events.S3Event;

import java.io.IOException;

public interface RecebeEventoS3EnviaSqsUseCase {

    void execute(S3Event s3Event) throws IOException;
}
