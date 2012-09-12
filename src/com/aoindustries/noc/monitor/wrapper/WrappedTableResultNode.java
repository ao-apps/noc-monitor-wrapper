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

/**
 * @author  AO Industries, Inc.
 */
public class WrappedTableResultNode extends WrappedNode implements TableResultNode {

    final private TableResultNode wrapped;

    WrappedTableResultNode(WrappedMonitor monitor, TableResultNode wrapped) throws RemoteException {
        super(monitor, wrapped);
        this.wrapped = wrapped;
    }

    @Override
    public void addTableResultListener(TableResultListener tableResultListener) throws RemoteException {
        wrapped.addTableResultListener(tableResultListener);
    }

    @Override
    public void removeTableResultListener(TableResultListener tableResultListener) throws RemoteException {
        wrapped.removeTableResultListener(tableResultListener);
    }

    @Override
    public TableResult getLastResult() throws RemoteException {
        return wrapped.getLastResult();
    }
}
