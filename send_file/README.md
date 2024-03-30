## Configuração

1. Clone o repositório do projeto:
    ```
    git clone https://github.com/seu-usuario/projeto-softwalter.git
    ```

2. Configure as credenciais da AWS:
    - Instale e configure o AWS CLI, ou
    - Configure as credenciais no arquivo `~/.aws/credentials`

3. Configure as variáveis de ambiente:
    - Configure as variáveis de ambiente `AWS_ACCESS_KEY_ID` e `AWS_SECRET_ACCESS_KEY`

4. Configure o banco de dados PostgreSQL:
    - Crie um banco de dados PostgreSQL com o nome `softwalter`
    - Configure as credenciais do banco de dados no arquivo `application.properties`

5. Inicie o LocalStack usando Docker Compose:
    ```
    docker-compose up
    ```

6. Crie o bucket S3 localmente usando o AWS CLI:
    ```
    aws --endpoint-url=http://localhost:4566 s3 mb s3://my-test-bucket
    ```

7. Configure as variáveis de ambiente para o LocalStack:
    - Defina as seguintes variáveis de ambiente:
        - `AWS_REGION`: `us-east-1`
        - `AWS_S3_URL`: `http://localhost:4566`

## Como Executar

1. Compile o projeto:
    ```
    gradle build
    ```

2. Execute a aplicação localmente:
    ```
    java -jar build/libs/projeto-softwalter.jar
    ```

## Arquitetura

- **AWS Lambda**: Utilizado para processar os eventos SQS e gerar os documentos PDF.
- **Amazon SQS**: Fila de mensagens para enviar os eventos que acionam o processamento dos documentos PDF.
- **Amazon S3**: Armazenamento dos documentos PDF gerados.
- **PostgreSQL**: Banco de dados utilizado para armazenar informações sobre pessoas e contratos.

## Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para abrir uma issue ou enviar um pull request.

## Licença

Este projeto é distribuído sob a licença MIT. Consulte o arquivo `LICENSE` para obter mais informações.
