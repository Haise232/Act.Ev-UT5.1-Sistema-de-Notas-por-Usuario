import java.util.Scanner;
import java.util.List;

/*
 * Clase principal del programa
 * Aqui estan los menus y se controla todo el flujo
 */
public class App {

    // Scanner para leer del teclado (lo usamos en todo el programa)
    static Scanner sc = new Scanner(System.in);
    static UserService userService = new UserService();
    static NoteService noteService = new NoteService();

    public static void main(String[] args) {

        System.out.println("================================");
        System.out.println("  SISTEMA DE NOTAS POR USUARIO");
        System.out.println("================================");

        int opcion = 0;

        // Bucle del menu principal
        while (opcion != 3) {
            System.out.println("\n--- Menu Principal ---");
            System.out.println("1. Registrarse");
            System.out.println("2. Iniciar sesion");
            System.out.println("3. Salir");
            System.out.print("Opcion: ");

            // Leemos la opcion como String y la pasamos a int para evitar problemas con el Scanner
            String entrada = sc.nextLine().trim();
            try {
                opcion = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Introduce un numero.");
                continue;
            }

            if (opcion == 1) {
                registrar();
            } else if (opcion == 2) {
                iniciarSesion();
            } else if (opcion == 3) {
                System.out.println("Hasta luego!");
            } else {
                System.out.println("Opcion no valida.");
            }
        }

        sc.close();
    }

    // Pide los datos y registra al usuario
    static void registrar() {
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Contraseña (min 6 caracteres): ");
        String password = sc.nextLine();

        userService.registrar(email, password);
    }

    // Pide los datos, hace login y si es correcto entra al menu de notas
    static void iniciarSesion() {
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Contraseña: ");
        String password = sc.nextLine();

        if (userService.login(email, password)) {
            System.out.println("Bienvenido " + email + "!");
            menuNotas(email);
        }
    }

    // Menu que aparece cuando el usuario ha iniciado sesion
    static void menuNotas(String email) {
        int opcion = 0;

        while (opcion != 6) {
            System.out.println("\n--- Menu de Notas ---");
            System.out.println("1. Crear nota");
            System.out.println("2. Ver mis notas");
            System.out.println("3. Buscar notas");
            System.out.println("4. Editar nota");
            System.out.println("5. Eliminar nota");
            System.out.println("6. Cerrar sesion");
            System.out.print("Opcion: ");

            String entrada = sc.nextLine().trim();
            try {
                opcion = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Introduce un numero.");
                continue;
            }

            switch (opcion) {
                case 1:
                    System.out.println("Escribe tu nota:");
                    String texto = sc.nextLine();
                    if (texto.isEmpty()) {
                        System.out.println("La nota no puede estar vacia.");
                    } else {
                        noteService.crearNota(email, texto);
                    }
                    break;

                case 2:
                    noteService.mostrarNotas(email);
                    break;

                case 3:
                    System.out.print("Palabra a buscar: ");
                    String palabra = sc.nextLine().trim();
                    if (palabra.isEmpty()) {
                        System.out.println("Tienes que escribir algo.");
                    } else {
                        noteService.buscarNotas(email, palabra);
                    }
                    break;

                case 4:
                    // Editar nota
                    List<String> notasEditar = noteService.leerNotas(email);
                    if (notasEditar.isEmpty()) {
                        System.out.println("No tienes notas.");
                    } else {
                        noteService.mostrarNotas(email);
                        System.out.print("Numero de nota a editar: ");
                        try {
                            int num = Integer.parseInt(sc.nextLine().trim());
                            System.out.println("Escribe el nuevo contenido:");
                            String nuevo = sc.nextLine();
                            if (nuevo.isEmpty()) {
                                System.out.println("No puede estar vacio.");
                            } else {
                                if (noteService.editarNota(email, num, nuevo)) {
                                    System.out.println("Nota editada!");
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Pon un numero valido.");
                        }
                    }
                    break;

                case 5:
                    // Eliminar nota
                    List<String> notasEliminar = noteService.leerNotas(email);
                    if (notasEliminar.isEmpty()) {
                        System.out.println("No tienes notas.");
                    } else {
                        noteService.mostrarNotas(email);
                        System.out.print("Numero de nota a eliminar: ");
                        try {
                            int num = Integer.parseInt(sc.nextLine().trim());
                            if (noteService.eliminarNota(email, num)) {
                                System.out.println("Nota eliminada!");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Pon un numero valido.");
                        }
                    }
                    break;

                case 6:
                    System.out.println("Sesion cerrada.");
                    break;

                default:
                    System.out.println("Opcion no valida.");
            }
        }
    }
}
