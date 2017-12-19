package controller;

import model.*;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.stage.Stage;
import view.GlobalView;
import view.LabyrinthView;

import java.util.*;

import view.ElementView;
import view.PlayerView;

/**
 * Controller class.
 * This class handles communication between model and view.
 * It contains one single instance of view and model.
 */
public class Controller {

	/**
	 * Instance of controller
	 */
	private static Controller instance = null;
	private Stage stage;

	private static GlobalView globalView;
	private static Game game;

    private Controller() {
        game = Game.getInstance();
        globalView= GlobalView.getInstance();
    }

	private EventHandler<KeyEvent> keyboardListener = event -> {
        switch (event.getCode()) {

            case UP:
            	// Déplacement du personnage
            	game.getPlayer().seDeplacer(game.getPlayer().getX(),game.getPlayer().getY()-1);
                globalView.getPlayerView().updatePosition(game.getPlayer().getX(),game.getPlayer().getY());
                areColliding(game.getCandies());
                areColliding(game.getEnemies());
                break;
            case DOWN:
            	game.getPlayer().seDeplacer(game.getPlayer().getX(),game.getPlayer().getY()+1);
				globalView.getPlayerView().updatePosition(game.getPlayer().getX(), game.getPlayer().getY());
                areColliding(game.getCandies());
				areColliding(game.getEnemies());
                break;
            case LEFT:
            	game.getPlayer().seDeplacer(game.getPlayer().getX()-1,game.getPlayer().getY());
				globalView.getPlayerView().updatePosition(game.getPlayer().getX(), game.getPlayer().getY());
                areColliding(game.getCandies());
				areColliding(game.getEnemies());
                break;
            case RIGHT:
            	game.getPlayer().seDeplacer(game.getPlayer().getX()+1,game.getPlayer().getY());
				globalView.getPlayerView().updatePosition(game.getPlayer().getX(), game.getPlayer().getY());
                areColliding(game.getCandies());
				areColliding(game.getEnemies());
                break;

        }
    };


		public static Controller makeInstance() {
			if (instance == null)
				instance = new Controller();
			return instance;
		}

		public void start(Stage primaryStage){
			this.stage = primaryStage;

			ArrayList wallCoordinates = generateWalls(game.getLabyrinth().getWalls());

            game.getCandies().forEach((key, candy)-> globalView.addCandyView(key, candy.getX(), candy.getY()));

            game.getEnemies().forEach((key, enemy)-> globalView.addEnemyView(enemy.getX(), enemy.getY()));

			game.getButtons().forEach((key, button)-> globalView.addEnemyView(button.getX(), button.getY()));


			globalView.createGlobalView(primaryStage, wallCoordinates);
			globalView.getLabyrinthView().setOnAction(keyboardListener);
			primaryStage.show();
		}

		private ArrayList generateWalls(Set<List<Vertex>> walls){
			ArrayList<Integer> wallsCoordinates = new ArrayList();
			walls.forEach((wall) -> {
				int xs = wall.get(0).getX();
				int ys = wall.get(0).getY();
				int xt = wall.get(1).getX();
				int yt = wall.get(1).getY();
				wallsCoordinates.add(xs);
				wallsCoordinates.add(ys);
				wallsCoordinates.add(xt);
				wallsCoordinates.add(yt);
			});
			return wallsCoordinates;
		}

		//No for each loop for this reason : You cannot do that because inserting/removing an element into the Collection screws the AbstractList generated by the Iterator
		//When you use the for(Object : Collection) syntax you cannot modify the original Collection while you process the AbstractList
		//I admit that is not well documented but modifying a Collection while you use an Iterator to scan it is NOT supported thow your Exception
	//source : http://www.dreamincode.net/forums/topic/140116-solved-thanks-guysconcurrent-modification-exception/
		private int areColliding(HashMap<Integer, Element> elementHashMap){
		    int coordPlayerX = game.getPlayer().getX();
		    int coordPlayerY = game.getPlayer().getY();
		    System.out.println(elementHashMap.toString());

		    elementHashMap.forEach((key, e) -> {
				if (coordPlayerX == e.getX() && coordPlayerY == e.getY()){
					System.out.println("Collision");
					elementHashMap.remove(key);
					if (e instanceof Candy){
						globalView.removeCandyFromView(key);
					}
					if (e instanceof Enemy) {
						game.resetGame();
						globalView.resetView();
						start(this.stage);
					}

				}
		    });
		    return 0;
        }


}
