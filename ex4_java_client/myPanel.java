package ex4_java_client;

import ex4_java_client.elements.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;


public class myPanel extends JPanel {
    GameData game;
    private BufferedImage imageAgent;
    private BufferedImage imagePok1;
    private BufferedImage imagePok2;
    private double xMinNew;
    private double yMinNew;
    private double xMaxNew;
    private double yMaxNew;
    JLabel AreaTime;
    myPanel The_paint;
    JLabel AreaScore;
    JPanel TimeScore;
    JButton stop;
    int Score;
    int Time;

    public myPanel(GameData game) {
        this.game = game;
        scalingsize();
        this.setPreferredSize(new Dimension(900, 900));
        //this.setBounds(0,100,900,700);
        try {
            imageAgent = ImageIO.read(new File("ex4_java_client/images/pokemonBall.png"));
            imagePok1 = ImageIO.read(new File("ex4_java_client/images/Pokemon3.png"));
            imagePok2 = ImageIO.read(new File("ex4_java_client/images/Pokemon2.png"));
        } catch (IOException ex) {
            System.out.println("Didn't succeed to open the image");
        }
        repaint();
    }

    private void scalingsize() {
        xMinNew = Integer.MAX_VALUE;
        yMinNew = Integer.MAX_VALUE;
        xMaxNew = Integer.MIN_VALUE;
        yMaxNew = Integer.MIN_VALUE;
        Iterator<Node> iterator = this.game.getAlgoGraph().getGraph().nodeIter();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            xMinNew = Math.min(node.getLocation().x(), xMinNew);
            yMinNew = Math.min(node.getLocation().y(), yMinNew);
            xMaxNew = Math.max(node.getLocation().x(), xMaxNew);
            yMaxNew = Math.max(node.getLocation().y(), yMaxNew);
        }
    }


    private int getXScale(geoLocation pos) {
        return (int) ((((pos.x() - xMinNew) / (xMaxNew - xMinNew)) * this.getWidth() * 0.9) + (0.05 * this.getWidth()));
    }


    private int getYScale(geoLocation pos) {
        return (int) ((((pos.y() - yMinNew) * (this.getHeight() - 100) / (yMaxNew - yMinNew)) * 0.9) + (0.05 * (this.getHeight() - 100)));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g1 = (Graphics2D) g;
        Iterator<Node> currIter = this.game.getAlgoGraph().getGraph().nodeIter();
        while (currIter.hasNext()) {
            Node currNode = currIter.next();
            g.setColor(Color.BLUE);
            g.fillOval(getXScale(currNode.getLocation()), getYScale(currNode.getLocation()), 15, 15);//We need to do the scaling right!
            g.drawString(currNode.getKey() + "", getXScale(currNode.getLocation()), getYScale(currNode.getLocation()));
        }
        Iterator<Edge> Edges = this.game.getAlgoGraph().getGraph().edgeIter();
        while (Edges.hasNext()) {
            Edge currEdge = Edges.next();
            Node src = this.game.getAlgoGraph().getGraph().getNode(currEdge.getSrc());
            Node dest = this.game.getAlgoGraph().getGraph().getNode(currEdge.getDest());
            g.setColor(Color.BLACK);
            g.drawLine(getXScale(src.getLocation()) + 8, getYScale(src.getLocation()) + 8, getXScale(dest.getLocation()) + 8, getYScale(dest.getLocation()) + 8);
            drawArrow(g1, getXScale(src.getLocation()) + 8, getYScale(src.getLocation()) + 8, getXScale(dest.getLocation()) + 8, getYScale(dest.getLocation()) + 8);
        }
        g.setColor(Color.YELLOW);
        for (int i = 0; i < this.game.getPokemons().size(); i++) {
            Pokemon pokemon = this.game.getPokemons().get(i);
            if (pokemon.getType() > 0) {
                g.drawImage(imagePok1, getXScale(pokemon.getLocation()) - 8, getYScale(pokemon.getLocation()) - 8, 40, 40, null);
            } else {
                g.drawImage(imagePok2, getXScale(pokemon.getLocation()) - 8, getYScale(pokemon.getLocation()) - 8, 35, 35, null);
            }
        }
        for (int i = 0; i < this.game.getAgents().size(); i++) {
            Agent agent = this.game.getAgents().get(i);
            g.drawImage(imageAgent, getXScale(agent.getLocation()) - 8, getYScale(agent.getLocation()) - 8, 35, 35, null);
        }
    }


    void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
        Graphics2D g = (Graphics2D) g1.create();

        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx * dx + dy * dy);
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g.transform(at);

        g.drawLine(0, 0, len, 0);
        g.fillPolygon(new int[]{len, len - 5, len - 5, len}, new int[]{0, -5, 5, 0}, 4);
    }


}