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
  
  void game(int num_map){
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
  
  void charger_la_map(int num_map){
    if(charger_la_map == true){
      Terrain.loadMap(num_map);
      charger_la_map = false;
    }
  }
  
  void debuter_nouvelle_partie(){
    this.with_move = false;
    choisir_arme();
    if(tout_le_monde_est_pret() == true){
      lancer_chrono(3);
    } 
  }
  
  void choisir_arme(){
    if(Player1.ready == false){ Player1.armeselect(); }else{ textSize(20/f); text("READY",5/f,90/f);  }
    if(Player2.ready == false){ Player2.armeselect(); }else{ textSize(20/f); text("READY",1342/f,90/f); }
  }
  
  void lancer_chrono(int duree){
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
  void restart_player(){
    Player1.restart();
    Player2.restart();
  }
  
  void recompense(){
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