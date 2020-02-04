//Olivia Kim
//constructors 
//This file has all the data you need for the pokemons to make the game work
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
import java.util.Random;
class Pokemon{
	private String name, special, type, resist, weak; //private variables that belongs to Pokemon class
	private ArrayList<Attack> attacks = new ArrayList<Attack>();
	private ArrayList<Attack> atts = new ArrayList<Attack>();
	private int hp, num, originalhp;
	private Random rand = new Random();	
	private int energy = 50;
	private boolean spestorm = false;
	private boolean specard = true;
	private boolean status=true;
	private int times = 0 ;
	private int debuff = 0;
	public Pokemon(String data){ //construct Pokemons from the data
		String [] stats = data.split(",");
		name = stats[0];
		hp= Integer.parseInt(stats[1]);
		originalhp= hp;
		type = stats[2];
		resist = stats[3];
		weak = stats[4];
		num = Integer.parseInt(stats[5]);
		for (int i = 0; i/4<num; i+=4){ //make another list for attacks
			String attname = stats[6+i];
			int cost = Integer.parseInt(stats[7+i]);
			int dmg = Integer.parseInt(stats[8+i]);
			String spec = stats[9+i];
			attacks.add(new Attack(attname,cost,dmg,spec)); //attacks will be classified as "Attack"
		}
    }    
	public void attack(Pokemon other, int attnum){ //reduces hp / user's pokmon attacks enemy pokemon
		Attack att = attacks.get(attnum); //pick the attack that user/badPoke wants
		energy-=att.cost; //energy reduced
		attspecial(att,other); 
		if (type.equals(other.resist)){ //dmg depends on the type and resist
			if ((att.dmg-debuff)/2>0){ //prevents from debuff to make the dmg negative
				if(specard){ //when hit
					System.out.printf("%s's attack wasn't very effective...\n",name);
					other.hp-= (att.dmg-debuff)/2; 
				}
			}
		}
		else if(type.equals(other.weak)){
			if ((att.dmg-debuff)*2>0){
				if(specard){
					System.out.printf("%s's attack was VERY VERY VERY effective!!!\n",name);
					other.hp-= (att.dmg-debuff)*2;
				}
			}
		}
		else{	
			if((att.dmg-debuff)>0){
				if (specard){
					other.hp-= (att.dmg-debuff);
				}
			}
		}
		specard = true; //resets everything
	}
	public void attspecial(Attack att,Pokemon other){ 
		if (att.special.equals("recharge")){
			energy+=20;
		}
		else if(att.special.equals("disable")){
			if(other.debuff==0){
				System.out.printf("%s has been disabled!\n",other.name);
			}
			else if(other.debuff==10){
				System.out.printf("%s has already been disabled!\n",other.name);
			}
			other.debuff = 10; //this will be continued until the battle ends
		}
		else if(att.special.equals("wild card")){
			int t = rand.nextInt(2);
			if(t==0){
				specard = true; //hit
			}
			else if(t==1){
				System.out.printf("%s's attack missed!\n",name);
				specard= false; //missed
			}
		}
		else if(att.special.equals("wild storm")){
			String[] suffixes = {"st","nd","rd","th"};
			int t = rand.nextInt(2);
			
			if(t==0){
				spestorm = true;
				energy+=att.cost;
				if(times+1>3){ //display how many time the pokemon used wild storm
					System.out.printf("%s's attack has suceeded for the %s%s time!\n",name,times+1,suffixes[3]);
				}
				else{
					System.out.printf("%s's attack has suceeded for the %s%s time! ",name,times+1,suffixes[times]);
				}
				times+= 1;
			}
			else if(t==1){
				spestorm = false;
				times = 0; //reset
			}
		}
		else if(att.special.equals("stun")){
			int t = rand.nextInt(2);
			if(t==0){

			}
			else if(t==1){
				other.status = false; //other pokemon gets stunned	
				System.out.printf("%s has been stunned!\n",other.name);
			}
		}
	}
	public boolean returnstorm(){
		return spestorm;
	}

	public boolean returnstatus(){
		return status;
	}
	public void recoverstatus(){ //pokemon is not stunned anymore
		status=true;
	}
	public boolean checkEne(int attnum){ //true if the pokemon has enough energy
		Attack att = attacks.get(attnum);
		if (energy-att.cost>=0){
			return true;
		}
		return false;
	}
	public void recoverEne(){
		if (energy+10<=50){
			energy+=10;
		}
	}
	public int getHealth(){ //returns for info
		return hp;
	}
    public int getAttacks(){ //attack number
    	return num;
    }
    public String getName(){
    	return name;
    }
    public int getEne(){
    	return energy;
    }
    public String getAtt(int at){ // attack name * VERY DIFFERENT WITH getAttacks METHOD
    	return attacks.get(at-1).attname;
    }
	public String toStringAttack(int at){ //prints info for attack 
		return String.format("%d. %s Energy Cost: %d Damage: %d Special: %s",at+1,attacks.get(at).attname,attacks.get(at).cost,attacks.get(at).dmg,attacks.get(at).special);
	}
    public String toString(){ //displays user's choice of pokemons
    	return String.format(" Name:%s  HP: %d  Type: %s  Resist: %s  Weakness: %s  \n", name, hp, type, resist, weak);
    }
    public void addAtt(){ //add all the attack that can use
    	atts = new ArrayList<Attack>();
   		for (int i = 0; i<num;i++){
			Attack att = attacks.get(i);
			if ((energy-att.cost)>=0){
				atts.add(att);
			}
		}
    }
    public String getatt(int num){
    	return atts.get(num).attname;
    }
    public int getattssize(){
    	return atts.size();
    }
    public void recoverhp(){
    	if(hp+20<=originalhp){
    		hp+=20;
    	}
    	else if(originalhp-hp<20 && originalhp-hp>=0){
    		hp=originalhp;
    	}
    }
    public void recoverDebuff(){
    	debuff = 0;
    }
	class Attack{
		private String attname, special; //variables for name cost dmg special
		private int cost, dmg;

		public Attack(String attname, int cost , int dmg, String special){ //constructor
			this.attname = attname;
			this.cost = cost;
			this.dmg = dmg;
			this.special = special;
		}	
	} 
}
