package com.ksg.view.comp.button;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class ImageButton extends JButton{
    
    Image img;

    public ImageButton(String file)
    {   
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(file));
        img = icon.getImage();

        Image newImg = img.getScaledInstance(15, 15, Image.SCALE_SMOOTH);
        ImageIcon newIcon = new ImageIcon(newImg);        

        this.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));

        this.setFocusPainted(false);
        this.setRolloverEnabled(true);
        this.setIcon(newIcon);
//        this.setBorderPainted(true);
        
        //this.setContentAreaFilled(false);
        this.setPreferredSize(new Dimension(25,25));

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(Color.GREEN);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(UIManager.getColor("control"));
            }
        });
    }
}