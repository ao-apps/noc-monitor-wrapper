/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.wrapper;

import com.aoindustries.noc.monitor.common.TableResult;
import com.aoindustries.noc.monitor.common.TableResultListener;
import com.aoindustries.noc.monitor.common.TableResultNode;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedTableResultNode extends WrappedNode implements TableResultNode {

    final private TableResultNode wrapped;

    protected WrappedTableResultNode(WrappedMonitor monitor, TableResultNode wrapped) {
        super(monitor, wrapped);
        this.wrapped = wrapped;
    }

    @Override
    final public void addTableResultListener(final TableResultListener tableResultListener) throws RemoteException {
        monitor.call(
            new Callable<Void>() {
                @Override
                public Void call() throws RemoteException {
                    wrapped.addTableResultListener(monitor.wrapTableResultListener(tableResultListener));
                    return null;
                }
            }
        );
    }

    @Override
    final public void removeTableResultListener(final TableResultListener tableResultListener) throws RemoteException {
        monitor.call(
            new Callable<Void>() {
                @Override
                public Void call() throws RemoteException {
                    wrapped.removeTableResultListener(monitor.wrapTableResultListener(tableResultListener));
                    return null;
                }
            }
        );
    }

    @Override
    final public TableResult getLastResult() throws RemoteException {
        return monitor.call(
            new Callable<TableResult>() {
                @Override
                public TableResult call() throws RemoteException {
                    return wrapped.getLastResult();
                }
            }
        );
    }
}
