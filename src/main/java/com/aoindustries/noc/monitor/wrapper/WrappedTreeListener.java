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
  private final TreeListener wrapped;

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
    List<AlertLevelChange> wrappedChanges = new ArrayList<>(changes.size());
    for (AlertLevelChange change : changes) {
      Node node = change.getNode();
      wrappedChanges.add(change.setNode(monitor.wrapNode(node, node.getUuid())));
    }
    wrappedChanges = Collections.unmodifiableList(wrappedChanges);
    wrapped.nodeAlertLevelChanged(wrappedChanges);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof TreeListener)) {
      return false;
    }

    // Unwrap this
    TreeListener thisTreeListener = WrappedTreeListener.this;
    while (thisTreeListener instanceof WrappedTreeListener) thisTreeListener = ((WrappedTreeListener)thisTreeListener).wrapped;

    // Unwrap other
    TreeListener otherTreeListener = (TreeListener)obj;
    while (otherTreeListener instanceof WrappedTreeListener) otherTreeListener = ((WrappedTreeListener)otherTreeListener).wrapped;

    // Check equals
    return thisTreeListener.equals(otherTreeListener);
  }

  @Override
  public int hashCode() {
    return wrapped.hashCode();
  }
}
