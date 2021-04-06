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
	private Set<String> parsePackages = new HashSet<>();
	/**
	 * Packages to ignore
	 */
	private Set<String> ignorePackages = new HashSet<>();
	/**
	 * Ignore packages array
	 */
	private String[] ignores = new String[0];

	// --------------------------- Public methods ----------------------------

	/**
	 * Get all packages that need to be parsed
	 * @return String[] The package array
	 */
	public String[] getParsePackages() {
		return this.parsePackages.toArray(new String[this.parsePackages.size()]);
	}

	/**
	 * Determine whether the package set to be ignored
	 * @param pkgName The package name or class file name (e.g. "org.iotcity.iot.framework.actor.test")
	 * @return boolean If returns true, the package is ignored, otherwise will be parsed
	 */
	public boolean isIgnoredPackage(String pkgName) {
		if (pkgName == null || pkgName.length() == 0 || this.ignorePackages.size() == 0) return false;
		if (this.ignorePackages.contains(pkgName)) return true;
		String[] pkgs = this.ignores;
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
	public void addAllPackages(String[] parsePackages, String[] ignorePackages) {
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
			this.ignores = this.ignorePackages.toArray(new String[this.ignorePackages.size()]);
		}
	}

	/**
	 * Add a package that need to be parsed
	 * @param pkgName The package full name (e.g. "org.iotcity.iot.framework.actor.test")
	 */
	public void addParsePackage(String pkgName) {
		this.parsePackages.add(pkgName);
	}

	/**
	 * Remove a package that has been added to parse
	 * @param pkgName The package full name (e.g. "org.iotcity.iot.framework.actor.test")
	 */
	public void removeParsePackage(String pkgName) {
		this.parsePackages.remove(pkgName);
	}

	/**
	 * Clear all packages that has been added to parse
	 */
	public void clearParsePackage() {
		this.parsePackages.clear();
	}

	/**
	 * Add a ignore package, analyzer will skip parse this package
	 * @param pkgName The package full name (e.g. "org.iotcity.iot.framework.actor.test")
	 */
	public void addIgnorePackage(String pkgName) {
		this.ignorePackages.add(pkgName);
		this.ignores = this.ignorePackages.toArray(new String[this.ignorePackages.size()]);
	}

	/**
	 * Remove a ignore package
	 * @param pkgName The package full name (e.g. "org.iotcity.iot.framework.actor.test")
	 */
	public void removeIgnorePackage(String pkgName) {
		this.ignorePackages.remove(pkgName);
		this.ignores = this.ignorePackages.toArray(new String[this.ignorePackages.size()]);
	}

	/**
	 * Clear all ignore packages
	 */
	public void clearIgnorePackage() {
		this.ignorePackages.clear();
		this.ignores = this.ignorePackages.toArray(new String[this.ignorePackages.size()]);
	}

}
