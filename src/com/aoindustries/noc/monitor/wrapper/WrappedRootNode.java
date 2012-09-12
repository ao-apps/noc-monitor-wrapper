/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.wrapper;

import com.aoindustries.noc.monitor.common.MonitoringPoint;
import com.aoindustries.noc.monitor.common.NodeSnapshot;
import com.aoindustries.noc.monitor.common.RootNode;
import com.aoindustries.noc.monitor.common.TreeListener;
import java.rmi.RemoteException;
import java.util.SortedSet;
import java.util.concurrent.Callable;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedRootNode extends WrappedNode implements RootNode {

    final private RootNode wrapped;

    protected WrappedRootNode(WrappedMonitor monitor, RootNode wrapped) {
        super(monitor, wrapped);
        this.wrapped = wrapped;
    }

    @Override
    final public void addTreeListener(final TreeListener treeListener) throws RemoteException {
        monitor.call(
            new Callable<Void>() {
                @Override
                public Void call() throws RemoteException {
                    wrapped.addTreeListener(treeListener);
                    return null;
                }
            }
        );
    }

    @Override
    final public void removeTreeListener(final TreeListener treeListener) throws RemoteException {
        monitor.call(
            new Callable<Void>() {
                @Override
                public Void call() throws RemoteException {
                    wrapped.removeTreeListener(treeListener);
                    return null;
                }
            }
        );
    }

    @Override
    final public NodeSnapshot getSnapshot() throws RemoteException {
        return monitor.call(
            new Callable<NodeSnapshot>() {
                @Override
                public NodeSnapshot call() throws RemoteException {
                    NodeSnapshot nodeSnapshot = wrapped.getSnapshot();
                    wrapSnapshot(monitor, nodeSnapshot);
                    return nodeSnapshot;
                }
            }
        );
    }

    /**
     * Recursively wraps the nodes of the snapshot.
     */
    private static void wrapSnapshot(WrappedMonitor monitor, NodeSnapshot snapshot) throws RemoteException {
        snapshot.setNode(monitor.wrapNode(snapshot.getNode()));
        for(NodeSnapshot child : snapshot.getChildren()) wrapSnapshot(monitor, child);
    }

    @Override
    final public SortedSet<MonitoringPoint> getMonitoringPoints() throws RemoteException {
        return monitor.call(
            new Callable<SortedSet<MonitoringPoint>>() {
                @Override
                public SortedSet<MonitoringPoint> call() throws RemoteException {
                    return wrapped.getMonitoringPoints();
                }
            }
        );
    }
}
