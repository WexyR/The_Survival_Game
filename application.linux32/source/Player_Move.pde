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
      this.control[i] = int(controls[i]);
    }

  }
  
  void move(int num_player, Terrain Terrain){
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
  
  void restart(){
    restart_position();
    this.sens_du_joueur = 0;
  }
  void restart_position(){
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
  
    void peut_bouger_lateralement(){
      
    if(a_le_droit_de_bouger_a_gauche()==true){
      if(InputEngine.key_is_pressed(this.control[0])) this.pos_x -= 4;
    }
    if(a_le_droit_de_bouger_a_droite()==true){
      if(InputEngine.key_is_pressed(this.control[1])) this.pos_x += 4;   
    }
  }
  void peut_sauter(){
   
    if(rencontre_un_objet_en_tombant() == true && InputEngine.key_is_pressed(this.control[4])) this.saute = true;
      if(collision_pendant_le_saut() == true || touche_pas_le_plafond() == false){
        this.saute = false;
        this.hauteur_du_saut = 0;
      } 
    }
  void saut(){
    
    this.hauteur_du_saut +=8;
    this.pos_y -= 8;
    if(this.hauteur_du_saut == hauteur_finale_du_saut) this.saute = false;
    if(this.saute == false) this.hauteur_du_saut =0;
    
  }
  
  
  
  
  
  void gravite(){
    if(soumis_a_la_gravite()==true)  this.subit_la_gravite();
      if(rencontre_un_objet_en_tombant()==true) this.pos_y = this.position_tableau_tete_y*32;
  }
  void subit_la_gravite(){
    this.pos_y +=7;
  }
  
  
  
  
  
  void contact_avec_echelle(){
    soumis_a_la_gravite = false;
    if(this.arrive_sur_une_echelle() == true) this.se_replace_sur_lechelle();
    this.bouge_sur_lechelle();
  }
  void se_replace_sur_lechelle(){
    if(arrive_sur_une_echelle() == true && InputEngine.key_is_pressed(this.control[2]) || InputEngine.key_is_pressed(this.control[3])){
      if(Terrain.tableau_echelles[this.position_tableau_tete_y][this.position_tableau_corps_a_gauche_x] == ECHELLE) this.pos_x = (this.position_tableau_corps_a_gauche_x *32);
      if(Terrain.tableau_echelles[this.position_tableau_tete_y][this.position_tableau_corps_a_droite_x] == ECHELLE) this.pos_x = (this.position_tableau_corps_a_droite_x *32);
      if(Terrain.tableau_echelles[this.position_tableau_pied_y][this.position_tableau_corps_a_gauche_x] == ECHELLE) this.pos_x = (this.position_tableau_corps_a_gauche_x *32);
      if(Terrain.tableau_echelles[this.position_tableau_pied_y][this.position_tableau_corps_a_droite_x] == ECHELLE) this.pos_x = (this.position_tableau_corps_a_droite_x *32);
      if(Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_gauche_x] == ECHELLE) this.pos_x = (this.position_tableau_corps_a_gauche_x *32);
      if(Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_droite_x] == ECHELLE) this.pos_x = (this.position_tableau_corps_a_droite_x *32);
    }
  }
  void bouge_sur_lechelle(){
    if(est_sur_une_echelle()==true){
        if(InputEngine.key_is_pressed(this.control[2]) && ((Terrain.tableau_echelles[this.position_tableau_pied_y][this.position_tableau_corps_a_gauche_x] == ECHELLE || Terrain.tableau_echelles[this.position_tableau_pied_y][this.position_tableau_corps_a_droite_x] == ECHELLE)||(Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_gauche_x] == ECHELLE || Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_droite_x] == ECHELLE))) this.pos_y -=4;
        if(InputEngine.key_is_pressed(this.control[3]) && (Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_gauche_x] == ECHELLE || Terrain.tableau_echelles[this.position_tableau_pied_y+1][this.position_tableau_corps_a_droite_x] == ECHELLE)) this.pos_y +=4;
    }
  }
  
  void test_sens_du_joueur(){
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