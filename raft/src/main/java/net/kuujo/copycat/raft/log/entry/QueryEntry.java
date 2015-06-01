/*
 * Copyright 2015 the original author or authors.
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
package net.kuujo.copycat.raft.log.entry;

import net.kuujo.copycat.io.Buffer;
import net.kuujo.copycat.io.serializer.SerializeWith;
import net.kuujo.copycat.io.serializer.Serializer;
import net.kuujo.copycat.io.util.ReferenceManager;
import net.kuujo.copycat.raft.Query;

/**
 * Query entry.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
@SerializeWith(id=1009)
public class QueryEntry extends OperationEntry<QueryEntry> {
  private Query query;

  public QueryEntry(long index) {
    super(null);
    setIndex(index);
  }

  public QueryEntry(ReferenceManager<Entry<?>> referenceManager) {
    super(referenceManager);
  }

  /**
   * Returns the query.
   *
   * @return The query.
   */
  public Query getQuery() {
    return query;
  }

  /**
   * Sets the query.
   *
   * @param query The query.
   * @return The query entry.
   */
  public QueryEntry setQuery(Query query) {
    this.query = query;
    return this;
  }

  @Override
  public void writeObject(Buffer buffer, Serializer serializer) {
    super.writeObject(buffer, serializer);
    serializer.writeObject(query, buffer);
  }

  @Override
  public void readObject(Buffer buffer, Serializer serializer) {
    super.readObject(buffer, serializer);
    query = serializer.readObject(buffer);
  }

  @Override
  public String toString() {
    return String.format("%s[index=%d, term=%d, session=%d, timestamp=%d, query=%s]", getClass().getSimpleName(), getIndex(), getTerm(), getSession(), getTimestamp(), query);
  }

}
