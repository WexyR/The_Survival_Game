class Option{
  private Graphisme Graphisme;
  private SoundEngine SoundEngine;
  private InputEngine InputEngine;
  private boolean init_button;
  private final int x_barre_de_son = 100;
  private final int y_barre_de_son = 100;
  private final int taille_barre_de_son = 400;
  private float x_button_barre_de_son;
  private boolean init_button_son =false;
  private boolean ne_plus_charger_la_page_option = false;
  private int num_new_resolution;
  private boolean la_resolution_change = false;
  private int case_x = -1;
  private int case_y = -1;
  private String[] controls_j1;
  private String[] controls_j2;
  private int[] control_j1 = new int[6];
  private int[] control_j2 = new int[6];
  public Option(){
    this.Graphisme = new Graphisme();
    this.SoundEngine = new SoundEngine();
    this.InputEngine = new InputEngine();
    this.num_new_resolution = numero_resolution;
    this.controls_j1 = loadStrings("Player_Ressources/control_1_str.txt");
    this.controls_j2 = loadStrings("Player_Ressources/control_2_str.txt");
    String[] str = new String[6];
    str = loadStrings("Player_Ressources/control_1.txt");
    control_j1 = int(str);
    str = loadStrings("Player_Ressources/control_2.txt");
    control_j2 = int(str);
  }
  void option(){
    this.ne_plus_charger_la_page_option = false;
    Graphisme.OptionPaint();
    if(mousePressed) if(mouseX > width/2-60/f && mouseX < width/2+60/f && mouseY > height/2+300/f && mouseY < height/2+350/f) quitter_les_options();
    this.barre_de_son(this.x_barre_de_son/f,this.y_barre_de_son/f, this.taille_barre_de_son/f, this.x_button_barre_de_son/f);
    this.gerer_la_resolution();
    this.option_control_player();
  }
  void quitter_les_options(){
    save_controls();
    String power_lvl;
    if(SoundEngine.modulateGain ==0){
      power_lvl = str(SoundEngine.powerInit);
    }else{
      power_lvl = str(SoundEngine.modulateGain);
    }
    String[] save_power_lvl = {power_lvl};
    saveStrings("/data/Option_Ressources/niveau_son.txt", save_power_lvl);
    if(this.la_resolution_change == true){
      String[] new_resolution = {str(this.num_new_resolution),"1472","800","736","400"};
      saveStrings("/data/resolution.txt",new_resolution);
      Graphisme.resolution_restart();
      stop();
    }
    
    this.ne_plus_charger_la_page_option = true;   
  }
  void save_controls(){
    String[] str1 = {controls_j1[0], controls_j1[1], controls_j1[2], controls_j1[3], controls_j1[4],controls_j1[5]};
    saveStrings("/data/Player_Ressources/control_1_str.txt", str1);
    String[] str2 = {str(control_j1[0]), str(control_j1[1]), str(control_j1[2]), str(control_j1[3]), str(control_j1[4]), str(control_j1[5])};
    saveStrings("/data/Player_Ressources/control_1.txt", str2);
    String[] str3 = {controls_j2[0], controls_j2[1], controls_j2[2], controls_j2[3], controls_j2[4], controls_j2[5]};
    saveStrings("/data/Player_Ressources/control_2_str.txt", str3);
    String[] str4 = {str(control_j2[0]), str(control_j2[1]), str(control_j2[2]), str(control_j2[3]), str(control_j2[4]), str(control_j2[5])};
    saveStrings("/data/Player_Ressources/control_2.txt", str4);
  }
  
  void barre_de_son(int x,int y, int taille, float x_button){
    if(init_button_son == false){
      this.x_button_barre_de_son = map(SoundEngine.powerInit,-40,0.1,x,x+taille)*f;
      init_button_son = true;
    }
    if(mousePressed){
      if(mouseY > y-20/f && mouseY < y+20/f && mouseX> x && mouseX < x+taille){
        this.x_button_barre_de_son = mouseX*f;
        SoundEngine.regler_le_volume(x,y,taille,this.x_button_barre_de_son/f);
      }
    }
    Graphisme.barre_de_son(x,y,taille,this.x_button_barre_de_son/f);
  }
  void gerer_la_resolution(){
    if(mousePressed){
      if(mouseX>1100/f && mouseX< 1130/f && mouseY> 90/f && mouseY < 120 && this.num_new_resolution !=0){
        this.num_new_resolution = 0;
      }
      if(mouseX>1100/f && mouseX< 1130/f && mouseY> 160/f && mouseY < 190 && this.num_new_resolution !=1){
        this.num_new_resolution = 1;
      }
      if( this.num_new_resolution != numero_resolution){
        this.la_resolution_change = true;
      }else{
        this.la_resolution_change = false;
      }
    }
    Graphisme.option_resolPaint(this.num_new_resolution);
    
  }
  void option_control_player(){
    
    for(int i = 0; i< controls_j1.length; i++){
      if(mousePressed){
        if(mouseX > i*85/f+150/f && mouseX < i*85/f+210/f && mouseY > 250/f && mouseY < 310/f){
          this.case_x = i;
          this.case_y = 1;
        }
        if(mouseX > i*85/f+150/f && mouseX < i*85/f+210/f && mouseY > 320/f && mouseY < 380/f){
          this.case_x = i;
          this.case_y = 2;
        }
      }
      if(keyPressed){
        
        if(this.case_x != -1){
          
          if(still_not_used(key) == true){
            if(this.case_y == 1){
              int KC = 0;
              for(int j=0; j<144; j++){
                if(touches[j] == true) { this.control_j1[this.case_x] = j; KC = j; }
              }
              if(int(key) != 65535) {
                this.controls_j1[this.case_x] = str(key);
              }else if(KC == 38){
                this.controls_j1[this.case_x] = "↑";
              }else if(KC == 40){
                this.controls_j1[this.case_x] = "↓";
              }else if(KC == 37){
                this.controls_j1[this.case_x] = "←";
              }else if(KC == 39){
                this.controls_j1[this.case_x] = "→";
              }else if(KC == 16){
                this.controls_j1[this.case_x] = "▲"; 
              }else{
                this.controls_j1[this.case_x] = "?";
              }
            }
            if(this.case_y == 2){
              int KC = 0;
              for(int j=0; j<144; j++){
                if(touches[j] == true) { this.control_j2[this.case_x] = j; KC = j; }
              }
              if(int(key) != 65535) {
                this.controls_j2[this.case_x] = str(key);
              }else if(KC == 38){
                this.controls_j2[this.case_x] = "↑";
              }else if(KC == 40){
                this.controls_j2[this.case_x] = "↓";
              }else if(KC == 37){
                this.controls_j2[this.case_x] = "←";
              }else if(KC == 39){
                this.controls_j2[this.case_x] = "→";
              }else if(KC == 16){
                this.controls_j2[this.case_x] = "▲";
              }else{
                this.controls_j2[this.case_x] = "?";
              }
            }
            this.case_x = -1;
            this.case_y = -1;
          }
        }
      }
    }
    Graphisme.option_controlPaint(this.case_x, this.case_y, controls_j1,controls_j2);
  }
  public boolean still_not_used(char c){
    int compteur=0;
    for(int i=0; i<controls_j1.length; i++){
      String str = this.controls_j1[i];
      if(c != str.charAt(0)) compteur++;
    }
    for(int i=0; i<controls_j2.length; i++){
      String str = this.controls_j2[i];
      if(c != str.charAt(0)) compteur++;
    }
    if(compteur == 2*controls_j1.length){
      return true;
    }else{
      return false;
    }
  }
}