package net.sf.juoserver.protocol;

import net.sf.juoserver.api.*;
import net.sf.juoserver.model.*;
import net.sf.juoserver.protocol.SkillUpdate.SkillUpdateType;
import net.sf.juoserver.protocol.item.ItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Game controller. A different instance of this class will be associated
 * to each client's session.
 */
public class GameController extends AbstractProtocolController implements ModelOutputPort {

	private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

	private static final String CONTROLLER_ID_POSTFIX = "-controller";
	
	private final String controllerId;
	private final Core core;
	private final Configuration configuration;
	private final ProtocolIoPort clientHandler;
	private final ClientMovementTracker movementTracker;
	private final InterClientNetwork network;

	// Controller Managers
	private final ItemManager itemManager;
	private final LoginManager loginManager;
	private final CommandManager commandManager;
	private final GeneralInfoManager generalInfoManager;

	// Server Systems
	private final NpcSystem npcSystem;
	private final CombatSystem combatSystem;

	private ClientVersion clientVersion;
	private PlayerSession session;

	public GameController(String clientName, ProtocolIoPort clientHandler, Core core, Configuration configuration,
			ClientMovementTracker movementTracker, LoginManager loginManager, InterClientNetwork network, NpcSystem npcSystem,
		  ItemManager itemManager, CommandManager commandManager, CombatSystem combatSystem, GeneralInfoManager generalInfoManager) {
		super();
		this.controllerId = clientName + CONTROLLER_ID_POSTFIX;
		this.clientHandler = clientHandler;
		this.core = core;
		this.configuration = configuration;
		this.movementTracker = movementTracker;
		this.loginManager = loginManager;
		this.network = network;
		this.npcSystem = npcSystem;
		this.combatSystem = combatSystem;
		this.itemManager = itemManager;
		this.commandManager = commandManager;
		this.generalInfoManager = generalInfoManager;
	}

	public void setSession(PlayerSession session) {
		this.session = session;
	}

	public void handle(BuffDebuff buffDebuff) {
		System.out.println(buffDebuff);
	}

	public void handle(LoginSeed loginSeed) {
		System.out.println(loginSeed);
	}

	// This message is sent in the second connection right after the new seed
	public CharacterList handle(ServerLoginRequest request) throws IOException {
		Account account = loginManager.getAuthorizedAccount(request.getAuthenticationKey());
		if (account == null) {
			clientHandler.deactivate();
			return null;
		}
		
		session = new UOPlayerSession(core, account, this, network);
		network.addIntercomListener(session);

		// Context Initialization
		var context = new UOPlayerContext(session, core, clientHandler);
		generalInfoManager.setContext(context);
		itemManager.setContext(context);
		commandManager.setContext(context);
		
		List<String> names = session.getCharacterNames();
		List<PlayingCharacter> chars = new ArrayList<>();
		for (String name : names) {
			chars.add(new PlayingCharacter(name, account.getPassword()));
		}
		return new CharacterList(chars, new ArrayList<>(),
				// TODO: create constants/enum for the following two flags
				new Flag(0x14),   // 1-char only 
				new Flag(0x1A8)); // Mondain's Legacy
	}
	
	public Message handle(CharacterSelect request) {
		session.selectCharacterById(request.getCharId());
		return new ClientVersion();
	}
	
	// TODO: complete this handler - see messages SERVER_74,75 in sample_login
	// see PacketHandlers#DoLogin() [@ RunUo source]
	// may require reading the map files
	public List<Message> handle(ClientVersion clientVersion) {
		if (this.clientVersion != null) {
			return null;
		}
		this.clientVersion = clientVersion;
		LOGGER.info("Client version: " + clientVersion.getClientVersion());
		GameStatus status = session.startGame();
		return sendGameStatus(session.getMobile(), status);
	}

	private List<Message> sendGameStatus(Mobile mobile, GameStatus status) {
		LightLevels lightLevel = status.getLightLevel();
		Season season = status.getSeason();

		// Register selected mobile to combat system
		combatSystem.registerMobile(mobile, session);

		List<Message> response = new ArrayList<>(asList(
				new LoginConfirm(mobile.getSerialId(), (short) mobile.getModelId(),
						(short) mobile.getX(), (short) mobile.getY(), (byte) mobile.getZ(),
						(byte) mobile.getDirection().getCode(), (byte) mobile.getNotoriety().getCode(),
						(short) 7168, (short) 4096),
				// TODO: don't hard-code the map size (7168 x 4096) and index (0), see Core#init() [@ RunUo source]
				new GeneralInformation(new GeneralInformation.SetCursorHueSetMap((byte) 0)),
				new SeasonalInformation(season, true),
				new DrawGamePlayer(mobile),
				new CharacterDraw(mobile),
				new OverallLightLevel(new UOProtocolLightLevel(lightLevel)),
				new PersonalLightLevel(mobile.getSerialId(), new UOProtocolLightLevel(lightLevel)),
				new ClientFeatures(ClientFeature.T2A),
				new CharacterWarmode((byte) 0),
				new LoginComplete()
		));
		response.addAll(core.findItemsInRegion(mobile, 20)
				.stream().map(ObjectInfo::new)
				.toList());
		response.addAll( mobileObjectsRevisions( mobile ) );
		return response;
	}
	
	private Collection<? extends Message> mobileObjectsRevisions(Mobile mobile) {
		List<Message> revisions = new ArrayList<>();
		for (Item item : mobile.getItems().values()) {
			revisions.add( new ObjectRevision(item) );
		}
		return revisions;
	}
	
	/**
	 * Answers with the same sequence, increments the internal sequence (0 -->
	 * 256 and then always restart from 1).
	 * 
	 * @param request movement request
	 * @return movement response
	 * @throws IntercomException in case an inter-client error occurs 
	 */
	public List<Message> handle(MoveRequest request) {
		if (movementTracker.getExpectedSequence() == request.getSequence()) {
			session.move(request.getDirection(), request.isRunning());

			movementTracker.incrementExpectedSequence();

			return asList( new MovementAck(request.getSequence(), session.getMobile().getNotoriety()) );
		} else {
			LOGGER.warn("Movement request rejected - expected sequence: "
					+ movementTracker.getExpectedSequence() + ", actual sequence: "
					+ request.getSequence());
			return asList( new MovementReject(request.getSequence(), session.getMobile().getX(), session.getMobile().getY(),
					session.getMobile().getZ(), session.getMobile().getDirectionWithRunningInfo()) );
		}
	}
	
	/**
	 * Handles possible client's position synchronization requests.
	 * 
	 * @param synchRequest synchronization request
	 * @return messages suitable for synchronizing the client's position
	 */
	public List<Message> handle(MovementAck synchRequest) {
		// TODO: this handler is not actually needed (i.e., called) yet
		return asList(new DrawGamePlayer(session.getMobile()), new CharacterDraw(session.getMobile()));
	}

	@Override
	public void mobileApproached(Mobile mobile) {
		try {
			clientHandler.sendToClient(new CharacterDraw(mobile), new ObjectRevision(mobile));
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}

	@Override
	public void mobileGotAway(Mobile mobile) {
		try {
			clientHandler.sendToClient(new DeleteObject(mobile.getSerialId()));
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}

	public void handle(UnicodeSpeechRequest request) {
		if (commandManager.isCommand(request)) {
			commandManager.execute(request);
		} else {
			session.speak(request.getMessageType(), request.getHue(),
					request.getFont(), request.getLanguage(), request.getText());
		}
	}
	
	@Override
	public void mobileSpoke(Mobile speaker, MessageType type, int hue, int font,
			String language, String text) {
		try {
			clientHandler.sendToClient( new UnicodeSpeech(speaker, type, hue,
					font, language, text) );
		} catch (IOException e) {
			throw new IntercomException(e);
		}
	}
	
	/**
	 * Handles tooltips requests.
	 * 
	 * @param mcr request
	 * @return tooltip information
	 */
	public List<Message> handle(MegaClilocRequest mcr) {
		List<Message> msgs = new ArrayList<>();
		for (int querySerial : mcr.getQuerySerials()) {

			// TODO: distinguish between items and mobiles as in handle(LookRequest)
			Mobile mobile = core.findMobileByID( querySerial );
			if (mobile != null) {
				msgs.add(MegaClilocResponse.createMobileMegaClilocResponse(mobile));
			} else {
				Item item = core.findItemByID( querySerial );
				if (item != null) {
					msgs.add(MegaClilocResponse.createItemMegaClilocResponse(item));
				}
			}
		}
		return msgs;
	}

	public Message handle(PingPong ping) {
		return new PingPong(ping.getSequenceNumber());
	}
	
	//TODO: complete this handler - see CLIENT_77 in sample_login
	public Message handle(GetPlayerStatus gps) {
		switch (gps.getRequest()) {
		case GodClient:
			//TODO: check if clients has the right privileges, kick otherwise
			return null;
		case Stats:
			return new StatusBarInfo(core.findMobileByID(gps.getSerial()));
		case Skills:
			return new SkillUpdate(SkillUpdateType.FullListWithCap,
					core.findMobileByID(gps.getSerial()).getSkills().toArray(new Skill[0]));
		default:
			return null;
		}
	}
	
	public List<? extends Message> handle(DoubleClick doubleClick) {
		if (doubleClick.isPaperdollRequest()) {
			Mobile mob = core.findMobileByID(doubleClick.getObjectSerialId());
			return List.of(new Paperdoll(doubleClick.getObjectSerialId(),
					mob.getName() + ", " + mob.getTitle(),
					false, false));
		}
		var item = core.findItemByID(doubleClick.getObjectSerialId());
		if (item != null) {
			return itemManager.use(item);
		}
		LOGGER.warn("SerialId {} not found!", doubleClick.getObjectSerialId());
		return Collections.emptyList();
	}

	public List<Message> handle(GeneralInformation generalInformation) {
		return generalInfoManager.handle(generalInformation);
	}
	
	public void handle(SpyOnClient spyOnClient) {} // Ignore this message
	
	// ======================== items ========================
	
	public Message handle(LookRequest lookRequest) {
		Mobile mobile = core.findMobileByID( lookRequest.getSerialId() );
		if (mobile != null) {
			return new ClilocMessage(mobile);
		} else {
			System.out.println("id "+lookRequest.getSerialId());
			Item item = core.findItemByID( lookRequest.getSerialId() );
			return new SendSpeech(item);
			// TODO: handle items' stacks too
		}
	}
	
	public void handle(final DropItem dropItem) throws IOException {
		session.dropItem(dropItem.getItemSerial(), dropItem.isDroppedOnTheGround(),
				dropItem.getTargetContainerSerial(), dropItem.getTargetPosition());
	}
	
	@Override
	public void containerChangedContents(Container updatedContainer) {
		try {
			clientHandler.sendToClient(new ContainerItems(updatedContainer));
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}

	@Override
	public void itemDragged(Item item, Mobile droppingMobile,
			int targetSerialId) {
		try {
			clientHandler.sendToClient(new DragItem(item, droppingMobile, targetSerialId));
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}

	@Override
	public void itemChanged(Item item) {
		try {
			clientHandler.sendToClient(new ObjectInfo(item), new ObjectRevision(item));
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}

	public void handle(PickUpItem pickUpItem) {
	}
	
	public void handle(WearItem wearItem) {
		session.wearItemOnMobile(wearItem.getLayer(), wearItem.getItemSerialId());
	}
	
	@Override
	public void mobileChangedClothes(Mobile wearingMobile) {
		try {
			clientHandler.sendToClient(new CharacterDraw(wearingMobile));
		} catch (IOException e) {
			throw new IntercomException(e);
		}
	}

	@Override
	public void mobileChanged(Mobile mobile) {
		try {
			clientHandler.sendToClient(new UpdatePlayer(mobile));
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}

	@Override
	public void mobileDroppedCloth(Mobile mobile, Item droppedCloth) {
		try {
			clientHandler.sendToClient(new DeleteObject(droppedCloth.getSerialId()));
			// TODO: send sound (0x54) too - e.g., 54 01 00 57 00 00 0F 41  01 C8 00 00
		} catch (IOException e) {
			throw new ProtocolException(e);
		}
	}

	@Override
	public void groundItemsCreated(Collection<Item> items) {
		try {
			for (Item item : items) {
				clientHandler.sendToClient(new ObjectInfo(item));
			}
		} catch (IOException e) {
			throw new IntercomException(e);
		}
	}

	// ====================== COMBAT =========================
	public List<Message> handle(WarMode warMode) {
		session.toggleWarMode(warMode.isWar());
		return asList(warMode, new CharacterDraw(session.getMobile()), new AttackSucceed(0));
	}
	
	@Override
	public void mobileChangedWarMode(Mobile mobile) {
		try {								
			clientHandler.sendToClient(new CharacterDraw(mobile));
		} catch (IOException e) {
			throw new IntercomException(e);
		}		
	}
	
	public List<Message> handle(AttackRequest attackRequest) {
		var attacked = core.findMobileByID(attackRequest.getMobileID());

		var mobile = session.getMobile();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} is attacking {} ", mobile, attacked);
		}

		combatSystem.beginCombat(mobile, attacked);
		//combatSystem.attackStarted(session, attacked);
		//session.attack(attacked);

		return asList(new AttackOK(attacked),
				new FightOccurring(mobile, attacked),
				new AttackSucceed(attacked));
	}
	
	@Override
	public void mobileAttack(Mobile attacker, int attackerDamage, Mobile attacked) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} attacked by {}", attacked, attacker);
		}

		try {
			if (attacked.equals(session.getMobile())) {
				clientHandler.sendToClient(new CharacterAnimation(attacker, AnimationRepeat.ONCE, AnimationType.ATTACK_WITH_SWORD_OVER_AND_SIDE, 10, AnimationDirection.FORWARD),
						new AttackOK(attacker.getSerialId()),
						new AttackSucceed(attacker),
						new FightOccurring(attacked, attacker),
						new Damage(attacker, attackerDamage));
			} else {
				clientHandler.sendToClient(new Damage(attacker, attackerDamage), new StatusBarInfo(attacker),
						new CharacterAnimation(attacker, AnimationRepeat.ONCE, AnimationType.ATTACK_WITH_SWORD_OVER_AND_SIDE, 10, AnimationDirection.FORWARD));
			}
		} catch (IOException e) {
			throw new IntercomException(e);
		}		
	}
	
	@Override
	public void mobileAttackFinished(Mobile attacker) {
		try {
			LOGGER.debug("{} Attack finished {}", session.getMobile(), attacker);
			//combatSystem.combatFinished(attacker, session.getMobile());
			clientHandler.sendToClient(new AttackSucceed(0));
		} catch (IOException e) {
			throw new IntercomException(e);
		}
	}

	@Override
	public void mobileDamaged(Mobile mobile, int damage) {
		try {

			//new CharacterAnimation(opponent, AnimationRepeat.ONCE, AnimationType.ATTACK_WITH_SWORD_OVER_AND_SIDE, 100, AnimationDirection.FORWARD)
			clientHandler.sendToClient(new StatusBarInfo(mobile), new CharacterAnimation(mobile, AnimationRepeat.ONCE, AnimationType.GET_HIT, 10, AnimationDirection.BACKWARD), new Damage(mobile, damage));
		} catch (IOException e) {
			throw new IntercomException(e);
		}
	}

	// ====================== END COMBAT ======================
	
	public void handle(GenericAOSCommands commands) {
		// TODO handle GenericAOSCommands
	}

	// ======================= HANDLE HELP ====================
	public List<Message> handle(RequestHelp requestHelp) {
		System.out.println("User "+session.getMobile().getName()+" requested help");
		return Collections.singletonList(new WarMode(CharacterStatus.WarMode));
	}

	// ======================= HANDLE DEATH =====================


	@Override
	public void mobiledKilled(Mobile mobile) {
		try {
			if (mobile.isNpc()) {
				clientHandler.sendToClient(new DeathAnimation(mobile, 0x1FFD),new DeleteObject(mobile));
			} else {
				clientHandler.sendToClient(new DeathAnimation(mobile, 0x1FFD),
						new CharacterDraw(mobile),
						new StatusBarInfo(mobile),
						new AttackSucceed(0));
			}
		} catch (IOException exception) {
			throw new IntercomException(exception);
		}
	}

	// ======================= HANDLE CURSOR =====================

	@Override
	public void sendCursor(int cursorId, CursorType type, CursorTarget target) {
		try {
			clientHandler.sendToClient(new Cursor(target, cursorId, type));
		} catch (IOException exception) {
			throw new IntercomException(exception);
		}
	}

	public void handle(Cursor cursor) {
		session.selectCursor(cursor);
	}


	// ======================= HANDLE NPC =====================

	@Override
	public void npcOnRange(Collection<Mobile> npcs) {
		var messages = new ArrayList<>();
		for (Mobile npc : npcs) {
			messages.add(new CharacterDraw(npc));
			messages.add(new ObjectRevision(npc));
		}
		try {
			clientHandler.sendToClient(messages.toArray(new Message[]{}));
		} catch (IOException exception) {
			throw new IntercomException(exception);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GameController that = (GameController) o;
		return Objects.equals(controllerId, that.controllerId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(controllerId);
	}

	@Override
	public String toString() {
		return controllerId;
	}

}
