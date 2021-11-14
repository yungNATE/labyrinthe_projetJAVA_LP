import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GameMap extends Fenetre {

    //PROP
   public   HashMap<Position, Tile> left;
   public   HashMap<Position, Tile> right;
   int tileSize = 50;
    List<Position> caisses;

    //CONSTR
    public GameMap(String map) {
        super(0, 0, Color.BLACK);

        this.setLayout(null);

        setTitle("Labyrinth");
        setLocationRelativeTo(null);

        setUpLabyrinths(map);
        drawMaps();

        setVisible(true);
    }

    //METH
    // affiche l'écran de fin : victoire = true / défaite = false
    public void ecranDeFin(Boolean isWin) {
        setVisible(false);
        JFrame fin = new JFrame(isWin ? "Fin heureuse" : "Crack...");
        JLabel etat = new JLabel(isWin ? "Bravo ! Tu as vaincu le labyrinthe." : "Quel dommage... Une chute des plus terrible..");
        if(isWin) etat.setForeground(Color.WHITE);
        etat.setFont(new Font("Verdana", Font.PLAIN, 18));
        Border border = etat.getBorder();
        Border margin = new EmptyBorder(50,20,50,20);
        etat.setBorder(new CompoundBorder(border, margin));
        etat.setHorizontalAlignment(JLabel.CENTER);

        Color bg = isWin ? Color.green : Color.red;


        fin.setLayout(new BorderLayout());
        fin.getContentPane().add(etat, BorderLayout.CENTER);
        fin.setResizable(false);
        fin.getContentPane().setBackground(bg);
        fin.pack();
        fin.setVisible(true);

    }
    // récupère la data des labyrinths dans le fichier JSON
    public void setUpLabyrinths(String mapAJouer) {
        JSONParser parser = new JSONParser();
        int posYTile, posXTile; //px

        HashMap<Position, Tile> currentLabyrinth = new HashMap<>();
        caisses = new ArrayList();

        try {
            Object obj = parser.parse(new FileReader("ressources/maps.json"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject map = (JSONObject) jsonObject.get(mapAJouer);

            JSONArray premierNiveau = (JSONArray) map.get("left"); // pas besoin de prévoir le programme pour gérer n labyrinthes
            JSONArray secondNiveau = (JSONArray) map.get("right"); // donc on récupère à la main le 1er ainsi que le second
            //System.out.println(map);

            Map<JSONArray, Position> maps = new HashMap<>();
            maps.put(premierNiveau, new Position(20, 20)); // Offset for left map
            maps.put(secondNiveau, new Position(600, 20)); // Offset for right map

            for (Map.Entry<JSONArray, Position> currentMap : maps.entrySet()) {

                JSONArray niveau = currentMap.getKey();
                Position pos = currentMap.getValue();
                posYTile = pos.posY;

                for (Object line : niveau) {
                    posYTile += tileSize;
                    posXTile = pos.posX - tileSize;

                    for (Object tile : (ArrayList) line) {
                        posXTile += tileSize;
                        Position position = new Position(posXTile, posYTile);
                        if (tile.toString().equals("BOX")){
                            caisses.add(position);
                            currentLabyrinth.put(position, new Tile(Tile.TypeCase.FLOOR, position));
                        }
                        else currentLabyrinth.put(position, new Tile(Tile.TypeCase.valueOf(tile.toString()), position));
                    }
                }
                if (niveau == premierNiveau) left = (HashMap<Position, Tile>) currentLabyrinth.clone();
                else right = (HashMap<Position, Tile>) currentLabyrinth.clone();
                currentLabyrinth = new HashMap<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // affiche les labyrinthes
    public void drawMaps() {
        for (HashMap<Position, Tile> map : List.of(left, right)) {
            for (var entry : map.entrySet()) {
                entry.getValue().afficher(this);
            }
        }
    }
}
