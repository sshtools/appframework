/**
 * Maverick Application Framework - Application framework
 * Copyright © ${project.inceptionYear} SSHTOOLS Limited (support@sshtools.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.sshtools.appframework.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LimitedCache<K, V> {

	private List<K> keys = new ArrayList<K>();
	private int limit = 1000;
	private Map<K, V> map = new HashMap<K, V>();
	private List<V> values = new ArrayList<V>();

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

	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	public V get(K key) {
		return map.get(key);
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
