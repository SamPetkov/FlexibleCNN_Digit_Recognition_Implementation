package petkov.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * Component for drawing !
 *
 * @author sam_petkov 19.05.2021
 */
public class DrawArea extends JComponent {

    private final Font sansSerifBold = new Font("Courier New", Font.BOLD, 18);
    // Image in which we're going to draw
    private Image image;
    // Graphics2D praintbrush
    private Graphics2D g2;
    // Mouse coordinates
    private int currentX, currentY, oldX, oldY;
    public DrawArea() {
        setDoubleBuffered(false);
        setToolTipText("Please enter a digit");
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "",
                TitledBorder.LEFT,
                TitledBorder.TOP, sansSerifBold, Color.BLACK));
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // save coord x,y when mouse is pressed
                oldX = e.getX();
                oldY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                // coord x,y when drag mouse
                currentX = e.getX();
                currentY = e.getY();

                if (g2 != null) {
                    g2.setStroke(new BasicStroke(10));
                    // draw line if g2 context not null
                    g2.drawLine(oldX, oldY, currentX, currentY);
                    // refresh draw area to repaint
                    repaint();
                    // store current coords x,y as olds x,y
                    oldX = currentX;
                    oldY = currentY;
                }
            }
        });
    }

    protected void paintComponent(Graphics g) {
        if (image == null) {
            // image to draw null ==> we create
            image = createImage(getSize().width, getSize().height);
            g2 = (Graphics2D) image.getGraphics();
            // enable antialiasing
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // clear draw area
            clear();
        }

        g.drawImage(image, 0, 0, null);
    }

    public void clear() {
        g2.setPaint(Color.gray);
        // draw white on entire draw area to clear
        g2.fillRect(0, 0, getSize().width, getSize().height);
        g2.setPaint(Color.white);
        repaint();
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}