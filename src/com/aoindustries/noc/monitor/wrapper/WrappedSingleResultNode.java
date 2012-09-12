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
import java.util.concurrent.Callable;

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
    final public void addSingleResultListener(final SingleResultListener singleResultListener) throws RemoteException {
        monitor.call(
            new Callable<Void>() {
                @Override
                public Void call() throws RemoteException {
                    wrapped.addSingleResultListener(singleResultListener);
                    return null;
                }
            }
        );
    }

    @Override
    final public void removeSingleResultListener(final SingleResultListener singleResultListener) throws RemoteException {
        monitor.call(
            new Callable<Void>() {
                @Override
                public Void call() throws RemoteException {
                    wrapped.removeSingleResultListener(singleResultListener);
                    return null;
                }
            }
        );
    }

    @Override
    final public SingleResult getLastResult() throws RemoteException {
        return monitor.call(
            new Callable<SingleResult>() {
                @Override
                public SingleResult call() throws RemoteException {
                    return wrapped.getLastResult();
                }
            }
        );
    }
}
