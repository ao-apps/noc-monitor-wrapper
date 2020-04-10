/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.wrapper;

import com.aoindustries.noc.monitor.common.Monitor;
import com.aoindustries.noc.monitor.common.Node;
import com.aoindustries.noc.monitor.common.RootNode;
import com.aoindustries.noc.monitor.common.SingleResultListener;
import com.aoindustries.noc.monitor.common.SingleResultNode;
import com.aoindustries.noc.monitor.common.TableMultiResult;
import com.aoindustries.noc.monitor.common.TableMultiResultListener;
import com.aoindustries.noc.monitor.common.TableMultiResultNode;
import com.aoindustries.noc.monitor.common.TableResultListener;
import com.aoindustries.noc.monitor.common.TableResultNode;
import com.aoindustries.noc.monitor.common.TreeListener;
import com.aoindustries.util.IdentityKey;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Wraps a monitor, completely hiding both the monitor from the caller and the callbacks from the monitor.
 * Also provides the basic mechanism for disconnect and reconnect.
 * Provides factory methods to create specialized wrappers or otherwise alter the default wrappers.
 *
 * TODO: UUID's for listeners, too
 *
 * @author  AO Industries, Inc.
 */
public class WrappedMonitor implements Monitor {

    final Object connectionLock = new Object();
    private Monitor wrapped;

    /**
     * Wraps the provided monitor.
     */
    public WrappedMonitor(Monitor wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Will connect when first needed.
     */
    public WrappedMonitor() {
        this.wrapped = null;
    }

    // <editor-fold defaultstate="collapsed" desc="Connect and Reconnect">
    /**
     * Disconnects this wrapper.  The wrapper will automatically reconnect on the next use.
     * TODO: How to signal outer cache layers?
     *
     * @see #getWrapped()
     * @see #connect()
     */
    final protected void disconnect() throws RemoteException {
        synchronized(connectionLock) {
            wrapped = null;
        }
    }

    /**
     * Gets the wrapped monitor, reconnecting if needed.
     *
     * @see #disconnect()
     * @see #connect()
     */
    final protected Monitor getWrapped() throws RemoteException {
        synchronized(connectionLock) {
            // (Re)connects to the wrapped factory
            if(wrapped==null) wrapped = connect();
            return wrapped;
        }
    }

    /**
     * Connects to the wrapped monitor.  This is only called when disconnected or not yet connected.
     *
     * @exception  UnsupportedOperationException  if not supported
     *
     * @see #disconnect()
     * @see #getWrapped()
     */
    protected Monitor connect() throws RemoteException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Reconnect not supported.");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Monitor">
    /**
     * Gets the root node for the given locale, username, and password.  May
     * reuse existing root nodes.
     */
    @Override
    public WrappedRootNode login(Locale locale, String username, String password) throws RemoteException, IOException, SQLException {
        RootNode wrappedRootNode = getWrapped().login(locale, username, password);
        return wrapRootNode(wrappedRootNode, wrappedRootNode.getUuid());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Node">
    private final Map<UUID,WrappedNode> nodeCache = new WeakHashMap<UUID,WrappedNode>();

    final WrappedNode wrapNode(Node node, UUID uuid) throws RemoteException {
        if(node instanceof SingleResultNode) {
            return (WrappedNode)wrapSingleResultNode((SingleResultNode)node, uuid);
        } else if(node instanceof TableResultNode) {
            return (WrappedNode)wrapTableResultNode((TableResultNode)node, uuid);
        } else if(node instanceof TableMultiResultNode<?>) {
            return (WrappedNode)wrapTableMultiResultNode((TableMultiResultNode<?>)node, uuid);
        } else if(node instanceof RootNode) {
            return (WrappedNode)wrapRootNode((RootNode)node, uuid);
        } else {
            if(node instanceof WrappedNode) {
                WrappedNode wrapper = (WrappedNode)node;
                if(wrapper.monitor==this) return wrapper;
            }
            //UUID uuid = node.getUuid();
            synchronized(nodeCache) {
                WrappedNode wrapper = nodeCache.get(uuid);
                if(wrapper==null) {
                    wrapper = newWrappedNode(node);
                    nodeCache.put(uuid, wrapper);
                }
                return wrapper;
            }
        }
    }

    protected WrappedNode newWrappedNode(Node node) throws RemoteException {
        return new WrappedNode(this, node);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="RootNode">
    private final Map<UUID,WrappedRootNode> rootNodeCache = new WeakHashMap<UUID,WrappedRootNode>();

    final WrappedRootNode wrapRootNode(RootNode node, UUID uuid) throws RemoteException {
        if(node instanceof WrappedRootNode) {
            WrappedRootNode wrapper = (WrappedRootNode)node;
            if(wrapper.monitor==this) return wrapper;
        }
        //UUID uuid = node.getUuid();
        synchronized(rootNodeCache) {
            WrappedRootNode wrapper = rootNodeCache.get(uuid);
            if(wrapper==null) {
                wrapper = newWrappedRootNode(node);
                rootNodeCache.put(uuid, wrapper);
            }
            return wrapper;
        }
    }

    protected WrappedRootNode newWrappedRootNode(RootNode node) throws RemoteException {
        return new WrappedRootNode(this, node);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="SingleResultNode">
    private final Map<UUID,WrappedSingleResultNode> singleResultNodeCache = new WeakHashMap<UUID,WrappedSingleResultNode>();

    final WrappedSingleResultNode wrapSingleResultNode(SingleResultNode node, UUID uuid) throws RemoteException {
        if(node instanceof WrappedSingleResultNode) {
            WrappedSingleResultNode wrapper = (WrappedSingleResultNode)node;
            if(wrapper.monitor==this) return wrapper;
        }
        //UUID uuid = node.getUuid();
        synchronized(singleResultNodeCache) {
            WrappedSingleResultNode wrapper = singleResultNodeCache.get(uuid);
            if(wrapper==null) {
                wrapper = newWrappedSingleResultNode(node);
                singleResultNodeCache.put(uuid, wrapper);
            }
            return wrapper;
        }
    }

    protected WrappedSingleResultNode newWrappedSingleResultNode(SingleResultNode node) throws RemoteException {
        return new WrappedSingleResultNode(this, node);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TableMultiResultNode">
    @SuppressWarnings("rawtypes")
    private final Map<UUID,WrappedTableMultiResultNode> tableMultiResultNodeCache = new WeakHashMap<UUID,WrappedTableMultiResultNode>();

    @SuppressWarnings({"unchecked","rawtypes"})
    final <R extends TableMultiResult> WrappedTableMultiResultNode<R> wrapTableMultiResultNode(TableMultiResultNode<R> node, UUID uuid) throws RemoteException {
        if(node instanceof WrappedTableMultiResultNode<?>) {
            WrappedTableMultiResultNode<R> wrapper = (WrappedTableMultiResultNode<R>)node;
            if(wrapper.monitor==this) return wrapper;
        }
        //UUID uuid = node.getUuid();
        synchronized(tableMultiResultNodeCache) {
            WrappedTableMultiResultNode<R> wrapper = tableMultiResultNodeCache.get(uuid);
            if(wrapper==null) {
                wrapper = newWrappedTableMultiResultNode(node);
                tableMultiResultNodeCache.put(uuid, wrapper);
            }
            return wrapper;
        }
    }

    protected <R extends TableMultiResult> WrappedTableMultiResultNode<R> newWrappedTableMultiResultNode(TableMultiResultNode<R> node) throws RemoteException {
        return new WrappedTableMultiResultNode<R>(this, node);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TableResultNode">
    private final Map<UUID,WrappedTableResultNode> tableResultNodeCache = new WeakHashMap<UUID,WrappedTableResultNode>();

    final WrappedTableResultNode wrapTableResultNode(TableResultNode node, UUID uuid) throws RemoteException {
        if(node instanceof WrappedTableResultNode) {
            WrappedTableResultNode wrapper = (WrappedTableResultNode)node;
            if(wrapper.monitor==this) return wrapper;
        }
        //UUID uuid = node.getUuid();
        synchronized(tableResultNodeCache) {
            WrappedTableResultNode wrapper = tableResultNodeCache.get(uuid);
            if(wrapper==null) {
                wrapper = newWrappedTableResultNode(node);
                tableResultNodeCache.put(uuid, wrapper);
            }
            return wrapper;
        }
    }

    protected WrappedTableResultNode newWrappedTableResultNode(TableResultNode node) throws RemoteException {
        return new WrappedTableResultNode(this, node);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TreeListener">
    private final Map<IdentityKey<TreeListener>,WrappedTreeListener> treeListenerCache = new WeakHashMap<IdentityKey<TreeListener>,WrappedTreeListener>();

    final WrappedTreeListener wrapTreeListener(TreeListener treeListener) throws RemoteException {
        if(treeListener instanceof WrappedTreeListener) {
            WrappedTreeListener wrapper = (WrappedTreeListener)treeListener;
            if(wrapper.monitor==this) return wrapper;
        }
        IdentityKey<TreeListener> key = new IdentityKey<TreeListener>(treeListener);
        synchronized(treeListenerCache) {
            WrappedTreeListener wrapper = treeListenerCache.get(key);
            if(wrapper==null) {
                wrapper = newWrappedTreeListener(treeListener);
                treeListenerCache.put(key, wrapper);
            }
            return wrapper;
        }
    }

    protected WrappedTreeListener newWrappedTreeListener(TreeListener treeListener) throws RemoteException {
        return new WrappedTreeListener(this, treeListener);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="SingleResultListener">
    private final Map<IdentityKey<SingleResultListener>,WrappedSingleResultListener> singleResultListenerCache = new WeakHashMap<IdentityKey<SingleResultListener>,WrappedSingleResultListener>();

    final WrappedSingleResultListener wrapSingleResultListener(SingleResultListener singleResultListener) throws RemoteException {
        if(singleResultListener instanceof WrappedSingleResultListener) {
            WrappedSingleResultListener wrapper = (WrappedSingleResultListener)singleResultListener;
            if(wrapper.monitor==this) return wrapper;
        }
        IdentityKey<SingleResultListener> key = new IdentityKey<SingleResultListener>(singleResultListener);
        synchronized(singleResultListenerCache) {
            WrappedSingleResultListener wrapper = singleResultListenerCache.get(key);
            if(wrapper==null) {
                wrapper = newWrappedSingleResultListener(singleResultListener);
                singleResultListenerCache.put(key, wrapper);
            }
            return wrapper;
        }
    }

    protected WrappedSingleResultListener newWrappedSingleResultListener(SingleResultListener singleResultListener) throws RemoteException {
        return new WrappedSingleResultListener(this, singleResultListener);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TableMultiResultListener">
    @SuppressWarnings("rawtypes")
    private final Map<IdentityKey<TableMultiResultListener>,WrappedTableMultiResultListener> tableMultiResultListenerCache = new WeakHashMap<IdentityKey<TableMultiResultListener>,WrappedTableMultiResultListener>();

    @SuppressWarnings({"unchecked","rawtypes"})
    final <R extends TableMultiResult> WrappedTableMultiResultListener<R> wrapTableMultiResultListener(TableMultiResultListener<R> tableMultiResultListener) throws RemoteException {
        if(tableMultiResultListener instanceof WrappedTableMultiResultListener<?>) {
            WrappedTableMultiResultListener<R> wrapper = (WrappedTableMultiResultListener<R>)tableMultiResultListener;
            if(wrapper.monitor==this) return wrapper;
        }
        IdentityKey<TableMultiResultListener> key = new IdentityKey<TableMultiResultListener>(tableMultiResultListener);
        synchronized(tableMultiResultListenerCache) {
            WrappedTableMultiResultListener<R> wrapper = tableMultiResultListenerCache.get(key);
            if(wrapper==null) {
                wrapper = newWrappedTableMultiResultListener(tableMultiResultListener);
                tableMultiResultListenerCache.put(key, wrapper);
            }
            return wrapper;
        }
    }

    protected <R extends TableMultiResult> WrappedTableMultiResultListener<R> newWrappedTableMultiResultListener(TableMultiResultListener<R> tableMultiResultListener) throws RemoteException {
        return new WrappedTableMultiResultListener<R>(this, tableMultiResultListener);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TableResultListener">
    private final Map<IdentityKey<TableResultListener>,WrappedTableResultListener> tableResultListenerCache = new WeakHashMap<IdentityKey<TableResultListener>,WrappedTableResultListener>();

    final WrappedTableResultListener wrapTableResultListener(TableResultListener tableResultListener) throws RemoteException {
        if(tableResultListener instanceof WrappedTableResultListener) {
            WrappedTableResultListener wrapper = (WrappedTableResultListener)tableResultListener;
            if(wrapper.monitor==this) return wrapper;
        }
        IdentityKey<TableResultListener> key = new IdentityKey<TableResultListener>(tableResultListener);
        synchronized(tableResultListenerCache) {
            WrappedTableResultListener wrapper = tableResultListenerCache.get(key);
            if(wrapper==null) {
                wrapper = newWrappedTableResultListener(tableResultListener);
                tableResultListenerCache.put(key, wrapper);
            }
            return wrapper;
        }
    }

    protected WrappedTableResultListener newWrappedTableResultListener(TableResultListener tableResultListener) throws RemoteException {
        return new WrappedTableResultListener(this, tableResultListener);
    }
    // </editor-fold>
}
