package Game;

import Collections.ListasIterador.Classes.LinkedUnorderedList;
import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

/**
 * The Graphical User Interface for the game "Labirinto da Glória".
 * <p>
 * This class is responsible for rendering the game state visually using Java Swing.
 * It draws the maze structure (rooms and corridors), player positions, and animations
 * without relying on external graph libraries, strictly adhering to the project constraints.
 * </p>
 *
 * @author YourGroup
 * @version 1.0
 */
public class GameGUI extends JFrame {

    /** Reference to the game map structure. */
    private MazeMap map;

    /** List of players to render on the map. */
    private LinkedUnorderedList<Player> players;

    /** Text area for displaying the game log. */
    private JTextArea logArea;

    /** Custom panel for drawing graphics. */
    private GamePanel gamePanel;

    // --- ANIMATION STATE ---
    private Player animatingPlayer = null;
    private Room animStartRoom = null;
    private Room animEndRoom = null;
    private double animProgress = 0.0;
    private Timer animationTimer;

    /**
     * Constructs the GameGUI.
     *
     * @param map     The MazeMap containing rooms and connections.
     * @param players The list of players currently in the game.
     */
    public GameGUI(MazeMap map, LinkedUnorderedList<Player> players) {
        this.map = map;
        this.players = players;

        setTitle("Labirinto da Glória - 3D Visualizer");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Setup the Log Area (Right Side)
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(20, 20, 25));
        logArea.setForeground(new Color(0, 255, 100)); // Hacker/Matrix Green style
        logArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        logArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setPreferredSize(new Dimension(350, 800));
        scroll.setBorder(null);
        add(scroll, BorderLayout.EAST);

        // 2. Setup the Game Drawing Area (Center)
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        // 3. Setup Animation Loop (60 FPS)
        animationTimer = new Timer(16, e -> {
            if (animatingPlayer != null) {
                animProgress += 0.05; // Animation speed (5% per frame)
                if (animProgress >= 1.0) {
                    animProgress = 1.0;
                    animatingPlayer = null; // Animation finished
                }
                gamePanel.repaint();
            }
        });
        animationTimer.start();

        setVisible(true);
    }

    /**
     * Appends a message to the on-screen log.
     *
     * @param text The message to display.
     */
    public void log(String text) {
        logArea.append(text + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength()); // Auto-scroll to bottom
    }

    /**
     * Triggers a smooth movement animation for a player.
     *
     * @param p    The player moving.
     * @param from The room the player is leaving.
     * @param to   The destination room.
     */
    public void animateMove(Player p, Room from, Room to) {
        this.animatingPlayer = p;
        this.animStartRoom = from;
        this.animEndRoom = to;
        this.animProgress = 0.0;
        gamePanel.repaint();
    }

    /**
     * Forces a redraw of the game board.
     */
    public void refresh() {
        gamePanel.repaint();
    }

    /**
     * Inner class responsible for the actual custom drawing of the map.
     */
    private class GamePanel extends JPanel {
        public GamePanel() {
            setBackground(new Color(40, 40, 50)); // Dark Gray background
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Safety check: Ensure map has rooms
            if (!map.getRooms().hasNext()) return;

            // --- LAYER 1: CORRIDORS (Lines) ---
            // We draw lines first so they appear behind the room circles.
            g2.setStroke(new BasicStroke(3));

            Iterator<Room> itFrom = map.getRooms();
            while(itFrom.hasNext()) {
                Room r1 = itFrom.next();

                // Check connections to all other rooms (Inefficient but safe for N < 100)
                // We use a fresh iterator to check pairs
                Iterator<Room> itTo = map.getRooms();
                while(itTo.hasNext()) {
                    Room r2 = itTo.next();

                    // Only draw if they are neighbors (connected edge)
                    if (map.isNeighbor(r1, r2)) {
                        double weight = map.getWeight(r1, r2);

                        // Color coding for paths
                        if (weight > 100) {
                            g2.setColor(new Color(150, 50, 50)); // Red = Locked
                        } else {
                            g2.setColor(new Color(100, 100, 120)); // Gray = Open
                        }

                        // Draw line between coordinates
                        g2.drawLine(r1.getX(), r1.getY(), r2.getX(), r2.getY());
                    }
                }
            }

            // --- LAYER 2: ROOMS (Circles) ---
            Iterator<Room> itDraw = map.getRooms();
            while(itDraw.hasNext()) {
                Room r = itDraw.next();
                int x = r.getX();
                int y = r.getY();
                int size = 50;

                // Room Styling based on Type
                if (r.getType().equals("TREASURE")) g2.setColor(new Color(255, 215, 0)); // Gold
                else if (r.getType().equals("ENTRANCE")) g2.setColor(new Color(50, 205, 50)); // Green
                else g2.setColor(new Color(70, 130, 180)); // Blue

                // Draw filled circle centered at X,Y
                g2.fillOval(x - size/2, y - size/2, size, size);

                // Draw border
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(x - size/2, y - size/2, size, size);

                // Draw Label (Room ID)
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString(r.getId(), x - 20, y - 30);

                // Draw Badges for Interactions
                if (r.getInteraction().equals("lever")) {
                    g2.setColor(Color.ORANGE);
                    g2.fillOval(x - 20, y + 20, 15, 15);
                    g2.setColor(Color.BLACK);
                    g2.drawString("!", x - 16, y + 32);
                } else if (r.getInteraction().equals("enigma")) {
                    g2.setColor(Color.MAGENTA);
                    g2.fillOval(x + 5, y + 20, 15, 15);
                    g2.setColor(Color.BLACK);
                    g2.drawString("?", x + 9, y + 32);
                }
            }

            // --- LAYER 3: PLAYERS ---
            Iterator<Player> playerIt = players.iterator();
            while(playerIt.hasNext()) {
                Player p = playerIt.next();
                int px = 0, py = 0;

                // Check if this specific player is currently animating
                if (p == animatingPlayer && animStartRoom != null && animEndRoom != null) {
                    // INTERPOLATION LOGIC
                    int sx = animStartRoom.getX();
                    int sy = animStartRoom.getY();
                    int ex = animEndRoom.getX();
                    int ey = animEndRoom.getY();

                    // Calculate intermediate point based on progress (0.0 to 1.0)
                    px = (int) (sx + (ex - sx) * animProgress);
                    py = (int) (sy + (ey - sy) * animProgress);
                } else {
                    // Static position
                    if (p.getCurrentRoom() != null) {
                        px = p.getCurrentRoom().getX();
                        py = p.getCurrentRoom().getY();
                    }
                }

                // Draw Player Token
                g2.setColor(p.getColor());
                g2.fillOval(px - 15, py - 15, 30, 30);

                // Player Border
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(px - 15, py - 15, 30, 30);

                // Player Name
                g2.setColor(Color.WHITE);
                g2.drawString(p.getName(), px - 20, py + 45);

                // Visual Status Effects
                if(p.getSkipTurns() > 0) {
                    g2.setColor(Color.RED);
                    g2.setFont(new Font("Arial", Font.BOLD, 20));
                    g2.drawString("ZZZ", px, py - 20); // Sleeping indicator
                }
            }
        }
    }
}