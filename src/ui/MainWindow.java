package ui;

import dao.CategorieDAO;
import dao.ContactDAO;
import model.Categorie;
import model.Contact;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

// Classe principale de la fenêtre principale de l'application
public class MainWindow extends JFrame {
    

    // Constructeur de la classe MainWindow
    public MainWindow() {

        // Configuration de base de la fenêtre
        setTitle("Gestionnaire de Contacts");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centre la fenêtre
        setLayout(new BorderLayout());

        // ---------------------- En-tête avec fond dégradé ----------------------
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color noir = new Color(0, 0, 0);
                Color vert = new Color(0, 153, 0); // Vert "La Cité"
                GradientPaint gp = new GradientPaint(0, 0, noir, getWidth(), getHeight(), vert);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(600, 100));
        headerPanel.setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("Gestionnaire de Contacts");
        headerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        headerLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        headerPanel.add(headerLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // ---------------------- Formulaire central ----------------------
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JTextField nomField = new JTextField();
        JTextField prenomField = new JTextField();
        JTextField telephoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField photoField = new JTextField();
        JButton photoBtn = new JButton("Choisir...");
        JComboBox<Categorie> categorieComboBox = new JComboBox<>();

        // Chargement des catégories depuis la base
        List<Categorie> categories = new CategorieDAO().getAllCategories();
        for (Categorie c : categories) {
            categorieComboBox.addItem(c);
        }

        // Ajout des composants du formulaire
        panel.add(new JLabel("Nom :"));
        panel.add(nomField);
        panel.add(new JLabel("Prénom :"));
        panel.add(prenomField);
        panel.add(new JLabel("Téléphone :"));
        panel.add(telephoneField);
        panel.add(new JLabel("Email :"));
        panel.add(emailField);
        panel.add(new JLabel("Photo :"));
        panel.add(photoField);
        panel.add(new JLabel("")); // case vide pour alignement
        panel.add(photoBtn);
        panel.add(new JLabel("Catégorie :"));
        panel.add(categorieComboBox);

        add(panel, BorderLayout.CENTER);

        // ---------------------- Boutons ----------------------
        JButton ajouterBtn = new JButton("Ajouter le contact");
        JButton afficherBtn = new JButton("Afficher les contacts");

        JPanel btnPanel = new JPanel();
        btnPanel.add(ajouterBtn);
        btnPanel.add(afficherBtn);

        // ---------------------- Action bouton Choisir photo ----------------------
        photoBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int res = chooser.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                photoField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        // ---------------------- Action bouton Ajouter ----------------------
        ajouterBtn.addActionListener((ActionEvent e) -> {
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String telephone = telephoneField.getText();
            String email = emailField.getText();
            String photo = photoField.getText();
            Categorie categorie = (Categorie) categorieComboBox.getSelectedItem();

            if (nom.isEmpty() || prenom.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nom et prénom obligatoires !");
                return;
            }

            Contact c = new Contact(nom, prenom, categorie, telephone, email, photo);
            new ContactDAO().ajouterContact(c);

            JOptionPane.showMessageDialog(this, "Contact ajouté avec succès !");
            //
            // Réinitialisation du formulaire
            nomField.setText("");
            prenomField.setText("");
            telephoneField.setText("");
            emailField.setText("");
            photoField.setText("");
            categorieComboBox.setSelectedIndex(0);
        });

        // ---------------------- Action bouton Afficher les contacts ----------------------
        afficherBtn.addActionListener(e -> new ContactsTableWindow());

        // ---------------------- Pied de page (footer) ----------------------
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Ajout d'un logo dans le coin gauche
        ImageIcon icon = new ImageIcon(getClass().getResource("OIP.jpg"));
        Image img = icon.getImage().getScaledInstance(180, 50, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(img));

        // Ajout du texte des droits d’auteur
        JLabel droitLabel = new JLabel("© 2025 Mouloud, Abdelghani, Walid – Tous droits réservés");
        droitLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        footer.add(imageLabel, BorderLayout.WEST);
        footer.add(droitLabel, BorderLayout.EAST);

        // Combinaison boutons + pied de page
        JPanel panelGlobalSud = new JPanel(new BorderLayout());
        panelGlobalSud.add(btnPanel, BorderLayout.NORTH);
        panelGlobalSud.add(footer, BorderLayout.SOUTH);

        add(panelGlobalSud, BorderLayout.SOUTH);

        setVisible(true); // Affiche la fenêtre
    }
}