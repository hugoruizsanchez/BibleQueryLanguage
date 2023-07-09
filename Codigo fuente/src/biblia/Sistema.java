package biblia;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Sistema {
	
	/**
	 * Variables del sistema
	 */
   	
	public boolean ver_versiculos = true; // Ver versículos sí/no
	public String version = "rv60.xml"; // Versión de la biblia de entre las disponibles
	public int espaciado = 0; // Espacios que tendrán los versículos. Si es 0, estarán juntos.  
	public int delimitador =0; // Delimitador entre palabras. 
	public int formato =0; // Formato en que se leerán los versículos: / 0. Mt 5:3-2 / 1. Mt 5.3-2 / 2. Mt 5,3-2; 

	
	
    /**
     * Constructor
     */
    
    public Sistema () {
        
    }
    
    /**
     * El método de guardar pasajes recibirá una entrada, en base a la cual asignará valores numéricos a cada pasaje.
     * @param entrada
     * @return
     */

    
    public ArrayList<Pasaje> guardarPasajes (String entrada) {
        
        // CREACIÓN DE OBJETOS -> utilidades, para utilizar la clase de utilidades, y un conjunto indeterminado de pasajes
        
        ArrayList<Pasaje> listaPasajes = new ArrayList<Pasaje>();
        
        // CONFIGURACIÓN DE LA ENTRADA: 
        
        entrada = entrada.replaceAll("\\s", "");
        entrada = entrada.toUpperCase();
        //Ajuste de método, simplemente adaptará la entrada a los valores originales. En la abreviatura se realizará el proceso inverso
        switch (formato) {
        case 0:
        break;
        case 1:
        entrada = entrada.replaceAll("\\.", ":");
        break;
        case 2:
        entrada = entrada.replaceAll(",", ":");
        break;
        default:
        break;
        }
         
        if (entrada.charAt(entrada.length() - 1) != ';') {
            entrada = entrada + ';';
        }
        
        // SEPARACION DE LOS PASAJES: CONVERSIÓN DE STRING A ARRAYLIST EN FORMA DE PASAJES SEPARADOS
      
        //El formato 2 no es compatible con las "," como separadores.  
        String[] pasajes_separados_cast = {};
        
        if (formato != 2) {
        pasajes_separados_cast = entrada.split("[;,]");
        }
        else {
        pasajes_separados_cast = entrada.split("[;]");
        }
         
        
        ArrayList<String> pasajes_separados = new ArrayList<String>(Arrays.asList(pasajes_separados_cast));
        
        // EXTRACCIÓN DE CADA PASAJE
          
            // VARIABLES A UTILIZAR
        
        int[] libros_con_espacios = {64, 63, 62, 61, 60, 55, 54, 53, 52, 47, 46, 14, 13, 12, 11, 10, 9}; // Almacena los libros concretos que tienen espacios en sus abreviaturas. 
        int [] libros_uncap = {31,57,65,63,64};
        
        
        int libro = 0;
        int capitulo = 0;
        int versiculo_inicial = 0;
        int versiculo_final = 0;
        boolean capitulo_dos = false;

        boolean compuesto = false; // Versículo compuesto, p.ej "Mt 5:7-12:3"
        boolean unico = false; // Capitulo que engloba todos los versículos
        boolean con_espacios = false; // Versículo al que le  que acompaña un libro con espacios p.ej "1 R 4:21". 
        boolean uncap = false; // Si el libro pertenece a la lista de un solo capítulo
        String abreviatura =""; 
        ArrayList<String> nombresCompuestos = new ArrayList<String>();
        ArrayList<Integer> longitudCompuestos = new ArrayList<Integer>();
        
            // RECORRIDO DEL BUCLE
        
        for (int i = 0; i < pasajes_separados.size(); i++) {


            // RECONOCIMIENTO DE LIBRO
            // En base a libroInt se extrae el valor numérico de libro.
            // Si la salida es error (99), es porque no se especifica, por lo que se obvia 
            // que es un número que forma parte del mismo libro y se omite.
           
            if (libroInt(pasajes_separados.get(i)) != 99) {
                libro = libroInt(pasajes_separados.get(i));
            }
                  
            
            // EXCEPCIONES: LIBROS CON UN SOLO CAPITULO
            
            //COMPROBACION
            
            for (int k=0; k<libros_uncap.length; k++) {
                if (libro == libros_uncap [k]) {
                    uncap = true;
                }
            }
            
            if (pasajes_separados.get(i).matches(".*[a-zA-Z].*")) {
                
            }
            else {
                uncap = false;
            }
            
            if (pasajes_separados.get(i).contains(":") || pasajes_separados.get(i).equals("FLM1") || pasajes_separados.get(i).equals("FILEMON1") || pasajes_separados.get(i).equals("JUD1") || pasajes_separados.get(i).equals("JUDAS1") | pasajes_separados.get(i).equals("JUDAS1") || pasajes_separados.get(i).equals("ABD1") || pasajes_separados.get(i).equals("ABDIAS1")|| pasajes_separados.get(i).equals("2JN")|| pasajes_separados.get(i).equals("2JUAN")|| pasajes_separados.get(i).equals("3JN")|| pasajes_separados.get(i).equals("3JUAN")) {
                uncap = false;
            }
            
            if (uncap == true) {
                
                int ultima_letra_int =0;
                
                for (int v =0; v<pasajes_separados.get(i).length(); v++) {
                    
                    char letra = pasajes_separados.get(i).charAt(v);
                    
                    if (Character.isLetter(letra) == true) {
                        ultima_letra_int = v;
                    }
                    
                }
                
                String ultima_letra_string = pasajes_separados.get(i).charAt(ultima_letra_int)+"";
                
                
                
                String separacion_extraordinaria [] = pasajes_separados.get(i).split(ultima_letra_string);
                
                pasajes_separados.set(i, separacion_extraordinaria [0]+ultima_letra_string+"1:"+separacion_extraordinaria [1]);
                
                
            }
           

            // RECONOCIMIENTO DE CAPÍTULO 
            // Array_capitulo es un split que quita todos los caracteres excepto los numeros, y ":", "-""
            // Si es un capítulo sin versículos, se asume que va del 1 al limite y se añade la excepcion
            
            if (pasajes_separados.get(i).contains(":") == true) {

                // COMPROBAR SI EL LIBRO TIENE ESPACIOS - si no, no podra identificars el capitulo - 
                
                for (int z = 0; z < libros_con_espacios.length; z++) {

                    if (libro == libros_con_espacios[z]) {
                        con_espacios = true;

                    }
                }

                // SI TIENE ESPACIOS
                
                if (con_espacios == true) {

                    String array_capitulo[] = pasajes_separados.get(i).split(":");
                    
                    int ultima_letra_int =0;
                    
                    for (int c =0; c<array_capitulo [0].length(); c++) {
                    	
                    	char letra = array_capitulo [0].charAt(c);
                    	if (Character.isLetter(letra) == true) {
                            ultima_letra_int = c;
                        }
                    	
                    }
                    
                    String ultima_letra_string = pasajes_separados.get(i).charAt(ultima_letra_int)+"";
                    
                    String array_capitulo_completo[] = array_capitulo[0].split(ultima_letra_string);
                    
                    capitulo = Integer.parseInt(array_capitulo_completo[1].replaceAll("\\s", ""));

                // SI NO TIENE ESPACIOS
                } else {
                    String array_capitulo[] = pasajes_separados.get(i).replaceAll("[^\\d:-]", "").split(":");
                    capitulo = Integer.parseInt(array_capitulo[0]);
                }
           
            } else {
                
                // SI ES UN SOLO NUMERO ES UN VERSICULO SUELTO Y SE POSPONE \\d+
                
                if (pasajes_separados.get(i).replaceAll("\\s", "").matches("[0-9\\-]+") == true) {
     
                
                } else {

                    for (int z = 0; z < libros_con_espacios.length; z++) {

                        if (libro == libros_con_espacios[z]) {
                            con_espacios = true;

                        }
                    }
                    
    
                   // RECONOCIMIENTO DE DOS CAPITULOS TES EL CASO MAMOOOON
                    if (pasajes_separados.get(i).contains("-")) {
                   	    
                        
                        if (con_espacios == true) {
                            unico = true;
                            compuesto = true;
                            
                            // ******  Modificar más adelante: que el split detecte la última letra [A-Z] y lo haga a partir de ahí. 
                            
                            int ultima_letra_int =0;
                            
                            for (int c =0; c<pasajes_separados.get(i).length(); c++) {
                            	
                            	char letra = pasajes_separados.get(i).charAt(c);
                            	if (Character.isLetter(letra) == true) {
                                    ultima_letra_int = c;
                                }
                            	
                            }
                            
                            String ultima_letra_string = pasajes_separados.get(i).charAt(ultima_letra_int)+"";
                            
                            String[] capitulo_multiple_array_separado = pasajes_separados.get(i).split(ultima_letra_string); 
                            String [] array_capitulo_multiple = capitulo_multiple_array_separado [1].split("-");
                            
                            
                             int capituloinicial = Integer.parseInt(array_capitulo_multiple [0].replaceAll("[^\\d:-]", ""));
                             int capitulofinal = Integer.parseInt(array_capitulo_multiple[1].replaceAll("[^\\d:-]", ""));
                             
                             if (capituloinicial>capitulofinal) {
                            	 capituloinicial = capitulofinal;
                             }
                               
                             int capituloauxiliar = capituloinicial;
              
                         
                             for (int w =0; w<((capitulofinal-capituloinicial)+1); w++) {
                                 
                                 if (capituloauxiliar>capituloLimite(libro)) {
                                	 capituloauxiliar = capituloLimite(libro);
                                	 w = capitulofinal-capituloinicial;
                                 } 
                            	 
                            	 listaPasajes.add(new Pasaje (libro, capituloauxiliar, 1, versiculoLimite(libro, capituloauxiliar)));
                                 capituloauxiliar++;
                                 
                             }
                             
                              capitulo_dos = true;
                              longitudCompuestos.add(capitulofinal-capituloinicial+1);
                             
                                          
                        }
                        else {
                            unico = true;
                            compuesto = true;
                            // SI NO TIENE ESPACIOS
                            
                             String string_capitulo_multiple = pasajes_separados.get(i).replaceAll("[^\\d:-]", "");
                             String [] array_capitulo_multiple = string_capitulo_multiple.split("-");
                             
                             int capituloinicial = Integer.parseInt(array_capitulo_multiple [0]);
                             int capitulofinal = Integer.parseInt(array_capitulo_multiple[1]);
                             
                             if (capituloinicial>capitulofinal) {
                            	 capituloinicial = capitulofinal;
                             }
                             
                             int capituloauxiliar = capituloinicial;
                              
                             
                             for (int w =0; w<((capitulofinal-capituloinicial)+1); w++) {
                                 
                            	 if (capituloauxiliar>capituloLimite(libro)) {
                                	 capituloauxiliar = capituloLimite(libro);
                                	 w = capitulofinal-capituloinicial;
                                 } 
                            	 
                            	 listaPasajes.add(new Pasaje (libro, capituloauxiliar, 1, versiculoLimite(libro, capituloauxiliar)));
                                 capituloauxiliar++;
                                 
                             }
                             
                              capitulo_dos = true;
                              longitudCompuestos.add(capitulofinal-capituloinicial+1);    
                        }
                        
                        
                        
        
                    }
                    // RECONOCIMIENTO DE UN SOLO CAPITULO
                    else {
                       
                         
                // SI ES DE UN LIBRO CON ESPACIOS...  // 
                    
                    if (con_espacios == true) {
                    	
                    	 int ultima_letra_int =0;
                         
                         for (int c =0; c<pasajes_separados.get(i).length(); c++) {
                         	
                         	char letra = pasajes_separados.get(i).charAt(c);
                         	if (Character.isLetter(letra) == true) {
                                 ultima_letra_int = c;
                             }
                         	
                         }
                         
                         String ultima_letra_string = pasajes_separados.get(i).charAt(ultima_letra_int)+"";

                        String[] capitulo_unico_string = pasajes_separados.get(i).split(ultima_letra_string); 

                        capitulo = Integer.parseInt(capitulo_unico_string[1].replaceAll("\\s", ""));
                        versiculo_inicial = 1;
                        versiculo_final = versiculoLimite(libro, capitulo);
                        unico = true;
                        
                // Y SI NO LO ES...
                    } else {

                        String array_capitulo_unico = pasajes_separados.get(i).replaceAll("[^\\d:-]", "");
                        capitulo = Integer.parseInt(array_capitulo_unico);
                        versiculo_inicial = 1;
                        versiculo_final = versiculoLimite(libro, capitulo);
                        unico = true;

                    }
                    }
                    


                }
            }

            // RECONOCIMIENTO DE VERSÍCULOS 
            
            // Los versículos únicos  se omiten, sus variables ya han sido introducidas anteriormente
            
            if (unico == false) {
                
                String versiculos = pasajes_separados.get(i);
                versiculos = versiculos.substring(versiculos.indexOf(":") + 1);
                

                versiculos = versiculos.replaceAll("\\s", "");
                // ***********************************
                // Si el versículo contiene un :, es compuesto
                if (versiculos.contains(":") == true) {

                    compuesto = true;
                    
                    // EXTRACCIÓN DE CAPÍTULO COMPUESTO
                    int capitulo_inicial_compuesto = capitulo;
                    int capitulo_auxiliar = capitulo;

                    // EXTRACCIÓN DE VERSICULO FINAL E INICIAL 
                    String versiculo_compuesto[] = pasajes_separados.get(i).split("-");

                    versiculo_compuesto[0] = versiculo_compuesto[0].replaceAll("[^0-9:]", "");
                    versiculo_compuesto[1] = versiculo_compuesto[1].replaceAll("[^0-9:]", "");

                    // VERSÍCULO INICIAL COMPUESTO
                    String[] versiculo_inicial_compuesto_string = versiculo_compuesto[0].split(":");

                    int versiculo_inicial_compuesto = Integer.parseInt(versiculo_inicial_compuesto_string[1]);

                    // VERSÍCULO FINAL COMPUESTO
                    String[] versiculo_final_compuesto_string = versiculo_compuesto[1].split(":");

                    int versiculo_final_compuesto = Integer.parseInt(versiculo_final_compuesto_string[1]);

                    // CAPÍTULO FINAL COMPUESTO
                    int capitulo_final_compuesto = Integer.parseInt(versiculo_final_compuesto_string[0]);

                    // DESARROLLO 
                   
                        // PREVENCIÓN DE ERRORES
                    
                    if (versiculo_inicial_compuesto <1 || versiculo_inicial_compuesto> versiculoLimite(libro, capitulo)) { // / modificado
                        versiculo_inicial_compuesto =1;
                    }
                    
                    if (capitulo_auxiliar> capituloLimite(libro)) {
                    	capitulo_auxiliar = capituloLimite(libro);
                    }
                    
                    if (capitulo_inicial_compuesto>capitulo_final_compuesto) {
                   	 capitulo_inicial_compuesto = capitulo_final_compuesto;
                    }
                    
                    longitudCompuestos.add(capitulo_final_compuesto- capitulo_inicial_compuesto+1);
              
                    
                   
                    listaPasajes.add(new Pasaje(libro, capitulo_auxiliar, versiculo_inicial_compuesto, versiculoLimite(libro, capitulo_inicial_compuesto)));
                    capitulo_auxiliar++;
                    for (int j = 0; j < ((capitulo_final_compuesto - capitulo_inicial_compuesto) - 1); j++) {

                    	if (capitulo_auxiliar>capituloLimite(libro)) {
                       	 capitulo_auxiliar = capituloLimite(libro);
                       	 j = capitulo_final_compuesto-capitulo_inicial_compuesto;
                        } 
                    	
                        listaPasajes.add(new Pasaje(libro, capitulo_auxiliar, 1, versiculoLimite(libro, capitulo_auxiliar)));
                        capitulo_auxiliar++;
                    }
                    
                         // PREVENCIÓN DE ERRORES
                    
                    if (versiculo_final_compuesto> versiculoLimite(libro, capitulo_auxiliar)){
                        versiculo_final_compuesto = versiculoLimite(libro, capitulo_auxiliar);
                    }
                    
                    if (versiculo_final_compuesto<1) {
                        versiculo_final_compuesto = versiculoLimite(libro, capitulo_auxiliar);
                    }
                    
                    if (capitulo_auxiliar > capituloLimite(libro)) {
                    	capitulo_auxiliar = capituloLimite(libro);
                    }

                    listaPasajes.add(new Pasaje(libro, capitulo_auxiliar, 1, versiculo_final_compuesto));
                    capitulo = capitulo_auxiliar;

                } else if (versiculos.contains("-") == false) {
                    versiculo_inicial = Integer.parseInt(versiculos);
                    versiculo_final = 0;
                } else {
                      
                    
                    String[] versiculos_separados = versiculos.split("-");
                    versiculo_inicial = Integer.parseInt(versiculos_separados[0]);
                    versiculo_final = Integer.parseInt(versiculos_separados[1]);

                }
            }
            // SALIDA DE DATOS
           
            if (compuesto == false) {
                
                
                if (capitulo_dos == true) {
                    
                    capitulo = Integer.parseInt(pasajes_separados.get(i).replaceAll("\\s", ""));
                    versiculo_inicial =1; 
                    versiculo_final = versiculoLimite(libro, capitulo);
                    capitulo_dos = false;
                }
                
                if (versiculo_inicial<1 || versiculo_inicial> versiculoLimite(libro, capitulo) ) {
                    versiculo_inicial =1;
                }
                
                if (versiculo_final>versiculoLimite(libro, capitulo)) {
                    versiculo_final = versiculoLimite(libro, capitulo);
                }
                
                if (versiculo_final ==0) {
                    
                }
                else if (versiculo_final<1) {
                    versiculo_final = versiculoLimite(libro, capitulo);
                }
                
                if (capitulo<1) {
                	capitulo =1;
                }
                
                if (capitulo>capituloLimite(libro)) {
                	capitulo = capituloLimite (libro);
                	versiculo_final = versiculoLimite (libro, capitulo);
                }
                
                
                
                listaPasajes.add(new Pasaje(libro, capitulo, versiculo_inicial, versiculo_final));
      
            }
          
  
            // EXTRACCIÓN DE LA ABREVIATURA UTILIZADA POR EL USUARIO // Mt 5:12-6:15
            
            if (pasajes_separados.get(i).matches(".*[a-zA-Z].*")) {
            	
            	 int ultima_letra_int =0;
                 
                 // Comprobación de la ultima letra. 
                 
                 for (int c =0; c<pasajes_separados.get(i).length(); c++) {
                 	
                 	char letra = pasajes_separados.get(i).charAt(c);
                 	if (Character.isLetter(letra) == true) {
                         ultima_letra_int = c;
                     }
                 	
                 }
                 
                 boolean finalizado = false; 
                 int primera_letra_int =0; 
                 while (finalizado == false && primera_letra_int < pasajes_separados.get(i).length() ) {
                	 
                	 if (Character.isLetter(pasajes_separados.get(i).charAt(primera_letra_int)) ) {
                		 finalizado = true;
                		 
                	 }
                	 else {
                		 primera_letra_int++;
                		
                	 }
                	 
                 }
             
                 
                 abreviatura = pasajes_separados.get(i).substring(0, ultima_letra_int+1);
                 
                 
                 
                 String mayuscula = abreviatura.charAt(primera_letra_int)+"";
                 
                 if (compuesto == true) {
                	
                	 
                	  
                	  nombresCompuestos.add(abreviatura = mayuscula + abreviatura.substring(1, abreviatura.length()).toLowerCase());
  	 
                	 
                 }          
                 else if (abreviatura.matches(".*\\d.*")  && abreviatura.length()>2) {
                	
                	 abreviatura = abreviatura.charAt(0)+" "+mayuscula+abreviatura.substring(primera_letra_int+1, abreviatura.length()).toLowerCase();
                 }
                 else if (abreviatura.matches(".*\\d.*"))  {
                	 
                	 abreviatura = abreviatura.charAt(0)+ " "+mayuscula;
                	 
                 }
                 else {
                	
                	 abreviatura = mayuscula + abreviatura.substring(1, abreviatura.length()).toLowerCase();
                	 
                 }
         
            	
            }
            
            if (compuesto == false) {
            	listaPasajes.get(i).abreviatura = abreviatura+ " "+ capitulo+ ":"+versiculo_inicial;
                
                if (versiculo_final != 0) {
                	listaPasajes.get(i).abreviatura = abreviatura + " " + capitulo + ":"+  versiculo_inicial+"-"+versiculo_final;
                }
            }
            
            switch (formato) {
            case 0:
            break;
            case 1:
            listaPasajes.get(i).abreviatura = listaPasajes.get(i).abreviatura.replaceAll(":", ".");
            break;
            case 2:
            listaPasajes.get(i).abreviatura = listaPasajes.get(i).abreviatura.replaceAll(":", ",");
            break;
            default:
            break;
            }      
            
      
            
          // RECOMPOSICIÓN DE VARIABLES 
            
            compuesto = false;
            unico = false;
            con_espacios = false;     
 
            
        }
        
        // Comprobar si hay abreviaturas nulas
        
        boolean abreviaturas_nulas = false;
        
        for (int i =0; i<listaPasajes.size(); i++) {
        	if (listaPasajes.get(i).abreviatura == null) {
        		abreviaturas_nulas = true;
        	}
        }
        
        
        // Excepción: asignación de abreviaturas a pasajes compuestos 
        
        if (abreviaturas_nulas == true) {
        
        int identificador_compuesto=0;
        int contador =0; 
        boolean terminado = false;
        
        
        int limite =0;
        
        while (contador < listaPasajes.size() ) {
        	if (listaPasajes.get(contador).abreviatura == null) {
        		
        		listaPasajes.get(contador).abreviatura = nombresCompuestos.get(identificador_compuesto) + " "+ listaPasajes.get(contador).capitulo + ":"+listaPasajes.get(contador).versiculoinicial +"-"+ listaPasajes.get(contador).versiculofinal;
        		switch (formato) {
                case 0:
                break;
                case 1:
                listaPasajes.get(contador).abreviatura = listaPasajes.get(contador).abreviatura.replaceAll(":", ".");
                break;
                case 2:
                listaPasajes.get(contador).abreviatura = listaPasajes.get(contador).abreviatura.replaceAll(":", ",");
                break;
                default:
                break;
                }  
        		limite++;
        	}
        	
        	if (limite>=longitudCompuestos.get(identificador_compuesto)) {
        		
        		identificador_compuesto++;
        		limite =0; 
        		
        	}
        	
        	contador++;
        }
        
        }        
       
        
        
        // Excepción: libro incorrecto
        
        for (int i=0; i<listaPasajes.size(); i++) {
        	if (listaPasajes.get(i).libro == 0) {
        		listaPasajes.remove(i);
        	}
        }
        

        return listaPasajes;
         
    }
    
    
    /**
     * Extraer el libro al que pertenece en base a un String. Sirve para traducir el string a un valor numérico que se consultará en el XML.
     * El orden de los valores corresponde al documento XML. 
     * Se encuentran en mayúscula a fin de que no haya errores de conversión, porque todo String introducido al usuario se comparará en mayúsculas. 
     * Si existe un error, devuelve 99, valor que en el principal hace que se omita la función y continúe con el versículo anterior. 
     * @param abreviatura
     * @return 
     */
    
    public static int libroInt(String abreviatura) {

        int valor = 99;

        String[] abreviaturas = {"GN", "EX", "LV", "NM", "DT", "JOS", "JUE", "RT", "1 S", "2 S", "1 R", "2 R", "1 CR", "2 CR", "ESD", "NEH", "EST", "JOB", "SAL", "PR", "EC", "CNT", "IS", "JER", "LM", "EZ", "DN", "OS", "JL", "AM", "ABD", "JON", "MI", "NAH", "HAB", "SOF", "HAG", "ZAC", "MAL", "MT", "MR", "LC", "JN", "HCH", "RO", "1 CO", "2 CO", "GA", "EF", "FIL", "COL", "1 TS", "2 TS", "1 TI", "2 TI", "TIT", "FLM", "HE", "STG", "1 P", "2 P", "1 JN", "2 JN", "3 JN", "JUD", "AP"};

        // COMPROBACIÓN DE ERRORES. 
        

        // ASIGNACIÓN 
        

            if (abreviatura.toUpperCase().contains("GN")) {
                valor = 1;
            } else if (abreviatura.toUpperCase().contains("EX")) {
                valor = 2;
            } else if (abreviatura.toUpperCase().contains("LV")) {
                valor = 3;
            } else if (abreviatura.toUpperCase().contains("NM")) {
                valor = 4;
            } else if (abreviatura.toUpperCase().contains("DT")) {
                valor = 5;
            } else if (abreviatura.toUpperCase().contains("JOS")) {
                valor = 6;
            } else if (abreviatura.toUpperCase().contains("JUE")) {
                valor = 7;
            } else if (abreviatura.toUpperCase().contains("RT")) {
                valor = 8;
            } else if (abreviatura.toUpperCase().contains("1S") || abreviatura.toUpperCase().contains("1 S")) {
                valor = 9;
            } else if (abreviatura.toUpperCase().contains("2S") || abreviatura.toUpperCase().contains("2 S")) {
                valor = 10;
            } else if (abreviatura.toUpperCase().contains("1R") || abreviatura.toUpperCase().contains("1 R")) {
                valor = 11;
            } else if (abreviatura.toUpperCase().contains("2R") || abreviatura.toUpperCase().contains("2 R")) {
                valor = 12;
            } else if (abreviatura.toUpperCase().contains("1CR") || abreviatura.toUpperCase().contains("1 CR")) {
                valor = 13;
            } else if (abreviatura.toUpperCase().contains("2CR") || abreviatura.toUpperCase().contains("2 CR")) {
                valor = 14;
            } else if (abreviatura.toUpperCase().contains("ESD")) {
                valor = 15;
            } else if (abreviatura.toUpperCase().contains("NEH")) {
                valor = 16;
            } else if (abreviatura.toUpperCase().contains("EST")) {
                valor = 17;
            } else if (abreviatura.toUpperCase().contains("JOB")) {
                valor = 18;
            } else if (abreviatura.toUpperCase().contains("SAL")) {
                valor = 19;
            } else if (abreviatura.toUpperCase().contains("PR")) {
                valor = 20;
            } else if (abreviatura.toUpperCase().contains("EC")) {
                valor = 21;
            } else if (abreviatura.toUpperCase().contains("CNT")) {
                valor = 22;
            } else if (abreviatura.toUpperCase().contains("IS")) {
                valor = 23;
            } else if (abreviatura.toUpperCase().contains("JER")) {
                valor = 24;
            } else if (abreviatura.toUpperCase().contains("FLM")) {
                valor = 57;
            } else if (abreviatura.toUpperCase().contains("LM")) {
                valor = 25;
            } else if (abreviatura.toUpperCase().contains("EZ")) {
                valor = 26;
            } else if (abreviatura.toUpperCase().contains("DN")) {
                valor = 27;
            } else if (abreviatura.toUpperCase().contains("OS")) {
                valor = 28;
            } else if (abreviatura.toUpperCase().contains("JL")) {
                valor = 29;
            } else if (abreviatura.toUpperCase().contains("AM")) {
                valor = 30;
            } else if (abreviatura.toUpperCase().contains("ABD")) {
                valor = 31;
            } else if (abreviatura.toUpperCase().contains("JON")) {
                valor = 32;
            } else if (abreviatura.toUpperCase().contains("MI")) {
                valor = 33;
            } else if (abreviatura.toUpperCase().contains("NAH")) {
                valor = 34;
            } else if (abreviatura.toUpperCase().contains("HAB")) {
                valor = 35;
            } else if (abreviatura.toUpperCase().contains("SOF")) {
                valor = 36;
            } else if (abreviatura.toUpperCase().contains("HAG")) {
                valor = 37;
            } else if (abreviatura.toUpperCase().contains("ZAC")) {
                valor = 38;
            } else if (abreviatura.toUpperCase().contains("MAL")) {
                valor = 39;
            } else if (abreviatura.toUpperCase().contains("MT")) {
                valor = 40;
            } else if (abreviatura.toUpperCase().contains("MR")) {
                valor = 41;
            } else if (abreviatura.toUpperCase().contains("LC")) {
                valor = 42;
            } else if (abreviatura.toUpperCase().contains("HCH")) {
                valor = 44;
            } else if (abreviatura.toUpperCase().contains("RO")) {
                valor = 45;
            } else if (abreviatura.toUpperCase().contains("1CO") || abreviatura.toUpperCase().contains("1 CO")) {
                valor = 46;
            } else if (abreviatura.toUpperCase().contains("2CO") || abreviatura.toUpperCase().contains("2 CO")) {
                valor = 47;
            } else if (abreviatura.toUpperCase().contains("GA")) {
                valor = 48;
            } else if (abreviatura.toUpperCase().contains("EF")) {
                valor = 49;
            } else if (abreviatura.toUpperCase().contains("FIL")) {
                valor = 50;
            } else if (abreviatura.toUpperCase().contains("COL")) {
                valor = 51;
            } else if (abreviatura.toUpperCase().contains("1TS") || abreviatura.toUpperCase().contains("1 TS")) {
                valor = 52;
            } else if (abreviatura.toUpperCase().contains("2TS") || abreviatura.toUpperCase().contains("2 TS")) {
                valor = 53;
            } else if (abreviatura.toUpperCase().contains("1TI") || abreviatura.toUpperCase().contains("1 TI")) {
                valor = 54;
            } else if (abreviatura.toUpperCase().contains("2TI") || abreviatura.toUpperCase().contains("2 TI")) {
                valor = 55;
            } else if (abreviatura.toUpperCase().contains("TIT")) {
                valor = 56;
            } else if (abreviatura.toUpperCase().contains("HE")) {
                valor = 58;
            } else if (abreviatura.toUpperCase().contains("STG")) {
                valor = 59;
            } else if (abreviatura.toUpperCase().contains("1P") || abreviatura.toUpperCase().contains("1 P")) {
                valor = 60;
            } else if (abreviatura.toUpperCase().contains("2P")) {
                valor = 61;
            } else if (abreviatura.toUpperCase().contains("JUD")) {
                valor = 65;
            } else if (abreviatura.toUpperCase().contains("AP")) {
                valor = 66;
            } else if (abreviatura.toUpperCase().contains("JN")) {

                if (abreviatura.toUpperCase().contains("1JN") || abreviatura.toUpperCase().contains("1 JN")) {
                    valor = 62;
                } else if (abreviatura.toUpperCase().contains("2JN") || abreviatura.toUpperCase().contains("2 JN")) {
                    valor = 63;
                } else if (abreviatura.toUpperCase().contains("3JN") || abreviatura.toUpperCase().contains("3 JN")) {
                    valor = 64;
                } else {
                    valor = 43;
                }

            } else {
                valor = 99;
            }

        

        return valor;

    }
    
    /**
     * Calcula el versículo límite a partir de un libro y un capitulo, servirá para degranar pasajes compuestos por más de un capítulo.      * Esto lo realiza haciendo una comprobación de cuantas iteraciones realiza el contador sobre un capítulo que contiene los elementos hijo "verse". 
     * Los parámetros que se introducen sustituyen los atributos de los elementos que emplearía la función normalmente. 
     * @param libro
     * @param capitulo
     * @return 
     */
    
    public int versiculoLimite (int libro ,int capitulo) {
        
        int contador =0;
        String libro_string = Integer.toString(libro);
        String capitulo_string = Integer.toString(capitulo);
        
        
        try {

            // CREACIÓN DE LOS OBJETOS NECESARIOS
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

           // CONFIGURACIÓN DEL NOMBRE DEL ARCHIVO
            Document document = builder.parse(new File("bqlbibles/"+version));

            // NODELIST: Lista de nodos ("elementos"), en este caso, de los elementos "BIBLEBOOK"
            // -> Los nodos pueden ser atributos, elementos, etc (ver condicional), necesitan ser filtrados en cada búsqueda. 
            NodeList listaLibro = document.getElementsByTagName("BIBLEBOOK");

            for (int i = 0; i < listaLibro.getLength(); i++) {

            // Creamos un nodo que almacene todas las etiquetas <BIBLEBOOK> . Con el nodo podremos manipular la información
                Node nodo = listaLibro.item(i);

                // Si el nodo se reconoce como un elemento...
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element nodoElemento = (Element) nodo;

                    // FRANJA PARA LIBRO -----------------------------
                    if (nodoElemento.getAttribute("bnumber").equals(libro_string)) {

                        // Extraemos los hijos. 
                        NodeList hijos = nodoElemento.getChildNodes();

                        for (int j = 0; j < hijos.getLength(); j++) {

                            Node hijo = hijos.item(j);

                            if (hijo.getNodeType() == Node.ELEMENT_NODE) {

                                // FRANJA PARA CAPÍTULO  ----------------------------- 
                                
                                
                                Element hijoElemento = (Element) hijo;
                                
                                if (hijoElemento.getAttribute("cnumber").equals(capitulo_string)){

                                NodeList hijos2 = hijo.getChildNodes();

                                for (int z = 0; z < hijos2.getLength(); z++) {

                                    Node hijo2 = hijos2.item(z);

                                // FRANJA PARA VERSÍCULO  ----------------------------- 
                                    if (hijo2.getNodeType() == Node.ELEMENT_NODE) {
                                        Element hijoElemento2 = (Element) hijo2;

                                        contador++;
                                        
                                        Integer.parseInt(hijoElemento2.getAttribute("vnumber"));
                                        
                                    }

                                }

                            }

                        }

                    }

                }

            }
        }

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Sistema.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Sistema.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Sistema.class.getName()).log(Level.SEVERE, null, ex);
        }

        return contador;
    }
    
    /**
     * Calcula el capítulo límite a partir de un libro, servirá para corregir los errores del usuario al introducir capítulos incorrectos. 
     * Esto lo realiza haciendo una comprobación de cuantas iteraciones realiza el contador sobre el elemento libro. 
     * Los parámetros que se introducen sustituyen los atributos de los elementos que emplearía la función normalmente. 
     * @param libro
     * @return 
     */
    

    public int capituloLimite (int libro) {
        
        int contador =0;
        String libro_string = Integer.toString(libro);
        
        try {

            // CREACIÓN DE LOS OBJETOS NECESARIOS
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

           // CONFIGURACIÓN DEL NOMBRE DEL ARCHIVO
            Document document = builder.parse(new File("bqlbibles/"+version));

            // NODELIST: Lista de nodos ("elementos"), en este caso, de los elementos "BIBLEBOOK"
            // -> Los nodos pueden ser atributos, elementos, etc (ver condicional), necesitan ser filtrados en cada búsqueda. 
            NodeList listaLibro = document.getElementsByTagName("BIBLEBOOK");

            for (int i = 0; i < listaLibro.getLength(); i++) {

            // Creamos un nodo que almacene todas las etiquetas <BIBLEBOOK> . Con el nodo podremos manipular la información
                Node nodo = listaLibro.item(i);

                // Si el nodo se reconoce como un elemento...
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element nodoElemento = (Element) nodo;

                    // FRANJA PARA LIBRO -----------------------------
                    if (nodoElemento.getAttribute("bnumber").equals(libro_string)) {

                        // Extraemos los hijos. 
                        NodeList hijos = nodoElemento.getChildNodes();

                        for (int j = 0; j < hijos.getLength(); j++) {

                            Node hijo = hijos.item(j);

                            if (hijo.getNodeType() == Node.ELEMENT_NODE) {

                                // FRANJA PARA CAPÍTULO  ----------------------------- 
                                
                                contador++;
                                
                                Element hijoElemento = (Element) hijo;
                                
                                NodeList hijos2 = hijo.getChildNodes();

                                for (int z = 0; z < hijos2.getLength(); z++) {

                                    Node hijo2 = hijos2.item(z);

                                // FRANJA PARA VERSÍCULO  ----------------------------- 
                                    if (hijo2.getNodeType() == Node.ELEMENT_NODE) {
                                        Element hijoElemento2 = (Element) hijo2;

                                        
                                    }

                                }
                        }

                    }

                }

            }
        }

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Sistema.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Sistema.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Sistema.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
        return contador;
    }
    
    
    /**
     * En base a un objeto Pasaje, se leen todos los versículos que este contiene. 
     * @param pasaje
     * @return
     */

    
	public String cuerpoPasajes(Pasaje pasaje) {

		// DEFINICIÓN DE VARIABLES

		String salida = "";  // String salida: esta será la variable que devuelva la función, que contendrá los versículos
		int versiculo = 0; // Variable interna con la que manipular la muestra de versículos

		// ANÁLISIS DEL PASAJE - ¿Es único?

		boolean unico = false; 

		if (pasaje.versiculofinal == 0) {
			unico = true;
		}

		// IMPRESIÓN DEL PASAJE

		if (unico == true) { // Si es único, solo contendrá un versículo.

			if (ver_versiculos == true) {
				System.out.print(pasaje.versiculoinicial + " ");
			}

			salida = (leerVersiculo(version, pasaje.libro, pasaje.capitulo, pasaje.versiculoinicial));

		}

		else { // Si no, pasa a realizarse un bucle.

			versiculo = pasaje.versiculoinicial;

			for (int i = 0; i < (pasaje.versiculofinal - pasaje.versiculoinicial) + 1; i++) { //Si ver versiculos es true, de haber espaciado, para evitar la desfiguración del contenido se adaptará con un espacio en blanco.

				if (ver_versiculos == true)  {
					
					if (espaciado>0) {
						
						if (versiculo<=9) {
							salida = salida + " " + versiculo + " ";
						}
						else {
							salida = salida + versiculo + " ";
						}
						
					}
					else {
						
						salida = salida + versiculo + " ";
						
					}
		
					
				}
					

				if (espaciado == 0) // Si el espaciado está configurado en 0, se devuelve el texto en prosa, por lo que el ajuste de los versículos debe tener un margen
					salida = salida + (leerVersiculo(version, pasaje.libro, pasaje.capitulo, versiculo) + " ");
				if (espaciado > 0) // Si es mayor que cero, no existirá margen de los versículos
					salida = salida + (leerVersiculo(version, pasaje.libro, pasaje.capitulo, versiculo));

				if (i != pasaje.versiculofinal - pasaje.versiculoinicial) { // Exta excepción se hace para que no haya	espaciado extra. 												// espaciado extra.

					for (int j = 0; j < espaciado; j++) {

						salida = salida + "\n";

					}

				}

				versiculo++;

			}

		}

		return salida;

	}
    
    /**
     * Método que arroja un solo versículo en entrada String. Se usa como función estática que implementa leerPasaje.
     * @param version
     * @param libro_i
     * @param capitulo_i
     * @param versiculo_i
     * @return
     */
    
    // https://chuidiang.org/index.php?title=Java_y_xpath -  SISTEMA XPATH PARA LA LECTURA DE VERSÍCULOS
	// Permitirá la optimización del sistema utilizando librerías más precisas. 
	
    public static String leerVersiculo (String version, int libro_i, int capitulo_i, int versiculo_i) {
    	String salida = "";
    	
    	String libro = Integer.toString(libro_i);
    	String capitulo = Integer.toString(capitulo_i);
    	String versiculo = Integer.toString(versiculo_i);
    	
     	try {

            // CREACIÓN DE LOS OBJETOS NECESARIOS
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

           // CONFIGURACIÓN DEL NOMBRE DEL ARCHIVO
            Document document = builder.parse(new File("bqlbibles/"+version));

            // NODELIST: Lista de nodos ("elementos"), en este caso, de los elementos "BIBLEBOOK"
            // -> Los nodos pueden ser atributos, elementos, etc (ver condicional), necesitan ser filtrados en cada búsqueda. 
            NodeList listaLibro = document.getElementsByTagName("BIBLEBOOK");

            for (int i = 0; i < listaLibro.getLength(); i++) {

            // Creamos un nodo que almacene todas las etiquetas <BIBLEBOOK> . Con el nodo podremos manipular la información
                Node nodo = listaLibro.item(i);

                // Si el nodo se reconoce como un elemento...
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element nodoElemento = (Element) nodo;

                    // FRANJA PARA LIBRO -----------------------------
                    if (nodoElemento.getAttribute("bnumber").equals(libro)) {

                        // Extraemos los hijos. 
                        NodeList hijos = nodoElemento.getChildNodes();

                        for (int j = 0; j < hijos.getLength(); j++) {

                            Node hijo = hijos.item(j);

                            if (hijo.getNodeType() == Node.ELEMENT_NODE) {

                                // FRANJA PARA CAPÍTULO  ----------------------------- 
                                                             
                                Element hijoElemento = (Element) hijo;
                                
                                if (hijoElemento.getAttribute("cnumber").equals(capitulo)){

                                NodeList hijos2 = hijo.getChildNodes();

                                for (int z = 0; z < hijos2.getLength(); z++) {

                                    Node hijo2 = hijos2.item(z);

                                // FRANJA PARA VERSÍCULO  ----------------------------- 
                                    if (hijo2.getNodeType() == Node.ELEMENT_NODE) {
                                        Element hijoElemento2 = (Element) hijo2;
                                        
                                       if (hijoElemento2.getAttribute("vnumber").equals(versiculo)) {
                                       	salida = hijoElemento2.getTextContent();
                                       }
                                        
                                        
                                        
                                    }

                                }

                            }

                        }

                    }

                }

            }
        }

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Sistema.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Sistema.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Sistema.class.getName()).log(Level.SEVERE, null, ex);
        }
     	
     	return salida;
    	
    }
    
    
 
    
}
