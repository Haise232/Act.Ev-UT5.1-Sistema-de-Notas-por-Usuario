# Sistema de Notas por Usuario

Actividad de evaluación UT5.1 - 1º DAM Programación

## Descripción

Aplicación de consola en Java que permite registrar usuarios, iniciar sesión y gestionar notas personales. Toda la información se guarda en ficheros de texto para que persista entre ejecuciones.

## Funcionalidades

- **Registro de usuarios**: se pide email y contraseña, se validan y se guardan en `users.txt`
- **Inicio de sesión**: comprueba las credenciales contra el fichero
- **Crear notas**: escribe una nota con fecha y hora y la guarda en el fichero del usuario
- **Ver notas**: muestra todas las notas del usuario que ha iniciado sesión
- **Buscar notas**: busca notas que contengan una palabra clave
- **Editar notas**: permite cambiar el contenido de una nota existente
- **Eliminar notas**: borra una nota seleccionada por número

## Estructura del proyecto

```
src/
├── App.java              → Clase principal, menús y flujo del programa
├── UserService.java      → Registro, login y gestión de users.txt
├── NoteService.java      → Crear, leer, buscar, editar y eliminar notas
├── Validator.java        → Validaciones de email y contraseña
└── data/
    ├── users.txt                         → Fichero con los usuarios (email;contraseña)
    └── usuarios/
        └── <email_sanitizado>/
            └── notas.txt                 → Notas del usuario
```

## Cómo ejecutar

1. Abrir el proyecto en VS Code con la extensión de Java
2. Ejecutar la clase `App.java`

O desde terminal:

```bash
javac -d bin src/*.java
cd bin
java App
```

**Nota:** el programa debe ejecutarse desde la raíz del proyecto para que las rutas a los ficheros funcionen bien.

## Tecnologías usadas

- Java (JDK 17+)
- `java.nio.file` para lectura/escritura de ficheros
- `try-with-resources` para gestionar los recursos
- Scanner para la entrada por consola

## Validaciones

- El email debe contener `@` y un punto en el dominio
- La contraseña debe tener mínimo 6 caracteres
- No se permite registrar un email que ya exista
- Las notas no pueden estar vacías

## Formato de los datos

**users.txt** - cada línea tiene un usuario:
```
email@ejemplo.com;contraseña123
otrousuario@mail.com;clave456
```

**notas.txt** - las notas se separan con `---`:
```
[6/3/2026 10:30]
Esta es mi primera nota
---
[6/3/2026 11:15]
Otra nota de ejemplo
---
```

## Autor

Joaquín - 1º DAM
