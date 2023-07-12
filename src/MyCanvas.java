import javax.swing.*;
import java.awt.*;

public class MyCanvas extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i <10 ; i++) {
            g.drawRect (500,500,i*100,i*100);
            g.drawString ("Square", 550, 550);

        }


    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 200); // Adjust the size of your canvas here
    }
}