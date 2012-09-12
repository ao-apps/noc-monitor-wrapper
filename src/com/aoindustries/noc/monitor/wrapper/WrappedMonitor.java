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
public abstract class WrappedMonitor<
    N extends WrappedNode,
    RN extends WrappedRootNode,
    SRN extends WrappedSingleResultNode,
    TMRN extends WrappedTableMultiResultNode,
    TRN extends WrappedTableResultNode
> implements Monitor {

    private final Monitor wrapped;

    protected WrappedMonitor(Monitor wrapped) throws RemoteException {
        this.wrapped = wrapped;
    }

    /**
     * Gets the root node for the given locale, username, and password.  May
     * reuse existing root nodes.
     */
    @Override
    public RN login(Locale locale, String username, String password) throws RemoteException, IOException, SQLException {
        return wrapRootNode(wrapped.login(locale, username, password));
    }

    private final Map<IdentityKey<Node>,N> nodeCache = new WeakHashMap<IdentityKey<Node>,N>();
    N wrapNode(Node node) throws RemoteException {
        if(node instanceof SingleResultNode) {
            return (N)wrapSingleResultNode((SingleResultNode)node);
        } else if(node instanceof TableResultNode) {
            return (N)wrapTableResultNode((TableResultNode)node);
        } else if(node instanceof TableMultiResultNode<?>) {
            return (N)wrapTableMultiResultNode((TableMultiResultNode<?>)node);
        } else if(node instanceof RootNode) {
            return (N)wrapRootNode((RootNode)node);
        } else {
            if(node instanceof WrappedNode) {
                N wrapper = (N)node;
                if(wrapper.monitor==this) return wrapper;
            }
            IdentityKey<Node> key = new IdentityKey<Node>(node);
            synchronized(nodeCache) {
                N wrapper = nodeCache.get(key);
                if(wrapper==null) {
                    wrapper = newWrappedNode(node);
                    nodeCache.put(key, wrapper);
                }
                return wrapper;
            }
        }
    }
    protected abstract N newWrappedNode(Node node) throws RemoteException;

    private final Map<IdentityKey<RootNode>,RN> rootNodeCache = new WeakHashMap<IdentityKey<RootNode>,RN>();
    RN wrapRootNode(RootNode node) throws RemoteException {
        if(node instanceof WrappedRootNode) {
            WrappedRootNode wrapper = (WrappedRootNode)node;
            if(wrapper.monitor==this) return (RN)wrapper;
        }
        IdentityKey<RootNode> key = new IdentityKey<RootNode>(node);
        synchronized(rootNodeCache) {
            RN wrapper = rootNodeCache.get(key);
            if(wrapper==null) {
                wrapper = newWrappedRootNode(node);
                rootNodeCache.put(key, wrapper);
            }
            return wrapper;
        }
    }
    protected abstract RN newWrappedRootNode(RootNode node) throws RemoteException;

    private final Map<IdentityKey<SingleResultNode>,SRN> singleResultNodeCache = new WeakHashMap<IdentityKey<SingleResultNode>,SRN>();
    SRN wrapSingleResultNode(SingleResultNode node) throws RemoteException {
        if(node instanceof WrappedSingleResultNode) {
            SRN wrapper = (SRN)node;
            if(wrapper.monitor==this) return wrapper;
        }
        IdentityKey<SingleResultNode> key = new IdentityKey<SingleResultNode>(node);
        synchronized(singleResultNodeCache) {
            SRN wrapper = singleResultNodeCache.get(key);
            if(wrapper==null) {
                wrapper = newWrappedSingleResultNode(node);
                singleResultNodeCache.put(key, wrapper);
            }
            return wrapper;
        }
    }
    protected abstract SRN newWrappedSingleResultNode(SingleResultNode node) throws RemoteException;

    private final Map<IdentityKey<TableMultiResultNode>,TMRN> tableMultiResultNodeCache = new WeakHashMap<IdentityKey<TableMultiResultNode>,TMRN>();
    TMRN wrapTableMultiResultNode(TableMultiResultNode<?> node) throws RemoteException {
        if(node instanceof WrappedTableMultiResultNode<?>) {
            TMRN wrapper = (TMRN)node;
            if(wrapper.monitor==this) return wrapper;
        }
        IdentityKey<TableMultiResultNode> key = new IdentityKey<TableMultiResultNode>(node);
        synchronized(tableMultiResultNodeCache) {
            TMRN wrapper = tableMultiResultNodeCache.get(key);
            if(wrapper==null) {
                wrapper = newWrappedTableMultiResultNode(node);
                tableMultiResultNodeCache.put(key, wrapper);
            }
            return wrapper;
        }
    }
    protected abstract TMRN newWrappedTableMultiResultNode(TableMultiResultNode<?> node) throws RemoteException;

    private final Map<IdentityKey<TableResultNode>,TRN> tableResultNodeCache = new WeakHashMap<IdentityKey<TableResultNode>,TRN>();
    TRN wrapTableResultNode(TableResultNode node) throws RemoteException {
        if(node instanceof WrappedTableResultNode) {
            TRN wrapper = (TRN)node;
            if(wrapper.monitor==this) return wrapper;
        }
        IdentityKey<TableResultNode> key = new IdentityKey<TableResultNode>(node);
        synchronized(tableResultNodeCache) {
            TRN wrapper = tableResultNodeCache.get(key);
            if(wrapper==null) {
                wrapper = newWrappedTableResultNode(node);
                tableResultNodeCache.put(key, wrapper);
            }
            return wrapper;
        }
    }
    protected abstract TRN newWrappedTableResultNode(TableResultNode node) throws RemoteException;
}
