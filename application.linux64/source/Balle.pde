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
  void move(){
    this.x += this.Vx;
    this.y += this.Vy;
  }
  void display(){
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