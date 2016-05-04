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
        /*
        System.out.println("Hello");
        
        Matrix A = Matrix.randM(1,1);
        Matrix B = Matrix.randM(1,1);
        
        A.print();
        System.out.println();
        B.print();
        System.out.println();
        A.add(B).print();
        System.out.println();
        A.sub(B).print();
        System.out.println();
        A.mul(B).print();
        System.out.println();*/
    }   
    
    private static final long serialVersionUID = 1L;
    
    private static final int defHight   = 400;
    private static final int defLength  = 600;
    private String nameSelectedFile;
    
    private int     SourceHeight;
    private int     SourceWidth; 
    private Image   imageSource;
    private Matrix  source;
    
    private Image   imageDest;
    private Matrix  dest;
    
    private ImageComponent imageComponent;
    private int State;
    
    public AffineFrame() {
        
        setSize(defLength, defHight);
        setTitle("Affine transformation");
        
        Panel panel = new Panel();
        add(panel, BorderLayout.SOUTH);
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
    
    private class Panel extends JPanel{
        
        private static final long serialVersionUID = 1L;

        public Panel() {
            
            JButton selectButton = new JButton("Select Picture");
            ActionListener select = new SelectPicture();
            selectButton.addActionListener(select);
            add(selectButton);
            
            JButton transButton = new JButton("Transformation");
            ActionListener transf = new PerfTransform();
            transButton.addActionListener(transf);
            add(transButton);
        }
        
        private class SelectPicture implements ActionListener {
            
            public void actionPerformed(ActionEvent event) {
                
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                
                int result = chooser.showOpenDialog(Panel.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    
                    nameSelectedFile = chooser.getSelectedFile().getPath();
                    State = 0;
                    imageComponent = new ImageComponent();   
                    AffineFrame.this.add(imageComponent);
                    AffineFrame.this.setVisible(true);
                }
            }
        }
        
        private class PerfTransform implements ActionListener {
            
            public void actionPerformed(ActionEvent event) {
                
                State = 1;
                imageComponent = new ImageComponent();
                AffineFrame.this.add(imageComponent);
                AffineFrame.this.setVisible(true);
            }
        }
    }
    
    private class ImageComponent extends JComponent {
        
        private static final long serialVersionUID = 1L;

        public ImageComponent() {
            
            imageSource = new ImageIcon(nameSelectedFile).getImage();
            source = getPixels(imageSource);
            
            if (State == 1) {
                System.out.print(SourceHeight);
                imageDest = pixelsToImage(source);
            }
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
            
            int[][] res = new int[SourceHeight][SourceWidth];
            for (int i = 0; i < SourceHeight; i++)
                for (int j = 0; j < SourceWidth; j++)
                    res[i][j] = pixArray[i*SourceWidth + j];
            
            return new Matrix(res);
        }
        
        private Image pixelsToImage(Matrix picture) {
            
            return createImage(new MemoryImageSource(picture.getWigth(), picture.getHeight(), 
                                                          picture.getArray(), 0, picture.getWigth()));
        }
        
        public void paintComponent(Graphics g) {

            if (imageSource == null) 
                return;
            
            g.drawImage(imageSource, 0, 0, null);
            
            if (imageDest == null) 
                return;
            
            g.drawImage(imageDest, SourceWidth+100 , 0, null);
        }
    }
}
