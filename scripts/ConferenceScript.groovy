
import com.admtel.telephonyserver.core.*
import com.admtel.telephonyserver.events.*
import com.admtel.telephonyserver.radius.*;
import com.admtel.telephonyserver.events.Event.EventType;
import com.admtel.telephonyserver.prompts.*;

import org.apache.log4j.Logger;
import groovyx.net.ws.WSClient;

public class ConferenceScript extends Script {
	static final Logger log = Logger.getLogger(ConferenceScript.class)	

	static ThreadLocal wsClients = new ThreadLocal()
	static String TOKEN  = "1234"
	
	def conferenceDTO = null;

	def getProxy(){
		def proxy = wsClients.get()
		if (proxy==null){
			proxy = new WSClient("http://localhost:8080/adm-appserver/AppServerAPI?WSDL", this.class.classLoader)
			proxy.initialize()
			wsClients.set(proxy)
		}
		return proxy
	}	
	
	def getConference(conferenceNumber){
		getProxy().getConferenceByNumber(TOKEN, conferenceNumber)
	}
				
	@Override
	public String getDisplayStr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onStart(Object data) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onTimer() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processEvent(Event event) {
		
		try{
			log.debug "Calling ${currentState.toString()} on${event.getEventType()}"
			currentState = currentState."on${event.getEventType()}"(event)
		}
		catch (MissingMethodException e){
			
		}
	}		
	
	def currentState = [this] as conference.InitialState
}
