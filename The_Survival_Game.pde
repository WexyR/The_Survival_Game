GameEngine GameEngine;
InputEngine InputEngine;
public boolean[] touches=new boolean[145];
public int numero_resolution;
public int f;
public boolean mouseReleased;

void settings(){
  String[] lines = loadStrings("resolution.txt");
  numero_resolution = int(lines[0]);                              //|  Ces lignes permettent juste de charger la taille de la fenêtre
  f = numero_resolution+1;
  size(int(lines[2*int(lines[0])+1]),int(lines[2*int(lines[0])+2]));  //|  du jeu depuis un fichier texte exterieur
}

void setup(){
  minim = new Minim(this);
  song = minim.loadFile("./data/Menu_Ressources/song.mp3"); /* import du son de fond depuis la superclass obligatoire, du a la library Minim */
  GameEngine = new GameEngine();
  InputEngine = new InputEngine();
}

void draw(){
  frameRate(40);
  clear();
  GameEngine.APL();
  mouseReleased = false;
}

/* import du son de fond depuis la superclass obligatoire, dû a la library Minim */
Minim minim;
AudioPlayer song;
void stop() {
  song.close();
  minim.stop();
  super.stop();
}

/*donne a InputEngine les droits maximum concernant les entrées des données clavier*/
void keyPressed(){
  InputEngine.keyPressed();
}
void keyReleased(){
  InputEngine.keyReleased();
}

void mouseReleased(){
  mouseReleased = true;
}