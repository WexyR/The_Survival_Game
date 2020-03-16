import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.spi.*; 
import ddf.minim.signals.*; 
import ddf.minim.*; 
import ddf.minim.analysis.*; 
import ddf.minim.ugens.*; 
import ddf.minim.effects.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class The_Survival_Game extends PApplet {

GameEngine GameEngine;
InputEngine InputEngine;
public boolean[] touches=new boolean[145];
public int numero_resolution;
public int f;
public boolean mouseReleased;

public void settings(){
  String[] lines = loadStrings("resolution.txt");
  numero_resolution = PApplet.parseInt(lines[0]);                              //|  Ces lignes permettent juste de charger la taille de la fen\u00eatre
  f = numero_resolution+1;
  size(PApplet.parseInt(lines[2*PApplet.parseInt(lines[0])+1]),PApplet.parseInt(lines[2*PApplet.parseInt(lines[0])+2]));  //|  du jeu depuis un fichier texte exterieur
}

public void setup(){
  minim = new Minim(this);
  song = minim.loadFile("./data/Menu_Ressources/song.mp3"); /* import du son de fond depuis la superclass obligatoire, du a la library Minim */
  GameEngine = new GameEngine();
  InputEngine = new InputEngine();
}

public void draw(){
  frameRate(40);
  clear();
  GameEngine.APL();
  mouseReleased = false;
}

/* import du son de fond depuis la superclass obligatoire, d\u00fb a la library Minim */
Minim minim;
AudioPlayer song;
public void stop() {
  song.close();
  minim.stop();
  super.stop();
}

/*donne a InputEngine les droits maximum concernant les entr\u00e9es des donn\u00e9es clavier*/
public void keyPressed(){
  InputEngine.keyPressed();
}
public void keyReleased(){
  InputEngine.keyReleased();
}

public void mouseReleased(){
  mouseReleased = true;
}
class Arme {
  private boolean controles_charges = false;
  private PImage arme_a_droite;
  private PImage arme_a_gauche;
  private int numero_de_l_arme = 0;
  private int tourner_arme = 0;
  private float pos_x;
  private float pos_y; 
  private int num_player;
  private int sens_du_joueur;
  private String[] controls;
  private int[] control;
  private int angle_rotation_arme;
  private boolean regarde_a_gauche = true;
  private boolean changement_de_direction = false;
  private ParticuleurBalle ParticuleurBalle;
  private Graphisme Graphisme;
  private float angle = 180;
  private float pos_x_autre_joueur;
  private float pos_y_autre_joueur;
  private String[] liste_arme = loadStrings("./Graphisme_Ressources/armes/liste_arme.txt");
  private int[] degats_armes = new int[liste_arme.length];
  private int[] temps_entre_chaque_tir = new int[liste_arme.length];
  private int[] chargeur = new int[liste_arme.length];
  private int[] temps_rechargement = new int[liste_arme.length];
  private float[] angle_d_imprecision = new float[liste_arme.length];
  private int degats;
  private int munitions;
  private int munitions_chargeur_vide;
  private int debut;
  
  public Arme() {
    this.Graphisme = new Graphisme();
    this.ParticuleurBalle = new ParticuleurBalle();
    for(int j = 0; j < this.liste_arme.length; j++){
      String[] arme_tmp = loadStrings("./Graphisme_Ressources/armes/" + str(j) + "/caractarme.txt");
      this.degats_armes[j] = PApplet.parseInt(arme_tmp[2]);
      this.temps_entre_chaque_tir[j] = PApplet.parseInt(arme_tmp[4]);
      this.chargeur[j] = PApplet.parseInt(arme_tmp[5]);
      this.temps_rechargement[j] = PApplet.parseInt(arme_tmp[6]);
      this.angle_d_imprecision[j] = PApplet.parseInt(arme_tmp[1]);
    }
  }

  public void APL_Arme(int num_player, float pos_x, float pos_y, int sens_du_joueur, boolean with_move, int numero_arme, boolean echelle, Terrain Terrain) {
    if(echelle == false){
      this.degats = this.degats_armes[numero_arme];
      this.numero_de_l_arme = numero_arme;
      this.num_player = num_player;
      this.pos_x = pos_x;
      this.pos_y = pos_y;    
      this.pos_x_autre_joueur = pos_x_autre_joueur;
      this.pos_y_autre_joueur = pos_y_autre_joueur;
      this.sens_du_joueur = sens_du_joueur;
      this.controls = loadStrings("Player_Ressources/control_" + str(num_player)+ ".txt");
      if (with_move){
        charger_images();
        correction_de_l_angle();
        tourner_arme();
        test_changement_de_direction();
      }
    }
    this.ParticuleurBalle.charger_balles(Terrain);
    chargeur_vide_ou_non();
    if (ParticuleurBalle.chargeur == 0) { this.munitions = this.munitions_chargeur_vide; } else { this.munitions = ParticuleurBalle.chargeur; }
    Graphisme.chargeurpaint(this.munitions, this.num_player);
  }
  
  public void tir(boolean echelle){
    if (echelle == false){
      this.ParticuleurBalle.tir(this.pos_x+14/f, this.pos_y+28/f, this.angle,32/f,this.temps_entre_chaque_tir[this.numero_de_l_arme],this.chargeur[this.numero_de_l_arme],
      this.temps_rechargement[this.numero_de_l_arme], this.angle_d_imprecision[this.numero_de_l_arme]);
    }
  }
  
  public void restart(){
    this.ParticuleurBalle = new ParticuleurBalle();
    this.tourner_arme = 0;
    this.angle = 180;
    this.sens_du_joueur = 0;
    this.regarde_a_gauche = true;
    this.munitions = 10;
  }

  public void charger_images() {
    arme_a_gauche  = loadImage("Graphisme_Ressources/armes/" + str(this.numero_de_l_arme)+"/" + str(this.num_player-1) + "_" + str(numero_resolution) + "_0" + ".png");
    arme_a_droite  = loadImage("Graphisme_Ressources/armes/" + str(this.numero_de_l_arme)+"/" + str(this.num_player-1) + "_" + str(numero_resolution) + "_1" + ".png");
    if (this.controles_charges == false) {
      this.control = new int[this.controls.length];
      for (int i=0; i <= controls.length-1; i++) {
        this.control[i] = PApplet.parseInt(controls[i]);
        this.controles_charges = true;
      }
    }
  }

  public void correction_de_l_angle(){
    if (this.sens_du_joueur == 1 && this.tourner_arme == 0) this.angle = 0;
    if (this.sens_du_joueur == 1 && this.tourner_arme == -45) this.angle = 45;
    if (this.sens_du_joueur == 1 && this.tourner_arme == 45) this.angle = 315;
    if (this.sens_du_joueur == 0 && this.tourner_arme == 0) this.angle = 180;
    if (this.sens_du_joueur == 0 && this.tourner_arme == 45) this.angle = 135;
    if (this.sens_du_joueur == 0 && this.tourner_arme == -45) this.angle = 225;
  }  
  
  public void tourner_arme() {
      if (this.changement_de_direction == true) {
        if (this.sens_du_joueur == 1){
          if(this.angle >= 105 && this.angle <= 180) this.angle = 180-this.angle;
            if(this.angle > 180 && this.angle <= 270){
              this.angle = this.angle-180;
              this.angle = 360 - this.angle;
              if (this.sens_du_joueur == 1 && this.tourner_arme > 75) this.tourner_arme = 75;
          }
        }
        if (this.sens_du_joueur == 0){
          if(this.angle >= 0 && this.angle <= 75) this.angle = 180-this.angle;
            if(this.angle >= 270 && this.angle <= 360){
              this.angle = this.angle-180;
              this.angle = 360 - this.angle;              
              if (this.sens_du_joueur == 0 && this.tourner_arme < -75) this.tourner_arme = -75;
          }
        }
        this.tourner_arme *= -1;
        this.changement_de_direction = false;
      }
      if (this.angle < 0) this.angle = 360+angle;
      if (this.angle >= 360) this.angle = this.angle-360;
      if (InputEngine.key_is_pressed(this.control[2]) || InputEngine.key_is_pressed(this.control[3])) {
        if (this.sens_du_joueur == 1 && InputEngine.key_is_pressed(this.control[2]) && this.tourner_arme <= 90 && this.tourner_arme > -75) {
          this.angle_rotation_arme = -3;
          this.tourner_arme += this.angle_rotation_arme;
          this.angle -= this.angle_rotation_arme;
        } else if (this.sens_du_joueur == 1 && InputEngine.key_is_pressed(this.control[3]) && this.tourner_arme < 90 && this.tourner_arme >= -75) {
          this.angle_rotation_arme = 3;
          this.tourner_arme += this.angle_rotation_arme;
          this.angle -= this.angle_rotation_arme;
        } else if (this.sens_du_joueur == 0 && InputEngine.key_is_pressed(this.control[2]) && this.tourner_arme >= -90 && this.tourner_arme < 75) {
          this.angle_rotation_arme = 3;
          this.tourner_arme += this.angle_rotation_arme;
          this.angle -= this.angle_rotation_arme;
        } else if (this.sens_du_joueur == 0 && InputEngine.key_is_pressed(this.control[3]) && this.tourner_arme > -90 && this.tourner_arme <= 75) {
          this.angle_rotation_arme = -3;
          this.tourner_arme += this.angle_rotation_arme;
          this.angle -= this.angle_rotation_arme;
         }
      }
      Graphisme.weaponpaint(this.pos_x, this.pos_y, this.sens_du_joueur, this.tourner_arme, this.arme_a_gauche, this.arme_a_droite);
   }

  public void test_changement_de_direction() {
    if (InputEngine.key_is_pressed(this.control[0]) || InputEngine.key_is_pressed(this.control[1])) {
      if (this.regarde_a_gauche == false && InputEngine.key_is_pressed(this.control[0])) {
        this.regarde_a_gauche = true;
        this.changement_de_direction = true;
      }
      if (this.regarde_a_gauche == true && InputEngine.key_is_pressed(this.control[1])) {
        this.regarde_a_gauche = false;
        this.changement_de_direction = true;
      }
    }
  }
  
  public void chargeur_vide_ou_non(){
    int difference;
    boolean chargeur_vide = ParticuleurBalle.chargeur_vide;
    if (chargeur_vide == true){
      this.debut = millis();
    }
    int chrono = millis();
    difference = chrono - this.debut;
    if (difference >= this.temps_rechargement[this.numero_de_l_arme]) { this.munitions_chargeur_vide = this.chargeur[this.numero_de_l_arme]; } else { this.munitions_chargeur_vide = 0; }
  }
}
class Balle {
  private float x;
  private float y;
  private float angle;
  private int vitesse;
  private float Vx;
  private float Vy;
  private final int VIDE = 0;
  
  public Balle(float x, float y, float angle, int vitesse){
    this.x=x;
    this.y=y;
    this.angle = angle;
    this.vitesse = vitesse;
    this.Vx = vitesse*cos(radians(this.angle));
    this.Vy = vitesse*sin(radians(-this.angle));
  }
  public void move(){
    this.x += this.Vx;
    this.y += this.Vy;
  }
  public void display(){
    rect(this.x,this.y,6/f,6/f);
  }
  public boolean finished(Terrain Terrain){
    if(this.x > width || this.x < 0 || this.y > height ||this.y < 0 
    || (Terrain.tableau_objet_dur[(int)(this.y/(32/f))][(int)(this.x/(32/f))] != VIDE && Terrain.tableau_objet_mi_dur[(int)(this.y/(32/f))][(int)(this.x/(32/f))] == VIDE)
    ){
      return true;
    }else{
      return false;
    }
  }
}
class Collision{
  private Player Player1;
  private Player Player2;
  public Collision(Player Player1, Player Player2){
    this.Player1 = Player1;
    this.Player2 = Player2;
  }
  
  public boolean tester_balle_joueur(int num_joueur_qui_tire){
    float pos_x_ennemi;
    float pos_y_ennemi;
    float pos_x_balle = 0;
    float pos_y_balle = 0;
    ArrayList<Balle> balls;
    boolean touche = false;
    if(num_joueur_qui_tire == 1){
      pos_x_ennemi = Player2.Player_Move.position_x(2)/f;
      pos_y_ennemi = Player2.Player_Move.position_y(2)/f;
      balls = Player1.Arme.ParticuleurBalle.balls;
    }else{
      pos_x_ennemi = Player1.Player_Move.position_x(1)/f;
      pos_y_ennemi = Player1.Player_Move.position_y(1)/f;
      balls = Player2.Arme.ParticuleurBalle.balls;
    }
    for(int i=balls.size()-1; i>=0; i--){
      Balle Balle = balls.get(i);
      pos_x_balle = Balle.x;
      pos_y_balle = Balle.y;
      if(pos_x_balle > pos_x_ennemi && pos_x_balle < pos_x_ennemi+28 && pos_y_balle > pos_y_ennemi && pos_y_balle < pos_y_ennemi+64){
        touche = true;
      }
    }
    return touche;
  }
}
class Game{
  private Player Player1;
  private Player Player2;
  private Terrain Terrain;
  private Collision Collision;
  private boolean with_move = true;
  private boolean recommencer_partie = true;
  private boolean mettre_a_zero_le_minuteur = true;
  private boolean charger_la_map = true;
  private int s;
  private int debut;
  public Game(){
    this.Player1 = new Player(1,96,5*32);
    this.Player2 = new Player(2,960,4*32);
    this.Collision = new Collision(this.Player1, this.Player2);
    this.Terrain = new Terrain();
  }
  
  public void game(int num_map){
    background(100);
    charger_la_map(num_map);
    Terrain.draw(num_map);
    
    Player1.draw(this.with_move,this.Terrain);
    Player2.draw(this.with_move,this.Terrain);
    
    if(a_player_is_dead()){
      recompense();
      restart_player();
      
      mettre_a_zero_le_minuteur = true;
      this.recommencer_partie = true;
    }
    if(Collision.tester_balle_joueur(1)) Player2.prend_des_degats(Player1.Arme.degats);
    if(Collision.tester_balle_joueur(2)) Player1.prend_des_degats(Player2.Arme.degats);
    if(this.recommencer_partie == true) debuter_nouvelle_partie();
    
  }
  
  public void charger_la_map(int num_map){
    if(charger_la_map == true){
      Terrain.loadMap(num_map);
      charger_la_map = false;
    }
  }
  
  public void debuter_nouvelle_partie(){
    this.with_move = false;
    choisir_arme();
    if(tout_le_monde_est_pret() == true){
      lancer_chrono(3);
    } 
  }
  
  public void choisir_arme(){
    if(Player1.ready == false){ Player1.armeselect(); }else{ textSize(20/f); text("READY",5/f,90/f);  }
    if(Player2.ready == false){ Player2.armeselect(); }else{ textSize(20/f); text("READY",1342/f,90/f); }
  }
  
  public void lancer_chrono(int duree){
    if(mettre_a_zero_le_minuteur ==true){
      debut = millis();
      mettre_a_zero_le_minuteur = false;
    }
    s = millis();
    textSize(50/f);
    fill(0);
    text(str(duree-(s-debut)/1000),736/f,200/f);
   if(duree-(s-debut)/1000 <= 0){
    this.recommencer_partie = false;
    this.with_move = true;
   }
  }
  public void restart_player(){
    Player1.restart();
    Player2.restart();
  }
  
  public void recompense(){
    if(Player1.pdv <= 0 && Player2.pdv <= 0){
      Player1.recompense();
      Player2.recompense();
    }else if(Player1.pdv <= 0 && Player2.pdv > 0){
      Player2.recompense();
    }else if(Player2.pdv <= 0 && Player1.pdv > 0){
      Player1.recompense();
    }
  }
  
  private boolean tout_le_monde_est_pret(){
    if(Player1.ready == true && Player2.ready == true){
      return true;
    }else{
      return false;
    }
  }
  private boolean a_player_is_dead(){
    if(Player1.pdv <= 0 || Player2.pdv <= 0){
      return true;
    }else{
      return false;
    }
  }
}
class GameEngine{
  private Menu Menu;
  private Game Game;
  private SoundEngine SoundEngine;
  
  public GameEngine(){
    this.SoundEngine = new SoundEngine();
    this.Menu = new Menu();
    this.Game = new Game();
  }
   
  public void APL(){   
    if(Menu.fin_du_menu) {
      Game.game(Menu.num_map);
      SoundEngine.Stop_music();
    }else{ 
      Menu.menu();
      SoundEngine.music();
    }
  }
}
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
  
  public void MenuPaint(int num_map){
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
  
  public void charger_image_du_menu(int num_map){
    this.image_menu = loadImage("./Menu_Ressources/image_menu_" +str(numero_resolution)+".png");
    this.miniature = loadImage("./Map_Ressources/"+str(num_map)+"/miniature_image_terrain_"+str(numero_resolution)+".png");
    this.image_menu_chargee = false;
    image(image_menu,0,0);
  }
  
  public void OptionPaint(){
    if(this.image_option_chargee == false) this.charger_image_des_options();
    image(image_option,0,0);
    fill(0xff316BA3);
    rect(width/2-60/f,height/2+300/f,120/f,50/f);
    if(mouseX > width/2-60/f && mouseX < width/2+60/f && mouseY > height/2+300/f && mouseY < height/2+350/f){       fill(255);     }else{    fill(0); }
    textSize(30/f);
    textAlign(CENTER);
    text("RETOUR",width/2,height/2+340/f);
    textAlign(LEFT);
    fill(0);
  }
  
  public void charger_image_des_options(){
    this.image_option = loadImage("./Menu_Ressources/image_option_"+str(numero_resolution)+".png");
    this.image_option_chargee = true; 
  }
  
 public void barre_de_son(int x,int y,int taille, float x_button){
  textSize(40/f);
  text("Volume",100/f,70/f);
  stroke(0xff55D3F7);
  fill(0);
  rect(x,y-10/f,taille,20/f);
  line(x,y,x+taille,y);
  stroke(0);
  fill(0xff55D3F7);
  rect(x_button-10/f,y-20/f,20/f,40/f);
  fill(0);
  rect(x_button-6/f,y-12/f,4/f,24/f);
  rect(x_button+2/f,y-12/f,4/f,24/f);
  }
  
  public void option_resolPaint(int num_new_resolution){
    noStroke();
    textSize(40/f);
    text("R\u00e9solution",1100/f,70/f);
    textSize(25/f);
    if(num_new_resolution == 0){ fill(0xff55D3F7); }else{ fill(0); }
    rect(1100/f,90/f,30/f,30/f);
    fill(0);
    text("1472 x 800",1150/f,115/f);
    if(num_new_resolution == 1){ fill(0xff55D3F7); }else{ fill(0); }
    rect(1100/f,160/f,30/f,30/f);
    fill(0);
    text("736 x 400",1150/f,185/f);
    stroke(0);
  }
  
  public void resolution_restart(){
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

  public void playerpaint(int num_player, float pos_x, float pos_y, int sens, int etat, int localisation){
    img = loadImage("./Graphisme_Ressources/player_" + str(num_player) + "/" + str(sens) + "_" + str(etat) + "_" + str(numero_resolution) + "_" + str(localisation) + ".png");
    image(img,pos_x,pos_y);
  }
  
  public void weaponpaint(float pos_x, float pos_y, int sens_du_joueur, int tourner_arme, PImage arme_a_gauche, PImage arme_a_droite){
    pushMatrix();
    if (sens_du_joueur == 0) { translate(pos_x+15/f, pos_y+36/f); } else { translate(pos_x+15/f, pos_y+36/f); }
    rotate(radians(tourner_arme));
    imageMode(CENTER);
    if (sens_du_joueur == 0) { image(arme_a_gauche, 0, 0); } else { image(arme_a_droite, 0, 0); }
    imageMode(CORNER);
    popMatrix();
  }
  
  public void caractplayer(int num_player, int pdv, int cash, int points_armure){
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
  
  public void chargeurpaint(int munitions, int num_player){
    if (num_player == 1) {
      text(munitions,200/f,113/f);
    } else {
      textAlign(RIGHT);
      text(munitions,1280/f,115/f);
      textAlign(LEFT);
    }
  }
  
  public void armeselect(int num_player, int menu_selection, int maximum){  
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
  
  public void dessiner_terrain(int num_map){
    if(this.image_chargee == false){
      image_terrain = loadImage("./Map_Ressources/"+str(num_map)+"/image_terrain_"+ str(numero_resolution) + ".png");
      this.image_chargee = true;
    }
    image(image_terrain,0,0);
  }
  

  public void option_controlPaint(int case_x, int case_y, String[] controls_j1, String[] controls_j2){
    String[] str_control = {"left", "right", "up", "down", "jump", "shoot"};
    textSize(40/f);
    text("Controls",100/f,190/f);
    textSize(30/f);
    text("Player 1",10/f,290/f);
      for(int i = 0; i < controls_j1.length; i++){
      textSize(20/f);
      fill(0);
      text(str_control[i],i*85/f+150/f,240/f);
      fill(0xff55D3F7);
      rect(i*85/f+150/f,250/f,60/f,60/f);
      if( i==case_x && case_y== 1) { fill(255); }else{ fill(0);}
      textSize(30/f);
      text(controls_j1[i],i*85/f+170/f,290/f);
    }
    fill(0);
    text("Player 2",10/f,360/f);
      for(int i = 0; i < controls_j2.length; i++){
      fill(0xff55D3F7);
      rect(i*85/f+150/f,320/f,60/f,60/f);
      if( i==case_x && case_y ==2) { fill(255); }else{ fill(0);}
      textSize(30/f);
      text(controls_j2[i],i*85/f+170/f,360/f);
    }
  }
}
class InputEngine {
  public void keyPressed() {
    if(keyCode <= 144){
      touches[keyCode]=true;
    }
  }

  public void keyReleased() {
    if(keyCode <= 144){
      touches[keyCode]=false;
    }
  }

  public boolean key_is_pressed(int c) {
    return touches[c];
  }
  
  public boolean key_is_not_pressed(int c) {
    if (touches[c]){
      return false;  
    } else { 
      return true;
    }
  }
  
  public int keycode(){
    return keyCode;
  }
}
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
  
  public void menu(){
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
  
  public void APL_option(){
    Option.option();
  } 
}
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
    control_j1 = PApplet.parseInt(str);
    str = loadStrings("Player_Ressources/control_2.txt");
    control_j2 = PApplet.parseInt(str);
  }
  public void option(){
    this.ne_plus_charger_la_page_option = false;
    Graphisme.OptionPaint();
    if(mousePressed) if(mouseX > width/2-60/f && mouseX < width/2+60/f && mouseY > height/2+300/f && mouseY < height/2+350/f) quitter_les_options();
    this.barre_de_son(this.x_barre_de_son/f,this.y_barre_de_son/f, this.taille_barre_de_son/f, this.x_button_barre_de_son/f);
    this.gerer_la_resolution();
    this.option_control_player();
  }
  public void quitter_les_options(){
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
  public void save_controls(){
    String[] str1 = {controls_j1[0], controls_j1[1], controls_j1[2], controls_j1[3], controls_j1[4],controls_j1[5]};
    saveStrings("/data/Player_Ressources/control_1_str.txt", str1);
    String[] str2 = {str(control_j1[0]), str(control_j1[1]), str(control_j1[2]), str(control_j1[3]), str(control_j1[4]), str(control_j1[5])};
    saveStrings("/data/Player_Ressources/control_1.txt", str2);
    String[] str3 = {controls_j2[0], controls_j2[1], controls_j2[2], controls_j2[3], controls_j2[4], controls_j2[5]};
    saveStrings("/data/Player_Ressources/control_2_str.txt", str3);
    String[] str4 = {str(control_j2[0]), str(control_j2[1]), str(control_j2[2]), str(control_j2[3]), str(control_j2[4]), str(control_j2[5])};
    saveStrings("/data/Player_Ressources/control_2.txt", str4);
  }
  
  public void barre_de_son(int x,int y, int taille, float x_button){
    if(init_button_son == false){
      this.x_button_barre_de_son = map(SoundEngine.powerInit,-40,0.1f,x,x+taille)*f;
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
  public void gerer_la_resolution(){
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
  public void option_control_player(){
    
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
              if(PApplet.parseInt(key) != 65535) {
                this.controls_j1[this.case_x] = str(key);
              }else if(KC == 38){
                this.controls_j1[this.case_x] = "\u2191";
              }else if(KC == 40){
                this.controls_j1[this.case_x] = "\u2193";
              }else if(KC == 37){
                this.controls_j1[this.case_x] = "\u2190";
              }else if(KC == 39){
                this.controls_j1[this.case_x] = "\u2192";
              }else if(KC == 16){
                this.controls_j1[this.case_x] = "\u25b2"; 
              }else{
                this.controls_j1[this.case_x] = "?";
              }
            }
            if(this.case_y == 2){
              int KC = 0;
              for(int j=0; j<144; j++){
                if(touches[j] == true) { this.control_j2[this.case_x] = j; KC = j; }
              }
              if(PApplet.parseInt(key) != 65535) {
                this.controls_j2[this.case_x] = str(key);
              }else if(KC == 38){
                this.controls_j2[this.case_x] = "\u2191";
              }else if(KC == 40){
                this.controls_j2[this.case_x] = "\u2193";
              }else if(KC == 37){
                this.controls_j2[this.case_x] = "\u2190";
              }else if(KC == 39){
                this.controls_j2[this.case_x] = "\u2192";
              }else if(KC == 16){
                this.controls_j2[this.case_x] = "\u25b2";
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
class ParticuleurBalle {
  private ArrayList<Balle> balls;
  private int debut;
  private int le_joueur_peut_retirer;
  private int chrono;
  private boolean demarrer_le_minuteur = true;
  private int chargeur;
  private boolean caracteristiques_armes_chargees = false;
  private float random_entre_angle_et_angle_imprecision;
  private float angle_imprecision;
  private boolean chargeur_vide = false;
  private int chargeur_vide_prend_sa_valeur = 0;
  
  public ParticuleurBalle() {
    this.balls = new ArrayList<Balle>();
    this.balls.add(new Balle(1, 1, 1, 0));
  }

  public void tir(float x, float y, float angle, int vitesse, int temps_entre_chaque_tir, int chargeur_de_l_arme, int temps_de_rechargement_de_l_arme, float angle_d_imprecision){
    if (this.caracteristiques_armes_chargees == false) {
      this.chargeur = chargeur_de_l_arme;
      this.caracteristiques_armes_chargees = true;
    }   
     if (this.demarrer_le_minuteur == true){
       this.debut = millis();
       this.demarrer_le_minuteur = false;
     }    
    this.chrono = millis();
    this.le_joueur_peut_retirer = this.chrono - this.debut; 
    if (this.chargeur != 0){
      this.chargeur_vide_prend_sa_valeur = 0;
      this.chargeur_vide = false;
    }
    if (this.chargeur == 0){
      this.chargeur_vide = false;
      if (this.chargeur_vide_prend_sa_valeur == 0){
        this.chargeur_vide = true;
        this.chargeur_vide_prend_sa_valeur = 1;
      }
      if (this.le_joueur_peut_retirer >= this.debut/1000 + temps_de_rechargement_de_l_arme){
        this.chargeur += chargeur_de_l_arme;
      }
    }  
    if(this.le_joueur_peut_retirer >= this.debut/1000 + temps_entre_chaque_tir && this.chargeur > 0){
      this.demarrer_le_minuteur = true;
      this.chargeur -= 1;
      float random = (int)random(0,2);
      float random_d_imprecision = random(0,angle_d_imprecision);
      if (random == 0) angle_imprecision = angle + random_d_imprecision;
      if (random == 1) angle_imprecision = angle - random_d_imprecision;
      this.balls.add(new Balle(x,y,angle_imprecision,vitesse));
     }
   }

  public void charger_balles(Terrain Terrain) {
    for (int i = balls.size ()-1; i >= 0; i--) {
      Balle Balle = this.balls.get(i);
      Balle.move();
      if (Balle.finished(Terrain)) {
        balls.remove(i);
      }
      Balle.display();
    }
  }
}
class Player {
  private int num_player;
  private int localisation;
  private int[] section = new int[2];
  private String[] controls;
  private int[] control;
  private String[] liste_arme;
  private String[] liste_armure;
  private PImage select;
  private boolean ready;
  private Graphisme Graphisme;
  private Arme Arme;
  private InputEngine InputEngine;
  private Player_Move Player_Move;
  private int numero_arme;
  private int numero_armure;
  private int pdv;
  private int cash = 0;
  private String[] prix_str_armes;
  private String[] prix_str_armures;
  private int[] prix_armes;
  private int[] prix_armures;
  private int[] protection_armure;
  private boolean peut_rechoisir_une_arme = true;
  private int menu_selection = 0;
  private int maximum;
  private boolean peut_choisir_une_armure;
  private int points_armure;
  private boolean transition_bouclier_a_pdv = true;
  
  public Player(int num_player, float pos_x, float pos_y) {
    
    this.num_player = num_player;
    this.Graphisme = new Graphisme();
    this.InputEngine = new InputEngine();
    this.Arme = new Arme();
    this.Player_Move = new Player_Move(num_player, pos_x, pos_y);
    this.controls = loadStrings("Player_Ressources/control_" + str(num_player) + ".txt");
    this.control = new int[this.controls.length];
    for(int i=0; i <= controls.length-1; i++){
      this.control[i] = PApplet.parseInt(controls[i]);
    }
    this.liste_arme = loadStrings("./Graphisme_Ressources/armes/liste_arme.txt");
    this.liste_armure = loadStrings("./Graphisme_Ressources/armures/liste_armure.txt");
    this.select = loadImage("./Graphisme_Ressources/game/select_" + str(numero_resolution) + ".png");
    this.prix_str_armes = new String[this.liste_arme.length];
    this.prix_str_armures = new String[this.liste_armure.length];
    this.prix_armes = new int[this.liste_arme.length];
    this.prix_armures = new int[this.liste_armure.length];
    this.protection_armure = new int[this.liste_armure.length];
      
    for(int j = 0; j < this.liste_arme.length; j++){
      String[] prix_str_armes_tmp = loadStrings("./Graphisme_Ressources/armes/" + str(j) + "/caractarme.txt");
      this.prix_armes[j] = PApplet.parseInt(prix_str_armes_tmp[0]);
    }
    for(int k = 0; k < this.liste_armure.length; k++){
      String[] armure_tmp = loadStrings("./Graphisme_Ressources/armures/" + str(k) + "/caractarmure.txt");
      this.prix_armures[k] = PApplet.parseInt(armure_tmp[0]);
      this.protection_armure[k] = PApplet.parseInt(armure_tmp[1]); 
    }
  }

  public void draw(boolean with_move, Terrain Terrain) {
    if(with_move == true ){
      if(InputEngine.key_is_pressed(48) && this.num_player == 1){
        this.pdv -= 40;
      }else if(InputEngine.key_is_pressed(49) && this.num_player == 2){
        this.pdv -= 40;
      }
      Player_Move.move(num_player, Terrain);
      this.calculer_etat();
      if(le_joueur_tir()){
        tirer();
      }
    } 
    Graphisme.playerpaint(this.num_player, Player_Move.position_x(this.num_player)/f, Player_Move.position_y(this.num_player)/f, Player_Move.sens(num_player), this.etat, this.localisation);
    Graphisme.caractplayer(this.num_player, this.pdv, this.cash, this.points_armure);
    Arme.APL_Arme(this.num_player, Player_Move.position_x(this.num_player)/f, Player_Move.position_y(this.num_player)/f, Player_Move.sens(this.num_player), with_move,this.numero_arme, Player_Move.est_sur_une_echelle(), Terrain);
  }
  
  int compteur = 0;
  
  public void armeselect(){
    Graphisme.armeselect(this.num_player, this.menu_selection, this.maximum);
    selectionner();
    if (this.menu_selection == 0) selectionner_armes_ou_armures();
    if (this.menu_selection != 0) selectionner_equipements();
    image(select, 368/f*num_player+4/f, section[num_player-1]*32/f);
  }
  
  public void prend_des_degats(int degats){
    int transition;
    if (this.points_armure > 0){ this.points_armure -= degats; }else{ this.pdv -= degats; }
    if (this.points_armure < 0 && this.transition_bouclier_a_pdv == true){
      transition = -(this.points_armure);
      this.points_armure = 0;
      this.pdv -= transition;
      this.transition_bouclier_a_pdv = false;
    }
    if (this.pdv <= 0) {
      this.numero_arme = 0;
      this.cash += 25;
    } 
  }

  public void selectionner(){
    if(this.menu_selection == 0) this.maximum = 1;
    if(this.menu_selection == 1) this.maximum = liste_arme.length-1;
    if(this.menu_selection == 2) this.maximum = liste_armure.length-1;

    if(InputEngine.key_is_pressed(this.control[2]) && this.section[num_player-1] > 0){
        compteur++;
        if(compteur >= 4){
          this.section[num_player-1]--;
          compteur = 0;
        }
      }else if(InputEngine.key_is_pressed(this.control[3]) && this.section[num_player-1] < this.maximum){
        compteur++;
        if(compteur >= 4){
          this.section[num_player-1]++;
          compteur = 0;
        }
      }else{
        compteur = 0;
      }
      if(InputEngine.key_is_pressed(this.control[4])){
        this.ready = true;
      }
      if(InputEngine.key_is_pressed(this.control[0])){
        this.menu_selection = 0;
        this.section[num_player-1] = 0;
      }
  }

  public void selectionner_armes_ou_armures(){
    if (InputEngine.key_is_pressed(this.control[1])) {
      this.menu_selection = this.section[num_player-1]+1;
      this.peut_rechoisir_une_arme = false;
    }
  }
  
  public void selectionner_equipements(){
      if((this.menu_selection == 1 && InputEngine.key_is_pressed(this.control[1]) && this.prix_armes[this.section[num_player-1]] <= this.cash && this.peut_rechoisir_une_arme == true)
      || (this.menu_selection == 2 && InputEngine.key_is_pressed(this.control[1]) && this.prix_armures[this.section[num_player-1]] <= this.cash && this.peut_rechoisir_une_arme == true)){
        if(this.menu_selection == 1) {
          this.numero_arme = this.section[num_player-1];
          this.cash -= this.prix_armes[this.section[num_player-1]];          
        }
        if(this.menu_selection == 2) {
          this.numero_armure = this.section[num_player-1];
          this.cash -= this.prix_armures[this.section[num_player-1]];
          this.points_armure = protection_armure[this.numero_armure];
        }
        this.peut_rechoisir_une_arme = false;
      }     
      if (InputEngine.key_is_not_pressed(this.control[1])){
        this.peut_rechoisir_une_arme = true;
      }            
    }
  
  public void tirer(){
    Arme.tir(Player_Move.est_sur_une_echelle());
  }
  
  public void restart(){
    this.localisation = 0;
    this.etat = 0;
    section = new int[2];
    this.ready = false;
    Player_Move.restart();
    Arme.restart();
    this.pdv = 100;
    this.peut_choisir_une_armure = true;
  }
  
  public void recompense(){
    this.cash += 50;
  }
  
  private int compteur1_etat = 1;
  private int compteur2_etat = 0;
  private int etat;
  public void calculer_etat() {
    if (Player_Move.joueur_bouge_au_sol() == true) {
      this.localisation = 0;
      this.compteur1_etat++;
      if (this.compteur1_etat == 5) {
        this.compteur2_etat++;
        this.compteur1_etat =1;
      }
      this.etat= compteur2_etat%4;
    }else if(Player_Move.est_sur_une_echelle() == true && Player_Move.soumis_a_la_gravite() == true) {
      if(Player_Move.n_a_pas_monte_completement_lechelle() == true){
        if (Player_Move.joueur_bouge_verticalement() == true) {
          this.localisation = 1;
          this.compteur1_etat++;
          if (this.compteur1_etat == 5) {
            this.compteur2_etat++;
            this.compteur1_etat =1;
          }
          this.etat = compteur2_etat%4;
          
        }else{
          this.compteur1_etat = 1;
          this.compteur2_etat = 0;
          this.etat = 0;
        }
      }else{
        this.localisation = 0;
        this.etat = 0;
      }
    }
  }
  
  private boolean le_joueur_tir(){
    if(InputEngine.key_is_pressed(this.control[5])){
      return true;
    }else{
      return false;
    }
  }
}
class Player_Move{
  private boolean controls_charges = false;
  private boolean soumis_a_la_gravite = true;
  private float PIED_JOUEUR;
  private int position_tableau_corps_a_gauche_x;
  private int position_tableau_corps_a_droite_x;
  private int position_arrondie_tableau_corps_a_gauche_x;
  private int position_arrondie_tableau_corps_a_droite_x;
  private int position_tableau_tete_y;
  private int position_tableau_pied_y;
  private int hauteur_finale_du_saut = 128;
  private int hauteur_du_saut;
  private boolean saute;
  final char ESPACE = ' ';
  final int VIDE = 0;
  final int ECHELLE = 1;
  private String[] controls;
  private int[] control;
  private Graphisme Graphisme;
  private Terrain Terrain;
  private float pos_x;
  private float pos_y;
  private int num_player;
  private int sens_du_joueur;
  private InputEngine InputEngine;
  
  public Player_Move(int num_player, float pos_x, float pos_y){
    this.pos_x = pos_x;
    this.pos_y = pos_y;
    this.Graphisme = new Graphisme();
    this.InputEngine = new InputEngine();
    this.controls = loadStrings("Player_Ressources/control_" + str(num_player)+ ".txt");
    this.control = new int[this.controls.length];
    for(int i=0; i <= controls.length-1; i++){
      this.control[i] = PApplet.parseInt(controls[i]);
    }

  }
  
  public void move(int num_player, Terrain Terrain){
    this.Terrain = Terrain;
    this.num_player = num_player;
    this.position_tableau_corps_a_gauche_x = (int)(this.pos_x/32);
    this.position_tableau_corps_a_droite_x = (int)((this.pos_x+28)/32);
    this.position_tableau_tete_y =  (int)(this.pos_y/32);
    this.position_tableau_pied_y = (int)((this.pos_y+32)/32);
    if(est_sur_une_echelle() == false && arrive_sur_une_echelle() == false && this.saute == false) this.gravite();
    this.peut_bouger_lateralement();
    this.contact_avec_echelle();
    this.peut_sauter();
    this.test_sens_du_joueur();
    if(this.saute == true) this.saut();
  }
  
  public float position_x(int num_player){
    return this.pos_x;
  }
  public float position_y(int num_player){
    return this.pos_y;
  }
  public int sens(int num_player){
    return this.sens_du_joueur;
  }
  
  public void restart(){
    restart_position();
    this.sens_du_joueur = 0;
  }
  public void restart_position(){
    boolean position_trouvee = false;
    int random_x = 0;
    int random_y = 0;
    while(position_trouvee == false){
      random_x = (int)((random(1,1472))/32);
      random_y = (int)((random(1,(800-2*32)))/32);
      if(Terrain.tableau_objet_dur[random_y][random_x] == VIDE && Terrain.tableau_objet_dur[random_y+1][random_x] == VIDE && Terrain.tableau_objet_dur[random_y+2][random_x] != VIDE){
        position_trouvee = true;
      }
    }
    this.pos_x = random_x*32;
    this.pos_y = random_y*32;
  }
  
    public void peut_bouger_lateralement(){
      
    if(a_le_droit_de_bouger_a_gauche()==true){
      if(InputEngine.key_is_pressed(this.control[0])) this.pos_x -= 4;
    }
    if(a_le_droit_de_bouger_a_droite()==true){
      if(InputEngine.key_is_pressed(this.control[1])) this.pos_x += 4;   
    }
  }
  public void peut_sauter(){
   
    if(rencontre_un_objet_en_tombant() == true && InputEngine.key_is_pressed(this.control[4])) this.saute = true;
      if(collision_pendant_le_saut() == true || touche_pas_le_plafond() == false){
        this.saute = false;
        this.hauteur_du_saut = 0;
      } 
    }
  public void saut(){
    
    this.hauteur_du_saut +=8;
    this.pos_y -= 8;
    if(this.hauteur_du_saut == hauteur_finale_du_saut) this.saute = false;
    if(this.saute == false) this.hauteur_du_saut =0;
    
  }
  
  
  
  
  
  public void gravite(){
    if(soumis_a_la_gravite()==true)  this.subit_la_gravite();
      if(rencontre_un_objet_en_tombant()==true) this.pos_y = this.position_tableau_tete_y*32;
  }
  public void subit_la_gravite(){
    this.pos_y +=7;
  }
  
  
  
  
  
  public void contact_avec_echelle(){
    soumis_a_la_gravite = false;
    if(this.arrive_sur_une_echelle() == true) this.se_replace_sur_lechelle();
    this.bouge_sur_lechelle();
  }
  public void se_replace_sur_lechelle(){
    if(arrive_sur_une_echelle() == true && InputEngine.key_is_pressed(this.control[2]) || InputEngine.key_is_pressed(this.control[3])){
      if(Terrain.tableau_echelles[this.position_tableau_tete_y][this.position_tableau_corps_a_gauche_x] == ECHELLE) this.pos_x = (this.position_tableau_corps_a_gauche_x *32);
      if(Terrain.tableau_echelles[this.position_tableau_tete_y][this.position_tableau_corps_a_droite_x] == ECHELLE) this.pos_x = (this.position_tableau_corps_a_droite_x *32);
      if(Terrain.tableau_echelles[this.position_tableau_pied_y][this.position_tableau_corps_a_gauche_x] == ECHELLE) this.pos_x = (this.position_tableau_corps_a_gauche_x *32);
      if(Terrain.tableau_echelles[this.position_tableau_pied_y][this.position_tableau_corps_a_droite_x] == ECHELLE) this.pos_x = (this.position_tableau_corps_a_droite_x *32);
      if(Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_gauche_x] == ECHELLE) this.pos_x = (this.position_tableau_corps_a_gauche_x *32);
      if(Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_droite_x] == ECHELLE) this.pos_x = (this.position_tableau_corps_a_droite_x *32);
    }
  }
  public void bouge_sur_lechelle(){
    if(est_sur_une_echelle()==true){
        if(InputEngine.key_is_pressed(this.control[2]) && ((Terrain.tableau_echelles[this.position_tableau_pied_y][this.position_tableau_corps_a_gauche_x] == ECHELLE || Terrain.tableau_echelles[this.position_tableau_pied_y][this.position_tableau_corps_a_droite_x] == ECHELLE)||(Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_gauche_x] == ECHELLE || Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_droite_x] == ECHELLE))) this.pos_y -=4;
        if(InputEngine.key_is_pressed(this.control[3]) && (Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_gauche_x] == ECHELLE || Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_droite_x] == ECHELLE)) this.pos_y +=4;
    }
  }
  
  public void test_sens_du_joueur(){
    if(InputEngine.key_is_pressed(this.control[0])){
      this.sens_du_joueur = 0;
    }else if(InputEngine.key_is_pressed(this.control[1])){
      this.sens_du_joueur = 1;
    }
  }
  
  
  
  
  
  
  
  
  
  
  
  public boolean rencontre_un_objet_en_tombant(){
    return (Terrain.tableau_objet_dur[this.position_tableau_pied_y +1][this.position_tableau_corps_a_gauche_x] != VIDE
          ||Terrain.tableau_objet_dur[this.position_tableau_pied_y +1][this.position_tableau_corps_a_droite_x] != VIDE);
  }
  public boolean soumis_a_la_gravite(){
    return (Terrain.tableau_objet_dur[this.position_tableau_pied_y +1][this.position_tableau_corps_a_gauche_x] == VIDE
          &&Terrain.tableau_objet_dur[this.position_tableau_pied_y +1][this.position_tableau_corps_a_droite_x] == VIDE);
  }
  public boolean n_a_pas_monte_completement_lechelle(){
    return (Terrain.tableau_echelles[this.position_tableau_pied_y][this.position_tableau_corps_a_gauche_x] != VIDE
          &&Terrain.tableau_echelles[this.position_tableau_pied_y +1][this.position_tableau_corps_a_gauche_x] != VIDE);
  }
  
  
  
  
  public boolean a_le_droit_de_bouger_a_gauche(){
  return (this.pos_x >=32
          && Terrain.tableau_objet_dur[this.position_tableau_tete_y][this.position_tableau_corps_a_gauche_x] == VIDE
          && Terrain.tableau_objet_dur[this.position_tableau_pied_y][this.position_tableau_corps_a_gauche_x] == VIDE);
 
  }
  public boolean a_le_droit_de_bouger_a_droite(){
  return (this.pos_x <=1440
          && Terrain.tableau_objet_dur[this.position_tableau_tete_y][this.position_tableau_corps_a_droite_x] == VIDE
          && Terrain.tableau_objet_dur[this.position_tableau_pied_y][this.position_tableau_corps_a_droite_x] == VIDE);
  }
  public boolean collision_pendant_le_saut(){
    return (saute == true && (Terrain.tableau_objet_dur[this.position_tableau_tete_y][this.position_tableau_corps_a_droite_x] != VIDE
                              || Terrain.tableau_objet_dur[this.position_tableau_tete_y][this.position_tableau_corps_a_gauche_x] != VIDE)); 
  }
  public boolean touche_pas_le_plafond(){
    if(this.pos_y > 0){
      return true;
    }else{
      return false;
    }
  }
  
  
  
  
  public boolean est_sur_une_echelle(){
    return ((Terrain.tableau_echelles[this.position_tableau_tete_y][this.position_tableau_corps_a_gauche_x] == ECHELLE
          && Terrain.tableau_echelles[this.position_tableau_tete_y][this.position_tableau_corps_a_droite_x] == ECHELLE)
          ||(Terrain.tableau_echelles[this.position_tableau_pied_y][this.position_tableau_corps_a_gauche_x] == ECHELLE
          && Terrain.tableau_echelles[this.position_tableau_pied_y][this.position_tableau_corps_a_droite_x] == ECHELLE)
          ||(Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_gauche_x] == ECHELLE
          && Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_droite_x] == ECHELLE));
  }
  public boolean arrive_sur_une_echelle(){
    return ((Terrain.tableau_echelles[this.position_tableau_tete_y][this.position_tableau_corps_a_gauche_x] == ECHELLE && Terrain.tableau_echelles[this.position_tableau_tete_y][this.position_tableau_corps_a_droite_x] != ECHELLE
          ||Terrain.tableau_echelles[this.position_tableau_tete_y][this.position_tableau_corps_a_droite_x] == ECHELLE && Terrain.tableau_echelles[this.position_tableau_tete_y][this.position_tableau_corps_a_gauche_x] != ECHELLE
          ||Terrain.tableau_echelles[this.position_tableau_pied_y][this.position_tableau_corps_a_gauche_x] == ECHELLE && Terrain.tableau_echelles[this.position_tableau_pied_y][this.position_tableau_corps_a_droite_x] != ECHELLE
          ||Terrain.tableau_echelles[this.position_tableau_pied_y][this.position_tableau_corps_a_droite_x] == ECHELLE && Terrain.tableau_echelles[this.position_tableau_pied_y][this.position_tableau_corps_a_gauche_x] != ECHELLE
          ||Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_gauche_x] == ECHELLE && Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_droite_x] != ECHELLE
          ||Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_droite_x] == ECHELLE && Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_gauche_x] != ECHELLE));
  }
  public boolean ne_touche_plus_lechelle(){
    return (Terrain.tableau_echelles[this.position_tableau_tete_y][this.position_tableau_corps_a_droite_x] != ECHELLE
          &&Terrain.tableau_echelles[this.position_tableau_tete_y][this.position_tableau_corps_a_gauche_x] != ECHELLE
          &&Terrain.tableau_echelles[this.position_tableau_pied_y][this.position_tableau_corps_a_droite_x] != ECHELLE
          &&Terrain.tableau_echelles[this.position_tableau_pied_y][this.position_tableau_corps_a_gauche_x] != ECHELLE);
  }
  
  
  public boolean joueur_bouge_au_sol(){
    if(InputEngine.key_is_pressed(this.control[0]) || InputEngine.key_is_pressed(this.control[1])){
      return true;
    }else{
      return false;
    }
  }
  public boolean joueur_bouge_verticalement(){
    if(InputEngine.key_is_pressed(this.control[2]) || InputEngine.key_is_pressed(this.control[3])){
      return true;
    }else{
      return false;
    }
  }
}








class SoundEngine{
  private boolean charger_le_niveau_du_son = true;
  private String[] power = loadStrings("./Option_Ressources/niveau_son.txt");
  private int powerInit = PApplet.parseInt(power[0]);
  private float modulateGain;
  public void music(){ 
    song.play();
    this.initialiser_volume(); 
  }
  public void Stop_music(){  
    song.close();
  }
  
  public void regler_le_volume(int x,int y,int taille, float x_button){
    
    this.modulateGain = map(x_button,x,x+taille,-40,0.1f);
    song.setGain(this.modulateGain);
    if(this.modulateGain <= -37.5f){
      song.mute();
    }else{
      song.unmute();
    }
  }
  public void initialiser_volume(){
    if(this.charger_le_niveau_du_son == true){
      if(powerInit <= -37.5f){
        song.mute();
      }else{
        song.unmute();
        song.setGain(powerInit);
      }
      
      this.charger_le_niveau_du_son = false;
      
    }
  }
}
class Terrain {
  private Graphisme Graphisme;
  private int[][] tableau_objet_dur = new int[25][47];
  private int[][] tableau_objet_mi_dur = new int[25][47];
  private int[][] tableau_echelles = new int[25][47];
  public Terrain() {
    this.Graphisme = new Graphisme();
  }
  public void loadMap(int num_map) {

    String[] tableau_dur_str = loadStrings("./Map_Ressources/"+str(num_map)+"/tableau_objet_dur.txt");
    String[] tableau_mi_dur_str = loadStrings("./Map_Ressources/"+str(num_map)+"/tableau_objet_mi_dur.txt");
    String[] tableau_echelles_str = loadStrings("./Map_Ressources/"+str(num_map)+"/tableau_echelles.txt");
    for (int j = 0; j <= 24; j++) {
      for (int i = 0; i < 46; i++) {
        this.tableau_objet_dur[j][i] = PApplet.parseInt(tableau_dur_str[j].charAt(i))-48;
        this.tableau_objet_mi_dur[j][i] = PApplet.parseInt(tableau_mi_dur_str[j].charAt(i))-48;
        this.tableau_echelles[j][i] = PApplet.parseInt(tableau_echelles_str[j].charAt(i))-48;
      }
    }
  }
  public void draw(int num_map) {
    Graphisme.dessiner_terrain(num_map);
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#000000", "--stop-color=#cccccc", "The_Survival_Game" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
