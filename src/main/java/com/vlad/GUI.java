package com.vlad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class GUI extends JFrame {
    private static final int SIZE = 5;
    private final JPanel[][] grid = new JPanel[SIZE][SIZE];
    private JButton actionButton;
    private JButton actionButton2;
    private QLearning qLearning = new QLearning(0.01f, 0, 0, 5);
    private ImageIcon catIcon;
    private ImageIcon ratIcon;
    private JLabel catLabel;
    private JLabel ratLabel;
    private JTable matrixTable;
    private JLabel numberLabel;

    private int ratRow, ratCol;
    private int catRow, catCol = 0;
    public GUI() {
        setTitle("Lab6");
        setSize(1200, 600); // Увеличим размер окна для отображения обеих панелей
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());  // Меняем компоновку на BorderLayout

        // Панель управления с кнопкой
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.setPreferredSize(new Dimension(550, 0)); // Ширина панели с кнопкой управления
        //btn1
        actionButton = new JButton("RandomMove");
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                QLearningCatMove();
                updateMatrixDisplay();
                if (qLearning.isMouseChatched){
                    updateNumberDisplay();
                    clearRedCircles();
                    qLearning.catMemoryReset();
                    moveCatTo(qLearning.catXposition, qLearning.catYposition);
                }
            }
        });
        controlPanel.add(actionButton, BorderLayout.NORTH);

        //btn2
        actionButton2 = new JButton("NextCycle");
        actionButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                while (!qLearning.isMouseChatched) {
                    QLearningCatMove();
                    updateMatrixDisplay();
                }
                
                if (qLearning.isMouseChatched){
                    updateNumberDisplay();
                    clearRedCircles();
                    qLearning.catMemoryReset();
                    moveCatTo(qLearning.catXposition, qLearning.catYposition);
                }
            }
        });
    controlPanel.add(actionButton2, BorderLayout.NORTH);


        matrixTable = new JTable(SIZE, SIZE);
        updateMatrixDisplay(); // Инициализация матрицы
        controlPanel.add(new JScrollPane(matrixTable), BorderLayout.CENTER);

        numberLabel = new JLabel("#");
        numberLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Игровое поле
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(SIZE, SIZE)); // Игровое поле с сеткой 5x5
        gamePanel.setBackground(Color.LIGHT_GRAY); // Цвет фона для игрового поля
        initializeGrid(gamePanel);

        // Добавляем панели в главное окно
        add(controlPanel, BorderLayout.WEST);
        add(gamePanel, BorderLayout.CENTER);

        

        // Добавляем метку числа под таблицей
        controlPanel.add(numberLabel, BorderLayout.SOUTH);

        loadIcons();
        placeCatAndRat(qLearning.catXposition, qLearning.catYposition, 0, 0);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeIcons();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                moveCat(e.getKeyCode());
            }
        });

        setFocusable(true);
        setVisible(true);
    }

    private void QLearningCatMove() {
        MyNode catPosition = qLearning.randomCatMove();
        moveCatTo(catPosition.x, catPosition.y);
    }

    
    private void updateNumberDisplay() {
        numberLabel.setText("Відносна максимальна зміна: " + qLearning.maxDeltaQ + "%");
    }

    private void resizeIcons() {
        int cellWidth = getWidth() / SIZE;
        int cellHeight = getHeight() / SIZE;
        int iconSize = Math.min(cellWidth, cellHeight);

        Image scaledCatImage = catIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        Image scaledRatImage = ratIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);

        catLabel.setIcon(new ImageIcon(scaledCatImage));
        ratLabel.setIcon(new ImageIcon(scaledRatImage));

        revalidate();
        repaint();
    }

    private void loadIcons() {
        catIcon = new ImageIcon("D:\\Study\\kurs4\\SHI\\6\\java\\lab6\\src\\main\\resources\\cat.png");
        ratIcon = new ImageIcon("D:\\Study\\kurs4\\SHI\\6\\java\\lab6\\src\\main\\resources\\rat.png");
        catLabel = new JLabel(catIcon);
        ratLabel = new JLabel(ratIcon);
    }

    private void initializeGrid(JPanel gamePanel) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid[row][col] = new JPanel();
                grid[row][col].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                gamePanel.add(grid[row][col]);
            }
        }
    }

    private void placeCatAndRat(int catRow, int catCol, int ratRow, int ratCol) {
        this.catRow = catRow;
        this.catCol = catCol;
        grid[catRow][catCol].add(catLabel);
        grid[ratRow][ratCol].add(ratLabel);
        JPanel prevPanel = grid[catRow][catCol];
        prevPanel.add(new JLabel(new ImageIcon(createRedCircle(10))));
        revalidate();
        repaint();
    }

    private void moveCatTo(int x, int y) {
        grid[catRow][catCol].remove(catLabel);
        catRow = x;
        catCol = y;
        JPanel prevPanel = grid[catRow][catCol];
        prevPanel.add(new JLabel(new ImageIcon(createRedCircle(10))));
        grid[catRow][catCol].add(catLabel);
        revalidate();
        repaint();
    }

    private Image createRedCircle(int size) {
        Image image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setColor(Color.RED);
        g2d.fillOval(0, 0, size, size);
        g2d.dispose();
        return image;
    }
    private void moveCat(int keyCode) {
        grid[catRow][catCol].remove(catLabel);

        switch (keyCode) {
            case KeyEvent.VK_W -> catRow = Math.max(0, catRow - 1);
            case KeyEvent.VK_S -> catRow = Math.min(SIZE - 1, catRow + 1);
            case KeyEvent.VK_A -> catCol = Math.max(0, catCol - 1);
            case KeyEvent.VK_D -> catCol = Math.min(SIZE - 1, catCol + 1);
        }

        grid[catRow][catCol].add(catLabel);

        revalidate();
        repaint();
    }


    public void updateMatrixValue(int row, int col, int value) {
        if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
            qLearning.Q[row][col] = value;
            updateMatrixDisplay();
        }
    }

    // Метод для обновления отображения таблицы
    private void updateMatrixDisplay() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                matrixTable.setValueAt(qLearning.Q[row][col], row, col);
            }
        }
    }

    private void clearRedCircles() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                JPanel panel = grid[row][col];
                
                // Удаляем все компоненты, которые не являются котом или крысой
                if (panel != grid[catRow][catCol] && panel != grid[ratRow][ratCol]) {
                    panel.removeAll();
                }
            }
        }
    
        revalidate();
        repaint();
    }
    

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}
