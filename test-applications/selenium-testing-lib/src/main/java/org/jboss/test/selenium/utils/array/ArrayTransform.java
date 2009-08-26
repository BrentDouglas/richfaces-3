/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */

package org.jboss.test.selenium.utils.array;

/**
 * Abstract class providing generic array transformations.
 * 
 * Use overridden method transform(S[] sourceArray) to transform
 * sourceArray to T[] targetArray. Method transform(S[] sourceArray) is
 * implementation of transformation of each item from type S to type T.
 * 
 * Items are transformed to target array as one-to-one preserving order of source.
 *          
 * @author <a href="mailto:lfryc@redhat.com">Lukas Fryc</a>
 * @version $Revision$
 * @param <S> Type of source items.
 * @param <T> Type of target items.
 */
public abstract class ArrayTransform<S, T> {
	
    /**
	 * This method is implementation of transformation each item of sourceArray
	 * and type S to item of type T in targetArray.
	 * 
	 * @param source
	 *            transformation object
	 * @return transformation result
	 */
	public abstract T transformation(S source);

	private Class<T> tClass;

	/**
	 * Constructs ArrayTransform with implementation of transformation
	 * predefining tClass like a class of transformation-target array.
	 * 
	 * @param tClass
	 *            class of type T in which should be typed resulting target array
	 */
	public ArrayTransform(Class<T> tClass) {
		this.tClass = tClass;
	}

	/**
	 * Process transformation of S[] sourceArray with T[] targetArray like
	 * return value.
	 * 
	 * @param sourceArray
	 *            array of type S which should be transformed to targetArray of type T
	 * @return targetArray of type T after transformation from S[] sourceArray
	 */
	@SuppressWarnings("unchecked")
	public T[] transform(S[] sourceArray) {
		T[] targetArray = (T[]) java.lang.reflect.Array.newInstance(tClass,
				sourceArray.length);
		for (int i = 0; i < sourceArray.length; i++) {
			targetArray[i] = transformation(sourceArray[i]);
		}
		return targetArray;
	}
}
