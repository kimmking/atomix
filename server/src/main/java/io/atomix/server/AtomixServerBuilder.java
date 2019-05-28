/*
 * Copyright 2018-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.server;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import io.atomix.cluster.AbstractClusterBuilder;
import io.atomix.cluster.MemberId;
import io.atomix.cluster.discovery.NodeDiscoveryProvider;
import io.atomix.primitive.partition.ManagedPartitionGroup;
import io.atomix.utils.component.Component;
import io.atomix.utils.net.Address;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Builder for {@link AtomixServer} instance.
 * <p>
 * This builder is used to configure an {@link AtomixServer} instance programmatically. To create a new builder, use one of the
 * {@link AtomixServer#builder()} static methods.
 * <pre>
 *   {@code
 *   AtomixBuilder builder = Atomix.builder();
 *   }
 * </pre>
 * The instance is configured by calling the {@code with*} methods on this builder. Once the instance has been
 * configured, call {@link #build()} to build the instance:
 * <pre>
 *   {@code
 *   Atomix atomix = Atomix.builder()
 *     .withMemberId("member-1")
 *     .withHost("192.168.10.2")
 *     .withPort(5000)
 *     .build();
 *   }
 * </pre>
 * Backing the builder is an {@link AtomixConfig} which is loaded when the builder is initially constructed. To load
 * a configuration from a file, use {@link AtomixServer#builder(String)}.
 */
public class AtomixServerBuilder extends AbstractClusterBuilder<AtomixServer> {
  private final AtomixConfig config;
  private final ClassLoader classLoader;
  private final Component.Scope scope;

  public AtomixServerBuilder(Component.Scope scope) {
    this(new AtomixConfig(), AtomixServerBuilder.class.getClassLoader(), scope);
  }

  protected AtomixServerBuilder(AtomixConfig config, ClassLoader classLoader) {
    this(config, classLoader, Component.Scope.RUNTIME);
  }

  protected AtomixServerBuilder(AtomixConfig config, ClassLoader classLoader, Component.Scope scope) {
    super(config.getClusterConfig());
    this.config = checkNotNull(config);
    this.classLoader = checkNotNull(classLoader);
    this.scope = checkNotNull(scope);
  }

  /**
   * Enables the shutdown hook.
   * <p>
   * When the shutdown hook is enabled, the instance will be shutdown when the JVM exits.
   *
   * @return the Atomix builder
   */
  public AtomixServerBuilder withShutdownHookEnabled() {
    return withShutdownHook(true);
  }

  /**
   * Sets whether the shutdown hook is enabled.
   * <p>
   * When the shutdown hook is enabled, the instance will be shutdown when the JVM exits.
   *
   * @param enabled if <code>true</code> a shutdown hook will be registered
   * @return the Atomix builder
   */
  public AtomixServerBuilder withShutdownHook(boolean enabled) {
    config.setEnableShutdownHook(enabled);
    return this;
  }

  /**
   * Sets the system management partition group.
   * <p>
   * The system management group must be configured for stateful instances. This group will be used to store primitive
   * and transaction metadata and coordinate primary elections for replication protocols.
   * <pre>
   *   {@code
   *   Atomix atomix = Atomix.builder()
   *     .withManagementGroup(RaftPartitionGroup.builder("system")
   *       .withNumPartitions(1)
   *       .build())
   *     .build();
   *   }
   * </pre>
   * <p>
   * The configured partition group is replicated on whichever nodes define them in this configuration. That is,
   * this node will participate in whichever partition group is provided to this method.
   * <p>
   * The management group can also be configured in {@code atomix.conf} under the {@code management-group} key.
   *
   * @param systemManagementGroup the system management partition group
   * @return the Atomix builder
   */
  public AtomixServerBuilder withManagementGroup(ManagedPartitionGroup systemManagementGroup) {
    config.setSystemGroup(systemManagementGroup.config());
    return this;
  }

  /**
   * Sets the primitive partition groups.
   * <p>
   * The primitive partition groups represent partitions that are directly accessible to distributed primitives. To use
   * partitioned primitives, at least one node must be configured with at least one data partition group.
   * <pre>
   *   {@code
   *   Atomix atomix = Atomix.builder()
   *     .withPartitionGroups(PrimaryBackupPartitionGroup.builder("data")
   *       .withNumPartitions(32)
   *       .build())
   *     .build();
   *   }
   * </pre>
   * The partition group name is used to uniquely identify the group when constructing primitive instances. Partitioned
   * primitives will reference a specific protocol and partition group within which to replicate the primitive.
   * <p>
   * The configured partition groups are replicated on whichever nodes define them in this configuration. That is,
   * this node will participate in whichever partition groups are provided to this method.
   * <p>
   * The partition groups can also be configured in {@code atomix.conf} under the {@code partition-groups} key.
   *
   * @param partitionGroups the partition groups
   * @return the Atomix builder
   * @throws NullPointerException if the partition groups are null
   */
  public AtomixServerBuilder withPartitionGroups(ManagedPartitionGroup... partitionGroups) {
    return withPartitionGroups(Arrays.asList(checkNotNull(partitionGroups, "partitionGroups cannot be null")));
  }

  /**
   * Sets the primitive partition groups.
   * <p>
   * The primitive partition groups represent partitions that are directly accessible to distributed primitives. To use
   * partitioned primitives, at least one node must be configured with at least one data partition group.
   * <pre>
   *   {@code
   *   Atomix atomix = Atomix.builder()
   *     .withPartitionGroups(PrimaryBackupPartitionGroup.builder("data")
   *       .withNumPartitions(32)
   *       .build())
   *     .build();
   *   }
   * </pre>
   * The partition group name is used to uniquely identify the group when constructing primitive instances. Partitioned
   * primitives will reference a specific protocol and partition group within which to replicate the primitive.
   * <p>
   * The configured partition groups are replicated on whichever nodes define them in this configuration. That is,
   * this node will participate in whichever partition groups are provided to this method.
   * <p>
   * The partition groups can also be configured in {@code atomix.conf} under the {@code partition-groups} key.
   *
   * @param partitionGroups the partition groups
   * @return the Atomix builder
   * @throws NullPointerException if the partition groups are null
   */
  public AtomixServerBuilder withPartitionGroups(Collection<ManagedPartitionGroup> partitionGroups) {
    partitionGroups.forEach(group -> config.addPartitionGroup(group.config()));
    return this;
  }

  /**
   * Adds a primitive partition group.
   * <p>
   * The provided group will be added to the list of already configured partition groups. The primitive partition groups
   * represent partitions that are directly accessible to distributed primitives. To use partitioned primitives, at
   * least one node must be configured with at least one data partition group.
   * <pre>
   *   {@code
   *   Atomix atomix = Atomix.builder()
   *     .withPartitionGroups(PrimaryBackupPartitionGroup.builder("data")
   *       .withNumPartitions(32)
   *       .build())
   *     .build();
   *   }
   * </pre>
   * The partition group name is used to uniquely identify the group when constructing primitive instances. Partitioned
   * primitives will reference a specific protocol and partition group within which to replicate the primitive.
   * <p>
   * The configured partition groups are replicated on whichever nodes define them in this configuration. That is,
   * this node will participate in whichever partition groups are provided to this method.
   * <p>
   * The partition groups can also be configured in {@code atomix.conf} under the {@code partition-groups} key.
   *
   * @param partitionGroup the partition group to add
   * @return the Atomix builder
   * @throws NullPointerException if the partition group is null
   */
  public AtomixServerBuilder addPartitionGroup(ManagedPartitionGroup partitionGroup) {
    config.addPartitionGroup(partitionGroup.config());
    return this;
  }

  /**
   * Requires explicit serializable type registration for user types.
   *
   * @return the Atomix builder
   */
  public AtomixServerBuilder withTypeRegistrationRequired() {
    return withTypeRegistrationRequired(true);
  }

  /**
   * Sets whether serializable type registration is required for user types.
   *
   * @param required whether serializable type registration is required for user types
   * @return the Atomix builder
   */
  public AtomixServerBuilder withTypeRegistrationRequired(boolean required) {
    config.setTypeRegistrationRequired(required);
    return this;
  }

  /**
   * Enables compatible serialization for user types.
   *
   * @return the Atomix builder
   */
  public AtomixServerBuilder withCompatibleSerialization() {
    return withCompatibleSerialization(true);
  }

  /**
   * Sets whether compatible serialization is enabled for user types.
   *
   * @param enabled whether compatible serialization is enabled for user types
   * @return the Atomix builder
   */
  public AtomixServerBuilder withCompatibleSerialization(boolean enabled) {
    config.setCompatibleSerialization(enabled);
    return this;
  }

  @Override
  public AtomixServerBuilder withClusterId(String clusterId) {
    super.withClusterId(clusterId);
    return this;
  }

  @Override
  public AtomixServerBuilder withMemberId(String localMemberId) {
    super.withMemberId(localMemberId);
    return this;
  }

  @Override
  public AtomixServerBuilder withMemberId(MemberId localMemberId) {
    super.withMemberId(localMemberId);
    return this;
  }

  @Override
  public AtomixServerBuilder withHost(String host) {
    super.withHost(host);
    return this;
  }

  @Override
  public AtomixServerBuilder withPort(int port) {
    super.withPort(port);
    return this;
  }

  @Override
  @Deprecated
  public AtomixServerBuilder withAddress(String address) {
    super.withAddress(address);
    return this;
  }

  @Override
  @Deprecated
  public AtomixServerBuilder withAddress(String host, int port) {
    super.withAddress(host, port);
    return this;
  }

  @Override
  @Deprecated
  public AtomixServerBuilder withAddress(int port) {
    super.withAddress(port);
    return this;
  }

  @Override
  public AtomixServerBuilder withAddress(Address address) {
    super.withAddress(address);
    return this;
  }

  @Override
  @Deprecated
  public AtomixServerBuilder withZone(String zone) {
    super.withZone(zone);
    return this;
  }

  @Override
  public AtomixServerBuilder withZoneId(String zoneId) {
    super.withZoneId(zoneId);
    return this;
  }

  @Override
  @Deprecated
  public AtomixServerBuilder withRack(String rack) {
    super.withRack(rack);
    return this;
  }

  @Override
  public AtomixServerBuilder withRackId(String rackId) {
    super.withRackId(rackId);
    return this;
  }

  @Override
  public AtomixServerBuilder withHostId(String hostId) {
    super.withHostId(hostId);
    return this;
  }

  @Override
  public AtomixServerBuilder withProperties(Properties properties) {
    super.withProperties(properties);
    return this;
  }

  @Override
  public AtomixServerBuilder withProperty(String key, String value) {
    super.withProperty(key, value);
    return this;
  }

  @Override
  public AtomixServerBuilder withMessagingInterface(String iface) {
    super.withMessagingInterface(iface);
    return this;
  }

  @Override
  public AtomixServerBuilder withMessagingInterfaces(String... ifaces) {
    super.withMessagingInterfaces(ifaces);
    return this;
  }

  @Override
  public AtomixServerBuilder withMessagingInterfaces(Collection<String> ifaces) {
    super.withMessagingInterfaces(ifaces);
    return this;
  }

  @Override
  public AtomixServerBuilder withMessagingPort(int bindPort) {
    super.withMessagingPort(bindPort);
    return this;
  }

  @Override
  public AtomixServerBuilder withConnectionPoolSize(int connectionPoolSize) {
    super.withConnectionPoolSize(connectionPoolSize);
    return this;
  }

  @Override
  public AtomixServerBuilder withMembershipProvider(NodeDiscoveryProvider locationProvider) {
    super.withMembershipProvider(locationProvider);
    return this;
  }

  @Override
  public AtomixServerBuilder withBroadcastUpdates(boolean broadcastUpdates) {
    super.withBroadcastUpdates(broadcastUpdates);
    return this;
  }

  @Override
  public AtomixServerBuilder withBroadcastDisputes(boolean broadcastDisputes) {
    super.withBroadcastDisputes(broadcastDisputes);
    return this;
  }

  @Override
  public AtomixServerBuilder withNotifySuspect(boolean notifySuspect) {
    super.withNotifySuspect(notifySuspect);
    return this;
  }

  @Override
  public AtomixServerBuilder withGossipInterval(Duration gossipInterval) {
    super.withGossipInterval(gossipInterval);
    return this;
  }

  @Override
  public AtomixServerBuilder withGossipFanout(int gossipFanout) {
    super.withGossipFanout(gossipFanout);
    return this;
  }

  @Override
  public AtomixServerBuilder withProbeInterval(Duration probeInterval) {
    super.withProbeInterval(probeInterval);
    return this;
  }

  @Override
  public AtomixServerBuilder withSuspectProbes(int suspectProbes) {
    super.withSuspectProbes(suspectProbes);
    return this;
  }

  @Override
  public AtomixServerBuilder withFailureTimeout(Duration failureTimeout) {
    super.withFailureTimeout(failureTimeout);
    return this;
  }

  @Override
  public AtomixServerBuilder withCertPath(String certPath) {
    super.withCertPath(certPath);
    return this;
  }

  @Override
  public AtomixServerBuilder withKeyPath(String keyPath) {
    super.withKeyPath(keyPath);
    return this;
  }

  @Override
  public AtomixServerBuilder withTlsEnabled() {
    super.withTlsEnabled();
    return this;
  }

  @Override
  public AtomixServerBuilder withTlsEnabled(boolean tlsEnabled) {
    super.withTlsEnabled(tlsEnabled);
    return this;
  }

  /**
   * Builds a new Atomix instance.
   * <p>
   * The returned instance will be configured with the initial builder configuration plus any overrides that were made
   * via the builder. The returned instance will not be running. To start the instance call the {@link AtomixServer#start()}
   * method:
   * <pre>
   *   {@code
   *   Atomix atomix = Atomix.builder()
   *     .withMemberId("member-1")
   *     .withHost("192.168.10.2")
   *     .withPort(5000)
   *     .build();
   *   atomix.start().join();
   *   }
   * </pre>
   *
   * @return a new Atomix instance
   */
  @Override
  public AtomixServer build() {
    return new AtomixServer(config, classLoader, scope);
  }
}