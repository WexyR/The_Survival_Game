class GameEngine{
  private Menu Menu;
  private Game Game;
  private SoundEngine SoundEngine;
  
  public GameEngine(){
    this.SoundEngine = new SoundEngine();
    this.Menu = new Menu();
    this.Game = new Game();
  }
   
  void APL(){   
    if(Menu.fin_du_menu) {
      Game.game(Menu.num_map);
      SoundEngine.Stop_music();
    }else{ 
      Menu.menu();
      SoundEngine.music();
    }
  }
}