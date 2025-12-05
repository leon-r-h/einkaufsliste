package com.siemens.einkaufsliste.gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.*;

import javax.swing.*;

import com.formdev.flatlaf.FlatLightLaf;

public class register extends JFrame {
    public register(){
        this.setTitle("Events Elemente");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(515, 320);

        Rectangle bounds = this.getGraphicsConfiguration().getBounds();
        int x = bounds.x + (bounds.width  - this.getWidth())  / 2;
        int y = bounds.y + (bounds.height - this.getHeight()) / 2;
        this.setLocation(x, y);

        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        // Hauptpanel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Textfeld
        JLabel name = new JLabel("Name");
        panel.add(name);
        JTextField nameField = new JTextField();
        panel.add(nameField);

        // Passwortfeld
        JLabel passwort = new JLabel("Passwort");
        panel.add(passwort);
        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordField);

        // Checkbox
        JLabel newsletter = new JLabel("Newsletter");
        panel.add(newsletter);
        JCheckBox newsletterBox = new JCheckBox();
        panel.add(newsletterBox);

        // Radio Buttons (Geschlecht)
        JLabel gender = new JLabel("Geschlecht");
        panel.add(gender);
        JPanel radioPanel = new JPanel();
        JRadioButton male = new JRadioButton("Männlich");
        JRadioButton female = new JRadioButton("Weiblich");
        JRadioButton other = new JRadioButton("Andere");
        other.setSelected(true);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);
        genderGroup.add(other);
        radioPanel.add(male);
        radioPanel.add(female);
        radioPanel.add(other);
        panel.add(radioPanel);

        JButton buttonCancel = new JButton("Cancel");

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                register.this.dispose();
                new register();
            }
        });
        panel.add(buttonCancel);

        JButton buttonDone = new JButton("Done");

        buttonDone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.out.println("Der Name ist:" + nameField.getText());
                System.out.println("Das Passwort ist:" + passwordField.getText());
                if(newsletterBox.isSelected()){
                    System.out.println("Das Abo wurde gewält");
                }
                String gender;
                if (male.isSelected()) {
                    gender = "Männlich";
                } else if (female.isSelected()) {
                    gender = "Weiblich";
                } else {
                    gender = "Andere";
                }
                register.this.dispose();
            }
        });
        panel.add(buttonDone);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(0, 3, 10, 10));
        panel2.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        panel2.add(new JLabel(""));
        JButton buttonRegister = new JButton("Login");

        buttonRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                register.this.dispose();
                new login();
            }
        });
        panel2.add(buttonRegister);
        panel2.add(new JLabel(""));
        
        this.add(panel);
        this.add(panel2);
        this.setVisible(true); 
    }
    public static void main(String[] args){
        FlatLightLaf.setup();
        new register();
    }
}