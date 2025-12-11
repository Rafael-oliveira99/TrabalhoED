package Game;

import Collections.ListasIterador.Classes.LinkedUnorderedList;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 * The Graphical User Interface for the game "Labirinto da Glória".
 * <p>
 * This class is responsible for rendering the game state visually using Java
 * Swing.
 * It draws the maze structure (rooms and corridors), player positions, and
 * animations
 * without relying on external graph libraries, strictly adhering to the project
 * constraints.
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

    // --- HOVER STATE para destacar conexões ---
    private Room hoveredRoom = null;

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

        // Auto-fix overlapping rooms
        optimizeLayout();

        setVisible(true);
    }

    /**
     * Applies a simple force-directed layout step to spread out overlapping rooms.
     */
    private void optimizeLayout() {
        int iterations = 400; // mais iterações para espalhar bem as salas
        int minDistance = 200; // distância maior para visualização clara
        int width = 1100; // slightly less than window width
        int height = 700; // slightly less than window height

        // Using custom collection to comply with requirements
        LinkedUnorderedList<Room> roomList = new LinkedUnorderedList<>();
        Iterator<Room> it = map.getRooms();
        while (it.hasNext())
            roomList.addToRear(it.next());

        for (int k = 0; k < iterations; k++) {
            // Convert to array for O(1) access during O(N^2) loop
            Object[] rooms = new Object[roomList.size()];
            int idx = 0;
            for (Room r : roomList)
                rooms[idx++] = r;

            for (int i = 0; i < rooms.length; i++) {
                Room r1 = (Room) rooms[i];

                // FIX: Force Treasure room to center
                if (r1.getType().equals("TREASURE") || r1.getId().equalsIgnoreCase("Tesouro")) {
                    r1.setX(width / 2);
                    r1.setY(height / 2);
                    continue; // Do not move it with forces
                }

                for (int j = i + 1; j < rooms.length; j++) {
                    Room r2 = (Room) rooms[j];

                    double dx = r1.getX() - r2.getX();
                    double dy = r1.getY() - r2.getY();
                    double dist = Math.sqrt(dx * dx + dy * dy);

                    if (dist < minDistance) {
                        if (dist < 0.1) {
                            dx = Math.random() - 0.5;
                            dy = Math.random() - 0.5;
                            dist = 1;
                        }

                        double force = (minDistance - dist) / 1.5; // aumentar força de repulsão
                        double fx = (dx / dist) * force;
                        double fy = (dy / dist) * force;

                        r1.setX((int) (r1.getX() + fx));
                        r1.setY((int) (r1.getY() + fy));

                        // Only move r2 if it's not the treasure
                        if (!r2.getType().equals("TREASURE") && !r2.getId().equalsIgnoreCase("Tesouro")) {
                            r2.setX((int) (r2.getX() - fx));
                            r2.setY((int) (r2.getY() - fy));
                        }
                    }
                }

                // Keep in bounds
                if (r1.getX() < 50)
                    r1.setX(50);
                if (r1.getX() > width)
                    r1.setX(width);
                if (r1.getY() < 50)
                    r1.setY(50);
                if (r1.getY() > height)
                    r1.setY(height);
            }
        }
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
            // Dark background, but we will paint over it with a gradient
            setBackground(new Color(20, 20, 25));

            // Adicionar mouse listener para hover sobre salas
            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    Room oldHovered = hoveredRoom;
                    hoveredRoom = null;

                    // Verificar se o rato está sobre alguma sala
                    Iterator<Room> it = map.getRooms();
                    while (it.hasNext()) {
                        Room r = it.next();
                        int dx = e.getX() - r.getX();
                        int dy = e.getY() - r.getY();
                        double dist = Math.sqrt(dx * dx + dy * dy);

                        if (dist < 30) { // raio de detecção maior que a sala
                            hoveredRoom = r;
                            break;
                        }
                    }

                    // Repintar se mudou a sala com hover
                    if (oldHovered != hoveredRoom) {
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Enable high-quality rendering
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int w = getWidth();
            int h = getHeight();

            // --- LAYER 0: BACKGROUND (Vignette) ---
            // Center is slightly lighter, corners are very dark
            Point2D center = new Point2D.Float(w / 2.0f, h / 2.0f);
            float radius = Math.max(w, h);
            float[] dist = { 0.0f, 1.0f };
            Color[] colors = { new Color(45, 45, 55), new Color(10, 10, 15) };
            RadialGradientPaint bgPaint = new RadialGradientPaint(center, radius, dist, colors);
            g2.setPaint(bgPaint);
            g2.fillRect(0, 0, w, h);

            // Safety check: Ensure map has rooms
            if (!map.getRooms().hasNext())
                return;

            // --- LAYER 1: CORRIDORS (Lines with Glow) ---
            g2.setStroke(new BasicStroke(3));

            Iterator<Room> itFrom = map.getRooms();
            while (itFrom.hasNext()) {
                Room r1 = itFrom.next();
                Iterator<Room> itTo = map.getRooms();
                while (itTo.hasNext()) {
                    Room r2 = itTo.next();

                    if (map.isNeighbor(r1, r2)) {
                        double weight = map.getWeight(r1, r2);

                        // Verificar se esta conexão deve ser destacada (hover)
                        boolean isHighlighted = (hoveredRoom == r1 || hoveredRoom == r2);

                        // Draw Shadow/Glow first
                        if (weight > 100) {
                            // Locked: Red Glow
                            g2.setColor(new Color(255, 0, 0, isHighlighted ? 150 : 50));
                            g2.setStroke(new BasicStroke(isHighlighted ? 8 : 6));
                            g2.drawLine(r1.getX(), r1.getY(), r2.getX(), r2.getY());

                            // Core Line
                            g2.setColor(new Color(200, 50, 50));
                            g2.setStroke(new BasicStroke(isHighlighted ? 4 : 2));
                        } else {
                            // Open: Subtle Blue/Gray (ou brilhante se hover)
                            if (isHighlighted) {
                                // Destacar com cor brilhante
                                g2.setColor(new Color(100, 255, 255, 200));
                                g2.setStroke(new BasicStroke(5));
                            } else {
                                g2.setColor(new Color(100, 120, 140, 100));
                                g2.setStroke(new BasicStroke(2));
                            }
                        }
                        g2.drawLine(r1.getX(), r1.getY(), r2.getX(), r2.getY());
                    }
                }
            }

            // --- LAYER 2: ROOMS (Spheres) ---
            Iterator<Room> itDraw = map.getRooms();
            while (itDraw.hasNext()) {
                Room r = itDraw.next();
                drawRoom(g2, r);
            }

            // --- LAYER 3: PLAYERS ---
            Iterator<Player> playerIt = players.iterator();
            while (playerIt.hasNext()) {
                Player p = playerIt.next();
                drawPlayer(g2, p);
            }
        }

        private void drawRoom(Graphics2D g2, Room r) {
            int x = r.getX();
            int y = r.getY();
            int size = 50;
            int shadowOffset = 5;

            // Aumentar tamanho se estiver em hover
            boolean isHovered = (r == hoveredRoom);
            if (isHovered) {
                size = 60; // sala fica maior ao passar o rato
            }

            // 1. Drop Shadow
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillOval(x - size / 2 + shadowOffset, y - size / 2 + shadowOffset, size, size);

            // 2. Sphere Gradient
            Color baseColor;
            Color highlightColor;

            if (r.getType().equals("TREASURE")) {
                baseColor = new Color(218, 165, 32); // Goldenrod
                highlightColor = new Color(255, 255, 224); // Light Yellow
            } else if (r.getType().equals("ENTRANCE")) {
                baseColor = new Color(34, 139, 34); // Forest Green
                highlightColor = new Color(144, 238, 144); // Light Green
            } else {
                baseColor = new Color(70, 130, 180); // Steel Blue
                highlightColor = new Color(176, 224, 230); // Powder Blue
            }

            // Gradient focused slightly top-left for 3D effect
            Point2D center = new Point2D.Float(x - size / 6.0f, y - size / 6.0f);
            float radius = size / 1.8f;
            float[] dist = { 0.0f, 1.0f };
            Color[] colors = { highlightColor, baseColor };
            RadialGradientPaint spherePaint = new RadialGradientPaint(center, radius, dist, colors);

            g2.setPaint(spherePaint);
            g2.fillOval(x - size / 2, y - size / 2, size, size);

            // 3. Border (Subtle)
            g2.setColor(new Color(255, 255, 255, 150));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(x - size / 2, y - size / 2, size, size);

            // 4. Label Background (Rounded Rect)
            String label = r.getId();
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(label);
            int textH = fm.getHeight();

            int labelX = x - textW / 2;
            int labelY = y - size / 2 - 10;

            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRoundRect(labelX - 5, labelY - textH + 3, textW + 10, textH, 10, 10);

            g2.setColor(Color.WHITE);
            g2.drawString(label, labelX, labelY);

            // 5. Badges
            if (!r.getInteraction().equals("none")) {
                int badgeSize = 18;
                int badgeX = x + size / 2 - 10;
                int badgeY = y - size / 2 - 5;

                if (r.getInteraction().equals("lever")) {
                    g2.setColor(new Color(255, 140, 0)); // Dark Orange
                    g2.fillOval(badgeX, badgeY, badgeSize, badgeSize);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                    g2.drawString("!", badgeX + 6, badgeY + 14);
                } else if (r.getInteraction().equals("enigma")) {
                    g2.setColor(new Color(148, 0, 211)); // Violet
                    g2.fillOval(badgeX, badgeY, badgeSize, badgeSize);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                    g2.drawString("?", badgeX + 5, badgeY + 14);
                }
            }
        }

        private void drawPlayer(Graphics2D g2, Player p) {
            int px = 0, py = 0;

            // Interpolation Logic
            if (p == animatingPlayer && animStartRoom != null && animEndRoom != null) {
                int sx = animStartRoom.getX();
                int sy = animStartRoom.getY();
                int ex = animEndRoom.getX();
                int ey = animEndRoom.getY();
                px = (int) (sx + (ex - sx) * animProgress);
                py = (int) (sy + (ey - sy) * animProgress);
            } else {
                if (p.getCurrentRoom() != null) {
                    px = p.getCurrentRoom().getX();
                    py = p.getCurrentRoom().getY();
                }
            }

            // Draw Player as a smaller sphere
            int size = 26;

            // Shadow
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillOval(px - size / 2 + 3, py - size / 2 + 3, size, size);

            // Sphere
            Point2D center = new Point2D.Float(px - size / 6.0f, py - size / 6.0f);
            float radius = size / 1.8f;
            float[] dist = { 0.0f, 1.0f };
            Color base = p.getColor();
            Color highlight = Color.WHITE;
            Color[] colors = { highlight, base };
            RadialGradientPaint pPaint = new RadialGradientPaint(center, radius, dist, colors);

            g2.setPaint(pPaint);
            g2.fillOval(px - size / 2, py - size / 2, size, size);

            // Border
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1));
            g2.drawOval(px - size / 2, py - size / 2, size, size);

            // Name Label
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.drawString(p.getName(), px - 15, py + 25);

            // Status Effects
            if (p.getSkipTurns() > 0) {
                g2.setColor(new Color(255, 100, 100));
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                g2.drawString("Zzz", px + 10, py - 10);
            }
        }
    }
}