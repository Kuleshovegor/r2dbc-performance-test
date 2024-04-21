package tech.ydb.kuleshovegor;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.ydb.io.r2dbc.YdbConnectionFactory;

@Component
public class PersonRepository {
    private final YdbConnectionFactory connectionFactory;

    public PersonRepository(YdbConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Flux<Person> findById(Long id) {
        return connectionFactory.create().flatMapMany(ydbConnection -> ydbConnection.setReadOnly(true)
                .thenMany(ydbConnection
                .createStatement("select * from people where id > ? limit 100;")
                .bind(0, id)
                .execute()
                .flatMap(ydbResult -> ydbResult.map((row, rowMetadata) -> {
                    Long localId = row.get("id", Long.class);
                    String first_name = row.get("first_name", String.class);
                    String second_name = row.get("second_name", String.class);

                    return new Person(localId, first_name, second_name);
                }))));
    }

    public Flux<Person> findAll() {
        return connectionFactory.create().flatMapMany(ydbConnection -> ydbConnection.setReadOnly(true)
                .thenMany(
                ydbConnection
                        .createStatement("select * from people limit 100;")
                        .execute()
                        .flatMap(ydbResult -> ydbResult.map((row, rowMetadata) -> {
                            Long id = row.get("id", Long.class);
                            String first_name = row.get("first_name", String.class);
                            String second_name = row.get("second_name", String.class);

                            return new Person(id == null ? 0 : id, first_name, second_name);
                        }))));
    }

    public Mono<Person> save(Person person) {
        return connectionFactory.create().flatMap(ydbConnection ->
                ydbConnection
                        .createStatement("insert into people (id, first_name, second_name) values (?, ?, ?);")
                        .bind(0, person.getId())
                        .bind(1, person.getFirstName())
                        .bind(2, person.getLastName())
                        .execute()
                        .then(Mono.just(person)));
    }

    public Mono<Void> deleteById(Long id) {
        return connectionFactory.create().flatMap(ydbConnection ->
                ydbConnection
                        .createStatement("delete from people where id = ?;")
                        .bind(0, id)
                        .execute()
                        .then()
        );
    }
}
