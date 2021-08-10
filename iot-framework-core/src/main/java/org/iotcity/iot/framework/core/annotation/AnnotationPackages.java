package org.iotcity.iot.framework.core.annotation;

import java.util.HashSet;
import java.util.Set;

import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Configure the parse scope of annotations
 * @author Ardon
 */
public class AnnotationPackages {

	// --------------------------- Private fields ----------------------------

	/**
	 * Packages to parse
	 */
	private final Set<String> parsePackages = new HashSet<>();
	/**
	 * Packages to ignore
	 */
	private final Set<String> ignorePackages = new HashSet<>();
	/**
	 * Whether the ignore packages has changed.
	 */
	private boolean changed = true;
	/**
	 * Ignore packages array
	 */
	private String[] ignores = new String[0];

	// --------------------------- Public methods ----------------------------

	/**
	 * Get all packages that need to be parsed
	 * @return String[] The package array
	 */
	public final String[] getParsePackages() {
		return this.parsePackages.toArray(new String[this.parsePackages.size()]);
	}

	/**
	 * Determine whether the package set to be ignored
	 * @param pkgName The package name or class file name (e.g. "org.iotcity.iot.framework.actor.test")
	 * @return boolean If returns true, the package is ignored, otherwise will be parsed
	 */
	public final boolean isIgnoredPackage(String pkgName) {
		if (pkgName == null || pkgName.length() == 0 || ignorePackages.size() == 0) return false;
		if (ignorePackages.contains(pkgName)) return true;
		if (changed) {
			changed = false;
			ignores = ignorePackages.toArray(new String[ignorePackages.size()]);
		}
		String[] pkgs = ignores;
		for (String pkg : pkgs) {
			if (pkgName.startsWith(pkg + ".")) return true;
		}
		return false;
	}

	/**
	 * Add all packages to the analyzer
	 * @param parsePackages Packages that need to be parsed
	 * @param ignorePackages Ignore packages, analyzer will skip parse these packages
	 */
	public final void addAllPackages(String[] parsePackages, String[] ignorePackages) {
		if (parsePackages != null && parsePackages.length > 0) {
			for (String pkg : parsePackages) {
				if (StringHelper.isEmpty(pkg)) continue;
				this.parsePackages.add(pkg);
			}
		}
		if (ignorePackages != null && ignorePackages.length > 0) {
			for (String pkg : ignorePackages) {
				if (StringHelper.isEmpty(pkg)) continue;
				this.ignorePackages.add(pkg);
			}
			if (!changed) changed = true;
		}
	}

	/**
	 * Add a package that need to be parsed
	 * @param pkgName The package full name (e.g. "org.iotcity.iot.framework.actor.test")
	 */
	public final void addParsePackage(String pkgName) {
		parsePackages.add(pkgName);
	}

	/**
	 * Remove a package that has been added to parse
	 * @param pkgName The package full name (e.g. "org.iotcity.iot.framework.actor.test")
	 */
	public final void removeParsePackage(String pkgName) {
		parsePackages.remove(pkgName);
	}

	/**
	 * Clear all packages that has been added to parse
	 */
	public final void clearParsePackage() {
		parsePackages.clear();
	}

	/**
	 * Add a ignore package, analyzer will skip parse this package
	 * @param pkgName The package full name (e.g. "org.iotcity.iot.framework.actor.test")
	 */
	public final void addIgnorePackage(String pkgName) {
		ignorePackages.add(pkgName);
		if (!changed) changed = true;
	}

	/**
	 * Remove a ignore package
	 * @param pkgName The package full name (e.g. "org.iotcity.iot.framework.actor.test")
	 */
	public final void removeIgnorePackage(String pkgName) {
		ignorePackages.remove(pkgName);
		if (!changed) changed = true;
	}

	/**
	 * Clear all ignore packages
	 */
	public final void clearIgnorePackage() {
		ignorePackages.clear();
		if (!changed) changed = true;
	}

}
