package com.api.gestion.facturas.service.impl;

import com.api.gestion.facturas.constantes.FacturaConstantes;
import com.api.gestion.facturas.dao.FacturaDAO;
import com.api.gestion.facturas.pojo.Factura;
import com.api.gestion.facturas.security.jwt.JwtFilter;
import com.api.gestion.facturas.service.FacturaService;
import com.api.gestion.facturas.util.FacturaUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
//@Slf4j   //habilita el uso del registro de logs (logging) mediante la biblioteca SLF4J. Se crea una instancia de un logger log que puede ser utilizada para registrar mensajes en diferentes niveles (info, debug, error, etc.).
public class FacturaServiceImpl implements FacturaService {

    private static final Logger log = LoggerFactory.getLogger(FacturaServiceImpl.class); //habilita el uso de log - la anotacion @Slf4j  no me funciona debido a que esta importada de lombok

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private FacturaDAO facturaDAO;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) { //recibe un Map<String, Object> que contiene los datos para generar el reporte PDF. el mapa tiene la clave de tipo String y el Valor de tipo Object lo que indica que puede recibir cualquier tipo de dato(String, boolean, Integer, double, Object, etc.)
        log.info("Dentro del método generar reporte");
        try{
            String fileName; //nombre del archivo del reporte pdf a generar
            if(validateRequestMap(requestMap)){ //validateRequestMap valida que requestMap que se recibe contenga los campos isGenerate

                //valida si ya esta generado, si ya está extrae el uuid y lo guarda en fileName.(Ya esta generado - Actualiza extrae el uuid para volverlo a colocar despues)
                if(requestMap.containsKey("isGenerate") && !(Boolean)requestMap.get("isGenerate")){ //valida que requestMap contenga isGenerate y obtiene del requestMap y castea a boolean y valida si isGenerate no sea false(valor por defecto de boolean es false). Debe ser true
                    fileName = (String) requestMap.get("uuid"); //en la fileName guarda el uuid que se castea a String luedo de obtenerse del requestMap
                }
                else{ //Si el reporte no existe, si no esta generado: isGenerate=false. Se Crea, le agrega un uuid por medio del metodo getUUID() de FacturaUtils
                    fileName = FacturaUtils.getUUID();  //en la fileName guarda el uuid que crea el metodo getUUID() de FacturaUtils
                    requestMap.put("uuid",fileName); //se agrega el uuid generado al requestMap
                    insertarFactura(requestMap); //llama al metodo de crear factura y le envia el requestMap con el uuid generado
                }

                //Construye una cadena de texto con información del nombre, contacto, email y metodo de pago obtenida del requestMap
                String data = "Nombre : " + requestMap.get("nombre") + "\nNumero de contacto : " + requestMap.get("numeroContacto") + "" +
                        "\n" + "Email : " + requestMap.get("email") + "\n" + "Metodo de pago : " + requestMap.get("metodoPago");

                Document document = new Document(); //Crea una instancia(objeto) de Document llamado document
                //Asocia el documento con un PdfWriter que escribe el contenido en un archivo.
                PdfWriter.getInstance(document,new FileOutputStream(FacturaConstantes.STORE_LOCATION+"\\"+fileName+".pdf")); //se envia el documento y FileOutputStream Recibe la ruta donde se creará el reporte pdf

                document.open(); //Abre el documento para su escritura.
                setRectangleInPdf(document); //Llama al método setRectangleInPdf para agregar un rectángulo decorativo.

                //Crea un encabezado de párrafo  y se le asigna el formato "Header" establecido en la funcion getFont
                Paragraph paragrapHeader = new Paragraph("Gestión de categorias y productos\n",getFont("Header"));
                paragrapHeader.setAlignment(Element.ALIGN_CENTER); // al paragrapHeader lo centra en la página.
                document.add(paragrapHeader); //agrega al documento: paragrapHeader

                //Crea una tabla con cinco columnas y establece su ancho al 100% del documento.
                PdfPTable pdfPTable = new PdfPTable(5);
                pdfPTable.setWidthPercentage(100); //ancho al 100% del documento
                addTableHeader(pdfPTable); //Agrega los encabezados de la tabla en el pdf llamando a addTableHeader

                //Convierte la cadena de detalles del producto(String) en un objeto JSONArray.
                JSONArray jsonArray = FacturaUtils.getJsonArrayFromString((String)requestMap.get("productoDetalles"));

                //Itera sobre cada elemento del JSONArray jsonArray y agrega filas a la tabla mediante el método addRows
                for(int i = 0;i < jsonArray.length();i++){
                    //Llama al método addRows y le pasa la tabla pdfPTable junto con el mapa de datos generado.
                    addRows(pdfPTable,FacturaUtils.getMapFromJson(jsonArray.getString(i)));
                }
                document.add(pdfPTable); //agrega al document la tabla pdfPTable

                //Crea un pie de página con el monto total y un mensaje de agradecimiento
                Paragraph footer = new Paragraph("Total : " + requestMap.get("montoTotal") + "\n" +
                        "Gracias por visitarnos, vuelva pronto !!",getFont("Data"));
                document.add(footer); //agrega el footer al document

                document.close();  //Cierra el documento para finalizar la escritura.

                //Devuelve una respuesta HTTP exitosa (200 OK) con el UUID del archivo PDF.
                return new ResponseEntity<>("{\"uuid\":\""+fileName+"\"}", HttpStatus.OK);
            }
            //si no logra pasar la validacion de validateRequestMap(requestMap)
            return FacturaUtils.getResponseEntity("Datos requeridos no encontrados",HttpStatus.BAD_REQUEST);
        //Captura cualquier excepción y muestra el error en la consola
        }catch (Exception exception){
            exception.printStackTrace();
        }
        //Si ocurre algún error, devuelve una respuesta HTTP con estado 500 Internal Server Error.
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Factura>> getFacturas() {
        List<Factura> facturas = new ArrayList<>();
        if(jwtFilter.isAdmin()){
            facturas = facturaDAO.getFacturas(); //muestras todas las facturas
        }
        else{ //si no es admin permite mmostrar las facturas del cliente actual
            facturas = facturaDAO.getFacturaByUsername(jwtFilter.getCurrentUser());
        }
        return new ResponseEntity<>(facturas, HttpStatus.OK); //retorna las facturas y status OK
    }


    //obtener el contenido de un archivo PDF en formato de bytes
    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        log.info("Dentro de getPdf : requestMap{}",requestMap);
        try{
            byte[] bytesArray = new byte[0];  //Inicializa un array de bytes vacío para contener el contenido del PDF.

            //Si requestMap no contiene la clave uuid y la validación del mapa falla, devuelve un ResponseEntity con el array de bytes vacío y el estado BAD_REQUEST (404
            if(!requestMap.containsKey("uuid") && validateRequestMap(requestMap)){
                return new ResponseEntity<>(bytesArray,HttpStatus.BAD_REQUEST); //retorna un array vacio y respuesta 404
            }

            //Construye la ruta completa del archivo PDF a partir de la ubicación almacenada en FacturaConstantes.STORE_LOCATION y el valor de uuid
            String filePath = FacturaConstantes.STORE_LOCATION+"\\"+(String) requestMap.get("uuid")+".pdf";
            log.info("Imprimiendo la ruta");
            log.info(filePath);

            //Verifica si el archivo PDF existe en la ruta especificada. Si el archivo existe, lo lee en un array de bytes y devuelve el contenido con un estado HTTP OK.
            if(FacturaUtils.isFileExist(filePath)){
                bytesArray = getByteArray(filePath);
                return new ResponseEntity<>(bytesArray,HttpStatus.OK);
            }
            else{  //Si el archivo no existe, Genera el reporte PDF con generateReport(requestMap). Lo lee y devuelve el bytesArray con Status Ok
                requestMap.put("isGenerate",false);
                generateReport(requestMap);
                bytesArray = getByteArray(filePath); //Lee el contenido del archivo generado en el array de bytes.
                return new ResponseEntity<>(bytesArray,HttpStatus.OK); //Devuelve el contenido del PDF con estado HTTP OK
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> deleteFactura(Integer id) {
        try{
            Optional optional = facturaDAO.findById(id);
            if(!optional.isEmpty()){
                facturaDAO.deleteById(id);
                return FacturaUtils.getResponseEntity("Factura eliminada",HttpStatus.OK);
            }
            return FacturaUtils.getResponseEntity("No existe la factura con ese ID",HttpStatus.NOT_FOUND);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Leerá todo el contenido del archivo pdf y devolverá un array de bytes (byte[]) que puede ser enviado, almacenado o procesado.
    private byte[] getByteArray(String filePath) throws IOException {
        File initialFile = new File(filePath); //Crea una instancia de File que representa el archivo en la ruta especificada por filePath
        InputStream inputStream = new FileInputStream(initialFile); //Abre un flujo de entrada (InputStream) para leer el contenido del archivo
        byte[] byteArray = IOUtils.toByteArray(inputStream);  //la clase IOUtils de la librería Apache Commons IO para leer todo el contenido del archivo en un array de bytes (byte[]). Útil cuando se maneja archivos binarios, como imágenes, archivos PDF, o documentos.
        inputStream.close(); //Cierra el flujo de entrada para liberar los recursos asociados.
        return byteArray; //Devuelve el array de bytes que contiene el contenido del archivo.
    }

    //Establece un Rectangulo como borde decorativo en el PDF
    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Dentro de setRectangleInPdf");

        // Define las coordenadas del rectángulo (x, y) inferior izquierda y (x, y) superior derecha
        Rectangle rectangle = new Rectangle(577,825,18,15);
        rectangle.enableBorderSide(1); // Lado izquierdo
        rectangle.enableBorderSide(2); // Lado superior
        rectangle.enableBorderSide(4); // Lado derecho
        rectangle.enableBorderSide(8); // Lado inferior

        rectangle.setBorderColor(BaseColor.BLACK);  // Establece el color del borde del rectángulo color negro
        rectangle.setBorderWidth(1); // Define el grosor del borde del rectángulo
        document.add(rectangle);  // Agrega el rectángulo al documento PDF
    }
    // Devuelve una fuente personalizada según el tipo especificado que se recibe(Header o Data)

    private Font getFont(String type){
        log.info("Dentro de getFont");

        switch (type){  // Evalúa el tipo de fuente solicitado
            case "Header":  //tipo de letra, tamaño y color para el encabezado
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE,18,BaseColor.BLACK); // Crea una fuente Helvetica, negrita y cursiva, tamaño 18, color negro
                headerFont.setStyle(Font.BOLD); //Asegura que la fuente tenga estilo en negrita
                return headerFont; //retorna el diseño aplicado.

            case "Data": //tipo de letra, tamaño y color para la Data
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN,11,BaseColor.BLACK); // Crea una fuente Times Roman, tamaño 11, color negro
                dataFont.setStyle(Font.BOLD);  // Asegura que la fuente tenga estilo en negrita
                return dataFont; //retorna el diseño aplicado.

            default:
                return new Font(); //por defecto regresa el diseño por defecto
        }
    }

    // Método para agregar filas de datos a la tabla en el PDF
    private void addRows(PdfPTable pdfPTable,Map<String,Object> data){ //recibe un objeto pdfPTable y una mapa con los datos: data
        log.info("Dentro de addRows");  // Imprime en el log que se ha ingresado al método
        pdfPTable.addCell((String)data.get("nombre"));   // Agrega una celda con el valor correspondiente al nombre del producto que se obtiene por medio del mapa data que se recibe
        pdfPTable.addCell((String)data.get("categoria"));  //Del mapa data que se recibe se obtiene el contenido de categoria y la agrega a la celda del objeto pdfPTable
        pdfPTable.addCell((String)data.get("cantidad"));
        pdfPTable.addCell(Double.toString((Double)data.get("precio"))); //Del mapa data que se recibe se obtiene el contenido de precio luego castea ese valor y lo convierte de Object a Double, y luego convierte ese Double a String para agregarlo a la celda del objeto pdfPTable
        pdfPTable.addCell(Double.toString((Double)data.get("total")));
    }

    // Método que agrega los encabezados de la tabla en el PDF: "Nombre","Categoria","Cantidad","Precio","Sub Total
    private void addTableHeader(PdfPTable pdfPTable){
        log.info("Dentro del addTableHeader");
        //Stream.of(...): Crea un flujo (Stream) de elementos a partir de una lista explícita de valores que son los títulos de las columnas de la tabla. Este flujo Stream permite aplicar operaciones funcionales como forEach, map, filter, etc.
        Stream.of("Nombre","Categoria","Cantidad","Precio","Sub Total")
                .forEach(columnTitle -> {  // // Recorre cada título de columna(columnTitle) y por cada una realiza lo siguiente
                    PdfPCell pdfPCell = new PdfPCell(); //crea una instancia de pdfPCell, que Crea una celda para el encabezado de la tabla
                    pdfPCell.setBackgroundColor(BaseColor.LIGHT_GRAY); // Establece el color de fondo de la celda
                    pdfPCell.setBorderWidth(2);  // Establece el ancho del borde de la celda
                    pdfPCell.setPhrase(new Phrase(columnTitle)); // Asigna el texto del título de la columna como contenido de la celda
                    pdfPCell.setBackgroundColor(BaseColor.YELLOW); // Cambia el color de fondo de la celda a amarillo (sobrescribe el color anterior)
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);  // Alinea el contenido de la celda al centro
                    pdfPTable.addCell(pdfPCell);  // Agrega la celda al objeto PdfPTable
                });
    }

    //Metodo para insertar o guardar una factura, recibe un mapa que trae los data llamado requestMap - No retorna Nada
    private void insertarFactura(Map<String,Object> requestMap){
        try{
            Factura factura = new Factura(); //crea un objeto Factura llamado factura.
            //de requestMap que se recibe se extrae la data de los campos se castea(convierte de tipo Object a String) y se asigna a los campos del objeto factura
            factura.setUuid((String)requestMap.get("uuid")); //del requestMap se extrae el valor uuid y convierte de Objet a String luego lo asigna al campo uuid del objeto factura
            factura.setNombre((String)requestMap.get("nombre"));
            factura.setEmail((String)requestMap.get("email"));
            factura.setNumeroContacto((String)requestMap.get("numeroContacto"));
            factura.setMetodoPago((String) requestMap.get("metodoPago"));
            factura.setTotal(Integer.parseInt((String) requestMap.get("montoTotal")));
            factura.setProductoDetalles((String)requestMap.get("productoDetalles"));
            factura.setCreatedBy(jwtFilter.getCurrentUser()); //se asigna quien creo la factura asignando al campo createdBy, obteniendo el usuarioActual por medio de jwtFilter
            facturaDAO.save(factura); //guarda la factura pasando los datos del objeto factura al metodo save.
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }


    //Valida el mapa requestMap contenga los campos: nombre, numeroContacto, email, metodoPago, .... retorna un boolean true(si estan todos los datos definidos) o false(si no cumple algun dato definido)
    private boolean validateRequestMap(Map<String,Object> requestMap){
        return requestMap.containsKey("nombre") &&
                requestMap.containsKey("numeroContacto") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("metodoPago") &&
                requestMap.containsKey("productoDetalles") &&
                requestMap.containsKey("montoTotal");
    }
}
