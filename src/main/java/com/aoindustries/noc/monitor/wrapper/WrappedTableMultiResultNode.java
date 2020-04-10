/*
 * Copyright 2012, 2020 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.noc.monitor.wrapper;

import com.aoindustries.noc.monitor.common.TableMultiResult;
import com.aoindustries.noc.monitor.common.TableMultiResultListener;
import com.aoindustries.noc.monitor.common.TableMultiResultNode;
import java.rmi.RemoteException;
import java.util.List;

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
	public void addTableMultiResultListener(TableMultiResultListener<? super R> tableMultiResultListener) throws RemoteException {
		wrapped.addTableMultiResultListener(monitor.wrapTableMultiResultListener(tableMultiResultListener));
	}

	@Override
	public void removeTableMultiResultListener(TableMultiResultListener<? super R> tableMultiResultListener) throws RemoteException {
		wrapped.removeTableMultiResultListener(monitor.wrapTableMultiResultListener(tableMultiResultListener));
	}

	@Override
	public List<?> getColumnHeaders() throws RemoteException {
		return wrapped.getColumnHeaders();
	}

	@Override
	public List<? extends R> getResults() throws RemoteException {
		return wrapped.getResults();
	}
}
