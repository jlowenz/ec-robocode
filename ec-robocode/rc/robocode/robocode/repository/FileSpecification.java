/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Ported to Java 5
 *     - Code cleanup
 *******************************************************************************/
package robocode.repository;


import java.util.*;
import java.io.*;
import java.net.*;

import robocode.util.*;
import robocode.manager.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public abstract class FileSpecification implements Comparable<FileSpecification>, Serializable, Cloneable {
	
	protected Properties props = new Properties();
	
	private final static String ROBOCODE_VERSION = "robocode.version";
	private final static String LIBRARY_DESCRIPTION = "library.description";

	protected String name;
	protected String description;
	protected String authorName;
	protected String authorEmail;
	protected String authorWebsite;
	protected String version;
	protected String robocodeVersion;
	protected boolean developmentVersion;
	protected boolean valid;
	protected URL webpage;
	protected String libraryDescription;
	protected File rootDir;
	private File packageFile;
	
	private String filePath;
	private String fileName;
	private String propertiesFileName;
	private String thisFileName;
	private String fileType;
	private NameManager nameManager;
	private long fileLastModified;
	private long fileLength;

	private boolean duplicate;

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			Utils.log("Clone not supported!");
			return null;
		}
	}
		
	protected FileSpecification() {}

	public abstract String getUid();	
	
	public static FileSpecification createSpecification(RobotRepositoryManager repositoryManager, File f, File rootDir, String prefix, boolean developmentVersion) {
		String filename = f.getName();
		String fileType = Utils.getFileType(filename);
		
		FileSpecification newSpec = null;
		
		if (fileType.equals(".team")) {
			newSpec = new TeamSpecification(f, rootDir, prefix, developmentVersion);
		} else if (fileType.equals(".jar") || fileType.equals(".zip")) {
			newSpec = new JarSpecification(f, rootDir, prefix, developmentVersion);
		} else {
			newSpec = new RobotSpecification(f, rootDir, prefix, developmentVersion);
			if (!developmentVersion && newSpec.getValid() == false) {
				newSpec = new ClassSpecification((RobotSpecification) newSpec);
			}
		}
		newSpec.developmentVersion = developmentVersion;
		newSpec.rootDir = rootDir;
		newSpec.storeJarFile(repositoryManager.getRobotsDirectory(), repositoryManager.getRobotCache());
		
		return newSpec;
	}

	private void storeJarFile(File robotDir, File robotCacheDir) {
		File src = null;

		if (rootDir.getName().indexOf(".jar_") == rootDir.getName().length() - 5
				|| rootDir.getName().indexOf(".zip_") == rootDir.getName().length() - 5) {
			if (rootDir.getParentFile().equals(robotCacheDir)) {
				src = new File(robotDir, rootDir.getName().substring(0, rootDir.getName().length() - 1));
			} else if (rootDir.getParentFile().getParentFile().equals(robotCacheDir)) {
				src = new File(rootDir.getParentFile(), rootDir.getName().substring(0, rootDir.getName().length() - 1));
			}
		}
		if (src != null && !src.exists()) {
			src = null;
		}
		this.packageFile = src;
	}
		
	public File getJarFile() {
		return packageFile;
	}
	
	public String toString() {
		return getFileName() + ": length " + getFileLength() + " modified " + getFileLastModified();
	}
	
	public boolean isDevelopmentVersion() {
		return developmentVersion;
	}

	public int compareTo(FileSpecification other) {
		return Utils.compare(getNameManager().getFullPackage(), getNameManager().getFullClassName(),
				getNameManager().getVersion(), other.getNameManager().getFullPackage(),
				other.getNameManager().getFullClassName(), other.getNameManager().getVersion());
	}

	public boolean isSameFile(String filePath, long fileLength, long fileLastModified) {
		if (!filePath.equals(this.filePath)) {
			return false;
		}
		if (fileLength != this.fileLength) {
			return false;
		}
		if (fileLastModified != this.fileLastModified) {
			return false;
		}
		return true;
	}

	public boolean isSameFile(FileSpecification other) {
		if (other == null) {
			return false;
		}
		if (other.getFileLength() != getFileLength()) {
			return false;
		}
		if (other.getFileLastModified() != getFileLastModified()) {
			return false;
		}
		return true;
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof FileSpecification) {
			FileSpecification other = (FileSpecification) o;

			if (!other.getName().equals(getName())) {
				return false;
			}
			if (getVersion() == null) {
				if (other.getVersion() != null) {
					return false;
				}
			}
			if (other.getVersion() == null) {
				if (getVersion() != null) {
					return false;
				}
			}
			if (other.getVersion() != null && getVersion() != null) {
				if (!other.getVersion().equals(getVersion())) {
					return false;
				}
			}
			if (other.getFileLength() != getFileLength()) {
				return false;
			}
			if (other.getFileLastModified() != getFileLastModified()) {
				return false;
			}
			if (!other.getFileType().equals(getFileType())) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Gets the robotName.
	 * 
	 * @return Returns a String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the robotDescription.
	 * 
	 * @return Returns a String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the robotAuthorName.
	 * 
	 * @return Returns a String
	 */
	public String getAuthorName() {
		return authorName;
	}

	/**
	 * Gets the robotAuthorEmail.
	 * 
	 * @return Returns a String
	 */
	public String getAuthorEmail() {
		return authorEmail;
	}

	/**
	 * Gets the robotAuthorWebsite.
	 * 
	 * @return Returns a String
	 */
	public String getAuthorWebsite() {
		return authorWebsite;
	}

	/**
	 * Gets the robotVersion.
	 * 
	 * @return Returns a String
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Gets the robocodeVersion
	 * 
	 * @return Returns a String
	 */
	public String getRobocodeVersion() {
		return robocodeVersion;
	}

	/**
	 * Sets the robocodeVersion
	 * 
	 * @param robocodeVersion to set
	 */
	public void setRobocodeVersion(String robocodeVersion) {
		this.robocodeVersion = robocodeVersion;
		props.setProperty(ROBOCODE_VERSION, robocodeVersion);
	}

	public String getLibraryDescription() {
		return libraryDescription;
	}

	public void setLibraryDescription(String libraryDescription) {
		this.libraryDescription = libraryDescription;
		props.setProperty(LIBRARY_DESCRIPTION, libraryDescription);
	}

	/**
	 * Gets the thisFilename.
	 * 
	 * @return Returns a String
	 */
	public String getThisFileName() {
		return thisFileName;
	}

	/**
	 * Sets the thisFilename.
	 * 
	 * @param thisFilename The thisFilename to set
	 */
	public void setThisFileName(String thisFileName) {
		this.thisFileName = thisFileName;
	}

	/**
	 * Gets the filePath.
	 * 
	 * @return Returns a String
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Sets the filePath.
	 * 
	 * @param filePath The filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * Gets the propertiesFilename.
	 * 
	 * @return Returns a String
	 */
	public String getPropertiesFileName() {
		return propertiesFileName;
	}

	/**
	 * Sets the propertiesFilename.
	 * 
	 * @param propertiesFilename The propertiesFilename to set
	 */
	public void setPropertiesFileName(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
	}

	/**
	 * Gets the filename.
	 * 
	 * @return Returns a String
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the filename.
	 * 
	 * @param filename The filename to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Gets the fileType.
	 * 
	 * @return Returns a String
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * Sets the fileType.
	 * 
	 * @param fileType The fileType to set
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * Gets the robotWebpage.
	 * 
	 * @return Returns a String
	 */
	public URL getWebpage() {
		return webpage;
	}

	/**
	 * Gets the fileLastModified.
	 * 
	 * @return Returns a String
	 */
	public long getFileLastModified() {
		return fileLastModified;
	}

	/**
	 * Sets the fileLastModified.
	 * 
	 * @param fileLastModified The fileLastModified to set
	 */
	public void setFileLastModified(long fileLastModified) {
		this.fileLastModified = fileLastModified;
	}

	/**
	 * Gets the fileLength.
	 * 
	 * @return Returns a String
	 */
	public long getFileLength() {
		return fileLength;
	}

	/**
	 * Sets the fileLength.
	 * 
	 * @param fileLength The fileLength to set
	 */
	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public void store(OutputStream out, String desc) throws IOException {
		props.store(out, desc);
	}

	protected void load(FileInputStream in) throws IOException {
		props.load(in);
		robocodeVersion = props.getProperty(ROBOCODE_VERSION);
		libraryDescription = props.getProperty(LIBRARY_DESCRIPTION);
	}

	public String getFullPackage() {
		return getNameManager().getFullPackage();
	}

	public String getFullClassNameWithVersion() {
		return getNameManager().getFullClassNameWithVersion();
	}

	public String getFullClassName() {
		return getNameManager().getFullClassName();
	}

	public boolean getValid() {
		return valid;
	}

	public boolean isDuplicate() {
		return duplicate;
	}

	public void setDuplicate(boolean isDuplicate) {
		this.duplicate = isDuplicate;
	}

	public NameManager getNameManager() {
		if (nameManager == null) {
			if (this instanceof RobotSpecification) {
				nameManager = new NameManager(name, version, developmentVersion, false);
			} else if (this instanceof TeamSpecification) {
				nameManager = new NameManager(name, version, developmentVersion, true);
			} else {
				throw new RuntimeException("Cannot get a nameManager for file type " + getFileType());
			}
		}
		return nameManager;
	}

	public boolean exists() {
		return new File(getFilePath()).exists();
	}

	/**
	 * Gets the rootDir.
	 * 
	 * @return Returns a File
	 */
	public File getRootDir() {
		return rootDir;
	}
}
