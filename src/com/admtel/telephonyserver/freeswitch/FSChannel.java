package com.admtel.telephonyserver.freeswitch;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.admtel.telephonyserver.config.SwitchType;
import com.admtel.telephonyserver.core.AdmAddress;
import com.admtel.telephonyserver.core.Channel;
import com.admtel.telephonyserver.core.QueuedMessageHandler;
import com.admtel.telephonyserver.core.Result;
import com.admtel.telephonyserver.core.Script;
import com.admtel.telephonyserver.core.ScriptManager;
import com.admtel.telephonyserver.core.Switch;
import com.admtel.telephonyserver.core.Switches;
import com.admtel.telephonyserver.events.AnsweredEvent;
import com.admtel.telephonyserver.events.ConferenceJoinedEvent;
import com.admtel.telephonyserver.events.ConferenceLeftEvent;
import com.admtel.telephonyserver.events.ConferenceMutedEvent;
import com.admtel.telephonyserver.events.ConferenceTalkEvent;
import com.admtel.telephonyserver.events.DialFailedEvent;
import com.admtel.telephonyserver.events.DialStartedEvent;
import com.admtel.telephonyserver.events.Event;
import com.admtel.telephonyserver.events.HangupEvent;
import com.admtel.telephonyserver.events.AlertingEvent;
import com.admtel.telephonyserver.events.LinkedEvent;
import com.admtel.telephonyserver.events.PlayAndGetDigitsEndedEvent;
import com.admtel.telephonyserver.events.PlayAndGetDigitsFailedEvent;
import com.admtel.telephonyserver.events.PlayAndGetDigitsStartedEvent;
import com.admtel.telephonyserver.events.PlaybackEndedEvent;
import com.admtel.telephonyserver.events.PlaybackFailedEvent;
import com.admtel.telephonyserver.events.PlaybackStartedEvent;
import com.admtel.telephonyserver.events.QueueBridgedEvent;
import com.admtel.telephonyserver.events.QueueFailedEvent;
import com.admtel.telephonyserver.events.QueueJoinedEvent;
import com.admtel.telephonyserver.freeswitch.commands.FSMemberMuteCommand;
import com.admtel.telephonyserver.freeswitch.commands.FSQueueCommand;
import com.admtel.telephonyserver.freeswitch.events.FSChannelBridgeEvent;
import com.admtel.telephonyserver.freeswitch.events.FSChannelCreateEvent;
import com.admtel.telephonyserver.freeswitch.events.FSChannelDataEvent;
import com.admtel.telephonyserver.freeswitch.events.FSChannelDestroyEvent;
import com.admtel.telephonyserver.freeswitch.events.FSChannelEvent;
import com.admtel.telephonyserver.freeswitch.events.FSChannelExecuteCompleteEvent;
import com.admtel.telephonyserver.freeswitch.events.FSChannelExecuteEvent;
import com.admtel.telephonyserver.freeswitch.events.FSChannelOriginateEvent;
import com.admtel.telephonyserver.freeswitch.events.FSChannelOutgoingEvent;
import com.admtel.telephonyserver.freeswitch.events.FSChannelStateEvent;
import com.admtel.telephonyserver.freeswitch.events.FSChannelStateEvent.CallState;
import com.admtel.telephonyserver.freeswitch.events.FSCommandReplyEvent;
import com.admtel.telephonyserver.freeswitch.events.FSConferenceJoinedEvent;
import com.admtel.telephonyserver.freeswitch.events.FSConferenceMuteEvent;
import com.admtel.telephonyserver.freeswitch.events.FSConferenceRemovedEvent;
import com.admtel.telephonyserver.freeswitch.events.FSConferenceTalkingEvent;
import com.admtel.telephonyserver.freeswitch.events.FSDtmfEvent;
import com.admtel.telephonyserver.freeswitch.events.FSEvent;
import com.admtel.telephonyserver.freeswitch.events.FSQueueEvent;
import com.admtel.telephonyserver.freeswitch.events.FSChannelStateEvent.ChannelState;
import com.admtel.telephonyserver.freeswitch.events.FSEvent.EventType;
import com.admtel.telephonyserver.utils.AdmUtils;
import com.admtel.telephonyserver.utils.CodecsUtils;
import com.admtel.telephonyserver.utils.PromptsUtils;
import com.admtel.telephonyserver.events.QueueLeftEvent;

public class FSChannel extends Channel {

	private IoSession session;
	private State currentState = new NullState();

	static Logger fsChannelLog = Logger.getLogger(FSChannel.class);

	private abstract class State {
		public abstract void processEvent(FSEvent fsEvent);

		public abstract boolean onTimer(Object data);

		Result result;

		public void setResult(Result result) {
			this.result = result;
		}

		public Result getResult() {
			return this.result;
		}
	}

	private class IdleState extends State {

		public IdleState() {
			session.write(buildMessage(getId(), "execute", "set",
					"hangup_after_bridge=false"));
		}

		@Override
		public boolean onTimer(Object data) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void processEvent(FSEvent fsEvent) {
			// TODO Auto-generated method stub

		}

	}

	private class AlertingState extends State {

		@Override
		public boolean onTimer(Object data) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void processEvent(FSEvent fsEvent) {
			switch (fsEvent.getEventType()) {
			case ChannelData: {
				FSChannelDataEvent cde = (FSChannelDataEvent) fsEvent;
				String admArgs = cde.getValue("variable_adm_args");
				getChannelData().addDelimitedVars(admArgs, "&");
				String sipReqParams = cde.getValue("variable_sip_req_params");
				getChannelData().addDelimitedVars(
						CodecsUtils.urlDecode(sipReqParams), ";");

				// Create script
				ScriptManager.getInstance().createScript(FSChannel.this);
			}
				break;
			}

		}

	}

	private class NullState extends State {

		@Override
		public void processEvent(FSEvent fsEvent) {

			Event result = null;

			switch (fsEvent.getEventType()) {
			case ChannelCreate: {
				FSChannelCreateEvent cce = (FSChannelCreateEvent) fsEvent;
				if (FSChannel.this.getAcctUniqueSessionId() == null) {
					FSChannel.this.setAcctUniqueSessionId(UUID.randomUUID()
							.toString());
				}
			}
				break;
			case ChannelState: {
				FSChannelStateEvent cse = (FSChannelStateEvent) fsEvent;
				if (cse.getChannelState() == ChannelState.CS_ROUTING
						&& cse.getCallState() == CallState.RINGING) {
					getChannelData().setCalledNumber(cse.getCalledIdNum());
					getChannelData().setCallerIdNumber(cse.getCallerIdNum());
					getChannelData().setCallerIdName(cse.getCallerIdNum());
					getChannelData().setUserName(cse.getUserName());
					getChannelData().setLoginIP(cse.getChannelAddress());
					getChannelData().setAccountCode(cse.getAccountCode());
					getChannelData().setServiceNumber(cse.getCalledIdNum());
					currentState = new AlertingState();
					FSChannel.this.onEvent(new AlertingEvent(FSChannel.this));
				}
			}
				break;
			}

		}

		@Override
		public boolean onTimer(Object data) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private class PlayingAndGettingDigits extends State {
		private int max;
		private long timeout;
		private String terminators;
		private boolean interruptPlay;
		private String prompt;
		private String termDigit;

		public PlayingAndGettingDigits(int max, String prompt, long timeout,
				String terminators, boolean interruptPlay) {

			this.max = max;
			this.timeout = timeout;
			this.terminators = terminators;
			this.interruptPlay = interruptPlay;
			this.prompt = prompt;
			this.termDigit = "";
			execute();

		}

		public PlayingAndGettingDigits(int max, String[] prompts, long timeout,
				String terminators, boolean interruptPlay) {
			this.prompt = PromptsUtils.expandPrompts(prompts, "&",
					SwitchType.Freeswitch);
			this.max = max;
			this.timeout = timeout;
			this.terminators = terminators;
			this.interruptPlay = interruptPlay;
			this.termDigit = "";
			execute();
		}

		@Override
		public void processEvent(FSEvent event) {

			switch (event.getEventType()) {
			case CommandReply: {
				FSCommandReplyEvent cre = (FSCommandReplyEvent) event;
				if (!cre.isSuccess()) {
					FSChannel.this.onEvent(new PlayAndGetDigitsFailedEvent(
							FSChannel.this, cre.getResultDescription()));
				}
			}
				break;
			case ChannelExecute: { // TODO look for failure events
				String application = event.getValue("Application");
				if (application.equals("read")) {
					FSChannel.this.onEvent(new PlayAndGetDigitsStartedEvent(
							FSChannel.this));
				}
			}
				break;
			case ChannelExecuteComplete: { // TODO look for failure events
				String application = event
						.getValue("variable_current_application");
				if (application.equals("read")) {
					String readResult = event.getValue("variable_read_result");
					if (readResult == null || !readResult.equals("success")) {
						FSChannel.this.onEvent(new PlayAndGetDigitsFailedEvent(
								FSChannel.this, readResult));
					} else {
						String digits = event.getValue("variable_digits");
						PlayAndGetDigitsEndedEvent pee = new PlayAndGetDigitsEndedEvent(
								FSChannel.this, digits);
						pee.setTerminatingDigit(this.termDigit);
						FSChannel.this.onEvent(pee);

					}
				}
			}
				break;
			case DTMF: {
				FSDtmfEvent dtmfEvent = (FSDtmfEvent) event;
				if (terminators.indexOf(dtmfEvent.getDtmf()) != -1) {
					this.termDigit = dtmfEvent.getDtmf();
				}
			}
				break;
			}
		}

		public void execute() {

			session.write(buildMessage(getId(), "execute", "set",
					"playback_delimiter=&"));
			if (!this.interruptPlay) {
				session.write(buildMessage(getId(), "execute", "set",
						"sleep_eat_digits=false"));
			} else {
				session.write(buildMessage(getId(), "execute", "set",
						"sleep_eat_digits=true"));
			}
			session.write(buildMessage(getId(), "execute", "read", String
					.format("%d %d %s digits %d %s", 0, max, prompt, timeout,
							terminators)));

		}

		@Override
		public boolean onTimer(Object data) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private class PlayingbackState extends State {

		public PlayingbackState(String prompt, String terminators) {

			session.write(buildMessage(getId(), "execute", "set",
					"playback_delimiter=&"));
			session.write(buildMessage(getId(), "execute", "set",
					"playback_terminators=" + terminators));
			session.write(buildMessage(getId(), "execute", "playback", prompt));
		}

		@Override
		public void processEvent(FSEvent event) {

			switch (event.getEventType()) {
			case CommandReply: {
				FSCommandReplyEvent cre = (FSCommandReplyEvent) event;
				if (!cre.isSuccess()) {
					FSChannel.this.onEvent(new PlaybackFailedEvent(
							FSChannel.this, cre.getResultDescription()));
				}
			}
				break;
			case ChannelExecute: { // TODO look for failure events
				String application = event.getValue("Application");
				if (application.equals("playback")) {
					FSChannel.this.onEvent(new PlaybackStartedEvent(
							FSChannel.this));
				}
			}
				break;
			case ChannelExecuteComplete: { // TODO look for failure events
				String application = event.getValue("Application");
				if (application.equals("playback")) {
					String returnCode = CodecsUtils.urlDecode(event.getValue(
							"variable_current_application_response", ""));
					fsChannelLog.debug("Return code is " + returnCode);
					if (returnCode.equals("FILE PLAYED")) {
						FSChannel.this
								.onEvent(new PlaybackEndedEvent(
										FSChannel.this,
										event.getValue(
												"variable_playback_terminator_used",
												""), ""));
					} else {
						FSChannel.this.onEvent(new PlaybackFailedEvent(
								FSChannel.this, returnCode)); // TODO return
																// unified error
																// codes
					}
				}
			}
				break;
			}
		}

		@Override
		public boolean onTimer(Object data) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private class DialingState extends State {

		final Logger log = Logger.getLogger(DialingState.class);
		
		AdmAddress admAddress;

		public DialingState(String address, long timeout) {
		
			admAddress = new AdmAddress(address);
			
			String fsAddress = _switch.getAddressTranslator().translate(admAddress);
			
			session.write(buildMessage(getId(), "execute", "bridge", fsAddress));
		}

		@Override
		public boolean onTimer(Object data) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void processEvent(FSEvent fsEvent) {
			switch (fsEvent.getEventType()) {
			case CommandReply: {
				FSCommandReplyEvent cre = (FSCommandReplyEvent) fsEvent;
				if (!cre.isSuccess()) {
					FSChannel.this.onEvent(new DialFailedEvent(FSChannel.this));
				}
			}
				break;
			case ChannelOriginate: {
				FSChannelOriginateEvent coe = (FSChannelOriginateEvent) fsEvent;
				FSChannel otherChannel = (FSChannel) FSChannel.this.getSwitch()
						.getChannel(coe.getChannelId());

				if (otherChannel != null){
					otherChannel.getChannelData().setLoginIP(coe.getChannelAddress());					
					otherChannel.getChannelData().setCalledNumber(admAddress.getDestination());
					otherChannel.setAcctUniqueSessionId(FSChannel.this.getAcctUniqueSessionId());
					otherChannel.setUserName(FSChannel.this.getUserName());
					otherChannel.getChannelData().setDestinationNumberIn(FSChannel.this.getChannelData().getCalledNumber());
					otherChannel.getChannelData().setRemoteIP(FSChannel.this.getLoginIP());				
				}
				FSChannel.this.onEvent(new DialStartedEvent(FSChannel.this,
						otherChannel));
			}
				break;

			}

		}

	}

	private class JoinConferenceState extends State {
		String conferenceId;
		boolean moderator;
		boolean muted;
		boolean deaf;

		public JoinConferenceState(String conferenceId, boolean moderator,
				boolean muted, boolean deaf) {

			this.conferenceId = conferenceId;
			this.moderator = moderator;
			this.muted = muted;
			this.deaf = deaf;
			String flags = "";
			String command;
			if (moderator) {
				flags = AdmUtils.addWithDelimiter(flags, "moderator", "|");
			}
			if (muted) {
				flags = AdmUtils.addWithDelimiter(flags, "mute", "|");
			}
			if (deaf) {
				flags = AdmUtils.addWithDelimiter(flags, "deaf", "|");
			}
			if (!flags.isEmpty()) {
				command = String.format("conferenceId+flags{%s}", flags);
			} else {
				command = conferenceId;
			}
			session.write(buildMessage(getId(), "execute", "conference",
					command));
		}

		@Override
		public boolean onTimer(Object data) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void processEvent(FSEvent fsEvent) {
			switch (fsEvent.getEventType()) {
			case ConferenceJoined: {
				FSConferenceJoinedEvent cje = (FSConferenceJoinedEvent) fsEvent;
				FSChannel.this.onEvent(new ConferenceJoinedEvent(
						FSChannel.this, cje.getConferenceName(), cje
								.getMemberId(), moderator, muted, deaf));
			}
				break;
			case ConferenceRemoved: {
				FSConferenceRemovedEvent cre = (FSConferenceRemovedEvent) fsEvent;
				FSChannel.this.onEvent(new ConferenceLeftEvent(FSChannel.this,
						cre.getConferenceName(), cre.getMemberId()));
			}
				break;
			case ConferenceTalking: {
				FSConferenceTalkingEvent cte = (FSConferenceTalkingEvent) fsEvent;
				FSChannel.this
						.onEvent(new ConferenceTalkEvent(FSChannel.this, cte
								.getConferenceName(), cte.getMemberId(), cte
								.isOn()));
			}
				break;
			case ConferenceMute: {
				FSConferenceMuteEvent cme = (FSConferenceMuteEvent) fsEvent;
				FSChannel.this.onEvent(new ConferenceMutedEvent(FSChannel.this,
						cme.getConferenceName(), cme.getMemberId(), cme
								.isMuted()));
			}
				break;
			}

		}

	}

	private class QueueState extends State {

		private String queueName;
		private Boolean isAgent;

		public QueueState(String queueName, boolean agent) {
			this.isAgent = agent;
			this.queueName = queueName;

			session.write(buildMessage(getId(), "execute", "set",
					"hangup_after_bridge=false"));
			FSQueueCommand queueCmd = new FSQueueCommand(FSChannel.this,
					queueName, isAgent);
			session.write(queueCmd);
		}

		@Override
		public boolean onTimer(Object data) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void processEvent(FSEvent event) {
			switch (event.getEventType()) {
			case CommandReply: {
				FSCommandReplyEvent cre = (FSCommandReplyEvent) event;
				if (!cre.isSuccess()) {
					FSChannel.this.onEvent(new QueueFailedEvent(FSChannel.this,
							queueName, cre.getResultDescription()));
				}
			}
				break;
			case Queue: {
				FSQueueEvent queueEvent = (FSQueueEvent) event;
				switch (queueEvent.getAction()) {
				case push:
					FSChannel.this.onEvent(new QueueJoinedEvent(FSChannel.this,
							queueEvent.getQueueName(), isAgent));
					break;
				case bridgecallerstart: {
					Channel otherChannel = _switch.getChannel(queueEvent
							.getPeerChannel());
					FSChannel.this.onEvent(new QueueBridgedEvent(
							FSChannel.this, otherChannel));
				}
					break;

				// TODO leave
				}

			}
				break;
			}

		}

	}
	
	private class AcdQueueState extends State{

		String queueName;
		public AcdQueueState(String queueName){
			this.queueName = queueName;
		}
		@Override
		public void processEvent(FSEvent fsEvent) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onTimer(Object data) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}

	/*
	 * private MessageHandler messageHandler = new QueuedMessageHandler() {
	 * 
	 * @Override public void onMessage(Object message) { if (message == null ||
	 * !(message instanceof FSEvent)) { return; } FSEvent fsEvent = (FSEvent)
	 * message;
	 * 
	 * log .debug(String .format(
	 * "%s, START processing event (%s) state (%s), internalState(%s)",
	 * FSChannel.this, fsEvent, state, currentState.getClass()
	 * .getSimpleName()));
	 * 
	 * switch (fsEvent.getEventType()) { case ChannelDestroy: {
	 * FSChannelDestroyEvent cde = (FSChannelDestroyEvent) fsEvent; } break;
	 * case ChannelHangup: { FSChannel.this.onEvent(new
	 * HangupEvent(FSChannel.this)); } break; case ChannelAnswered: {
	 * currentState = new IdleState(); FSChannel.this.onEvent(new
	 * AnsweredEvent(FSChannel.this)); } break; } if (currentState != null) {
	 * currentState.processEvent(fsEvent); }
	 * 
	 * if (fsEvent.getEventType() == EventType.ChannelDestroy) {
	 * getListeners().clear();
	 * FSChannel.this.getSwitch().removeChannel(FSChannel.this); }
	 * log.debug(String.format(
	 * "%s, END processing event (%s) state (%s), internalState(%s)",
	 * FSChannel.this, fsEvent, state,
	 * currentState.getClass().getSimpleName()));
	 * 
	 * } };
	 */
	public FSChannel(Switch _switch, String id, IoSession session) {
		super(_switch, id);
		setIoSession(session);
	}

	public void setIoSession(IoSession session) {
		this.session = session;
	}

	@Override
	public Result internalAnswer() {
		session.write(buildMessage(FSChannel.this.getId(), "execute", "answer",
				""));
		return Result.Ok;
	}

	@Override
	public Result internalDial(String address, long timeout) {
		if (address != null && address.length() > 0) {
			currentState = new DialingState(address, timeout);
		} else {
			fsChannelLog.warn(String.format("%s, invalid dial string %s",
					this.getId(), address));
			return Result.InvalidParameters;
		}
		return currentState.getResult();
	}

	@Override
	public Result internalHangup(Integer cause) {
		session.write(String.format(
				"SendMsg %s\ncall-command: hangup\nhangup-cause: %d", getId(),
				cause));
		return Result.Ok;
	}

	@Override
	public Result internalPlayAndGetDigits(int max, String prompt,
			long timeout, String terminators, boolean interruptPlay) {
		currentState = new PlayingAndGettingDigits(max, prompt, timeout,
				terminators, interruptPlay);
		return currentState.getResult();
	}

	@Override
	public Result internalPlayAndGetDigits(int max, String[] prompt,
			long timeout, String terminators, boolean interruptPlay) {
		currentState = new PlayingAndGettingDigits(max, prompt, timeout,
				terminators, interruptPlay);
		return currentState.getResult();
	}

	@Override
	public Result internalPlayback(String[] prompt, String terminators) {
		String p = PromptsUtils.expandPrompts(prompt, "&",
				SwitchType.Freeswitch);
		return internalPlayback(p, terminators);
	}

	@Override
	public Result internalPlayback(String prompt, String terminators) {
		currentState = new PlayingbackState(prompt, terminators);
		return currentState.getResult();
	}

	@Override
	public Result internalJoinConference(String conferenceId,
			boolean moderator, boolean startMuted, boolean startDeaf) {
		currentState = new JoinConferenceState(conferenceId, moderator,
				startMuted, startDeaf);
		return currentState.getResult();
	}

	private String buildMessage(String uuid, String command, String app,
			String arg) {
		return String
				.format("SendMsg %s\ncall-command: %s\nexecute-app-name: %s\nexecute-app-arg: %s\n",
						uuid, command, app, arg);
	}

	@Override
	public boolean onTimer(Object data) {
		return super.onTimer(data);

	}

	@Override
	public Result internalQueue(String queueName, boolean agent) {
		currentState = new QueueState(queueName, agent);
		return currentState.getResult();
	}

	@Override
	synchronized protected void processNativeEvent(Object event) {
		if (event instanceof FSEvent) {
			FSEvent fsEvent = (FSEvent) event;
			if (!(event instanceof FSChannelExecuteEvent || event instanceof FSChannelExecuteCompleteEvent)) {
				fsChannelLog
						.debug(String
								.format("%s, START processing event (%s) state (%s), internalState(%s)",
										FSChannel.this, fsEvent, state,
										currentState.getClass().getSimpleName()));
			}

			switch (fsEvent.getEventType()) {
			case ChannelDestroy: {
				FSChannelDestroyEvent cde = (FSChannelDestroyEvent) fsEvent;
			}
				break;

			case ChannelHangup: {
				FSChannel.this.onEvent(new HangupEvent(FSChannel.this));
			}
				break;
			case ChannelAnswered: {
				currentState = new IdleState();
				FSChannel.this.onEvent(new AnsweredEvent(FSChannel.this));
			}
				break;
			case ChannelBridge: {
				FSChannelBridgeEvent cbe = (FSChannelBridgeEvent) fsEvent;
				String otherChannelId = cbe.getPeerChannelId();
				if (!cbe.getChannelId().equals(getId())) {
					otherChannelId = cbe.getChannelId();
				}
				Channel otherChannel = _switch.getChannel(otherChannelId);
				FSChannel.this.onEvent(new LinkedEvent(this, otherChannel));
			}
				break;
			case ChannelState: {
				FSChannelStateEvent cse = (FSChannelStateEvent) fsEvent;
				if (cse.getChannelState() == ChannelState.CS_REPORTING
						&& cse.getCallState() == CallState.HANGUP) {
					FSChannel.this.onEvent(new HangupEvent(FSChannel.this));
				}
			}
				break;
			}
			if (currentState != null) {
				currentState.processEvent(fsEvent);
			}
			if (!(event instanceof FSChannelExecuteEvent || event instanceof FSChannelExecuteCompleteEvent)) {
				fsChannelLog
						.debug(String
								.format("%s, END processing event (%s) state (%s), internalState(%s)",
										FSChannel.this, fsEvent, state,
										currentState.getClass().getSimpleName()));
			}

		}

	}

	@Override
	public Result internalConferenceMute(String conferenceId, String memberId,
			boolean mute) {
		FSMemberMuteCommand cmd = new FSMemberMuteCommand(this, conferenceId,
				memberId, mute);
		fsChannelLog.trace(String.format("%s, on channel %s", cmd, this));
		session.write(cmd);
		return Result.Ok;
	}

	@Override
	public Result internalAcdQueue(String queueName) {
		// TODO Auto-generated method stub
		return null;
	}
}
