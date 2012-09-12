/*
 * Copyright 2012 by AO Industries, Inc.,
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
import java.util.concurrent.Callable;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedNode implements Node {

    final protected WrappedMonitor monitor;
    final private Node wrapped;

    protected WrappedNode(WrappedMonitor monitor, Node wrapped) {
        this.monitor = monitor;
        this.wrapped = wrapped;
    }

    @Override
    final public WrappedNode getParent() throws RemoteException {
        return monitor.call(
            new Callable<WrappedNode>() {
                @Override
                public WrappedNode call() throws RemoteException {
                    return monitor.wrapNode(wrapped.getParent());
                }
            }
        );
    }

    @Override
    final public List<? extends WrappedNode> getChildren() throws RemoteException {
        return monitor.call(
            new Callable<List<? extends WrappedNode>>() {
                @Override
                public List<? extends WrappedNode> call() throws RemoteException {
                    List<? extends Node> children = wrapped.getChildren();
                    // Wrap
                    List<WrappedNode> localWrapped = new ArrayList<WrappedNode>(children.size());
                    for(Node child : children) {
                        localWrapped.add(monitor.wrapNode(child));
                    }
                    return Collections.unmodifiableList(localWrapped);
                }
            }
        );
    }

    @Override
    final public AlertLevel getAlertLevel() throws RemoteException {
        return monitor.call(
            new Callable<AlertLevel>() {
                @Override
                public AlertLevel call() throws RemoteException {
                    return wrapped.getAlertLevel();
                }
            }
        );
    }

    @Override
    final public String getAlertMessage() throws RemoteException {
        return monitor.call(
            new Callable<String>() {
                @Override
                public String call() throws RemoteException {
                    return wrapped.getAlertMessage();
                }
            }
        );
    }

    @Override
    final public boolean getAllowsChildren() throws RemoteException {
        return monitor.call(
            new Callable<Boolean>() {
                @Override
                public Boolean call() throws RemoteException {
                    return wrapped.getAllowsChildren();
                }
            }
        );
    }

    @Override
    final public String getId() throws RemoteException {
        return monitor.call(
            new Callable<String>() {
                @Override
                public String call() throws RemoteException {
                    return wrapped.getId();
                }
            }
        );
    }

    @Override
    final public String getLabel() throws RemoteException {
        return monitor.call(
            new Callable<String>() {
                @Override
                public String call() throws RemoteException {
                    return wrapped.getLabel();
                }
            }
        );
    }
    
    @Override
    final public boolean equals(final Object O) {
        try {
            return monitor.call(
                new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws RemoteException {
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
                }
            );
        } catch(RemoteException e) {
            throw new WrappedException(e);
        }
    }

    @Override
    final public int hashCode() {
        try {
            return monitor.call(
                new Callable<Integer>() {
                    @Override
                    public Integer call() throws RemoteException {
                        return wrapped.hashCode();
                    }
                }
            );
        } catch(RemoteException e) {
            throw new WrappedException(e);
        }
    }
}
