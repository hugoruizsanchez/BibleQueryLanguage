package biblia;


public class Pasaje {
    
    // ATRIBUTOS
    
	public String abreviatura;
    public int libro;
    public int capitulo; 
    public int versiculoinicial; 
    public int versiculofinal; 
    
    // CONSTRUCTOR 

    public Pasaje(int libro, int capitulo, int versiculoinicial, int versiculofinal) {
        this.libro = libro;
        this.capitulo = capitulo;
        this.versiculoinicial = versiculoinicial;
        this.versiculofinal = versiculofinal;
    }
    
        
    
    // MÉTODOS GET
    

    public int getLibro() {
        return libro;
    }

    public int getCapitulo() {
        return capitulo;
    }

    public int getVersiculoinicial() {
        return versiculoinicial;
    }

    public int getVersiculofinal() {
        return versiculofinal;
    }
    
    
    
    
}
  

   

   
   
    
    


