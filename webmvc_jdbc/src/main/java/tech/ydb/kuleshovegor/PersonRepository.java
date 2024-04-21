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

@Component
public class PersonRepository {
    @Value("${jdbcUrl}")
    String jdbcUrl;

    public List<Person> findById(Long id) {
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

                    return persons;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Person> findAll() {
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

                    return persons;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Person save(Person person) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
            try (Statement statement = connection.createStatement()) {
                String sql = "insert into people (id, first_name, second_name) values ("+person.getId()+", \""+person.getFirstName()+"\", \""+person.getLastName()+"\");";
                statement.executeUpdate(sql);

                return person;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void deleteById(Long id) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet rs = statement.executeQuery("delete from people where id =" + id + ";")) {
                    rs.next();
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
