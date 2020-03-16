class Graphisme{
  private boolean image_menu_chargee = false;
  private boolean image_option_chargee = false;
  private PImage image_menu;
  private PImage miniature;
  private PImage image_option;
  private String[] liste_arme;
  private String[] liste_armure;
  private String[] liste_map;
  private boolean image_chargee = false;
  private PImage image_terrain;
  private String[] controls_j1;
  private String[] controls_j2;
  private PImage img;
  
  public Graphisme(){
    liste_arme = loadStrings("./Graphisme_Ressources/armes/liste_arme.txt");
    liste_armure = loadStrings("./Graphisme_Ressources/armures/liste_armure.txt");
    liste_map = loadStrings("./Map_Ressources/liste_map.txt");
  }
  
  void MenuPaint(int num_map){
    if(this.image_menu_chargee == false) this.charger_image_du_menu(num_map);
    image(miniature, width/2-(368/2/f), 550/f);
    textSize(30/f);
    fill(255);
    textAlign(CENTER);
    text(liste_map[num_map],width/2,535/f);
    fill(167,2,2);
    rect(width/2-500/f,height/2-300/f,1000/f,100/f);
    rect(width/2-500/f,height/2-50/f,1000/f,100/f);
    textSize(80/f);
    if(mouseX > width/2-500/f && mouseX < width/2+500/f && mouseY > height/2-300/f && mouseY < height/2-200/f){     fill(255);     }else{     fill(0); }
    text("PLAY",width/2,height/2-220/f);
    if(mouseX > width/2-500/f && mouseX<width/2+500/f && mouseY < height/2+50/f && mouseY>height/2-50){         fill(255);     }else{     fill(0); }
    text("OPTIONS",width/2,height/2+30/f);
    textAlign(LEFT);
    fill(167,2,2);
    triangle(450/f,650/f,510/f,590/f,510/f,710/f);
    triangle(1022/f,650/f,962/f,590/f,962/f,710/f);
  }
  
  void charger_image_du_menu(int num_map){
    this.image_menu = loadImage("./Menu_Ressources/image_menu_" +str(numero_resolution)+".png");
    this.miniature = loadImage("./Map_Ressources/"+str(num_map)+"/miniature_image_terrain_"+str(numero_resolution)+".png");
    this.image_menu_chargee = false;
    image(image_menu,0,0);
  }
  
  void OptionPaint(){
    if(this.image_option_chargee == false) this.charger_image_des_options();
    image(image_option,0,0);
    fill(#316BA3);
    rect(width/2-60/f,height/2+300/f,120/f,50/f);
    if(mouseX > width/2-60/f && mouseX < width/2+60/f && mouseY > height/2+300/f && mouseY < height/2+350/f){       fill(255);     }else{    fill(0); }
    textSize(30/f);
    textAlign(CENTER);
    text("RETOUR",width/2,height/2+340/f);
    textAlign(LEFT);
    fill(0);
  }
  
  void charger_image_des_options(){
    this.image_option = loadImage("./Menu_Ressources/image_option_"+str(numero_resolution)+".png");
    this.image_option_chargee = true; 
  }
  
 void barre_de_son(int x,int y,int taille, float x_button){
  textSize(40/f);
  text("Volume",100/f,70/f);
  stroke(#55D3F7);
  fill(0);
  rect(x,y-10/f,taille,20/f);
  line(x,y,x+taille,y);
  stroke(0);
  fill(#55D3F7);
  rect(x_button-10/f,y-20/f,20/f,40/f);
  fill(0);
  rect(x_button-6/f,y-12/f,4/f,24/f);
  rect(x_button+2/f,y-12/f,4/f,24/f);
  }
  
  void option_resolPaint(int num_new_resolution){
    noStroke();
    textSize(40/f);
    text("Résolution",1100/f,70/f);
    textSize(25/f);
    if(num_new_resolution == 0){ fill(#55D3F7); }else{ fill(0); }
    rect(1100/f,90/f,30/f,30/f);
    fill(0);
    text("1472 x 800",1150/f,115/f);
    if(num_new_resolution == 1){ fill(#55D3F7); }else{ fill(0); }
    rect(1100/f,160/f,30/f,30/f);
    fill(0);
    text("736 x 400",1150/f,185/f);
    stroke(0);
  }
  
  void resolution_restart(){
    fill(0);
    rect(1080/f,210/f,370/f,165/f);
    fill(255,0,0);
    textSize(30/f);
    text("Le changement de la" ,1100/f,250/f);
    text("resolution necessite",1100/f,285/f);
    text("un redemarrage du",1100/f,320/f);
    text("programme.",1100/f,355/f);
    fill(0);  
  }

  void playerpaint(int num_player, float pos_x, float pos_y, int sens, int etat, int localisation){
    img = loadImage("./Graphisme_Ressources/player_" + str(num_player) + "/" + str(sens) + "_" + str(etat) + "_" + str(numero_resolution) + "_" + str(localisation) + ".png");
    image(img,pos_x,pos_y);
  }
  
  void weaponpaint(float pos_x, float pos_y, int sens_du_joueur, int tourner_arme, PImage arme_a_gauche, PImage arme_a_droite){
    pushMatrix();
    if (sens_du_joueur == 0) { translate(pos_x+15/f, pos_y+36/f); } else { translate(pos_x+15/f, pos_y+36/f); }
    rotate(radians(tourner_arme));
    imageMode(CENTER);
    if (sens_du_joueur == 0) { image(arme_a_gauche, 0, 0); } else { image(arme_a_droite, 0, 0); }
    imageMode(CORNER);
    popMatrix();
  }
  
  void caractplayer(int num_player, int pdv, int cash, int points_armure){
    img = loadImage("./Graphisme_Ressources/game/menu_" + str(num_player) + "_" + str(numero_resolution) + ".png");
    image(img,1107*(num_player-1)/f,0);
    textSize(28/f);
    if(num_player == 1) {
      text(str(pdv),200/f,25/f);
      text(str(cash),200/f,56/f);
      text(str(points_armure),200/f,86/f);
    }else{
      textAlign(RIGHT);
      text(str(pdv),1280/f,25/f);
      text(str(cash),1280/f,56/f);
      text(str(points_armure),1280/f,86/f);
      textAlign(LEFT);
    }
  }
  
  void chargeurpaint(int munitions, int num_player){
    if (num_player == 1) {
      text(munitions,200/f,113/f);
    } else {
      textAlign(RIGHT);
      text(munitions,1280/f,115/f);
      textAlign(LEFT);
    }
  }
  
  void armeselect(int num_player, int menu_selection, int maximum){  
    for(int i = 0; i <= maximum; i ++){
        if(num_player == 1){
          fill(122,254,220,200);
        }else{
          fill(255,0,0,200);
        }
        rect(368/f*num_player+4/f,i*32/f,360/f,32/f);
        fill(0,0,0,200);
        textSize(28/f);
        if (menu_selection == 0){
          text("Armes",368/f*num_player+10/f,24/f); 
          text("Armures",368/f*num_player+10/f,56/f);
        }
        if (menu_selection == 1) text(liste_arme[i],368/f*num_player+10/f,i*32/f+24/f);
        if (menu_selection == 2) text(liste_armure[i],368/f*num_player+10/f,i*32/f+24/f);
      }
  }
  
  void dessiner_terrain(int num_map){
    if(this.image_chargee == false){
      image_terrain = loadImage("./Map_Ressources/"+str(num_map)+"/image_terrain_"+ str(numero_resolution) + ".png");
      this.image_chargee = true;
    }
    image(image_terrain,0,0);
  }
  

  void option_controlPaint(int case_x, int case_y, String[] controls_j1, String[] controls_j2){
    String[] str_control = {"left", "right", "up", "down", "jump", "shoot"};
    textSize(40/f);
    text("Controls",100/f,190/f);
    textSize(30/f);
    text("Player 1",10/f,290/f);
      for(int i = 0; i < controls_j1.length; i++){
      textSize(20/f);
      fill(0);
      text(str_control[i],i*85/f+150/f,240/f);
      fill(#55D3F7);
      rect(i*85/f+150/f,250/f,60/f,60/f);
      if( i==case_x && case_y== 1) { fill(255); }else{ fill(0);}
      textSize(30/f);
      text(controls_j1[i],i*85/f+170/f,290/f);
    }
    fill(0);
    text("Player 2",10/f,360/f);
      for(int i = 0; i < controls_j2.length; i++){
      fill(#55D3F7);
      rect(i*85/f+150/f,320/f,60/f,60/f);
      if( i==case_x && case_y ==2) { fill(255); }else{ fill(0);}
      textSize(30/f);
      text(controls_j2[i],i*85/f+170/f,360/f);
    }
  }
}
