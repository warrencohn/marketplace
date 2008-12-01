package edu.umich.itcs;

import org.apache.log4j.Logger;

import com.ramsayconz.wocore.CoreProperties;
import com.ramsayconz.wocore.CoreSession;
import com.webobjects.appserver.WOApplication;
import com.webobjects.foundation.NSMutableDictionary;


public class ITCSSession extends CoreSession {
	private static final Logger 	logger = Logger.getLogger (ITCSSession.class);

	private static CoreProperties					_coreSesProps = null;

	protected NSMutableDictionary<String, Object> 	sessionInfo = null;

	public ITCSSession () {
		super ();
		logger.trace("-----+ constructor");
	}

	@Override
	public void sessionWillAwake() {
		super.sessionWillAwake();
		logger.trace("-->--> sessionWillAwake");

		if (null == _coreSesProps) {
			_coreSesProps = SessionProperties.getSessionProperties(WOApplication.application().name());
			_coreSesProps.alphaDump(true);
		}

		if (null == sessionInfo) {
			sessionInfo = new NSMutableDictionary<String, Object>();
			sessionInfo.takeValueForKey(0, "HitCount");
			SessionInfoDict.add_SessionInfo(sessionID(), sessionInfo);
		}

		logger.trace("<--<-- sessionWillAwake");
	}
	
	@Override
	public void awake() {
		super.awake();

		if (!SessionInfoDict.any_SessionInfo(sessionID())) {
			logger.error("     | unknown sessionID :" + sessionID() + ".  We are terminating this session.");
			terminate();
		}
	}
	
	@Override
	public void terminate() {
		logger.trace("-->--> terminate()  [id=" + sessionID() + "]");
		SessionInfoDict.log_SessionInfo();
		SessionInfoDict.sub_SessionInfo(sessionID());
		super.terminate();
	}

	public static CoreProperties getSesProps() {
		return _coreSesProps;
	}

	//----------------------------------------------------------------------------------------------------------------

	/**
	 * Finds the session in SessionInfo with key sessionID and +1 its count of actions.
	 */
	protected void incHitCount() {
		final Integer clicks = getHitCount();
		if (clicks != null) {
			sessionInfo.takeValueForKey(clicks+1, "HitCount");
		}
	}

	/**
	 * Returns a count of the number of times the session indicated by sessionID has been through the request-response
	 * loop. Used to set the session timeout to a sensible length after the user has clicked at east once in Marketplace
	 * and may be used to stop showing a splash panel after a couple of hits.
	 *
	 * @return the number of actions recorded for sessionID
	 */
	public Integer getHitCount() {
		return (Integer) sessionInfo.valueForKey("HitCount");
	}

}