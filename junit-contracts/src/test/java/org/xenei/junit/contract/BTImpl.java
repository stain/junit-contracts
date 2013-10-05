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

package org.xenei.junit.contract;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * concrete example of BT implementation
 * 
 */
public class BTImpl extends BT {

	private IProducer<B> producer = new IProducer<B>() {

		@Override
		public B newInstance() {
			Listener.add("BTImpl.producer.newInstance()");
			return new BImpl();
		}

		@Override
		public void cleanUp() {
			Listener.add("BTImpl.producer.cleanUp()");
		}

	};

	@Override
	protected IProducer<B> getProducer() {
		return producer;
	}

	@After
	public final void cleanupBTImpl() {
		producer.cleanUp();
	}

	@BeforeClass
	public static void beforeClass() {
		Listener.clear();
	}

	@AfterClass
	public static void afterClass() {
		String[] expected = { "BTImpl.producer.newInstance()", "bname",
				"BTImpl.producer.cleanUp()" };

		List<String> l = Listener.get();
		Assert.assertEquals(l, Arrays.asList(expected));

	}
}
