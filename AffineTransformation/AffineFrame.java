import Matrix.*;
import Affine.*;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

public class AffineFrame extends JFrame{

    public static void main(String[] args) {
        
        AffineFrame.run();
    }   
    
    private static final long serialVersionUID = 1L;
    
    private enum STATE {SELECT, TRANSFORM};
    private static final int defHight   = 400;
    private static final int defLength  = 600;

    private int     SourceWidth; 
    private int     SourceHeight;
    
    private Image   imageSource;
    private Matrix  source;
    
    private Image   imageDest;
    private Matrix  dest;
    
    private JTextField angle;
    private JTextField expansion;
    private JLabel[]   label;
    
    private STATE State;
    private String nameSelectedFile;
    private ImageComponent imageComponent;
    
    public AffineFrame() {
        
        setSize(defLength, defHight);
        setTitle("Affine transformation");
        
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(3,2));
        // add TextField and label
        label = new JLabel[2];
        label[0] = new JLabel("angle of rotation");
        label[1] = new JLabel("coefficient of expansion"); 
        angle = new JTextField("0", 10);
        expansion = new JTextField("1", 10);
        
        ActionListener select = new SelectPicture();
        JButton sButton = new JButton("Select Picture");
        sButton.addActionListener(select);
        add(sButton);
        
        ActionListener transf = new PerfTransform();
        JButton tButton = new JButton("Transformation");
        tButton.addActionListener(transf);
        add(tButton);
        
        northPanel.add(label[0]);
        northPanel.add(angle);
        northPanel.add(label[1]);
        northPanel.add(expansion);
        northPanel.add(sButton);
        northPanel.add(tButton);
        
        add(northPanel, BorderLayout.NORTH);
        // add Image
        imageComponent = new ImageComponent();   
        this.add(imageComponent);
     }
    
    public static void run() {
        
        EventQueue.invokeLater(new Runnable() {
            
            public void run() {
               
                AffineFrame frame = new AffineFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }

    private class SelectPicture implements ActionListener {
        
        public void actionPerformed(ActionEvent event) {
            
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            
            int result = chooser.showOpenDialog(AffineFrame.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                
                nameSelectedFile = chooser.getSelectedFile().getPath();
                State = STATE.SELECT;                  
                imageComponent.repaint();
                AffineFrame.this.setVisible(true);  
            }
        }
    }
    
    private class PerfTransform implements ActionListener {
        
        public void actionPerformed(ActionEvent event) {
            
            State = STATE.TRANSFORM;
            String An = angle.getText();
            double A = Double.parseDouble(An);
            String exp = expansion.getText();
            double E = Double.parseDouble(exp);
            Affine af = new Affine(A,E,Affine.Mode.NEAREST_NEIGHBOR);
            dest = af.transform(source);
            imageComponent.repaint();
            AffineFrame.this.setVisible(true);
        }
    }   

    private class ImageComponent extends JComponent {
        
        private static final long serialVersionUID = 1L;

        public ImageComponent() {
            
            super();
        }
        
        public void repaint() {
            
            if (State == STATE.TRANSFORM || State == STATE.SELECT) {
                
                imageSource = new ImageIcon(nameSelectedFile).getImage();
                source = getPixels(imageSource);
                
                if (State == STATE.TRANSFORM) {
                    
                    imageDest = pixelsToImage(dest);
                }
                
                this.setSize(3*SourceWidth,3*SourceHeight);
            }
            
            super.repaint();
        }
        
        private Matrix getPixels(Image image) {
            
            SourceWidth = image.getWidth(AffineFrame.this);
            SourceHeight = image.getHeight(AffineFrame.this);
            
            int[] pixArray = new int[SourceHeight*SourceWidth];
            PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, SourceWidth, SourceHeight, pixArray, 0, SourceWidth);
            
            try {
                
                pixelGrabber.grabPixels();
            } catch (InterruptedException e) {
            
                System.out.print("ERROR: Can't get pixels");
                e.printStackTrace();
            }
            
            double[][] res = new double[SourceHeight][SourceWidth];
            for (int i = 0; i < SourceHeight; i++)
                for (int j = 0; j < SourceWidth; j++)
                    res[i][j] = pixArray[i*SourceWidth + j];
            
            return new Matrix(res);
        }
        
        private Image pixelsToImage(Matrix picture) {
            
            double[] dArray = picture.getArray();
            int[] iArray = new int[dArray.length];
            for(int i = 0; i < dArray.length; ++i)
                iArray[i] = (int)dArray[i];
            return createImage(new MemoryImageSource(picture.getWigth(), picture.getHeight(), 
                                                     iArray, 0, picture.getWigth()));
        }
        
        public void paintComponent(Graphics g) {

            if (imageSource == null) 
                return;
            
            g.drawImage(imageSource, 0, 0, null);
            
            if (imageDest == null) 
                return;
            
            g.drawImage(imageDest, SourceWidth+50 , 0, null);
        }
    }
}
