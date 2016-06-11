/*
 * Copyright 2016 The LmdbJava Project, http://lmdbjava.org/
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
package org.lmdbjava.bench;

import java.nio.ByteBuffer;
import static java.util.Objects.requireNonNull;
import java.util.zip.CRC32;

/**
 * A key-value store implementation wrapper.
 * <p>
 * An implementation must support repeated cycles through the write, read and
 * CRC checks. Each cycle should result in a clean environment equivalent to an
 * isolated, atomic transaction for that run.
 */
abstract class AbstractStore {

  final CRC32 CRC = new CRC32();

  /**
   * The field used by the benchmark to set a key.
   */
  final ByteBuffer key;
  /**
   * The field used by the benchmark to get or set a value.
   */
  final ByteBuffer val;

  protected AbstractStore(final ByteBuffer key, final ByteBuffer val) {
    requireNonNull(key);
    requireNonNull(val);
    this.key = key;
    this.val = val;
  }

  /**
   * Iterate every key, in order, and update the CRC field. The benchmark
   * guarantees to reset the CRC field before each invocation.
   */
  abstract void crc32() throws Exception;

  /**
   * Called when the CRC phase has completed. The implementation must perform
   * any clean up required so that a fresh run can be made.
   */
  abstract void finishCrcPhase() throws Exception;

  /**
   * Fetch the key. The key to fetch is indicated by the {@link #key}. An
   * implementation must set the {@link #val} to the value. The benchmark
   * guarantees it will only request keys it has previously stored. The
   * benchmark guarantees it will not mutate the value buffer, however the
   * implementation must be free to mutate the key buffer for the subsequent get
   * call.
   */
  abstract void get() throws Exception;

  /**
   * Puts the key-value pair contained in the {@link #key} and {@link #val}
   * fields. The benchmark guarantees there will only be exactly one key for
   * each value and a given key will only ever be presented once per run. The
   * implementation must permit the benchmark to mutate the key/value fields
   * without restriction.
   */
  abstract void put() throws Exception;

  /**
   * Starts the read phase.
   */
  abstract void startReadPhase() throws Exception;

  /**
   * Starts the write phase.
   */
  abstract void startWritePhase() throws Exception;

}