import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/*
 * Clase que se encarga de gestionar los usuarios
 * Registro, login y todo lo relacionado con users.txt
 */
public class UserService {

    // Ruta al fichero donde se guardan los usuarios
    private Path archivoUsuarios;
    // Carpeta donde se guardan las carpetas de cada usuario
    private Path carpetaUsuarios;

    public UserService() {
        this.archivoUsuarios = Paths.get("src", "data", "users.txt");
        this.carpetaUsuarios = Paths.get("src", "data", "usuarios");

        // Si no existe el fichero de usuarios lo creamos
        try {
            if (!Files.exists(archivoUsuarios)) {
                Files.createDirectories(archivoUsuarios.getParent());
                Files.createFile(archivoUsuarios);
            }
        } catch (IOException e) {
            System.out.println("Error al iniciar el sistema: " + e.getMessage());
        }
    }

    // Metodo para registrar un nuevo usuario
    public boolean registrar(String email, String password) {
        // Primero validamos el email
        if (!Validator.validarEmail(email)) {
            System.out.println("El email no tiene un formato valido.");
            return false;
        }

        // Validamos la contraseña
        if (!Validator.validarPassword(password)) {
            System.out.println("La contraseña tiene que tener al menos 6 caracteres.");
            return false;
        }

        email = email.trim().toLowerCase();

        // Miramos si ya existe ese email
        if (existeUsuario(email)) {
            System.out.println("Ese email ya esta registrado.");
            return false;
        }

        // Guardamos el usuario en el fichero con la contraseña hasheada
        String passwordHash = Validator.hashPassword(password);
        try (BufferedWriter bw = Files.newBufferedWriter(archivoUsuarios,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            bw.write(email + ";" + passwordHash);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error al guardar el usuario: " + e.getMessage());
            return false;
        }

        // Creamos la carpeta del usuario y su fichero de notas
        try {
            Path carpeta = carpetaUsuarios.resolve(Validator.limpiarEmail(email));
            Files.createDirectories(carpeta);
            Path notas = carpeta.resolve("notas.txt");
            if (!Files.exists(notas)) {
                Files.createFile(notas);
            }
        } catch (IOException e) {
            System.out.println("Error al crear la carpeta del usuario: " + e.getMessage());
            return false;
        }

        System.out.println("Usuario registrado correctamente!");
        return true;
    }

    // Metodo para hacer login
    public boolean login(String email, String password) {
        if (!Validator.validarEmail(email)) {
            System.out.println("El email no tiene un formato valido.");
            return false;
        }

        email = email.trim().toLowerCase();

        // Hasheamos la contraseña introducida y comparamos con la guardada
        String passwordHash = Validator.hashPassword(password);
        try (BufferedReader br = Files.newBufferedReader(archivoUsuarios)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length == 2) {
                    if (partes[0].equals(email) && partes[1].equals(passwordHash)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer los usuarios: " + e.getMessage());
        }

        System.out.println("Email o contraseña incorrectos.");
        return false;
    }

    // Comprueba si un email ya esta registrado
    private boolean existeUsuario(String email) {
        try {
            List<String> lineas = Files.readAllLines(archivoUsuarios);
            for (String linea : lineas) {
                String[] partes = linea.split(";");
                if (partes.length >= 1 && partes[0].equals(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al comprobar usuarios: " + e.getMessage());
        }
        return false;
    }
}
