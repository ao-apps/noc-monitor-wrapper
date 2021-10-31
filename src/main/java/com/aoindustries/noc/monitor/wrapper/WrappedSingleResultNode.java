/*
 * noc-monitor-wrapper - Base support for wrappers of Monitoring API.
 * Copyright (C) 2012, 2020, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of noc-monitor-wrapper.
 *
 * noc-monitor-wrapper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * noc-monitor-wrapper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with noc-monitor-wrapper.  If not, see <https://www.gnu.org/licenses/>.
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

	private final SingleResultNode wrapped;

	protected WrappedSingleResultNode(WrappedMonitor monitor, SingleResultNode wrapped) {
		super(monitor, wrapped);
		this.wrapped = wrapped;
	}

	@Override
	public void addSingleResultListener(SingleResultListener singleResultListener) throws RemoteException {
		wrapped.addSingleResultListener(monitor.wrapSingleResultListener(singleResultListener));
	}

	@Override
	public void removeSingleResultListener(SingleResultListener singleResultListener) throws RemoteException {
		wrapped.removeSingleResultListener(monitor.wrapSingleResultListener(singleResultListener));
	}

	@Override
	public SingleResult getLastResult() throws RemoteException {
		return wrapped.getLastResult();
	}
}
