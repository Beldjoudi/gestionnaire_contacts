package ui;

// Importation des classes nécessaires depuis les packages DAO et model
import dao.CategorieDAO;
import dao.ContactDAO;
import model.Categorie;
import model.Contact;

// Importation des bibliothèques Java Swing et AWT
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Classe principale qui étend JFrame pour créer une fenêtre Swing
public class ContactsTableWindow extends JFrame {

    // Déclaration des composants Swing et variables de gestion
    private JTable table; // Tableau principal
    private DefaultTableModel model; // Modèle de données du tableau
    private int[] ids; // Tableau pour stocker les ID des contacts
    private JTextField searchField; // Champ de recherche
    private JComboBox<Categorie> categorieFilter; // Filtre de catégorie
    private JLabel photoLabel; // Label pour afficher la photo
    private List<Contact> allContacts; // Liste de tous les contacts

    // Constructeur de la fenêtre
    public ContactsTableWindow() {
        setTitle("Liste des Contacts"); // Titre de la fenêtre
        setSize(1000, 550); // Taille de la fenêtre
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Fermeture sans quitter l'application
        setLocationRelativeTo(null); // Centre la fenêtre à l'écran

        // Création du conteneur principal avec marges
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        setContentPane(container);

        // Création de la barre supérieure (recherche + filtre)
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));

        // Champ de recherche
        searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setToolTipText("Rechercher un nom ou prenom");

        // ComboBox de filtre par catégorie
        categorieFilter = new JComboBox<>();
        categorieFilter.setFont(new Font("SansSerif", Font.PLAIN, 14));
        categorieFilter.addItem(new Categorie(0, "Toutes les categories")); // Option par défaut
        List<Categorie> categories = new CategorieDAO().getAllCategories(); // Chargement des catégories
        for (Categorie c : categories) {
            categorieFilter.addItem(c); // Ajout des catégories à la ComboBox
        }

        // Ajout des composants au panneau de recherche
        searchPanel.add(new JLabel("🔍 Rechercher : "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(categorieFilter, BorderLayout.EAST);

        // Ajout de la barre supérieure au conteneur principal
        topPanel.add(searchPanel);
        container.add(topPanel, BorderLayout.NORTH);

        // Définition des colonnes du tableau
        String[] columns = {"Nom", "Prenom", "Telephone", "Email", "Categorie", "Photo"};
        model = new DefaultTableModel(columns, 0); // Création du modèle vide
        table = new JTable(model); // Création du tableau
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(28); // Hauteur des lignes
        table.setShowGrid(true); // Affiche la grille
        table.setGridColor(Color.LIGHT_GRAY); // Couleur de la grille

        // Centrage du texte dans les cellules du tableau
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Ajout du tableau avec scroll dans le conteneur principal
        JScrollPane scrollPane = new JScrollPane(table);
        container.add(scrollPane, BorderLayout.CENTER);

        // Zone d'affichage de la photo à droite
        photoLabel = new JLabel();
        photoLabel.setHorizontalAlignment(JLabel.CENTER);
        photoLabel.setVerticalAlignment(JLabel.CENTER);
        photoLabel.setPreferredSize(new Dimension(200, 200));
        photoLabel.setBorder(BorderFactory.createTitledBorder("Photo du contact"));
        container.add(photoLabel, BorderLayout.EAST);

        // Création des boutons Supprimer et Modifier
        JButton supprimerBtn = new JButton("Supprimer");
        JButton modifierBtn = new JButton("Modifier");
        supprimerBtn.setBackground(new Color(220, 53, 69));
        supprimerBtn.setForeground(Color.WHITE);
        modifierBtn.setBackground(new Color(0, 123, 255));
        modifierBtn.setForeground(Color.WHITE);

        // Panneau des boutons en bas
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(modifierBtn);
        buttonPanel.add(supprimerBtn);
        container.add(buttonPanel, BorderLayout.SOUTH);

        // Chargement initial des contacts
        chargerContacts();

        // Écoute des changements dans le champ de recherche
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrer(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrer(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrer(); }
        });

        // Écoute des changements dans le filtre catégorie
        categorieFilter.addActionListener(e -> filtrer());

        // Écoute de la sélection dans le tableau
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                afficherPhotoSelectionnee();
            }
        });

        // Action du bouton supprimer
        supprimerBtn.addActionListener(e -> supprimerContactSelectionne());

        // Action du bouton modifier
        modifierBtn.addActionListener(e -> modifierContactSelectionne());

        // Rendre la fenêtre visible
        setVisible(true);
    }

    // Méthode pour charger les contacts depuis la base
    private void chargerContacts() {
        allContacts = new ContactDAO().getAllContacts();
        afficherContacts(allContacts);
    }

    // Méthode pour afficher une liste de contacts dans le tableau
    private void afficherContacts(List<Contact> contacts) {
        model.setRowCount(0); // Vide le tableau
        ids = new int[contacts.size()];

        for (int i = 0; i < contacts.size(); i++) {
            Contact c = contacts.get(i);
            ids[i] = c.getId();

            model.addRow(new Object[]{
                    c.getNom(),
                    c.getPrenom(),
                    c.getTelephone(),
                    c.getEmail(),
                    c.getCategorie().getNom(),
                    c.getPhoto()
            });
        }

        photoLabel.setIcon(null); // Réinitialise l'image
    }

    // Méthode pour filtrer les contacts selon le champ et la catégorie
    private void filtrer() {
        String texte = searchField.getText().toLowerCase();
        Categorie selectedCat = (Categorie) categorieFilter.getSelectedItem();

        List<Contact> resultats = allContacts.stream()
                .filter(c -> c.getNom().toLowerCase().contains(texte)
                        || c.getPrenom().toLowerCase().contains(texte))
                .filter(c -> selectedCat.getId() == 0 || c.getCategorie().getId() == selectedCat.getId())
                .toList();

        afficherContacts(resultats);
    }

    // Méthode pour afficher la photo du contact sélectionné
    private void afficherPhotoSelectionnee() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            photoLabel.setIcon(null);
            return;
        }

        String cheminPhoto = (String) model.getValueAt(selectedRow, 5);
        if (cheminPhoto != null && !cheminPhoto.isEmpty()) {
            ImageIcon icon = new ImageIcon(cheminPhoto);
            Image image = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            photoLabel.setIcon(new ImageIcon(image));
        } else {
            photoLabel.setIcon(null);
        }
    }

    // Méthode pour supprimer un contact sélectionné
    private void supprimerContactSelectionne() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez selectionner un contact !");
            return;
        }

        int idASupprimer = ids[selectedRow];
        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer ce contact ?", "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new ContactDAO().supprimerContact(idASupprimer);
            chargerContacts();
            JOptionPane.showMessageDialog(this, "Contact supprime !");
        }
    }

    // Méthode pour modifier un contact sélectionné
    private void modifierContactSelectionne() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez selectionner un contact !");
            return;
        }

        int id = ids[selectedRow];
        String nom = (String) model.getValueAt(selectedRow, 0);
        String prenom = (String) model.getValueAt(selectedRow, 1);
        String telephone = (String) model.getValueAt(selectedRow, 2);
        String email = (String) model.getValueAt(selectedRow, 3);
        String categorieNom = (String) model.getValueAt(selectedRow, 4);
        String photo = (String) model.getValueAt(selectedRow, 5);
//
        CategorieDAO catDAO = new CategorieDAO();
        Categorie categorie = catDAO.getAllCategories()
                .stream()
                .filter(cat -> cat.getNom().equals(categorieNom))
                .findFirst()
                .orElse(null);

        Contact contact = new Contact(id, nom, prenom, categorie, telephone, email, photo);
        new ModifierContactWindow(contact, this::chargerContacts); // Ouvre fenêtre de modification
    }
}
