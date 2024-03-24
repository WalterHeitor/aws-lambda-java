package br.com.softwalter.core.usecase.person;

import br.com.softwalter.core.domain.Person;

import java.util.List;

public interface EnviaMenssagensSQS {

    public void sendToSQS(List<Person> personList);
}
