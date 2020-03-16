class Terrain {
  private Graphisme Graphisme;
  private int[][] tableau_objet_dur = new int[25][47];
  private int[][] tableau_objet_mi_dur = new int[25][47];
  private int[][] tableau_echelles = new int[25][47];
  public Terrain() {
    this.Graphisme = new Graphisme();
  }
  void loadMap(int num_map) {

    String[] tableau_dur_str = loadStrings("./Map_Ressources/"+str(num_map)+"/tableau_objet_dur.txt");
    String[] tableau_mi_dur_str = loadStrings("./Map_Ressources/"+str(num_map)+"/tableau_objet_mi_dur.txt");
    String[] tableau_echelles_str = loadStrings("./Map_Ressources/"+str(num_map)+"/tableau_echelles.txt");
    for (int j = 0; j <= 24; j++) {
      for (int i = 0; i < 46; i++) {
        this.tableau_objet_dur[j][i] = int(tableau_dur_str[j].charAt(i))-48;
        this.tableau_objet_mi_dur[j][i] = int(tableau_mi_dur_str[j].charAt(i))-48;
        this.tableau_echelles[j][i] = int(tableau_echelles_str[j].charAt(i))-48;
      }
    }
  }
  void draw(int num_map) {
    Graphisme.dessiner_terrain(num_map);
  }
}


