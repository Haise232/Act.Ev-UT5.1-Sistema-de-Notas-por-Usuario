import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainGUI extends JFrame {

    private UserService userService;
    private NoteService noteService;
    private String currentUser;

    // CardLayout para cambiar entre login y notas
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private JTextField loginEmailField;
    private JPasswordField loginPassField;

    private JLabel welcomeLabel;
    private DefaultListModel<String> notesListModel;
    private JList<String> notesList;
    private JTextArea noteContentArea;
    private JTextField searchField;

    // Listas para mapear indices reales de las notas
    private List<String> allNotes;
    private List<Integer> displayIndices;

    public MainGUI() {
        userService = new UserService();
        noteService = new NoteService();
        allNotes = new ArrayList<>();
        displayIndices = new ArrayList<>();

        setTitle("Sistema de Notas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 500);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(crearPanelLogin(), "login");
        mainPanel.add(crearPanelNotas(), "notas");

        add(mainPanel);
        cardLayout.show(mainPanel, "login");
    }

    // Crea el formulario de login centrado con BoxLayout
    private JPanel crearPanelLogin() {
        JPanel panel = new JPanel(new GridBagLayout());

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(25, 35, 25, 35));

        JLabel titulo = new JLabel("Sistema de Notas");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(titulo);
        box.add(Box.createVerticalStrut(20));

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(lblEmail);
        box.add(Box.createVerticalStrut(4));
        loginEmailField = new JTextField(22);
        loginEmailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        loginEmailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(loginEmailField);
        box.add(Box.createVerticalStrut(12));

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(lblPass);
        box.add(Box.createVerticalStrut(4));
        loginPassField = new JPasswordField(22);
        loginPassField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        loginPassField.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(loginPassField);
        box.add(Box.createVerticalStrut(18));

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JButton btnLogin = new JButton("Iniciar sesión");
        JButton btnRegistrar = new JButton("Registrarse");

        btnLogin.addActionListener(e -> iniciarSesion());
        btnRegistrar.addActionListener(e -> registrar());
        // Enter en password tambien hace login
        loginPassField.addActionListener(e -> iniciarSesion());

        btnPanel.add(btnLogin);
        btnPanel.add(btnRegistrar);
        box.add(btnPanel);

        panel.add(box);
        return panel;
    }

    // Crea el panel principal con lista de notas, contenido y botones
    private JPanel crearPanelNotas() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel topBar = new JPanel(new BorderLayout(8, 0));

        welcomeLabel = new JLabel("Bienvenido");
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD));

        JButton btnLogout = new JButton("Cerrar sesión");
        btnLogout.addActionListener(e -> cerrarSesion());

        topBar.add(welcomeLabel, BorderLayout.WEST);
        topBar.add(btnLogout, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        // Lista de notas a la izquierda, contenido a la derecha
        notesListModel = new DefaultListModel<>();
        notesList = new JList<>(notesListModel);
        notesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarNotaSeleccionada();
            }
        });

        JScrollPane listScroll = new JScrollPane(notesList);
        listScroll.setPreferredSize(new Dimension(230, 0));
        listScroll.setBorder(BorderFactory.createTitledBorder("Notas"));

        noteContentArea = new JTextArea();
        noteContentArea.setEditable(false);
        noteContentArea.setLineWrap(true);
        noteContentArea.setWrapStyleWord(true);
        noteContentArea.setMargin(new Insets(6, 6, 6, 6));
        JScrollPane contentScroll = new JScrollPane(noteContentArea);
        contentScroll.setBorder(BorderFactory.createTitledBorder("Contenido"));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, listScroll, contentScroll);
        splitPane.setDividerLocation(240);
        panel.add(splitPane, BorderLayout.CENTER);

        // Botones de accion y barra de busqueda abajo
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));

        JButton btnCrear = new JButton("+ Nueva");
        btnCrear.addActionListener(e -> crearNota());
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarNota());
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarNota());

        bottomPanel.add(btnCrear);
        bottomPanel.add(btnEditar);
        bottomPanel.add(btnEliminar);

        bottomPanel.add(Box.createHorizontalStrut(12));
        searchField = new JTextField(14);
        searchField.addActionListener(e -> buscarNotas());
        bottomPanel.add(searchField);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarNotas());
        bottomPanel.add(btnBuscar);
        JButton btnTodas = new JButton("Todas");
        btnTodas.addActionListener(e -> cargarNotas());
        bottomPanel.add(btnTodas);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Valida campos y registra un nuevo usuario
    private void registrar() {
        String email = loginEmailField.getText().trim();
        String password = new String(loginPassField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rellena todos los campos.",
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!Validator.validarEmail(email)) {
            JOptionPane.showMessageDialog(this, "El email no tiene un formato válido.",
                    "Email inválido", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Validator.validarPassword(password)) {
            JOptionPane.showMessageDialog(this,
                    "La contraseña debe tener al menos 6 caracteres.",
                    "Contraseña inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userService.registrar(email, password)) {
            JOptionPane.showMessageDialog(this,
                    "Usuario registrado correctamente.\nYa puedes iniciar sesión.",
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            loginPassField.setText("");
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar. El email puede estar ya en uso.",
                    "Error de registro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void iniciarSesion() {
        String email = loginEmailField.getText().trim();
        String password = new String(loginPassField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rellena todos los campos.",
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userService.login(email, password)) {
            currentUser = email.trim().toLowerCase();
            welcomeLabel.setText("Bienvenido: " + currentUser);
            loginPassField.setText("");
            cargarNotas();
            cardLayout.show(mainPanel, "notas");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Email o contraseña incorrectos.",
                    "Error de login", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cerrarSesion() {
        currentUser = null;
        notesListModel.clear();
        noteContentArea.setText("");
        searchField.setText("");
        allNotes.clear();
        displayIndices.clear();
        cardLayout.show(mainPanel, "login");
    }

    private void cargarNotas() {
        allNotes = noteService.leerNotas(currentUser);
        displayIndices = new ArrayList<>();
        for (int i = 0; i < allNotes.size(); i++) {
            displayIndices.add(i + 1);
        }
        actualizarListaNotas();
        searchField.setText("");
    }

    private void actualizarListaNotas() {
        notesListModel.clear();
        noteContentArea.setText("");

        for (int i = 0; i < displayIndices.size(); i++) {
            int idx = displayIndices.get(i) - 1;
            String resumen = obtenerResumen(allNotes.get(idx), displayIndices.get(i));
            notesListModel.addElement(resumen);
        }
    }

    private String obtenerResumen(String nota, int numero) {
        String primera = nota.split("\n")[0];
        if (primera.length() > 45) {
            primera = primera.substring(0, 42) + "...";
        }
        return "Nota " + numero + " - " + primera;
    }

    private void mostrarNotaSeleccionada() {
        int sel = notesList.getSelectedIndex();
        if (sel >= 0 && sel < displayIndices.size()) {
            int idx = displayIndices.get(sel) - 1;
            noteContentArea.setText(allNotes.get(idx));
            noteContentArea.setCaretPosition(0);
        }
    }

    private void crearNota() {
        JTextArea textArea = new JTextArea(8, 35);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(textArea);

        int result = JOptionPane.showConfirmDialog(this, scroll,
                "Escribe tu nueva nota", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String texto = textArea.getText().trim();
            if (texto.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "La nota no puede estar vacía.",
                        "Nota vacía", JOptionPane.WARNING_MESSAGE);
            } else {
                noteService.crearNota(currentUser, texto);
                cargarNotas();
                if (notesListModel.size() > 0) {
                    notesList.setSelectedIndex(notesListModel.size() - 1);
                }
            }
        }
    }

    private void editarNota() {
        int sel = notesList.getSelectedIndex();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una nota de la lista.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int noteNumber = displayIndices.get(sel);
        String notaActual = allNotes.get(noteNumber - 1);

        String textoEditable = notaActual;
        String[] lineas = notaActual.split("\n");
        if (lineas.length > 1 && lineas[0].startsWith("[")) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < lineas.length; i++) {
                if (i > 1) sb.append("\n");
                sb.append(lineas[i]);
            }
            textoEditable = sb.toString();
        }

        JTextArea textArea = new JTextArea(textoEditable, 8, 35);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(textArea);

        int result = JOptionPane.showConfirmDialog(this, scroll,
                "Editar nota " + noteNumber, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String nuevoTexto = textArea.getText().trim();
            if (nuevoTexto.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "La nota no puede estar vacía.",
                        "Nota vacía", JOptionPane.WARNING_MESSAGE);
            } else {
                if (noteService.editarNota(currentUser, noteNumber, nuevoTexto)) {
                    cargarNotas();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al editar la nota.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void eliminarNota() {
        int sel = notesList.getSelectedIndex();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una nota de la lista.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int noteNumber = displayIndices.get(sel);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres eliminar la nota " + noteNumber + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (noteService.eliminarNota(currentUser, noteNumber)) {
                cargarNotas();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar la nota.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarNotas() {
        String palabra = searchField.getText().trim();
        if (palabra.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Escribe algo para buscar.",
                    "Búsqueda vacía", JOptionPane.WARNING_MESSAGE);
            return;
        }

        allNotes = noteService.leerNotas(currentUser);
        displayIndices = new ArrayList<>();

        for (int i = 0; i < allNotes.size(); i++) {
            if (allNotes.get(i).toLowerCase().contains(palabra.toLowerCase())) {
                displayIndices.add(i + 1);
            }
        }

        if (displayIndices.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron notas con \"" + palabra + "\".",
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            cargarNotas();
        } else {
            actualizarListaNotas();
        }
    }
}
