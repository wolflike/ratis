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

import java.util.List;

/**
 * JMX information about the state of the current raft cluster.
 * 关于当前raft状态的JMX信息。
 * 定义一个MBean，用于JConsole来监控raft实例的一些信息（这些信息确实重要）
 * id、leaderId、currentTerm、groupId、role、follower、groupsOfSingleNode
 */
public interface RaftServerMXBean {

  /**
   * Identifier of the current server.
   */
  String getId();

  /**
   * Identifier of the leader node.
   */
  String getLeaderId();

  /**
   * Latest RAFT term.
   */
  long getCurrentTerm();

  /**
   * Cluster identifier.
   */
  String getGroupId();

  /**
   * RAFT Role of the server.
   */
  String getRole();

  /**
   * Addresses of the followers, only for leaders
   */
  List<String> getFollowers();

  /**
   * Gets the Groups of the Server.
   */
  List<String> getGroups();

}
