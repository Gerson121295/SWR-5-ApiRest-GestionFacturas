package com.api.gestion.facturas.util;

import com.api.gestion.facturas.service.impl.FacturaServiceImpl;
import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//@Slf4j
public class FacturaUtils {

    private static final Logger log = LoggerFactory.getLogger(FacturaServiceImpl.class); //habilita el uso de log - la anotacion @Slf4j  no me funciona debido a que esta importada de lombok

    private FacturaUtils(){
    }

    //Método estático devuelve una instancia de ResponseEntity<String>, permite enviar respuestas HTTP personalizadas.
    public static ResponseEntity<String> getResponseEntity(String message, HttpStatus httpStatus){ //recibe el mensaje que se incluira y el estado de la respuesta.
        return new ResponseEntity<String>("Mensaje : " + message,httpStatus); //retorna el mensaje y el codigo de estado
    }
    //Ejemplo de uso: return FacturaUtils.getResponseEntity("Operación exitosa", HttpStatus.OK);

    //Genera el codigo UUID Universal Unico de una factura por medio de la fecha actual
    public static String getUUID(){ //retorna un string
        Date date = new Date(); //Crea una nueva instancia de Date, que representa la fecha y hora actuales.
        long time = date.getTime();
        return "FACTURA-" + time; //retorna FACTURA-"tiempo actual como UUID"
    }

    //Metodo convierte data string a JSONArray y devuelve JSONArray. -Permitira obtener los datos y guardarlos en un arreglo
    public static JSONArray getJsonArrayFromString(String data) throws JSONException {
        JSONArray jsonArray = new JSONArray(data); // Convierte la cadena de texto `data` a un JSONArray
        return jsonArray;
    }

    /*  -- Ejemplo de USO:
    String data = "[{\"nombre\":\"Producto1\",\"precio\":10.0},{\"nombre\":\"Producto2\",\"precio\":20.5}]";
            JSONArray jsonArray = getJsonArrayFromString(data);
            System.out.println(jsonArray);
     */

    //Convierte una cadena JSON en un mapa (Map) de tipo clave-valor (String, Object)(eso devuelve).
    //Cuando el valor del mapa es de tipo Object, significa que puede almacenar cualquier tipo de dato en el valor (números, cadenas, listas, otros mapas, etc.), es mas flexible
    public static Map<String,Object> getMapFromJson(String data){ //Recibe un String data, que se espera sea un JSON válido.
        if(!Strings.isNullOrEmpty(data)){ //si data no es nulo o vacio
            //Usa Gson para convertir el JSON (data) en un objeto Map<String, Object>.
            //se agrega la data, y se obtiene el tipo de data(TypeToken)
            return new Gson().fromJson(data,new TypeToken<Map<String,Object>>(){
            }.getType());  //new TypeToken<Map<String, Object>>() {}.getType() indica a Gson el tipo de dato exacto que debe interpretar (genérico Map<String, Object>)
        }
        return new HashMap<>(); //Si data es nulo o vacío, devuelve un mapa vacío.
    }
    /* Ejemplo de Uso:
         String jsonData = "{\"nombre\":\"Gerson\",\"edad\":30}";
            Map<String, Object> map = getMapFromJson(jsonData);
            System.out.println(map); // Imprime: {nombre=Gerson, edad=30.0}
    */


    //Verifica si un archivo existe en la ruta especificad
    public static boolean isFileExist(String path){ //Se recibe el parámetro path, que es la ruta del archivo a comprobar.
        log.info("Dentro de isFileExist{}",path);
        try{
            File file = new File(path); //Se crea una instancia del objeto File con la ruta especificada.
            //si file no es igual a null y file existe(es true): Si ambas condiciones son verdaderas, retorna un Boolean(TRUE), si no retorna FALSE.
            return file != null && file.exists() ? Boolean.TRUE : Boolean.FALSE;
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return false; //Si ocurre una excepción o no se encuentra el archivo, se devuelve false.
    }


}
