/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.wrapper;

import com.aoindustries.noc.monitor.common.AlertLevelChange;
import com.aoindustries.noc.monitor.common.Node;
import com.aoindustries.noc.monitor.common.TreeListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedTreeListener implements TreeListener {

    final WrappedMonitor monitor;
    final private TreeListener wrapped;

    protected WrappedTreeListener(WrappedMonitor monitor, TreeListener wrapped) {
        this.monitor = monitor;
        this.wrapped = wrapped;
    }

    @Override
    public void nodeAdded() throws RemoteException {
        wrapped.nodeAdded();
    }

    @Override
    public void nodeRemoved() throws RemoteException {
        wrapped.nodeRemoved();
    }

    @Override
    public void nodeAlertLevelChanged(List<AlertLevelChange> changes) throws RemoteException {
        List<AlertLevelChange> wrappedChanges = new ArrayList<AlertLevelChange>(changes.size());
        for(AlertLevelChange change : changes) {
            Node node = change.getNode();
            wrappedChanges.add(change.setNode(monitor.wrapNode(node, node.getUuid())));
        }
        wrappedChanges = Collections.unmodifiableList(wrappedChanges);
        wrapped.nodeAlertLevelChanged(wrappedChanges);
    }

    @Override
    public boolean equals(Object O) {
        if(O==null) return false;
        if(!(O instanceof TreeListener)) return false;

        // Unwrap this
        TreeListener thisTreeListener = WrappedTreeListener.this;
        while(thisTreeListener instanceof WrappedTreeListener) thisTreeListener = ((WrappedTreeListener)thisTreeListener).wrapped;

        // Unwrap other
        TreeListener otherTreeListener = (TreeListener)O;
        while(otherTreeListener instanceof WrappedTreeListener) otherTreeListener = ((WrappedTreeListener)otherTreeListener).wrapped;

        // Check equals
        return thisTreeListener.equals(otherTreeListener);
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }
}
