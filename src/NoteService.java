import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 * Clase para gestionar las notas de cada usuario
 * Crear, ver, buscar, editar y eliminar notas
 */
public class NoteService {

    // Carpeta base donde estan las carpetas de los usuarios
    private Path carpetaBase;

    public NoteService() {
        this.carpetaBase = Paths.get("src", "data", "usuarios");
    }

    // Devuelve la ruta al fichero de notas de un usuario
    private Path getRutaNotas(String email) {
        String carpeta = Validator.limpiarEmail(email);
        return carpetaBase.resolve(carpeta).resolve("notas.txt");
    }

    // Crea una nota nueva y la guarda en el fichero
    public void crearNota(String email, String texto) {
        Path fichero = getRutaNotas(email);

        try {
            // por si acaso no existe la carpeta
            Files.createDirectories(fichero.getParent());

            try (BufferedWriter bw = Files.newBufferedWriter(fichero,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                // guardamos la fecha y hora
                LocalDateTime ahora = LocalDateTime.now();
                String fecha = ahora.getDayOfMonth() + "/" + ahora.getMonthValue() + "/"
                        + ahora.getYear() + " " + ahora.getHour() + ":" + ahora.getMinute();

                bw.write("[" + fecha + "]");
                bw.newLine();
                bw.write(texto);
                bw.newLine();
                bw.write("---");
                bw.newLine();
            }

            System.out.println("Nota guardada!");
        } catch (IOException e) {
            System.out.println("Error al guardar la nota: " + e.getMessage());
        }
    }

    // Lee todas las notas del fichero y las devuelve en una lista
    public List<String> leerNotas(String email) {
        Path fichero = getRutaNotas(email);
        List<String> notas = new ArrayList<>();

        if (!Files.exists(fichero)) {
            return notas;
        }

        try (BufferedReader br = Files.newBufferedReader(fichero)) {
            String linea;
            String notaActual = "";

            while ((linea = br.readLine()) != null) {
                if (linea.equals("---")) {
                    // cuando encontramos el separador guardamos la nota
                    if (!notaActual.isEmpty()) {
                        notas.add(notaActual.trim());
                        notaActual = "";
                    }
                } else {
                    notaActual = notaActual + linea + "\n";
                }
            }
            // por si queda algo sin separador al final
            if (!notaActual.trim().isEmpty()) {
                notas.add(notaActual.trim());
            }
        } catch (IOException e) {
            System.out.println("Error al leer las notas: " + e.getMessage());
        }

        return notas;
    }

    // Muestra por pantalla todas las notas del usuario
    public void mostrarNotas(String email) {
        List<String> notas = leerNotas(email);

        if (notas.isEmpty()) {
            System.out.println("No tienes notas todavia.");
            return;
        }

        System.out.println("\n===== Tus notas =====");
        for (int i = 0; i < notas.size(); i++) {
            System.out.println("\n-- Nota " + (i + 1) + " --");
            System.out.println(notas.get(i));
        }
        System.out.println("=====================\n");
    }

    // Busca notas que contengan una palabra clave
    public void buscarNotas(String email, String palabra) {
        List<String> notas = leerNotas(email);
        boolean hayResultados = false;

        System.out.println("\nBuscando \"" + palabra + "\"...");

        for (int i = 0; i < notas.size(); i++) {
            // pasamos todo a minusculas para que no importe mayus/minus
            if (notas.get(i).toLowerCase().contains(palabra.toLowerCase())) {
                System.out.println("\n-- Nota " + (i + 1) + " --");
                System.out.println(notas.get(i));
                hayResultados = true;
            }
        }

        if (!hayResultados) {
            System.out.println("No se encontraron notas con esa palabra.");
        }
    }

    // Elimina una nota por su numero
    public boolean eliminarNota(String email, int numero) {
        List<String> notas = leerNotas(email);

        if (numero < 1 || numero > notas.size()) {
            System.out.println("Numero de nota no valido.");
            return false;
        }

        notas.remove(numero - 1);
        return guardarTodasLasNotas(email, notas);
    }

    // Edita una nota existente por su numero
    public boolean editarNota(String email, int numero, String nuevoTexto) {
        List<String> notas = leerNotas(email);

        if (numero < 1 || numero > notas.size()) {
            System.out.println("Numero de nota no valido.");
            return false;
        }

        // Intentamos mantener la fecha original de la nota
        String notaVieja = notas.get(numero - 1);
        String fecha = "";
        if (notaVieja.startsWith("[")) {
            int pos = notaVieja.indexOf("]");
            if (pos != -1) {
                fecha = notaVieja.substring(0, pos + 1);
            }
        }

        String notaNueva = fecha + " (editada)\n" + nuevoTexto;
        notas.set(numero - 1, notaNueva);

        return guardarTodasLasNotas(email, notas);
    }

    // Reescribe todo el fichero de notas (se usa al editar o eliminar)
    private boolean guardarTodasLasNotas(String email, List<String> notas) {
        Path fichero = getRutaNotas(email);

        try (BufferedWriter bw = Files.newBufferedWriter(fichero,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (int i = 0; i < notas.size(); i++) {
                bw.write(notas.get(i));
                bw.newLine();
                bw.write("---");
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error al guardar las notas: " + e.getMessage());
            return false;
        }
    }
}
