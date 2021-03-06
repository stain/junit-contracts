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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xenei.classpathutils.ClassPathFilter;
import org.xenei.classpathutils.ClassPathUtils;
import org.xenei.classpathutils.filter.AndClassFilter;
import org.xenei.classpathutils.filter.HasAnnotationClassFilter;
import org.xenei.classpathutils.filter.NotClassFilter;
import org.xenei.classpathutils.filter.OrClassFilter;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.NoContractTest;
import org.xenei.junit.contract.info.ContractTestMap;
import org.xenei.junit.contract.info.TestInfo;

/**
 * 
 * Class to produce report data about the state of the contract tests.
 *
 */
public class InterfaceReport {

	/**
	 * A collection of all classes in the package classes. This includes
	 * interfaces, abstract and concrete implementations.
	 */
	private final Collection<Class<?>> packageClasses;

	/**
	 * A filter that describes the classes to skip
	 */
	private final ClassPathFilter filter;

	/**
	 * A map of all interfaces implemented in the packages that have contract
	 * tests to their InterfaceInfo record.
	 */
	private Map<Class<?>, InterfaceInfo> interfaceInfoMap;

	/**
	 * A map of interface to all contract tests for the interface. this includes
	 * classes not in the specified packages.
	 **/
	private final ContractTestMap contractTestMap;

	private final ContractImplMap contractImplMap;

	private static final Log LOG = LogFactory
			.getLog(ContractTestMap.class);

	private static final ClassPathFilter INTERESTING_CLASSES = new AndClassFilter(
			ClassPathFilter.INTERFACE_CLASS, new NotClassFilter(ClassPathFilter.ANNOTATION_CLASS),
			new NotClassFilter(new HasAnnotationClassFilter(
					NoContractTest.class)));

	private static final Comparator<Class<?>> CLASS_NAME_COMPARATOR = new Comparator<Class<?>>() {

		@Override
		public int compare(final Class<?> o1, final Class<?> o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};

	/**
	 * Get a collection of InterfaceInfo objects for the entire test suite.
	 * 
	 * @return The colleciton of InterfaceInfo objects.
	 */
	public Collection<InterfaceInfo> getInterfaceInfoCollection() {
		return getInterfaceInfoMap().values();
	}

	/**
	 * Get the interface info map.
	 *
	 * All interfaces implemented in the packages that have contract tests.
	 *
	 * @return
	 */
	private Map<Class<?>, InterfaceInfo> getInterfaceInfoMap() {

		if (interfaceInfoMap == null) {
			interfaceInfoMap = new HashMap<Class<?>, InterfaceInfo>();
			for (final Class<?> c : packageClasses) {
				if (INTERESTING_CLASSES.accept(c)) {
					if (!interfaceInfoMap.containsKey(c)) {
						interfaceInfoMap.put(c, new InterfaceInfo(c));
					}
				} else {
					final Contract contract = c.getAnnotation(Contract.class);
					if (contract != null) {
						InterfaceInfo ii = interfaceInfoMap.get(contract
								.value());
						if (ii == null) {
							ii = new InterfaceInfo(contract.value());
							interfaceInfoMap.put(contract.value(), ii);
						}
						ii.add(c);
					}
				}
			}
		}
		return interfaceInfoMap;
	}

	/**
	 * Constructor.
	 * 
	 * If the filter parameter is null it defaults to <code>true()</code> and
	 * all classes are processed.
	 * 
	 * @param packages
	 *            The list of packages to process.
	 * @param filter
	 *            the filter of classes to process. may be null.
	 * @param classLoader
	 *            the class loader to use.
	 */
	public InterfaceReport(final String[] packages, ClassPathFilter filter,
			final ClassLoader classLoader) {

		if (packages.length == 0) {
			throw new IllegalArgumentException(
					"At least one package must be specified");
		}

		contractTestMap = new ContractTestMap();
		if (filter != null) {
			this.filter = filter;
		} else {
			this.filter = ClassPathFilter.TRUE;
		}

		// find all the contract annotated tests on the class path.
		// this includes classes not in the specified packages
		packageClasses = new HashSet<Class<?>>();
		for (final String p : packages) {
			packageClasses.addAll(ClassPathUtils.getClasses(classLoader, p,
					this.filter));
		}

		if (packageClasses.size() == 0) {
			throw new IllegalArgumentException("No classes found in "
					+ Arrays.asList(packages));
		}

		contractImplMap = new ContractImplMap(packageClasses);
	}

	/**
	 * Get the collection of Class objects that were included in the packages.
	 * 
	 * @return The collection of Class objects that were included in the
	 *         packages.
	 */
	public Collection<Class<?>> getPackageClasses() {
		return packageClasses;
	}

	/**
	 * Get the set of errors encountered when discovering contract tests. This
	 * is a list of all errors for all tests.
	 * 
	 * @return The list of Throwable objects that represent errors.
	 *
	 */
	public List<Throwable> getErrors() {
		final List<Throwable> retval = new ArrayList<Throwable>();
		for (final TestInfo testInfo : contractTestMap.listTestInfo()) {
			retval.addAll(testInfo.getErrors());
		}
		return retval;
	}

	/**
	 * get a set of interfaces that do not have contract tests defined.
	 *
	 * @return The list of interfaces that don't have contract tests defined.
	 */
	public Set<Class<?>> getUntestedInterfaces() {
		final Set<Class<?>> retval = new TreeSet<Class<?>>(
				CLASS_NAME_COMPARATOR);

		for (final InterfaceInfo info : getInterfaceInfoMap().values()) {
			// no test and has methods
			if (info.getTests().isEmpty()
					&& (info.getName().getDeclaredMethods().length > 0)) {
				retval.add(info.getName());
			}
		}
		return retval;
	}

	/**
	 * Search for classes that extend interfaces with contract tests but that
	 * don't have an implementation of the test producer.
	 *
	 * @return the set of Classes that do not have contract tests.
	 */
	public Set<Class<?>> getUnImplementedTests() {
		final Set<Class<?>> retval = new TreeSet<Class<?>>(
				CLASS_NAME_COMPARATOR);
		// only interested in concrete implementations
		ClassPathFilter filter = new NotClassFilter(new OrClassFilter(
				ClassPathFilter.ABSTRACT_CLASS, ClassPathFilter.INTERFACE_CLASS));

		for (final Class<?> clazz : filter.filterClasses(packageClasses)) {
			// we are only interested if there is no contract test for the
			// class and there are parent tests
			LOG.debug(String.format("checking %s for contract tests", clazz));
			final Set<Class<?>> interfaces = contractTestMap
					.getAllInterfaces(clazz);
			if ( !interfaces.isEmpty() ) 
			{
				final Map<Class<?>, InterfaceInfo> interfaceInfo = getInterfaceInfoMap();

				interfaces.retainAll(interfaceInfo.keySet());
			}
			// interfaces contains only contract test interfaces that clazz
			// implements.
			if (!interfaces.isEmpty()) {
				// not empty so we are need to verify that we have a test
				// for clazz
				if (!contractImplMap.hasTestFor(clazz)) {
					retval.add(clazz);
				}
			}
		}
		return retval;
	}

	/**
	 * Get the filter
	 * 
	 * @return The filter used to filter classes.
	 */
	public ClassPathFilter getClassFilter() {
		return filter;
	}

	/**
	 * A mapping of contracts implementations to tests
	 *
	 * contract implementations are classes annotated with
	 * <code>@ContaractImpl</code>
	 *
	 * tests are the the contract tests being tested by the implementation.
	 * Tests are annotated with <Code>@Contract</code>.
	 *
	 * A test may have more than one implementation.
	 */
	private static class ContractImplMap {
		// the map of the contract tests to their implementations.
		private final Map<Class<?>, Set<Class<?>>> forwardMap;
		// the map of an implementation to the contract it tests.
		private final Map<Class<?>, Class<?>> reverseMap;

		/**
		 * Constructor.
		 * @param classes The classes for the map.
		 */
		public ContractImplMap(final Collection<Class<?>> classes) {
			forwardMap = new HashMap<Class<?>, Set<Class<?>>>();
			reverseMap = new HashMap<Class<?>, Class<?>>();
			for (final Class<?> c : classes) {
				final ContractImpl contractImpl = c
						.getAnnotation(ContractImpl.class);
				if (contractImpl != null) {
					add(c, contractImpl);
				}
			}
		}

		/**
		 * Add a ContractImpl to the map.
		 *
		 * @param contractTestImplClass
		 *            The class annotated with <code>@ContractImpl</code>
		 * @param contractImpl
		 *            The ContractImpl annotation.
		 */
		private void add(final Class<?> contractTestImplClass,
				final ContractImpl contractImpl) {
			Set<Class<?>> set = forwardMap.get(contractImpl.value());
			if (set == null) {
				set = new HashSet<Class<?>>();
				forwardMap.put(contractImpl.value(), set);
			}
			set.add(contractTestImplClass);
			reverseMap.put(contractTestImplClass, contractImpl.value());
		}

		/**
		 * Return true if there is a contract test implementation for a specific
		 * contract test.
		 *
		 * @param contractTestImplClass
		 *            The class annotated with <code>@ContractImpl</code>
		 * @return
		 */
		public boolean hasTestFor(final Class<?> contractTestImplClass) {
			return forwardMap.containsKey(contractTestImplClass);
		}
	}

}
