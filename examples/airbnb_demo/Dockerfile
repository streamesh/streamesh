FROM mysql:8.0.18 as mysql-bnb
COPY mysql-reader/my.cnf /etc/mysql
COPY mysql-reader/init.sql /docker-entrypoint-initdb.d
CMD ["mysqld"]

FROM maven:3.6.3-jdk-11 as maven-merger
WORKDIR /usr
COPY http-data-merger ./http-data-merger/
WORKDIR /usr/http-data-merger
RUN  mvn clean package

FROM maven:3.6.3-jdk-11 as maven-db-reader
WORKDIR /usr
COPY simple-db-reader ./simple-db-reader/
ARG db_address
WORKDIR /usr/simple-db-reader
#RUN echo $db_address
RUN mvn clean package -Ddb.address=$db_address


FROM adoptopenjdk/openjdk11:jre-11.0.7_10-alpine as http-data-merger
COPY --from=maven-merger /usr/http-data-merger/target/http-data-merger-1.0-SNAPSHOT.jar /usr/bin

FROM adoptopenjdk/openjdk11:jre-11.0.7_10-alpine as simple-db-reader
COPY --from=maven-db-reader /usr/simple-db-reader/target/simple-db-reader-1.0-SNAPSHOT.jar /usr/bin

FROM amancevice/pandas:0.25.3 as python-plotter
WORKDIR /
COPY python-plotter/plot_1.py ./usr/
RUN pip3 install matplotlib seaborn

FROM amancevice/pandas:0.25.3 as file_retriever
WORKDIR /
COPY file_retriever/file_retriever.py ./usr/
RUN mkdir ./data
COPY data/AB_NYC_2019_Manhattan.txt ./data
COPY data/AB_NYC_2019_rest.txt ./data
