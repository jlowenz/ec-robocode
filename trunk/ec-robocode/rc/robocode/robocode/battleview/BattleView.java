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
 *     - Modified to use BufferStrategy instead of SwingUtilities, hence this
 *       class is now extending Canvas instead of JPanel.
 *     - Integration of robocode.render.
 *     - Removed all code for handling dirty rectangles, which is not necessary
 *       anymore due to the new robocode.render.
 *     - Added drawRobotPaint() to support for painting the robots using their
 *       Robot.onPaint() method
 *       the painting.
 *     - Added the ability to enable and disable drawing the ground and
 *       explosions
 *     - Added support for Robocode SG painting
 *     - Added rendering hints
 *     - Bullets are now painted as filled circles with a size that matches the
 *       bullet energy. Explosions from bullet hits are also sized based on the
 *       bullet energy.
 *     - Bullets and scan arcs are now painted using the robot's bullet and scan
 *       color
 *     - Added the "Display TPS in titlebar" option
 *     - Renders the Robocode losing the RobocodeLogo class
 *     - Removed setBattlePaused
 *     - Code cleanup
 *******************************************************************************/
package robocode.battleview;


import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

import robocode.dialog.RobocodeFrame;
import robocode.peer.*;
import robocode.battlefield.*;
import robocode.battle.*;
import robocode.util.*;
import robocode.manager.*;
import robocode.render.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
@SuppressWarnings("serial")
public class BattleView extends Canvas {
	public static final int PAINTROBOCODELOGO = 0;
	public static final int PAINTBATTLE = 1;

	private final static Color CANVAS_BG_COLOR = SystemColor.controlDkShadow;
	
	private final static Area BULLET_AREA = new Area(new Ellipse2D.Double(-0.5, -0.5, 1, 1));

	private final static int ROBOT_TEXT_Y_OFFSET = 24; 
	
	private RobocodeFrame robocodeFrame;

	// The battle and battlefield, 
	private Battle battle;
	private BattleField battleField;

	private boolean initialized;
	private double scale = 1.0;
	private int paintMode = PAINTROBOCODELOGO;
	
	// Ground
	private int[][] groundTiles;

	private int groundTileWidth = 64;
	private int groundTileHeight = 64;
	
	private Image groundImage;

	// Draw option related things	
	private boolean drawRobotName;
	private boolean drawRobotEnergy;
	private boolean drawScanArcs;
	private boolean drawExplosions;
	private boolean drawGround;

	private int noBuffers;
	
	private RenderingHints renderingHints;

	// TPS and FPS
	private boolean displayTPS;
	private boolean displayFPS;

	// Fonts and the like
	private Font smallFont;
	private FontMetrics smallFontMetrics;
	
	private ImageManager imageManager;

	private RobocodeManager manager;

	private BufferStrategy bufferStrategy;

	private Image offscreenImage;
	private Graphics2D offscreenGfx;

	private GeneralPath robocodeTextPath = new RobocodeLogo().getRobocodeText();
	
	private static MirroredGraphics mirroredGraphics = new MirroredGraphics();

	private GraphicsState graphicsState = new GraphicsState();
	
	/**
	 * BattleView constructor.
	 */
	public BattleView(RobocodeManager manager, RobocodeFrame robocodeFrame, ImageManager imageManager) {
		super();

		this.manager = manager;
		this.robocodeFrame = robocodeFrame;
		this.imageManager = imageManager;
		this.manager = manager;
	}

	/**
	 * Shows the next frame. The game calls this every frame.
	 */
	public void update() {
		try {
			if (robocodeFrame.isIconified() || (getWidth() <= 0) || (getHeight() <= 0)) {
				return;
			}

			if (!initialized) {
				initialize();
			}

			if (offscreenImage != null) {
				offscreenGfx = (Graphics2D) offscreenImage.getGraphics();
				offscreenGfx.setRenderingHints(renderingHints);

				paintBattle(offscreenGfx);

				if (bufferStrategy != null) {
					Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
	
					if (g != null) {
						g.drawImage(offscreenImage, 0, 0, null);
	
						// Flush the buffer to the main graphics
						if (getGraphics() != null) {
							// FNL: The above check to prevents internal NullPointerException in
							// Component.BltBufferStrategy.show()	
							bufferStrategy.show();
						}
	
						g.dispose();
					}
				}
			}
		} catch (Exception e) {
			Utils.log("Could not draw: " + e);
			e.printStackTrace(System.err);
		}
	}

	public void paint(Graphics g) {
		switch (paintMode) {
		case PAINTROBOCODELOGO:
			paintRobocodeLogo((Graphics2D) g);
			return;

		case PAINTBATTLE: {
			update();
			break;
		}
		}
	}

	public int getPaintMode() {
		return paintMode;
	}

	public void setDisplayOptions() {
		RobocodeProperties props = manager.getProperties();
		
		displayTPS = props.getOptionsViewTPS();
		displayFPS = props.getOptionsViewFPS();
		drawRobotName = props.getOptionsViewRobotNames();
		drawRobotEnergy = props.getOptionsViewRobotEnergy();
		drawScanArcs = props.getOptionsViewScanArcs();
		drawGround = props.getOptionsViewGround();
		drawExplosions = props.getOptionsViewExplosions();

		noBuffers = props.getOptionsRenderingNoBuffers();

		renderingHints = props.getRenderingHints();
	}

	private void initialize() {
		if (offscreenImage != null) {
			offscreenImage.flush();
			offscreenImage = null;
		}

		offscreenImage = getGraphicsConfiguration().createCompatibleImage(getWidth(), getHeight());
		offscreenGfx = (Graphics2D) offscreenImage.getGraphics();

		if (bufferStrategy == null) {
			createBufferStrategy(noBuffers);
			bufferStrategy = getBufferStrategy();
		}

		// If we are scaled...
		if (getWidth() < battleField.getWidth() || getHeight() < battleField.getHeight()) {
			// Use the smaller scale.
			// Actually we don't need this, since
			// the RobocodeFrame keeps our aspect ratio intact.
			
			scale = Math.min((double) getWidth() / battleField.getWidth(),
					(double) getHeight() / battleField.getHeight());

			offscreenGfx.scale(scale, scale);
		} else {
			scale = 1;
		}

		// Scale font
		smallFont = new Font("Dialog", Font.PLAIN, (int) (10 / scale));
		smallFontMetrics = offscreenGfx.getFontMetrics(smallFont);

		// Initialize ground image
		if (drawGround) {
			createGroundImage();
		} else {
			groundImage = null;
		}

		initialized = true;
	}

	private void createGroundImage() {
		// Reinitialize ground tiles

		final int NUM_HORZ_TILES = battleField.getWidth() / groundTileWidth + 1;
		final int NUM_VERT_TILES = battleField.getHeight() / groundTileHeight + 1;

		if ((groundTiles == null) || (groundTiles.length != NUM_VERT_TILES) || (groundTiles[0].length != NUM_HORZ_TILES)) {

			groundTiles = new int[NUM_VERT_TILES][NUM_HORZ_TILES];
			for (int y = NUM_VERT_TILES - 1; y >= 0; y--) {
				for (int x = NUM_HORZ_TILES - 1; x >= 0; x--) {
					groundTiles[y][x] = (int) Math.round(Math.random() * 4);
				}
			}
		}

		// Create new buffered image with the ground pre-rendered

		int groundWidth = (int) (battleField.getWidth() * scale);
		int groundHeight = (int) (battleField.getHeight() * scale);
	
		groundImage = new BufferedImage(groundWidth, groundHeight, BufferedImage.TYPE_INT_RGB);

		Graphics2D groundGfx = (Graphics2D) groundImage.getGraphics();

		groundGfx.setRenderingHints(renderingHints);

		groundGfx.setTransform(AffineTransform.getScaleInstance(scale, scale));
	
		for (int y = NUM_VERT_TILES - 1; y >= 0; y--) {
			for (int x = NUM_HORZ_TILES - 1; x >= 0; x--) {
				Image img = imageManager.getGroundTileImage(groundTiles[y][x]);

				if (img != null) {
					groundGfx.drawImage(img, x * groundTileWidth, y * groundTileHeight, null);
				}
			}
		}
	}

	public void paintBattle(Graphics2D g) {
		// Save the graphics state
		graphicsState.save(g);

		// Reset transform
		g.setTransform(new AffineTransform());

		// Clear canvas
		g.setColor(CANVAS_BG_COLOR);
		g.fillRect(0, 0, getWidth(), getHeight());

		// Calculate border space
		double dx = (getWidth() - scale * battleField.getWidth()) / 2;
		double dy = (getHeight() - scale * battleField.getHeight()) / 2;

		// Scale and translate the graphics
		AffineTransform at = AffineTransform.getTranslateInstance(dx, dy);

		at.concatenate(AffineTransform.getScaleInstance(scale, scale));
		g.setTransform(at);

		// Set the clip rectangle
		g.setClip(0, 0, battleField.getWidth(), battleField.getHeight());

		// Draw ground
		drawGround(g);

		// Draw battlefield objects
		// drawObjects(g);

		// Draw scan arcs
		drawScanArcs(g);

		// Draw robots
		drawRobots(g);

		// Draw all bullets
		drawBullets(g);

		// Draw all text
		drawText(g);

		// Restore the graphics state
		graphicsState.restore(g);
	}

	private void drawGround(Graphics2D g) {
		if (!drawGround) {
			// Ground should not be drawn
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, battleField.getWidth(), battleField.getHeight());
		} else {
			// Create pre-rendered ground image if it is not available
			if (groundImage == null) {
				createGroundImage();
			}

			// Draw the pre-rendered ground if it is available
			if (groundImage != null) {
				int groundWidth = (int) (battleField.getWidth() * scale);
				int groundHeight = (int) (battleField.getHeight() * scale);

				int dx = (getWidth() - groundWidth) / 2;
				int dy = (getHeight() - groundHeight) / 2;

				AffineTransform savedTx = g.getTransform();

				g.setTransform(new AffineTransform());

				g.drawImage(groundImage, dx, dy, groundWidth, groundHeight, null);

				g.setTransform(savedTx);
			}
		}
	}

	private void drawScanArcs(Graphics2D g) {
		if (drawScanArcs) {
			for (RobotPeer r : battle.getRobots()) {
				if (!r.isDead()) {
					drawScanArc((Graphics2D) g, r); 
				}
			}
		}
	}

	private void drawRobots(Graphics2D g) {
		double x, y;
		AffineTransform at;
		int battleFieldHeight = battle.getBattleField().getHeight();

		if (drawGround) {
			RenderImage explodeDebrise = imageManager.getExplosionDebriseRenderImage();

			for (RobotPeer r : battle.getRobots()) {
				if (r.isDead()) {
					x = r.getX();
					y = battleFieldHeight - r.getY();

					at = AffineTransform.getTranslateInstance(x, y);

					explodeDebrise.setTransform(at);
					explodeDebrise.paint(g);
				}
			}
		}

		for (RobotPeer r : battle.getRobots()) {
			if (!r.isDead()) {
				x = r.getX();
				y = battleFieldHeight - r.getY();

				at = AffineTransform.getTranslateInstance(x, y);
				at.rotate(r.getHeading());

				RenderImage robotRenderImage = imageManager.getColoredBodyRenderImage(r.getBodyColor());
	
				robotRenderImage.setTransform(at);
				robotRenderImage.paint(g);
	
				at = AffineTransform.getTranslateInstance(x, y);
				at.rotate(r.getGunHeading());
	
				RenderImage gunRenderImage = imageManager.getColoredGunRenderImage(r.getGunColor());
	
				gunRenderImage.setTransform(at);
				gunRenderImage.paint(g);
	
				if (!r.isDroid()) {
					at = AffineTransform.getTranslateInstance(x, y);
					at.rotate(r.getRadarHeading());
	
					RenderImage radarRenderImage = imageManager.getColoredRadarRenderImage(r.getRadarColor());
	
					radarRenderImage.setTransform(at);
					radarRenderImage.paint(g);
				}
			}
		}
	}

	private void drawText(Graphics2D g) {
		RobotPeer c;

		for (int i = 0; i < battle.getRobots().size(); i++) {
			c = (RobotPeer) battle.getRobots().elementAt(i);
			if (c.isDead()) {
				continue;
			}
			int x = (int) c.getX();
			int y = battle.getBattleField().getHeight() - (int) c.getY();

			if (drawRobotEnergy && c.getRobot() != null) {
				g.setColor(Color.white);
				int ll = (int) c.getEnergy();
				int rl = (int) ((c.getEnergy() - ll + .001) * 10.0);

				if (rl == 10) {
					rl = 9;
				}
				String energyString = ll + "." + rl;

				if (c.getEnergy() == 0 && !c.isDead()) {
					energyString = "Disabled";
				}
				centerString(g, energyString, x, y - ROBOT_TEXT_Y_OFFSET - smallFontMetrics.getHeight() / 2, smallFont,
						smallFontMetrics);
			}
			if (drawRobotName) {
				if (!drawRobotEnergy) {
					g.setColor(Color.white);
				}
				centerString(g, c.getVeryShortName(), x, y + ROBOT_TEXT_Y_OFFSET + smallFontMetrics.getHeight() / 2,
						smallFont, smallFontMetrics);
			}
			if (c.isPaintEnabled() && c.getRobot() != null) {
				drawRobotPaint(g, c);
			}
			if (c.getSayTextPeer() != null) {
				if (c.getSayTextPeer().getText() != null) {
					g.setColor(c.getSayTextPeer().getColor());
					centerString(g, c.getSayTextPeer().getText(), c.getSayTextPeer().getX(),
							battle.getBattleField().getHeight() - c.getSayTextPeer().getY(), smallFont, smallFontMetrics);
				}
			}
		}
	}

	private void drawRobotPaint(Graphics2D g, RobotPeer robotPeer) {
		// Do the painting
		if (robotPeer.isSGPaintEnabled()) {
			robotPeer.getRobot().onPaint(g);
		} else {
			mirroredGraphics.bind(g, battleField.getHeight());
			robotPeer.getRobot().onPaint(mirroredGraphics);
			mirroredGraphics.release();
		}
	}
	
	private void drawBullets(Graphics2D g) {
		BulletPeer bullet;

		double x, y;

		for (int i = 0; i < battle.getBullets().size(); i++) {
			bullet = (BulletPeer) battle.getBullets().elementAt(i);
			if (!bullet.isActive() && !bullet.hasHitVictim && !bullet.hasHitBullet) {
				continue;
			}

			x = bullet.getX();
			y = battle.getBattleField().getHeight() - bullet.getY();

			AffineTransform at = AffineTransform.getTranslateInstance(x, y);

			if (!bullet.hasHitVictim && !bullet.hasHitBullet) {

				// radius = sqrt(0.5^2 / 0.1 * power), where 0.5 is half pixel width and 0.1 is min. power
				double scale = 2 * Math.sqrt(2.5 * bullet.getPower());

				at.scale(scale, scale);
				Area bulletArea = BULLET_AREA.createTransformedArea(at);

				Color bulletColor = bullet.getOwner().getBulletColor();

				if (bulletColor == null) {
					bulletColor = Color.WHITE;
				}
				g.setColor(bulletColor);
				g.fill(bulletArea);

			} else if (drawExplosions) {	
				if (!(bullet instanceof ExplosionPeer)) {
					double scale = Math.sqrt(1000 * bullet.getPower()) / 128;

					at.scale(scale, scale);
				}

				RenderImage explosionRenderImage = imageManager.getExplosionRenderImage(bullet.getWhichExplosion(),
						bullet.getFrame());
				
				explosionRenderImage.setTransform(at);
				explosionRenderImage.paint(g);
			}
		}
	}

	public void setBattle(Battle battle) {
		this.battle = battle;
	}

	public void setPaintMode(int newPaintMode) {
		paintMode = newPaintMode;
	}

	private void centerString(Graphics2D g, String s, int x, int y, Font font, FontMetrics fm) {
		g.setFont(font);
		int left, top, descent;
		int width, height;

		width = fm.stringWidth(s);
		height = fm.getHeight();
		descent = fm.getDescent();

		left = x - width / 2;
		top = y - height / 2;

		if (left + width > battleField.getWidth()) {
			left = battleField.getWidth() - width;
		}
		if (top + height >= battleField.getHeight()) {
			top = battleField.getHeight() - height - 1;
		}
		if (left < 0) {
			left = 0;
		}
		if (top < 0) {
			top = 0;
		}
		g.drawString(s, left, (top + height - descent));
	}

	public void saveFrame(String fileName) {
		try {
			java.io.FileOutputStream o = new java.io.FileOutputStream(fileName);
			com.sun.image.codec.jpeg.JPEGImageEncoder encoder = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(o);

			encoder.encode((BufferedImage) offscreenImage);
			o.close();
		} catch (Exception e) {
			Utils.log("Could not save frame: " + e);
		}
	}

	public void setTitle(String s) {
		if (robocodeFrame != null) {
			robocodeFrame.setTitle(s);
		}
	}

	private Rectangle drawScanArc(Graphics2D g, RobotPeer robot) {
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) .2));

		Arc2D.Double scanArc = (Arc2D.Double) robot.getScanArc().clone();
		double aStart = scanArc.getAngleStart();
		double aExtent = scanArc.getAngleExtent();

		aStart = 360 - aStart;
		aStart -= aExtent;
		if (aStart < 0) {
			aStart += 360;
		}
		scanArc.setAngleStart(aStart);
		scanArc.y = battle.getBattleField().getHeight() - robot.getY() - robocode.Rules.RADAR_SCAN_RADIUS;

		Color scanColor = robot.getScanColor();

		if (scanColor == null) {
			scanColor = Color.BLUE;
		}
		g.setColor(scanColor);

		if (aExtent >= .5) {
			g.fill(scanArc);
		} else {
			g.draw(scanArc);
		}
		g.setComposite(AlphaComposite.SrcOver);
		return scanArc.getBounds();
	}

	public void setBattleField(BattleField battleField) {
		this.battleField = battleField;
	}

	/**
	 * Draws the Robocode logo
	 */
	private void paintRobocodeLogo(Graphics2D g) {
		setBackground(Color.BLACK);
		g.clearRect(0, 0, getWidth(), getHeight());

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.transform(AffineTransform.getTranslateInstance((getWidth() - 320) / 2, (getHeight() - 46) / 2));
		g.setColor(Color.DARK_GRAY);
		g.fill(robocodeTextPath);
	}

	public boolean isDisplayTPS() {
		return displayTPS;
	}

	public boolean isDisplayFPS() {
		return displayFPS;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
}
