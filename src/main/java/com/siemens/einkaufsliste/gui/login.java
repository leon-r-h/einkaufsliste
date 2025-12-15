package com.siemens.einkaufsliste.gui;

import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.*;

import javax.swing.*;

import com.formdev.flatlaf.FlatLightLaf;

public class login extends JFrame {
    public login(){
        this.setTitle("Login");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(300, 190);

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

        JButton buttonCancel = new JButton("Cancel");

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                login.this.dispose();
                new login();
            }
        });
        panel.add(buttonCancel);

        JButton buttonDone = new JButton("Done");

        buttonDone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.out.println("Der Name ist:" + nameField.getText());
                System.out.println("Das Passwort ist:" + passwordField.getText());
                login.this.dispose();
            }
        });
        panel.add(buttonDone);
        
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(0, 3, 10, 10));
        panel2.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        panel2.add(new JLabel(""));
        JButton buttonRegister = new JButton("Register");

        buttonRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                login.this.dispose();
                new register();
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

        new login();
    }
}
