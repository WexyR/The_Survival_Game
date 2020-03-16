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
      this.degats_armes[j] = int(arme_tmp[2]);
      this.temps_entre_chaque_tir[j] = int(arme_tmp[4]);
      this.chargeur[j] = int(arme_tmp[5]);
      this.temps_rechargement[j] = int(arme_tmp[6]);
      this.angle_d_imprecision[j] = int(arme_tmp[1]);
    }
  }

  void APL_Arme(int num_player, float pos_x, float pos_y, int sens_du_joueur, boolean with_move, int numero_arme, boolean echelle, Terrain Terrain) {
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
  
  void tir(boolean echelle){
    if (echelle == false){
      this.ParticuleurBalle.tir(this.pos_x+14/f, this.pos_y+28/f, this.angle,32/f,this.temps_entre_chaque_tir[this.numero_de_l_arme],this.chargeur[this.numero_de_l_arme],
      this.temps_rechargement[this.numero_de_l_arme], this.angle_d_imprecision[this.numero_de_l_arme]);
    }
  }
  
  void restart(){
    this.ParticuleurBalle = new ParticuleurBalle();
    this.tourner_arme = 0;
    this.angle = 180;
    this.sens_du_joueur = 0;
    this.regarde_a_gauche = true;
    this.munitions = 10;
  }

  void charger_images() {
    arme_a_gauche  = loadImage("Graphisme_Ressources/armes/" + str(this.numero_de_l_arme)+"/" + str(this.num_player-1) + "_" + str(numero_resolution) + "_0" + ".png");
    arme_a_droite  = loadImage("Graphisme_Ressources/armes/" + str(this.numero_de_l_arme)+"/" + str(this.num_player-1) + "_" + str(numero_resolution) + "_1" + ".png");
    if (this.controles_charges == false) {
      this.control = new int[this.controls.length];
      for (int i=0; i <= controls.length-1; i++) {
        this.control[i] = int(controls[i]);
        this.controles_charges = true;
      }
    }
  }

  void correction_de_l_angle(){
    if (this.sens_du_joueur == 1 && this.tourner_arme == 0) this.angle = 0;
    if (this.sens_du_joueur == 1 && this.tourner_arme == -45) this.angle = 45;
    if (this.sens_du_joueur == 1 && this.tourner_arme == 45) this.angle = 315;
    if (this.sens_du_joueur == 0 && this.tourner_arme == 0) this.angle = 180;
    if (this.sens_du_joueur == 0 && this.tourner_arme == 45) this.angle = 135;
    if (this.sens_du_joueur == 0 && this.tourner_arme == -45) this.angle = 225;
  }  
  
  void tourner_arme() {
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

  void test_changement_de_direction() {
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
  
  void chargeur_vide_ou_non(){
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

