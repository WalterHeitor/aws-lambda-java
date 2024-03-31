CREATE TABLE person (
    id VARCHAR(255) PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    gender VARCHAR(255),
    ip_address VARCHAR(255)
);

CREATE TABLE contract (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255),
    description VARCHAR(255),
    start_date DATE,
    end_date DATE,
    value NUMERIC,
    status VARCHAR(255),
    person_id VARCHAR(255),
    FOREIGN KEY (person_id) REFERENCES person(id)
);
