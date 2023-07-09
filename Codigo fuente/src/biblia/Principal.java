package biblia;
import java.util.*;

public class Principal {

	public static void main(String[] args) {

		// CREACIÓN DE OBJETOS.
		Sistema sistema = new Sistema();
		Scanner entrada = new Scanner(System.in);
		ArrayList<Pasaje> listaPasajes = new ArrayList<Pasaje>();

		
		  // VERSIÓN DE PRUEBA: sistema.guardarPasajes()
		  /*
		  listaPasajes = sistema.guardarPasajes("Mt 5-6; Jn 4-5");
		  
		  for (int i = 0; i < listaPasajes.size(); i++) { System.out.println("Libro: "
		  + listaPasajes.get(i).getLibro()); System.out.println("Capitulo: " +
		  listaPasajes.get(i).getCapitulo()); System.out.println("Versiculo inicio: " +
		  listaPasajes.get(i).getVersiculoinicial());
		  System.out.println("Versiculo fin: " +
		  listaPasajes.get(i).getVersiculofinal()); System.out.println("Abreviatura: "
		  + listaPasajes.get(i).abreviatura);
		  
		  System.out.println("----------------"); 
		  */
		 

		// VERSIÓN DE PRUEBA 0.1.0

		System.out.println("     /\n- - / - -    ___  ____    __\n   /\u0009    / _ )/ __ \\  / /\n  /\u0009   / _  / /_/ / / /__\n /        /____/\\___\\_\\/____/\n\nBible Query Language 0.1.0 client version.");

		// VARIABLES PARA LA CONSOLA

		String marco = "<bql> ";
		String comando = "";
		boolean quote = false;

		while (0 < 1) {

			while (comando.equals("")) {
				System.out.print(marco);
				comando = entrada.nextLine().replaceAll("\\s", "").toUpperCase();
			}

			if (comando.contains("QUOTE")) {
				quote = true;
			} else {
				quote = false;
			}

			while (comando.charAt(comando.length() - 1) != ';') {

				System.out.print("----> ");

				if (quote == true) {

					comando = comando + ";" + entrada.nextLine().replaceAll("\\s", "").toUpperCase();

				}

				else {
					comando = comando + entrada.nextLine().replaceAll("\\s", "").toUpperCase();
				}

			}

			if (comando.contains("SETENABLE_VERSES") == true && comando != "") {
				comando = comando.replaceAll(";", "");
				if (encontrarElemento("=", comando) != -1) {

					if (comando.substring(encontrarElemento("=", comando) + 1).equals("TRUE")) {
						sistema.ver_versiculos = true;
						System.out.println("ENABLE_VERSES set to true.");
					} else if (comando.substring(encontrarElemento("=", comando) + 1).equals("FALSE")) {
						sistema.ver_versiculos = false;
						System.out.println("ENABLE_VERSES set to false.");
					} else {
						System.out.println("ERROR (Unspecified variable)");
					}

				}

			}

			else if (comando.contains("SETLINE_SPACING") == true && comando != "") {
				comando = comando.replaceAll(";", "");
				if (encontrarElemento("=", comando) != -1) {

					try {
						sistema.espaciado = Integer.parseInt(comando.substring(encontrarElemento("=", comando) + 1));
						System.out.println("LINE_SPACING set to " + sistema.espaciado + ".");
					} catch (Exception e) {
						System.out.println("ERROR (Unspecified variable).");
					}

				}

			}

			else if (comando.contains("SETVERSION") == true && comando != "") {
				comando = comando.replaceAll(";", "");
				sistema.version = (comando.substring(encontrarElemento("=", comando) + 1).toLowerCase() + ".xml");

				System.out.println("VERSION changed to "
						+ comando.substring(encontrarElemento("=", comando) + 1).toLowerCase() + ".xml");

			}
			else if (comando.contains("SETVERSE_FORMAT") == true ) {
				comando = comando.replaceAll(";", "");
				if (encontrarElemento("=", comando) != -1) {
					try {
						sistema.formato = Integer.parseInt(comando.substring(encontrarElemento("=", comando) + 1));
						
						if (sistema.formato <0 || sistema.formato >2) {
							sistema.formato = 0;
							System.out.println("VERSE_FORMAT set to default (0)");
						}
						else {
							System.out.println("VERSE_FORMAT set to " + sistema.formato + ".");
						}
						
					} catch (Exception e) {
						System.out.println("ERROR (Unspecified variable).");
					}

				}

				
			}

			else if (comando.contains("SELECTCONFIGURATION") == true && comando != "") {
				comando = comando.replaceAll(";", "");
				System.out.println("\nDefault configurations.\nversion: " + sistema.version + "\nverses: "+ sistema.ver_versiculos + "\nspace: " + sistema.espaciado + "\n");

			}

			else if (comando.contains("QUOTE") == true && comando != "") {
				boolean error_quote = false;
				try {

					if (comando.substring(encontrarElemento("E", comando) + 1).charAt(0) == ';') {

						listaPasajes = sistema.guardarPasajes(comando.substring(encontrarElemento("E", comando) + 2));

					} else {
						listaPasajes = sistema.guardarPasajes(comando.substring(encontrarElemento("E", comando) + 1));
					}

				} catch (Exception e) {
					System.out.println ();
					System.out.println("ERROR (Formatting errors in entered verse).");
					
					error_quote = true;
				}

				System.out.println ();
				
				if (listaPasajes.size()<=0 && error_quote == false) {
					System.out.println ("ERROR (Unrecognized book).\n");
				}
				
				
				

				if (error_quote == false && listaPasajes.size()>0) {
				
				long startTime = System.currentTimeMillis();

				for (int i = 0; i < listaPasajes.size(); i++) {

					System.out.println(sistema.cuerpoPasajes(listaPasajes.get(i)));
					System.out.println("- - - - - - - - - -");
					System.out.println(listaPasajes.get(i).abreviatura);
					System.out.println();

				}

				long endTime = System.currentTimeMillis() - startTime;

				System.out.print(listaPasajes.size() + " passages in " + (float) endTime / 1000 + " segs");

				System.out.println();
				System.out.println();
				
				
				}
				
				

			} else {
				System.out.println("ERROR (Unrecognized command).");

			}

			comando = "";

		}
    	

    	// FUNCIONES. 
        
        
        
        
        
    }
    
    public static int encontrarElemento (String elemento, String texto) {
    	
    	int localizacion =0;
    	boolean finalizado = false;
    	
    	while (finalizado == false && localizacion < texto.length() ) {
    		
    		String letra = texto.charAt(localizacion)+"";
    		
    		if (letra.equals(elemento)) {
    			finalizado = true;
    		}
    		else {
    			localizacion++;
    		}
    		
    	}
    	
    	if (finalizado == false) localizacion = -1; 
    	
    	return localizacion;
    	
    }

}
