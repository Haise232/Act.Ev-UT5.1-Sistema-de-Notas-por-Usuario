import java.util.List;
import java.util.Scanner;

public class App {

    private static Scanner scanner = new Scanner(System.in);
    private static UserService userService = new UserService();
    private static NoteService noteService = new NoteService();
    private static String currentUser = null;

    public static void main(String[] args) {
        System.out.println("=== Sistema de Notas ===");

        while (true) {
            if (currentUser == null) {
                if (!menuLogin()) break;
            } else {
                if (!menuNotas()) break;
            }
        }

        System.out.println("Hasta luego!");
        scanner.close();
    }

    // Menu de inicio: login, registro o salir
    private static boolean menuLogin() {
        System.out.println("\n1. Iniciar sesion");
        System.out.println("2. Registrarse");
        System.out.println("0. Salir");
        System.out.print("Opcion: ");
        String opcion = scanner.nextLine().trim();

        switch (opcion) {
            case "1":
                iniciarSesion();
                break;
            case "2":
                registrar();
                break;
            case "0":
                return false;
            default:
                System.out.println("Opcion no valida.");
        }
        return true;
    }

    private static void iniciarSesion() {
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("Rellena todos los campos.");
            return;
        }

        if (userService.login(email, password)) {
            currentUser = email.trim().toLowerCase();
            System.out.println("Bienvenido: " + currentUser);
        }
    }

    private static void registrar() {
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("Rellena todos los campos.");
            return;
        }

        if (!Validator.validarEmail(email)) {
            System.out.println("El email no tiene un formato valido.");
            return;
        }

        if (!Validator.validarPassword(password)) {
            System.out.println("La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        if (userService.registrar(email, password)) {
            System.out.println("Ya puedes iniciar sesion.");
        }
    }

    // Menu principal de notas
    private static boolean menuNotas() {
        System.out.println("\n--- Menu de Notas ---");
        System.out.println("1. Ver notas");
        System.out.println("2. Crear nota");
        System.out.println("3. Editar nota");
        System.out.println("4. Eliminar nota");
        System.out.println("5. Buscar notas");
        System.out.println("6. Cerrar sesion");
        System.out.println("0. Salir");
        System.out.print("Opcion: ");
        String opcion = scanner.nextLine().trim();

        switch (opcion) {
            case "1":
                noteService.mostrarNotas(currentUser);
                break;
            case "2":
                crearNota();
                break;
            case "3":
                editarNota();
                break;
            case "4":
                eliminarNota();
                break;
            case "5":
                buscarNotas();
                break;
            case "6":
                currentUser = null;
                System.out.println("Sesion cerrada.");
                break;
            case "0":
                return false;
            default:
                System.out.println("Opcion no valida.");
        }
        return true;
    }

    private static void crearNota() {
        System.out.println("Escribe tu nota (linea vacia para terminar):");
        StringBuilder texto = new StringBuilder();
        String linea;
        while (!(linea = scanner.nextLine()).isEmpty()) {
            if (texto.length() > 0) texto.append("\n");
            texto.append(linea);
        }

        if (texto.length() == 0) {
            System.out.println("La nota no puede estar vacia.");
            return;
        }

        noteService.crearNota(currentUser, texto.toString());
    }

    private static void editarNota() {
        noteService.mostrarNotas(currentUser);
        List<String> notas = noteService.leerNotas(currentUser);
        if (notas.isEmpty()) return;

        System.out.print("Numero de nota a editar: ");
        int numero;
        try {
            numero = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Numero no valido.");
            return;
        }

        if (numero < 1 || numero > notas.size()) {
            System.out.println("Numero de nota no valido.");
            return;
        }

        System.out.println("Escribe el nuevo texto (linea vacia para terminar):");
        StringBuilder texto = new StringBuilder();
        String linea;
        while (!(linea = scanner.nextLine()).isEmpty()) {
            if (texto.length() > 0) texto.append("\n");
            texto.append(linea);
        }

        if (texto.length() == 0) {
            System.out.println("La nota no puede estar vacia.");
            return;
        }

        if (noteService.editarNota(currentUser, numero, texto.toString())) {
            System.out.println("Nota editada correctamente.");
        } else {
            System.out.println("Error al editar la nota.");
        }
    }

    private static void eliminarNota() {
        noteService.mostrarNotas(currentUser);
        List<String> notas = noteService.leerNotas(currentUser);
        if (notas.isEmpty()) return;

        System.out.print("Numero de nota a eliminar: ");
        int numero;
        try {
            numero = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Numero no valido.");
            return;
        }

        System.out.print("Seguro que quieres eliminar la nota " + numero + "? (s/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("s")) {
            if (noteService.eliminarNota(currentUser, numero)) {
                System.out.println("Nota eliminada.");
            } else {
                System.out.println("Error al eliminar la nota.");
            }
        } else {
            System.out.println("Eliminacion cancelada.");
        }
    }

    private static void buscarNotas() {
        System.out.print("Palabra a buscar: ");
        String palabra = scanner.nextLine().trim();
        if (palabra.isEmpty()) {
            System.out.println("Escribe algo para buscar.");
            return;
        }
        noteService.buscarNotas(currentUser, palabra);
    }
}
