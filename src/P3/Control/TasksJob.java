package P3.Control;

import P3.Modelo.Tarea;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;


public class TasksJob implements TasksAPI {
    private static final String NUEVO_FICHERO = "N";
    private static final String GUARDAR = "s";
    private static final String ANYADIR = "a";
    private static final String LISTAR = "l";
    private static final String ELIMINAR = "r";
    private static final String BUSCAR = "t";
    private static final String SI = "y";

    private static final String PATRON_NUMERO = "[+-]?\\d*(\\.\\d+)?";
    public static final String PATRON_IDTAREA = "^data: TASK NUMBER: .*$";
    public static final String PATRON_NOMBRE = "^data: NAME.*$";
    public static final String PATRON_DESCRIPCION = "^data: DESCRIPTION.*$";
    public static final String PATRON_FECHA = "^data: DATE .*$";
    public static final String TEXTO_FECHA = "data: DATE       : ";
    public static final String TEXTO_DESCRIPCION = "data: DESCRIPTION: ";
    public static final String TEXTO_NOMBRE = "data: NAME       : ";
    public static final String TEXTO_ID_TAREA = "data: TASK NUMBER: ";

    public static final String MENU_TASKS2 = "**MENU**";
    public static final String MENSAJE_NUEVO_FICHERO = "**NEW TASK FILE**";
    public static final String FICHERO_TAREAS_CREADO = "NEW TASK FILE HAS BEEN CREATED";
    public static final String MENSAJE_SALIDA = "BYE";
    public static final String MENSAJE_GUARDAR_TAREAS = "SAVE TASKS";
    public static final String MENSAJE_LISTAR_TAREAS = "**LIST TASK**";
    public static final String TAREAS_GUARDADAS = "TASKS HAVE BEEN SAVED";
    public static final String MENSAJE_FIN_LISTA = "**END**";
    public static final String MENSAJE_BUSCAR_TAREAS = "**SEARCH TASK**";
    public static final String MENSAJE_CONFIRMAR_ELIMINAR = "CONFIRM (Y/N)";
    public static final String MENSAJE_TAREA_NO_ENCONTRADA = "TASK NOT FOUND";
    public static final String MENSAJE_ELIMINAR_TAREA = "**REMOVE TASK**";

    private Mainframe mainframe;


    public TasksJob(Mainframe mainframe) throws IOException {
        //TODO HACER SINGLETON
        this.mainframe = mainframe;
    }


    /**
     * Opción de tasks2 para crear un nuevo fichero de tareas.
     *
     * @Override
     */
    public boolean nuevoFicheroTareas() throws IOException, InterruptedException {
        if(mainframe.enviarString(NUEVO_FICHERO)){
            if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
                if(mainframe.esperarPantalla(MENSAJE_NUEVO_FICHERO)){
                    if(mainframe.enviarString(SI)){
                        if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
                            if(mainframe.esperarPantalla(FICHERO_TAREAS_CREADO)){
                                if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
                                    if(mainframe.esperarPantalla(MENU_TASKS2)){
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
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
     * @Override
     */
    public CODIGO_ERROR anyadirTarea(String idTarea, String nombreTarea, String descripcionTarea, String fecha)
            throws IOException, InterruptedException{

           if(existeIdTarea(idTarea)){
               return CODIGO_ERROR.IDTAREA_REPETIDO;
           }

            if(mainframe.enviarString(ANYADIR)){
                if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
                    if(mainframe.esperarPantalla("**ADD TASK**")){
                        if(mainframe.enviarString(idTarea)){
                            if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
                                if(mainframe.enviarString(nombreTarea)){
                                    if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
                                        if(mainframe.enviarString(descripcionTarea)){
                                            if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
                                                if(mainframe.enviarString(fecha)){
                                                    if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
                                                        if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
                                                            if(mainframe.esperarPantalla(MENU_TASKS2)){
                                                                return CODIGO_ERROR.OK;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return CODIGO_ERROR.NOK;
    }

    private boolean existeIdTarea(String idTarea) throws IOException, InterruptedException {
        List<Tarea> tareas = listarTareas();
        for(Tarea t :tareas){
            if(t.getId().equals(idTarea)){
                return true;
            }
        }
        return false;
    }


    /**
     * Opción de tasks2 para eliminar una tarea.
     *
     * @param idTarea
     * @Override
     */
    public CODIGO_ERROR eliminarTarea(String idTarea) throws IOException, InterruptedException {
        if(mainframe.enviarString(ELIMINAR)){
            if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
                if(mainframe.esperarPantalla(MENSAJE_ELIMINAR_TAREA)){
                    if(mainframe.enviarString(idTarea)){
                        if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
                            if(mainframe.esperarPantalla(MENSAJE_TAREA_NO_ENCONTRADA)){
                                if(mainframe.enviarComando(MainframeAPI.COMANDO_ENTER)){
                                    if(mainframe.esperarPantalla(MENU_TASKS2)){
                                        return CODIGO_ERROR.IDTAREA_INCORRECTO;
                                    }
                                }
                            }else if(mainframe.esperarPantalla(MENSAJE_CONFIRMAR_ELIMINAR)){
                                if(mainframe.enviarString(SI)){
                                    if(mainframe.enviarComando(MainframeAPI.COMANDO_ENTER)){
                                        if(mainframe.enviarComando(MainframeAPI.COMANDO_ENTER)){
                                            if(mainframe.esperarPantalla(MENU_TASKS2)){
                                                return CODIGO_ERROR.OK;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return CODIGO_ERROR.NOK;
    }

    /**
     * Opción de tasks2 para buscar las tareas de una fecha concreta.
     *
     * @param fecha
     * @throws IOException
     * @throws InterruptedException
     * @Override
     */
    public List<Tarea> buscarTareas(String fecha) throws IOException, InterruptedException{
        List<Tarea> tareas = new ArrayList();
        if(mainframe.enviarString(BUSCAR)){
            if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
                if(mainframe.esperarPantalla(MENSAJE_BUSCAR_TAREAS)){
                    if(mainframe.enviarString(fecha)){
                        if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
                            if(mainframe.esperarPantalla(MENSAJE_FIN_LISTA)){
                                if(mainframe.enviarComando(Mainframe.COMANDO_ASCII)){
                                    tareas = obtenerListaTareas();
                                    if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
                                        if(mainframe.esperarPantalla(MENU_TASKS2)) {
                                            return tareas;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return tareas;
    }

    /**
     * Opción de tasks2 para listar las tareas.
     *
     * @throws IOException
     * @throws InterruptedException
     * @Override
     */
    public List<Tarea> listarTareas() throws IOException, InterruptedException{
        List<Tarea> tareas = new ArrayList();
        if(mainframe.enviarString(LISTAR)){
           if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
               if(mainframe.esperarPantalla(MENSAJE_LISTAR_TAREAS)){
                   if(mainframe.enviarComando(Mainframe.COMANDO_ASCII)){
                       tareas = obtenerListaTareas();
                       if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
                           if(mainframe.esperarPantalla(MENU_TASKS2)){
                               return tareas;
                           }
                       }
                   }
               }
           }
        }
        return tareas;
    }

    private List<Tarea> obtenerListaTareas() throws IOException {
        String resultado = mainframe.obtenerRespuestaMaquina();
        String[] lineas = resultado.split(System.getProperty("line.separator"));
        return parsearTareas(lineas);
    }

    /**
     * Parsea la lista de tareas recibida del mainframe.
     *
     * @param resultado
     * @return
     */
    private List<Tarea> parsearTareas(String[] resultado) {
        String idTarea = "";
        String nombre = "";
        String descripcion = "";
        String fecha = "";
        List<Tarea> tareas = new ArrayList();

        for (String line : resultado) {
            if (line.matches(PATRON_IDTAREA)) {
                idTarea = line.replace(TEXTO_ID_TAREA, "").strip();
            } else if (line.matches(PATRON_NOMBRE)) {
                nombre = line.replace(TEXTO_NOMBRE, "").strip();
            } else if (line.matches(PATRON_DESCRIPCION)) {
                descripcion = line.replace(TEXTO_DESCRIPCION, "").strip();
            } else if (line.matches(PATRON_FECHA)) {
                fecha = line.replace(TEXTO_FECHA, "").strip();
                tareas.add(new Tarea(idTarea, nombre, descripcion, fecha));
            }
        }
        return tareas;
    }

    /**
     * Opción de tasks2 para guardar las tareas.
     *
     * @throws IOException
     * @throws InterruptedException
     * @Override
     */
    public boolean guardarTareas() throws IOException, InterruptedException {
        if(mainframe.enviarString(GUARDAR)){
            if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
                if(mainframe.esperarPantalla(TAREAS_GUARDADAS)){
                    if(mainframe.enviarComando(Mainframe.COMANDO_ENTER)){
                        if(mainframe.esperarPantalla(MENU_TASKS2)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Termina la comunicación con la terminal s3270.
     *
     * @return
     * @Override
     */
    public boolean salir(String guardarTareas) throws IOException, InterruptedException {
        if(mainframe.enviarString(Mainframe.COMANDO_EXIT)){
            if(mainframe.enviarComando(MainframeAPI.COMANDO_ENTER)){
                if(mainframe.esperarPantalla(MENSAJE_SALIDA)){
                    if(mainframe.enviarComando(MainframeAPI.COMANDO_ENTER)){
                        return true;
                    }
                }else if(mainframe.esperarPantalla(MENSAJE_GUARDAR_TAREAS)){
                    if(mainframe.enviarString(guardarTareas)){
                        if(mainframe.enviarComando(MainframeAPI.COMANDO_ENTER)){
                            if(mainframe.esperarPantalla(MENSAJE_SALIDA)){
                                if(mainframe.enviarComando(MainframeAPI.COMANDO_ENTER)){
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}