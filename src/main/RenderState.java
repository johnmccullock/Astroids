package main;

import java.awt.AlphaComposite;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;

public interface RenderState
{
	abstract void render(Graphics2D g2d, AffineTransform original, AlphaComposite base);
}
