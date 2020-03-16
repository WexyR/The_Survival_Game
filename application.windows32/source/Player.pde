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
      this.control[i] = int(controls[i]);
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
      this.prix_armes[j] = int(prix_str_armes_tmp[0]);
    }
    for(int k = 0; k < this.liste_armure.length; k++){
      String[] armure_tmp = loadStrings("./Graphisme_Ressources/armures/" + str(k) + "/caractarmure.txt");
      this.prix_armures[k] = int(armure_tmp[0]);
      this.protection_armure[k] = int(armure_tmp[1]); 
    }
  }

  void draw(boolean with_move, Terrain Terrain) {
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
  
  void armeselect(){
    Graphisme.armeselect(this.num_player, this.menu_selection, this.maximum);
    selectionner();
    if (this.menu_selection == 0) selectionner_armes_ou_armures();
    if (this.menu_selection != 0) selectionner_equipements();
    image(select, 368/f*num_player+4/f, section[num_player-1]*32/f);
  }
  
  void prend_des_degats(int degats){
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

  void selectionner(){
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

  void selectionner_armes_ou_armures(){
    if (InputEngine.key_is_pressed(this.control[1])) {
      this.menu_selection = this.section[num_player-1]+1;
      this.peut_rechoisir_une_arme = false;
    }
  }
  
  void selectionner_equipements(){
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
  
  void tirer(){
    Arme.tir(Player_Move.est_sur_une_echelle());
  }
  
  void restart(){
    this.localisation = 0;
    this.etat = 0;
    section = new int[2];
    this.ready = false;
    Player_Move.restart();
    Arme.restart();
    this.pdv = 100;
    this.peut_choisir_une_armure = true;
  }
  
  void recompense(){
    this.cash += 50;
  }
  
  private int compteur1_etat = 1;
  private int compteur2_etat = 0;
  private int etat;
  void calculer_etat() {
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