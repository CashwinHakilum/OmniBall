import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import java.io.*;
import javax.sound.sampled.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.jbox2d.collision.*;
import org.jbox2d.collision.shapes.*;

import org.jbox2d.common.*; 

import org.jbox2d.dynamics.*;

/**
 * 
 * @author Ashwin B. Chakilum
 * A Freshman at UIUC
 * Majoring in Computer Science
 * ^_^
 */

public class Main extends JPanel implements ActionListener, KeyListener, MouseListener, MouseMotionListener
{	
	
	public static void main(String args[])
	{		
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			System.out.println("lol not good.");
		}
		
		ArrayList<String> resolutions = new ArrayList<String>();
		Dimension maxScreenSize = Toolkit.getDefaultToolkit().getScreenSize();

		resolutions.add("640x480 (4:3)");
		resolutions.add("800x600 (4:3)");
		resolutions.add("1024x768 (4:3)");
		resolutions.add("1024x576 (16:9)");
		resolutions.add("1152x648 (16:9)");
		resolutions.add("1280x600 (16:9)");
		resolutions.add("1280x720 (16:9)");
		resolutions.add("1360x768 (16:9)");
		resolutions.add("1366x768 (16:9)");
		resolutions.add("1600x900 (16:9)");
		resolutions.add("1920x1080 (16:9)");
		
		int width = maxScreenSize.width;
		int height = maxScreenSize.height;
		
		for (Iterator<String> it = resolutions.iterator(); it.hasNext();)
		{
			try
			{
				String resolution = it.next();
				
				int xIndex = resolution.indexOf("x");
				int spaceIndex = resolution.indexOf(" ");
				
				int tempWidth = Integer.parseInt(resolution.substring(0, xIndex));
				int tempHeight = Integer.parseInt(resolution.substring(xIndex + 1, spaceIndex));
				
				if (width < tempWidth || height < tempHeight)
				{
					it.remove();
				}
			} catch (Exception e)
			{
				
			}

		}
		
		resolutions.set(resolutions.size() - 1, maxScreenSize.width + "x" + maxScreenSize.height + " [FULL SCREEN]");
	
		try
		{
			Object chosenRes = JOptionPane.showInputDialog(null,
	                "Choose your desired resolution",
	                "Omniball [ALPHA]",
	                JOptionPane.PLAIN_MESSAGE,
	                null,
	                resolutions.toArray(),
	                resolutions.toArray()[resolutions.size() - 1]);
			
			if (chosenRes == null)
			{
				System.exit(0);
			}
			
			String res = (String) chosenRes;
			
			int xIndex = res.indexOf("x");
			int spaceIndex = res.indexOf(" ");
			
			width = Integer.parseInt(res.substring(0, xIndex));
			height = Integer.parseInt(res.substring(xIndex + 1, spaceIndex));
			
			
		} catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		
		

		BufferedImage transCursor = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(transCursor, new Point(0, 0), "hidden cursor");

		
		JFrame frame = new JFrame("Omniball [ALPHA]");
		frame.setBackground(Color.RED);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setIconImage(new ImageIcon("src/img/icon.jpg").getImage());
        frame.add(new Main());
        frame.setCursor(cursor);
    
        frame.setSize(width, height);
        
        if (width < maxScreenSize.width || height < maxScreenSize.height)
        {
        	frame.setUndecorated(false);
        }
        else
        {
            frame.setUndecorated(true);
        }
        
        frame.setVisible(true);
	}
	
	private int spawnIters;
	private PolygonDef spawnDef;
	private BodyDef spawnBodyDef;
	private Body tempSpawn;
	private ArrayList<Body> spawns = new ArrayList<Body>();
	
	private IntroScreen is;


	private World world;
	private AABB worldBounds;

	private Body platform1;
	private Body platform2;
	private Body platform3;
	private Body platform4;
	private Body platform5;
	private Body platform6;
	private Body wall1;
	private Body wall2;
	private Body wall3;
	private Body wall4;
	
	private ArrayList<Body> platform;
	private ArrayList<Body> walls;
	
	private CircleDef wheelDef; // Define shape properties itself
	private BodyDef wheelBodyDef; // Define location in world + interaction
	private Body wheel;
	private int radius;
	private double angle;
	private float wheelX;
	private float wheelY;
	private float smoothSpeedX;
	private float smoothSpeedY;
	private float speed;
	private int jumpRecharge = 3;
	private boolean inAir = true;
	private Color wheelColor;
	private int oscillation = 1;
	private int lightness = 1;
	private int wheelHP = 100;
	private int wheelMaxHP = 100;
	private int recoveryItersHP = 0;
	private int wheelSP = 100;
	private int wheelMaxSP = 100;
	private int recoveryItersSP = 0;
	private int fireDelay = 5;

	private int flickerIters;
	
	private CircleDef bulletDef;
	private BodyDef bulletBodyDef;
	private ArrayList<Body> bullets;
	private int nBullets;
	private final int MAX_BULLETS = 25;
	
	private ArrayList<Body> targets;
	private CircleDef targetDef;
	private BodyDef targetBodyDef;
	private int targetStretch = 0;

	private Timer refreshTimer;
	private int reloadIters;
	private int shootIters;
	private int fadeInIters;
	private int FPS = 60;
	
	private long startTime;
	private long currentTime;
	
	private int mouseX; // mouse location relative to window coordinates
	private int mouseY; 
	private int mouseXOnWorld; // mouse location relative to world coordinates
	private int mouseYOnWorld;
	
	private boolean moveLeft;
	private boolean moveRight;
	private boolean jumpKeyDown;
	private boolean speedfallKeyDown;
	private boolean mouseOnScreen = false;
	private boolean LmousePressed = false;
	private boolean RmousePressed = false;
	private boolean mouseDragged = false;
	private boolean tutKeyDown = false;
	
	private final int doublePressInterval = 6;
	private int timeKeyDown = 0;
	private int timeKeyReleased = 0;
	private int lastKeyPressed = 0;
	private boolean doublePressDetected = false;

	private int cameraX;
	private int cameraY;
	private double scaleFactor = 0.5;
	
	private Clip clip;
	private AudioInputStream ais;
	
	private int waveNum;
	private int maxSpawns;
	private int nSpawns;
	private int maxSpeed;
	private int score;
	
	public Main()
	{
		waveNum = 1;
		maxSpawns = 10;
		nSpawns = 10;
		maxSpeed = 50;
		score = 0;
		
		setBackground(Color.black);
		
		moveLeft = false;
		moveRight = false;
		
		worldBounds = new AABB();
		worldBounds.lowerBound.set(-5000.0f, -5000.0f);
		worldBounds.upperBound.set(5000.0f, 5000.0f);
		
		Vec2 gravity = new Vec2(0.0f, 10.0f);
		
		boolean sleep = false;
		
		world = new World(worldBounds, gravity, sleep);
		//world.setWarmStarting(true);
		
		wheelDef = new CircleDef();  // Define shape properties itself
		wheelBodyDef = new BodyDef(); // Define location in world + interaction
		this.radius = 30;
		
		wheelDef.radius = this.radius;
		wheelDef.friction = 0.25f;
		wheelDef.restitution = 0.0f;
		wheelDef.density = 1000.0f;
		
		angle = 0; 
		
		wheelBodyDef.fixedRotation = true;
		wheelBodyDef.angle = (int) angle;
		wheelBodyDef.isBullet = true;
		wheelBodyDef.isSleeping = false;
		wheelBodyDef.position = new Vec2(-1000, 350);
		
		wheel = world.createBody(wheelBodyDef);
		wheel.createShape(wheelDef);
		wheel.setMassFromShapes();
		wheel.setBullet(false);
		wheel.setLinearVelocity(new Vec2(100.0f, -25.0f));
		
		wheelColor = new Color(0, 127, 255);
		
		jumpKeyDown = false;
		speedfallKeyDown = false;
	
		float smoothSpeedX = 0;
		float smoothSpeedY = 0;
		
		for (int i = 1; i <= maxSpawns; i++)
		{
			spawnSpawn();
			nSpawns++;
		}
		
		PolygonDef platformDef = new PolygonDef();
		BodyDef platformBodyDef = new BodyDef();
		
		platformDef.density = 0.0f; // Stationary body, not affected by gravity
		platformDef.friction = 0.1f;
		platformDef.restitution = 0.0f;
		
		platformDef.setAsBox(2000.0f, 50.0f);
		platformBodyDef.position.set(2000, 700);
		
		platform1 = world.createBody(platformBodyDef);
		platform1.createShape(platformDef);
		platform1.setMassFromShapes();
		platform1.setBullet(false);
		
		platformDef.setAsBox(200.0f, 50.0f);
		//platformBodyDef.position.set(2000, -200);
		platformBodyDef.position.set(2000, -2000);
		
		platform2 = world.createBody(platformBodyDef);
		platform2.createShape(platformDef);
		platform2.setMassFromShapes();
		platform2.setBullet(false);
		
		//platformBodyDef.position.set(1600, -100);
		platformBodyDef.position.set(1600, -2000);
		
		platform3 = world.createBody(platformBodyDef);
		platform3.createShape(platformDef);
		platform3.setMassFromShapes();
		platform3.setBullet(false);
		
		
		//platformBodyDef.position.set(1200, 0);
		platformBodyDef.position.set(1200, -2000);
		
		platform4 = world.createBody(platformBodyDef);
		platform4.createShape(platformDef);
		platform4.setMassFromShapes();
		platform4.setBullet(false);

		//platformBodyDef.position.set(2400, -100);
		platformBodyDef.position.set(2400, -2000);
		
		platform5 = world.createBody(platformBodyDef);
		platform5.createShape(platformDef);
		platform5.setMassFromShapes();
		platform5.setBullet(false);

		//platformBodyDef.position.set(2800, 0);
		platformBodyDef.position.set(2800, -2000);
		
		platform6 = world.createBody(platformBodyDef);
		platform6.createShape(platformDef);
		platform6.setMassFromShapes();
		platform6.setBullet(false);
		
		PolygonDef wallDef = new PolygonDef();
		BodyDef wallBodyDef = new BodyDef();
		
		wallDef.density = 0.0f;
		wallDef.friction = 0.1f;
		wallDef.restitution = 0.0f;
		wallDef.setAsBox(50.0f, 200.0f);
		
		wallBodyDef.position.set(275, 450);
		
		wall1 = world.createBody(wallBodyDef);
		wall1.createShape(wallDef);
		wall1.setMassFromShapes();
		wall1.setBullet(false);
		
		wallDef.setAsBox(50.0f, 400.0f);
		wallBodyDef.position.set(50, 100);
		
		wall2 = world.createBody(wallBodyDef);
		wall2.createShape(wallDef);
		wall2.setMassFromShapes();
		wall2.setBullet(false);
		
		wallDef.setAsBox(50.0f, 400.0f);
		wallBodyDef.position.set(3950, 100);
		
		wall3 = world.createBody(wallBodyDef);
		wall3.createShape(wallDef);
		wall3.setMassFromShapes();
		wall3.setBullet(false);
		
		wallDef.setAsBox(50.0f, 200.0f);
		wallBodyDef.position.set(3725, 450);
		
		wall4 = world.createBody(wallBodyDef);
		wall4.createShape(wallDef);
		wall4.setMassFromShapes();
		wall4.setBullet(false);
		
		bulletDef = new CircleDef();
		bulletBodyDef = new BodyDef();
		
		bulletDef.radius = 5;
		bulletDef.friction = 0.0f;
		bulletDef.restitution = 1.0f;
		bulletDef.density = 100.0f;
		
		bulletBodyDef.fixedRotation = true;
		bulletBodyDef.isBullet = true;
		bulletBodyDef.isSleeping = false;
		
		bullets = new ArrayList<Body>();
		nBullets = MAX_BULLETS;
		
		targetDef = new CircleDef();
		targetBodyDef = new BodyDef();
		
		targetDef.density = 50.0f;
		targetDef.radius = 50;
		targetDef.restitution = 1.0f;
		targetDef.density = 0.0f;
		targetDef.friction = 0.0f;
		
		targetBodyDef.fixedRotation = true;
		targetBodyDef.isSleeping = true;
		
		
		targets = new ArrayList<Body>();

		flickerIters = 0;
		shootIters = 7;
		fadeInIters = 0;
		reloadIters = 0;
		
		startTime = System.currentTimeMillis();
		currentTime = System.currentTimeMillis();
				
		cameraX = 0;
		cameraY = 0;
		
		is = new IntroScreen();
		
		refreshTimer = new Timer( (int) (1000 / FPS * 1.0), this);
		refreshTimer.start();
		
		playSound("training.wav");
		
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		setFocusable(true);

	}
	 
	protected void paintComponent(Graphics g)
	{	
		super.paintComponent(g);
		wheelX = wheel.getWorldCenter().x;
		wheelY = wheel.getWorldCenter().y;
		
		//cameraX += (int) (((-wheelX + (getWidth() / 2) - (((mouseX) - (getWidth() / 2)) * 0.75) - cameraX) * 0.075));
		//cameraY += (int) (((-wheelY + (getHeight() / 2) - (((mouseY) - (getHeight() / 2)) * 0.75) - cameraY) * 0.075));	
		
		cameraX += (((-wheelX + (getWidth() / 2) / scaleFactor) * scaleFactor) - (((mouseX) - (getWidth() / 2)) * 0.5) - cameraX) * 0.1;
		cameraY += (((-wheelY + (getHeight() / 2) / scaleFactor) * scaleFactor) - (((mouseY) - (getHeight() / 2)) * 0.5) - cameraY) * 0.1;
		
		//cameraX += (((-wheelX + (getWidth() / 2) / scaleFactor) * scaleFactor) - cameraX) * 0.5;
		//cameraY += (((-wheelY + (getHeight() / 2) / scaleFactor) * scaleFactor) - cameraY) * 0.5;
		
		mouseXOnWorld = (int) ((mouseX - cameraX) / scaleFactor);
		mouseYOnWorld = (int) ((mouseY - cameraY) / scaleFactor);
		
		//if (cameraX + (getWidth() * 2) >= 10000) { cameraX = 10000 - (int) (getWidth() * 2); }
		//if (cameraX - (getWidth() * 2) <= -10000) { cameraX = -10000 + (int) (getWidth() * 2); }
		//if (cameraY + (getHeight() * 2) >= 10000) { cameraY = 10000 - (int) (getHeight() * 2); }
		//if (cameraY - (getHeight() * 2) <= -10000) { cameraY = -10000 + (int) (getHeight() * 2); }
		
		AffineTransform t = new AffineTransform();
		Graphics2D g2 = (Graphics2D) g;
		
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(2.0f));
		
		t.setToTranslation(cameraX, cameraY);
		t.scale(scaleFactor, scaleFactor);
		g2.setTransform(t);
		
		g2.setColor(Color.RED);
		g2.drawRect(-10000, -10000, 20000, 20000);
		
		g2.setColor(new Color(0, 25, 0));
		g2.fillRect(0, -300, 4000, 1000);
		
		g2.setColor(new Color(25, 25, 25));
		for (int i = -200; i <= 200; i++)
		{
			g2.drawLine(i * 50, -10000, i * 50, 10000);
		}

		for (int i = -200; i <= 200; i++)
		{
			g2.drawLine(-10000, i * 50, 10000, i * 50);
		}
		
		g2.setColor(new Color(0, 75, 0));
		for (int i = 0; i <= 160; i++)
		{
			g2.drawLine(i * 25, -300, i * 25, 750);
		}

		for (int i = -12; i <= 30; i++)
		{
			g2.drawLine(0, i * 25, 4000, i * 25);
		}
		
		g2.setColor(Color.RED);
		
		for (Body spawn : spawns)
		{
			for (double d = 1; d >= 0.01; d /= 1.25)
			{
				t.setToTranslation(cameraX, cameraY);
				t.scale(scaleFactor, scaleFactor);
				g2.setTransform(t);
				
				if (d == 1)
				{
					g2.translate(spawn.getWorldCenter().x, spawn.getWorldCenter().y);
				}
				else
				{
					double increment = Math.log10(1.0 / d) / Math.log10(1.25);
					g2.translate(spawn.getWorldCenter().x - (spawn.getLinearVelocity().x * (increment / 25.0)), 
							spawn.getWorldCenter().y - (spawn.getLinearVelocity().y * (increment / 25.0)));
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) d));
				}
				
				g2.fillRect(-20, -20, 40, 40);
			}
			
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.00f));
			
			t.setToTranslation(cameraX, cameraY);
			t.scale(scaleFactor, scaleFactor);
			g2.setTransform(t);
			g2.translate(spawn.getWorldCenter().x, spawn.getWorldCenter().y);
			
			g2.fillRect(-20, -20, 40, 40);
		}
		
		g2.setColor(Color.RED);
		g2.setStroke(new BasicStroke(100.0f));
		g2.drawRect(-10000, -10000, 20000, 20000);
		g2.setStroke(new BasicStroke(2.0f));
		
		for (double d = 1; d >= 0.01; d /= 1.25)
		{
			t.setToTranslation(cameraX, cameraY);
			t.scale(scaleFactor, scaleFactor);
			g2.setTransform(t);
			
			if (d == 1)
			{
				g2.translate(wheel.getWorldCenter().x, wheel.getWorldCenter().y);
			}
			else
			{
				double increment = Math.log10(1.0 / d) / Math.log10(1.25);
				g2.translate(wheelX - (wheel.getLinearVelocity().x * (increment / 100.0)), 
						wheelY - (wheel.getLinearVelocity().y * (increment / 100.0)));
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) d));
			}

			g2.rotate(angle);
			
			g2.setColor(Color.GRAY);
			g2.fillOval(-1 * radius, -1 * radius, radius * 2, radius * 2);
			g2.setColor(wheelColor);
			g2.drawOval(-1 * radius, -1 * radius, radius * 2, radius * 2);
			g2.drawLine(-radius, 0, radius, 0);
			g2.drawLine(0, -radius, 0, radius);
		}
		
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.00f));
		
		t.setToTranslation(cameraX, cameraY);
		t.scale(scaleFactor, scaleFactor);
		g2.setTransform(t);
		
		g2.translate(wheel.getWorldCenter().x, wheel.getWorldCenter().y);
		g2.rotate(angle);
		
		g2.setColor(Color.GRAY);
		g2.fillOval(-1 * radius, -1 * radius, radius * 2, radius * 2);
		g2.setColor(wheelColor);
		g2.drawOval(-1 * radius, -1 * radius, radius * 2, radius * 2);
		g2.drawLine(-radius, 0, radius, 0);
		g2.drawLine(0, -radius, 0, radius);
		
		if (targets.get(0) != null)
		{
			t.setToTranslation(cameraX, cameraY);
			t.scale(scaleFactor, scaleFactor);
			g2.setTransform(t);
			
			double angle = Math.atan2(targets.get(0).getWorldCenter().y-wheel.getWorldCenter().y,targets.get(0).getWorldCenter().x-wheel.getWorldCenter().x)*(180.0/Math.PI);
			double radians = angle * (Math.PI / 180);
			g2.setColor(Color.RED);
			g2.drawLine((int) (wheelX + ((radius + 50) * Math.cos(radians))), (int) (wheelY + ((radius + 50) * Math.sin(radians))), (int) (wheelX + (75 * Math.cos(radians))), (int) (wheelY + (75 * Math.sin(radians))));
		}
		
		t.setToTranslation(cameraX, cameraY);
		t.scale(scaleFactor, scaleFactor);
		g2.setTransform(t);
		g2.translate(wheel.getWorldCenter().x, wheel.getWorldCenter().y);
		
		g2.setColor(Color.orange);
		if (wheel.getLinearVelocity().y > 100.0f)
		{
			flickerIters++;
			if (flickerIters % 5 == 0)
			{
				g2.setColor(Color.orange);
				g2.drawArc(-40, -40, 80, 80, 200, 140);
			}
		}
		else
		{
			flickerIters = 0;
		}
		
		
		for (Body target : targets)
		{
			t.setToTranslation(cameraX, cameraY);
			t.scale(scaleFactor, scaleFactor);
			g2.setTransform(t);
			g2.translate(target.getWorldCenter().x, target.getWorldCenter().y);
			
			g2.setColor(Color.RED);
			g2.fillOval(-1 * 3 * targetStretch, -1 * 51, 3 * targetStretch * 2, 51 * 2);
			g2.setColor(Color.WHITE);
			g2.drawOval(-1 * 3 * targetStretch, -1 * 51, 3 * targetStretch * 2, 51 * 2);
			g2.fillOval(-1 * 2 * targetStretch, -1 * 34, 2 * targetStretch * 2, 2 * 34);
			g2.setColor(Color.RED);
			g2.fillOval(-1 * targetStretch, -1 * 17, targetStretch * 2, 17 * 2);
			targetStretch++;
			if (targetStretch > 17)
			{
				targetStretch = 17;
			}
		}
		
		t.setToTranslation(cameraX, cameraY);
		t.scale(scaleFactor, scaleFactor);
		g2.setTransform(t);
		
		g2.translate(wall1.getWorldCenter().x, wall1.getWorldCenter().y);
		g2.setColor(Color.BLACK);
		g2.fillRect(-50, -200, 100, 400);
		g2.setColor(Color.WHITE);
		g2.drawRect(-50, -200, 100, 400);
		g2.drawString("[wall]", -12, 5);
		
		t.setToTranslation(cameraX, cameraY);
		t.scale(scaleFactor, scaleFactor);
		g2.setTransform(t);
		

		g2.translate(wall2.getWorldCenter().x, wall2.getWorldCenter().y);
		g2.setColor(Color.BLACK);
		g2.fillRect(-50, -400, 100, 800);
		g2.setColor(Color.WHITE);
		g2.drawRect(-50, -400, 100, 800);
		g2.drawString("[wall]", -12, 5);
		
		t.setToTranslation(cameraX, cameraY);
		t.scale(scaleFactor, scaleFactor);
		g2.setTransform(t);
		

		g2.translate(wall3.getWorldCenter().x, wall3.getWorldCenter().y);
		g2.setColor(Color.BLACK);
		g2.fillRect(-50, -400, 100, 800);
		g2.setColor(Color.WHITE);
		g2.drawRect(-50, -400, 100, 800);
		g2.drawString("[wall]", -12, 5);
		
		t.setToTranslation(cameraX, cameraY);
		t.scale(scaleFactor, scaleFactor);
		g2.setTransform(t);
		

		g2.translate(wall4.getWorldCenter().x, wall4.getWorldCenter().y);
		g2.setColor(Color.BLACK);
		g2.fillRect(-50, -200, 100, 400);
		g2.setColor(Color.WHITE);
		g2.drawRect(-50, -200, 100, 400);
		g2.drawString("[wall]", -12, 5);
		
		
		t.setToTranslation(cameraX, cameraY);
		t.scale(scaleFactor, scaleFactor);
		g2.setTransform(t);

		g2.translate(platform1.getWorldCenter().x, platform1.getWorldCenter().y);
		g2.setColor(Color.BLACK);
		g2.fillRect(-2000, -50, 4000, 100);
		g2.setColor(Color.WHITE);
		g2.drawRect(-2000, -50, 4000, 100);
		g2.drawString("[platform]", -16, 5);
		
		t.setToTranslation(cameraX, cameraY);
		t.scale(scaleFactor, scaleFactor);
		g2.setTransform(t);
/*
		g2.translate(platform2.getWorldCenter().x, platform2.getWorldCenter().y);
		g2.setColor(Color.BLACK);
		g2.fillRect(-200, -50, 400, 100);
		g2.setColor(Color.WHITE);
		g2.drawRect(-200, -50, 400, 100);
		g2.drawString("[platform]", -16, 5);
		
		t.setToTranslation(cameraX, cameraY);
		t.scale(scaleFactor, scaleFactor);
		g2.setTransform(t);

		g2.translate(platform3.getWorldCenter().x, platform3.getWorldCenter().y);
		g2.setColor(Color.BLACK);
		g2.fillRect(-200, -50, 400, 100);
		g2.setColor(Color.WHITE);
		g2.drawRect(-200, -50, 400, 100);
		g2.drawString("[platform]", -16, 5);
		
		t.setToTranslation(cameraX, cameraY);
		t.scale(scaleFactor, scaleFactor);
		g2.setTransform(t);

		g2.translate(platform4.getWorldCenter().x, platform4.getWorldCenter().y);
		g2.setColor(Color.BLACK);
		g2.fillRect(-200, -50, 400, 100);
		g2.setColor(Color.WHITE);
		g2.drawRect(-200, -50, 400, 100);
		g2.drawString("[platform]", -16, 5);
		
		t.setToTranslation(cameraX, cameraY);
		t.scale(scaleFactor, scaleFactor);
		g2.setTransform(t);

		g2.translate(platform5.getWorldCenter().x, platform5.getWorldCenter().y);
		g2.setColor(Color.BLACK);
		g2.fillRect(-200, -50, 400, 100);
		g2.setColor(Color.WHITE);
		g2.drawRect(-200, -50, 400, 100);
		g2.drawString("[platform]", -16, 5);
		
		t.setToTranslation(cameraX, cameraY);
		t.scale(scaleFactor, scaleFactor);
		g2.setTransform(t);

		g2.translate(platform6.getWorldCenter().x, platform6.getWorldCenter().y);
		g2.setColor(Color.BLACK);
		g2.fillRect(-200, -50, 400, 100);
		g2.setColor(Color.WHITE);
		g2.drawRect(-200, -50, 400, 100);
		g2.drawString("[platform]", -16, 5);
*/
		if (mouseOnScreen)
		{
			t.setToIdentity();		
			//t.scale(scaleFactor, scaleFactor);
			g2.setTransform(t);
			
			g2.setColor(Color.green);
			g2.drawOval(mouseX - 10, mouseY - 10, 20, 20);
			g2.drawLine(mouseX, mouseY - 15, mouseX, mouseY + 15);
			g2.drawLine(mouseX - 15, mouseY, mouseX + 15, mouseY);
					
			t.setToTranslation(cameraX, cameraY);
			t.scale(scaleFactor, scaleFactor);
			g2.setTransform(t);
		
			g2.setColor(Color.MAGENTA);
			
			double angle = Math.atan2(mouseYOnWorld-wheel.getWorldCenter().y,mouseXOnWorld-wheel.getWorldCenter().x)*(180.0/Math.PI);
			double radians = angle * (Math.PI / 180);
			g2.drawLine((int) (wheelX + ((radius + 1) * Math.cos(radians))), (int) (wheelY + ((radius + 1) * Math.sin(radians))), (int) (wheelX + (50 * Math.cos(radians))), (int) (wheelY + (50 * Math.sin(radians))));
		}
		
		g2.setStroke(new BasicStroke(1.0f));
		
		g2.setColor(Color.cyan);
		for(int i = 0; i < bullets.size(); i++)
		{
			t.setToTranslation(cameraX, cameraY);
			t.scale(scaleFactor, scaleFactor);
			g2.setTransform(t);
			
			Body b = (Body) bullets.get(i);
			g2.translate(b.getWorldCenter().x, b.getWorldCenter().y);
			g2.drawOval(-5, -5, 10, 10);
		}
		
		t.setToIdentity();		
		g2.setTransform(t);
		
		g2.setColor(Color.black);
		g2.fillRect(0, 0, 100, 150);
		g2.setColor(Color.white);
		g2.drawRect(0, 0, 100, 150);
		//g2.drawString("A: " + '\u221e' + "/" + '\u221e', 10, 20);
		g2.drawString("A: " + nBullets + "/" + MAX_BULLETS, 10, 20);
		if (nBullets < MAX_BULLETS)
		{
			g2.fillRect(10, 22, (int) (60 * (reloadIters / 12.0)), 1);
		}
		
		g2.drawString("P: (" + (int) wheelX + "," + (int) wheelY + ")", 10, 40);
		g2.drawString("V: (" + (int) wheel.getLinearVelocity().x + "," + (int) wheel.getLinearVelocity().y + ")", 10, 60);
		
		int seconds = (int) ((currentTime - startTime) / 1000);
		int minutes = seconds / 60;
		
		if (seconds % 60 < 10) 
		     { g2.drawString("T: " + minutes + ":0" + (seconds % 60), 10, 80); } 
		else { g2.drawString("T: " + minutes + ":" + (seconds % 60), 10, 80); }
		
		g2.drawString("m: (" + mouseX + "," + mouseY + ")", 10, 100);
		g2.drawString("mw: (" + mouseXOnWorld + "," + mouseYOnWorld + ")", 10, 120);
		g2.drawString("HP: " + wheelHP + "/" + wheelMaxHP, 10, 140);
		g2.drawString("SP: " + wheelSP + "/" + wheelMaxSP, 10, 160);
		
		if (tutKeyDown)
		{
			g2.drawString("MOUSE: Aim / Move Camera", 10, getHeight() - 190);
			g2.drawString("LMB: Fire", 10, getHeight() - 170);
			g2.drawString("W: Jump / Boost Up Wall When Rolling / Climb Objects", 10, this.getHeight() - 130);
			g2.drawString("A/D: Speed Left / Right (double tap fast for boost) / Roll Up Walls", 10, this.getHeight() - 110);
			g2.drawString("S: Engage Speedfall", 10, this.getHeight() - 90);
			g2.drawString("SPACE: Respawn", 10, this.getHeight() - 50);
			g2.drawString("ESC: Exit.", 10, this.getHeight() - 30);

		}
		else
		{
			g2.drawString("SHIFT: Display controls.", 10, this.getHeight() - 50);
			g2.drawString("ESC: Exit.", 10, this.getHeight() - 30);
		}
		
		g2.drawString("OmniBall v0.9.1a | Developed by Ashwin Chakilum, a Sophomore at UIUC, majoring in Computer Science ^_^", 10, this.getHeight() - 10);
		
		t.setToIdentity();
		g2.setTransform(t);
		
		fadeFromWhite(g2);

	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{	
		requestFocusInWindow();
		world.step((float) (10.0 / (FPS * 1.0)), 5);
		
		speed =  (float) Math.sqrt(Math.pow(wheel.getLinearVelocity().x, 2) + Math.pow(wheel.getLinearVelocity().y, 2));
		scaleFactor += (((1 / ((speed / 141.4 / 2) + 1.5)) - scaleFactor)) * 0.01 ;
		
		smoothSpeedX += (wheel.getLinearVelocity().x - smoothSpeedX) / 0.5;
		smoothSpeedY += (wheel.getLinearVelocity().y - smoothSpeedY) / 0.5;

		if (lightness >= 127)
		{
			oscillation *= -1;
		}
		
		if (lightness <= 0)
		{
			oscillation *= -1;
		}
		
		lightness += oscillation;
		
		wheelColor = new Color(0, lightness, 225);
		
		if (wheel.getTouchingBodyIsland().size() < 2)
		{
			inAir = true;
		}
		else
		{
			inAir = false;
		}
		
		if (jumpRecharge < 5 && wheel.getBodiesInContact().size() > 0 && !inAir)
		{
			jumpRecharge++;
		}
		
		if (inAir)
		{
			jumpRecharge = 0;
		}
		
		if (doublePressDetected)
		{
			timeKeyReleased = 0;
			timeKeyDown = 0;
		}
		else
		if (timeKeyDown > 0 && lastKeyPressed != 0)
		{
			timeKeyReleased++;
		}
		else
		if ((moveLeft || moveRight) && lastKeyPressed == 0)
		{
			timeKeyDown++;
		}
		
		if (isOutOfBounds(wheel))
		{
			respawn();
		}
		
		for (Iterator<Body> it = bullets.iterator(); it.hasNext();)
		{
			Body bullet = it.next();
			if (bullet.getTouchingBodyIsland().size() > 1 || isOutOfBounds(bullet))
			{
				for (Iterator<Body> it2 = targets.iterator(); it2.hasNext();)
				{
					Body target = it2.next();
					if (bullet.isTouching(target))
					{
						world.destroyBody(target);
						it2.remove();
						targetStretch = 0;
						playSound("break.wav");
					}
				}
				
				for (Iterator<Body> it3 = spawns.iterator(); it3.hasNext();)
				{
					Body spawn = it3.next();
					if (bullet.isTouching(spawn))
					{
						world.destroyBody(spawn);
						it3.remove();
					}
				}
				world.destroyBody(bullet);
				it.remove();
				bullet = null;
			}
		}
		
		for (Iterator<Body> it = spawns.iterator(); it.hasNext();)
		{
			Body spawn = it.next();
			
			if (wheel.isTouching(spawn))
			{
				wheelHP -= 10;
				recoveryItersHP = 0;
			}
			if (isOutOfBounds(spawn) || wheel.isTouching(spawn))
			{
				world.destroyBody(spawn);
				it.remove();
				spawn = null;
			}
			


		}
		
		if (nBullets < MAX_BULLETS)
		{
			reloadIters++;
		}
		
		if (reloadIters >= 16)
		{
			if (nBullets < MAX_BULLETS)
			{
				nBullets++;
			}
			reloadIters = 0;
		}
		
		if ((moveLeft || moveRight) && (wheel.isTouching(wall1) || wheel.isTouching(wall2) || wheel.isTouching(wall3) || wheel.isTouching(wall4)) && wheel.getLinearVelocity().y > -100.0f)
		{
			wheel.applyImpulse(new Vec2(0.0f, -10000000.0f), wheel.getLocalCenter());
		}
		
		if (moveLeft)
		{
			if (wheel.getLinearVelocity().x > -105.0f)
			{
				if (doublePressDetected && wheel.getLinearVelocity().x > -90.0f)
				{
					wheel.setLinearVelocity(new Vec2(-90.0f, wheel.getLinearVelocity().y));
					timeKeyReleased = 0;
					timeKeyDown = 0;
					doublePressDetected = false;
					lastKeyPressed = 0;
					playSound("dash.wav");
					wheelSP -= 10;
				}
				wheel.applyImpulse(new Vec2(-6000000.0f, 0.0f), wheel.getLocalCenter());
			}
			angle -= 1 / Math.PI;
		}
		
		if (moveRight)
		{
			if (wheel.getLinearVelocity().x < 105.0f)
			{
				if (doublePressDetected && wheel.getLinearVelocity().x < 90.0f)
				{
					wheel.setLinearVelocity(new Vec2(90.0f, wheel.getLinearVelocity().y));
					timeKeyReleased = 0;
					timeKeyDown = 0;
					doublePressDetected = false;
					lastKeyPressed = 0;
					playSound("dash.wav");
					wheelSP -= 10;
				}
				wheel.applyImpulse(new Vec2(6000000.0f, 0.0f), wheel.getLocalCenter());
			}
			angle +=  1 / Math.PI;
		}
		
		if (wheel.getLinearVelocity().y > 105.0f)
		{
			wheel.applyImpulse(new Vec2(0.0f, -5000000.0f), wheel.getLocalCenter());
		}
		
		if (LmousePressed)
		{
			if (shootIters % fireDelay == 0)
			{
				shoot();
			}
			shootIters++;
		}
		else
		{
			shootIters = 3;
		}
		

		
		spawnTargets();
		
		if (spawns.size() <= 0)
		{
			waveNum++;
			maxSpeed += 10;
			maxSpawns += 5;
			nSpawns += 5;
			
			for (int i = 0; i < maxSpawns; i++)
			{
				spawnSpawn();
			}
		}
		
		if (!moveLeft && !moveRight)
		{
			if (wheel.getTouchingBodyIsland().size() > 1)
			{
				angle +=  (0.01 * wheel.getLinearVelocity().x)  / Math.PI;
			}
			else
			{
				angle +=  (0.01 * wheel.getLinearVelocity().x)  / Math.PI;
			}
			
		}
		
		// SPAWN MOVEMENT (DISPLACEMENT)
		for (Body spawn : spawns)
		{
			double angle = Math.atan2(wheel.getWorldCenter().y - spawn.getWorldCenter().y , wheel.getWorldCenter().x - spawn.getWorldCenter().x);
			//spawn.applyForce(new Vec2((float) (maxSpeed * Math.cos(angle)), (float) (maxSpeed * Math.sin(angle)) - 500), spawn.getLocalCenter());
			spawn.setLinearVelocity(new Vec2( (float) (maxSpeed * Math.cos(angle)), (float) (maxSpeed * Math.sin(angle))));
		}
		
		if (recoveryItersHP < 1) 
		{
			recoveryItersHP++;
		}
		
		if (recoveryItersSP < 1)
		{
			recoveryItersSP++;
		}
		
		
		if (recoveryItersHP >= 1 && wheelHP < wheelMaxHP)
		{
			wheelHP += 2;
			recoveryItersHP = 0;
		}
		
		if (recoveryItersSP >= 1 && wheelSP < wheelMaxSP)
		{
			wheelSP++;
			recoveryItersSP = 0;
		}
		
		if (wheelHP <= 0)
		{
			respawn();
		}
		
		currentTime = System.currentTimeMillis();
		
		repaint();

	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		int c = e.getKeyCode();
		
		if (c == e.VK_D)
		{
			if (timeKeyReleased <= doublePressInterval && timeKeyDown <= doublePressInterval && lastKeyPressed == e.VK_D)
			{
				doublePressDetected = true;
				timeKeyReleased = 0;
			}
			if (lastKeyPressed == e.VK_A || timeKeyReleased > doublePressInterval)
			{
				timeKeyReleased = 0;
				timeKeyDown = 0;
				doublePressDetected = false;
				lastKeyPressed = 0;
			}
			moveRight = true;
		}

		if (c == e.VK_A)
		{
			if (timeKeyReleased <= doublePressInterval && timeKeyDown <= doublePressInterval && lastKeyPressed == e.VK_A)
			{
				doublePressDetected = true;
				timeKeyReleased = 0;
			}
			if (lastKeyPressed == e.VK_D || timeKeyReleased > doublePressInterval)
			{
				timeKeyReleased = 0;
				timeKeyDown = 0;
				doublePressDetected = false;
				lastKeyPressed = 0;
			}
			moveLeft = true;
		}
		

		if (c == e.VK_W && wheel.getTouchingBodyIsland().size() > 1 && !jumpKeyDown && jumpRecharge >= 2)
		{
			int nSpawnsTouching = 0;
			for (Body spawn : spawns)
			{
				if (wheel.getTouchingBodyIsland().contains(spawn))
				{
						nSpawnsTouching++;
				}
			}
			
			if (nSpawnsTouching == wheel.getTouchingBodyIsland().size() - 1)
			{
				return;
			}
			
			jumpKeyDown = true;
			float ySpeed = wheel.getLinearVelocity().y - 60.0f;
			if (ySpeed <= -60.0f)
			{
				ySpeed = wheel.getLinearVelocity().y;
			}
			if (wheel.getLinearVelocity().y >= -60.0f)
			{
				ySpeed = -60.0f;
				playSound("dash.wav");
				wheelSP -= 10;
			}
			
			jumpRecharge = 0;
			
			wheel.setLinearVelocity(new Vec2(wheel.getLinearVelocity().x, ySpeed));
			
			return;
		}
		
		if (c == e.VK_S && wheel.getTouchingBodyIsland().size() < 2 && !speedfallKeyDown)
		{
			
			speedfallKeyDown = true;
			if (wheel.getLinearVelocity().y <= 100.0f)
			{
				playSound("dash.wav");
				wheelSP -= 10;
			}
			wheel.setLinearVelocity(new Vec2(wheel.getLinearVelocity().x, 100.0f));
		}
		
		if (c == e.VK_SPACE)
		{
			respawn();
		}
		
		if (c == e.VK_ESCAPE)
		{
			System.exit(0);
		}
		
		if (c == e.VK_SHIFT)
		{
			tutKeyDown = true;
		}
		
		if (c == e.VK_1)
		{
			scaleFactor = 1;
		}
		
		if (c == e.VK_2)
		{
			scaleFactor = 0.5;
		}
		
		if (c == e.VK_3)
		{
			scaleFactor = 0.33;
		}
		
		if (c == e.VK_4)
		{
			scaleFactor = 0.25;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		int c = e.getKeyCode();
		
		if (c == e.VK_D)
		{
			if (doublePressDetected)
			{
				doublePressDetected = false;
				lastKeyPressed = 0;
			}
			else
			if (timeKeyDown > doublePressInterval)
			{
				timeKeyReleased = 0;
				timeKeyDown = 0;
				doublePressDetected = false;
				lastKeyPressed = 0;
			}
			else
			{
				lastKeyPressed = e.VK_D;
			}

			moveRight = false;
		}

		if (c == e.VK_A)
		{
			if (doublePressDetected)
			{
				doublePressDetected = false;
				lastKeyPressed = 0;
			}
			else
			if (timeKeyDown > doublePressInterval)
			{
				timeKeyReleased = 0;
				timeKeyDown = 0;
				doublePressDetected = false;
				lastKeyPressed = 0;
			}
			else
			{
				lastKeyPressed = e.VK_A;
			}

			moveLeft = false;
		}
		
		if (c == e.VK_W)
		{
			jumpKeyDown = false;
		}
		
		if (c == e.VK_S)
		{
			speedfallKeyDown = false;
		}
		
		if (c == e.VK_SHIFT)
		{
			tutKeyDown = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) 
	{
		//int c = e.getKeyCode();
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		
	}

	@Override
	public void mouseEntered(MouseEvent e) 
	{
		mouseOnScreen = true;
	}

	@Override
	public void mouseExited(MouseEvent e) 
	{
		mouseOnScreen = false;
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		if (e.getButton() == e.BUTTON1)
		{
			LmousePressed = true;
		}
		if (e.getButton() == e.BUTTON3)
		{
			RmousePressed = true;
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) 
	{
		LmousePressed = false;
		RmousePressed = false;
		mouseDragged = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		mouseDragged = true;
		this.mouseX = (int) e.getX();
		this.mouseY =(int) e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) 
	{
		this.mouseX = (int) e.getX();
		this.mouseY = (int) e.getY();
	}
	
	private boolean isOutOfBounds(Body b)
	{
		if(b.getWorldCenter().y >= worldBounds.upperBound.y || b.getWorldCenter().y <= worldBounds.lowerBound.y || b.getWorldCenter().x > worldBounds.upperBound.x || b.getWorldCenter().x < worldBounds.lowerBound.x)
		{
			return true;
		}
		return false;
	}
	
	private void shoot()
	{
		if (nBullets <= 0)
		{
			return;
		}
			double angle = Math.atan2(mouseYOnWorld-wheel.getWorldCenter().y,mouseXOnWorld-wheel.getWorldCenter().x)*(180.0/Math.PI);
			double radians = angle * (Math.PI / 180);
			bulletBodyDef.position = new Vec2((int) (wheelX + ((radius + 10 + (0.25 * Math.abs(wheel.getLinearVelocity().x))) * Math.cos(radians))), 
											  (int) (wheelY + ((radius + 10 + (0.25 * Math.abs(wheel.getLinearVelocity().y))) * Math.sin(radians))));
			
			Body bullet = world.createBody(bulletBodyDef);
			bullets.add(bullet);
			
			bullet.createShape(bulletDef);
			bullet.setMassFromShapes();
			bullet.setBullet(true);
			bullet.applyImpulse(new Vec2((float) (10000000 * Math.cos(radians)), (float) (10000000 * Math.sin(radians))), bullet.getWorldCenter());
			
			//nBullets--;
			
			double ang = Math.random() * 2 * Math.PI;
			cameraX += 12.5 * (Math.cos(ang));
			cameraY += 12.5 * (Math.sin(ang));
			
			playSound("shoot.wav");
			nBullets--;
					
		//}
	}
	
	private void respawn()
	{
		wheelHP = wheelMaxHP;
		wheelSP = wheelMaxSP;
		world.destroyBody(wheel);
		wheel = world.createBody(wheelBodyDef);
		wheel.createShape(wheelDef);
		wheel.setMassFromShapes();
		wheel.setBullet(false);
		wheel.setLinearVelocity(new Vec2(100.0f, -25.0f));
		nBullets = MAX_BULLETS;
	}
	
	private void spawnTargets()
	{
		if (targets.size() < 1)
		{
			int randX = 0;
			int randY = 0;
				randX = (int)(Math.random() * (3000 - 1000) + 1000);
				randY = (int)(Math.random() * (600 - 0) + 0);
				targetBodyDef.position.set(randX, randY);
				Body target = world.createBody(targetBodyDef);
				target.createShape(targetDef);
				target.setMassFromShapes();
				targets.add(target);
		}
	}
	
	private void fadeFromWhite(Graphics2D g2)
	{
		if (fadeInIters < 30)
		{
			fadeInIters++;
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.033f * fadeInIters));
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, getWidth(), getHeight());
			return;
		}
		else		
		if (fadeInIters < 130)
		{
			fadeInIters++;
			int subIters = fadeInIters - 30;
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.00f - (0.01f * subIters)));
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, getWidth(), getHeight());
			return;
		}
		else
		{
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.00f));
		}
		
	}
	
	public void playSound(final String filename)
	{
		try
		{
			File file = new File("src/ado/" + filename);
			final Clip clip = AudioSystem.getClip();
			AudioInputStream ais = AudioSystem.getAudioInputStream(file);
			
			
			
			clip.addLineListener(new LineListener()
			{
				public void update(LineEvent le)
				{
					if (le.getType() == LineEvent.Type.STOP)
					{
						if (filename.equals("training.wav"))
						{
							clip.stop();
							clip.setFramePosition(1500);
							clip.start();
						}
						else
						{
							clip.close();
						}

					}
				}
			});
			
			clip.open(ais);
			if (filename.equals("training.wav"))
			{
				clip.setFramePosition(1500);
			}
			clip.start();


		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private void spawnSpawn()
	{
		int randX = (int)(Math.random() * (3000 - 1000) + 1000);
		
		CircleDef spawnDef = new CircleDef();
		BodyDef spawnBodyDef = new BodyDef();
		
		spawnDef.density = 0.125f;
		spawnDef.friction = 0.0f;
		spawnDef.restitution = 0.5f;
		
		spawnDef.radius = 20f;
		spawnBodyDef.position.set(randX, 200);
		spawnBodyDef.fixedRotation = true;
		
		Body spawn = world.createBody(spawnBodyDef);
		spawn.createShape(spawnDef);
		spawn.setMassFromShapes();
		spawn.setBullet(false);
		
		spawns.add(spawn);
	}

}
