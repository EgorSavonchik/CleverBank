FROM postgres:latest

COPY src/main/resources/init.sql /docker-entrypoint-initdb.d/
