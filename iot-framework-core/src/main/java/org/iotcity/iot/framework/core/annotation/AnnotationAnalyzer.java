package org.iotcity.iot.framework.core.annotation;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Analyze annotations of the specified scope
 * @author Ardon
 */
public final class AnnotationAnalyzer extends AnnotationPackages {

	// --------------------------- Private fields ----------------------------

	/**
	 * Class file filter
	 */
	private static final FileFilter fileFiter = new FileFilter() {

		@Override
		public boolean accept(File file) {
			return file.isFile() ? file.getName().endsWith(".class") : true;
		}

	};

	/**
	 * The parsers in analyzer
	 */
	private final List<AnnotationParser> parsers = new ArrayList<>();
	/**
	 * The files parsed
	 */
	private final Set<String> parseFiles = new HashSet<>();

	// --------------------------- Public methods ----------------------------

	/**
	 * Add annotation parser object to analyzer
	 * @param parser Annotation parser
	 */
	public void addParser(AnnotationParser parser) {
		this.parsers.add(parser);
	}

	/**
	 * Start analyze
	 * @throws Exception Throw an exception when an error is encountered
	 */
	public void start() throws Exception {
		if (this.parsers.size() == 0) return;
		String[] packages = this.getParsePackages();
		if (packages == null || packages.length == 0) return;
		this.parseFiles.clear();
		for (String pkg : packages) {
			this.analyzePackage(pkg);
		}
		this.parseFiles.clear();
	}

	// --------------------------- Private methods ----------------------------

	/**
	 * Analyze a package for class file
	 * @param pkg package name (e.g. "org.iotcity.iot.framework.actor.test")
	 * @throws Exception Throw an exception when an error is encountered
	 */
	private void analyzePackage(String pkg) throws Exception {
		// Load resource from file
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String pkgPath = pkg.replaceAll("\\.", "/");
		Enumeration<URL> urls = loader.getResources(pkgPath);
		if (urls != null) {
			// From resources
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				if (url == null) continue;
				String protocol = url.getProtocol();
				if ("file".equals(protocol)) {
					this.analyzeFileClasses(pkg, new File(url.getPath()).listFiles(fileFiter));
				} else if ("jar".equals(protocol)) {
					this.analyzeJarClasses(pkgPath, ((JarURLConnection) url.openConnection()).getJarFile());
				}
			}
		} else {
			// From workspace
			URL[] urls2 = ((URLClassLoader) loader).getURLs();
			for (int i = 0; i < urls2.length; i++) {
				URL url = urls2[i];
				String urlPath = url.getPath();
				if (urlPath.endsWith("classes/")) continue;
				String jarPath = urlPath + "!/" + pkgPath;
				String[] jarInfo = jarPath.split("!");
				String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
				this.analyzeJarClasses(pkgPath, new JarFile(jarFilePath));
			}
		}
	}

	/**
	 * Analyze file classes
	 * @param pkgName The package name (e.g. "org.iotcity.iot.framework.actor.test")
	 * @param files Files for analyzing
	 * @throws Exception Throw an exception when an error is encountered
	 */
	private void analyzeFileClasses(String pkgName, File[] files) throws Exception {
		if (files == null || files.length == 0 || this.isIgnoredPackage(pkgName)) return;
		for (File f : files) {
			String fileName = f.getName();
			if (f.isFile()) {
				int pos = fileName.lastIndexOf(".");
				if (pos <= 0) continue;
				String className = pkgName + "." + fileName.substring(0, pos);
				this.analyzeClass(className);
			} else if (f.isDirectory()) {
				String newPkg = pkgName.length() > 0 ? (pkgName + "." + fileName) : fileName;
				this.analyzeFileClasses(newPkg, new File(f.getPath()).listFiles(fileFiter));
			}
		}
	}

	/**
	 * Analyze classes in jar file
	 * @param pkgPath The package path (e.g. "org/iotcity/iot/framework/actor/test")
	 * @param jar JAR file
	 * @throws Exception Throw an exception when an error is encountered
	 */
	private void analyzeJarClasses(String pkgPath, JarFile jar) throws Exception {
		if (jar == null) return;
		try {
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.isDirectory()) continue;
				String name = entry.getName();
				if (name == null || name.length() < 8) continue;
				if (name.charAt(0) == '/') name = name.substring(1);
				if (!name.startsWith(pkgPath) || !name.endsWith(".class")) continue;
				name = name.replaceAll("/", ".");
				name = name.substring(0, name.length() - 6);
				this.analyzeClass(name);
			}
		} finally {
			try {
				if (jar != null) jar.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * Analyze a class file
	 * @param className Class path name
	 * @throws Exception Throw an exception when an error is encountered
	 */
	private void analyzeClass(String className) throws Exception {
		if (this.isIgnoredPackage(className)) return;
		if (this.parseFiles.contains(className)) return;
		this.parseFiles.add(className);
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (clazz == null || clazz.isInterface() || clazz.getConstructors().length == 0) return;
		for (AnnotationParser parser : this.parsers) {
			parser.parse(clazz);
		}
	}

}
