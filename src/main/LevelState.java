package main;

import java.awt.AlphaComposite;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;

/**
 * Interface for the various game states used by the Game class.
 * 
 * @author John McCullock
 * @version 1.0 2015-08-07
 */
public interface LevelState
{
	abstract void reset();
	abstract void update();
	abstract void render(Graphics2D g2d, AffineTransform original, AlphaComposite base);
}
