package org.richfaces.helloworld.domain.queue;

import org.ajax4jsf.component.UIQueue;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.helloworld.domain.util.componentInfo.ComponentInfo;

@Name("queue")
@Scope(ScopeType.SESSION)
public class Queue {
	private UIQueue myQueue = null;
	private boolean disabled;
	private boolean ignoreDupResponses;
	private String name;
	private boolean rendered;
	private int requestDelay;
	private int size;
	private String sizeExceededBehavior;
	private int timeout;	
	
	public Queue(){
		this.disabled = false;
		this.ignoreDupResponses = false;
		this.name = "DefaultQueue";
		this.rendered = true;
		this.requestDelay = 500;
		this.size = 3;
		this.sizeExceededBehavior = "dropNext";
		this.timeout = 5000;
	}
	
	public String addQueue(){
		ComponentInfo info = ComponentInfo.getInstance();
		info.addField(myQueue);
		return null;
	}
	
	public UIQueue getMyQueue() {
		return myQueue;
	}
	public void setMyQueue(UIQueue myQueue) {
		this.myQueue = myQueue;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public boolean isIgnoreDupResponses() {
		return ignoreDupResponses;
	}
	public void setIgnoreDupResponses(boolean ignoreDupResponses) {
		this.ignoreDupResponses = ignoreDupResponses;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isRendered() {
		return rendered;
	}
	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}
	public int getRequestDelay() {
		return requestDelay;
	}
	public void setRequestDelay(int requestDelay) {
		this.requestDelay = requestDelay;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getSizeExceededBehavior() {
		return sizeExceededBehavior;
	}
	public void setSizeExceededBehavior(String sizeExceededBehavior) {
		this.sizeExceededBehavior = sizeExceededBehavior;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
