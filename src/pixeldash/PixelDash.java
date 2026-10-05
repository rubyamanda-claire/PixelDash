package pixeldash;

import javax.swing.JFrame;

public class PixelDash {

    public static void main(String[] args) {
//Instructions for starting pixelDash
    	//Creating a window
        JFrame window = new JFrame("PixelDash");
        
       //Creates object from GamePanel class
        GamePanel game = new GamePanel();

        window.add(game);//adds game to window

        window.setSize(1000, 600);//setting window size

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//close game when X is clicked

        window.setLocationRelativeTo(null);//window in the middle

        window.setResizable(false);//prevents resizing

        window.setVisible(true);// makes window visible

        game.requestFocusInWindow();//help game receive keyboard input
    }
}