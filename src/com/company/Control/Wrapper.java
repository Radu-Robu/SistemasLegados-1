/**
 * Emulador.java
 * <p>
 * Clase encargada de las funcionalidades estrictamente relacionadas con la comunicación con la terminal s3270.
 * <p>
 * Alberto Pérez & Radu Constantin Robu - Diciembre 2021
 */
package com.company.Control;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class Wrapper {
    public static final String TERMINAL_SIN_PANTALLA = "s3270";
    public static final String CADENA_CONECTAR_HOST = "connect 155.210.71.101:823";
    public static final String PATRON_OBTENER_SALIDA = "^data: .*";
    public static final String FORMATO_CADENA_TEXTO = "String(\"%s\")";
    public static final String COMANDO_EXIT = "exit";
    public static final String COMANDO_ENTER = "enter";
    public static final String COMANDO_ASCII = "ascii";
    public static final String PATRON_DATA = "data:";
    public static final String USERNAME = "PROG";
    public static final String PASSWORD = "PROG123";
    public static final String TASKS2JOB = "tasks2.job";
    public static final String CADENA_EXIT = "e";
    public static final String CADENA_OFF = "off";
    public static final int DELAY_CONNECT = 100;
    public static final int DELAY_TASKS = 200;
    public static final int DELAY_ENTER = 700;
    public static final String SI = "y";
    public static final String NUEVO_FICHERO = "n";
    public static final String GUARDAR = "s";
    public static final String ANYADIR = "a";
    public static final String LISTAR = "l";
    public static final String ELIMINAR = "r";
    public static final String BUSCAR = "t";
    public static final String PATRON_NUMERO = "[+-]?\\d*(\\.\\d+)?";
    public static final String PATRON_LISTAR_TAREAS = "^ \\*\\*LIST TASK\\*\\*[ ]*$";
    public static final String PATRON_IDTAREA = "^ TASK NUMBER: .*$";
    public static final String PATRON_NOMBRE = "^ NAME.*$";
    public static final String PATRON_DESCRIPCION = "^ DESCRIPTION.*$";
    public static final String PATRON_FECHA = "^ DATE .*$";
    public static final String TEXTO_FECHA = " DATE       : ";
    public static final String TEXTO_DESCRIPCION = " DESCRIPTION: ";
    public static final String TEXTO_NOMBRE = " NAME       : ";
    public static final String TEXTO_ID_TAREA = " TASK NUMBER: ";

    private Process proceso;
    private PrintWriter out = null;
    private BufferedReader inStream = null;

    private enum CODIGO_ERROR {
        IDTAREA_INCORRECTO,
        NOMBRE_INCORRECTO,
        DESCRIPCION_INCORRECTA,
        FECHA_INCORRECTA,
        DATOS_TAREA_OK,
        OK
    }

    /**
     * Constructor de la clase.
     *
     * @throws IOException
     */
    public Wrapper() throws IOException, InterruptedException {
        inicializarTerminal();
    }

    /**
     * Inicializa la terminal sin pantalla para la conexión con el mainframe.
     *
     * @throws IOException
     */
    private void inicializarTerminal() throws IOException {
        proceso = Runtime.getRuntime().exec(TERMINAL_SIN_PANTALLA);
        inStream = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(proceso.getOutputStream()));
    }

    /**
     * Envía una cadena de texto a la terminal s3270.
     *
     * @param mensaje
     * @return
     */
    private int enviarString(String mensaje) {
        out.println(String.format(FORMATO_CADENA_TEXTO, mensaje));
        out.flush();
        return 1;
    }

    /**
     * Envía un ENTER a la terminal s3270.
     *
     * @return
     */
    private int enviarEnter() {
        try {
            out.println(COMANDO_ENTER);
            out.flush();
            sleep(DELAY_ENTER);
            return 1;
        } catch (InterruptedException e) {
            return -1;
        }
    }

    /**
     * Envía el comando "ascii" a la terminal s3270 para obtener el vector de string.
     *
     * @return
     */
    private int enviarAscii() {
        out.println(COMANDO_ASCII);
        out.flush();
        return 1;

    }

    /**
     * Obtiene la respuesta textual del mainframe en la terminal s3270.
     *
     * @return
     */
    private List<String> obtenerRespuestaMaquina() {
        List<String> resultado = new ArrayList();
        String line = "";
        try {
            while (inStream.ready() && (line = inStream.readLine()) != null) {
                if (line.matches(PATRON_OBTENER_SALIDA)) {
                    resultado.add(line.replace(PATRON_DATA, ""));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultado;

    }

    /**
     * Realiza el login en el mainframe.
     *
     * @return
     * @throws InterruptedException
     */
    private int login() throws InterruptedException, IOException {
        inicializarTerminal();
        out.println(CADENA_CONECTAR_HOST);
        out.flush();
        sleep(DELAY_CONNECT);
        enviarEnter();
        enviarString(USERNAME);
        enviarEnter();
        enviarString(PASSWORD);
        enviarEnter();
        enviarEnter();
        enviarString(TASKS2JOB);
        enviarEnter();
        sleep(DELAY_TASKS);
        return 0;
    }

    /**
     * Realiza la salida controlada del mainframe a través de la terminal s3270.
     *
     * @return
     * @throws IOException
     */
    private int logout() throws IOException {
        enviarString(CADENA_EXIT);
        enviarEnter();

        enviarString(CADENA_OFF);
        enviarEnter();
        salir();

        return 0;
    }

    /**
     * Termina la comunicación con la terminal s3270.
     *
     * @return
     */
    private int salir() {
        out.println(COMANDO_EXIT);
        out.flush();
        return 1;
    }


    /**
     * FUNCIONALIDADES TASKS2.JOB
     */

    /**
     * Opción de tasks2 para crear un nuevo fichero de tareas.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void nuevoFicheroTareas() throws IOException, InterruptedException {
        // TODO Comprobar si existen tareas y confirmar con usuario el nuevo fichero.
        login();
        enviarString(NUEVO_FICHERO);
        enviarEnter();
        enviarAscii();
        enviarString(SI);
        enviarEnter();
        enviarEnter();
        enviarAscii();
        logout();
    }

    /**
     * Opción de tasks2 para añadir una nueva tarea.
     *
     * @param idTarea
     * @param nombreTarea
     * @param descripcionTarea
     * @param fecha
     * @throws IOException
     * @throws InterruptedException
     */
    public CODIGO_ERROR anyadirTarea(String idTarea, String nombreTarea,
                                     String descripcionTarea, String fecha) throws IOException, InterruptedException {
        CODIGO_ERROR resultado = comprobacionAnyadirTarea(idTarea, nombreTarea, descripcionTarea, fecha);
        if (resultado == CODIGO_ERROR.DATOS_TAREA_OK) {
            login();
            enviarString(ANYADIR);
            enviarEnter();
            enviarString(idTarea);
            enviarEnter();
            enviarString(nombreTarea);
            enviarEnter();
            enviarString(descripcionTarea);
            enviarEnter();
            //TODO controlar la fecha en el cliente, formato: dd mm yyyy
            enviarString(fecha);
            enviarEnter();
            enviarEnter();
            guardarTarea();
            logout();
            return CODIGO_ERROR.OK;
        } else {
            return resultado;
        }
    }

    /**
     * Opción de tasks2 para eliminar una tarea.
     *
     * @param idTarea
     * @throws IOException
     * @throws InterruptedException
     */
    public CODIGO_ERROR eliminarTarea(String idTarea) throws IOException, InterruptedException {
        CODIGO_ERROR resultado = comprobacionEliminarTarea(idTarea);
        if (resultado == CODIGO_ERROR.DATOS_TAREA_OK) {
            login();
            enviarString(ELIMINAR);
            enviarEnter();
            enviarString(idTarea);
            enviarEnter();
            enviarString(SI);
            enviarEnter();
            enviarEnter();
            guardarTarea();
            logout();
            return CODIGO_ERROR.OK;
        } else {
            return resultado;
        }
    }

    /**
     * Opción de tasks2 para buscar las tareas de una fecha concreta.
     *
     * @param fecha
     * @throws IOException
     * @throws InterruptedException
     */
    public void buscarTareas(String fecha) throws IOException, InterruptedException {
        login();
        enviarString(BUSCAR);
        enviarEnter();
        enviarString(fecha);
        enviarEnter();
        enviarAscii();
        enviarEnter();
        List<String> resultado = obtenerRespuestaMaquina();
        logout();
        for (String line : resultado) {
            System.out.println(line);
        }
    }

    /**
     * Opción de tasks2 para listar las tareas.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void listarTareas() throws IOException, InterruptedException {
        login();
        enviarString(LISTAR);
        enviarEnter();
        enviarAscii();
        enviarEnter();
        List<String> resultado = obtenerRespuestaMaquina();
        logout();
        for (String line : resultado) {
            System.out.println(line);
        }
        //parsearTareas(resultado);
    }

    /**
     * Opción de tasks2 para guardar las tareas.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void guardarTareas() throws IOException, InterruptedException {
        login();
        enviarString(GUARDAR);
        enviarEnter();
        enviarEnter();
        logout();
    }

    /**
     * Guarda de forma implícita una tarea añadida o eliminada.
     */
    private void guardarTarea() {
        enviarString(GUARDAR);
        enviarEnter();
        enviarEnter();
    }


    private CODIGO_ERROR comprobacionAnyadirTarea(String idTarea, String nombre, String descripcion, String fecha) {
        if (idTarea.matches(PATRON_NUMERO)) {
            if (nombre.length() > 0 && nombre.length() <= 16) {
                if (descripcion.length() > 0 && descripcion.length() <= 32) {
                    //TODO TRATAR LA FECHAA
                    return CODIGO_ERROR.DATOS_TAREA_OK;
                } else {
                    return CODIGO_ERROR.DESCRIPCION_INCORRECTA;
                }
            } else {
                return CODIGO_ERROR.NOMBRE_INCORRECTO;
            }
        } else {
            return CODIGO_ERROR.IDTAREA_INCORRECTO;
        }
    }

    private CODIGO_ERROR comprobacionEliminarTarea(String idTarea) {
        if (idTarea.matches(PATRON_NUMERO)) {
            return CODIGO_ERROR.DATOS_TAREA_OK;
        } else {
            return CODIGO_ERROR.IDTAREA_INCORRECTO;
        }

    }

    private void comprobacionBuscarTarea() {
        //TODO TRATAR LA FECHA
    }

    /**

     private void obtenerTareas() throws InterruptedException, IOException {
     int codigo_error = 0;
     emulador.login();
     emulador.enviarString(LISTAR);
     emulador.enviarEnter();
     emulador.enviarAscii();
     emulador.enviarEnter();
     List<String> resultado = emulador.obtenerRespuestaMaquina();
     emulador.logout();
     parsearTareas(resultado);
     }
     private void parsearTareas(List<String> resultado) {
     String idTarea = "";
     String nombre = "";
     String descripcion = "";
     String fecha = "";

     for (String line : resultado) {
     if (line.matches(PATRON_IDTAREA)) {
     idTarea = line.replace(TEXTO_ID_TAREA, "").strip();
     } else if (line.matches(PATRON_NOMBRE)) {
     nombre = line.replace(TEXTO_NOMBRE, "").strip();
     } else if (line.matches(PATRON_DESCRIPCION)) {
     descripcion = line.replace(TEXTO_DESCRIPCION, "").strip();
     } else if (line.matches(PATRON_FECHA)) {
     fecha = line.replace(TEXTO_FECHA, "").strip();
     tareas.almacenarTarea(idTarea, new Tarea(idTarea, nombre, descripcion, fecha));
     }
     }
     }
     private int comprobacionTarea(String idTarea, String nombreTarea, String descripcionTarea) {

     */

}