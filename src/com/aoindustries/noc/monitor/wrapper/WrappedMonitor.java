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
import com.aoindustries.util.WrappedException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;

/**
 * Wraps a monitor, completely hiding both the monitor from the caller and the callbacks from the monitor.
 * Also provides the basic mechanism for disconnect and reconnect.
 * Provides a single call method that all calls are sent through.
 * Provides factory methods to create specialized wrappers or otherwise alter the default wrappers.
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

    /**
     * Performs the call on the wrapped object, allowing retry.
     */
    final protected <T> T call(Callable<T> callable) throws RemoteException {
        return call(callable, true);
    }

    /**
     * Performs the call on the wrapped object.  This is the main hook to intercept requests
     * for features like auto-reconnects, timeouts, and retries.
     */
    protected <T> T call(Callable<T> callable, boolean allowRetry) throws RemoteException {
        try {
            return callable.call();
        } catch(RemoteException err) {
            throw err;
        } catch(Exception err) {
            throw new RuntimeException(err.getMessage(), err);
        }
    }

    /**
     * Gets the root node for the given locale, username, and password.  May
     * reuse existing root nodes.
     */
    @Override
    final public WrappedRootNode login(final Locale locale, final String username, final String password) throws RemoteException, IOException, SQLException {
        try {
            return call(
                new Callable<WrappedRootNode>() {
                    @Override
                    public WrappedRootNode call() throws RemoteException {
                        try {
                            return wrapRootNode(getWrapped().login(locale, username, password));
                        } catch(IOException e) {
                            throw new WrappedException(e);
                        } catch(SQLException e) {
                            throw new WrappedException(e);
                        }
                    }
                }
            );
        } catch(WrappedException e) {
            Throwable cause = e.getCause();
            if(cause instanceof IOException) throw (IOException)cause;
            if(cause instanceof SQLException) throw (SQLException)cause;
            throw e;
        }
    }

    private final Map<IdentityKey<Node>,WrappedNode> nodeCache = new WeakHashMap<IdentityKey<Node>,WrappedNode>();
    final WrappedNode wrapNode(Node node) throws RemoteException {
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
    protected WrappedNode newWrappedNode(Node node) throws RemoteException {
        return new WrappedNode(this, node);
    }

    private final Map<IdentityKey<RootNode>,WrappedRootNode> rootNodeCache = new WeakHashMap<IdentityKey<RootNode>,WrappedRootNode>();
    final WrappedRootNode wrapRootNode(RootNode node) throws RemoteException {
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
    protected WrappedRootNode newWrappedRootNode(RootNode node) throws RemoteException {
        return new WrappedRootNode(this, node);
    }

    private final Map<IdentityKey<SingleResultNode>,WrappedSingleResultNode> singleResultNodeCache = new WeakHashMap<IdentityKey<SingleResultNode>,WrappedSingleResultNode>();
    final WrappedSingleResultNode wrapSingleResultNode(SingleResultNode node) throws RemoteException {
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
    protected WrappedSingleResultNode newWrappedSingleResultNode(SingleResultNode node) throws RemoteException {
        return new WrappedSingleResultNode(this, node);
    }

    private final Map<IdentityKey<TableMultiResultNode>,WrappedTableMultiResultNode> tableMultiResultNodeCache = new WeakHashMap<IdentityKey<TableMultiResultNode>,WrappedTableMultiResultNode>();
    final <R extends TableMultiResult> WrappedTableMultiResultNode<R> wrapTableMultiResultNode(TableMultiResultNode<R> node) throws RemoteException {
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
    protected <R extends TableMultiResult> WrappedTableMultiResultNode<R> newWrappedTableMultiResultNode(TableMultiResultNode<R> node) throws RemoteException {
        return new WrappedTableMultiResultNode<R>(this, node);
    }

    private final Map<IdentityKey<TableResultNode>,WrappedTableResultNode> tableResultNodeCache = new WeakHashMap<IdentityKey<TableResultNode>,WrappedTableResultNode>();
    final WrappedTableResultNode wrapTableResultNode(TableResultNode node) throws RemoteException {
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
    protected WrappedTableResultNode newWrappedTableResultNode(TableResultNode node) throws RemoteException {
        return new WrappedTableResultNode(this, node);
    }
}
