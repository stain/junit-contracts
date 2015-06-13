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

import java.io.Serializable;

/**
 * This filter produces a logical NOT of the specified filter
 *
 */
public class NotClassFilter extends AbstractBaseClassFilter implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2488500113297018806L;
	/** The filter */
	private final ClassFilter filter;

	/**
	 * Constructs a new file filter that NOTs the result of another filter.
	 * 
	 * @param filter
	 *            the enclosed filter, must not be null
	 * @throws IllegalArgumentException
	 *             if the filter is null
	 */
	public NotClassFilter(ClassFilter filter) {
		if (filter == null) {
			throw new IllegalArgumentException("The filter must not be null");
		}
		this.filter = filter;
	}

	/**
	 * Returns the logical NOT of the underlying filter's return value for the
	 * same File.
	 * 
	 * @param class the Class to check
	 * @return true if the enclosed filter returns false
	 */
	@Override
	public boolean accept(Class<?> clazz) {
		return !filter.accept(clazz);
	}

	/**
	 * Returns the logical NOT of the underlying filter's return value for the
	 * same arguments.
	 * 
	 * @param classNname
	 *            the class name to check.
	 * @return true if the enclosed filter returns false
	 */
	@Override
	public boolean accept(String className) {
		return !filter.accept(className);
	}

	/**
	 * Provide a String representation of this c;ass filter.
	 *
	 * @return a String representation
	 */
	@Override
	public String toString() {
		return ClassFilter.Util.toString(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] args() {
		return new String[] { filter.toString() };
	}

}