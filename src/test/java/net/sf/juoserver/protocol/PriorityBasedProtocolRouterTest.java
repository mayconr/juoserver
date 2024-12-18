package net.sf.juoserver.protocol;

import net.sf.juoserver.TestingFactory;
import net.sf.juoserver.api.*;
import net.sf.juoserver.model.combat.UOCombatSystem;
import net.sf.juoserver.model.combat.PhysicalDamageCalculatorImpl;
import net.sf.juoserver.protocol.item.ItemManager;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PriorityBasedProtocolRouterTest {
	@Rule public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final LoginManager loginManager = context.mock(LoginManager.class);
	private final ProtocolIoPort clientHandler = context.mock(ProtocolIoPort.class);
	private final InterClientNetwork network = context.mock(InterClientNetwork.class);
	
	private final AuthenticationController auth = new AuthenticationController(clientHandler,
			TestingFactory.createTestConfiguration(), loginManager);
	private final GameController game = new GameController("client", clientHandler, context.mock(Core.class), context.mock(Configuration.class),
			context.mock(ClientMovementTracker.class), loginManager, network, context.mock(NpcSystem.class), new ItemManager(),new CommandManagerImpl(null, null), new UOCombatSystem(new PhysicalDamageCalculatorImpl(null)), null);
	
	@Test
	public void authenticationIsTheOnlyControllerInterestedInFirstConnectionMessages() throws UnknownHostException {
		assertTrue(auth.isInterestedIn(new LoginSeed(InetAddress.getLocalHost())));
		assertFalse(game.isInterestedIn(new LoginSeed(InetAddress.getLocalHost())));
		
		assertTrue(auth.isInterestedIn(new LoginRequest("user", "psw")));
		assertFalse(game.isInterestedIn(new LoginRequest("user", "psw")));
		
		assertTrue(auth.isInterestedIn(new SelectServer(42)));
		assertFalse(game.isInterestedIn(new SelectServer(42)));
	}
}
