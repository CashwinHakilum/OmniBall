import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import java.awt.Color;
import java.util.ArrayList;

import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

import java.util.ArrayList;
import java.util.Iterator;

import org.jbox2d.collision.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*; 
import org.jbox2d.dynamics.*;


public class Omniball 
{
	
	private World inWorld;
	
	private CircleDef wheelDef; // Define shape properties itself
	private BodyDef wheelBodyDef; // Define location in world + interaction
	private Body wheelBody;
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
	
	private int recoveryIters = 0;
	private int flickerIters;
	private int reloadIters;
	private int shootIters;
	
	private CircleDef bulletDef;
	private BodyDef bulletBodyDef;
	private ArrayList<Body> bullets;
	private int nBullets;
	private final int MAX_BULLETS = 10;
	
	public Omniball(World inWorld)
	{
		this.inWorld = inWorld;
		
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
		
		wheelBody = inWorld.createBody(wheelBodyDef);
		wheelBody.createShape(wheelDef);
		wheelBody.setMassFromShapes();
		wheelBody.setBullet(false);
		wheelBody.setLinearVelocity(new Vec2(100.0f, -25.0f));
		
		wheelColor = new Color(0, 127, 255);
		
		shootIters = 7;
		reloadIters = 0;
	}
	
	public void drawBullets(Graphics2D g2, AffineTransform t, int cameraX, int cameraY, int scaleFactor)
	{
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
	}
	
	public void drawBody(Graphics2D g2, AffineTransform t, int cameraX, int cameraY, int scaleFactor)
	{
		for (double d = 1; d >= 0.01; d /= 1.25)
		{
			t.setToTranslation(cameraX, cameraY);
			t.scale(scaleFactor, scaleFactor);
			g2.setTransform(t);
			
			if (d == 1)
			{
				g2.translate(wheelBody.getWorldCenter().x, wheelBody.getWorldCenter().y);
			}
			else
			{
				double increment = Math.log10(1.0 / d) / Math.log10(1.25);
				g2.translate(wheelX - (wheelBody.getLinearVelocity().x * (increment / 100.0)), 
						wheelY - (wheelBody.getLinearVelocity().y * (increment / 100.0)));
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
		
		g2.translate(wheelBody.getWorldCenter().x, wheelBody.getWorldCenter().y);
		g2.rotate(angle);
		
		g2.setColor(Color.GRAY);
		g2.fillOval(-1 * radius, -1 * radius, radius * 2, radius * 2);
		g2.setColor(wheelColor);
		g2.drawOval(-1 * radius, -1 * radius, radius * 2, radius * 2);
		g2.drawLine(-radius, 0, radius, 0);
		g2.drawLine(0, -radius, 0, radius);
		
		t.setToTranslation(cameraX, cameraY);
		t.scale(scaleFactor, scaleFactor);
		g2.setTransform(t);
		g2.translate(wheelBody.getWorldCenter().x, wheelBody.getWorldCenter().y);
		
		g2.setColor(Color.orange);
		if (wheelBody.getLinearVelocity().y > 100.0f)
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
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Getters and setters
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public CircleDef getCircleDef() {
		return wheelDef;
	}

	public void setCircleDef(CircleDef wheelDef) {
		this.wheelDef = wheelDef;
	}

	public BodyDef getBodyDef() {
		return wheelBodyDef;
	}

	public void setBodyDef(BodyDef wheelBodyDef) {
		this.wheelBodyDef = wheelBodyDef;
	}

	public Body getBody() {
		return wheelBody;
	}

	public void setBody(Body wheel) {
		this.wheelBody = wheel;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public float getX() {
		return wheelX;
	}

	public void setX(float wheelX) {
		this.wheelX = wheelX;
	}

	public float getY() {
		return wheelY;
	}

	public void setY(float wheelY) {
		this.wheelY = wheelY;
	}

	public float getSmoothSpeedX() {
		return smoothSpeedX;
	}

	public void setSmoothSpeedX(float smoothSpeedX) {
		this.smoothSpeedX = smoothSpeedX;
	}

	public float getSmoothSpeedY() {
		return smoothSpeedY;
	}

	public void setSmoothSpeedY(float smoothSpeedY) {
		this.smoothSpeedY = smoothSpeedY;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public int getJumpRecharge() {
		return jumpRecharge;
	}

	public void setJumpRecharge(int jumpRecharge) {
		this.jumpRecharge = jumpRecharge;
	}

	public boolean isInAir() {
		return inAir;
	}

	public void setInAir(boolean inAir) {
		this.inAir = inAir;
	}

	public Color getColor() {
		return wheelColor;
	}

	public void setColor(Color wheelColor) {
		this.wheelColor = wheelColor;
	}

	public int getOscillation() {
		return oscillation;
	}

	public void setOscillation(int oscillation) {
		this.oscillation = oscillation;
	}

	public int getLightness() {
		return lightness;
	}

	public void setLightness(int lightness) {
		this.lightness = lightness;
	}

	public int getHP() {
		return wheelHP;
	}

	public void setHP(int wheelHP) {
		this.wheelHP = wheelHP;
	}

	public int getMaxHP() {
		return wheelMaxHP;
	}

	public void setMaxHP(int wheelMaxHP) {
		this.wheelMaxHP = wheelMaxHP;
	}

	public int getRecoveryIters() {
		return recoveryIters;
	}

	public void setRecoveryIters(int recoveryIters) {
		this.recoveryIters = recoveryIters;
	}

	public int getFlickerIters() {
		return flickerIters;
	}

	public void setFlickerIters(int flickerIters) {
		this.flickerIters = flickerIters;
	}

	public CircleDef getBulletDef() {
		return bulletDef;
	}

	public void setBulletDef(CircleDef bulletDef) {
		this.bulletDef = bulletDef;
	}

	public BodyDef getBulletBodyDef() {
		return bulletBodyDef;
	}

	public void setBulletBodyDef(BodyDef bulletBodyDef) {
		this.bulletBodyDef = bulletBodyDef;
	}

	public ArrayList<Body> getBullets() {
		return bullets;
	}

	public void setBullets(ArrayList<Body> bullets) {
		this.bullets = bullets;
	}

	public int getnBullets() {
		return nBullets;
	}

	public void setnBullets(int nBullets) {
		this.nBullets = nBullets;
	}

	public int getMAX_BULLETS() {
		return MAX_BULLETS;
	}

}
