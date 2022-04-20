/*
 * noc-monitor-wrapper - Base support for wrappers of Monitoring API.
 * Copyright (C) 2012, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoindustries.noc.monitor.common.TableMultiResult;
import com.aoindustries.noc.monitor.common.TableMultiResultListener;
import com.aoindustries.noc.monitor.common.TableMultiResultNode;
import java.rmi.RemoteException;
import java.util.List;

/**
 * @author  AO Industries, Inc.
 */
public class WrappedTableMultiResultNode<R extends TableMultiResult> extends WrappedNode implements TableMultiResultNode<R> {

  private final TableMultiResultNode<R> wrapped;

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
