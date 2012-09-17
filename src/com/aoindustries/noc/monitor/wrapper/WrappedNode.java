/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.wrapper;

import com.aoindustries.noc.monitor.common.AlertLevel;
import com.aoindustries.noc.monitor.common.Node;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedNode implements Node {

    final WrappedMonitor monitor;
    final private Node wrapped;

    protected WrappedNode(WrappedMonitor monitor, Node wrapped) {
        this.monitor = monitor;
        this.wrapped = wrapped;
    }

    @Override
    public WrappedNode getParent() throws RemoteException {
        return monitor.wrapNode(wrapped.getParent());
    }

    @Override
    public List<? extends WrappedNode> getChildren() throws RemoteException {
        List<? extends Node> children = wrapped.getChildren();
        // Wrap
        List<WrappedNode> localWrapped = new ArrayList<WrappedNode>(children.size());
        for(Node child : children) {
            localWrapped.add(monitor.wrapNode(child));
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
    
    @Override
    public boolean equals(Object O) {
        if(O==null) return false;
        if(!(O instanceof Node)) return false;

        // Unwrap this
        Node thisNode = WrappedNode.this;
        while(thisNode instanceof WrappedNode) thisNode = ((WrappedNode)thisNode).wrapped;

        // Unwrap other
        Node otherNode = (Node)O;
        while(otherNode instanceof WrappedNode) otherNode = ((WrappedNode)otherNode).wrapped;

        // Check equals
        return thisNode.equals(otherNode);
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }
}
