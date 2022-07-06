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
package org.apache.ratis.grpc;

import org.apache.ratis.client.ClientFactory;
import org.apache.ratis.conf.Parameters;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.grpc.client.GrpcClientRpc;
import org.apache.ratis.grpc.server.GrpcLogAppender;
import org.apache.ratis.grpc.server.GrpcService;
import org.apache.ratis.protocol.ClientId;
import org.apache.ratis.rpc.SupportedRpcType;
import org.apache.ratis.server.RaftServer;
import org.apache.ratis.server.leader.LogAppender;
import org.apache.ratis.server.ServerFactory;
import org.apache.ratis.server.leader.FollowerInfo;
import org.apache.ratis.server.leader.LeaderState;
import org.apache.ratis.thirdparty.io.netty.buffer.PooledByteBufAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class GrpcFactory implements ServerFactory, ClientFactory {

  public static final Logger LOG = LoggerFactory.getLogger(GrpcFactory.class);

  private void checkPooledByteBufAllocatorUseCacheForAllThreads(Consumer<String> log) {
    final String name = "useCacheForAllThreads";
    final String key = "org.apache.ratis.thirdparty.io.netty.allocator." + name;
    final boolean value = PooledByteBufAllocator.defaultUseCacheForAllThreads();
    if (value) {
      log.accept("PERFORMANCE WARNING: " + name + " is " + value
          + " that may cause Netty to create a lot garbage objects and, as a result, trigger GC.\n"
          + "\tIt is recommended to disable " + name + " by setting -D" + key
          + "=" + !value + " in command line.");
    }
  }

  private final GrpcTlsConfig tlsConfig;
  private final GrpcTlsConfig adminTlsConfig;
  private final GrpcTlsConfig clientTlsConfig;
  private final GrpcTlsConfig serverTlsConfig;

  public static Parameters newRaftParameters(GrpcTlsConfig conf) {
    final Parameters p = new Parameters();
    GrpcConfigKeys.TLS.setConf(p, conf);
    return p;
  }

  public GrpcFactory(Parameters parameters) {
    this(
        GrpcConfigKeys.TLS.conf(parameters),
        GrpcConfigKeys.Admin.tlsConf(parameters),
        GrpcConfigKeys.Client.tlsConf(parameters),
        GrpcConfigKeys.Server.tlsConf(parameters)
    );
  }

  public GrpcFactory(GrpcTlsConfig tlsConfig) {
    this(tlsConfig, null, null, null);
  }

  private GrpcFactory(GrpcTlsConfig tlsConfig, GrpcTlsConfig adminTlsConfig,
      GrpcTlsConfig clientTlsConfig, GrpcTlsConfig serverTlsConfig) {
    this.tlsConfig = tlsConfig;
    this.adminTlsConfig = adminTlsConfig;
    this.clientTlsConfig = clientTlsConfig;
    this.serverTlsConfig = serverTlsConfig;
  }

  public GrpcTlsConfig getTlsConfig() {
    return tlsConfig;
  }

  public GrpcTlsConfig getAdminTlsConfig() {
    return adminTlsConfig != null ? adminTlsConfig : tlsConfig;
  }

  public GrpcTlsConfig getClientTlsConfig() {
    return clientTlsConfig != null ? clientTlsConfig : tlsConfig;
  }

  public GrpcTlsConfig getServerTlsConfig() {
    return serverTlsConfig != null ? serverTlsConfig : tlsConfig;
  }

  @Override
  public SupportedRpcType getRpcType() {
    return SupportedRpcType.GRPC;
  }

  @Override
  public LogAppender newLogAppender(RaftServer.Division server, LeaderState state, FollowerInfo f) {
    return new GrpcLogAppender(server, state, f);
  }

  @Override
  public GrpcService newRaftServerRpc(RaftServer server) {
    checkPooledByteBufAllocatorUseCacheForAllThreads(LOG::info);
    return GrpcService.newBuilder()
        .setServer(server)
        .setAdminTlsConfig(getAdminTlsConfig())
        .setServerTlsConfig(getServerTlsConfig())
        .setClientTlsConfig(getClientTlsConfig())
        .build();
  }

  @Override
  public GrpcClientRpc newRaftClientRpc(ClientId clientId, RaftProperties properties) {
    checkPooledByteBufAllocatorUseCacheForAllThreads(LOG::debug);
    return new GrpcClientRpc(clientId, properties,
        getAdminTlsConfig(), getClientTlsConfig());
  }
}
