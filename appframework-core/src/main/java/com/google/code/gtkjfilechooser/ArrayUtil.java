/*******************************************************************************
 * Copyright (c) 2010 Costantino Cerbo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Costantino Cerbo - initial API and implementation
 ******************************************************************************/
/*
 * Copyright 2010 Costantino Cerbo.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact me at c.cerbo@gmail.com if you need additional information or
 * have any questions.
 */
package com.google.code.gtkjfilechooser;

import java.lang.reflect.Array;

/**
 * @author Costantino Cerbo
 * 
 */
public class ArrayUtil {

	static public boolean areArrayEqual(Object array0, Object array1) {
		if (array0 == null && array1 == null) {
			return true;
		}

		if (array0 == null && array1 != null) {
			return false;
		}

		if (array0 != null && array1 == null) {
			return false;
		}

		if (!array0.getClass().isArray()) {
			return false;
		}

		if (!array1.getClass().isArray()) {
			return false;
		}

		int len0 = Array.getLength(array0);
		int len1 = Array.getLength(array1);
		if (len0 != len1) {
			return false;
		}

		for (int i = 0; i < len0; i++) {
			Object element0 = Array.get(array0, i);
			Object element1 = Array.get(array1, i);
			if (!element0.equals(element1)) {
				return false;
			}
		}

		return true;
	}
	
	public static void main(String[] args) {

		
	}
}
