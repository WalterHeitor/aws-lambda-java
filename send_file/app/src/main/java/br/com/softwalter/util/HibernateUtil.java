package br.com.softwalter.util;

import br.com.softwalter.entity.Contract;
import br.com.softwalter.entity.Person;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Crie as propriedades para configuração do Hibernate
            Properties hibernateProperties = new Properties();
            hibernateProperties.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            hibernateProperties.setProperty("hibernate.connection.url", "jdbc:postgresql://your-database-url");
            hibernateProperties.setProperty("hibernate.connection.username", "your-username");
            hibernateProperties.setProperty("hibernate.connection.password", "your-password");
            hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

            // Outras propriedades do Hibernate conforme necessário

            // Construa o registro de serviço do Hibernate
            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(hibernateProperties)
                    .build();

            // Adicione as classes de entidade (mapeamento) ao metadados
            Metadata metadata = new MetadataSources(serviceRegistry)
                    .addAnnotatedClass(Person.class)
                    .addAnnotatedClass(Contract.class)
                    .getMetadataBuilder()
                    .build();

            // Construa a fábrica de sessão do Hibernate
            return metadata.getSessionFactoryBuilder().build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao configurar o Hibernate!", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}

