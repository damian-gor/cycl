Po uruchomieniu aplikacji z opisaem API można zapoznać się pod adresem http://localhost:8080/swagger-ui/

Aplikacja działa na defaultowym porcie 8080

By podejrzeć bazę danych należy po uruchomieniu aplikacji otworzyć stronę: http://localhost:8080/h2-console/

Parametry bazy danych:

Driver Class: org.h2.Driver

JDBC URL: jdbc:h2:mem:testdb

User Name: sa

Password:

Możliwość konfiguracji defaultowej liczby zwracanych rekordów (10) w pliku application.yml za pomocą parametru 'default-paginaton.page-size'