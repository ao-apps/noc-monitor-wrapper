/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.wrapper;

import com.aoindustries.noc.monitor.common.SingleResultListener;
import com.aoindustries.noc.monitor.common.SingleResultNode;
import com.aoindustries.noc.monitor.common.SingleResult;
import java.rmi.RemoteException;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedSingleResultNode extends WrappedNode implements SingleResultNode {

    final private SingleResultNode wrapped;

    WrappedSingleResultNode(WrappedMonitor monitor, SingleResultNode wrapped) throws RemoteException {
        super(monitor, wrapped);
        this.wrapped = wrapped;
    }

    @Override
    public void addSingleResultListener(SingleResultListener singleResultListener) throws RemoteException {
        wrapped.addSingleResultListener(singleResultListener);
    }

    @Override
    public void removeSingleResultListener(SingleResultListener singleResultListener) throws RemoteException {
        wrapped.removeSingleResultListener(singleResultListener);
    }

    @Override
    public SingleResult getLastResult() throws RemoteException {
        return wrapped.getLastResult();
    }
}
