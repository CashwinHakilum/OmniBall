import java.awt.*;
import java.awt.FontMetrics;
import java.awt.geom.*;

public class IntroScreen 
{
	
	private String header;
	private Font font;
	private FontMetrics fm;
	private int keyframe;
	private int endframe;
	private AffineTransform t;
	
	public IntroScreen()
	{
		header = "OMNIBALL [ALPHA]";
		font = new Font("Terminal", Font.ITALIC, 50);
		keyframe = 0;
		endframe = 121;
		t = new AffineTransform();
		t.setToIdentity();
	}

	public int getKeyframe() 
	{
		return keyframe;
	}

	public void setKeyframe(int keyframe) 
	{
		this.keyframe = keyframe;
	}
	
	public void nextFrame()
	{
		keyframe++;
	}
	
	public boolean isDone()
	{
		return (keyframe == endframe);
	}
	
	public void animate(Graphics2D g2)
	{
		nextFrame();
		g2.setColor(Color.BLUE);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g2.setFont(font);
		fm = g2.getFontMetrics();
		
		if (keyframe <= 30)
		{
			int subframe = keyframe;
			g2.drawRect(-160 + (20 * subframe), 320 - fm.getHeight(), fm.stringWidth(header) + 25, fm.getHeight());
			g2.drawString(header, -150 + (20 * subframe), 300);
			return;
		}
		
		if (keyframe <= 90)
		{
			int subframe = keyframe - 60;
			g2.drawRect(440, 320 - fm.getHeight(), fm.stringWidth(header) + 25, fm.getHeight());
			g2.drawString(header, 450, 300);
			return;
		}
		
		if (keyframe <= 120)
		{
			int subframe = keyframe - 90;
			g2.drawRect(440 + (int) (30 * subframe), 320 - fm.getHeight(), fm.stringWidth(header) + 25, fm.getHeight());
			g2.drawString(header, 450 + (int) (30 * subframe), 300);
			return;
		}
		
		keyframe = endframe;
		
	}

}
