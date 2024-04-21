package tech.ydb.kuleshovegor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class PersonRepository {
    @Value("${jdbcUrl}")
    String jdbcUrl;

    public Flux<Person> findById(Long id) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
            connection.setReadOnly(true);
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rs = statement.executeQuery("select * from people where id > " + id + " limit 100;")) {
                    List<Person> persons = new ArrayList<>();

                    while (rs.next()) {
                        long localId = rs.getLong("id");
                        String first_name = rs.getString("first_name");
                        String second_name = rs.getString("second_name");

                        persons.add(new Person(localId, first_name, second_name));
                    }

                    return Flux.fromIterable(persons);
                }
            }
        } catch (SQLException ex) {
            return Flux.error(ex);
        }
    }

    public Flux<Person> findAll() {
        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
            connection.setReadOnly(true);
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rs = statement.executeQuery("select * from people limit 100;")) {
                    List<Person> persons = new ArrayList<>();
                    while (rs.next()) {
                        long id = rs.getLong("id");
                        String first_name = rs.getString("first_name");
                        String second_name = rs.getString("second_name");

                        persons.add(new Person(id, first_name, second_name));
                    }

                    return Flux.fromIterable(persons);
                }
            }
        } catch (SQLException ex) {
            return Flux.error(ex);
        }
    }

    public Mono<Person> save(Person person) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rs = statement.executeQuery("insert into people (id, first_name, second_name) values ("+person.getId()+", "+person.getFirstName()+", "+person.getLastName()+");")) {
                    rs.next();

                    return Mono.just(person);
                }
            }
        } catch (SQLException ex) {
            return Mono.error(ex);
        }
    }

    public Mono<Void> deleteById(Long id) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rs = statement.executeQuery("delete from people where id =" + id + ";")) {
                    rs.next();

                    return Mono.empty();
                }
            }
        } catch (SQLException ex) {
            return Mono.error(ex);
        }
    }
}
