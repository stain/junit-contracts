/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xenei.junit.bad;

/**
 * Class to test that the @Ignore annotation works and that the
 * system can properly detect an abstract @Contract annotated class
 * as an error.
 * 
 */
import org.junit.Ignore;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.IProducer;
import org.xenei.junit.contract.exampleTests.A;

/**
 * An implementation of an A contract test that is abstract.
 * 
 * THIS CONSTRUCT IS NOT ALLOWED AND IS ONLY USED FOR TESTING THE DETECTION OF
 * SUCH A CONDITION.
 *
 */
@Contract(A.class)
@Ignore
public abstract class BadAbstract {

	/**
	 * Constructor
	 */
	public BadAbstract() {
	}

	/**
	 * set the producer
	 * 
	 * @param producer
	 *            The producer we are testing.
	 */
	@Contract.Inject
	public final void setProducer(IProducer<A> producer) {
	}

}
