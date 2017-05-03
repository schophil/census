#
# Building
# docker build -t censusdb .
#
# Running:
# Interactive for testing
# docker run -p 5432:5432 -e POSTGRES_PASSWORD=password --name [a nice name] [image name]
#
# As a daemon
# docker run -p 5432:5432 -e POSTGRES_PASSWORD=postgres -d --name [a nice name] [image name]

FROM postgres

ADD *.sql /docker-entrypoint-initdb.d/
ADD *.csv /docker-entrypoint-initdb.d/
