package com.github.mayconr.juoserver.game;

import com.github.mayconr.juoserver.game.core.agent.AgentConfiguration;
import com.github.mayconr.juoserver.game.core.ai.ollama.OllamaClientChatImpl;
import com.github.mayconr.juoserver.game.core.ai.ollama.OllanaClient;
import com.github.mayconr.juoserver.game.core.database.Database;
import com.github.mayconr.juoserver.game.core.database.DatabaseConfiguration;
import com.github.mayconr.juoserver.game.core.event.DefaultEventBus;
import com.github.mayconr.juoserver.game.core.event.EventBus;
import com.github.mayconr.juoserver.game.core.prototype.PrototypeConfiguration;
import com.github.mayconr.juoserver.game.core.session.DefaultGameSession;
import com.github.mayconr.juoserver.game.core.session.GameSession;
import com.github.mayconr.juoserver.game.core.session.player.PlayerSessionFactory;
import com.github.mayconr.juoserver.game.packet.handler.*;
import com.github.mayconr.juoserver.game.server.ClientConnectedHandlerAdapter;
import com.github.mayconr.juoserver.game.server.ServerStartup;
import com.github.mayconr.juoserver.game.server.UOChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import java.util.List;

@Configuration
@Import({
        DatabaseConfiguration.class,
        AgentConfiguration.class,
        PrototypeConfiguration.class
})
public class JUOServerGameConfiguration {

    // ========= Independents =========

    @Bean
    public OllanaClient ollanaClient() {
        return new OllamaClientChatImpl("http://localhost:11434");
    }

    @Bean
    public EventBus eventBus() {
        return new DefaultEventBus();
    }

    @Bean
    public ChannelGroup channelGroup() {
        return new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    @Bean
    public NioEventLoopGroup parentNioEventLoopGroup() {
        return new NioEventLoopGroup(1);
    }

    @Bean
    public NioEventLoopGroup childNioEventLoopGroup() {
        return new NioEventLoopGroup(1);
    }

    // ========= Session Factory / Core Game Session =========

    @Bean
    public PlayerSessionFactory playerSessionFactory(
            ChannelGroup channelGroup,
            EventBus eventBus,
            Database database
    ) {
        return new PlayerSessionFactory(channelGroup, eventBus, database);
    }

    @Bean
    public GameSession gameSession(
            Database database,
            ChannelGroup channelGroup,
            EventBus eventBus,
            PlayerSessionFactory playerSessionFactory,
            OllanaClient ollanaClient
    ) {
        return new DefaultGameSession(database, channelGroup, eventBus, playerSessionFactory, ollanaClient);
    }

    // ========= Packet Handlers =========

    @Bean
    public List<SimpleChannelInboundHandler<?>> packetHandlers(
            Database database,
            GameSession gameSession
    ) {
        return List.of(
                new GameServerLoginHandler(database),
                new PingPongHandler(),
                new LoginCharacterHandler(gameSession),
                new DeleteCharacterHandler(database),
                new CreateCharacterHandler(database),
                new ClientVersionHandler(),
                new MoveRequestHandler(),
                new DoubleClickHandler(),
                new UnicodeSpeachRequestHandler(),
                new MegaClilocHandler(),
                new GeneralInformationHandler(),
                new LookRequestHandler(),
                new PickUpItemHandler(),
                new DropItemHandler(),
                new WearItemHandler(),
                new TargetHandler()
        );
    }

    // ========= Network =========

    @Bean
    public ClientConnectedHandlerAdapter connectionLoggingHandler(ChannelGroup channelGroup) {
        return new ClientConnectedHandlerAdapter(channelGroup);
    }

    @DependsOn({"connectionLoggingHandler", "packetHandlers"})
    @Bean
    public UOChannelInitializer channelInitializer(
            ClientConnectedHandlerAdapter clientConnectedHandlerAdapter,
            List<SimpleChannelInboundHandler<?>> packetHandlers
    ) {
        return new UOChannelInitializer(clientConnectedHandlerAdapter, packetHandlers);
    }

    @Bean
    public ServerBootstrap serverBootstrap(
            UOChannelInitializer channelInitializer,
            NioEventLoopGroup parentNioEventLoopGroup,
            NioEventLoopGroup childNioEventLoopGroup
    ) {
        return new ServerBootstrap()
                .group(parentNioEventLoopGroup, childNioEventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(channelInitializer)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    @Bean
    public ServerStartup serverStartup(
            ServerBootstrap serverBootstrap,
            NioEventLoopGroup parentNioEventLoopGroup,
            NioEventLoopGroup childNioEventLoopGroup
    ) {
        return new ServerStartup(serverBootstrap, parentNioEventLoopGroup, childNioEventLoopGroup);
    }
}
