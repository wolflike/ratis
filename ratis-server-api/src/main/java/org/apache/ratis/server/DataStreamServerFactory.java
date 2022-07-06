/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ratis.server;

import org.apache.ratis.conf.Parameters;
import org.apache.ratis.datastream.DataStreamFactory;
import org.apache.ratis.datastream.DataStreamType;

/** A {@link DataStreamFactory} to create server-side objects. */
public interface DataStreamServerFactory extends DataStreamFactory {
  static DataStreamServerFactory newInstance(DataStreamType type, Parameters parameters) {
    final DataStreamFactory dataStreamFactory = type.newServerFactory(parameters);
    if (dataStreamFactory instanceof DataStreamServerFactory) {
      return (DataStreamServerFactory)dataStreamFactory;
    }
    throw new ClassCastException("Cannot cast " + dataStreamFactory.getClass()
        + " to " + DataStreamServerFactory.class + "; rpc type is " + type);
  }

  /** Create a new {@link DataStreamServerRpc}. */
  DataStreamServerRpc newDataStreamServerRpc(RaftServer server);
}
