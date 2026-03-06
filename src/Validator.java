/*
 * Clase para validar los datos del usuario
 * Comprueba que el email y la contraseña sean correctos
 */
public class Validator {

    // Comprueba que el email tenga un formato mas o menos valido
    public static boolean validarEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        email = email.trim();

        // tiene que tener @ y al menos un punto despues del @
        if (!email.contains("@")) {
            return false;
        }

        String[] partes = email.split("@");
        if (partes.length != 2) {
            return false;
        }

        // la parte de antes y despues del @ no pueden estar vacias
        if (partes[0].isEmpty() || partes[1].isEmpty()) {
            return false;
        }

        // el dominio tiene que tener un punto
        if (!partes[1].contains(".")) {
            return false;
        }

        return true;
    }

    // La contraseña tiene que tener minimo 6 caracteres
    public static boolean validarPassword(String password) {
        if (password == null) {
            return false;
        }
        return password.length() >= 6;
    }

    // Limpia el email para usarlo como nombre de carpeta
    // Ej: joaquin@gmail.com -> joaquin_at_gmail_com
    public static String limpiarEmail(String email) {
        String limpio = email.trim().toLowerCase();
        limpio = limpio.replace("@", "_at_");
        limpio = limpio.replace(".", "_");
        return limpio;
    }
}
