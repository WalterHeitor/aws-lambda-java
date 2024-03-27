package br.com.softwalter.core.usecase.ports.output;

import br.com.softwalter.core.domain.Person;

import java.util.List;

public interface EnviaMenssagensSQS {

    void sendToSQS(List<Person> personList);
}
