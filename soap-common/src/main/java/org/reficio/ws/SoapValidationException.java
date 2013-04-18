/**
 * Copyright (c) 2012-2013 Reficio (TM) - Reestablish your software!. All Rights Reserved.
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
package org.reficio.ws;

import java.util.ArrayList;
import java.util.List;

/**
 * Default exception thrown by the SoapBuilder.
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
public class SoapValidationException extends SoapException {

    private final List<AssertionError> errors;

    public SoapValidationException(List<AssertionError> errors) {
        super("Message validation failed with " + errors.size() + " error(s)\n" + errors);
        this.errors = errors;
    }

    public List<AssertionError> getErrors() {
        return new ArrayList<AssertionError>(errors);
    }

}
