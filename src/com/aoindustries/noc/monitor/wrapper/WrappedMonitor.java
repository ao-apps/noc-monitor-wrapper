/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.wrapper;

import com.aoindustries.noc.monitor.common.Monitor;
import com.aoindustries.noc.monitor.common.Node;
import com.aoindustries.noc.monitor.common.RootNode;
import com.aoindustries.noc.monitor.common.SingleResultNode;
import com.aoindustries.noc.monitor.common.TableMultiResult;
import com.aoindustries.noc.monitor.common.TableMultiResultNode;
import com.aoindustries.noc.monitor.common.TableResultNode;
import com.aoindustries.util.IdentityKey;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * The general framework RMI server for wrapping and exposing monitors to the network.
 *
 * Exports the monitor and all nodes.  The wrapped monitor is not exported directly,
 * but rather this wrapper of it is exported.
 *
 * @author  AO Industries, Inc.
 */
public abstract class WrappedMonitor implements Monitor {

    private final Monitor wrapped;

    protected WrappedMonitor(Monitor wrapped) throws RemoteException {
        this.wrapped = wrapped;
    }

    /**
     * Gets the root node for the given locale, username, and password.  May
     * reuse existing root nodes.
     */
    @Override
    public WrappedRootNode login(Locale locale, String username, String password) throws RemoteException, IOException, SQLException {
        return wrapRootNode(wrapped.login(locale, username, password));
    }

    private final Map<IdentityKey<Node>,WrappedNode> nodeCache = new WeakHashMap<IdentityKey<Node>,WrappedNode>();
    WrappedNode wrapNode(Node node) throws RemoteException {
        if(node instanceof SingleResultNode) {
            return (WrappedNode)wrapSingleResultNode((SingleResultNode)node);
        } else if(node instanceof TableResultNode) {
            return (WrappedNode)wrapTableResultNode((TableResultNode)node);
        } else if(node instanceof TableMultiResultNode<?>) {
            return (WrappedNode)wrapTableMultiResultNode((TableMultiResultNode<?>)node);
        } else if(node instanceof RootNode) {
            return (WrappedNode)wrapRootNode((RootNode)node);
        } else {
            if(node instanceof WrappedNode) {
                WrappedNode wrapper = (WrappedNode)node;
                if(wrapper.monitor==this) return wrapper;
            }
            IdentityKey<Node> key = new IdentityKey<Node>(node);
            synchronized(nodeCache) {
                WrappedNode wrapper = nodeCache.get(key);
                if(wrapper==null) {
                    wrapper = newWrappedNode(node);
                    nodeCache.put(key, wrapper);
                }
                return wrapper;
            }
        }
    }
    protected abstract WrappedNode newWrappedNode(Node node) throws RemoteException;

    private final Map<IdentityKey<RootNode>,WrappedRootNode> rootNodeCache = new WeakHashMap<IdentityKey<RootNode>,WrappedRootNode>();
    WrappedRootNode wrapRootNode(RootNode node) throws RemoteException {
        if(node instanceof WrappedRootNode) {
            WrappedRootNode wrapper = (WrappedRootNode)node;
            if(wrapper.monitor==this) return wrapper;
        }
        IdentityKey<RootNode> key = new IdentityKey<RootNode>(node);
        synchronized(rootNodeCache) {
            WrappedRootNode wrapper = rootNodeCache.get(key);
            if(wrapper==null) {
                wrapper = newWrappedRootNode(node);
                rootNodeCache.put(key, wrapper);
            }
            return wrapper;
        }
    }
    protected abstract WrappedRootNode newWrappedRootNode(RootNode node) throws RemoteException;

    private final Map<IdentityKey<SingleResultNode>,WrappedSingleResultNode> singleResultNodeCache = new WeakHashMap<IdentityKey<SingleResultNode>,WrappedSingleResultNode>();
    WrappedSingleResultNode wrapSingleResultNode(SingleResultNode node) throws RemoteException {
        if(node instanceof WrappedSingleResultNode) {
            WrappedSingleResultNode wrapper = (WrappedSingleResultNode)node;
            if(wrapper.monitor==this) return wrapper;
        }
        IdentityKey<SingleResultNode> key = new IdentityKey<SingleResultNode>(node);
        synchronized(singleResultNodeCache) {
            WrappedSingleResultNode wrapper = singleResultNodeCache.get(key);
            if(wrapper==null) {
                wrapper = newWrappedSingleResultNode(node);
                singleResultNodeCache.put(key, wrapper);
            }
            return wrapper;
        }
    }
    protected abstract WrappedSingleResultNode newWrappedSingleResultNode(SingleResultNode node) throws RemoteException;

    private final Map<IdentityKey<TableMultiResultNode>,WrappedTableMultiResultNode> tableMultiResultNodeCache = new WeakHashMap<IdentityKey<TableMultiResultNode>,WrappedTableMultiResultNode>();
    <R extends TableMultiResult> WrappedTableMultiResultNode<R> wrapTableMultiResultNode(TableMultiResultNode<R> node) throws RemoteException {
        if(node instanceof WrappedTableMultiResultNode<?>) {
            WrappedTableMultiResultNode<R> wrapper = (WrappedTableMultiResultNode<R>)node;
            if(wrapper.monitor==this) return wrapper;
        }
        IdentityKey<TableMultiResultNode> key = new IdentityKey<TableMultiResultNode>(node);
        synchronized(tableMultiResultNodeCache) {
            WrappedTableMultiResultNode<R> wrapper = tableMultiResultNodeCache.get(key);
            if(wrapper==null) {
                wrapper = newWrappedTableMultiResultNode(node);
                tableMultiResultNodeCache.put(key, wrapper);
            }
            return wrapper;
        }
    }
    protected abstract <R extends TableMultiResult> WrappedTableMultiResultNode<R> newWrappedTableMultiResultNode(TableMultiResultNode<R> node) throws RemoteException;

    private final Map<IdentityKey<TableResultNode>,WrappedTableResultNode> tableResultNodeCache = new WeakHashMap<IdentityKey<TableResultNode>,WrappedTableResultNode>();
    WrappedTableResultNode wrapTableResultNode(TableResultNode node) throws RemoteException {
        if(node instanceof WrappedTableResultNode) {
            WrappedTableResultNode wrapper = (WrappedTableResultNode)node;
            if(wrapper.monitor==this) return wrapper;
        }
        IdentityKey<TableResultNode> key = new IdentityKey<TableResultNode>(node);
        synchronized(tableResultNodeCache) {
            WrappedTableResultNode wrapper = tableResultNodeCache.get(key);
            if(wrapper==null) {
                wrapper = newWrappedTableResultNode(node);
                tableResultNodeCache.put(key, wrapper);
            }
            return wrapper;
        }
    }
    protected abstract WrappedTableResultNode newWrappedTableResultNode(TableResultNode node) throws RemoteException;
}
