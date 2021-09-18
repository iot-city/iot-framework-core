package org.iotcity.iot.framework.core.util.pool;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.function.Predicate;

/**
 * The soft reference queue.
 * @author ardon
 * @date 2021-09-16
 */
final class SoftReferenceQueue<T> implements Queue<T> {

	/**
	 * The soft reference queue.
	 */
	private final Queue<SoftReference<T>> queue;

	/**
	 * Constructor for soft reference queue.
	 * @param queue The queue.
	 */
	SoftReferenceQueue(Queue<SoftReference<T>> queue) {
		this.queue = queue;
	}

	/**
	 * Remove one object with null value.
	 */
	void removeOne() {
		for (Iterator<SoftReference<T>> iter = queue.iterator(); iter.hasNext();) {
			if (iter.next().get() == null) {
				iter.remove();
				break;
			}
		}
	}

	/**
	 * Remove all objects with null value.
	 */
	void removeAll() {
		queue.removeIf(new Predicate<SoftReference<T>>() {

			@Override
			public boolean test(SoftReference<T> t) {
				return t.get() == null;
			}

		});
	}

	@Override
	public T poll() {
		while (true) {
			SoftReference<T> reference = queue.poll();
			if (reference == null) return null;
			T object = reference.get();
			if (object != null) return object;
		}
	}

	@Override
	public boolean offer(T e) {
		return queue.add(new SoftReference<>(e));
	}

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public void clear() {
		queue.clear();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return null;
	}

	@Override
	public Object[] toArray() {
		return null;
	}

	@Override
	public <E> E[] toArray(E[] a) {
		return null;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean add(T e) {
		return false;
	}

	@Override
	public T remove() {
		return null;
	}

	@Override
	public T element() {
		return null;
	}

	@Override
	public T peek() {
		return null;
	}

}
