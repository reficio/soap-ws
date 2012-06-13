/**
 * Copyright (c) 2012 centeractive ag. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.centeractive.ws.server.matcher;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract visitor that contains convenience methods to store and retrieve results of a generic-type
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
public abstract class AggregatingVisitor<T> implements BindingOperationVisitor {

    private Set<T> results = new HashSet<T>();

    /**
     * Stores a result
     * @param result result to store
     */
    public void addResult(T result) {
        results.add(result);
    }

    /**
     * Returns a result if only one was found
     * @return found unique result
     */
    public T getUniqueResult() {
        // return if only one unique result was found
        if(results.size() == 1) {
            return results.iterator().next();
        }
        return null;
    }

    /**
     * Returns all found results
     * @return Returns all found results
     */
    public Set<T> getResults() {
        return new HashSet<T>(results);
    }
}
