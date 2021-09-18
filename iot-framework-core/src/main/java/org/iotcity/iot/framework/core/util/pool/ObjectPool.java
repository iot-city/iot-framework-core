package org.iotcity.iot.framework.core.util.pool;

import java.lang.ref.SoftReference;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The object pool for thread safe.
 * 
 * <pre>
 * // An example of using object pool.
 * ObjectPool<DemoBean> pool = new ObjectPool<DemoBean>(true, false, 8) {
 * 
 * 	&#64;Override
 * 	protected DemoBean create() {
 * 		DemoBean bean = new DemoBean();
 * 		// ...
 * 		return bean;
 * 	}
 * 
 * 	&#64;Override
 * 	protected void reset(DemoBean object) {
 * 		// ...
 * 	}
 * };
 * 
 * // Obtain an object from pool.
 * DemoBean bean = pool.obtain();
 * // Do something ...
 * // Free the object when finished.
 * pool.free(bean);
 * </pre>
 * 
 * @author ardon
 * @date 2021-09-16
 */
public abstract class ObjectPool<T> {

	/**
	 * Indicates whether soft references are used.
	 */
	private final boolean soft;
	/**
	 * The queue.
	 */
	private final Queue<T> queue;
	/**
	 * Indicates whether the queue has been destroyed.
	 */
	private AtomicBoolean destroyed = new AtomicBoolean();

	/**
	 * Constructor for object pool with no maximum capacity.
	 * @param threadSafe Indicates whether thread safety is supported.
	 * @param softReferences Indicates whether soft references are used.
	 */
	public ObjectPool(boolean threadSafe, boolean softReferences) {
		this(threadSafe, softReferences, Integer.MAX_VALUE);
	}

	/**
	 * Constructor for object pool.
	 * @param threadSafe Indicates whether thread safety is supported.
	 * @param softReferences Indicates whether soft references are used.
	 * @param maximumCapacity The maximum number of free objects to store in this pool. Objects are not created until {@link #obtain()} is called and no free objects are available.
	 */
	public ObjectPool(boolean threadSafe, boolean softReferences, final int maximumCapacity) {
		Queue<T> queue;
		this.soft = softReferences;
		if (threadSafe) {
			// Create thread safe queue.
			queue = new LinkedBlockingQueue<T>(maximumCapacity) {

				private static final long serialVersionUID = 1L;

				@Override
				public boolean add(T o) {
					return super.offer(o);
				}

			};
		} else if (softReferences) {
			// Create no thread safe queue for soft reference.
			queue = new LinkedList<T>() {

				private static final long serialVersionUID = 1L;

				public boolean add(T object) {
					if (size() >= maximumCapacity) return false;
					super.add(object);
					return true;
				}

			};
		} else {
			// Create normal queue.
			queue = new ArrayDeque<T>() {

				private static final long serialVersionUID = 1L;

				public boolean offer(T object) {
					if (size() >= maximumCapacity) return false;
					super.offer(object);
					return true;
				}

			};
		}
		// Store queue.
		if (softReferences) {
			@SuppressWarnings("unchecked")
			Queue<T> squeue = new SoftReferenceQueue<>(((Queue<SoftReference<T>>) queue));
			this.queue = squeue;
		} else {
			this.queue = queue;
		}
	}

	/**
	 * Returns an object from this pool (never null).
	 */
	public T obtain() {
		T object = queue.poll();
		return object != null ? object : create();
	}

	/**
	 * Puts the specified object in the pool, making it eligible to be returned by {@link #obtain()}.<br/>
	 * If the pool already contains the maximum number of free objects, the specified object is reset but not added to the pool.<br/>
	 * If using soft references and the pool contains the maximum number of free objects, the first soft reference whose object has been garbage collected is discarded to make room.
	 * @param object The object from pool.
	 */
	public void free(T object) {
		if (object == null) return;
		if (destroyed.get()) {
			reset(object);
		} else {
			if (!queue.offer(object)) {
				if (soft) {
					((SoftReferenceQueue<T>) queue).removeOne();
					if (!queue.offer(object)) {
						reset(object);
					}
				} else {
					reset(object);
				}
			}
		}
	}

	/**
	 * Removes and reset all free objects from this pool.
	 */
	public void destroy() {
		if (destroyed.compareAndSet(false, true)) {
			for (Iterator<T> iter = queue.iterator(); iter.hasNext();) {
				T object = iter.next();
				if (object != null) reset(object);
			}
			queue.clear();
		}
	}

	/**
	 * The number of objects available to be obtained.<br/>
	 * If using soft references, this number may include objects that have been garbage collected.<br/>
	 * {@link #clean()} may be used first to remove empty soft references.
	 */
	public int size() {
		return queue.size();
	}

	/**
	 * Create a new object and store to the pool.
	 * @return The object instance.
	 */
	protected abstract T create();

	/**
	 * Reset the object resource on calling {@link #free(Object)}.
	 * @param object The object instance (never null).
	 */
	protected abstract void reset(T object);

}
