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
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Ported to Java 5
 *     - Code cleanup
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Vector;
import robocode.repository.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
@SuppressWarnings("serial")
public class AvailableRobotsPanel extends JPanel {

	private Vector<FileSpecification> availableRobots = new Vector<FileSpecification>(); 

	private Vector<FileSpecification> robotList = new Vector<FileSpecification>();

	private Vector<String> availablePackages = new Vector<String>();

	private JScrollPane availableRobotsScrollPane;

	private JList availableRobotsList;

	private JButton actionButton;

	private JList actionList;

	private JList availablePackagesList;

	private JScrollPane availablePackagesScrollPane;

	private RobotNameCellRenderer robotNamesCellRenderer;

	private RobotSelectionPanel robotSelectionPanel;

	private String title;

	private EventHandler eventHandler = new EventHandler();

	public AvailableRobotsPanel(JButton actionButton, String title, JList actionList,
			RobotSelectionPanel robotSelectionPanel) {
		super();
		this.title = title;
		this.actionButton = actionButton;
		this.actionList = actionList;
		this.robotSelectionPanel = robotSelectionPanel;
		initialize();
	}

	/**
	 * Return the Page property value.
	 * 
	 * @return JPanel
	 */
	private void initialize() {
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title));
		setLayout(new BorderLayout());

		JPanel top = new JPanel();

		top.setLayout(new GridLayout(1, 2));
		JPanel a = new JPanel();

		a.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Packages"));
		a.setLayout(new BorderLayout());
		a.add(getAvailablePackagesScrollPane());
		a.setPreferredSize(new Dimension(120, 100));
		top.add(a);
		JPanel b = new JPanel();

		b.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Robots"));
		b.setLayout(new BorderLayout());
		b.add(getAvailableRobotsScrollPane());
		b.setPreferredSize(new Dimension(120, 100));
		top.add(b);
		add(top, BorderLayout.CENTER);

		JLabel f5Label = new JLabel("Press F5 to refresh");

		f5Label.setHorizontalAlignment(JLabel.CENTER);
		add(f5Label, BorderLayout.SOUTH);
	}

	public Vector<FileSpecification> getAvailableRobots() {
		return availableRobots;
	}

	public Vector<FileSpecification> getRobotList() {
		return robotList;
	}

	public Vector<FileSpecification> getSelectedRobots() {
		int sel[] = getAvailableRobotsList().getSelectedIndices();

		Vector<FileSpecification> moves = new Vector<FileSpecification>();

		for (int i = 0; i < sel.length; i++) {
			moves.add(availableRobots.elementAt(sel[i]));
		}
		return moves;
	}

	/**
	 * Return the availableRobotsList.
	 * 
	 * @return JList
	 */
	private JList getAvailableRobotsList() {
		if (availableRobotsList == null) {
			availableRobotsList = new JList();
			availableRobotsList.setModel(new AvailableRobotsModel());
			availableRobotsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			robotNamesCellRenderer = new RobotNameCellRenderer();
			availableRobotsList.setCellRenderer(robotNamesCellRenderer);
			MouseListener mouseListener = new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					// This does not work in Linux under IBM JRE 1.3.0...
					if (e.getClickCount() >= 2) {
						if (e.getClickCount() % 2 == 0) {
							if (actionButton != null) {
								actionButton.doClick();
							}
						}
					}
				}
			};

			availableRobotsList.addMouseListener(mouseListener);
			availableRobotsList.addListSelectionListener(eventHandler);
		}
		return availableRobotsList;
	}

	/**
	 * Return the JScrollPane1 property value.
	 * 
	 * @return JScrollPane
	 */
	private JScrollPane getAvailableRobotsScrollPane() {
		if (availableRobotsScrollPane == null) {
			availableRobotsScrollPane = new JScrollPane();
			availableRobotsScrollPane.setViewportView(getAvailableRobotsList());
		}
		return availableRobotsScrollPane;
	}

	public void setRobotList(Vector<FileSpecification> robotListVector) { 
		AvailableRobotsPanel.this.robotList = robotListVector;
		SwingUtilities.invokeLater(
				new Runnable() {
			public void run() {
				availablePackages.clear();
				availableRobots.clear();

				if (AvailableRobotsPanel.this.robotList == null) {
					AvailableRobotsPanel.this.robotList = new Vector<FileSpecification>();
					availablePackages.add("One moment please...");
					((AvailablePackagesModel) getAvailablePackagesList().getModel()).changed();
					getAvailablePackagesList().clearSelection();
					((AvailableRobotsModel) getAvailableRobotsList().getModel()).changed();
				} else {
					availablePackages.add("(All)");
					String packageName = null;

					for (int i = 0; i < robotList.size(); i++) {
						FileSpecification robotSpecification = (FileSpecification) robotList.elementAt(i);

						if (packageName != null && robotSpecification.getFullPackage() != null
								&& !packageName.equals(robotSpecification.getFullPackage())) {
							packageName = robotSpecification.getFullPackage();
							if (!availablePackages.contains(robotSpecification.getFullPackage())) {
								availablePackages.add(robotSpecification.getFullPackage());
							}
						} else if (robotSpecification.getFullPackage() != null
								&& !robotSpecification.getFullPackage().equals(packageName)) {
							packageName = robotSpecification.getFullPackage();
							if (!availablePackages.contains(robotSpecification.getFullPackage())) {
								availablePackages.add(robotSpecification.getFullPackage());
							}
						}
					}
					availablePackages.add("(No package)");

					for (int i = 0; i < robotList.size(); i++) {
						availableRobots.add(robotList.elementAt(i));
					}
					((AvailablePackagesModel) getAvailablePackagesList().getModel()).changed();
					getAvailablePackagesList().setSelectedIndex(0);
					((AvailableRobotsModel) getAvailableRobotsList().getModel()).changed();
					getAvailablePackagesList().requestFocus();
				}
			}
		});
	}

	private void availablePackagesListSelectionChanged() {
		int sel[] = getAvailablePackagesList().getSelectedIndices();

		availableRobots.clear();
		if (sel.length == 1) {
			robotNamesCellRenderer.setUseShortNames(true);
			getAvailablePackagesList().scrollRectToVisible(getAvailablePackagesList().getCellBounds(sel[0], sel[0]));
		} else {
			robotNamesCellRenderer.setUseShortNames(false);
		}

		for (int i = 0; i < sel.length; i++) {
			String selectedPackage = availablePackages.elementAt(sel[i]);

			if (selectedPackage.equals("(All)")) {
				robotNamesCellRenderer.setUseShortNames(false);
				availableRobots.clear();
				for (int j = 0; j < robotList.size(); j++) {
					availableRobots.add(robotList.elementAt(j));
				}
				break;
			} // else single package.
			else {
				for (int j = 0; j < robotList.size(); j++) {
					FileSpecification robotSpecification = (FileSpecification) robotList.elementAt(j);

					if (robotSpecification.getFullPackage() == null) {
						if (selectedPackage.equals("(No package)")) {
							availableRobots.add(robotSpecification);
						}
					} else {
						if (robotSpecification.getFullPackage().equals(selectedPackage)) {
							availableRobots.add(robotSpecification);
						}
					}
				}
			}
		}
		((AvailableRobotsModel) getAvailableRobotsList().getModel()).changed();
		if (availableRobots.size() > 0) {
			availableRobotsList.setSelectedIndex(0);
			availableRobotsListSelectionChanged();
		}
	}

	private void availableRobotsListSelectionChanged() {
		int sel[] = getAvailableRobotsList().getSelectedIndices();

		if (sel.length == 1) {
			if (actionList != null) {
				actionList.clearSelection();
			}
			FileSpecification robotSpecification = (FileSpecification) getAvailableRobotsList().getModel().getElementAt(
					sel[0]);

			if (robotSelectionPanel != null) {
				robotSelectionPanel.showDescription(robotSpecification);
			}
		} else {
			if (robotSelectionPanel != null) {
				robotSelectionPanel.showDescription(null);
			}
		}
	}

	public void clearSelection() {
		getAvailableRobotsList().clearSelection();
		((AvailableRobotsModel) getAvailableRobotsList().getModel()).changed();
	}

	/**
	 * Return the availableRobotsList.
	 * 
	 * @return JList
	 */
	private JList getAvailablePackagesList() {
		if (availablePackagesList == null) {
			availablePackagesList = new JList();
			availablePackagesList.setModel(new AvailablePackagesModel());
			availablePackagesList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			availablePackagesList.addListSelectionListener(eventHandler);
		}
		return availablePackagesList;
	}

	/**
	 * Return the availablePackagesScrollPane
	 * 
	 * @return JScrollPane
	 */
	private JScrollPane getAvailablePackagesScrollPane() {
		if (availablePackagesScrollPane == null) {
			availablePackagesScrollPane = new JScrollPane();
			availablePackagesScrollPane.setViewportView(getAvailablePackagesList());
		}
		return availablePackagesScrollPane;
	}

	private class EventHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == true) {
				return;
			}
			if (e.getSource() == getAvailableRobotsList()) {
				availableRobotsListSelectionChanged();
			} else if (e.getSource() == getAvailablePackagesList()) {
				availablePackagesListSelectionChanged();
			}
		}
	}


	@SuppressWarnings("serial")
	private class AvailablePackagesModel extends AbstractListModel {
		public void changed() {
			fireContentsChanged(this, 0, getSize());
		}

		public int getSize() {
			return availablePackages.size();
		}

		public String getElementAt(int which) {
			return availablePackages.elementAt(which);
		}
	}


	@SuppressWarnings("serial")
	private class AvailableRobotsModel extends AbstractListModel {
		public void changed() {
			fireContentsChanged(this, 0, getSize());
		}

		public int getSize() {
			return availableRobots.size();
		}

		public Object getElementAt(int which) {
			try {
				return availableRobots.elementAt(which);
			} catch (ArrayIndexOutOfBoundsException e) {
				// If the view updates while we're updating...
				return "";
			}
		}
	}


	@SuppressWarnings("serial")
	private class RobotNameCellRenderer extends JLabel implements ListCellRenderer {
		private boolean useShortNames = false;

		public RobotNameCellRenderer() {
			setOpaque(true);
		}

		public void setUseShortNames(boolean useShortNames) {
			this.useShortNames = useShortNames;
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			setComponentOrientation(list.getComponentOrientation());

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			if (useShortNames && value instanceof FileSpecification) {
				FileSpecification fileSpecification = (FileSpecification) value;

				if (fileSpecification instanceof TeamSpecification) {
					setText("Team: " + fileSpecification.getNameManager().getUniqueShortClassNameWithVersion());
				} else {
					setText(fileSpecification.getNameManager().getUniqueShortClassNameWithVersion());
				}
			} else if (value instanceof FileSpecification) {
				FileSpecification fileSpecification = (FileSpecification) value;

				if (fileSpecification instanceof TeamSpecification) {
					setText("Team: " + fileSpecification.getNameManager().getUniqueFullClassNameWithVersion());
				} else {
					setText(fileSpecification.getNameManager().getUniqueFullClassNameWithVersion());
				}
			} else {
				setText(value.toString());
			}
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			return this;
		}
	}
}
