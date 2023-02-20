/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
    + CT tính chéo trái: top x = x0 - Min(x0, y0), top y = y0 - Min(x0, y0)
    + CT tính cháo phải: top x = x0 - Min(x0, y0) , top y = y0 + Min(x0, y0)
    !! Nếu y >= biên => dư = y - (biên - 1)
    => x = x + dư, y = biên - 1 !! i++, j--
*/  
package com.mycompany.caro;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import javax.sound.sampled.*;

/**
 *
 * @author admin
 */
public class Board extends JPanel{
    private static final int N = 24;
    private static final int M = 24;
    
    public static final int ST_DRAW = 0;
    public static final int ST_WIN = 1;
    public static final int ST_NORMAL = 2;


    private EndGameListener endGameListener;
    private Image imgX;
    private Image imgO;
    private Cell[][] matrix = new Cell[N][M];
    private String currentPlayer = Cell.EMPTY_VALUE;
    
    public Board(String Player) throws IOException{
        this();
        this.currentPlayer = Player;
    }
    
    public Board() throws IOException{
        // Khoi tao matrix
        this.initMatrix();
        
        addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent e) {                
                super.mousePressed(e); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
                int x = e.getX();
                int y = e.getY();
                
                if(currentPlayer.equals(Cell.EMPTY_VALUE)){
                    return;
                }
                
                // Phát ra âm thanh
                soundClick();
                
                //Tinh toan xem x, y roi vao o nao trong board, sau do ve x, o tuy y
                for(int i = 0; i < N; i++){
                    for(int j = 0; j < M; j++){
                        Cell cell = matrix[i][j];
                        
                        
                        int cXStart = cell.getX();
                        int cYStart = cell.getY();
                        
                        int cXEnd = cXStart + cell.getW();
                        int cYEnd = cYStart + cell.getH();
                        
                        if(x >= cXStart && x <= cXEnd && y >= cYStart && y <=  cYEnd){
                            if(cell.getValue().equals(Cell.EMPTY_VALUE)){
                                cell.setValue(currentPlayer);
                                repaint();
                                int result = checkWin(currentPlayer, i, j);
                                if(endGameListener != null){
                                    endGameListener.end(currentPlayer, result);
                                }
                                
                                if(result == ST_NORMAL){
                                    currentPlayer = currentPlayer.equals(Cell.O_VALUE) ? Cell.X_VALUE : Cell.O_VALUE;
                                }    
                                
                            }
                        }
                    }
                }
            }
        });
       
        try{
            imgX = ImageIO.read(getClass().getResource("x.png"));
            imgO = ImageIO.read(getClass().getResource("o.png"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private synchronized void soundClick(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("Click1.wav"));
                    clip.open(audioInputStream);
                    clip.start();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    
    private void initMatrix(){
        for(int i = 0; i < N; i++){
            for(int j = 0; j < M; j++){
                Cell cell = new Cell();
                matrix[i][j] = cell;

            }
        }
    }
    
    public void reset(){
        this.initMatrix();
        this.setCurrentPlayer(Cell.EMPTY_VALUE);
        repaint();
    }
    
    public void setCurrentPlayer(String currentPlayer){
        this.currentPlayer = currentPlayer;
    }
    
    public void setEndGameListener(EndGameListener endGameListener){
        this.endGameListener = endGameListener;
    }
    
    public String getCurrentPlayer(){
        return currentPlayer;
    }
    
    //0: het nuoc 0 ai thang ca, 1: player thang, 2: player chua thang
    public int checkWin(String player, int i, int j){
        int count = 0;
        int min;
        int TopI, TopJ;
        // Chieu ngang
        for(int col = 0; col < M; col++){
            Cell cell = matrix[i][col];
            if(cell.getValue().equals(currentPlayer)){
                count++;
                if(count >= 5){
                    System.out.println("Ngang");
                    return ST_WIN;
                }
            }else{
                count = 0;
            }
        }
        
        // Chieu doc
        count = 0;
        for(int row = 0; row < N; row++){
            Cell cell = matrix[row][j];
            if(cell.getValue().equals(currentPlayer)){
                count++;
                if(count >= 5){
                    System.out.println("Doc");
                    return ST_WIN;
                }
            }else{
                count = 0;
            }
        }
        
        // Cheo trai
        count = 0;
        min = Math.min(i, j);
        TopI = i - min;
        TopJ = j - min;
        
        for(;TopI < N && TopJ < M; TopI++, TopJ++){
            Cell cell = matrix[TopI][TopJ];
            if(cell.getValue().equals(currentPlayer)){
                count++;
                if(count >= 5){
                    System.out.println("Cheo trai");
                    return ST_WIN;
                }
            }else{
                count = 0;
            }
        }
        
        // Cheo phai
        count = 0;
        min = Math.min(i, j);
        TopI = i - min;
        TopJ = j + min;
        if(TopJ >= M){
            int du = TopJ - (M - 1);
            TopI += du;
            TopJ = M - 1;
        }
        
        for(; TopI < N && TopJ >= 0; TopI++, TopJ--){
            Cell cell = matrix[TopI][TopJ];
            if(cell.getValue().equals(currentPlayer)){
                count++;
                if(count >= 5){
                    System.out.println("Cheo phai");
                    return ST_WIN;
                }
            }else{
                count = 0;
            }
        }
        
        return this.isFull() ? ST_DRAW : ST_NORMAL;
    }
    
    private boolean isFull(){
        int number  = N * N;
        
        int k = 0;
        for(int i = 0; i < N; i++){
            for(int j = 0; j < M; j++){
                Cell cell = matrix[i][j];
                if(!cell.getValue().equals(cell.EMPTY_VALUE)){
                    k++;
                }
            }
        }
        return k == number;
    }
    
    @Override
    public void paint(Graphics g) {
        int w = getWidth() / M;
        int h = getHeight() / N;
        Graphics2D graphic2d = (Graphics2D) g;
        
        for(int i = 0; i < N; i++){
            int k = i;
            for(int j = 0; j < M; j++){
                int x = j * w;
                int y = i * h;
                
                // Cap nhat lai ma tran
                Cell cell = matrix[i][j];
                cell.setX(x);
                cell.setY(y);
                cell.setW(w);
                cell.setH(h);
                
                Color color = k % 2 == 0 ? Color.white : Color.gray;      
                graphic2d.setColor(color);
                graphic2d.fillRect(x, y, w, h);
                if(cell.getValue().equals(Cell.X_VALUE)){
                    Image img = imgX;
                    graphic2d.drawImage(img, x, y, w, h, this);
                }else if(cell.getValue().equals(Cell.O_VALUE)){
                    Image img = imgO;
                    graphic2d.drawImage(img, x, y, w, h, this);
                }
                k++;
            }
        }
    }  
}
