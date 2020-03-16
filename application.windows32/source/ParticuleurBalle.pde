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

  void tir(float x, float y, float angle, int vitesse, int temps_entre_chaque_tir, int chargeur_de_l_arme, int temps_de_rechargement_de_l_arme, float angle_d_imprecision){
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

  void charger_balles(Terrain Terrain) {
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