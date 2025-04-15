package ui;

import dao.CategorieDAO;
import dao.ContactDAO;
import model.Categorie;
import model.Contact;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

// Classe permettant de modifier un contact existant via une fenêtre Swing
public class ModifierContactWindow extends JFrame {

    // Constructeur qui prend en paramètre le contact à modifier et une fonction de rappel
    public ModifierContactWindow(Contact contact, Runnable onUpdateCallback) {
        setTitle("Modifier le contact");
        setSize(450, 500);
        setLocationRelativeTo(null); // Centre la fenêtre
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Ferme uniquement cette fenêtre

        // ---------------------- Conteneur principal avec marges ----------------------
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ---------------------- Formulaire en grille ----------------------
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 15));

        // ---------------------- Champs pré-remplis ----------------------
        JTextField nomField = new JTextField(contact.getNom());
        JTextField prenomField = new JTextField(contact.getPrenom());
        JTextField telephoneField = new JTextField(contact.getTelephone());
        JTextField emailField = new JTextField(contact.getEmail());
        JTextField photoField = new JTextField(contact.getPhoto());
        JButton photoButton = new JButton("Choisir une image");

        // ComboBox des catégories avec chargement dynamique
        JComboBox<Categorie> categorieComboBox = new JComboBox<>();
        List<Categorie> categories = new CategorieDAO().getAllCategories();
        for (Categorie cat : categories) {
            categorieComboBox.addItem(cat);
        }
        categorieComboBox.setSelectedItem(contact.getCategorie()); // Sélectionner celle du contact

        // ---------------------- Bouton pour enregistrer les modifications ----------------------
        JButton modifierBtn = new JButton("✅ Enregistrer les modifications");

        // ---------------------- Action choisir une image ----------------------
        photoButton.addActionListener((ActionEvent e) -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                photoField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        // ---------------------- Action du bouton Enregistrer ----------------------
        modifierBtn.addActionListener((ActionEvent e) -> {
            // Mise à jour des champs du contact
            contact.setNom(nomField.getText());
            contact.setPrenom(prenomField.getText());
            contact.setTelephone(telephoneField.getText());
            contact.setEmail(emailField.getText());
            contact.setPhoto(photoField.getText());
            contact.setCategorie((Categorie) categorieComboBox.getSelectedItem());

            // Mise à jour en base de données
            new ContactDAO().modifierContact(contact);

            JOptionPane.showMessageDialog(this, "Contact modifie !");

            // Exécuter le callback pour mettre à jour l'affichage dans la fenêtre principale
            onUpdateCallback.run();

            // Fermer cette fenêtre
            dispose();
        });

        // ---------------------- Ajout des champs dans le formulaire ----------------------
        formPanel.add(new JLabel("Nom :")); formPanel.add(nomField);
        formPanel.add(new JLabel("Prenom :")); formPanel.add(prenomField);
        formPanel.add(new JLabel("Telephone :")); formPanel.add(telephoneField);
        formPanel.add(new JLabel("Email :")); formPanel.add(emailField);
        formPanel.add(new JLabel("Photo :")); formPanel.add(photoField);
        formPanel.add(new JLabel("")); formPanel.add(photoButton);
        formPanel.add(new JLabel("Categorie :")); formPanel.add(categorieComboBox);

        // Panneau contenant le bouton d'enregistrement centré
        JPanel boutonPanel = new JPanel();
        boutonPanel.add(modifierBtn);

        // Ajout des panneaux au conteneur principal
        container.add(formPanel, BorderLayout.CENTER);
        container.add(boutonPanel, BorderLayout.SOUTH);

        // Ajout du conteneur à la fenêtre
        add(container);

        setVisible(true); // Affiche la fenêtre
    }
}
