package main;

import java.awt.AlphaComposite;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;

public interface AppStatable
{
	abstract void render(Graphics2D g2d, AffineTransform original, AlphaComposite base);
	abstract void update();
	abstract UI getUI();
	abstract void setFPS(int fps);
}
