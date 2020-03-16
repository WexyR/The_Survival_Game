import ddf.minim.spi.*;
import ddf.minim.signals.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.ugens.*;
import ddf.minim.effects.*;


class SoundEngine{
  private boolean charger_le_niveau_du_son = true;
  private String[] power = loadStrings("./Option_Ressources/niveau_son.txt");
  private int powerInit = int(power[0]);
  private float modulateGain;
  void music(){ 
    song.play();
    this.initialiser_volume(); 
  }
  void Stop_music(){  
    song.close();
  }
  
  void regler_le_volume(int x,int y,int taille, float x_button){
    
    this.modulateGain = map(x_button,x,x+taille,-40,0.1);
    song.setGain(this.modulateGain);
    if(this.modulateGain <= -37.5){
      song.mute();
    }else{
      song.unmute();
    }
  }
  void initialiser_volume(){
    if(this.charger_le_niveau_du_son == true){
      if(powerInit <= -37.5){
        song.mute();
      }else{
        song.unmute();
        song.setGain(powerInit);
      }
      
      this.charger_le_niveau_du_son = false;
      
    }
  }
}
