//Olivia Kim
//Pokemon RPG style game
//You can add more pokemons and more options later on 
//This file will get information from the user and run the major part of the game
import java.io.*;
import java.util.*;
import java.util.Random;
public class PokemonMain{
	private static ArrayList<Pokemon> allPokes; //global variables for pokemon 
	private static ArrayList<Pokemon> goodPokes;
	private static ArrayList<Pokemon> badPokes;
	private static ArrayList<Pokemon> notuserPokes;
	private static Pokemon userPoke;
	private static Random rand = new Random(); 
	private static int turn = -1; //random turn
	private static boolean lost = false; //when user doesn't have any goodPoke left game ends
	public static void main(String []arg){
		allPokes = new ArrayList<Pokemon>();
		goodPokes = new ArrayList<Pokemon>();
		badPokes = new ArrayList<Pokemon>();
		notuserPokes = new ArrayList<Pokemon>();
		loadPokes(); 
		display();
		goodPick();
		badPick();
		Collections.shuffle(badPokes);
		for(Pokemon badGuy:badPokes){
			if(goodPokes.size()<1){
					lost = true;
					break;
			}
			prologue(badGuy);
			while (badGuy.getHealth()>0){
				turn = rand.nextInt(2);
				
				if(turn==0){
					chooseAct(badGuy);
					if(goodPokes.size()<1){
					lost = true;
					break;
					}
					if(badGuy.getHealth()>0){
						badGuyAct(badGuy);
					}
					if(goodPokes.size()<1){
						lost = true;
						break;
					}	
				}
				else if(turn==1){
					if(badGuy.getHealth()>0){
						badGuyAct(badGuy);
					}
					if(goodPokes.size()<1){
						lost = true;
						break;
					}
					chooseAct(badGuy);
					if(goodPokes.size()<1){
						lost = true;
						break;
					}
				}
			}
			if(lost){
				break;
			}
			recoverene();
			recoverhealth();
			userPoke.recoverDebuff();
		}
		if(lost){
			System.out.println("Maybe next time, try again.");
		}
		else{
			System.out.println("Congrats, you are now a Trainer Supreme");
		}
	}

	public static void loadPokes(){
		try{ //loads pokemon and construct data
			Scanner inFile = new Scanner (new BufferedReader(new FileReader("pokemon.txt")));
			inFile.nextLine(); //skip first line that has a number
			while(inFile.hasNextLine()){ //stops when there is no more line
				allPokes.add(new Pokemon(inFile.nextLine())); //add all the pokemon 
			}
			inFile.close();
		}
		catch(IOException ex){ //when there is no text file
			System.out.println("No txt file found");
		}
	}
	public static void display(){ //display all the pokemon
		System.out.println("|__________________________________________________________________________|");
		for (int i = 0; i<allPokes.size(); i++){
			System.out.printf(" %d %s ",i+1,allPokes.get(i));
		}
		System.out.println("|--------------------------------------------------------------------------|");
	}
	public static void goodPick(){ 
		String[] suffixes = {"st","nd","rd","th"};
		Scanner kb = new Scanner(System.in);
		while(goodPokes.size()<4){
			System.out.printf("Pick your %d%s Pokemon\n",goodPokes.size()+1,suffixes[goodPokes.size()]);
			String uIn = kb.nextLine().replaceAll("[^0-9]+",""); //only get integers
			int num = -1;
			if(!uIn.equals("")){ //if uIn was only made up with integers 
				num = Integer.parseInt(uIn);
				if(num>0 && num < allPokes.size()+1){ // user input is in range 0 - length of all pickable pokemon
					Pokemon pokemon = allPokes.get(num-1);
					if (goodPokes.contains(pokemon)){  //if goodPoke already have that pokemon the user can't choose the same guy again  
						System.out.println("You have already chosen that Pokemon. Please choose a different one");	
					}
					else{
						goodPokes.add(allPokes.get(num-1));	
					}
				}
			}
		}
	}
	public static void badPick(){//computer pokemon will be allpoke - goodpoke
		for (int i = 0; i < allPokes.size(); i++){
			Pokemon pokemon = allPokes.get(i);
			if(!goodPokes.contains(pokemon)){
				badPokes.add(allPokes.get(i));
			}
		}
	}
	public static void prologue(Pokemon bad){ //user will pick his pokemon
		Scanner kb = new Scanner (System.in);	
		getuserPoke();
		System.out.printf("%s, I choose you!\n",userPoke.getName());
		System.out.printf("Player has encountered a wild %s!\n",bad.getName());
	}
	public static void getuserPoke(){
		Scanner kb = new Scanner (System.in);	
		for(int i = 0; i < goodPokes.size(); i++){ //display pokemons that are still alive
			System.out.printf("%d %s HP: %d Energy: %d \n",i+1,goodPokes.get(i).getName(), goodPokes.get(i).getHealth(), goodPokes.get(i).getEne());
		}
		System.out.println("Choose a Pokemon that's going to fight for you:");
		String uIn = kb.nextLine().replaceAll("[^0-4]+",""); 	
		int num = -1;
		if(!uIn.equals("")){
			num = Integer.parseInt(uIn);
			userPoke = goodPokes.get(num-1);
		}
		notuserPokes = new ArrayList<Pokemon>(); //this variable is for retreat/resets everytime in case userpoke has been changed
		for(Pokemon goodPoke: goodPokes){
			if(!goodPoke.getName().equals(userPoke.getName())){
				notuserPokes.add(goodPoke); //if goodPoke is not in notuserpoke it will be added
			}
		}
	}
	public static void chooseAct(Pokemon badGuy){ 
		System.out.println(badGuy.returnstatus());
		if(userPoke.returnstatus()){
			System.out.printf("What should %s do?\n1.Fight \n2.Retreat \n3.Pass\n",userPoke.getName());
			Scanner kb = new Scanner (System.in);
			int act = kb.nextInt();
			if (act==1){ //fight
				chooseAttack(badGuy); //deals dmg and everything
				if(badGuy.getHealth()>0){
					System.out.printf("%s has %dHP\n",badGuy.getName(),badGuy.getHealth());
				}
				else{
					System.out.printf("%s has fainted!!\n",badGuy.getName());
				}
			}
			else if(act==2){ //retreat
				retreat();
			}
			else if(act==3){ //pass
				userPoke.recoverEne();
				System.out.printf("Player has passed his/her turn, %s's current energy: %d\n",userPoke.getName(),userPoke.getEne());
			}
		}
		else{
			System.out.printf("%s has been stunned. It can't move.\n",userPoke.getName());
			userPoke.recoverEne();
			userPoke.recoverstatus();
		}
	}
	public static void chooseAttack(Pokemon badGuy){
		System.out.println("Attacks:");
		Scanner kb = new Scanner (System.in);
		for(int i = 0; i<userPoke.getAttacks();i++){
			System.out.println(userPoke.toStringAttack(i)); //prints all the options
		}
		int num = kb.nextInt();
		if(userPoke.checkEne(num-1)){
			System.out.printf("%s used %s! \n",userPoke.getName(), userPoke.getAtt(num));
			userPoke.attack(badGuy,num-1); //calls attack method from pokemon class
			while(userPoke.returnstorm()){ //wild storm is true
				if(badGuy.getHealth()<=0){//badguy will faint 
					break;
				}
				userPoke.attack(badGuy,num-1);
			}
		}
		else{
			System.out.printf("%s doesn't have enough energy, please pick a different attack/action.\n",userPoke.getName());
			chooseAct(badGuy); //user has to pick something else
		}
	}
	public static void retreat(){
		Scanner kb = new Scanner(System.in);
		System.out.printf("%s, come back!\n",userPoke.getName());
		System.out.println("Which Pokemon do you want to send out?");
		for (int i = 0; i < notuserPokes.size(); i++){ //print all the pokemon that the user can switch to
			System.out.printf("%d %s HP: %d Energy: %d \n",i+1,notuserPokes.get(i).getName(),notuserPokes.get(i).getHealth(),notuserPokes.get(i).getEne());
		}
		Pokemon poke = userPoke;
		while(userPoke==poke){
			String uIn = kb.nextLine().replaceAll("[^0-3]+","");
			int num = -1;
			if(!uIn.equals("")){
				num = Integer.parseInt(uIn);
				userPoke = notuserPokes.get(num-1); //userPoke has been updated
			}
		}
		System.out.printf("%s, I choose you!\n",userPoke.getName());
	}
	public static void badGuyAct(Pokemon badGuy){
		badGuy.addAtt(); //get whatever the badguy can attack
		if(badGuy.returnstatus()){//not stunned
			if (badGuy.getattssize()>0){
				int num = rand.nextInt(badGuy.getattssize());
				System.out.printf("Wild %s used %s!! \n",badGuy.getName(),badGuy.getatt(num));
				badGuy.attack(userPoke,num); //deal dmg and energy-cost
				if(userPoke.getHealth()>0){
					System.out.printf("%s's health: %dHP\n",userPoke.getName(),userPoke.getHealth());
				}
				else{
					System.out.printf("%s has fainted!!\n",userPoke.getName());
					for (int i=0; i<goodPokes.size();i++){
						if(goodPokes.get(i).equals(userPoke)){
							goodPokes.remove(i); //removed because you can't revive
						}
					}
					if(goodPokes.size()>0){
						getuserPoke(); //pick a new Pokemon
						System.out.printf("%s, I choose you!\n",userPoke.getName());
					}
				}
			}
			else{//pass if badguy can't attack
				badGuy.recoverEne();
				System.out.printf("Wild %s has recovered his energy. Current energy: %d\n",badGuy.getName(),badGuy.getEne());
			}
		}
		else{ //stunned
			System.out.printf("%s has been stunned. It can't move.\n",badGuy.getName());
			badGuy.recoverEne();
			badGuy.recoverstatus();
		}
	}
	public static void recoverene(){
		for(Pokemon good: goodPokes){
			good.recoverEne();
		}
	}
	public static void recoverhealth(){
		for(Pokemon good: goodPokes){
			good.recoverhp();
		}
	}
}
