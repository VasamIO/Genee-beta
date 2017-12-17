package com.genee.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServiceList implements Iterable<Service> {

	List<Service> list;

	public ServiceList() {
		list = new ArrayList<>();
	}

	public void add(Service service) {
		list.add(service);
	}

	public Service get(String serviceId) {
		for (Service entry : list) {
			if (entry.getId().equals(serviceId)) {
				return entry;
			}
		}
		return null;
	}

	@Override
	public Iterator<Service> iterator() {
		return list.iterator();
	}

}
