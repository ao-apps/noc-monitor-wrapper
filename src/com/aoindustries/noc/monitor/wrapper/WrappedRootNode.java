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
    public void addTreeListener(TreeListener treeListener) throws RemoteException {
        wrapped.addTreeListener(monitor.wrapTreeListener(treeListener));
    }

    @Override
    public void removeTreeListener(TreeListener treeListener) throws RemoteException {
        wrapped.removeTreeListener(monitor.wrapTreeListener(treeListener));
    }

    @Override
    public NodeSnapshot getSnapshot() throws RemoteException {
        NodeSnapshot nodeSnapshot = wrapped.getSnapshot();
        wrapSnapshot(monitor, nodeSnapshot);
        return nodeSnapshot;
    }

    /**
     * Recursively wraps the nodes of the snapshot.
     */
    private static void wrapSnapshot(WrappedMonitor monitor, NodeSnapshot snapshot) throws RemoteException {
        snapshot.setNode(monitor.wrapNode(snapshot.getNode()));
        for(NodeSnapshot child : snapshot.getChildren()) wrapSnapshot(monitor, child);
    }

    @Override
    public SortedSet<MonitoringPoint> getMonitoringPoints() throws RemoteException {
        return wrapped.getMonitoringPoints();
    }
}
