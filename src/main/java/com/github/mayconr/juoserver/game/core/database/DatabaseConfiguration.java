package com.github.mayconr.juoserver.game.core.database;

import com.github.mayconr.juoserver.game.core.prototype.PrototypeManager;
import org.springframework.context.annotation.Bean;

public class DatabaseConfiguration {

    @Bean
    public Database database(PrototypeManager prototypeManager) {
        return new HardcodedDatabase(prototypeManager);
    }


}
