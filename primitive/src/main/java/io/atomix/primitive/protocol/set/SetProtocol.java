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
package io.atomix.primitive.protocol.set;

import java.util.Set;

/**
 * Set protocol.
 */
public interface SetProtocol<E> extends Set<E> {

  /**
   * Returns the set name.
   *
   * @return the set name
   */
  String name();

  /**
   * Adds the specified listener to the set which will be notified whenever the entries in the set are changed.
   *
   * @param listener listener to register for events
   */
  void addListener(SetProtocolEventListener<E> listener);

  /**
   * Removes the specified listener from the set such that it will no longer receive change notifications.
   *
   * @param listener listener to deregister for events
   */
  void removeListener(SetProtocolEventListener<E> listener);

  /**
   * Closes the set.
   */
  void close();

}