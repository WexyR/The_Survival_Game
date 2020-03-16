class InputEngine {
  void keyPressed() {
    if(keyCode <= 144){
      touches[keyCode]=true;
    }
  }

  void keyReleased() {
    if(keyCode <= 144){
      touches[keyCode]=false;
    }
  }

  public boolean key_is_pressed(int c) {
    return touches[c];
  }
  
  public boolean key_is_not_pressed(int c) {
    if (touches[c]){
      return false;  
    } else { 
      return true;
    }
  }
  
  public int keycode(){
    return keyCode;
  }
}

