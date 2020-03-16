class Collision{
  private Player Player1;
  private Player Player2;
  public Collision(Player Player1, Player Player2){
    this.Player1 = Player1;
    this.Player2 = Player2;
  }
  
  boolean tester_balle_joueur(int num_joueur_qui_tire){
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