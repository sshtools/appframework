/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package com.sshtools.appframework.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LimitedCache<K, V> {

	private Map<K, V> map = new HashMap<K, V>();
	private List<K> keys = new ArrayList<K>();
	private List<V> values = new ArrayList<V>();
	private int limit = 1000;

	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void cache(K key, V value) {
		while (values.size() > limit) {
			values.remove(0);
			K k = keys.remove(0);
			map.remove(k);
		}
		if (map.containsKey(key)) {
			keys.remove(key);
			values.remove(map.get(key));
		}
		keys.add(key);
		values.add(value);
		map.put(key, value);
	}

	public V get(K key) {
		return map.get(key);
	}

}
