package ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import game.CommandResult;
import game.GameSession;
import model.Room;

public class MazeRunnerUI extends JFrame {
    private static final Color APP_BACKGROUND = new Color(241, 244, 246);
    private static final Color PANEL_BACKGROUND = Color.WHITE;
    private static final Color PANEL_SOFT = new Color(248, 250, 251);
    private static final Color HEADER_START = new Color(31, 48, 61);
    private static final Color HEADER_END = new Color(35, 89, 93);
    private static final Color TEXT_PRIMARY = new Color(28, 34, 38);
    private static final Color TEXT_MUTED = new Color(94, 105, 113);
    private static final Color ACCENT = new Color(36, 106, 114);
    private static final Color ACCENT_DARK = new Color(22, 70, 76);
    private static final Color WARNING = new Color(178, 82, 55);
    private static final Color AMBER = new Color(184, 129, 43);
    private static final Color SUCCESS = new Color(47, 129, 84);
    private static final Color VISITED = new Color(221, 241, 235);
    private static final Color CURRENT = new Color(255, 242, 196);
    private static final Color UNKNOWN = new Color(229, 234, 238);
    private static final Color PATH = new Color(174, 187, 193);

    private GameSession session;
    private final Map<String, RoomTile> roomTiles;
    private final List<JButton> commandButtons;

    private JLabel locationLabel;
    private JLabel threatLabel;
    private JLabel inventoryLabel;
    private JLabel enemyLabel;
    private JLabel movesLabel;
    private JLabel itemLabel;
    private JLabel exitsLabel;
    private JTextArea descriptionArea;
    private JTextArea logArea;
    private JTextField commandField;
    private JProgressBar threatMeter;
    private JButton northButton;
    private JButton southButton;
    private JButton eastButton;
    private JButton westButton;
    private JButton takeButton;
    private HouseMapPanel houseMapPanel;

    public MazeRunnerUI() {
        session = new GameSession();
        roomTiles = new LinkedHashMap<>();
        commandButtons = new ArrayList<>();

        configureWindow();
        setContentPane(buildContent());
        refresh();
        appendLog("Welcome to MazeRunner. Use the map, movement buttons, or command box to escape.");
    }

    private void configureWindow() {
        setTitle("MazeRunner - BFS Graph Adventure");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(1040, 680));
        setLocationRelativeTo(null);
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBackground(APP_BACKGROUND);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel main = new JPanel(new BorderLayout(14, 14));
        main.setOpaque(false);
        main.add(buildMapPanel(), BorderLayout.WEST);
        main.add(buildRoomPanel(), BorderLayout.CENTER);
        main.add(buildControlPanel(), BorderLayout.EAST);

        root.add(main, BorderLayout.CENTER);
        root.add(buildLogPanel(), BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildHeader() {
        GradientPanel header = new GradientPanel(new BorderLayout(14, 8));
        header.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel titleBlock = new JPanel(new BorderLayout(0, 3));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("MazeRunner");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));

        JLabel subtitle = new JLabel("Escape the house while The Watcher follows the shortest BFS path.");
        subtitle.setForeground(new Color(219, 232, 233));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        titleBlock.add(title, BorderLayout.NORTH);
        titleBlock.add(subtitle, BorderLayout.SOUTH);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headerActions.setOpaque(false);
        headerActions.add(createHeaderPill("Goal: Vault"));
        headerActions.add(createHeaderPill("Algorithm: BFS"));

        JButton restartButton = createHeaderButton("Restart");
        restartButton.addActionListener(event -> restartGame());
        headerActions.add(restartButton);

        header.add(titleBlock, BorderLayout.WEST);
        header.add(headerActions, BorderLayout.EAST);
        return header;
    }

    private JPanel buildMapPanel() {
        JPanel panel = createPanel(new BorderLayout(12, 12));
        panel.setPreferredSize(new Dimension(410, 450));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        heading.add(createSectionHeading("House Map"), BorderLayout.WEST);

        JLabel hint = new JLabel("Live graph view");
        hint.setForeground(TEXT_MUTED);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        heading.add(hint, BorderLayout.EAST);
        panel.add(heading, BorderLayout.NORTH);

        houseMapPanel = new HouseMapPanel();
        houseMapPanel.setOpaque(false);
        houseMapPanel.setBorder(new EmptyBorder(8, 4, 8, 4));

        addRoomTile(houseMapPanel, "Vault", 3, 0);
        addRoomTile(houseMapPanel, "Conservatory", 0, 1);
        addRoomTile(houseMapPanel, "Armory", 1, 1);
        addRoomTile(houseMapPanel, "East Corridor", 2, 1);
        addRoomTile(houseMapPanel, "Cellar", 3, 1);
        addRoomTile(houseMapPanel, "Kitchen", 0, 2);
        addRoomTile(houseMapPanel, "Gallery", 1, 2);
        addRoomTile(houseMapPanel, "Study", 2, 2);
        addRoomTile(houseMapPanel, "Foyer", 1, 3);

        panel.add(houseMapPanel, BorderLayout.CENTER);
        panel.add(buildLegend(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildLegend() {
        JPanel legend = new JPanel(new GridLayout(2, 2, 8, 6));
        legend.setOpaque(false);
        legend.add(createLegendItem(CURRENT, "Current room"));
        legend.add(createLegendItem(VISITED, "Visited room"));
        legend.add(createLegendItem(UNKNOWN, "Unvisited room"));
        legend.add(createLegendItem(WARNING, "Watcher border"));
        return legend;
    }

    private JLabel createLegendItem(Color color, String text) {
        JLabel label = new JLabel(text);
        label.setIcon(new ColorSwatch(color));
        label.setIconTextGap(7);
        label.setForeground(TEXT_MUTED);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }

    private void addRoomTile(JPanel mapGrid, String roomName, int x, int y) {
        Room room = findRoom(roomName);
        RoomTile tile = new RoomTile(room.getName(), room.getDescription());
        roomTiles.put(roomName, tile);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.insets = new Insets(8, 8, 8, 8);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        mapGrid.add(tile, constraints);
    }

    private JPanel buildRoomPanel() {
        JPanel panel = createPanel(new BorderLayout(14, 14));

        JPanel top = new JPanel(new BorderLayout(8, 4));
        top.setOpaque(false);

        JLabel label = new JLabel("Current Location");
        label.setForeground(TEXT_MUTED);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));

        locationLabel = new JLabel();
        locationLabel.setForeground(TEXT_PRIMARY);
        locationLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));

        top.add(label, BorderLayout.NORTH);
        top.add(locationLabel, BorderLayout.CENTER);
        panel.add(top, BorderLayout.NORTH);

        JPanel detailPanel = new JPanel(new BorderLayout(12, 12));
        detailPanel.setOpaque(false);

        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        descriptionArea.setForeground(TEXT_PRIMARY);
        descriptionArea.setBackground(PANEL_SOFT);
        descriptionArea.setBorder(new EmptyBorder(16, 16, 16, 16));

        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setBorder(new LineBorder(new Color(225, 231, 235)));
        detailPanel.add(descriptionScroll, BorderLayout.CENTER);

        JPanel facts = new JPanel(new GridLayout(1, 2, 10, 10));
        facts.setOpaque(false);
        itemLabel = createFactLabel();
        exitsLabel = createFactLabel();
        facts.add(itemLabel);
        facts.add(exitsLabel);
        detailPanel.add(facts, BorderLayout.SOUTH);

        panel.add(detailPanel, BorderLayout.CENTER);
        panel.add(buildCommandPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildCommandPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setOpaque(false);

        wrapper.add(buildMovementPanel(), BorderLayout.CENTER);

        JPanel commandBox = new JPanel(new BorderLayout(8, 8));
        commandBox.setOpaque(false);
        commandBox.add(createSectionHeading("Command Box"), BorderLayout.NORTH);

        commandField = new JTextField();
        commandField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        commandField.setForeground(TEXT_PRIMARY);
        commandField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(202, 213, 218)),
                new EmptyBorder(9, 10, 9, 10)));
        commandField.addActionListener(event -> submitTypedCommand());

        JButton submitButton = registerCommandButton(createPrimaryButton("Run"));
        submitButton.addActionListener(event -> submitTypedCommand());

        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setOpaque(false);
        inputRow.add(commandField, BorderLayout.CENTER);
        inputRow.add(submitButton, BorderLayout.EAST);

        commandBox.add(inputRow, BorderLayout.CENTER);
        wrapper.add(commandBox, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel buildMovementPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.setOpaque(false);
        wrapper.add(createSectionHeading("Movement"), BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(3, 3, 8, 8));
        buttons.setOpaque(false);

        northButton = registerCommandButton(createDirectionButton("North", "N"));
        southButton = registerCommandButton(createDirectionButton("South", "S"));
        eastButton = registerCommandButton(createDirectionButton("East", "E"));
        westButton = registerCommandButton(createDirectionButton("West", "W"));

        northButton.addActionListener(event -> submitCommand("north"));
        southButton.addActionListener(event -> submitCommand("south"));
        eastButton.addActionListener(event -> submitCommand("east"));
        westButton.addActionListener(event -> submitCommand("west"));

        buttons.add(createSpacer());
        buttons.add(northButton);
        buttons.add(createSpacer());
        buttons.add(westButton);
        buttons.add(createCenterMoveLabel());
        buttons.add(eastButton);
        buttons.add(createSpacer());
        buttons.add(southButton);
        buttons.add(createSpacer());

        wrapper.add(buttons, BorderLayout.CENTER);
        return wrapper;
    }

    private JLabel createCenterMoveLabel() {
        JLabel label = new JLabel("Move", SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(PANEL_SOFT);
        label.setForeground(TEXT_MUTED);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setBorder(new LineBorder(new Color(225, 231, 235)));
        return label;
    }

    private JPanel buildControlPanel() {
        JPanel panel = createPanel(new BorderLayout(12, 12));
        panel.setPreferredSize(new Dimension(280, 450));

        JPanel status = new JPanel(new GridLayout(5, 1, 8, 8));
        status.setOpaque(false);

        threatLabel = createFactLabel();
        inventoryLabel = createFactLabel();
        enemyLabel = createFactLabel();
        movesLabel = createFactLabel();
        JLabel objectiveLabel = createFactLabel();
        objectiveLabel.setText("<html><b>Objective</b><br>Collect the Brass Key and unlock the Vault.</html>");

        status.add(threatLabel);
        status.add(inventoryLabel);
        status.add(enemyLabel);
        status.add(movesLabel);
        status.add(objectiveLabel);

        JPanel threatPanel = new JPanel(new BorderLayout(6, 6));
        threatPanel.setOpaque(false);
        threatPanel.add(createSectionHeading("Game Status"), BorderLayout.NORTH);
        threatMeter = new JProgressBar(0, 100);
        threatMeter.setStringPainted(true);
        threatMeter.setFont(new Font("Segoe UI", Font.BOLD, 11));
        threatMeter.setForeground(ACCENT);
        threatMeter.setBackground(new Color(229, 234, 238));
        threatPanel.add(threatMeter, BorderLayout.SOUTH);

        JPanel statusWrapper = new JPanel(new BorderLayout(8, 8));
        statusWrapper.setOpaque(false);
        statusWrapper.add(threatPanel, BorderLayout.NORTH);
        statusWrapper.add(status, BorderLayout.CENTER);
        panel.add(statusWrapper, BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridLayout(6, 1, 8, 8));
        actions.setOpaque(false);

        takeButton = registerCommandButton(createPrimaryButton("Take Item"));
        JButton lookButton = registerCommandButton(createSecondaryButton("Look Around"));
        JButton mapButton = registerCommandButton(createSecondaryButton("Visited Rooms"));
        JButton inventoryButton = registerCommandButton(createSecondaryButton("Inventory"));
        JButton helpButton = registerCommandButton(createSecondaryButton("Help"));
        JButton quitButton = registerCommandButton(createDangerButton("Quit"));

        takeButton.addActionListener(event -> submitCommand("take"));
        lookButton.addActionListener(event -> submitCommand("look"));
        mapButton.addActionListener(event -> submitCommand("map"));
        inventoryButton.addActionListener(event -> submitCommand("inventory"));
        helpButton.addActionListener(event -> submitCommand("help"));
        quitButton.addActionListener(event -> submitCommand("quit"));

        actions.add(takeButton);
        actions.add(lookButton);
        actions.add(mapButton);
        actions.add(inventoryButton);
        actions.add(helpButton);
        actions.add(quitButton);

        panel.add(actions, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane buildLogPanel() {
        logArea = new JTextArea(6, 20);
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setForeground(TEXT_PRIMARY);
        logArea.setBackground(new Color(250, 251, 252));
        logArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(210, 219, 224)), "Event Log"));
        scrollPane.setPreferredSize(new Dimension(400, 145));
        return scrollPane;
    }

    private void submitTypedCommand() {
        String command = commandField.getText().trim();
        if (command.isEmpty()) {
            commandField.requestFocusInWindow();
            return;
        }
        commandField.setText("");
        submitCommand(command);
    }

    private void submitCommand(String command) {
        CommandResult result = session.processCommand(command);
        for (String message : result.getMessages()) {
            appendLog(message);
        }
        refresh();

        if (result.getStatus() == CommandResult.Status.WIN) {
            JOptionPane.showMessageDialog(this, "You escaped the house.", "MazeRunner", JOptionPane.INFORMATION_MESSAGE);
        } else if (result.getStatus() == CommandResult.Status.LOSS) {
            JOptionPane.showMessageDialog(this, "The Watcher caught you.", "MazeRunner", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void restartGame() {
        session = new GameSession();
        logArea.setText("");
        appendLog("New game started. Find the Brass Key, unlock the Vault, and escape.");
        refresh();
        commandField.requestFocusInWindow();
    }

    private void refresh() {
        Room currentRoom = session.getPlayer().getCurrentRoom();
        locationLabel.setText(currentRoom.getName());
        descriptionArea.setText(currentRoom.getDescription());
        itemLabel.setText("<html><b>Room Item</b><br>" + getItemText(currentRoom) + "</html>");
        exitsLabel.setText("<html><b>Available Exits</b><br>" + currentRoom.getExitSummary() + "</html>");
        threatLabel.setText("<html><b>Threat Level</b><br>" + session.describeThreatLevel() + "</html>");
        threatLabel.setForeground(getThreatColor());
        inventoryLabel.setText("<html><b>Inventory</b><br>" + session.getPlayer().getInventorySummary() + "</html>");
        enemyLabel.setText("<html><b>Watcher Location</b><br>" + session.getEnemy().getCurrentRoom().getName() + "</html>");
        movesLabel.setText("<html><b>Moves</b><br>" + session.getPlayerMoveCount() + " room moves</html>");
        updateThreatMeter();

        for (JButton button : commandButtons) {
            button.setEnabled(session.isRunning());
        }

        commandField.setEnabled(session.isRunning());
        northButton.setEnabled(session.isRunning() && currentRoom.getExits().containsKey("north"));
        southButton.setEnabled(session.isRunning() && currentRoom.getExits().containsKey("south"));
        eastButton.setEnabled(session.isRunning() && currentRoom.getExits().containsKey("east"));
        westButton.setEnabled(session.isRunning() && currentRoom.getExits().containsKey("west"));
        takeButton.setEnabled(session.isRunning() && currentRoom.getItemHere() != null);

        for (Map.Entry<String, RoomTile> entry : roomTiles.entrySet()) {
            entry.getValue().refresh(session);
        }

        houseMapPanel.repaint();
    }

    private void updateThreatMeter() {
        int distance = session.getEnemyDistance();
        int value;
        if (distance <= 0) {
            value = 100;
        } else if (distance == 1) {
            value = 88;
        } else if (distance == 2) {
            value = 62;
        } else {
            value = 28;
        }

        threatMeter.setValue(value);
        threatMeter.setString(value >= 80 ? "Danger" : value >= 50 ? "Close" : "Safe");
        threatMeter.setForeground(getThreatColor());
    }

    private String getItemText(Room room) {
        if (room.getItemHere() == null) {
            return "No item in this room.";
        }
        return room.getItemHere();
    }

    private Color getThreatColor() {
        int distance = session.getEnemyDistance();
        if (distance <= 1) {
            return WARNING;
        }
        if (distance == 2) {
            return AMBER;
        }
        return SUCCESS;
    }

    private void appendLog(String message) {
        if (!message.isBlank()) {
            logArea.append(message + "\n\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
    }

    private Room findRoom(String name) {
        for (Room room : session.getMap().getAllRooms()) {
            if (room.getName().equals(name)) {
                return room;
            }
        }
        throw new IllegalArgumentException("Room not found: " + name);
    }

    private JPanel createPanel(BorderLayout layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(216, 225, 230)),
                new EmptyBorder(14, 14, 14, 14)));
        return panel;
    }

    private JLabel createSectionHeading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JLabel createFactLabel() {
        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setBackground(PANEL_SOFT);
        label.setForeground(TEXT_PRIMARY);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(224, 231, 235)),
                new EmptyBorder(9, 10, 9, 10)));
        return label;
    }

    private JLabel createHeaderPill(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(new Color(238, 246, 247));
        label.setForeground(ACCENT_DARK);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 213, 215)),
                new EmptyBorder(7, 10, 7, 10)));
        return label;
    }

    private JButton createHeaderButton(String text) {
        JButton button = createSecondaryButton(text);
        button.setBackground(Color.WHITE);
        return button;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(ACCENT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 13, 10, 13));
        return button;
    }

    private JButton createDirectionButton(String text, String shortcut) {
        JButton button = createPrimaryButton("<html><center>" + text + "<br><span style='font-size:9px'>" + shortcut
                + "</span></center></html>");
        button.setPreferredSize(new Dimension(90, 54));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(Color.WHITE);
        button.setForeground(ACCENT_DARK);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(185, 204, 210)),
                new EmptyBorder(9, 12, 9, 12)));
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = createSecondaryButton(text);
        button.setForeground(WARNING);
        return button;
    }

    private JButton registerCommandButton(JButton button) {
        commandButtons.add(button);
        return button;
    }

    private JLabel createSpacer() {
        JLabel label = new JLabel("");
        label.setPreferredSize(new Dimension(90, 54));
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                UIManager.getLookAndFeelDefaults();
            }
            new MazeRunnerUI().setVisible(true);
        });
    }

    private static class GradientPanel extends JPanel {
        GradientPanel(BorderLayout layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, HEADER_START, getWidth(), getHeight(), HEADER_END));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private class HouseMapPanel extends JPanel {
        HouseMapPanel() {
            super(new GridBagLayout());
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(PATH);

            Set<String> drawnEdges = new HashSet<>();
            for (Room room : session.getMap().getAllRooms()) {
                RoomTile fromTile = roomTiles.get(room.getName());
                if (fromTile == null) {
                    continue;
                }

                for (Room neighbor : room.getExits().values()) {
                    RoomTile toTile = roomTiles.get(neighbor.getName());
                    if (toTile == null) {
                        continue;
                    }

                    String edgeKey = makeEdgeKey(room, neighbor);
                    if (drawnEdges.add(edgeKey)) {
                        int x1 = fromTile.getX() + fromTile.getWidth() / 2;
                        int y1 = fromTile.getY() + fromTile.getHeight() / 2;
                        int x2 = toTile.getX() + toTile.getWidth() / 2;
                        int y2 = toTile.getY() + toTile.getHeight() / 2;
                        g2.drawLine(x1, y1, x2, y2);
                    }
                }
            }
            g2.dispose();
        }

        private String makeEdgeKey(Room a, Room b) {
            String first = a.getName().compareTo(b.getName()) <= 0 ? a.getName() : b.getName();
            String second = a.getName().compareTo(b.getName()) <= 0 ? b.getName() : a.getName();
            return first + "|" + second;
        }
    }

    private class RoomTile extends JPanel {
        private final String roomName;
        private final JLabel nameLabel;
        private final JLabel statusLabel;

        RoomTile(String roomName, String description) {
            this.roomName = roomName;
            setLayout(new BorderLayout(4, 4));
            setPreferredSize(new Dimension(88, 64));
            setBorder(new LineBorder(UNKNOWN));
            setToolTipText(description);

            nameLabel = new JLabel(roomName, SwingConstants.CENTER);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
            nameLabel.setForeground(TEXT_PRIMARY);

            statusLabel = new JLabel("Unvisited", SwingConstants.CENTER);
            statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            statusLabel.setForeground(TEXT_MUTED);

            add(nameLabel, BorderLayout.CENTER);
            add(statusLabel, BorderLayout.SOUTH);
        }

        void refresh(GameSession session) {
            Room room = findRoom(roomName);
            boolean current = session.getPlayer().getCurrentRoom() == room;
            boolean enemy = session.getEnemy().getCurrentRoom() == room;

            if (current) {
                setBackground(CURRENT);
                statusLabel.setText("You are here");
            } else if (room.isVisited()) {
                setBackground(VISITED);
                statusLabel.setText("Visited");
            } else {
                setBackground(UNKNOWN);
                statusLabel.setText("Unvisited");
            }

            if (room.getItemHere() != null) {
                statusLabel.setText("Item");
            }
            if (room == session.getMap().getGoalRoom()) {
                statusLabel.setText(room.isVisited() ? "Vault" : "Locked");
            }
            if (enemy && current) {
                statusLabel.setText("Caught");
            } else if (enemy) {
                statusLabel.setText("Watcher");
            }

            if (enemy) {
                setBorder(new LineBorder(WARNING, 3));
            } else if (current) {
                setBorder(new LineBorder(AMBER, 2));
            } else if (room.isVisited()) {
                setBorder(new LineBorder(new Color(150, 194, 179), 2));
            } else {
                setBorder(new LineBorder(new Color(202, 212, 218)));
            }
        }
    }

    private static class ColorSwatch implements javax.swing.Icon {
        private final Color color;

        ColorSwatch(Color color) {
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return 13;
        }

        @Override
        public int getIconHeight() {
            return 13;
        }

        @Override
        public void paintIcon(java.awt.Component component, Graphics graphics, int x, int y) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setColor(color);
            g2.fillRoundRect(x, y, getIconWidth(), getIconHeight(), 4, 4);
            g2.setColor(new Color(130, 142, 150));
            g2.drawRoundRect(x, y, getIconWidth(), getIconHeight(), 4, 4);
            g2.dispose();
        }
    }
}
