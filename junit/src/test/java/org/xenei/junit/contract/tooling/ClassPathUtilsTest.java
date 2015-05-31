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

package org.xenei.junit.contract.tooling;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;
import org.xenei.junit.contract.ClassPathUtils;
import org.xenei.junit.contract.filter.PrefixClassFilter;
import org.xenei.junit.contract.filter.WildcardClassFilter;

public class ClassPathUtilsTest {

	@Test
	public void testFindClassesFromClassJar() throws IOException
	{
		URL url = ClassPathUtilsTest.class.getResource("classes.jar");
		Set<String> names = ClassPathUtils.findClasses(url.toString()+"!/", "org.xenei.junit");
		assertEquals( 3, names.size() );
		names = ClassPathUtils.findClasses(url.toString()+"!/", "org.xenei.junit.contract.info");
		assertEquals( 1, names.size() );
		names = ClassPathUtils.findClasses(url.toString()+"!/", "com.xenei");
		assertEquals( 0, names.size() );
	}

	@Test
	public void testFindClassesFromJavadocJar() throws IOException
	{
		URL url = ClassPathUtilsTest.class.getResource("javadoc.jar");
		Set<String> names = ClassPathUtils.findClasses(url.toString()+"!/", "org.xenei.junit");
		assertEquals( 0, names.size() );
	}

	@Test
	public void testFindClassesFromSourceJar() throws IOException
	{
		URL url = ClassPathUtilsTest.class.getResource("sources.jar");
		Set<String> names = ClassPathUtils.findClasses(url.toString()+"!/", "org.xenei.junit");
		assertEquals( 0, names.size() );
	}

	@Test
	public void testFindClasses_StringString() throws IOException
	{
		URL url = ClassPathUtilsTest.class.getResource("/");
		Set<String> names = ClassPathUtils.findClasses(url.toString(), "org.xenei.junit.bad");
		assertEquals( 2, names.size() );
	}
	
	@Test
	public void testFindClasses_StringFilter() throws IOException
	{
		URL url = ClassPathUtilsTest.class.getResource("/org/xenei/junit");
		Set<String> names = ClassPathUtils.findClasses(url.toString(), "org.xenei.junit", new PrefixClassFilter("org.xenei.junit.bad"));
		assertEquals( 2, names.size() );
		
		url = ClassPathUtilsTest.class.getResource("/");
		names = ClassPathUtils.findClasses(url.toString(), "org.xenei.junit", new PrefixClassFilter("org.xenei.junit.bad"));
		assertEquals( 2, names.size() );
	}
	
	@Test
	public void testGetClasses_String() throws IOException
	{
		Collection<Class<?>> classes = ClassPathUtils.getClasses( "org.xenei.junit.bad");
		assertEquals( 2, classes.size() );
	}
	
	@Test
	public void testGetClasses_Filter() throws IOException
	{
		Collection<Class<?>> classes = ClassPathUtils.getClasses( "org.xenei.junit", new WildcardClassFilter("*.bad.*"));
		assertEquals( 2, classes.size() );
	}
}
