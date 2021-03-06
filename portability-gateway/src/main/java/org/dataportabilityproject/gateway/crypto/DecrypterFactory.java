/*
 * Copyright 2018 The Data-Portability Project Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dataportabilityproject.gateway.crypto;

import com.google.common.base.Preconditions;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;

/**
 * Methods for creating {@link Decrypter} classes for given types of encryption keys and algorithms.
 */
public class DecrypterFactory {

  /**
   * Creates a {@link DecrypterImpl} with the given {@link SecretKey} for use with "AES"-based symmetric
   * encryption.
   */
  public static Decrypter create(SecretKey key) {
    Preconditions.checkArgument(key.getAlgorithm().equals("AES"));
    return new DecrypterImpl("AES", key);
  }

  /**
   * Creates a {@link DecrypterImpl} with the given {@link PublicKey} for use with "RSA"-based
   * asymmetric encryption.
   */
  public static Decrypter create(PublicKey key) {
    Preconditions.checkArgument(key.getAlgorithm().equals("RSA"));
    return new DecrypterImpl("RSA/ECB/PKCS1Padding", key);
  }

  /**
   * Creates a {@link DecrypterImpl} with the given {@link PrivateKey} for use with "RSA"-based
   * asymmetric encryption.
   */
  public static Decrypter create(PrivateKey key) {
    Preconditions.checkArgument(key.getAlgorithm().equals("RSA"));
    return new DecrypterImpl("RSA/ECB/PKCS1Padding", key);
  }
}
