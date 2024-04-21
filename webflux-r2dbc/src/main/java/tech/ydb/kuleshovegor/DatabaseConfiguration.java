package tech.ydb.kuleshovegor;

import java.time.Duration;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.ydb.io.r2dbc.YdbConnectionFactory;

import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PORT;

@Configuration
class DatabaseConfiguration {
    @Bean
    public YdbConnectionFactory connectionFactory() {
        return (YdbConnectionFactory) ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "ydb")
                .option(HOST, "host")
                .option(PORT, 2135)
                .option(DATABASE, "database")
                .option(Option.valueOf("sessionPoolMinSize"), 4)
                .option(Option.valueOf("sessionPoolMaxSize"), 100)
                .option(Option.valueOf("sessionKeepAliveTime"), Duration.ofMinutes(5))
                .option(Option.valueOf("secureConnection"), true)
                .build());
    }
}
