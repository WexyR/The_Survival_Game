class Menu{
  private boolean fin_du_menu = false;
  private boolean la_partie_debute = false;
  private boolean lancer_les_options = false;
  private int num_map=1;
  private int nombre_de_map;
  private Graphisme Graphisme;
  private Option Option;
  public Menu(){
    this.Graphisme = new Graphisme();
    this.Option = new Option();
    String[] str_map = loadStrings("./Map_Ressources/liste_map.txt");
    this.nombre_de_map = str_map.length;
  }
  
  void menu(){
    Graphisme.MenuPaint(this.num_map);
      if(mousePressed && mouseX > width/2-500/f && mouseX < width/2+500/f && mouseY > height/2-300/f && mouseY < height/2-200/f && lancer_les_options == false) {
        this.fin_du_menu = true;
      }
      
      
      if(mouseReleased && mouseX > 450/f && mouseX < 510/f && mouseY > 590/f && mouseY < 710/f && lancer_les_options == false) {
        num_map--;
        if(num_map == -1) num_map = nombre_de_map -1;
      }
      if(mouseReleased && mouseX > 962/f && mouseX < 1022/f && mouseY > 590/f && mouseY < 710/f && lancer_les_options == false) {
        num_map++;
        if(num_map >= nombre_de_map) num_map =0;
      }
      
      
      if((mousePressed && mouseX > width/2-500/f && mouseX<width/2+500/f && mouseY < height/2+50/f && mouseY>height/2-50) || this.lancer_les_options == true){
        this.lancer_les_options = true;
       this.APL_option();
      }
      if(this.lancer_les_options == true){
        if(mousePressed && mouseX > width/2-60/f && mouseX < width/2+60/f && mouseY > height/2+300/f && mouseY < height/2+350/f) this.lancer_les_options = false;
      }
  }
  
  void APL_option(){
    Option.option();
  } 
}
