/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.caro;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.TimerTask;
import java.util.Timer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
/**
 *
 * @author admin
 */
public class Caro {
    
    private static int sec = 0;
    private static Timer timer = new Timer();
    private static JLabel lblTime;
    private static Board board;
    private static JButton btnStart;
    
    public static void main(String[] args) throws IOException{
        board = new Board();
        board.setEndGameListener(new EndGameListener() {
            @Override
            public void end(String player, int st) {
                if(st == Board.ST_WIN){
                    JOptionPane.showMessageDialog(null, "Người chơi " + player + " đã thắng");
                    stopGame();
                }else if(st == Board.ST_DRAW){
                    JOptionPane.showMessageDialog(null, "Hòa!!");
                    stopGame();
                }
            }
        });
        JPanel jPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(jPanel, BoxLayout.Y_AXIS);
        jPanel.setLayout(boxLayout);

        board.setPreferredSize(new Dimension(600, 600));
        
        FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER, 0, 0);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(flowLayout);
        
        btnStart = new JButton("Start");
        lblTime = new JLabel("0:0");
        bottomPanel.add(lblTime);
        bottomPanel.add(btnStart);
        
        btnStart.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(btnStart.getText().equals("Start")){
                    startGame();
                }else{
                    stopGame();
                }
            }
        });
        
        jPanel.add(board);
        jPanel.add(bottomPanel);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        
        JFrame jFrame = new JFrame("Game tic tac toe");
        jFrame.setSize(600, 600);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setResizable(true);
        jFrame.add(jPanel);
        
        int x = (int) (dimension.getWidth() / 2 - (jFrame.getWidth() / 2));
        int y = ((int) dimension.getHeight() / 2 - (jFrame.getHeight() / 2));
        
        
        jFrame.setLocation(x, y); // Hien thi ra giua man hinh

        // Co dãn layout bên trong
        jFrame.pack();
        // Show giao diện
        jFrame.setVisible(true); // Hien thi man hinh consol
        
        backgroundMusic();
    }
    
    private static void startGame(){
        // Hoi ai di truoc
        int choice = JOptionPane.showConfirmDialog(null, "Người chơi O đánh trước đúng không?", "Ai là người đánh trước?", JOptionPane.YES_NO_OPTION);
        board.reset();
        String currentPlayer = (choice == 0)? Cell.O_VALUE:Cell.X_VALUE;
        board.setCurrentPlayer(currentPlayer);
        
        //Dem nguoc
        sec = 0;
        lblTime.setText("0:0");
        timer.cancel();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sec++;
                String value = sec / 60 + ":" + sec % 60;
                lblTime.setText(value);
            }
        }, 1000, 1000);
        
        btnStart.setText("Stop");
    }
    
    private static void stopGame(){
        btnStart.setText("Start");
        
        sec = 0;
        lblTime.setText("0:0");
        
        timer.cancel();
        timer = new Timer();
        
        board.reset();
    }
    
    private static void backgroundMusic(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("Madness.wav"));
                    clip.open(audioInputStream);
                    clip.start();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    
}
