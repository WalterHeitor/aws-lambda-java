aws --endpoint-url=http://localhost:4566 s3 mb s3://my-test-bucket
aws --endpoint-url=http://localhost:4566 s3 sync ./localstack/mock_data.csv s3://my-test-bucket
aws --endpoint-url=http://localhost:4566 s3  cp ./localstack/mock_data.csv s3://my-test-bucket
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name my-test-queue