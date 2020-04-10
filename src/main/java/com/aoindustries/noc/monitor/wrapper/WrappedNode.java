/*
 * Copyright 2012, 2020 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.wrapper;

import com.aoindustries.noc.monitor.common.AlertLevel;
import com.aoindustries.noc.monitor.common.Node;
import com.aoindustries.util.WrappedException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedNode implements Node {

	final WrappedMonitor monitor;
	final private Node wrapped;
	final private UUID uuid;

	protected WrappedNode(WrappedMonitor monitor, Node wrapped) {
		this.monitor = monitor;
		this.wrapped = wrapped;
		this.uuid    = UUID.randomUUID();
	}

	@Override
	public WrappedNode getParent() throws RemoteException {
		Node wrappedParent = wrapped.getParent();
		return monitor.wrapNode(wrappedParent, wrappedParent.getUuid());
	}

	@Override
	public List<? extends WrappedNode> getChildren() throws RemoteException {
		List<? extends Node> children = wrapped.getChildren();
		// Wrap
		List<WrappedNode> localWrapped = new ArrayList<WrappedNode>(children.size());
		for(Node child : children) {
			localWrapped.add(monitor.wrapNode(child, child.getUuid()));
		}
		return Collections.unmodifiableList(localWrapped);
	}

	@Override
	public AlertLevel getAlertLevel() throws RemoteException {
		return wrapped.getAlertLevel();
	}

	@Override
	public String getAlertMessage() throws RemoteException {
		return wrapped.getAlertMessage();
	}

	@Override
	public boolean getAllowsChildren() throws RemoteException {
		return wrapped.getAllowsChildren();
	}

	@Override
	public String getId() throws RemoteException {
		return wrapped.getId();
	}

	@Override
	public String getLabel() throws RemoteException {
		return wrapped.getLabel();
	}

	/**
	 * After wrapping, the wrapped node gets a new UUID.
	 */
	@Override
	public UUID getUuid() throws RemoteException {
		return uuid;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Node)) return false;
		Node other = (Node)obj;
		try {
			return uuid.equals(other.getUuid());
		} catch(RemoteException err) {
			throw new WrappedException(err);
		}
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

	@Override
	public String toString() {
		try {
			return getLabel();
		} catch(RemoteException err) {
			throw new WrappedException(err);
		}
	}
}
