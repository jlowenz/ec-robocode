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
 *     Matthew Reeder
 *     - Added keyboard mnemonics to buttons
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
import javax.swing.border.*;
import java.util.*;

import robocode.repository.*;
import robocode.manager.RobotRepositoryManager;
import robocode.util.Utils;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder, Flemming N. Larsen  (current)
 */
@SuppressWarnings("serial")
public class RobotSelectionPanel extends WizardPanel {

	private AvailableRobotsPanel availableRobotsPanel;

	private JPanel selectedRobotsPanel;
	private JScrollPane selectedRobotsScrollPane;
	private JList selectedRobotsList;
	
	private JPanel buttonsPanel;
	private JPanel addButtonsPanel;
	private JPanel removeButtonsPanel;
	private JButton addButton;
	private JButton addAllButton;
	private JButton removeButton;
	private JButton removeAllButton;

	private EventHandler eventHandler = new EventHandler();
	private RobotDescriptionPanel descriptionPanel;
	private String instructions;
	private JLabel instructionsLabel;
	private JPanel mainPanel;
	private int maxRobots = 1;
	private int minRobots = 1;
	private JPanel numRoundsPanel;
	private JTextField numRoundsTextField;
	private boolean onlyShowSource;
	private boolean onlyShowWithPackage;
	private boolean onlyShowRobots;
	private boolean onlyShowDevelopment;
	private boolean onlyShowPackaged;
	private boolean ignoreTeamRobots;
	private String preSelectedRobots;
	private RobotNameCellRenderer robotNamesCellRenderer;
	private Vector<FileSpecification> selectedRobots = new Vector<FileSpecification>();
	private boolean showNumRoundsPanel;
	private RobotRepositoryManager robotManager;
	private boolean listBuilt;

	class EventHandler implements ActionListener, ListSelectionListener, HierarchyListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == RobotSelectionPanel.this.getAddAllButton()) {
				addAllButtonActionPerformed();
			} else if (e.getSource() == RobotSelectionPanel.this.getAddButton()) {
				addButtonActionPerformed();
			} else if (e.getSource() == RobotSelectionPanel.this.getRemoveAllButton()) {
				removeAllButtonActionPerformed();
			} else if (e.getSource() == RobotSelectionPanel.this.getRemoveButton()) {
				removeButtonActionPerformed();
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == true) {
				return;
			}
			if (e.getSource() == getSelectedRobotsList()) {
				selectedRobotsListSelectionChanged();
			}
		}

		public void hierarchyChanged(HierarchyEvent e) {
			if (!listBuilt && isShowing()) {
				// log("Building robot list.");
				listBuilt = true;
				buildRobotList();
			}
		}
	}

	private RobotSelectionPanel() {}

	/**
	 * NewBattleRobotsTab constructor comment.
	 */
	public RobotSelectionPanel(RobotRepositoryManager robotManager, int minRobots, int maxRobots,
			boolean showNumRoundsPanel, String instructions, boolean onlyShowSource, boolean onlyShowWithPackage,
			boolean onlyShowRobots, boolean onlyShowDevelopment, boolean onlyShowPackaged, boolean ignoreTeamRobots,
			String preSelectedRobots) {
		super();
		this.showNumRoundsPanel = showNumRoundsPanel;
		this.minRobots = minRobots;
		this.maxRobots = maxRobots;
		this.instructions = instructions;
		this.onlyShowSource = onlyShowSource;
		this.onlyShowWithPackage = onlyShowWithPackage;
		this.onlyShowRobots = onlyShowRobots;
		this.onlyShowDevelopment = onlyShowDevelopment;
		this.onlyShowPackaged = onlyShowPackaged;
		this.ignoreTeamRobots = ignoreTeamRobots;
		this.preSelectedRobots = preSelectedRobots;
		this.robotManager = robotManager;
		initialize();
		showInstructions();
	}

	private void addAllButtonActionPerformed() {
		JList selectedList = getSelectedRobotsList();
		SelectedRobotsModel selectedModel = (SelectedRobotsModel) selectedList.getModel();
		Vector<FileSpecification> availableRobots = availableRobotsPanel.getAvailableRobots();

		for (int i = 0; i < availableRobots.size(); i++) {
			selectedRobots.add(availableRobots.elementAt(i));
		}
		availableRobotsPanel.clearSelection();

		selectedList.clearSelection();
		selectedModel.changed();
		fireStateChanged();
		if (selectedModel.getSize() >= minRobots && selectedModel.getSize() <= maxRobots) {
			showInstructions();
		} else if (selectedModel.getSize() > maxRobots) {
			showWrongNumInstructions();
		}
	}

	private void addButtonActionPerformed() {
		SelectedRobotsModel selectedModel = (SelectedRobotsModel) getSelectedRobotsList().getModel();
		Vector<FileSpecification> moves = availableRobotsPanel.getSelectedRobots();

		for (int i = 0; i < moves.size(); i++) {
			selectedRobots.add(moves.elementAt(i));
		}
		availableRobotsPanel.clearSelection();
		selectedModel.changed();
		fireStateChanged();
		if (selectedModel.getSize() >= minRobots && selectedModel.getSize() <= maxRobots) {
			showInstructions();
		} else if (selectedModel.getSize() > maxRobots) {
			showWrongNumInstructions();
		}
	}

	/**
	 * Return the addAllButton
	 * 
	 * @return JButton
	 */
	private JButton getAddAllButton() {
		if (addAllButton == null) {
			addAllButton = new JButton();
			addAllButton.setText("Add All ->");
			addAllButton.setMnemonic('l');
			addAllButton.setDisplayedMnemonicIndex(5);
			addAllButton.addActionListener(eventHandler);
		}
		return addAllButton;
	}

	/**
	 * Return the addButton
	 * 
	 * @return JButton
	 */
	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setText("Add ->");
			addButton.setMnemonic('A');
			addButton.setDisplayedMnemonicIndex(0);
			addButton.addActionListener(eventHandler);
		}
		return addButton;
	}

	private JPanel getAddButtonsPanel() {
		if (addButtonsPanel == null) {
			addButtonsPanel = new JPanel();
			addButtonsPanel.setLayout(new GridLayout(2, 1));
			addButtonsPanel.add(getAddButton());
			addButtonsPanel.add(getAddAllButton());
		}
		return addButtonsPanel;
	}

	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = new JPanel();
			buttonsPanel.setLayout(new BorderLayout(5, 5));
			buttonsPanel.setBorder(BorderFactory.createEmptyBorder(21, 5, 5, 5));
			buttonsPanel.add(getAddButtonsPanel(), BorderLayout.NORTH);
			if (showNumRoundsPanel) {
				buttonsPanel.add(getNumRoundsPanel(), BorderLayout.CENTER);
			}
			buttonsPanel.add(getRemoveButtonsPanel(), BorderLayout.SOUTH);
		}
		return buttonsPanel;
	}

	/**
	 * Return the removeAllButton
	 * 
	 * @return JButton
	 */
	private JButton getRemoveAllButton() {
		if (removeAllButton == null) {
			removeAllButton = new JButton();
			removeAllButton.setText("<- Remove All");
			removeAllButton.setMnemonic('v');
			removeAllButton.setDisplayedMnemonicIndex(7);
			removeAllButton.addActionListener(eventHandler);
		}
		return removeAllButton;
	}

	/**
	 * Return the removeButton property value.
	 * 
	 * @return JButton
	 */
	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setText("<- Remove");
			removeButton.setMnemonic('m');
			removeButton.setDisplayedMnemonicIndex(5);
			removeButton.addActionListener(eventHandler);
		}
		return removeButton;
	}

	private JPanel getRemoveButtonsPanel() {
		if (removeButtonsPanel == null) {
			removeButtonsPanel = new JPanel();
			removeButtonsPanel.setLayout(new GridLayout(2, 1));
			removeButtonsPanel.add(getRemoveButton());
			removeButtonsPanel.add(getRemoveAllButton());
		}
		return removeButtonsPanel;
	}

	public String getSelectedRobotsAsString() {
		String s = "";

		for (int i = 0; i < selectedRobots.size(); i++) {
			if (i != 0) {
				s += ",";
			}
			s += selectedRobots.elementAt(i).getNameManager().getUniqueFullClassNameWithVersion();
		}
		return s;
	}

	public Vector<FileSpecification> getSelectedRobots() {
		return selectedRobots;
	}

	/**
	 * Return the selectedRobotsList.
	 * 
	 * @return JList
	 */
	private JList getSelectedRobotsList() {
		if (selectedRobotsList == null) {
			selectedRobotsList = new JList();
			selectedRobotsList.setModel(new SelectedRobotsModel());
			selectedRobotsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			robotNamesCellRenderer = new RobotNameCellRenderer();
			selectedRobotsList.setCellRenderer(robotNamesCellRenderer);
			MouseListener mouseListener = new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						removeButtonActionPerformed();
					}
					if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
						contextMenuActionPerformed();
					}
				}
			};

			selectedRobotsList.addMouseListener(mouseListener);
			selectedRobotsList.addListSelectionListener(eventHandler);
		}
		return selectedRobotsList;
	}

	private JPanel getSelectedRobotsPanel() {
		if (selectedRobotsPanel == null) {
			selectedRobotsPanel = new JPanel();
			selectedRobotsPanel.setLayout(new BorderLayout());
			selectedRobotsPanel.setPreferredSize(new Dimension(120, 100));
			selectedRobotsPanel.setBorder(
					BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Selected Robots"));
			selectedRobotsPanel.add(getSelectedRobotsScrollPane(), BorderLayout.CENTER);
		}
		return selectedRobotsPanel;
	}

	/**
	 * Return the selectedRobotsScrollPane property value.
	 * 
	 * @return JScrollPane
	 */
	private JScrollPane getSelectedRobotsScrollPane() {
		if (selectedRobotsScrollPane == null) {
			selectedRobotsScrollPane = new JScrollPane();
			selectedRobotsScrollPane.setViewportView(getSelectedRobotsList());
		}
		return selectedRobotsScrollPane;
	}

	/**
	 * Return the Page property value.
	 * 
	 * @return JPanel
	 */
	private void initialize() {
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new BorderLayout());
		add(getInstructionsLabel(), BorderLayout.NORTH);
		add(getMainPanel(), BorderLayout.CENTER);
		add(getDescriptionPanel(), BorderLayout.SOUTH);
		this.addHierarchyListener(eventHandler);
		this.setVisible(true);
	}

	private void removeAllButtonActionPerformed() {
		JList selectedList = getSelectedRobotsList();
		SelectedRobotsModel selectedModel = (SelectedRobotsModel) selectedList.getModel();

		selectedRobots.clear();
		selectedList.clearSelection();
		selectedModel.changed();
		fireStateChanged();
		showInstructions();
	}

	private void contextMenuActionPerformed() {}

	private void removeButtonActionPerformed() {
		JList selectedList = getSelectedRobotsList();
		SelectedRobotsModel selectedModel = (SelectedRobotsModel) selectedList.getModel();
		int sel[] = selectedList.getSelectedIndices();

		for (int i = 0; i < sel.length; i++) {
			selectedRobots.remove(sel[i] - i);
		}
		selectedList.clearSelection();
		selectedModel.changed();
		fireStateChanged();
		if (selectedModel.getSize() < minRobots || selectedModel.getSize() > maxRobots) {
			showWrongNumInstructions();
		} else {
			showInstructions();
		}
	}

	@SuppressWarnings("serial")
	class RobotNameCellRenderer extends JLabel implements ListCellRenderer {
		private boolean useShortNames;

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
				FileSpecification robotSpecification = (FileSpecification) value;

				if (value instanceof TeamSpecification) {
					setText("Team: " + robotSpecification.getNameManager().getUniqueShortClassNameWithVersion());
				} else {
					setText(robotSpecification.getNameManager().getUniqueShortClassNameWithVersion());
				}
			} else if (value instanceof FileSpecification) {
				FileSpecification robotSpecification = (FileSpecification) value;

				if (value instanceof TeamSpecification) {
					setText("Team: " + robotSpecification.getNameManager().getUniqueFullClassNameWithVersion());
				} else {
					setText(robotSpecification.getNameManager().getUniqueFullClassNameWithVersion());
				}
			} else {
				setText("??" + value.toString());
			}

			setEnabled(list.isEnabled());
			setFont(list.getFont());

			return this;
		}
	}


	class SelectedRobotsModel extends AbstractListModel {
		public void changed() {
			fireContentsChanged(this, 0, getSize());
		}

		public int getSize() {
			return selectedRobots.size();
		}

		public Object getElementAt(int which) {
			return selectedRobots.elementAt(which);
		}
	}

	public void buildRobotList() {
		new Thread(new Runnable() {
			public void run() {
				getAvailableRobotsPanel().setRobotList(null);
				Vector<FileSpecification> v = RobotSelectionPanel.this.robotManager.getRobotRepository().getRobotSpecificationsVector(onlyShowSource, onlyShowWithPackage, onlyShowRobots, onlyShowDevelopment, onlyShowPackaged, ignoreTeamRobots); 

				getAvailableRobotsPanel().setRobotList(v);
				if (selectedRobots != null && !selectedRobots.equals("")) {
					setSelectedRobots(getAvailableRobotsPanel().getRobotList(), preSelectedRobots);
					preSelectedRobots = null;
				}
			}
		}).start();
	}

	public AvailableRobotsPanel getAvailableRobotsPanel() {
		if (availableRobotsPanel == null) {
			availableRobotsPanel = new AvailableRobotsPanel(getAddButton(), "Available Robots", getSelectedRobotsList(),
					this);
		}
		return availableRobotsPanel;
	}

	private RobotDescriptionPanel getDescriptionPanel() {
		if (descriptionPanel == null) {
			descriptionPanel = new RobotDescriptionPanel();
			descriptionPanel.setBorder(BorderFactory.createEmptyBorder(1, 10, 1, 10));
		}
		return descriptionPanel;
	}

	private JLabel getInstructionsLabel() {
		if (instructionsLabel == null) {
			instructionsLabel = new JLabel();
			if (instructions != null) {
				instructionsLabel.setText(instructions);
			}
		}
		return instructionsLabel;
	}

	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setPreferredSize(new Dimension(550, 300));
			GridBagLayout layout = new GridBagLayout();

			mainPanel.setLayout(layout);

			GridBagConstraints constraints = new GridBagConstraints();

			constraints.fill = GridBagConstraints.BOTH;
			constraints.weightx = 2;
			constraints.weighty = 1;
			constraints.anchor = GridBagConstraints.NORTHWEST;
			constraints.gridwidth = 2;
			layout.setConstraints(getAvailableRobotsPanel(), constraints);
			mainPanel.add(getAvailableRobotsPanel());
			constraints.gridwidth = 1;
			constraints.weightx = 0;
			constraints.weighty = 0;
			constraints.anchor = GridBagConstraints.CENTER;
			layout.setConstraints(getButtonsPanel(), constraints);
			mainPanel.add(getButtonsPanel());
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			constraints.weightx = 1;
			constraints.weighty = 1;
			constraints.anchor = GridBagConstraints.NORTHWEST;
			layout.setConstraints(getSelectedRobotsPanel(), constraints);
			mainPanel.add(getSelectedRobotsPanel());
		}
		return mainPanel;
	}

	public int getNumRounds() {
		try {
			return Integer.parseInt(getNumRoundsTextField().getText());
		} catch (NumberFormatException e) {
			return 10;
		}
	}

	private JPanel getNumRoundsPanel() {
		if (numRoundsPanel == null) {
			numRoundsPanel = new JPanel();
			numRoundsPanel.setLayout(new BoxLayout(numRoundsPanel, BoxLayout.Y_AXIS));
			numRoundsPanel.setBorder(BorderFactory.createEmptyBorder());
			numRoundsPanel.add(new JPanel());
			JPanel j = new JPanel();

			j.setLayout(new BoxLayout(j, BoxLayout.Y_AXIS));
			TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					"Number of Rounds");

			j.setBorder(border);
			j.add(getNumRoundsTextField());
			j.setPreferredSize(new Dimension(border.getMinimumSize(j).width, j.getPreferredSize().height));
			j.setMinimumSize(j.getPreferredSize());
			j.setMaximumSize(j.getPreferredSize());
			numRoundsPanel.add(j);
			numRoundsPanel.add(new JPanel());
		}
		return numRoundsPanel;
	}

	/**
	 * Return the numRoundsTextField
	 * 
	 * @return JTextField
	 */
	private JTextField getNumRoundsTextField() {
		if (numRoundsTextField == null) {
			numRoundsTextField = new JTextField();
			numRoundsTextField.setAutoscrolls(false);
			// Center in panel
			numRoundsTextField.setAlignmentX((float) .5);
			// Center text in textfield
			numRoundsTextField.setHorizontalAlignment(JTextField.CENTER);
		}
		return numRoundsTextField;
	}

	public int getSelectedRobotsCount() {
		return selectedRobots.size();
	}

	public boolean isReady() {
		return (getSelectedRobotsCount() >= minRobots && getSelectedRobotsCount() <= maxRobots);
	}

	public void refreshRobotList() {
		getAvailableRobotsPanel().setRobotList(null);
		RobotSelectionPanel.this.robotManager.clearRobotList();
		buildRobotList();
	}

	private void selectedRobotsListSelectionChanged() {
		int sel[] = getSelectedRobotsList().getSelectedIndices();

		if (sel.length == 1) {
			availableRobotsPanel.clearSelection();
			FileSpecification robotSpecification = (FileSpecification) getSelectedRobotsList().getModel().getElementAt(
					sel[0]);

			showDescription(robotSpecification);
		} else {
			showDescription(null);
		}
	}

	public void setNumRounds(int numRounds) {
		getNumRoundsTextField().setText("" + numRounds);
	}

	private void setSelectedRobots(Vector<FileSpecification> robotList, String selectedRobotsString) { 
		if (selectedRobotsString != null) {
			StringTokenizer tokenizer;

			tokenizer = new StringTokenizer(selectedRobotsString, ",");
			if (robotList == null) {
				Utils.log(new RuntimeException("Cannot add robots to a null robots list!"));
				return;
			}
			this.selectedRobots.clear();
			while (tokenizer.hasMoreTokens()) {
				String bot = tokenizer.nextToken();

				for (int i = 0; i < robotList.size(); i++) {
					if (((FileSpecification) robotList.elementAt(i)).getNameManager().getUniqueFullClassNameWithVersion().equals(
							bot)) {
						this.selectedRobots.add(robotList.elementAt(i));
						break;
					}
				}
			}
		}
		((SelectedRobotsModel) getSelectedRobotsList().getModel()).changed();
		fireStateChanged();
	}

	public void showDescription(FileSpecification robotSpecification) {
		getDescriptionPanel().showDescription(robotSpecification);
	}

	public void showInstructions() {
		if (instructions != null) {
			instructionsLabel.setText(instructions);
			instructionsLabel.setVisible(true);
		} else {
			instructionsLabel.setVisible(false);
		}
	}

	public void showWrongNumInstructions() {
		if (minRobots == maxRobots) {
			if (minRobots == 1) {
				instructionsLabel.setText("Please select exactly 1 robot.");
			} else {
				instructionsLabel.setText("Please select exactly " + minRobots + " robots.");
			}
		} else {
			instructionsLabel.setText("Please select between " + minRobots + " and " + maxRobots + " robots.");
		}
		instructionsLabel.setVisible(true);
	}
}
