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

package org.xenei.junit.contract.filter;

import static org.junit.Assert.*;

import org.junit.Test;
import org.xenei.junit.contract.filter.parser.Parser;

public class AndClassFilterTest {

	private Class<?> cls = String.class;
	private String str = cls.getName();

	@Test
	public void testAcceptClass() {
		ClassFilter filter = new AndClassFilter(ClassFilter.FALSE,
				ClassFilter.FALSE);
		assertFalse(filter.accept(cls));

		filter = new AndClassFilter(ClassFilter.FALSE, ClassFilter.TRUE);
		assertFalse(filter.accept(cls));

		filter = new AndClassFilter(ClassFilter.TRUE, ClassFilter.FALSE);
		assertFalse(filter.accept(cls));

		filter = new AndClassFilter(ClassFilter.TRUE, ClassFilter.TRUE);
		assertTrue(filter.accept(cls));
	}

	@Test
	public void testAccceptString() {
		ClassFilter filter = new AndClassFilter(ClassFilter.FALSE,
				ClassFilter.FALSE);
		assertFalse(filter.accept(str));

		filter = new AndClassFilter(ClassFilter.FALSE, ClassFilter.TRUE);
		assertFalse(filter.accept(str));

		filter = new AndClassFilter(ClassFilter.TRUE, ClassFilter.FALSE);
		assertFalse(filter.accept(str));

		filter = new AndClassFilter(ClassFilter.TRUE, ClassFilter.TRUE);
		assertTrue(filter.accept(str));
	}

	@Test
	public void testToString() {
		ClassFilter filter = new AndClassFilter(ClassFilter.FALSE,
				ClassFilter.TRUE);
		assertEquals("And( False(), True() )", filter.toString());
	}

	@Test
	public void testParse() throws Exception {
		Parser p = new Parser();

		ClassFilter filter = new AndClassFilter(ClassFilter.FALSE,
				ClassFilter.TRUE);

		ClassFilter cf = p.parse(filter.toString());
		assertTrue("wrong class type", cf instanceof AndClassFilter);
		String[] args = cf.args();
		assertEquals(ClassFilter.FALSE.toString(), args[0]);
		assertEquals(ClassFilter.TRUE.toString(), args[1]);
	}

}