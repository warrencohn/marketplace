package edu.umich.marketplace.woc;

import com.ramsayconz.wocore.woc.CoreComponent;
import com.webobjects.appserver.WOContext;

public class ErrorPageDefault extends CoreComponent {

	public String 			defaultMessage = "Our apologies. There was a software error in Marketplace.  " + 
											 "This does not happen often and was probably not as a result of anything you did.  " +
											 "An email with details of this error has been sent for the attention of the " +
											 "MarketPlace developer and administrators, who will use it to help diagnose what " +
											 "happened.  Thank you for your patience -- if you suffer this error repeatedly, " +
											 "please send an email to the 'Contact Us' e-mail address address below for more " +
											 "immediate attention.";

	public ErrorPageDefault(WOContext context) {
		super(context);
	}

	private String 			_errorMessage = "", _panelTitle = "", _pageTitle = "";
	private String 			_linkBackToApplication, _linkTextToApplication;
	private boolean			_okToResume = false;

	public String getPanelTitle() {
		return (null == _panelTitle || _panelTitle.length() == 0) ? "Marketplace internal error" : _panelTitle;
	}

	public void setPanelTitle(String panelTitle) {
		_panelTitle = panelTitle;
	}

	public String getErrorMessage() {
		return (null == _errorMessage || _errorMessage.length() == 0) ? defaultMessage : _errorMessage ;
	}

	public void setErrorMessage(String errorMessage) {
		_errorMessage = errorMessage;
	}

	public String getLinkBackToApplication() {
		return _linkBackToApplication;
	}

	public void setLinkBackToApplication(String linkBackToApplication) {
		_linkBackToApplication = linkBackToApplication;
	}

	public String getLinkTextToApplication() {
		return _linkTextToApplication;
	}

	public void setLinkTextToApplication(String linkTextToApplication) {
		_linkTextToApplication = linkTextToApplication;
	}

	public String getPageTitle() {
		return _pageTitle;
	}

	public void setPageTitle(String title) {
		_pageTitle = title;
	}

	public boolean getOkToResume() {
		return _okToResume;
	}

	public void setOkToResume(boolean okToResume) {
		_okToResume = okToResume;
	}
}