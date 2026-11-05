import javax.swing.*;
import javax.swing.border.Border;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class TSS extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String MENU = "MENU";
    private static final String STAGE = "STAGE";
    private static final String RESULTS = "RESULTS";
    private static final String REVIEW = "REVIEW";
    private static final String[] STAGE_NAMES = {"THINK", "STACK", "SOLVE"};
    private static final int QUESTIONS_PER_GAME = 10;
    private static final Question[][] QUESTION_BANK = {
            {
                    new Question("ARRAYS", "Given [5, 2, 8, 1, 9], what value should you identify?",
                            new String[]{"A. Smallest number", "B. Largest number", "C. Average"},
                            1, "Look for the greatest value in the array.", "Maximum means the greatest value."),
                    new Question("ARRAYS", "Which task asks for an extreme value in [4, 7, 2]?",
                            new String[]{"A. Find the maximum", "B. Count the values", "C. Find the sum"},
                            0, "An extreme value is either the smallest or largest value.", "Maximum and minimum are extreme values."),
                    new Question("ARRAYS", "To find the maximum in an array, what are you looking for?",
                            new String[]{"A. The value that appears twice", "B. The smallest value", "C. The greatest value"},
                            2, "Maximum means the greatest value.", "A maximum is the largest value."),
                    new Question("STACKS", "Which structure follows Last In, First Out (LIFO)?",
                            new String[]{"A. Queue", "B. Stack", "C. Tree"},
                            1, "Think of a stack of plates.", "A stack removes the most recently added item first."),
                    new Question("QUEUES", "Which structure normally serves items in arrival order?",
                            new String[]{"A. Queue", "B. Stack", "C. Binary tree"},
                            0, "Think of a line of people waiting.", "A queue is First In, First Out (FIFO)."),
                    new Question("TREES", "What is the topmost node in a tree called?",
                            new String[]{"A. Leaf", "B. Root", "C. Edge"},
                            1, "It is the starting node of the tree.", "The root is the topmost node."),
                    new Question("SORTING", "What is the goal of sorting a list?",
                            new String[]{"A. Arrange values in an order", "B. Remove all values", "C. Duplicate every value"},
                            0, "A common order is smallest to largest.", "Sorting arranges values according to a chosen order.")
            },
            {
                    new Question("ARRAYS", "How should you find the largest number in an array?",
                            new String[]{"A. Multiply every number", "B. Compare each number", "C. Count the numbers"},
                            1, "Compare each value with the largest value seen so far.", "A scan compares each value with the current maximum."),
                    new Question("ARRAYS", "Which strategy can find the maximum in one pass?",
                            new String[]{"A. Track the current largest value", "B. Add all values", "C. Remove every other value"},
                            0, "Keep a current maximum and update it when a larger value appears.", "Update the tracked maximum whenever a larger value is found."),
                    new Question("ARRAYS", "While scanning an array for its maximum, what should you do?",
                            new String[]{"A. Count each item", "B. Compare each item with the current maximum", "C. Multiply each item"},
                            1, "A comparison tells you whether to update the current maximum.", "Compare each item and update only when it is larger."),
                    new Question("STACKS", "Which operation adds an item to the top of a stack?",
                            new String[]{"A. Enqueue", "B. Push", "C. Dequeue"},
                            1, "It is the opposite of pop.", "Push adds an item to a stack."),
                    new Question("QUEUES", "Which operation removes the next item from a queue?",
                            new String[]{"A. Pop", "B. Push", "C. Dequeue"},
                            2, "It removes the item that has waited the longest.", "Dequeue removes the front item from a queue."),
                    new Question("TREES", "In a binary tree, how many children can a node have at most?",
                            new String[]{"A. Two", "B. Three", "C. Unlimited"},
                            0, "The prefix 'bi' means two.", "A binary tree node has at most two children."),
                    new Question("SORTING", "In a sorted array, where is the minimum usually found?",
                            new String[]{"A. At the beginning", "B. In the middle only", "C. At the end"},
                            0, "Assume ascending order.", "In ascending order, the smallest value comes first.")
            },
            {
                    new Question("ARRAYS", "Array: [5, 2, 8, 1, 9]. What is the largest number?",
                            new String[]{"5", "8", "9"},
                            2, "Compare 5, 2, 8, 1, and 9.", "9 is greater than 8, 5, 2, and 1."),
                    new Question("ARRAYS", "What is the maximum value in [3, 10, 6, 4]?",
                            new String[]{"6", "10", "4"},
                            1, "Compare the values; 10 is the greatest.", "10 is greater than 3, 6, and 4."),
                    new Question("ARRAYS", "You scan [7, 1, 12, 5]. What is the largest value?",
                            new String[]{"7", "5", "12"},
                            2, "Compare all four values.", "12 is greater than each of the other values."),
                    new Question("STACKS", "A stack receives push(4), then push(9). What does pop() return?",
                            new String[]{"4", "9", "Both values"},
                            1, "Pop removes the most recently pushed value.", "The last pushed item, 9, is removed first."),
                    new Question("QUEUES", "A queue receives enqueue(A), then enqueue(B). What leaves first?",
                            new String[]{"A", "B", "Neither"},
                            0, "A queue serves the earliest arrival first.", "A was enqueued before B, so A leaves first."),
                    new Question("TREES", "A binary tree node has children 2 and 3. What is its maximum number of children?",
                            new String[]{"1", "2", "3"},
                            1, "A binary node may have a left and a right child.", "A binary tree node has at most two children."),
                    new Question("SORTING", "After ascending sort, what is the order of [3, 1, 2]?",
                            new String[]{"[3, 2, 1]", "[1, 2, 3]", "[2, 1, 3]"},
                            1, "Ascending means smallest to largest.", "The ascending order is [1, 2, 3].")
            }
    };

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new GradientPanel();
    private final Preferences preferences = Preferences.userNodeForPackage(TSS.class);
    private final Random random = new Random();

    private final Color panelColor = new Color(28, 43, 73);
    private final Color panelSoft = new Color(18, 30, 52);
    private final Color cyan = new Color(0, 212, 220);
    private final Color cyanSoft = new Color(129, 240, 255);
    private final Color green = new Color(92, 206, 128);
    private final Color red = new Color(255, 102, 109);
    private final Color white = Color.WHITE;

    private int score;
    private int lives;
    private int streak;
    private int bestStreak;
    private int totalCorrect;
    private int totalAnswered;
    private int currentStage;
    private int questionNumber;
    private int hintsRemaining;
    private int hintsUsed;
    private int timeLeft;
    private int highScore;
    private boolean newRecord;
    private boolean storageWarningShown;
    private boolean soundEnabled = true;
    private boolean dailyChallenge;
    private boolean dailyChallengeCompleted;
    private int dailySeed;
    private int selectedAnswerIndex = -1;
    private int timerSeconds = 15;
    private Difficulty difficulty = Difficulty.MEDIUM;
    private Question currentQuestion;
    private final List<Question> missedQuestions = new ArrayList<>();
    private final Deque<Question>[] questionDecks = createQuestionDecks();
    private final List<String> unlockedAchievements = new ArrayList<>();
    private Timer questionTimer;
    private Timer feedbackTimer;

    private JComboBox<Difficulty> difficultyChoice;
    private JSpinner timerChoice;
    private JLabel menuBestScore;
    private JLabel timerLabel;
    private JLabel progressLabel;
    private JLabel streakLabel;
    private JLabel feedbackLabel;
    private JLabel hintLabel;
    private JProgressBar progressBar;
    private JButton hintButton;
    private JButton pauseButton;
    private JButton[] answerButtons;
    private JPanel stagePanel;
    private JPanel resultsPanel;
    private GraphicsDevice screenDevice;
    private Rectangle windowedBounds;
    private boolean fullScreen;

    @SuppressWarnings("unchecked")
    private static Deque<Question>[] createQuestionDecks() {
        return (Deque<Question>[]) new Deque<?>[STAGE_NAMES.length];
    }

    public TSS() {
        try {
            soundEnabled = preferences.getBoolean("soundEnabled", true);
            timerSeconds = Math.max(5, Math.min(120, preferences.getInt("timerSeconds", 15)));
            dailyChallengeCompleted = isDailyChallengeCompleted();
        } catch (SecurityException exception) {
            reportStorageProblem("read game settings", exception);
        }
        setTitle("THINK, STACK, SOLVE!");
        setSize(900, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        mainPanel.setLayout(cardLayout);
        installFullScreenKeys();
        add(mainPanel);
        showMenu();
    }

    private void installFullScreenKeys() {
        mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "toggleFullScreen");
        mainPanel.getActionMap().put("toggleFullScreen", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                if (fullScreen) {
                    exitFullScreen();
                } else {
                    enterFullScreen();
                }
            }
        });
        mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "captureScreenshot");
        mainPanel.getActionMap().put("captureScreenshot", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                captureScreenshot();
            }
        });
        mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exitFullScreen");
        mainPanel.getActionMap().put("exitFullScreen", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                if (fullScreen) {
                    exitFullScreen();
                }
            }
        });
    }

    private void captureScreenshot() {
        try {
            Point location = getLocationOnScreen();
            Rectangle bounds = new Rectangle(location.x, location.y, getWidth(), getHeight());
            BufferedImage screenshot = new Robot().createScreenCapture(bounds);
            Path screenshotDirectory = Paths.get(System.getProperty("user.home"),
                    "Downloads", "TSS Screenshots");
            Files.createDirectories(screenshotDirectory);
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            Path screenshotFile = screenshotDirectory.resolve("TSS_" + timestamp + ".png");
            if (!ImageIO.write(screenshot, "png", screenshotFile.toFile())) {
                throw new IOException("No PNG image writer is available.");
            }
            String message = "Screenshot saved to " + screenshotFile.toAbsolutePath();
            setTitle(message);
            System.out.println(message);
        } catch (AWTException | IOException | SecurityException | IllegalComponentStateException exception) {
            showStyledMessage("SCREENSHOT FAILED",
                    "The screenshot could not be saved.\n" + exception.getMessage());
        }
    }

    private void enterFullScreen() {
        if (fullScreen) {
            return;
        }
        try {
            screenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if (!screenDevice.isFullScreenSupported()) {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                return;
            }

            windowedBounds = getBounds();
            dispose();
            setUndecorated(true);
            setVisible(true);
            screenDevice.setFullScreenWindow(this);
            fullScreen = true;
        } catch (RuntimeException exception) {
            System.err.println("Unable to enter full-screen mode: " + exception.getMessage());
            if (screenDevice != null && screenDevice.getFullScreenWindow() == this) {
                screenDevice.setFullScreenWindow(null);
            }
            dispose();
            setUndecorated(false);
            setVisible(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            fullScreen = false;
        }
    }

    private void exitFullScreen() {
        if (!fullScreen) {
            return;
        }
        try {
            screenDevice.setFullScreenWindow(null);
        } finally {
            dispose();
            setUndecorated(false);
            setExtendedState(JFrame.NORMAL);
            if (windowedBounds != null) {
                setBounds(windowedBounds);
            }
            setVisible(true);
            fullScreen = false;
        }
    }

    private JPanel createMenu() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int cardWidth = Math.max(520, Math.min(900, screen.width - 64));
        JPanel panel = createStagePanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel card = new RoundedPanel(panelSoft, 30);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        int horizontalPadding = Math.max(28, Math.min(64, cardWidth / 12));
        int contentWidth = cardWidth - horizontalPadding * 2;
        int actionWidth = Math.max(200, (contentWidth - 12) / 2);
        card.setBorder(BorderFactory.createEmptyBorder(30, horizontalPadding, 24, horizontalPadding));
        card.setPreferredSize(new Dimension(cardWidth, Math.min(780, screen.height - 48)));
        card.setMaximumSize(new Dimension(cardWidth, screen.height - 48));

        JLabel title = createTitle("<html><div style='text-align:center;'>THINK, STACK,<br> SOLVE!</div></html>");
        title.setFont(new Font("Arial", Font.BOLD, 48));
        JLabel subtitle = createText("Think it through. Plan. Solve.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 20));

        difficultyChoice = new JComboBox<>(Difficulty.values());
        difficultyChoice.setSelectedItem(difficulty);
        difficultyChoice.setFont(new Font("Arial", Font.BOLD, 16));
        difficultyChoice.setForeground(white);
        difficultyChoice.setBackground(panelColor);
        difficultyChoice.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        difficultyChoice.addActionListener(e -> {
            difficulty = (Difficulty) difficultyChoice.getSelectedItem();
            updateMenuBestScore();
        });

        timerChoice = new JSpinner(new SpinnerNumberModel(timerSeconds, 5, 120, 1));
        timerChoice.setFont(new Font("Arial", Font.BOLD, 16));
        timerChoice.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        timerChoice.addChangeListener(e -> {
            timerSeconds = (Integer) timerChoice.getValue();
            saveTimerSetting();
            updateMenuBestScore();
        });

        menuBestScore = createText("");
        menuBestScore.setFont(new Font("Arial", Font.BOLD, 20));
        menuBestScore.setForeground(cyan);
        updateMenuBestScore();

        JCheckBox soundToggle = new JCheckBox("Sound effects", soundEnabled);
        soundToggle.setOpaque(false);
        soundToggle.setForeground(white);
        soundToggle.setFont(new Font("Arial", Font.PLAIN, 16));
        soundToggle.setFocusPainted(false);
        soundToggle.addActionListener(e -> setSoundEnabled(soundToggle.isSelected()));

        JButton play = createButton("PLAY GAME");
        JButton daily = createButton(dailyChallengeCompleted ? "DAILY CHALLENGE - COMPLETED" : "DAILY CHALLENGE");
        daily.setEnabled(!dailyChallengeCompleted);
        JButton instructions = createButton("INSTRUCTIONS");
        JButton achievements = createButton("ACHIEVEMENTS");
        JButton settingsButton = createButton("SETTINGS");
        JButton exit = createButton("EXIT");
        styleMenuButton(play, true);
        styleMenuButton(daily, false);
        styleMenuButton(instructions, false);
        styleMenuButton(achievements, false);
        styleMenuButton(settingsButton, false);
        styleMenuButton(exit, false);
        play.setFont(new Font("Arial", Font.BOLD, 25));
        play.setPreferredSize(new Dimension(contentWidth, 76));
        play.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        daily.setPreferredSize(new Dimension(actionWidth, 58));
        instructions.setPreferredSize(new Dimension(actionWidth, 58));
        achievements.setPreferredSize(new Dimension(actionWidth, 58));
        settingsButton.setPreferredSize(new Dimension(actionWidth, 58));
        if (actionWidth < 260) {
            daily.setFont(new Font("Arial", Font.BOLD, 13));
            instructions.setFont(new Font("Arial", Font.BOLD, 13));
            achievements.setFont(new Font("Arial", Font.BOLD, 13));
            settingsButton.setFont(new Font("Arial", Font.BOLD, 13));
        }
        exit.setMaximumSize(new Dimension(180, 42));
        exit.setPreferredSize(new Dimension(180, 42));
        play.addActionListener(e -> {
            dailyChallenge = false;
            resetGame();
        });
        daily.addActionListener(e -> startDailyChallenge());
        achievements.addActionListener(e -> showAchievements());
        settingsButton.addActionListener(e -> showSoundSettingsDialog());
        instructions.addActionListener(e -> showStyledMessage("How to Play",
                        "Answer 10 questions across the THINK, STACK, and SOLVE stages.\n" +
                        "Questions cover arrays, stacks, queues, trees, and sorting.\n" +
                        "A round has 10 questions. Choose difficulty for lives and set the timer in seconds.\n" +
                        "Use one hint per question (-5 points) and build streaks.\n" +
                        "Pause during a round to resume, change sound, or return to the menu.\n" +
                        "Daily challenges use the same questions for everyone on the same day.\n" +
                        "Press F12 to save a screenshot to your Downloads/TSS Screenshots folder."));
        exit.addActionListener(e -> System.exit(0));

        JPanel gameOptions = new JPanel(new GridLayout(1, 2, 16, 0));
        gameOptions.setOpaque(false);
        gameOptions.add(createMenuSetting("DIFFICULTY  ·  LIVES", difficultyChoice));
        gameOptions.add(createMenuSetting("TIME LIMIT  ·  SECONDS", timerChoice));

        JPanel actions = new JPanel(new GridLayout(2, 2, 12, 12));
        actions.setOpaque(false);
        actions.add(daily);
        actions.add(achievements);
        actions.add(instructions);
        actions.add(settingsButton);

        for (Component component : new Component[]{
                title, subtitle, gameOptions, menuBestScore, soundToggle, play, actions, exit
        }) {
            if (component instanceof JComponent) {
                ((JComponent) component).setAlignmentX(Component.CENTER_ALIGNMENT);
            }
        }

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(28));
        card.add(gameOptions);
        card.add(Box.createVerticalStrut(20));
        card.add(play);
        card.add(Box.createVerticalStrut(20));
        card.add(actions);
        card.add(Box.createVerticalStrut(16));
        card.add(soundToggle);
        card.add(Box.createVerticalStrut(8));
        card.add(menuBestScore);
        card.add(Box.createVerticalStrut(12));
        card.add(exit);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        panel.add(card, constraints);
        return panel;
    }

    private JPanel createMenuSetting(String labelText, JComponent control) {
        JPanel setting = new RoundedPanel(new Color(24, 43, 76), 22);
        setting.setLayout(new BoxLayout(setting, BoxLayout.Y_AXIS));
        setting.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        JLabel label = createText(labelText);
        label.setForeground(cyan);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        control.setAlignmentX(Component.LEFT_ALIGNMENT);
        setting.add(label);
        setting.add(Box.createVerticalStrut(10));
        setting.add(control);
        return setting;
    }

    private void styleMenuButton(JButton button, boolean primary) {
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, primary ? 76 : 58));
        button.setPreferredSize(new Dimension(primary ? 560 : 300, primary ? 76 : 58));
        button.setFont(new Font("Arial", Font.BOLD, primary ? 24 : 16));
        if (primary) {
            button.setBackground(cyan);
            button.setForeground(new Color(8, 24, 50));
        } else {
            button.setBackground(new Color(24, 43, 76));
        }
        button.putClientProperty("menuButtonBackground", button.getBackground());
    }

    private void updateMenuBestScore() {
        if (menuBestScore != null) {
            menuBestScore.setText("BEST SCORE (" + difficulty + ", " + timerSeconds + "s): " +
                    readHighScore(difficulty, timerSeconds));
            menuBestScore.setToolTipText("High score for the selected difficulty and timer setting");
        }
    }

    private void showMenu() {
        stopTimers();
        dailyChallenge = false;
        dailyChallengeCompleted = isDailyChallengeCompleted();
        mainPanel.removeAll();
        mainPanel.add(createMenu(), MENU);
        cardLayout.show(mainPanel, MENU);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void resetGame() {
        stopTimers();
        score = 0;
        lives = difficulty.startingLives;
        streak = 0;
        bestStreak = 0;
        totalCorrect = 0;
        totalAnswered = 0;
        currentStage = 0;
        questionNumber = 0;
        hintsUsed = 0;
        newRecord = false;
        missedQuestions.clear();
        unlockedAchievements.clear();
        for (int i = 0; i < questionDecks.length; i++) {
            questionDecks[i] = null;
        }
        showStage();
    }

    private void startDailyChallenge() {
        dailySeed = (int) LocalDate.now().toEpochDay();
        dailyChallenge = true;
        dailyChallengeCompleted = isDailyChallengeCompleted();
        resetGame();
    }

    private Question drawQuestion(int stage) {
        if (dailyChallenge) {
            if (questionDecks[stage] == null) {
                questionDecks[stage] = createQuestionStack(stage,
                        new Random(dailySeed * 31L + stage * 997L));
            }
        } else if (questionDecks[stage] == null || questionDecks[stage].isEmpty()) {
            questionDecks[stage] = createQuestionStack(stage, random);
        }
        if (questionDecks[stage].isEmpty()) {
            throw new IllegalStateException("Question bank exhausted before the round ended.");
        }
        return questionDecks[stage].pop();
    }

    private Deque<Question> createQuestionStack(int stage, Random shuffleRandom) {
        List<Question> shuffledQuestions = new ArrayList<>();
        Collections.addAll(shuffledQuestions, QUESTION_BANK[stage]);
        Collections.shuffle(shuffledQuestions, shuffleRandom);

        Deque<Question> stack = new ArrayDeque<>();
        for (Question question : shuffledQuestions) {
            stack.push(question);
        }
        return stack;
    }

    private void showStage() {
        stopTimers();
        if (questionNumber >= QUESTIONS_PER_GAME || lives <= 0) {
            finishGame();
            return;
        }

        currentStage = stageForQuestion(questionNumber);
        currentQuestion = drawQuestion(currentStage);
        selectedAnswerIndex = -1;
        hintsRemaining = 1;
        stagePanel = buildStagePanel();
        mainPanel.removeAll();
        mainPanel.add(stagePanel, STAGE);
        cardLayout.show(mainPanel, STAGE);
        mainPanel.revalidate();
        mainPanel.repaint();
        if (answerButtons.length > 0) {
            SwingUtilities.invokeLater(() -> answerButtons[0].requestFocusInWindow());
        }

        timeLeft = timerSeconds;
        updateTimerLabel();
        startQuestionTimer();
    }

    private int stageForQuestion(int questionIndex) {
        if (questionIndex < 3) {
            return 0;
        }
        if (questionIndex < 6) {
            return 1;
        }
        return 2;
    }

    private void saveTimerSetting() {
        try {
            preferences.putInt("timerSeconds", timerSeconds);
            preferences.flush();
        } catch (BackingStoreException | SecurityException exception) {
            reportStorageProblem("save timer setting", exception);
        }
    }

    private void startQuestionTimer() {
        if (questionTimer != null) {
            questionTimer.stop();
        }
        questionTimer = new Timer(1000, e -> {
            timeLeft--;
            updateTimerLabel();
            if (timeLeft <= 0) {
                completeQuestion(false, true);
            }
        });
        questionTimer.start();
    }

    private void pauseGame() {
        if (questionTimer == null || !questionTimer.isRunning()) {
            return;
        }
        questionTimer.stop();
        boolean finished = false;
        while (!finished) {
            int choice = showStyledChoiceDialog("PAUSED",
                    "The game is paused.\nTime remaining: " + timeLeft + " seconds.",
                    "RESUME", "SETTINGS", "MAIN MENU");
            if (choice == 0 || choice == JOptionPane.CLOSED_OPTION) {
                startQuestionTimer();
                finished = true;
            } else if (choice == 1) {
                showSoundSettingsDialog();
            } else {
                showMenu();
                finished = true;
            }
        }
    }

    private int showStyledChoiceDialog(String title, String message, String... choices) {
        JDialog dialog = createStyledDialog(title);
        JPanel content = createDialogContent();
        content.add(createDialogMessage(message), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttons.setOpaque(false);
        final int[] selected = {JOptionPane.CLOSED_OPTION};
        for (int i = 0; i < choices.length; i++) {
            final int choiceIndex = i;
            JButton button = createButton(choices[i]);
            button.setFont(new Font("Arial", Font.BOLD, 13));
            button.setPreferredSize(new Dimension(145, 44));
            if (i == 0) {
                dialog.getRootPane().setDefaultButton(button);
            }
            button.addActionListener(e -> {
                selected[0] = choiceIndex;
                dialog.dispose();
            });
            buttons.add(button);
        }
        content.add(buttons, BorderLayout.SOUTH);
        dialog.setContentPane(content);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(500, dialog.getHeight()));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        return selected[0];
    }

    private void showSoundSettingsDialog() {
        JDialog dialog = createStyledDialog("SETTINGS");
        JPanel content = createDialogContent();
        JPanel settings = new JPanel();
        settings.setOpaque(false);
        settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));
        JLabel heading = createText("AUDIO");
        heading.setFont(new Font("Arial", Font.BOLD, 15));
        heading.setForeground(cyanSoft);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        JCheckBox soundToggle = new JCheckBox("Enable sound effects", soundEnabled);
        soundToggle.setOpaque(false);
        soundToggle.setForeground(white);
        soundToggle.setFont(new Font("Arial", Font.PLAIN, 16));
        soundToggle.setFocusPainted(false);
        soundToggle.setAlignmentX(Component.LEFT_ALIGNMENT);
        settings.add(heading);
        settings.add(Box.createVerticalStrut(10));
        settings.add(soundToggle);
        content.add(settings, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttons.setOpaque(false);
        JButton save = createButton("SAVE");
        JButton cancel = createButton("CANCEL");
        save.setPreferredSize(new Dimension(140, 44));
        cancel.setPreferredSize(new Dimension(140, 44));
        dialog.getRootPane().setDefaultButton(save);
        save.addActionListener(e -> {
            setSoundEnabled(soundToggle.isSelected());
            dialog.dispose();
        });
        cancel.addActionListener(e -> dialog.dispose());
        buttons.add(save);
        buttons.add(cancel);
        content.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(420, dialog.getHeight()));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JDialog createStyledDialog(String title) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeDialog");
        dialog.getRootPane().getActionMap().put("closeDialog", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                dialog.dispose();
            }
        });
        return dialog;
    }

    private JPanel createDialogContent() {
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setBackground(new Color(12, 21, 39));
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 125, 145), 1),
                BorderFactory.createEmptyBorder(22, 24, 22, 24)));
        return content;
    }

    private JTextArea createDialogMessage(String message) {
        JTextArea text = new JTextArea(message);
        text.setEditable(false);
        text.setFocusable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setOpaque(false);
        text.setForeground(white);
        text.setFont(new Font("Arial", Font.PLAIN, 16));
        text.setBorder(BorderFactory.createEmptyBorder());
        text.setColumns(38);
        text.setRows(Math.max(3, Math.min(9, message.split("\n").length + 1)));
        return text;
    }

    private void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
        try {
            preferences.putBoolean("soundEnabled", enabled);
        } catch (SecurityException exception) {
            reportStorageProblem("save sound settings", exception);
        }
    }

    private void playSound() {
        if (soundEnabled) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private JPanel buildStagePanel() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int contentWidth = Math.max(520, Math.min(900, (int) (screen.width * 0.72)));
        int questionHeight = Math.max(150, Math.min(230, (int) (screen.height * 0.24)));

        JPanel panel = createStagePanel();
        panel.setLayout(new BorderLayout(24, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(
                Math.max(22, screen.height / 18), 30, Math.max(18, screen.height / 22), 30));

        JLabel badge = createText("QUESTION " + (questionNumber + 1) + " OF " + QUESTIONS_PER_GAME +
                "     /     " + STAGE_NAMES[currentStage]);
        badge.setForeground(cyanSoft);
        badge.setFont(new Font("Arial", Font.BOLD, 17));

        progressLabel = createText("CORRECT " + totalCorrect + "  |  LIVES " + lives);
        progressLabel.setFont(new Font("Arial", Font.BOLD, 15));
        streakLabel = createText("STREAK  " + streak + "     •     SCORE  " + score);
        streakLabel.setFont(new Font("Arial", Font.BOLD, 15));
        timerLabel = createText("");
        timerLabel.setForeground(cyanSoft);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.getAccessibleContext().setAccessibleName("Question countdown timer");
        timerLabel.getAccessibleContext().setAccessibleDescription("Time remaining to answer the current question");

        JPanel statusBar = new JPanel(new GridLayout(1, 4, 12, 0));
        statusBar.setOpaque(false);
        statusBar.add(createStatusCard("ROUND", badge));
        statusBar.add(createStatusCard("PROGRESS", progressLabel));
        statusBar.add(createStatusCard("STREAK / SCORE", streakLabel));
        statusBar.add(createStatusCard("TIME LEFT", timerLabel));

        pauseButton = createButton("PAUSE");
        pauseButton.setMaximumSize(new Dimension(130, 42));
        pauseButton.setPreferredSize(new Dimension(130, 42));
        pauseButton.setToolTipText("Pause the game and open settings");
        pauseButton.getAccessibleContext().setAccessibleDescription(
                "Pause the game, change sound settings, or return to the menu");
        pauseButton.addActionListener(e -> pauseGame());

        progressBar = new JProgressBar(0, QUESTIONS_PER_GAME);
        progressBar.setValue(questionNumber + 1);
        progressBar.setStringPainted(true);
        progressBar.setString("QUESTION " + (questionNumber + 1) + " / " + QUESTIONS_PER_GAME);
        progressBar.setForeground(cyan);
        progressBar.setBackground(new Color(20, 34, 57));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(contentWidth, 20));

        JPanel header = new JPanel(new BorderLayout(18, 12));
        header.setOpaque(false);
        header.add(statusBar, BorderLayout.CENTER);
        header.add(pauseButton, BorderLayout.EAST);
        header.add(progressBar, BorderLayout.SOUTH);

        JLabel topic = createText(currentQuestion.topic +
                (dailyChallenge ? "     •     DAILY CHALLENGE" : ""));
        topic.setFont(new Font("Arial", Font.BOLD, 15));
        topic.setForeground(cyanSoft);

        JLabel question = createText("<html><div style='text-align:center; width:" +
                Math.max(360, contentWidth - 100) + "px;'>" + currentQuestion.prompt + "</div></html>");
        question.setFont(new Font("Arial", Font.BOLD, screen.height < 800 ? 22 : 28));
        question.setHorizontalAlignment(SwingConstants.CENTER);
        question.setVerticalAlignment(SwingConstants.CENTER);
        question.setAlignmentX(Component.CENTER_ALIGNMENT);
        question.setMinimumSize(new Dimension(contentWidth - 60, question.getPreferredSize().height));
        question.setPreferredSize(new Dimension(contentWidth - 60, question.getPreferredSize().height));
        question.setMaximumSize(new Dimension(contentWidth - 60, question.getPreferredSize().height));

        JPanel questionCard = new RoundedPanel(panelSoft, 28);
        questionCard.setLayout(new GridBagLayout());
        questionCard.setBorder(BorderFactory.createEmptyBorder(26, 30, 26, 30));
        topic.setAlignmentX(Component.CENTER_ALIGNMENT);
        topic.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints topicConstraints = new GridBagConstraints();
        topicConstraints.gridx = 0;
        topicConstraints.gridy = 0;
        topicConstraints.fill = GridBagConstraints.HORIZONTAL;
        topicConstraints.weightx = 1;
        topicConstraints.anchor = GridBagConstraints.CENTER;
        topicConstraints.insets = new Insets(0, 0, 16, 0);
        questionCard.add(topic, topicConstraints);

        GridBagConstraints questionConstraints = new GridBagConstraints();
        questionConstraints.gridx = 0;
        questionConstraints.gridy = 1;
        questionConstraints.fill = GridBagConstraints.BOTH;
        questionConstraints.weightx = 1;
        questionConstraints.weighty = 1;
        questionConstraints.anchor = GridBagConstraints.CENTER;
        questionCard.add(question, questionConstraints);
        int cardHeight = Math.max(questionHeight,
                question.getPreferredSize().height + topic.getPreferredSize().height + 70);
        questionCard.setPreferredSize(new Dimension(contentWidth, cardHeight));
        questionCard.setMinimumSize(new Dimension(contentWidth, cardHeight));
        questionCard.setMaximumSize(new Dimension(contentWidth, cardHeight));
        questionCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel promptLabel = createText("CHOOSE YOUR ANSWER");
        promptLabel.setFont(new Font("Arial", Font.BOLD, 14));
        promptLabel.setForeground(cyanSoft);

        JPanel answers = new JPanel(new GridLayout(currentQuestion.answers.length, 1, 0, 12));
        answers.setOpaque(false);
        answers.setMaximumSize(new Dimension(contentWidth, currentQuestion.answers.length * 58));
        answerButtons = new JButton[currentQuestion.answers.length];
        for (int i = 0; i < currentQuestion.answers.length; i++) {
            final int answerIndex = i;
            JButton button = createButton(currentQuestion.answers[i]);
            button.setMaximumSize(new Dimension(contentWidth, 54));
            button.setPreferredSize(new Dimension(contentWidth, 54));
            button.setFont(new Font("Arial", Font.BOLD, contentWidth < 650 ? 14 : 16));
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
            button.getAccessibleContext().setAccessibleDescription("Answer choice " + (i + 1));
            button.addActionListener(e -> {
                selectedAnswerIndex = answerIndex;
                completeQuestion(answerIndex == currentQuestion.correctIndex, false);
            });
            answerButtons[i] = button;
            answers.add(button);
        }

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(Box.createVerticalGlue());
        center.add(questionCard);
        center.add(Box.createVerticalStrut(22));
        center.add(promptLabel);
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(Box.createVerticalStrut(12));
        center.add(answers);
        center.add(Box.createVerticalGlue());

        feedbackLabel = createText("Choose the best answer.");
        feedbackLabel.setFont(new Font("Arial", Font.BOLD, 16));
        feedbackLabel.setForeground(cyanSoft);
        feedbackLabel.setMaximumSize(new Dimension(contentWidth, 46));

        hintLabel = createText("");
        hintLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        hintLabel.setForeground(cyanSoft);
        hintLabel.setMaximumSize(new Dimension(contentWidth, 46));

        hintButton = createButton("USE HINT  ·  -5 POINTS");
        hintButton.setMaximumSize(new Dimension(260, 42));
        hintButton.setPreferredSize(new Dimension(260, 42));
        hintButton.setFont(new Font("Arial", Font.BOLD, 14));
        hintButton.addActionListener(e -> useHint());

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        hintButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.add(hintButton);
        footer.add(Box.createVerticalStrut(8));
        footer.add(hintLabel);
        footer.add(feedbackLabel);

        panel.add(header, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createStatusCard(String heading, JLabel value) {
        JPanel card = new RoundedPanel(panelSoft, 16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel title = createText(heading);
        title.setForeground(cyanSoft);
        title.setFont(new Font("Arial", Font.BOLD, 11));
        value.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(3));
        card.add(value);
        return card;
    }

    private void useHint() {
        if (hintsRemaining == 0) {
            return;
        }
        hintsRemaining--;
        score = Math.max(0, score - 5);
        hintsUsed++;
        hintLabel.setText("HINT: " + currentQuestion.hint);
        hintButton.setEnabled(false);
        streakLabel.setText("SCORE: " + score + "   |   STREAK: " + streak + "   |   LIVES: " + lives);
        playSound();
    }

    private void completeQuestion(boolean correct, boolean timedOut) {
        if (questionTimer == null || !questionTimer.isRunning()) {
            return;
        }
        questionTimer.stop();
        totalAnswered++;
        for (JButton button : answerButtons) {
            button.setEnabled(false);
        }
        hintButton.setEnabled(false);

        for (int i = 0; i < answerButtons.length; i++) {
            if (i == currentQuestion.correctIndex) {
                answerButtons[i].setText("✓  " + currentQuestion.answers[i]);
                answerButtons[i].setBackground(green);
            } else if (i == selectedAnswerIndex) {
                answerButtons[i].setText("✗  " + currentQuestion.answers[i]);
                answerButtons[i].setBackground(red);
            }
        }

        if (correct) {
            streak++;
            bestStreak = Math.max(bestStreak, streak);
            totalCorrect++;
            int points = 10 + (streak - 1) * 5;
            if (timeLeft > timerSeconds / 2) {
                points += 5;
            }
            score += points;
            feedbackLabel.setText("CORRECT! +" + points + " points. " + currentQuestion.explanation);
            feedbackLabel.setForeground(green);
            progressBar.setForeground(green);
            playSound();
        } else {
            streak = 0;
            lives--;
            missedQuestions.add(currentQuestion);
            feedbackLabel.setText((timedOut ? "TIME'S UP! " : "NOT QUITE. ") +
                    "Life lost. " + currentQuestion.explanation);
            feedbackLabel.setForeground(red);
            progressBar.setForeground(red);
            playSound();
            if (timedOut) {
                playSound();
            }
        }
        progressLabel.setText("CORRECT " + totalCorrect + "  |  LIVES " + lives);
        streakLabel.setText("STREAK " + streak + "  |  SCORE " + score);

        if (feedbackTimer != null) {
            feedbackTimer.stop();
        }
        final int[] pulse = {0};
        feedbackTimer = new Timer(180, e -> {
            pulse[0]++;
            feedbackLabel.setFont(new Font("Arial", Font.BOLD, pulse[0] % 2 == 0 ? 14 : 16));
            if (pulse[0] >= 4) {
                ((Timer) e.getSource()).stop();
                if (lives <= 0 || questionNumber + 1 >= QUESTIONS_PER_GAME) {
                    finishGame();
                } else {
                    questionNumber++;
                    showStage();
                }
            }
        });
        feedbackTimer.start();
    }

    private void updateTimerLabel() {
        if (timerLabel != null) {
            timerLabel.setText("TIME: " + timeLeft + "s");
            timerLabel.setForeground(timeLeft <= 5 ? red : cyanSoft);
        }
    }

    private void finishGame() {
        stopTimers();
        if (totalCorrect == QUESTIONS_PER_GAME) {
            score += 10;
        }
        unlockRunAchievements();
        if (dailyChallenge && totalAnswered == QUESTIONS_PER_GAME) {
            saveDailyChallengeCompleted();
        }
        highScore = readHighScore(difficulty, timerSeconds);
        if (score > highScore) {
            highScore = score;
            newRecord = true;
            saveHighScore(difficulty, timerSeconds, highScore);
        }

        JPanel panel = createStagePanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        JPanel card = new RoundedPanel(panelSoft, 28);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(32, 42, 30, 42));
        card.setPreferredSize(new Dimension(700, 560));
        card.setMaximumSize(new Dimension(760, 600));
        JLabel title = createTitle(lives > 0 ? "CHALLENGE COMPLETE!" : "GAME OVER");
        title.setFont(new Font("Arial", Font.BOLD, 34));
        JLabel result = createTitle("YOUR SCORE: " + score);
        result.setFont(new Font("Arial", Font.BOLD, 30));
        int accuracy = totalAnswered == 0 ? 0 : (totalCorrect * 100) / totalAnswered;
        JLabel summary = createText("Correct: " + totalCorrect + "/" + QUESTIONS_PER_GAME +
                "   |   Accuracy: " + accuracy + "%   |   Best streak: " + bestStreak);
        summary.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel best = createText("BEST SCORE (" + difficulty + ", " + timerSeconds + "s): " + highScore +
                (newRecord ? "  - NEW RECORD!" : ""));
        best.setFont(new Font("Arial", Font.BOLD, 17));
        best.setForeground(newRecord ? green : cyanSoft);
        JLabel performance = createText(getPerformanceText());
        performance.setFont(new Font("Arial", Font.BOLD, 18));
        String achievementText = unlockedAchievements.isEmpty()
                ? (dailyChallenge && totalAnswered == QUESTIONS_PER_GAME
                    ? "Daily challenge completed!"
                    : dailyChallenge ? "Daily challenge not completed yet."
                    : "Keep playing to unlock achievements.")
                : "UNLOCKED: " + String.join("  |  ", unlockedAchievements);
        JLabel achievementSummary = createText("<html><div style='text-align:center; width:760px;'>" +
                achievementText + "</div></html>");
        achievementSummary.setFont(new Font("Arial", Font.BOLD, 14));
        achievementSummary.setForeground(green);
        JLabel stars = createTitle(getStarRating());
        stars.setFont(new Font("Arial", Font.BOLD, 30));

        JButton replay = createButton("PLAY AGAIN");
        JButton review = createButton("REVIEW MISSED (" + missedQuestions.size() + ")");
        review.setEnabled(!missedQuestions.isEmpty());
        JButton menu = createButton("MAIN MENU");
        replay.addActionListener(e -> {
            dailyChallenge = false;
            resetGame();
        });
        review.addActionListener(e -> showReview());
        menu.addActionListener(e -> showMenu());

        for (Component component : new Component[]{
                title, result, summary, best, achievementSummary, stars, performance, replay, review, menu
        }) {
            if (component instanceof JComponent) {
                ((JComponent) component).setAlignmentX(Component.CENTER_ALIGNMENT);
            }
        }
        styleMenuButton(replay, true);
        styleMenuButton(review, false);
        styleMenuButton(menu, false);

        card.add(title);
        card.add(Box.createVerticalStrut(16));
        card.add(result);
        card.add(Box.createVerticalStrut(8));
        card.add(summary);
        card.add(Box.createVerticalStrut(8));
        card.add(best);
        card.add(Box.createVerticalStrut(12));
        card.add(achievementSummary);
        card.add(Box.createVerticalStrut(8));
        card.add(stars);
        card.add(Box.createVerticalStrut(6));
        card.add(performance);
        card.add(Box.createVerticalStrut(18));
        card.add(replay);
        card.add(Box.createVerticalStrut(8));
        card.add(review);
        card.add(Box.createVerticalStrut(8));
        card.add(menu);

        GridBagConstraints resultConstraints = new GridBagConstraints();
        resultConstraints.gridx = 0;
        resultConstraints.gridy = 0;
        resultConstraints.weightx = 1;
        resultConstraints.weighty = 1;
        resultConstraints.anchor = GridBagConstraints.CENTER;
        panel.add(card, resultConstraints);

        resultsPanel = panel;
        mainPanel.removeAll();
        mainPanel.add(resultsPanel, RESULTS);
        cardLayout.show(mainPanel, RESULTS);
        mainPanel.revalidate();
        mainPanel.repaint();
        playSound();
    }

    private void showReview() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int contentWidth = Math.max(520, Math.min(1000, screen.width - 100));
        JPanel reviewPanel = createStagePanel();
        reviewPanel.setLayout(new BorderLayout(18, 18));
        reviewPanel.setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));
        JLabel title = createTitle("REVIEW MISSED QUESTIONS");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        JLabel subtitle = createText("Review the correct answers and explanations, then try again.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        JPanel reviewHeader = new JPanel();
        reviewHeader.setOpaque(false);
        reviewHeader.setLayout(new BoxLayout(reviewHeader, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        reviewHeader.add(title);
        reviewHeader.add(Box.createVerticalStrut(6));
        reviewHeader.add(subtitle);

        JPanel entries = new JPanel();
        entries.setOpaque(false);
        entries.setLayout(new BoxLayout(entries, BoxLayout.Y_AXIS));
        for (Question question : missedQuestions) {
            JPanel card = new RoundedPanel(panelSoft, 18);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
            JLabel topic = createText(question.topic);
            topic.setForeground(cyanSoft);
            topic.setFont(new Font("Arial", Font.BOLD, 14));
            JLabel prompt = createText("<html><div style='width:" + (contentWidth - 100) +
                    "px;'>" + question.prompt + "</div></html>");
            prompt.setFont(new Font("Arial", Font.BOLD, 16));
            JLabel answer = createText("Correct answer: " + question.answers[question.correctIndex]);
            answer.setForeground(green);
            answer.setFont(new Font("Arial", Font.BOLD, 14));
            JLabel explanation = createText("<html><div style='width:" + (contentWidth - 100) +
                    "px;'>" + question.explanation + "</div></html>");
            explanation.setFont(new Font("Arial", Font.PLAIN, 14));
            card.add(topic);
            card.add(Box.createVerticalStrut(4));
            card.add(prompt);
            card.add(Box.createVerticalStrut(4));
            card.add(answer);
            card.add(Box.createVerticalStrut(3));
            card.add(explanation);
            card.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.setMaximumSize(new Dimension(contentWidth, Integer.MAX_VALUE));
            entries.add(card);
            entries.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(entries);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(44, 64, 96), 1, true));
        scrollPane.getViewport().setBackground(new Color(12, 21, 39));
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        JButton back = createButton("BACK TO RESULTS");
        styleMenuButton(back, false);
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.addActionListener(e -> {
            mainPanel.removeAll();
            mainPanel.add(resultsPanel, RESULTS);
            cardLayout.show(mainPanel, RESULTS);
            mainPanel.revalidate();
            mainPanel.repaint();
        });
        JPanel reviewFooter = new JPanel();
        reviewFooter.setOpaque(false);
        reviewFooter.add(back);
        reviewPanel.add(reviewHeader, BorderLayout.NORTH);
        reviewPanel.add(scrollPane, BorderLayout.CENTER);
        reviewPanel.add(reviewFooter, BorderLayout.SOUTH);
        mainPanel.removeAll();
        mainPanel.add(reviewPanel, REVIEW);
        cardLayout.show(mainPanel, REVIEW);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void unlockRunAchievements() {
        unlockAchievement("perfectRun", "Perfect Run", totalCorrect == QUESTIONS_PER_GAME);
        unlockAchievement("streakMaster", "Streak Master", bestStreak >= QUESTIONS_PER_GAME);
        unlockAchievement("hintFree", "Hint-Free", totalCorrect == QUESTIONS_PER_GAME && hintsUsed == 0);
        unlockAchievement("hardModeHero", "Hard Mode Hero",
                difficulty == Difficulty.HARD && totalCorrect == QUESTIONS_PER_GAME);
        unlockAchievement("dailyPlayer", "Daily Player",
                dailyChallenge && totalAnswered == QUESTIONS_PER_GAME);
    }

    private void unlockAchievement(String key, String name, boolean earned) {
        if (!earned) {
            return;
        }
        try {
            String preferenceKey = "achievement." + key;
            if (!preferences.getBoolean(preferenceKey, false)) {
                preferences.putBoolean(preferenceKey, true);
                unlockedAchievements.add(name);
                preferences.flush();
            }
        } catch (BackingStoreException | SecurityException exception) {
            reportStorageProblem("save achievements", exception);
        }
    }

    private void showAchievements() {
        String[][] achievements = {
                {"perfectRun", "Perfect Run - Answer all 10 questions correctly"},
                {"streakMaster", "Streak Master - Get 10 correct answers in a row"},
                {"hintFree", "Hint-Free - Clear a run without using hints"},
                {"hardModeHero", "Hard Mode Hero - Clear Hard difficulty"},
                {"dailyPlayer", "Daily Player - Complete a daily challenge"}
        };
        StringBuilder text = new StringBuilder("YOUR ACHIEVEMENTS\n\n");
        try {
            for (String[] achievement : achievements) {
                text.append(preferences.getBoolean("achievement." + achievement[0], false) ? "[UNLOCKED] " : "[LOCKED] ")
                        .append(achievement[1]).append('\n');
            }
        } catch (SecurityException exception) {
            reportStorageProblem("read achievements", exception);
            return;
        }
        showStyledMessage("ACHIEVEMENTS", text.toString());
    }

    private boolean isDailyChallengeCompleted() {
        try {
            return LocalDate.now().toString().equals(preferences.get("dailyCompletedDate", ""));
        } catch (SecurityException exception) {
            reportStorageProblem("read daily challenge status", exception);
            return false;
        }
    }

    private void saveDailyChallengeCompleted() {
        try {
            preferences.put("dailyCompletedDate", LocalDate.now().toString());
            preferences.flush();
            dailyChallengeCompleted = true;
        } catch (BackingStoreException | SecurityException exception) {
            reportStorageProblem("save daily challenge status", exception);
        }
    }

    private String highScoreKey(Difficulty selectedDifficulty, int seconds) {
        return seconds == 15
                ? "highScore." + selectedDifficulty.name()
                : "highScore." + selectedDifficulty.name() + "." + seconds + "s";
    }

    private int readHighScore(Difficulty selectedDifficulty, int seconds) {
        try {
            return preferences.getInt(highScoreKey(selectedDifficulty, seconds), 0);
        } catch (SecurityException exception) {
            reportStorageProblem("read", exception);
            return 0;
        }
    }

    private void saveHighScore(Difficulty selectedDifficulty, int seconds, int value) {
        try {
            preferences.putInt(highScoreKey(selectedDifficulty, seconds), value);
            preferences.flush();
        } catch (BackingStoreException | SecurityException exception) {
            reportStorageProblem("save", exception);
        }
    }

    private void reportStorageProblem(String operation, Exception exception) {
        System.err.println("Unable to " + operation + " the saved high score: " + exception.getMessage());
        if (!storageWarningShown) {
            storageWarningShown = true;
            showStyledMessage("HIGH SCORE STORAGE",
                    "The game could not " + operation + " your high score. The current game will continue, " +
                            "but the saved record may not be available.");
        }
    }

    private void showStyledMessage(String title, String message) {
        JDialog dialog = createStyledDialog(title);
        JPanel content = createDialogContent();
        content.add(createDialogMessage(message), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttons.setOpaque(false);
        JButton close = createButton("GOT IT");
        close.setPreferredSize(new Dimension(150, 44));
        dialog.getRootPane().setDefaultButton(close);
        close.addActionListener(e -> dialog.dispose());
        buttons.add(close);
        content.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(480, dialog.getHeight()));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private String getPerformanceText() {
        if (totalCorrect == QUESTIONS_PER_GAME) {
            return "Outstanding! You cleared every stage!";
        }
        if (totalCorrect >= 8) {
            return "Great job! You are on a strong streak.";
        }
        if (totalCorrect >= 5) {
            return "Nice work! Keep practicing to level up.";
        }
        return "Keep going! Every attempt makes you sharper.";
    }

    private String getStarRating() {
        if (totalCorrect >= 9) {
            return "★★★★★";
        }
        if (totalCorrect >= 7) {
            return "★★★★☆";
        }
        if (totalCorrect >= 4) {
            return "★★★☆☆";
        }
        if (totalCorrect >= 2) {
            return "★★☆☆☆";
        }
        return "★☆☆☆☆";
    }

    private void stopTimers() {
        if (questionTimer != null) {
            questionTimer.stop();
            questionTimer = null;
        }
        if (feedbackTimer != null) {
            feedbackTimer.stop();
            feedbackTimer = null;
        }
    }

    private JPanel createStagePanel() {
        JPanel panel = new GradientPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return panel;
    }

    private JLabel createTitle(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 30));
        label.setForeground(cyan);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JLabel createText(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        label.setForeground(white);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton createButton(String text) {
        JButton button = new RoundedButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(white);
        button.setBackground(panelColor);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(350, 44));
        button.setPreferredSize(new Dimension(350, 44));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(39, 67, 105));
                    button.repaint();
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    Object normalBackground = button.getClientProperty("menuButtonBackground");
                    button.setBackground(normalBackground instanceof Color
                            ? (Color) normalBackground : panelColor);
                    button.repaint();
                }
            }
        });
        return button;
    }

    private enum Difficulty {
        EASY(4),
        MEDIUM(3),
        HARD(2);

        private final int startingLives;

        Difficulty(int startingLives) {
            this.startingLives = startingLives;
        }
    }

    private static class Question {
        private final String topic;
        private final String prompt;
        private final String[] answers;
        private final int correctIndex;
        private final String hint;
        private final String explanation;

        private Question(String topic, String prompt, String[] answers, int correctIndex,
                         String hint, String explanation) {
            this.topic = topic;
            this.prompt = prompt;
            this.answers = answers;
            this.correctIndex = correctIndex;
            this.hint = hint;
            this.explanation = explanation;
        }
    }

    private static class RoundedButton extends JButton {
        private static final long serialVersionUID = 1L;

        private RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            setBorder(new RoundedBorder(20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.dispose();
            super.paintComponent(g);
            if (hasFocus()) {
                Graphics2D focusGraphics = (Graphics2D) g.create();
                focusGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                focusGraphics.setColor(Color.WHITE);
                focusGraphics.setStroke(new BasicStroke(2f));
                focusGraphics.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 18, 18);
                focusGraphics.dispose();
            }
        }
    }

    private static class RoundedBorder implements Border {
        private final int radius;

        private RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 10, 8, 10);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getBackground());
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private final Color panelColor;
        private final int radius;

        private RoundedPanel(Color panelColor, int radius) {
            this.panelColor = panelColor;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(panelColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class GradientPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setPaint(new GradientPaint(0, 0, new Color(8, 14, 30),
                    0, getHeight(), new Color(16, 24, 45)));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TSS game = new TSS();
            game.setVisible(true);
            game.enterFullScreen();
        });
    }
}
