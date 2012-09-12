/*
 * Copyright 2012 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.wrapper;

import com.aoindustries.noc.monitor.common.TableMultiResult;
import com.aoindustries.noc.monitor.common.TableMultiResultListener;
import com.aoindustries.noc.monitor.common.TableMultiResultNode;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedTableMultiResultNode<R extends TableMultiResult> extends WrappedNode implements TableMultiResultNode<R> {

    final private TableMultiResultNode<R> wrapped;

    protected WrappedTableMultiResultNode(WrappedMonitor monitor, TableMultiResultNode<R> wrapped) {
        super(monitor, wrapped);
        this.wrapped = wrapped;
    }

    @Override
    final public void addTableMultiResultListener(final TableMultiResultListener<? super R> tableMultiResultListener) throws RemoteException {
        monitor.call(
            new Callable<Void>() {
                @Override
                public Void call() throws RemoteException {
                    wrapped.addTableMultiResultListener(tableMultiResultListener);
                    return null;
                }
            }
        );
    }

    @Override
    final public void removeTableMultiResultListener(final TableMultiResultListener<? super R> tableMultiResultListener) throws RemoteException {
        monitor.call(
            new Callable<Void>() {
                @Override
                public Void call() throws RemoteException {
                    wrapped.removeTableMultiResultListener(tableMultiResultListener);
                    return null;
                }
            }
        );
    }

    @Override
    final public List<?> getColumnHeaders() throws RemoteException {
        return monitor.call(
            new Callable<List<?>>() {
                @Override
                public List<?> call() throws RemoteException {
                    return wrapped.getColumnHeaders();
                }
            }
        );
    }

    @Override
    final public List<? extends R> getResults() throws RemoteException {
        return monitor.call(
            new Callable<List<? extends R>>() {
                @Override
                public List<? extends R> call() throws RemoteException {
                    return wrapped.getResults();
                }
            }
        );
    }
}
